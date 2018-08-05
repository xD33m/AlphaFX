package com.sample.mainPanel.snipTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

public class SnipItPane extends JPanel {

    private Point mouseAnchor;
    private Point dragPoint;

    private SelectionPane selectionPane;
    private Rectangle selectionArea;

    public SnipItPane() {
        setOpaque(false);
        setLayout(null);
        selectionPane = new SelectionPane();
        add(selectionPane);
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseAnchor = e.getPoint();
                dragPoint = null;
                selectionPane.setLocation(mouseAnchor);
                selectionPane.setSize(0, 0);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                dragPoint = e.getPoint();
                int width = dragPoint.x - mouseAnchor.x;
                int height = dragPoint.y - mouseAnchor.y;

                int x = mouseAnchor.x;
                int y = mouseAnchor.y;

                if (width < 0) {
                    x = dragPoint.x;
                    width *= -1;
                }
                if (height < 0) {
                    y = dragPoint.y;
                    height *= -1;
                }
                selectionPane.setBounds(x, y, width, height);
                selectionPane.revalidate();
                selectionArea = new Rectangle(selectionPane.getX(), selectionPane.getY(), selectionPane.getWidth(), selectionPane.getHeight());
                repaint();
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
        Area area = new Area(bounds);
        area.subtract(new Area(selectionPane.getBounds()));

        g2d.setColor(new Color(192, 192, 192, 64));
        g2d.fill(area);

    }

}
