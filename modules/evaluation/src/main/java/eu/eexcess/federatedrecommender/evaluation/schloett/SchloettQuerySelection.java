package eu.eexcess.federatedrecommender.evaluation.schloett;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.federatedrecommender.evaluation.csv.CSVResultCreation;
import eu.eexcess.federatedrecommender.evaluation.csv.EvaluationQueryList;
import eu.eexcess.federatedrecommender.evaluation.evaluation.EvaluationQuery;

public class SchloettQuerySelection {
    static private final Logger logger = Logger.getLogger(SchloettQuerySelection.class.getName());

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        SchloettQuerySelection sQS = new SchloettQuerySelection();
        CSVResultCreation csv = new CSVResultCreation();

        List<String> selectedQueries = sQS.getSelectedQueryList(CSVResultCreation.DIRECTORYPATH, "selectedQueriesBlockRanking.txt");
        EvaluationQueryList selectedEvalQueries = new EvaluationQueryList();
        EvaluationQueryList queries = sQS.getQueriesWithInterests(csv);
        for (String query : selectedQueries) {
            // Integer i = queries.getQueries().indexOf(query);
            EvaluationQuery tmpQuery = null;
            for (EvaluationQuery evalQuery : queries.getQueries()) {
                if (evalQuery.query.equals(query)) {
                    tmpQuery = evalQuery;
                    break;
                }
            }
            if (tmpQuery != null) {
                boolean written = false;
                while (!written) {

                    EvaluationQuery tmpQuery2 = new EvaluationQuery();
                    tmpQuery2.query = tmpQuery.query;
                    System.out.println(tmpQuery.query);
                    for (Interest interest : tmpQuery.interests) {

                        // System.out.println(interest.text +" ");
                        // BufferedReader br = new BufferedReader(new
                        // InputStreamReader(System.in));
                        // String s = null;
                        // try {
                        // s = br.readLine();
                        // } catch (IOException e) {
                        // // TODO Auto-generated catch block
                        // e.printStackTrace();
                        // }
                        //
                        // if(s!=null){
                        // if(s.equals("y")){
                        tmpQuery2.interests.add(interest);
                        // }
                        // }

                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Description:");
                    String s = null;
                    try {
                        s = br.readLine();
                    } catch (IOException e) {
                        logger.log(Level.INFO, "", e);
                    }
                    if (s != null) {
                        tmpQuery2.description = s;
                    }

                    System.out.print("Write? ");

                    try {
                        s = br.readLine();
                    } catch (IOException e) {

                        logger.log(Level.INFO, "", e);
                    }
                    if (s != null)
                        if (s.equals("y")) {
                            selectedEvalQueries.getQueries().add(tmpQuery2);
                            written = true;
                        }

                }
            } else
                System.out.println("couldn not find " + query);
            ObjectMapper mapper = new ObjectMapper();

            try {
                File file = new File(CSVResultCreation.DIRECTORYPATH + "finalSelectedQueries-new.json");
                mapper.defaultPrettyPrintingWriter().writeValue(file, selectedEvalQueries);
                System.out.println("Writing to file:" + file.getAbsolutePath());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println(selectedEvalQueries);

    }

    private List<String> getSelectedQueryList(String directorypath, String fileName) {
        List<String> returnList = new ArrayList<String>();
        BufferedReader ifBR = null;
        try {
            ifBR = new BufferedReader(new FileReader(directorypath + fileName));
        } catch (FileNotFoundException e1) {
            closeBufferedReader(ifBR);
            logger.log(Level.INFO, "", e1);
        }
        String line = null;
        try {
            line = ifBR.readLine();
        } catch (IOException e1) {
            closeBufferedReader(ifBR);
            logger.log(Level.INFO, "", e1);
        }
        while (line != null)
            try {
                returnList.add(line);
                line = ifBR.readLine();
            } catch (IOException e) {
                logger.log(Level.INFO, "", e);
                closeBufferedReader(ifBR);
            }
        closeBufferedReader(ifBR);
        return returnList;
    }

    private void closeBufferedReader(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
        }
    }

    private EvaluationQueryList getQueriesWithInterests(CSVResultCreation csv) {
        return csv.getEvaluationQueriesFromJson("queriesEnDeDup.json");
    }
}
