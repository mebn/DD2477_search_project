package org.interaction;

import javax.swing.*;
import org.engine.OneResult;

import java.awt.*;

public class DetailedResultsFrame extends JFrame {
    public DetailedResultsFrame(OneResult res) {
        this.setLayout(new BorderLayout());

        JLabel transcript = new JLabel("<html><p style=\"width:400px; margin: auto;\">"+res.getTranscript()+"</p></html>");
        this.add(transcript, BorderLayout.CENTER);

        JLabel title = new JLabel(res.toString());
        this.add(title, BorderLayout.PAGE_START);

        this.setTitle("Detailed Results");

        this.setVisible(true);
        this.setSize(new Dimension(600, 500));
    }
}
