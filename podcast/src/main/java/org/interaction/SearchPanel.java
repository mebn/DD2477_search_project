package org.interaction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SearchPanel extends JPanel {
    private JTextField searchText;
    private JButton button;

    // Initialize the SearchPanel
    public SearchPanel(MainFrame mainFrame) {
        this.searchText = new JTextField();
        this.searchText.setPreferredSize(new Dimension(800, 32));
        this.searchText.addActionListener(mainFrame);
        this.add(searchText);

        this.button = new JButton("Search");
        this.button.addActionListener(mainFrame);
        this.add(button);
    }

    // Get the search text
    public String getSearchText() {
        return this.searchText.getText();
    }
    // Get the search button. Used for checking the origin of Action Events
    public JButton getSearchButton() {
        return this.button;
    }
    public JTextField getTextField() {
        return this.searchText;
    }
}
