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

package fr.mathisca.screenshots.main;

import fr.mathisca.screenshots.global.KeyListener;
import fr.mathisca.screenshots.global.TrayNotifier;
import fr.mathisca.screenshots.utils.LoggerManager;
import fr.mathisca.screenshots.utils.TextLog;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Main class
 */
public class Main {
    /**
     * Logs outputs of this class
     */
    private final static LoggerManager l = new LoggerManager("Main");

    /**
     * Initialize the program
     *
     * @param args not used
     */
    public static void main(String[] args) {
        new LoggerManager("Main").logInfo("Program initialized.");
        new KeyListener();
        new TrayNotifier();
    }

    /**
     * Take a screenshot of all screens
     * by 11101101b from stackoverflow
     *
     * @return screenshot of all screens
     */
    public static BufferedImage takeScreenshot() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        Rectangle allScreenBounds = new Rectangle();

        for (GraphicsDevice screen : screens) {
            Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();

            allScreenBounds.width += screenBounds.width;
            allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
        }

        try {
            Robot robot = new Robot();
            return robot.createScreenCapture(allScreenBounds);
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates and return a temporary folder for the application
     *
     * @return temporary folder
     */
    public static File getFolder() {
        File f = new File(System.getenv("APPDATA") + "/screenshots/");

        if (!f.exists() || f.isFile()) {
            if(!f.mkdir()) {
                l.logFatal("Can't create temp folder !");
                shutdown(2);
            }
        }
        return f;
    }

    /**
     * Stops the program and export logs
     */
    public static void shutdown() {
        TextLog.exportLog();
        System.exit(0);
    }

    /**
     * Stops the program with an exit code and export logs
     *
     * @param code exit code
     */
    public static void shutdown(int code) {
        if(code != 2)
            TextLog.exportLog();
        System.exit(code);
    }

}
