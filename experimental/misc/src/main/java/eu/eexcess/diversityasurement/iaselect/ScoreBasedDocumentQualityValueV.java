/* Copyright (C) 2014 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * @author Raoul Rubien
 */

package eu.eexcess.diversityasurement.iaselect;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import book.InvalidStateException;

/**
 * V(d|q,c) - relevance of a document or the quality of a document d for query q
 * when the intended category is c
 * <p>
 * See also [Agrawal, R., Gollapudi, S., Halverson, A., & Ieong, S. (2009).
 * Diversifying search results. In Proceedings of the Second ACM International
 * Conference on Web Search and Data Mining - WSDM ’09 (p. 5). New York, New
 * York, USA: ACM Press. http://doi.org/10.1145/1498759.1498766].
 * 
 * @author Raoul Rubien
 *
 */
public class ScoreBasedDocumentQualityValueV implements DocumentQualityValueV {

    // private Logger logger =
    // PianoLogger.getLogger(ScoreBasedDocumentQualityValueV.class);
    // Map<Document, HashSet<Category>> documentQualities = new
    // HashMap<Document, HashSet<Category>>();

    // @Deprecated
    // private IndexReader indexReader;
    // @Deprecated
    // private IndexSearcher indexSearcher;
    // @Deprecated
    // private QueryParser queryParser;

    public ScoreBasedDocumentQualityValueV() throws IOException {
        // openInIndex();
    }

    /**
     * V(d|q,c) - relevance of a document or the quality of a document d for
     * query q when the intended category is c
     * 
     * @param d
     *            one document out of R(q)
     * @param q
     *            query used for R(q)
     * @param c
     *            category the document belongs to
     * @return document relevance
     * @throws IllegalArgumentException
     *             if document or category is not found
     * @throws ParseException
     * @throws IOException
     * @throws InvalidStateException
     */
    @Override
    public double v(Document d, Query q, Category c) throws IllegalArgumentException {
        for (Category documentCategory : d.categories()) {
            if (documentCategory.equals(c)) {
                // return documentScore(d, q) * documentCategory.probability;
                return d.documentScore * documentCategory.probability;
            }
        }
        throw new IllegalArgumentException("failed fetching document quality value: category[" + c.name + "] for document[" + d.name + "] not found");
    }

    // @Deprecated
    // private void openInIndex() throws IOException {
    // Directory directory =
    // FSDirectory.open(Settings.RelevanceEvaluation.IOFIles.inLuceneIndexDirectory);
    // indexReader = DirectoryReader.open(directory);
    //
    // indexSearcher = new IndexSearcher(indexReader);
    // queryParser = new
    // QueryParser(Settings.RelevanceEvaluation.Lucene.SEARCH_FIELD_SECTIONTEXT,
    // new EnglishAnalyzer());
    // }

    // @Deprecated
    // @Override
    // public void close() throws IOException {
    // try {
    // indexReader.close();
    // } catch (IOException e) {
    // logger.log(Level.SEVERE, "index reader closed erroneous", e);
    // } catch (NullPointerException npe) {
    // logger.log(Level.SEVERE, "index reader already closed");
    // }
    // indexReader = null;
    // }

    // @Deprecated
    // /**
    // * Calculate document d relevance regarding query q.
    // * @param d
    // * @param q
    // * @return scoreDoc.score / topDocs.getMaxScore()
    // * @throws InvalidStateException if document with d.documentId not found
    // * @throws IOException
    // * @throws ParseException
    // */
    // private double documentScore(Document d, Query q) throws
    // InvalidStateException, IOException, ParseException {
    // org.apache.lucene.search.Query query = queryParser.parse(q.query);
    // TopDocs topDocs = indexSearcher.search(query,
    // Settings.RelevanceEvaluation.EstimationArguments.numTopDocumentsToConsider);
    //
    // for (ScoreDoc sDoc : topDocs.scoreDocs) {
    // if (d.documentId == sDoc.doc) {
    // return 1.0 - (sDoc.score / topDocs.getMaxScore());
    // }
    // }
    // throw new InvalidStateException("failed to find document id [" +
    // d.documentId + "] within ["
    // +
    // Settings.RelevanceEvaluation.EstimationArguments.numTopDocumentsToConsider
    // + "] top docs for query [" + q.query + "]");
    // }
}
