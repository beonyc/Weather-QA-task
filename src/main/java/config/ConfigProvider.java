package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
public interface ConfigProvider {
    Config config = ConfigFactory.load("conf/env.conf");

    String HOST = config.getString("weather-api.host");
    int PORT = config.getInt("weather-api.port");
    String API_KEY = config.getString("weather-api.key");

    String WEATHER_CURRENT_ENDPOINT = config.getString("weather-api.endpoints.current");

    static String getBaseUrl() {
        return "http://" + HOST + ":" + PORT;
    }
}
