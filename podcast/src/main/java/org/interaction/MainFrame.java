package org.interaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.engine.ESresponseProcessor;
import org.engine.Engine;
import org.engine.OneResult;
import org.interaction.LocalQuery;
import org.elasticsearch.action.search.SearchResponse;


public class MainFrame extends JFrame implements ActionListener {
    public static void main(String[] args) {
        Engine.initSearcher();
        MainFrame frame = new MainFrame();
        frame.setSize(new Dimension(1000,1000));
        frame.setVisible(true);
    }

    /*
    * Main GUI components
    * */
    private SearchPanel searchPanel;
    private ResultPanel resultPanel;

    // Not currently used, can be used for search options
    private JMenuBar menuBar;

    // Initialize the window
    public MainFrame() {
        this.setLayout(new BorderLayout());
        this.searchPanel = new SearchPanel(this);
        this.resultPanel = new ResultPanel();
        this.menuBar = new JMenuBar();
        this.setJMenuBar(this.menuBar);
        this.add(this.searchPanel, BorderLayout.PAGE_START);
        this.add(this.resultPanel, BorderLayout.CENTER);

        // Stops execution when the window is closed
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private static final int TYPE = 0;

    public ArrayList<OneResult> searchMerged(String text) throws Exception{
        // From podcast-code by Shuang
        int n = 2;
        LocalQuery query = new LocalQuery(text, n);
        SearchResponse res = Engine.client.search(query);
        ESresponseProcessor resProcessor = new ESresponseProcessor(Engine.client,TYPE);
        ArrayList<OneResult> results = resProcessor.group(res, query);
        Collections.sort(results,Collections.reverseOrder());

        return results;
    }

    public ArrayList<Result> search(String text) throws Exception{
        String body = "{\n" +
                "  \"query\" : {\n" +
                "    \"match\" : {\n" +
                "      \"results.alternatives.transcript\": \"" + text + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        URL url = new URL("http://localhost:9200/podcasts/_search"); // Change this to the Azure, depending on GUI option
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setRequestProperty("Content-Length", Integer.toString(body.getBytes(StandardCharsets.UTF_8).length));
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());

        out.write(body.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
        httpConn.connect();
        // Sends the request to the server

        ArrayList<Result> results = new ArrayList<>();

        if(httpConn.getResponseCode() == 200) {
            // Success, attempt to deserialize
            System.out.println("Success");
            BufferedReader br = new BufferedReader(new InputStreamReader((httpConn.getInputStream())));
            Gson gson = new GsonBuilder().create();
            String json = br.readLine();
            System.out.println(json);
            Response res = gson.fromJson(json, Response.class);
            Result[] hits = res.getHits();
            for(Result rs : hits) {
                results.add(rs);
            }

            System.out.println(res.toString());
        } else {
            System.out.println("Unsuccess");
        }
        System.out.println(httpConn.getResponseMessage());
        return results;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Called when a button press is registered
        if(ae.getSource().equals(this.searchPanel.getSearchButton())) {
            // Retrieve the search text
            String text = this.searchPanel.getSearchText();
            System.out.println("Searching for: " + text);
            ArrayList<OneResult> results = null;
            try {
                results = searchMerged(text);
            } catch (Exception e) {
                e.printStackTrace();
                results = new ArrayList<>();
            }

            // Updates the GUI with the results
            resultPanel.updateResults(results);
        }
    }
}
