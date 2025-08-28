package com.unimib.workingspot.ui.intro;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.unimib.workingspot.R;

/**
 * Activity that hosts the intro screens of the application. This activity hosts the following fragments:
 * <ul>
 *     <li>{@link com.unimib.workingspot.ui.intro.fragments.IntroStartFragment IntroStartFragment}</li>
 *     <li>{@link com.unimib.workingspot.ui.intro.fragments.RequestGeolocalizationPermissionsFragment RequestGeolocalizationPermissionsFragment}</li>
 * </ul>
 */
public class IntroActivity extends AppCompatActivity {
    /**
     * Called when the activity is created
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
    }

}