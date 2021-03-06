package com.sample.ui.mainPanel.snipTool;

import com.sample.ocr.TessOcr;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SelectionPane extends JPanel {

    private JButton button;
    private JLabel label;

    private static Rectangle rectangleBounds;

    public SelectionPane() {
        button = new JButton("Apply");
        setOpaque(false);

        label = new JLabel();
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(4, 4, 4, 4));
        label.setBackground(new Color(0, 0, 0, 0));
        label.setForeground(Color.WHITE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        gbc.gridy++;

        add(button, gbc);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                label.setText("x" + getX() + "x" + getY() + "x" + getWidth() + "x" + getHeight());
            }
        });

        button.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(SelectionPane.this).dispose();
            setRectangleBounds(new Rectangle(getX(), getY(), getWidth(), getHeight()));
            TessOcr.getInstance().setRectangle(getRectangleBounds());
        });


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        // I've chosen NOT to fill this selection rectangle, so that
        // it now appears as if you're "cutting" away the selection
        g2d.setColor(new Color(255, 255, 255, 128));
        // g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.BLACK);
        g2d.dispose();
    }

    public Rectangle getVirtualBounds() {

        Rectangle bounds = new Rectangle(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        System.out.println(Toolkit.getDefaultToolkit().get);
        // multi monitor compatibility
//        GraphicsDevice lstGDs[] = ge.getScreenDevices();
//        for (GraphicsDevice gd : lstGDs) {
//            bounds.add(gd.getDefaultConfiguration().getBounds());
//
//        }
//        return ge.getMaximumWindowBounds();
        return bounds;

    }

    public Rectangle getRectangleBounds() {
        return rectangleBounds;
    }

    private void setRectangleBounds(Rectangle rectangleBounds) {
        SelectionPane.rectangleBounds = rectangleBounds;
    }
}
