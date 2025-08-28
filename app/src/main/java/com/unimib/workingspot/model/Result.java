package com.unimib.workingspot.model;

import com.unimib.workingspot.model.weather.WeatherAPIResponse;

import java.util.Collections;
import java.util.List;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        return !(this instanceof Error);
    }


    /**
     * Represents a successful result containing the weather API response.
     * This class extends the base {@link Result} class
     */
    public static final class WeatherSuccess extends Result {
        // The weather API response that contains the weather data returned by the API
        private final WeatherAPIResponse weatherAPIResponse;

        /**
         * Constructor to initialize the WeatherSuccess object with a specific
         * WeatherAPIResponse.
         *
         * @param weatherAPIResponse - the response data from the weather API.
         */
        public WeatherSuccess(WeatherAPIResponse weatherAPIResponse) {
            this.weatherAPIResponse = weatherAPIResponse;
        }

        /**
         * Gets the weather API response data stored in this result.
         *
         * @return the weather API response containing the weather data.
         */
        public WeatherAPIResponse getData() {
            return weatherAPIResponse;
        }
    }


    /**
     * Represents a successful operation that returns a {@link User} object.
     * This class extends the base {@link Result} class
     */
    public static final class UserSuccess extends Result {
        private final User user;

        /**
         * Constructor to initialize the UserSuccess with a specific {@link User}
         * @param user The user contained in the response
         */
        public UserSuccess(User user) {
            this.user = user;
        }

        /**
         * Gets the {@link User} contained in the response
         * @return The user contained in the response
         */
        public User getUser() {
            return user;
        }
    }

    /**
     * Represents a generic successful response.
     * This class extends the base {@link Result} class
     */
    public static final class ResponseSuccess extends Result {
        private final String response;

        /**
         * Constructor to initialize the ResponseSuccess with a specific message
         * @param response The response message
         */
        public ResponseSuccess(final String response) {
            this.response = response;
        }

        /**
         * Gets the response message
         * @return The response message
         */
        public String getResponse() {
            return response;
        }
    }

    /**
     * Represents a generic error response.
     * This class extends the base {@link Result} class
     */
    public static final class Error extends Result {
        private final String errorMessage;

        /**
         * Constructor to initialize the Error with a specific message
         * @param errorMessage The error message
         */
        public Error(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        /**
         * Gets the error message
         * @return The error message
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    /**
     * Represents a successful result containing workplace data.
     * This class extends the {@link Result} class and encapsulates a list of workplaces
     * (which could either be multiple workplaces or a single workplace).
     * */
    public static final class WorkPlaceSuccess extends Result {
        private final List<WorkPlace> workPlaceList;

        /**
         * Constructor for initializing with a list of workplaces.
         *
         * @param workPlaceList - the list of workplaces.
         */
        public WorkPlaceSuccess(List<WorkPlace> workPlaceList) {
            this.workPlaceList = workPlaceList;
        }

        /**
         * Constructor for initializing with a single workplace.
         * This constructor wraps the workplace in a list for consistency.
         *
         * @param workPlace - a single workplace.
         */
        public WorkPlaceSuccess(WorkPlace workPlace) {
            this.workPlaceList = Collections.singletonList(workPlace);
        }

        /**
         * Gets the list of workplaces.
         *
         * @return the list of workplaces.
         */
        public List<WorkPlace> getWorkPlaceList() {
            return workPlaceList;
        }
    }

}
