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

package eu.eexcess.domaindetection.wikipedia.schools;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resource class that allows quick opening new index for creation and closing.
 * 
 * @author Raoul Rubien
 */
public class IndexWriterRessource implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(IndexWriterRessource.class.getName());
    protected IndexWriter outIndexWriter;
    private Version luceneVersion = Version.LATEST;
    private Double ramBufferSizeMB = 512.0;
    private File outIndexPath;

    public IndexWriterRessource(File outIndexPath) {
        this.outIndexPath = outIndexPath;
    }

    public Version getLuceneVersion() {
        return luceneVersion;
    }

    public void setLuceneVersion(Version luceneVersion) {
        this.luceneVersion = luceneVersion;
    }

    public Double getRamBufferSizeMB() {
        return ramBufferSizeMB;
    }

    public void setRamBufferSizeMB(Double ramBufferSizeMB) {
        this.ramBufferSizeMB = ramBufferSizeMB;
    }

    public File getOutIndexPath() {
        return outIndexPath;
    }

    /**
     * Creates/overwrites existing one. Remember to call
     * {@link #setOutIndexPath(String)} appropriately.
     * 
     * @param ramBufferSizeMB
     *            determines the amount of RAM that may be used for buffering
     * @throws IOException
     *             if unable to open/create index
     */
    public void open() throws IOException {

        try {
            Directory indexDirectory = FSDirectory.open(outIndexPath);
            Analyzer analyzer = new EnglishAnalyzer();
            IndexWriterConfig writerConfig = new IndexWriterConfig(luceneVersion, analyzer);
            writerConfig.setOpenMode(OpenMode.CREATE);
            writerConfig.setRAMBufferSizeMB(ramBufferSizeMB);
            outIndexWriter = new IndexWriter(indexDirectory, writerConfig);
            LOGGER.info("created new index at [" + outIndexPath + "]");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "unable to open/create index at [" + outIndexPath + "]", e);
            throw e;
        }
    }

    @Override
    public void close() {
        if (outIndexWriter != null) {
            try {
                outIndexWriter.commit();
                outIndexWriter.close();
                LOGGER.info("index closed [" + outIndexPath + "]");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "index closed erroneous", e);
            }
        } else {
            LOGGER.log(Level.SEVERE, "no resource to close found");
        }
        outIndexWriter = null;
    }
}
