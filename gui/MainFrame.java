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

public class MainFrame extends JFrame implements ActionListener {
    public static void main(String[] args) {
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

    public ArrayList<Result> search(String text) throws Exception{
        // TODO: connect to elasticsearch api

        String body = "{\n" +
                "  \"query\" : {\n" +
                "    \"match\" : {\n" +
                "      \"results.alternatives.transcript\": \"" + text + "\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        URL url = new URL("http://localhost:9200/podcasts/_search");
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

        ArrayList<Result> results = new ArrayList<>();

        if(httpConn.getResponseCode() == 200) {
            System.out.println("Success");
            BufferedReader br = new BufferedReader(new InputStreamReader((httpConn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output.substring(0, 500));
                results.add(new Result(output));
            }
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
            ArrayList<Result> results = null;
            try {
                results = search(text);
            } catch (Exception e) {
                e.printStackTrace();
                results = new ArrayList<>();
            }

            // Updates the GUI with the results
            resultPanel.updateResults(results);
        }
    }
}
