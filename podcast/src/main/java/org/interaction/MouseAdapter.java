package org.interaction;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseAdapter implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
        ResultLabel source = (ResultLabel) e.getSource();
        /*JOptionPane.showMessageDialog(null, source.result.getTranscript());*/
        DetailedResultsFrame frame = new DetailedResultsFrame(source.result);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
