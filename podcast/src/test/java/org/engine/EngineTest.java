package org.engine;

import org.ES.ElasticSearchClient;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.core.Tuple;
import org.interaction.LocalQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Unit test for simple App.
 */
public class EngineTest{
    public static HashMap<String, HashMap<String,Integer>> readListFile(String filename){
        HashMap<String, HashMap<String,Integer>> result = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line != null) {
//                System.out.println(line);
                String[] words = line.split("(\\s+|\t)");
                if(!result.containsKey(words[0])){
                    HashMap<String,Integer> oneResult = new HashMap<>();
                    try{
                        oneResult.put(words[2].split(":")[2],Integer.parseInt(words[3]));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    result.put(words[0],oneResult);
                }
                else{
                    result.get(words[0]).put(words[2].split(":")[2],Integer.parseInt(words[3]));
                }
                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static ArrayList<TestCase> readXmlFile(String filename){
        ArrayList<TestCase> testCases = new ArrayList<>();
        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("topic");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
//                    System.out.println("Current Element: " + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    TestCase testCase = new TestCase(element.getElementsByTagName("num").item(0).getTextContent(),
                            element.getElementsByTagName("query").item(0).getTextContent(),
                            element.getElementsByTagName("description").item(0).getTextContent());
                    testCases.add(testCase);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        return testCases;
    }

    public static void main(String[] args ) throws IOException {
        String queryFileName1 = "./evaluation/podcasts_2020_topics_train.xml";
        String queryFileName2 = "./evaluation/podcasts_2020_topics_test.xml";
        String labelFileName1 = "./evaluation/2020_train_qrels.list";
        String labelFileName2 = "./evaluation/2020_test_qrels.list";

        ArrayList<TestCase> testCases = readXmlFile(queryFileName1);
        ArrayList<TestCase> testCases2 = readXmlFile(queryFileName2);
        testCases.addAll(testCases2);
        HashMap<String, HashMap<String,Integer>> labels = readListFile(labelFileName1);
        HashMap<String, HashMap<String,Integer>> labels2 = readListFile(labelFileName2);
        labels.putAll(labels2);
        String host = "20.223.162.103";
        int port  = 9200;
        int groupType = 0;
        ElasticSearchClient elasticSearchClient = new ElasticSearchClient(host,port);
        String fileName = "ndcg2.txt";
        try {
            FileWriter writer = new FileWriter(fileName);
            for(TestCase testCase:testCases){
                LocalQuery query = new LocalQuery(testCase.query,2, false);
                SearchResponse searchResponse = elasticSearchClient.search("episodes_2min",query);
                ESresponseProcessor eSresponseProcessor = new ESresponseProcessor(elasticSearchClient,groupType);
                ArrayList<OneTranscriptSegment> results = eSresponseProcessor.groupFix(searchResponse,query);
                Collections.sort(results,Collections.reverseOrder());
                ArrayList<String> rankedResults = new ArrayList<>();
                for(OneTranscriptSegment oneTranscriptSegment:results){
                    String segName = String.valueOf(Integer.parseInt(oneTranscriptSegment.segId)*60.0);
                    String newName = oneTranscriptSegment.docId+"_"+segName;
                    rankedResults.add(newName);
                    writer.write(testCase.num+" "+newName+"\n");
                }
                HashMap<String,Integer> correctSegments = labels.get(testCase.num);
                if(correctSegments==null){
                    continue;
                }
                double ndcg = NDCG.compute(rankedResults,correctSegments);
                System.out.println(testCase.num);
                System.out.println(ndcg);
                writer.write(testCase.num+" "+ndcg+"\n");
            }
            writer.close(); // closing the file writer
            System.out.println("Successfully wrote to " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        elasticSearchClient.close();

    }


}
