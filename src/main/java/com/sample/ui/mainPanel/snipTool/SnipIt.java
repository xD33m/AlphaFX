package com.sample.ui.mainPanel.snipTool;

import javax.swing.*;
import java.awt.*;

public class SnipIt {

    public SnipIt() {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }

            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            // This works differently under Java 6
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new SnipItPane());
            frame.setBounds(new SelectionPane().getVirtualBounds());
            frame.setVisible(true);
        });

    }

}
