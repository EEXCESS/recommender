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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class IndexReaderRessource implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(IndexReaderRessource.class.getName());
    protected IndexReader indexReader;
    private File indexDirectory;

    public IndexReaderRessource(File indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    public void open() {

        try {
            if (indexReader != null) {
                LOGGER.info("warning: re-opening index without previous close");
                close();
            }
            indexReader = DirectoryReader.open(FSDirectory.open(indexDirectory));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "unable to open index reader at [" + indexDirectory.getAbsolutePath() + "]", e);
        }
    }

    @Override
    public void close() {

        try {
            if (indexReader != null) {
                indexReader.close();
                indexReader = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to close domain tree index", e);
        }
    }

    public IndexReader getIndexReader() {
        return indexReader;
    }

}
