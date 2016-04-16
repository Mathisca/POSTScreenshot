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

import fr.mathisca.screenshots.global.TrayNotifier;
import fr.mathisca.screenshots.main.Main;
import fr.mathisca.screenshots.networking.ScreenshotUpload;
import fr.mathisca.screenshots.utils.LoggerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Prints the screenshot in fullscreen, the user can then select what part of the image is to upload
 */
public class TransparentSelector extends JFrame implements MouseListener, MouseMotionListener {

    /**
     * The last instance of this class
     */
    private static TransparentSelector instance;

    /**
     * The screenshot
     */
    private final BufferedImage scr;

    /**
     * Logs outputs of this class
     */
    private final LoggerManager l = new LoggerManager("TransparentSelector");

    /**
     * Selection pane used by this instance
     */
    private final SelectionPane selectionPane;

    /**
     * Where the mouse was initially
     */
    private Point mouseAnchor;

    /**
     * Where the mouse is
     */
    private Point dragPoint;

    /**
     * Creates the JFrame that takes all the screen and fills it with the screenshot
     */
    public TransparentSelector() {
        l.logInfo("Creating the selection area...");

        instance = this;
        scr = Main.takeScreenshot();
        selectionPane = new SelectionPane();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        assert scr != null;
        this.setMaximizedBounds(new Rectangle(0, 1, scr.getWidth(), scr.getHeight()));
        this.setBounds(0, 0, scr.getWidth(), scr.getHeight());
        this.setSize(scr.getWidth(), scr.getHeight());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setAutoRequestFocus(true);

        this.setUndecorated(true);
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.add(selectionPane);
        this.add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(scr, 0, 0, null);
                g.dispose();
            }
        });

        this.setVisible(true);
        l.logInfo("Waiting for user...");
    }

    /**
     * Returns the main instance
     *
     * @return instance
     */
    public static TransparentSelector getInstance() {
        return instance;
    }

    /**
     * Nullify this instance
     */
    public static void nullInstance() {
        instance = null;
    }


    /**
     * Sets the mouse anchor and created the rectangle
     *
     * @param e the mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseAnchor = e.getPoint();
        dragPoint = null;
        selectionPane.setLocation(mouseAnchor);
        selectionPane.setSize(0, 0);
    }

    /**
     * Updated the rectangle when the mouse moves
     *
     * @param e the mouse event
     */
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
        repaint();
    }

    /**
     * When the mouse is released, it re-sizes the image and destroy this instance
     *
     * @param e the mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        instance.removeAll();
        instance.dispose();
        instance = null;

        try {
            BufferedImage newScreen = scr.getSubimage(SelectionPane.x, (SelectionPane.y + 1),
                    SelectionPane.width, SelectionPane.height);

            ScreenshotUpload.uploadScreenshot(newScreen);
        } catch (Exception e1) {
            l.logWarn("Bad selection !");
            TrayNotifier.showError("Error in the selection !");
        }

    }

    /**
     * Not used
     *
     * @param e the mouse event
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Not used
     *
     * @param e the mouse event
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Not used
     *
     * @param e the mouse event
     */
    @Override
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * Not used
     *
     * @param e the mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

}
