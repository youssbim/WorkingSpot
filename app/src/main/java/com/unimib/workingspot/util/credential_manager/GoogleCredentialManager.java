package com.unimib.workingspot.util.credential_manager;

import static com.unimib.workingspot.util.constants.AuthenticationConstants.FORMAT_SPECIFIER;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.NONCE_ALG_TYPE;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.NONCE_ERROR;
import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.unimib.workingspot.R;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Utility class to handle Google Credential Manager configuration and setup
 * for sign-in using the Jetpack Credential API.
 */
public class GoogleCredentialManager {

    private static final String TAG = GetCredentialRequest.class.getSimpleName();

    /**
     * Creates a CredentialManager and a GetCredentialRequest configured for
     * Google Sign-In with a hashed nonce for security.
     *
     * @param activity The activity from which this method is called.
     * @param view The view context used to initialize CredentialManager.
     * @return A Pair containing the CredentialManager and the GetCredentialRequest.
     */
    public Pair<CredentialManager, GetCredentialRequest> createCredentialManager(Activity activity, View view) {
        CredentialManager credentialManager = CredentialManager.create(view.getContext());

        GetCredentialRequest getCredentialRequest = null;

        try {
            GetSignInWithGoogleOption getSignInWithGoogleOption = new GetSignInWithGoogleOption
                    .Builder(activity.getString(R.string.default_web_client_id))
                    .setNonce(nonceGenerator())
                    .build();

            getCredentialRequest = new GetCredentialRequest.Builder()
                    .addCredentialOption(getSignInWithGoogleOption)
                    .build();
        } catch (NoSuchAlgorithmException ex) {
            Log.e(TAG, NONCE_ERROR, ex);
        }

        return new Pair<>(credentialManager, getCredentialRequest);
    }

    /**
     * Generates a secure, hashed nonce using the algorithm specified in constants.
     * This nonce is used to prevent replay attacks during authentication.
     *
     * @return A hashed string representing the nonce.
     * @throws NoSuchAlgorithmException If the specified algorithm is invalid or unsupported.
     */
    private String nonceGenerator() throws NoSuchAlgorithmException {
        // Generate a random UUID as the raw nonce
        String rawNonce = UUID.randomUUID().toString();

        // Convert the string to bytes
        byte[] bytes = rawNonce.getBytes();

        // Create a message digest with the specified algorithm (e.g., SHA-256)
        MessageDigest md = MessageDigest.getInstance(NONCE_ALG_TYPE);

        // Compute the hash (digest)
        byte[] digest = md.digest(bytes);

        // Convert the byte array to a hexadecimal string
        StringBuilder hashedNonce = new StringBuilder();
        for (byte b : digest) {
            hashedNonce.append(String.format(FORMAT_SPECIFIER, b)); // Usually "%02x"
        }

        // Return the final hashed nonce as a string
        return hashedNonce.toString();
    }
}
