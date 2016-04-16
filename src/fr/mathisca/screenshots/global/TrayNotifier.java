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

package fr.mathisca.screenshots.global;

import fr.mathisca.screenshots.main.Main;
import fr.mathisca.screenshots.networking.ScreenshotUpload;
import fr.mathisca.screenshots.utils.LoggerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This class manages the tray icon
 */
public class TrayNotifier implements ActionListener {
    /**
     * Logs outputs of this class
     */
    private static final LoggerManager l = new LoggerManager("TrayNotifier");

    /**
     * The tray icon of the program
     */
    private static TrayIcon trayIcon;

    /**
     * Creates the tray icon
     */
    public TrayNotifier() {
        if (!SystemTray.isSupported()) {
            l.logCritical("SystemTray is not supported !");
        }

        PopupMenu popup = new PopupMenu();

        trayIcon = new TrayIcon(createImage());
        final SystemTray tray = SystemTray.getSystemTray();

        MenuItem quit = new MenuItem("Exit");

        quit.addActionListener(e -> Main.shutdown());

        popup.add(quit);

        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        trayIcon.addActionListener(this);
    }

    /**
     * Transform the resource into an Image
     *
     * @return Image of the icon
     */
    private static Image createImage() {
        URL imageURL = TrayNotifier.class.getResource("icon.png");

        if (imageURL == null) {
            l.logCritical("Resource not found: icon.png");
            return null;
        } else {
            return (new ImageIcon(imageURL, "tray icon")).getImage();
        }
    }

    /**
     * Shows an information message through the tray icon
     *
     * @param message message to log
     */
    public static void showInfo(String message) {
        trayIcon.displayMessage("Screenshots", message, TrayIcon.MessageType.INFO);
    }

    /**
     * Shows an error message through the tray icon
     *
     * @param message message to log
     */
    public static void showError(String message) {
        trayIcon.displayMessage("Screenshots", message, TrayIcon.MessageType.ERROR);
    }

    /**
     * When the user clicks to the popup or the icon, it will open the last sent screenshot
     *
     * @param e the click event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String url = ScreenshotUpload.getLastUploadedUrl();

        if (url != null) {
            Desktop d = Desktop.getDesktop();
            try {
                d.browse(new URI(ScreenshotUpload.getLastUploadedUrl()));
            } catch (IOException e1) {
                l.logWarn("OS error : " + e1.getMessage());
            } catch (URISyntaxException e1) {
                l.logWarn("Error in url : " + e1.getMessage());
            }
        }
    }
}
