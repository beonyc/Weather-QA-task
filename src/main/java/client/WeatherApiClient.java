package client;

import config.ConfigProvider;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import model.ErrorResponse;
import model.WeatherResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static config.ConfigProvider.*;

public class WeatherApiClient {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Step("Получаем погоду для '{city}' с ключом '{apiKey}'")
    public Response getCurrentWeather(String city, String apiKey) {
        return RestAssured
                .given()
                .queryParam("q", city)
                .queryParam("key", apiKey)
                .when()
                .get(getBaseUrl() + WEATHER_CURRENT_ENDPOINT);
    }

    @Step("Парсинг тела ошибки")
    public ErrorResponse parseErrorResponse(String json) {
        try {
            return mapper.readValue(json, ErrorResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга JSON", e);
        }
    }
    @Step("Получаем температуру города {city}")
    public WeatherResponse getCurrentWeather(String city) {
        return RestAssured
                .given()
                .queryParam("q", city)
                .queryParam("key", API_KEY)
                .when()
                .get(getBaseUrl() + WEATHER_CURRENT_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .as(WeatherResponse.class, ObjectMapperType.JACKSON_2);
    }
}
