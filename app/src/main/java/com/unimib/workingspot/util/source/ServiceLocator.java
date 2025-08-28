package com.unimib.workingspot.util.source;

import static com.unimib.workingspot.util.constants.WeatherConstants.WEATHER_API_KEY;
import static com.unimib.workingspot.util.constants.Constants.DATASTORE;
import static com.unimib.workingspot.util.constants.Constants.HTTP_BROWSER_HEADER;
import static com.unimib.workingspot.util.constants.Constants.USER_AGENT;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.GOOGLE_PLACES_API_KEY;

import android.app.Application;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.unimib.workingspot.database.work_place.WorkPlaceRoomDatabase;
import com.unimib.workingspot.repository.work_place.IWorkPlaceRepository;
import com.unimib.workingspot.repository.work_place.WorkPlaceRepository;
import com.unimib.workingspot.repository.user.account.UserAccountRepository;
import com.unimib.workingspot.repository.user.authentication.UserAuthenticationRepository;
import com.unimib.workingspot.repository.weather.WeatherRepository;
import com.unimib.workingspot.service.weather.WeatherAPIService;
import com.unimib.workingspot.source.user.concretes.UserRealTimeDatabaseRemoteDataSource;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceGoogleRemoteDataSource;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceLocalDataSource;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceRemoteFirebaseDataSource;
import com.unimib.workingspot.source.work_place.concretes.WorkPlaceLocalDataSource;
import com.unimib.workingspot.source.work_place.concretes.WorkPlaceRemoteFirebaseDataSource;
import com.unimib.workingspot.source.user.abstracts.BaseUserLocalDataSource;
import com.unimib.workingspot.source.user.concretes.UserAccountFirebaseDataSource;
import com.unimib.workingspot.source.user.concretes.UserAuthenticationFirebaseDataSource;
import com.unimib.workingspot.source.user.concretes.UserLocalDataSource;
import com.unimib.workingspot.source.weather.BaseWeatherLocalDataSource;
import com.unimib.workingspot.source.weather.BaseWeatherRemoteDataSource;
import com.unimib.workingspot.source.weather.WeatherLocalDataSource;
import com.unimib.workingspot.source.weather.WeatherRemoteDataSource;
import com.unimib.workingspot.source.work_place.concretes.WorkPlaceRemoteGoogleDataSource;
import com.unimib.workingspot.util.constants.WeatherConstants;
import com.unimib.workingspot.util.data_store.DataStoreManagerSingleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class responsible for providing instances of data sources,
 * repositories, and services throughout the application.
 * <p>
 * This centralizes dependency management and allows easy access
 * to commonly used components such as repositories and API services.
 */

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private PlacesClient placesClient;


    private ServiceLocator() {}

    /**
     * Retrieves the singleton instance of the ServiceLocator.
     *
     * @return The singleton instance.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Provides a local data source for user data backed by DataStore.
     *
     * @param application The application context used to get DataStore instance.
     * @return An instance of BaseUserLocalDataSource.
     */
    private BaseUserLocalDataSource getUserLocalDataSource(Application application) {
        return new UserLocalDataSource(DataStoreManagerSingleton.getInstance(application, DATASTORE));
    }

    /**
     * Provides the UserAuthenticationRepository composed of local and Firebase sources.
     *
     * @param application The application context.
     * @return An instance of UserAuthenticationRepository.
     */
    public UserAuthenticationRepository getUserAuthenticationRepository(Application application) {
        return new UserAuthenticationRepository(getUserLocalDataSource(application),
                new UserAuthenticationFirebaseDataSource());
    }

    /**
     * Provides the UserAccountRepository composed of local and remote sources.
     *
     * @param application The application context.
     * @return An instance of UserAccountRepository.
     */
    public UserAccountRepository getUserAccountRepository(Application application) {
        return new UserAccountRepository(getUserLocalDataSource(application),
                new UserRealTimeDatabaseRemoteDataSource(),
        new UserAccountFirebaseDataSource());
    }

    /**
     * OkHttpClient instance configured with an interceptor that adds a custom "User-Agent" header
     * to every outgoing HTTP request.
     * <p>
     * This is typically used to mimic browser requests or comply with API requirements
     * that expect a specific User-Agent value.
     */
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header(USER_AGENT, HTTP_BROWSER_HEADER)
                        .build();
                return chain.proceed(request);
            })
            .build();

    /**
     * Creates and returns an instance of {@link WeatherAPIService} using Retrofit.
     * @return An instance of {@link WeatherAPIService} for making weather API requests.
     */
    public WeatherAPIService getWeatherAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherConstants.WEATHER_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherAPIService.class);
    }

    /**
     * Provides the WeatherRepository composed of remote and local data sources.
     *
     * @param application The application context.
     * @return An instance of WeatherRepository.
     */

    public WeatherRepository getWeatherRepository(Application application) {
        BaseWeatherRemoteDataSource weatherRemoteDataSource;
        BaseWeatherLocalDataSource weatherLocalDataSource;
        weatherRemoteDataSource =
                new WeatherRemoteDataSource(WEATHER_API_KEY);
        weatherLocalDataSource =
                new WeatherLocalDataSource(application);
        return  new WeatherRepository(weatherRemoteDataSource, weatherLocalDataSource);
    }

    /**
     * Provides the WorkPlace Room Database instance.
     * <p>
     * This method returns an instance of the `WorkPlaceRoomDatabase`, which provides access to
     * the DAO (Data Access Object) for interacting with the local Room database.
     *
     * @param application The application context used to access the database.
     * @return An instance of `WorkPlaceRoomDatabase` for local database operations.
     */
    public WorkPlaceRoomDatabase getWorkPlaceDAO(Application application) {
        return WorkPlaceRoomDatabase.getDatabase(application);
    }


    /**
     * Provides the WorkPlace repository composed of remote Firebase, local database,
     * and Google remote data sources.
     *
     * @param application The application context.
     * @return An instance of IWorkPlaceRepository.
     */
    public IWorkPlaceRepository getWorkPlaceRepository(Application application) {
        if(!Places.isInitialized()) {
            Places.initialize(application.getApplicationContext(), GOOGLE_PLACES_API_KEY);
            placesClient = Places.createClient(application.getApplicationContext());
        }
        BaseWorkPlaceRemoteFirebaseDataSource workPlaceRemoteDataSource = new WorkPlaceRemoteFirebaseDataSource();
        BaseWorkPlaceLocalDataSource workPlaceLocalDataSource = new WorkPlaceLocalDataSource(getWorkPlaceDAO(application));
        BaseWorkPlaceGoogleRemoteDataSource workPlaceGoogleRemoteDataSource = new WorkPlaceRemoteGoogleDataSource(application.getApplicationContext(), GOOGLE_PLACES_API_KEY, placesClient);
        return new WorkPlaceRepository(workPlaceRemoteDataSource, workPlaceLocalDataSource, workPlaceGoogleRemoteDataSource);
    }


}