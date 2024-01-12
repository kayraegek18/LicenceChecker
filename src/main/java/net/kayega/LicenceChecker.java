package net.kayega;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LicenceChecker extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §aLicence Checker Enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §cLicence Checker Disabled!");
    }

    public static boolean isControlIp(String licenceKey) {
        String ipAddress = getIP();
        if (ipAddress == null) {
            Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §cIp Address can't fetching, licence activation failed!");
            return false;
        }

        try {
            HttpPost post = new HttpPost("https://api.mineala.com/lisance/ip");

            // add request parameter, form parameters
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("lisance_key", licenceKey));
            urlParameters.add(new BasicNameValuePair("ip", ipAddress));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        return true;
                    case 201:
                        return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static LicenceStatus getLicenceStatus(String licenceKey) {
        String ipAddress = getIP();
        if (ipAddress == null) {
            Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §cIp Address can't fetching, licence activation failed!");
            return LicenceStatus.ERROR;
        }

        Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §eLicence status getting...");

        try {
            HttpPost post = new HttpPost("https://api.mineala.com/lisance/status");

            // add request parameter, form parameters
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("lisance_key", licenceKey));
            urlParameters.add(new BasicNameValuePair("ip", ipAddress));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        return LicenceStatus.ACTIVE;
                    case 201:
                        return LicenceStatus.NOT_ACTIVE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return LicenceStatus.ERROR;
        }
        return LicenceStatus.ERROR;
    }

    public static void activeLicence(String licenceKey) {
        String ipAddress = getIP();
        if (ipAddress == null) {
            Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §cIp Address can't fetching, licence activation failed!");
            return;
        }

        try {
            HttpPost post = new HttpPost("https://api.mineala.com/lisance/activate");

            // add request parameter, form parameters
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("lisance_key", licenceKey));
            urlParameters.add(new BasicNameValuePair("ip", ipAddress));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 401:
                        Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §cLicence activation failed!");
                        break;
                    case 404:
                        Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §cLicence not found!");
                        break;
                    case 400:
                        Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §cLicence already active!");
                        break;
                    case 200:
                        Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §aLicence activated!");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getIP() {
        String urlString = "https://checkip.amazonaws.com/";
        try {
            URL url = new URL(urlString);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
