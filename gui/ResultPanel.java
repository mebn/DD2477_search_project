import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.ArrayList;

import org.engine.OneResult;

public class ResultPanel extends JPanel {
    private ArrayList<OneResult> results; // Might not be useful, maybe just update the contents of the innerpanel
    private JScrollPane resultPane;
    private JPanel innerPanel;
    private MouseAdapter mouseAdapter = new MouseAdapter();
    private JLabel timeLabel;

    // Initialize the ResultPanel
    public ResultPanel() {
        this.results = new ArrayList<>();
        this.innerPanel = new JPanel();
        this.innerPanel.setLayout(new BoxLayout(this.innerPanel, BoxLayout.Y_AXIS));

        this.resultPane = new JScrollPane( this.innerPanel );
        this.resultPane.setLayout(new ScrollPaneLayout());
        this.setLayout(new BorderLayout());
        this.add(resultPane, BorderLayout.CENTER);
        this.timeLabel = new JLabel("Search metrics will go here");
        this.setBorder( new EtchedBorder() );
    }

    public void updateResults( ArrayList<OneResult> results, long timeMillis) {
        // Update the results list after a search has been completed
        this.results = results;
        this.innerPanel.removeAll();
        this.remove(this.timeLabel);
        this.timeLabel = new JLabel("Searching took " + timeMillis + "ms and found " + (results.isEmpty() ? "no" : results.size()) + " results.");
        this.add(timeLabel, BorderLayout.PAGE_START);
        for( OneResult r : this.results ) {
            ResultLabel label = new ResultLabel(r);
            label.addMouseListener(mouseAdapter);
            this.innerPanel.add(label);
        }

        // Swing needs these calls
        revalidate();
        repaint();
    }
}
