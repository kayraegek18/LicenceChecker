package net.kayega;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
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

    public static LicenceStatus getLicenceStatus(String licenceKey) {
        String ipAddress = getIP();
        if (ipAddress == null) {
            Bukkit.getServer().getConsoleSender().sendMessage("§7§[aLicenceChecker§7] §cIp Address can't fetching, licence activation failed!");
            return LicenceStatus.ERROR;
        }

        try {
            URL url = new URL("https://api.mineala.com/lisance/status");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("lisance_key", licenceKey);
            parameters.put("ip", ipAddress);

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            int status = con.getResponseCode();
            switch (status) {
                case 200:
                    return LicenceStatus.ACTIVE;
                case 201:
                    return LicenceStatus.NOT_ACTIVE;
            }
            con.disconnect();
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
            URL url = new URL("https://api.mineala.com/lisance/activate");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("lisance_key", licenceKey);
            parameters.put("ip", ipAddress);

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            int status = con.getResponseCode();
            switch (status) {
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
            con.disconnect();
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
