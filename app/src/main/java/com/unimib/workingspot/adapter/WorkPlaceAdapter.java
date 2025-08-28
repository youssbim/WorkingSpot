package com.unimib.workingspot.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.workingspot.R;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.util.bitmap.BitMapManager;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * RecyclerView.Adapter implementation that binds WorkPlace data to a card layout.
 * Each card displays the workplace's name, address, image (decoded from a base64 string)
 * and a favourite checkbox
 */
public class WorkPlaceAdapter extends RecyclerView.Adapter<WorkPlaceAdapter.ViewHolder> {

    /**
     * Interface for handling user interaction with each workplace item
     */
    public interface OnItemClickListener {
        /**
         * Callback for when a workplace is clicked/tapped
         * @param workPlace the selected workplace object
         */
        void onWorkPlaceItemClick(WorkPlace workPlace);

        /**
         * Callback for when the favourite button on a card is pressed
         * @param position the position in the list of the WorkPlace that the user want to save
         */
        void onFavouriteButtonPressed(int position);
    }
    private final int layout; // Resource ID for the card layout
    private final List<WorkPlace> workPlaceList; // The list of WorkPlaces to display
    private Context context; // Context the view is running in
    private final OnItemClickListener onItemClickListener; // Listener for the item events
    private final BitMapManager bitMapManager;
    private final boolean favouriteButtonEnabled;


    /**
     * Constructor for the WorkPlaceAdapter class
     * @param layout the resource ID for the card layout
     * @param workPlaceList The list of WorkPlaces object to display
     * @param favouriteButtonEnabled If the favourite button should be showed or not
     * @param onItemClickListener The listener for handling click events
     */
    public WorkPlaceAdapter(int layout, List<WorkPlace> workPlaceList,
                            boolean favouriteButtonEnabled,
                            OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.workPlaceList = workPlaceList;
        this.onItemClickListener = onItemClickListener;
        this.bitMapManager = new BitMapManager();
        this.favouriteButtonEnabled = favouriteButtonEnabled;
    }

    /**
     * ViewHolder class that holds references to the UI components of a single card
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private final TextView textViewWorkPlaceName;
        private final TextView textViewWorkPlaceAddress;
        private final CheckBox favouriteButton;
        private final ImageView imageViewWorkPlace;


        /**
         * Constructor for the ViewHolder class
         * @param view The view for the individual card
         */
        public ViewHolder(View view) {
            super(view);
            textViewWorkPlaceName = itemView.findViewById(R.id.name);
            textViewWorkPlaceAddress = itemView.findViewById(R.id.address);
            favouriteButton = itemView.findViewById(R.id.favouriteButton);
            imageViewWorkPlace = itemView.findViewById(R.id.card_image);
            view.setOnClickListener(this);
        }

        /**
         * Handles the click events on the card and on the favourite button
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.favouriteButton)
                onItemClickListener.onFavouriteButtonPressed(getBindingAdapterPosition());
            else
                onItemClickListener
                        .onWorkPlaceItemClick(workPlaceList.get(getBindingAdapterPosition()));
        }

        /**
         * Getter method for the WorkPlace name TextView
         * @return The WorkPlace name TextView of the card
         */
        public TextView getTextViewWorkPlaceName() {
            return textViewWorkPlaceName;
        }

        /**
         * Getter method for the WorkPlace address TextView
         * @return The WorkPlace address TextView of the card
         */
        public TextView getTextViewWorkPlaceAddress() {
            return textViewWorkPlaceAddress;
        }

        /**
         * Getter method for the favourite button
         * @return The favourite checkbox of the card
         */
        public CheckBox getFavouriteButton() {
            return favouriteButton;
        }

        /**
         * Getter method for the ImageView
         * @return The ImageView of the card
         */
        public ImageView getImageViewWorkPlace() {
            return imageViewWorkPlace;
        }
    }

    /**
     * Inflates the card layout and returns a new ViewHolder
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return a new ViewHolder for the card
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(this.context == null)
            this.context = parent.getContext(); // Set the context if it was not already set

        // Inflate the view
        View view = LayoutInflater.from(this.context)
                .inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Binds the data from a WorkPlace object to the corresponding views in the ViewHolder
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set the name and address to be displayed on the card
        holder.getTextViewWorkPlaceName().setText(workPlaceList.get(position).getName());
        holder.getTextViewWorkPlaceAddress().setText(workPlaceList.get(position).getAddress());

        // Sets the visibility of the favourite button
        if(favouriteButtonEnabled) {
            holder.getFavouriteButton().setChecked(workPlaceList.get(position).isSaved());
            // Set the listener for when the favourite button is pressed
            holder.getFavouriteButton().setOnClickListener(view ->
                    onItemClickListener.onFavouriteButtonPressed(position));
        } else {
            holder.getFavouriteButton().setVisibility(View.GONE);
        }
        // Decode the base64 string to an image and display it
        Bitmap bitmap = bitMapManager.decodeBitmap(workPlaceList.get(position).getB64PhotoEncoding());
        holder.getImageViewWorkPlace().setImageBitmap(bitmap);
    }

    /**
     * Returns the number of WorkPlaces in the list
     * @return the number of items in the adapter's list
     */
    @Override
    public int getItemCount() {
        return workPlaceList.size();
    }

}
