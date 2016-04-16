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

import fr.mathisca.screenshots.gui.TransparentSelector;
import fr.mathisca.screenshots.main.Main;
import fr.mathisca.screenshots.networking.ScreenshotUpload;
import fr.mathisca.screenshots.utils.LoggerManager;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Object that listen to all keys (not only on a JFrame, thanks to JNativeHook)
 */
public class KeyListener implements NativeKeyListener {

    /**
     * Logs outputs of this class
     */
    private final static LoggerManager l = new LoggerManager("KeyListener");

    /**
     * List of the keys
     * If <code>true</code>, then the key is pressed.
     * Else, if it's <code>false</code>, the key is released.
     */
    private final static BitSet keys = new BitSet();

    /**
     * Creates the hook
     */
    public KeyListener() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            l.logFatal("Impossible de hook les touches : " + ex.getMessage());
            Main.shutdown(1);
        }

        GlobalScreen.addNativeKeyListener(this);

    }

    /**
     * Triggered when a key is pressed
     * It changes its state in the <code>keys</code> boolean array
     *
     * @param e the key event
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        keys.set(e.getKeyCode(), true);

        if (keys.get(1)) {
            TransparentSelector s = TransparentSelector.getInstance();
            if (s != null) {
                l.logInfo("Cancelling.");
                s.dispose();
                s.removeAll();
                TransparentSelector.nullInstance();
            }
        }

        if (keys.get(29) && keys.get(42) && keys.get(2)) { //Ctrl + Shift + 1
            ScreenshotUpload.uploadScreenshot(Main.takeScreenshot());
        } else if (keys.get(29) && keys.get(42) && keys.get(3)) { //Ctrl + Shift + 2
            if (TransparentSelector.getInstance() == null)
                new TransparentSelector();
        }
    }

    /**
     * When a key is released, it changes its state in the <code>keys</code> boolean array
     *
     * @param e the key event
     */
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        keys.set(e.getKeyCode(), false);
    }

    /**
     * Not used
     *
     * @param e the key event
     */
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

    }
}
