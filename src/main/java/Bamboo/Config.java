package Bamboo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private final Properties properties = new Properties();

    public Config() {
        try{
            properties.load(new FileInputStream(".properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDomain() { return properties.getProperty("domain"); }
    public String getToken() { return properties.getProperty("token"); }
    public String getAuthHeader() { return properties.getProperty("auth_header"); }
}
