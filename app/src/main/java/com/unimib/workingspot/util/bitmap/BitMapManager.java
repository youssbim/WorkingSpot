package com.unimib.workingspot.util.bitmap;

import static com.unimib.workingspot.util.constants.Constants.NULL_BITMAP_ENCODING;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;

/**
 * Utility class for managing Bitmap and Drawable conversions,
 * including encoding/decoding and loading images using Glide.
 */
public class BitMapManager {

    private static final String TAG = BitMapManager.class.getSimpleName();

    /**
     * Callback interface for when a Bitmap is ready.
     */
    public interface BitmapCallback {
        /**
         * Called when the Bitmap resource is ready.
         *
         * @param bitmap The loaded bitmap.
         */
        void onBitmapReady(Bitmap bitmap);
    }

    /**
     * Callback interface for when a Drawable is ready.
     */
    public interface DrawableCallback {
        /**
         * Called when the Drawable resource is ready.
         *
         * @param drawable The loaded drawable.
         */
        void onDrawableReady(Drawable drawable);
    }

    /**
     * Loads an image from a URI into a Bitmap using Glide.
     * Falls back to a default image if the URI is null.
     *
     * @param context        The application context.
     * @param uri            The image URI.
     * @param callback       Callback to receive the loaded Bitmap.
     * @param fallbackImage  Resource ID of the fallback image.
     */
    public void createBitmap(Context context, Uri uri, BitmapCallback callback, int fallbackImage) {
        Glide.with(context)
                .asBitmap()
                .load(uri != null ? uri : fallbackImage)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        callback.onBitmapReady(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // No action needed
                    }
                });
    }

    /**
     * Converts a Bitmap to a Drawable using Glide.
     * Uses fallback image if bitmap is null.
     *
     * @param context        The application context.
     * @param bitmap         The Bitmap to convert.
     * @param callback       Callback to receive the Drawable.
     * @param fallbackImage  Resource ID of the fallback image.
     */
    public void createDrawable(Context context, Bitmap bitmap, DrawableCallback callback, int fallbackImage) {
        Glide.with(context)
                .asDrawable()
                .load(bitmap != null ? bitmap : fallbackImage)
                .placeholder(fallbackImage)
                .error(fallbackImage)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        callback.onDrawableReady(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // No action needed
                    }
                });
    }

    /**
     * Converts a Bitmap to a circular-cropped Drawable using Glide.
     * Uses fallback image if bitmap is null.
     *
     * @param context        The application context.
     * @param bitmap         The Bitmap to convert.
     * @param callback       Callback to receive the circular Drawable.
     * @param fallbackImage  Resource ID of the fallback image.
     */
    public void createCircularDrawable(Context context, Bitmap bitmap, DrawableCallback callback, int fallbackImage) {
        Glide.with(context)
                .asDrawable()
                .load(bitmap != null ? bitmap : fallbackImage)
                .circleCrop()
                .placeholder(fallbackImage)
                .error(fallbackImage)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        callback.onDrawableReady(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // No action needed
                    }
                });
    }

    /**
     * Encodes a Bitmap into a Base64 String.
     * If bitmap is null, it uses a fallback image resource.
     *
     * @param context        The application context.
     * @param bitmap         The Bitmap to encode.
     * @param fallbackImage  Resource ID of the fallback image.
     * @return Base64 encoded string or null if encoding fails.
     */
    public String encodeBitmap(Context context, Bitmap bitmap, int fallbackImage) {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), fallbackImage);
            if (bitmap == null) {
                Log.e(TAG, NULL_BITMAP_ENCODING);
                return null;
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }

    /**
     * Decodes a Base64 encoded string into a Bitmap.
     *
     * @param encodedBitmap The Base64 encoded bitmap string.
     * @return The decoded Bitmap, or null if decoding fails.
     */
    public Bitmap decodeBitmap(final String encodedBitmap) {
        if (encodedBitmap == null || encodedBitmap.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedBitmap);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }
}
