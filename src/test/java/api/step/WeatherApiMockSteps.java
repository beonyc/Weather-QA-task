package api.step;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static config.ConfigProvider.*;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WeatherApiMockSteps {
    private static WireMockServer wireMockServer;

    public static void startServer() {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(options().port(PORT));
            wireMockServer.start();
            WireMock.configureFor(HOST, PORT);
        }
    }

    public static void stopServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }

    public static void setupCityStub(String city) {
        try {
            String fileName = "src/test/resources/mocks/weather_" + city.replace(" ", "") + ".json";
            String body = Files.readString(Paths.get(fileName));

            WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(WEATHER_CURRENT_ENDPOINT))
                    .withQueryParam("q", WireMock.equalTo(city))
                    .withQueryParam("key", WireMock.equalTo(API_KEY))
                    .willReturn(WireMock.aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(body)));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения мока для города: " + city, e);
        }
    }




    public static void setupErrorStub(String city, String apiKey, int httpStatusCode, int errorCode, String errorMessage) {
        try {
            String fileName = "src/test/resources/mocks/error_" + errorCode + ".json";
            String body = Files.readString(Paths.get(fileName));

            WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(WEATHER_CURRENT_ENDPOINT))
                    .withQueryParam("q", WireMock.equalTo(city))
                    .withQueryParam("key", WireMock.equalTo(apiKey))
                    .willReturn(WireMock.aResponse()
                            .withStatus(httpStatusCode)
                            .withHeader("Content-Type", "application/json")
                            .withBody(body)));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания мока", e);
        }
    }
}