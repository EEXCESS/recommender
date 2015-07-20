package eu.eexcess.evaluation.queryexpansion;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

public class QueryExpansionEvaluationResultsAnalysis {
    private static final Logger LOGGER = Logger.getLogger(QueryExpansionEvaluationResultsAnalysis.class.getName());
    private static final String DIRECTORYPATH = "/home/hziak/Datasets/EExcess/EvalUserBackup/";
    private static final String[] SINGLEPAGEQUERIESARRAY = { "michelle obama", "Autism", "glass ceiling", "february revolution", "war on terrorism" };

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String[] args) {
        List<String> singlePageQueriesList = Arrays.asList(SINGLEPAGEQUERIESARRAY);
        File dir = new File(DIRECTORYPATH);
        List<LinkedHashMap> singleQueryMap = new ArrayList<LinkedHashMap>();

        List<LinkedHashMap> vagueQueryMap = new ArrayList<LinkedHashMap>();
        for (File file : dir.listFiles()) {
            try {

                ObjectMapper mapper = new ObjectMapper();

                HashMap<String, LinkedHashMap> map = mapper.readValue(file, new HashMap<String, LinkedHashMap>().getClass());

                for (String key : map.keySet()) {
                    if (singlePageQueriesList.contains(key)) {
                        singleQueryMap.add(map.get(key));

                    } else
                        vagueQueryMap.add(map.get(key));
                }

            } catch (Exception e) {
                // System.out.println("Leaving out "+file.getAbsolutePath());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        // System.out.println("single:");
        sumQueryMap(singleQueryMap);
        // System.out.println("vague:");
        sumQueryMap(vagueQueryMap);

    }

    @SuppressWarnings("rawtypes")
    private static void sumQueryMap(List<LinkedHashMap> queryMap) {
        Integer sumWiki = 0;
        Integer sumSource = 0;
        Integer sumSourceP = 0;
        Integer sumNone = 0;
        for (LinkedHashMap value : queryMap) {
            for (Object key : value.keySet()) {
                // System.out.println(key.toString());
                // System.out.println(value.get(key));
                // List<Object> asList = Arrays.asList(value.get(key));
                try {
                    Object entry = value.get(key);
                    // System.out.println(entry.getClass());
                    if (entry instanceof java.util.ArrayList) {
                        @SuppressWarnings({ "unchecked" })
                        ArrayList<LinkedHashMap> objects = (ArrayList<LinkedHashMap>) entry;
                        for (LinkedHashMap obj : objects) {
                            // System.out.println(obj.get("provider").toString());
                            if (obj.get("provider").toString().startsWith("wikipedia")) {
                                String num = ((String) obj.get("numSelect")).trim();
                                sumWiki += Integer.parseInt(num);
                            } else if (obj.get("provider").toString().startsWith("source partners:FiFoPicker expansion source partners:[PartnerBadge")) {
                                String num = ((String) obj.get("numSelect")).trim();
                                sumSource += Integer.parseInt(num);
                            } else if (obj.get("provider").toString().startsWith("source")) {
                                String num = ((String) obj.get("numSelect")).trim();
                                sumSourceP += Integer.parseInt(num);
                            } else {
                                String num = ((String) obj.get("numSelect")).trim();
                                sumNone += Integer.parseInt(num);
                            }

                        }
                    }
                } catch (ClassCastException e) {
                    LOGGER.log(Level.INFO, "", e);
                }
            }
        }

        LOGGER.log(Level.INFO, "None: " + sumNone);
        LOGGER.log(Level.INFO, "Sour: " + sumSource);
        LOGGER.log(Level.INFO, "SouP: " + sumSourceP);
        LOGGER.log(Level.INFO, "Wiki: " + sumWiki);
    }

}
