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

package eu.eexcess.logger;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A logger printing to System.out instead of System.err. to make errors more
 * noisy but normal logging quieter.
 * 
 * @author Raoul Rubien
 *
 */
public class PianoLogger {

	private static class PianoHandler extends Handler {
		@Override
		public void publish(LogRecord record) {
			if (getFormatter() == null) {
				setFormatter(new SimpleFormatter());
			}

			try {
				String message = getFormatter().format(record);
				if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
					System.err.write(message.getBytes());
				} else {
					System.out.write(message.getBytes());
				}
			} catch (Exception exception) {
				reportError(null, exception, ErrorManager.FORMAT_FAILURE);
				return;
			}

		}

		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}
	};

	public static <T> Logger getLogger(Class<T> c) {
		return getLogger(c.getCanonicalName());
	}

	public static Logger getLogger(String className) {
		Logger logger = Logger.getLogger(className);

		PianoHandler handler = new PianoHandler();
		handler.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		return logger;
	}
}
