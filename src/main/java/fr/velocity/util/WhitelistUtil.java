package fr.velocity.util;

import net.minecraft.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

import static fr.velocity.mod.proxy.CommonProxy.WHITELIST_URL;

public class WhitelistUtil {

    /**
     * Checks if the given server IP is whitelisted by querying a remote URL.
     *
     * @param serverIp The IP address of the server to check.
     * @return true if the IP is whitelisted, false otherwise.
     */
    public static boolean isIpWhitelisted(String serverIp) {
        try {
            URL url = new URL(WHITELIST_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }
            in.close();
            connection.disconnect();

            String[] whitelistedIps = content.toString().split("\n");

            for (String ip : whitelistedIps) {
                if (ip.trim().equals(serverIp)) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getRealIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    public static String getServerIp(MinecraftServer server) {
        return server.isDedicatedServer() ? getRealIp() : "127.0.0.1"; //wtf ???
    }


}
