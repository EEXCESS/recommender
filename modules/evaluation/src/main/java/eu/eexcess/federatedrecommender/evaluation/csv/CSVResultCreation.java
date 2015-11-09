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
 */
package eu.eexcess.federatedrecommender.evaluation.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;

public class CSVResultCreation {
    public static final String DIRECTORYPATH = "/home/hziak/Datasets/EEXCESS/evaluationBlockRanking/";
    private static final Logger LOGGER = Logger.getLogger(CSVResultCreation.class.getName());
    private static final String HEADER = "QUERY,DESCRIPTION,INT1,INT2,INT3,INT4,INT5,INT6,INT7,INT8,INT9,INT10,INT11,INT12,INT13,INT14,INT15,INT16,INT17,INT18,INT19,INT20,INT21,INT22,INT23,INT24,INT25,INT26,INT27,INT28,INT29,INT30"
            + ",TYPEFIRSTLIST"
            + ",FIRSTTITLE1,FIRSTDESCRIPTION1,FIRSTTITLE2,FIRSTDESCRIPTION2,FIRSTTITLE3,FIRSTDESCRIPTION3,FIRSTTITLE4,FIRSTDESCRIPTION4,FIRSTTITLE5,FIRSTDESCRIPTION5,FIRSTTITLE6,FIRSTDESCRIPTION6,FIRSTTITLE7,FIRSTDESCRIPTION7,FIRSTTITLE8,FIRSTDESCRIPTION8,FIRSTTITLE9,FIRSTDESCRIPTION9,FIRSTTITLE10,FIRSTDESCRIPTION10,"
            + ",SECONDTITLE1,SECONDDESCRIPTION1,SECONDTITLE2,SECONDDESCRIPTION2,SECONDTITLE3,SECONDDESCRIPTION3,SECONDTITLE4,SECONDDESCRIPTION4,SECONDTITLE5,SECONDDESCRIPTION5,SECONDTITLE6,SECONDDESCRIPTION6,SECONDTITLE7,SECONDDESCRIPTION7,SECONDTITLE8,SECONDDESCRIPTION8,SECONDTITLE9,SECONDDESCRIPTION9,SECONDTITLE10,SECONDDESCRIPTION10";
    private static final String REGEXPREPLACE = ",|\"|\n|\r";
    private final WebResource wRBlock;

