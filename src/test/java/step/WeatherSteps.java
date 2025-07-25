package step;

import api.step.WeatherApiMockSteps;
import client.WeatherApiClient;
import config.ConfigProvider;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.ErrorResponse;
import model.WeatherResponse;
import org.assertj.core.api.SoftAssertions;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class WeatherSteps {
    private final WeatherApiClient client = new WeatherApiClient();
    private Response response;
    private final Map<String, Double> expectedTemps = new HashMap<>();
    private final Map<String, WeatherResponse> actualResponses = new HashMap<>();
    private int httpStatusCode;
    private int errorCode;
    private String errorMessage;

    @BeforeAll
    public static void startMock() {
        WeatherApiMockSteps.startServer();
    }

    @AfterAll
    public static void stopMock() {
        WeatherApiMockSteps.stopServer();
    }

    @Given("Даны следующие города и ожидаемые температуры:")
    public void givenCitiesAndTemperatures(DataTable table) {
        table.asMaps().forEach(row -> {
            String city = row.get("city");
            double expected = Double.parseDouble(row.get("expectedTemp"));
            expectedTemps.put(city, expected);
            WeatherApiMockSteps.setupCityStub(city);
        });
    }

    @When("Отправляем запрос на получение погоды по каждому городу")
    public void getTemperature() {
        expectedTemps.keySet().forEach(city -> {
            WeatherResponse response = client.getCurrentWeather(city);
            actualResponses.put(city, response);
        });
    }

    @Then("Температура должна совпадать")
    public void compareTemperatures() {
        SoftAssertions softly = new SoftAssertions();

        expectedTemps.forEach((city, expectedTemp) -> {
            WeatherResponse response = actualResponses.get(city);
            double actualTemp = response.getCurrent().getTemp_c();

            softly.assertThat(actualTemp)
                    .as("Температура в городе %s", city)
                    .isEqualTo(expectedTemp);
        });

        softly.assertAll();
    }

    @When("Запрашиваем погоду для {string} с ключом {string} с параметрами:")
    public void requestWeatherWithKey(String city, String apiKey, DataTable dataTable) {
        Map<String, String> params = dataTable.asMaps().get(0);
// Получаем ожидаемые коды из примера
        httpStatusCode = Integer.parseInt(params.get("responseCode"));
        errorCode = Integer.parseInt(params.get("errorMessageCode"));
        errorMessage = String.valueOf(params.get("errorMessage"));

        String correctApiKey = "valid_key".equals(apiKey) ? ConfigProvider.API_KEY : apiKey;

        // Настраиваем мок
        WeatherApiMockSteps.setupErrorStub(
                city,
                correctApiKey,
                httpStatusCode,
                errorCode,
                errorMessage
        );

        // Выполняем запрос
        response = client.getCurrentWeather(
                city.isEmpty() ? null : city,
                correctApiKey.isEmpty() ? null : correctApiKey
        );
    }

    @Then("Должны получить ошибку:")
    public void verifyErrorResponse(String expectedErrorJson) {
        // Проверяем статус код
        response.then().statusCode(httpStatusCode);

        // Проверяем тело ответа
        ErrorResponse expectedError = client.parseErrorResponse(expectedErrorJson);
        ErrorResponse actualError = response.as(ErrorResponse.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualError.getCode()).isEqualTo(expectedError.getCode());
        softly.assertThat(actualError.getMessage()).isEqualTo(expectedError.getMessage());
        softly.assertAll();
    }


}

