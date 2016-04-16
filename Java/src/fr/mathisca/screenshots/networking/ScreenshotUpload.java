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

package fr.mathisca.screenshots.networking;

import fr.mathisca.screenshots.global.TrayNotifier;
import fr.mathisca.screenshots.main.Constants;
import fr.mathisca.screenshots.main.Main;
import fr.mathisca.screenshots.utils.LoggerManager;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Class used to upload a screenshot to the server through a POST request
 */
public class ScreenshotUpload {
    /**
     * Logs outputs of this class
     */
    private static final LoggerManager l = new LoggerManager("ScreenshotUpload");

    /**
     * Charset to be used in the requests
     */
    private static final String CHARSET = "UTF-8";

    /**
     * Boundary string
     */
    private static final String BOUNDARY = Long.toHexString(System.currentTimeMillis());

    /**
     * Carriage return, line feed
     */
    private static final String CRLF = "\r\n";


    /**
     * Filename of the saved image
     */
    private static String filename = "";

    /**
     * Last uploaded image url
     */
    private static String lastUploadedUrl;

    /**
     * Uploads a screenshot to a HTTP server through a POST request
     *
     * @param scr image to upload
     */
    public static void uploadScreenshot(BufferedImage scr) {
        l.logInfo("Sending..");
        disableCertificateValidation();
        File binaryFile = saveImage(scr);

        URLConnection connection = null;
        try {
            connection = new URL(Constants.LINK).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert connection != null;
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        try {
            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, CHARSET), true);

            sendPassword(writer);
            sendFile(writer, binaryFile, output);

            writer.append("--").append(BOUNDARY).append("--").append(CRLF).flush();

            if (!readReply(connection))
                throw new IllegalStateException("Error from the server");

            TrayNotifier.showInfo("Screenshot sent successfully !");

        } catch (Exception e) {
            l.logCritical("Error when sending the image ! " + e.getMessage());
            TrayNotifier.showError("Error when sending the screenshot !" +
                    "The file is still saved on the disk");
        }

    }

    /**
     * Gets the reply from the server
     *
     * @param connection connection to the server
     */
    private static boolean readReply(URLConnection connection) {
        try {
            int responseCode = ((HttpURLConnection) connection).getResponseCode();
            l.logInfo("Response code : " + responseCode);
            if (responseCode != 200)
                return false;

            l.logInfo("URL to image : " + (Constants.UPLOAD_DIR + filename));
            lastUploadedUrl = Constants.UPLOAD_DIR + filename;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Saves the image to the disk
     *
     * @param scr image to create
     * @return path to the freshly created image
     */
    private static File saveImage(BufferedImage scr) {
        filename = "screenshot-" + System.currentTimeMillis() + ".png";

        File binaryFile = new File((Main.getFolder().getPath() + "/" + filename));
        try {
            ImageIO.write(scr, "png", binaryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return binaryFile;
    }

    /**
     * Send the password to the server
     *
     * @param writer writer object to the server
     */
    private static void sendPassword(PrintWriter writer) {
        writer.append("--").append(BOUNDARY).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"password\"").append(CRLF);
        writer.append("Content-Type: text/plain; charset=" + CHARSET).append(CRLF);
        writer.append(CRLF).append(Constants.PASSWORD).append(CRLF).flush();
    }


    /**
     * Send a file to the server
     *
     * @param writer     writer object to the server
     * @param binaryFile file to send
     * @param output     output stream to the server
     */
    private static void sendFile(PrintWriter writer, File binaryFile, OutputStream output) {
        try {
            writer.append("--").append(BOUNDARY).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"screenshot\"; filename=\"").append(binaryFile.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), output);
            output.flush();
            writer.append(CRLF).flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows the program to connect to a HTTPS server without having the certificate
     * Vulnerable to MITM attacks
     */
    private static void disableCertificateValidation() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the last uploaded image url
     *
     * @return last uploaded image url
     */
    public static String getLastUploadedUrl() {
        return lastUploadedUrl;
    }
}
