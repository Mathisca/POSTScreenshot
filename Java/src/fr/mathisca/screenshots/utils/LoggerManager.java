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

package fr.mathisca.screenshots.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to instantiate at the beginning of each class that needs outputs
 *
 * @version 1.05
 */
public class LoggerManager {

    /**
     * Display name
     */
    private final String name;

    /**
     * Class constructor
     *
     * @param name display name in console
     */
    public LoggerManager(String name) {
        super();
        this.name = name.toUpperCase();
    }

    /**
     * This generates output to console.
     */
    private void log(String type, String message) {
        System.out.flush();
        System.err.flush();


        final Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd'/'MM'/'yy kk:mm:ss:SSS");

        if (type.equals("CRITICAL") || type.equals("FATAL")) {
            String log = format.format(d) + " [" + type + "] [" + name + "] " + message;
            System.err.println(log);
            TextLog.addLog(log);
        } else {
            String log = format.format(d) + " [" + type + "] [" + name + "] " + message;
            System.out.println(log);
            TextLog.addLog(log);
        }
    }

    /**
     * Log an information
     *
     * @param message message to log
     */
    public final void logInfo(String message) {
        this.log("INFO", message);
    }

    /**
     * Log a warning message
     *
     * @param message message to log
     */
    public final void logWarn(String message) {
        this.log("WARNING", message);
    }

    /**
     * Log a critical message
     *
     * @param message message to log
     */
    public final void logCritical(String message) {
        this.log("CRITICAL", message);
    }

    /**
     * Log a fatal message
     *
     * @param message message to log
     */
    public final void logFatal(String message) {
        this.log("FATAL", message);
    }

}