    public CSVResultCreation() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(clientConfig);
        wRBlock = client.resource("http://eexcess-demo.know-center.tugraz.at/eexcess-federated-recommender-web-service-evaluation-1.0-SNAPSHOT/evaluation/blockEvaluation");

    }

    public static void main(String[] args) {
        CSVResultCreation creation = new CSVResultCreation();
        EvaluationQueryList queries = creation.getEvaluationQueriesFromJson("finalSelectedQueries.json");
        FileWriter ofqw = creation.openCSV();
        // creation.writeQueriesToQueryCSVFile(queries);
        try {
            ofqw.write(HEADER + "\n");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not write file", e);
        }
        creation.createCSVResultsFile(queries, ofqw);

        creation.closeCSV(ofqw);

    }

    private void createCSVResultsFile(EvaluationQueryList queries, FileWriter ofqw) {

        for (EvaluationQuery query : queries.getQueries()) {
            SecureUserProfileEvaluation secureUserProfileEvaluation = convertEvalQueryToSecUserProfile(query);
            String blockResultString = getQueryResultCSV(getwRBlock(), secureUserProfileEvaluation, false);
            String blockResultStringInterchanged = getQueryResultCSV(getwRBlock(), secureUserProfileEvaluation, true);
            // String defaultResultString = ""; // Todo: Get default string from
            // // get queyr resultCSV
            // defaultResultString = getQueryResultCSV(getwRDefault(),
            // secureUserProfileEvaluation);
            String queryCSV = getQueryCSV(query);
            writeCSVLine(ofqw, blockResultString, queryCSV);
            writeCSVLine(ofqw, blockResultStringInterchanged, queryCSV);
        }

    }

    private void writeCSVLine(FileWriter ofqw, String blockResultString, String queryCSV) {
        String finalCSVString = null;
        if (queryCSV != null && blockResultString != null)
            finalCSVString = queryCSV + blockResultString;
        if (finalCSVString != null)
            try {
                ofqw.write(finalCSVString + "\n");
                LOGGER.log(Level.INFO, finalCSVString + "\n");
                // System.out.println(finalCSVString);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "", e);
            }
        // if (finalCSVString != null)
        // try {
        // ofqw.write(finalCSVString + "\n");
        // LOGGER.log(Level.INFO, finalCSVString + "\n");
        // // System.out.println(finalCSVString);
        // } catch (IOException e) {
        // LOGGER.log(Level.WARNING, "", e);
        // }
    }

    public EvaluationQueryList getEvaluationQueriesFromJson(String fileName) {
        JsonReader reader = null;
        try {

            reader = new JsonReader(new FileReader(DIRECTORYPATH + fileName));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "", e);
        }

        EvaluationQueryList queries = null;
        Gson gson = new GsonBuilder().create();

        queries = gson.fromJson(reader, EvaluationQueryList.class);
        return queries;
    }

    // @SuppressWarnings("unused")
    // private void writeQueriesToQueryCSVFile(EvaluationQueryList queries) {
    // FileWriter ofqw = openCSV();
    // writeCSVLine(queries, ofqw);
    // closeCSV(ofqw);
    // }

    /**
     * Closed the csv file
     * 
     * @param ofqw
     */
    private void closeCSV(FileWriter ofqw) {
        try {
            ofqw.flush();
            ofqw.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "", e);
        }
    }

    // /**
    // * writes a new line to the csv file
    // *
    // * @param queries
    // * @param ofqw
    // */
    // private void writeCSVLine(EvaluationQueryList queries, FileWriter ofqw) {
    // for (EvaluationQuery evalQueries : queries.getQueries()) {
    //
    // String result = getQueryCSV(evalQueries);
    // try {
    //
    // ofqw.write(result);
    // } catch (IOException e) {
    // LOGGER.log(Level.WARNING, "", e);
    // }
    //
    // }
    // }

    /**
     * opens the CSV file
     * 
     * @return
     */
    private FileWriter openCSV() {
        FileWriter ofqw = null;
        try {
            ofqw = new FileWriter(new File(DIRECTORYPATH + "query2.csv"));
        } catch (IOException e1) {
            LOGGER.log(Level.WARNING, "", e1);
        }
        return ofqw;
    }

    private String getQueryCSV(EvaluationQuery evalQueries) {
        StringBuilder builder = new StringBuilder();

        builder.append("\"");
        builder.append(evalQueries.query.replaceAll(",|\"|\n|\r", ""));
        builder.append("\"");
        builder.append(",");
        builder.append("\"");
        builder.append(evalQueries.description.replaceAll(",|\"|\n|\r", ""));
        builder.append("\"");
        builder.append(",");
        for (Interest interest : evalQueries.interests) {
            builder.append("\"");
            builder.append(interest.getText());
            builder.append("\"");
            builder.append(",");
        }
        Integer maxInterests = 30;
        maxInterests -= evalQueries.interests.size();
        while (maxInterests > 0) {
            maxInterests--;
            builder.append(",");
        }

        // builder.append(System.lineSeparator());
        return builder.toString();
    }

    private SecureUserProfileEvaluation convertEvalQueryToSecUserProfile(EvaluationQuery query) {
        SecureUserProfileEvaluation profile = new SecureUserProfileEvaluation();
        profile.getContextKeywords().add(new ContextKeyword(query.query, 0.5));
        // for (String queryPart : query.query.split(" ")) {
        // if (!queryPart.trim().isEmpty())
        // profile.contextKeywords.add(new ContextKeyword(queryPart, 0.5));
        // }
        profile.setDecomposer("eu.eexcess.partnerrecommender.reference.OrQueryGeneratorFieldTermConjunction");
        profile.setQueryID("query" + profile.hashCode());
        profile.getInterestList().addAll(query.interests);
        PartnerBadge mendeley = new PartnerBadge();
        mendeley.setSystemId("Mendeley");
        PartnerBadge europeana = new PartnerBadge();
        europeana.setSystemId("Europeana");
        PartnerBadge wikipedia = new PartnerBadge();
        wikipedia.setSystemId("Wikipedia-Local");
        profile.getPartnerList().add(wikipedia);
        // profile.partnerList.add(mendeley);
        // profile.partnerList.add(europeana);
        profile.setPicker("FiFoPicker");
        profile.setNumResults(10);
        return profile;
    }

    @SuppressWarnings("deprecation")
    private static String getQueryResultCSV(WebResource wresource, SecureUserProfileEvaluation secureUserProfileEvaluation, boolean interchanged) {
        StringBuilder builder = new StringBuilder();
        EvaluationResultLists resp = null;
        secureUserProfileEvaluation.setNumResults(10);
        resp = wresource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(EvaluationResultLists.class, secureUserProfileEvaluation);

        // if (resp.results.get(0).results.size() < 10) {
        // System.out.println("query did not return enough results! 10!="
        // + resp.results.get(0).results.size() + " "
        // + secureUserProfileEvaluation.contextKeywords.toString());
        // return null;
        // }

        ObjectMapper mapper = new ObjectMapper();
        boolean isValidBlock = false;
        boolean isValidBasic = false;
        for (EvaluationResultList evalResultList : resp.results) {

            if ("Basic".equals(evalResultList.provider))
                if (evalResultList.results.size() == 10)
                    isValidBasic = true;
                else
                    LOGGER.log(Level.INFO, "Not used: " + evalResultList.provider + " " + evalResultList.results.size());

            if ("BlockPicker (Basic(4),Diversity(3),Serendipity(3))".equals(evalResultList.provider))
                if (evalResultList.results.size() == 10)
                    isValidBlock = true;
                else
                    LOGGER.log(Level.INFO, "Not used: " + evalResultList.provider + " " + evalResultList.results.size());
        }
        if (isValidBlock && isValidBasic) {
            try {
                File file = new File(DIRECTORYPATH + "results/" + secureUserProfileEvaluation.getQueryID() + "#" + Long.toBinaryString(Math.round(Math.random())) + ".json");
                mapper.defaultPrettyPrintingWriter().writeValue(file, resp);
                // System.out.println("Writing to file:" +
                // file.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "", e);
            }
        }
        if (interchanged) {
            EvaluationResultList first = resp.results.removeFirst();
            resp.results.addLast(first);
            builder.append("\"block\",");
        } else {
            builder.append("\"basic\",");
        }

        for (EvaluationResultList resultList : resp.results) {
            int counter = 0;
            for (Result result : resultList.results) {

                if (result.title != null) {
                    builder.append("\"");
                    builder.append(result.title.replaceAll(REGEXPREPLACE, ""));
                    builder.append("\",");
                    String description = result.description.replaceAll(REGEXPREPLACE, "");
                    if (description.length() > 500)
                        description = description.substring(0, 500) + "...";
                    builder.append(description);
                    builder.append("\",");

                } else if (resp.results.get(0).results.size() - (++counter) < 10)
                    return null;
            }
            builder.append(",");
        }
        return builder.toString();
    }

    public WebResource getwRBlock() {
        return wRBlock;
    }

}
