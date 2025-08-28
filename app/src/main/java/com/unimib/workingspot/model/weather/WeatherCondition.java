package com.unimib.workingspot.model.weather;

import static com.unimib.workingspot.util.constants.WeatherConstants.WEATHER_API_CONDITION_SERIALIZED_NAME;

import com.google.gson.annotations.SerializedName;

/**
 *  This class represents a "WeatherCondition" object, which holds the
 *  description of the weather condition and a corresponding weather code.
 *  (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 */

public class WeatherCondition {

    @SerializedName(WEATHER_API_CONDITION_SERIALIZED_NAME)
    private String condition; // The weather condition description
    private int code; // The weather condition associated code

    /**
     * Default no-argument constructor required for Gson deserialization
     */
    public WeatherCondition() {

    }

    /**
     * Parameterized constructor to initialize WeatherCondition with a specific
     * condition description and its associated code.
     * @param condition  a string representing the weather condition
     * @param code the condition's associated code
     */
    public WeatherCondition(String condition, int code) {
        setCondition(condition);
        setCode(code);
    }

    /**
     * Gets the weather condition description.
     *
     * @return the condition description as a string.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets the weather condition description.
     *
     * @param condition - the weather condition description to set.
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Gets the weather condition code.
     *
     * @return the weather condition code as an integer.
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the weather condition code.
     *
     * @param code - the weather condition code to set.
     */
    public void setCode(int code) {
        this.code = code;
    }
}
