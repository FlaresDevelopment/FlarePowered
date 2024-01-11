package net.flarepowered.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utility {

    public static int post(URL url, String payload) {
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Accept", "application/json");
            byte[] out = payload.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = connection.getOutputStream();
            stream.write(out);
            connection.disconnect();
            return connection.getResponseCode();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 400;
    }

    public static byte[] generateChecksum(String filePath, String algorithm) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filePath), md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {}
        }

        return md.digest();
    }

    public static String generateStringChecksum(String filePath, String algorithm) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filePath), md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {}
        }

        byte[] digest = md.digest();

        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

}
