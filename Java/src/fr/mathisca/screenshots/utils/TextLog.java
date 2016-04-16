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

import fr.mathisca.screenshots.main.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class that export all logs saved through LoggerManager to a file
 */
public class TextLog {
    /**
     * Contains all logs
     */
    private final static ArrayList<String> logsContainer = new ArrayList<>();

    /**
     * Logs outputs of this class
     */
    private final static LoggerManager l = new LoggerManager("TextLog");

    /**
     * Adds a log to logsContainer
     *
     * @param log log to add to the array
     * @see LoggerManager
     */
    static void addLog(String log) {
        logsContainer.add(log);
    }

    /**
     * Exports logs to a file
     */
    public static void exportLog() {
        Date d = new Date();
        File file = new File(Main.getFolder().getAbsolutePath() + "/logs/log-" + d.getTime() + ".log");

        File f = new File(Main.getFolder().getAbsolutePath() + "/logs/");
        if((!f.exists() || !f.isDirectory()) && f.mkdir()) {
            Main.shutdown(2);
        }

        PrintWriter printWriter = null;

        try {
            printWriter = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        l.logInfo("Logs saved !");

        for (String log : logsContainer) {
            assert printWriter != null;
            printWriter.println(log);
        }

        assert printWriter != null;
        printWriter.close();

    }

}
