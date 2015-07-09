/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.federatedrecommender.domaindetection;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import no.uib.cipr.matrix.GivensRotation;

import com.sun.jersey.api.client.Client;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetectorException;
import eu.eexcess.federatedrecommender.domaindetection.probing.PartnerDomainsProbe;
import eu.eexcess.federatedrecommender.domaindetection.probing.PartnerDomainsProbe.CancelProbeCondition;

/**
 * This class realizes asynchronous probing of a {@link PartnerBadge} and
 * returns the results via callback. To prevent run away of the task it is
 * aborted if it did not finish within the {@link GivensRotation}
 * {@link #timeout}.
 * 
 * @author Raoul Rubien
 *
 */
public class AsyncPartnerDomainsProbe {

    public static interface ProbeDoneCallback {
        public void onProbeDoneCallback(String partnerId, Set<PartnerDomain> probeResult);

        public void onProbeFailedCallback(String partnerId);
    }

    private static class CancelCondition implements CancelProbeCondition {

        public boolean toBeCanceled = false;

        @Override
        synchronized public boolean isProbeToBeCancelled() {
            return toBeCanceled;
        }

    }

    /**
     * Aborts the {@link ProbeTask} if not finished within the given timeout.
     * 
     * @author Raoul Rubien
     *
     */
    private static class TaskController extends Thread {

        private Thread task;
        private long timeout;
        private Logger logger;
        private CancelCondition condition;

        public TaskController(Thread task, long timeoutMs, CancelCondition condition, Logger logger) {
            this.task = task;
            this.timeout = timeoutMs;
            this.logger = logger;
            this.condition = condition;
        }

        /**
         * starts a task with timeout and kill
         */
        @Override
        public void run() {
            task.start();
            try {
                logger.info("scheduling termination of [" + task.getName() + "] in [" + timeout + "ms]...");
                task.join(timeout);
            } catch (InterruptedException e) {
                logger.severe("task controller thread was interrupted, trying to abort task");
                condition.toBeCanceled = true;
            }
            if (task.isAlive()) {
                logger.info("found task running longer than [" + timeout + "ms] to be interrupted");
                condition.toBeCanceled = true;
            }
        }

    }

    /**
     * Thread that performs the asynchronous probing.
     * 
     * @author Raoul Rubien
     *
     */
    private static class ProbeTask extends Thread {

        private PartnerBadge partnerConfig;
        private Client partnerClient;
        private Set<ProbeDoneCallback> callbacks;
        private PartnerDomainsProbe probe;
        private CancelCondition condition;

        public ProbeTask(PartnerBadge partnerConfig, Client partnerClient, PartnerDomainsProbe probe, Set<ProbeDoneCallback> callbacks,
                CancelCondition condition, Logger logger) {
            this.partnerConfig = partnerConfig;
            this.partnerClient = partnerClient;
            this.callbacks = callbacks;
            this.probe = probe;
            this.condition = condition;
        }

        @Override
        public void run() {
            try {
                Set<PartnerDomain> domains = probe.probePartner(partnerClient, partnerConfig);
                // if probing was interrupted the result is not complete,
                // for that reason do not notify listener for this result
                if (condition.isProbeToBeCancelled()) {
                    synchronized (callbacks) {
                        for (ProbeDoneCallback callback : callbacks) {
                            callback.onProbeFailedCallback(partnerConfig.getSystemId());
                        }
                    }
                    return;
                }
                synchronized (callbacks) {
                    for (ProbeDoneCallback callback : callbacks) {
                        callback.onProbeDoneCallback(partnerConfig.getSystemId(), domains);
                    }
                }
            } catch (DomainDetectorException e) {
                e.printStackTrace();
                for (ProbeDoneCallback callback : callbacks) {
                    callback.onProbeFailedCallback(partnerConfig.getSystemId());
                }
            }
        }
    }

    private Logger logger = Logger.getLogger(AsyncPartnerDomainsProbe.class.getName());
    private CancelCondition cancelCondition;
    private TaskController probeController;
    private ProbeTask probeTask;
    private long timeout;
    private Set<ProbeDoneCallback> callbacks = new HashSet<ProbeDoneCallback>();

    /**
     * Constructs an asynchronous domain probe that allows asynchronous probing
     * with timeout. If probing is not done within the specified time, it is
     * aborted.
     * 
     * @param partnerConfig
     *            partner details
     * @param partnerClient
     *            connector for probing the partner
     * @param domainProbe
     *            a cloneable instance
     * @param asyncProbeTimeout
     *            the timeout in [ms] when the probe will be terminated it not
     *            finished until then
     * @throws CloneNotSupportedException
     *             if {@link PartnerDomainsProbe} instance can not be cloned
     *             using its {@link Cloneable} interface
     */
    public AsyncPartnerDomainsProbe(PartnerBadge partnerConfig, Client partnerClient, PartnerDomainsProbe domainProbe, long asyncProbeTimeout)
            throws CloneNotSupportedException {
        domainProbe = (PartnerDomainsProbe) domainProbe.clone();
        this.timeout = asyncProbeTimeout;
        cancelCondition = new CancelCondition();
        probeTask = new ProbeTask(partnerConfig, partnerClient, domainProbe, callbacks, cancelCondition, logger);
        probeTask.setName("probe-task-[" + partnerConfig.getSystemId() + "]");
        // this condition allows probing to terminate early and leave the result
        // uncomplete
        domainProbe.setCondition(cancelCondition);
        probeController = new TaskController(probeTask, timeout, cancelCondition, logger);
        probeController.setName("probe-task-controller-[" + partnerConfig.getSystemId() + "]");
    }

    /**
     * Performs an asynchronous domain probe if there is no other task running.
     * It terminates the task if the probe duration exceeds the {@link #timeout}
     * . If the task is ready within the timeout, the result is delivered via
     * {@link ProbeDoneCallback}.
     */
    public void probeAsyncronous() {
        if (isRunning()) {
            logger.info("failed to start new asynchronous probing while task is running");
        } else {
            cancelCondition.toBeCanceled = false;
            logger.info("starting probe [" + probeController.getName() + "] ...");
            probeController.start();
        }
    }

    /**
     * Adds callback(s) to be performed when a probe has finished.
     * 
     * @param callback
     */
    public void addCallback(ProbeDoneCallback callback) {
        synchronized (callbacks) {
            callbacks.add(callback);
        }
    }

    /**
     * Removes a desired callback.
     * 
     * @param callback
     *            the reference to be removed
     */
    public void removeCallback(ProbeDoneCallback callback) {
        synchronized (callbacks) {
            callbacks.remove(callback);
        }
    }

    /**
     * @return true if the task is still running
     */
    public boolean isRunning() {
        return (probeController.isAlive() || probeTask.isAlive());
    }

}
