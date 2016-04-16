/*
 * Copyright (C) 2016 Mathis Cariou
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.mathisca.screenshots.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Created a rectangle over the TransparentSelector
 */
class SelectionPane extends JPanel implements ComponentListener {

    /**
     * Actual x of the rectangle
     */
    public static int x;

    /**
     * Actual yof the rectangle
     */
    public static int y;

    /**
     * Actual width of the rectangle
     */
    public static int width;

    /**
     * Actual height of the rectangle
     */
    public static int height;

    /**
     * Creates the selection JPanel
     */
    public SelectionPane() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.gridy++;

        this.addComponentListener(this);
    }

    /**
     * Repaint the rectangle each time the bounds are changed by the TransparentSelector
     *
     * @param g graphics of the instance
     * @see TransparentSelector#mouseDragged
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(128, 128, 128, 64));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        float dash1[] = {10.0f};
        BasicStroke dashed =
                new BasicStroke(3.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f, dash1, 0.0f);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(dashed);
        g2d.drawRect(0, 0, getWidth() - 3, getHeight() - 3);
        g2d.dispose();
    }

    /**
     * Updates the variables of the position of the rectangle
     *
     * @param e the event
     */
    @Override
    public void componentResized(ComponentEvent e) {
        x = this.getX();
        y = this.getY();
        width = this.getWidth();
        height = this.getHeight();
    }

    /**
     * Not used
     *
     * @param e the event
     */
    @Override
    public void componentMoved(ComponentEvent e) {

    }

    /**
     * Not used
     *
     * @param e the event
     */
    @Override
    public void componentShown(ComponentEvent e) {

    }

    /**
     * Not used
     *
     * @param e the event
     */
    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
