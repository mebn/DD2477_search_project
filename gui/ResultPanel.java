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

    // Initialize the ResultPanel
    public ResultPanel() {
        this.results = new ArrayList<>();
        this.innerPanel = new JPanel();
        this.innerPanel.setLayout(new BoxLayout(this.innerPanel, BoxLayout.Y_AXIS));

        this.resultPane = new JScrollPane( this.innerPanel );
        this.resultPane.setLayout(new ScrollPaneLayout());
        this.add(resultPane);
        this.setBorder( new EtchedBorder() );
    }

    public void updateResults( ArrayList<OneResult> results ) {
        // Update the results list after a search has been completed
        this.results = results;
        this.innerPanel.removeAll();
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
