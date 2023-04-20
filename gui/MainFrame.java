import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public ArrayList<Result> search(String text) {
        // TODO: connect to elasticsearch api
        ArrayList<Result> results = new ArrayList<>();
        results.add(new Result());
        return results;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Called when a button press is registered
        if(ae.getSource().equals(this.searchPanel.getSearchButton())) {
            // Retrieve the search text
            String text = this.searchPanel.getSearchText();
            System.out.println("Searching for: " + text);
            ArrayList<Result> results = search(text);

            // Updates the GUI with the results
            resultPanel.updateResults(results);
        }
    }
}
