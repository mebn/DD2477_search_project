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
        frame.setTitle("Podcast Search Engine");
        frame.setVisible(true);
    }

    /*
    * Main GUI components
    * */
    private SearchPanel searchPanel;
    private ResultPanel resultPanel;

    // Not currently used, can be used for search options
    private JMenuBar menuBar;
    private JMenuItem changeN;
    private JMenuItem toggleSynonym;

    public int N = 2;
    public boolean useSynonyms = false;

    // Initialize the window
    public MainFrame() {
        this.setLayout(new BorderLayout());
        this.searchPanel = new SearchPanel(this);
        this.resultPanel = new ResultPanel();
        this.menuBar = new JMenuBar();
        this.changeN = new JMenuItem("N = " + this.N + ", click to change");
        this.changeN.addActionListener(this);
        this.menuBar.add(changeN);
        this.toggleSynonym = new JMenuItem("Synonyms disabled, click to change");
        this.toggleSynonym.addActionListener(this);
        this.menuBar.add(toggleSynonym);
        this.setJMenuBar(this.menuBar);
        this.add(this.searchPanel, BorderLayout.PAGE_START);
        this.add(this.resultPanel, BorderLayout.CENTER);

        // Stops execution when the window is closed
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    private static final int GROUPTYPE = 0;

    public ArrayList<OneResult> search(String text) throws Exception{
        if(this.N == 1 && this.useSynonyms) {
            JOptionPane.showMessageDialog(null, "Searching while N = 1 not searchable with synonym search due to indexing");
            return new ArrayList<>();
        }
        // From podcast-code by Shuang
        LocalQuery query = new LocalQuery(text, this.N, this.useSynonyms);
        ESresponseProcessor eSresponseProcessor = new ESresponseProcessor(Engine.client,GROUPTYPE);
        ArrayList<OneResult> results = eSresponseProcessor.group(query);
        Collections.sort(results,Collections.reverseOrder());
        return results;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Called when a button press is registered
        if(ae.getSource().equals(this.searchPanel.getSearchButton()) || ae.getSource().equals(this.searchPanel.getTextField())) {
            // Retrieve the search text
            String text = this.searchPanel.getSearchText();
            System.out.println("Searching for: " + text);
            ArrayList<OneResult> results = null;
            long startTime = System.currentTimeMillis();
            try {
                results = search(text);
            } catch (Exception e) {
                e.printStackTrace();
                results = new ArrayList<>();
            }
            long endtime = System.currentTimeMillis();

            // Updates the GUI with the results
            resultPanel.updateResults(results, endtime - startTime);
        } else if (ae.getSource() == this.changeN){
            String userInput = JOptionPane.showInputDialog("Enter new N:");
            try {
                int newN = Integer.valueOf(userInput);
                if(newN <= 0) throw new IllegalArgumentException("N is too small");
                this.N = newN;
                this.changeN.setText("N = " + this.N + ", click to change");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "N needs to be a whole number, at least 1", "Wrong input", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (ae.getSource() == this.toggleSynonym){
            this.useSynonyms = !this.useSynonyms;
            this.toggleSynonym.setText("Synonyms " + (this.useSynonyms ? "enabled" : "disabled") + ", click to change");

        }
    }
}
