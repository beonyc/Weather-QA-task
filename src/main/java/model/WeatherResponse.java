package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class WeatherResponse {
    private Current current;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Current {
        private double temp_c;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
