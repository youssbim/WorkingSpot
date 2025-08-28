package com.unimib.workingspot.model;

import static com.unimib.workingspot.util.constants.Constants.COLON;
import static com.unimib.workingspot.util.constants.Constants.NULL;
import static com.unimib.workingspot.util.constants.Constants.PIPE;
import static com.unimib.workingspot.util.constants.Constants.PIPE_REGEX;
import static com.unimib.workingspot.util.constants.Constants.USER_CLASS;

import androidx.annotation.NonNull;

public class User {
    private String uid;
    private String email;
    private String username;
    private String profilePicture;

    /**
     * Constructor to create a User object with all fields.
     * @param uid User ID
     * @param email User email address
     * @param username User's display name
     * @param profilePicture URL or path to profile picture
     */
    public User(String uid, String email, String username, String profilePicture){
        setUid(uid);
        setEmail(email);
        setUsername(username);
        setProfilePicture(profilePicture);
    }

    /**
     * Parses a string representation of a user and creates a User object.
     * The input string is expected to start with a prefix (5 characters),
     * which is removed before splitting the remaining string by '|'.
     * Fields with "null" string are converted to null.
     *
     * @param userString The string to parse, e.g. "User:<uid>|<email>|<username>|<profilePicture>"
     * @return User object created from parsed values
     */

    public static User userFromString(String userString){
        userString = userString.substring(5);
        String[] user = userString.split(PIPE_REGEX);
        return new User(
                user[0].trim().equals(NULL) ? null : user[0].trim(),
                user[1].trim().equals(NULL) ? null : user[1].trim(),
                user[2].trim().equals(NULL) ? null : user[2].trim(),
                user[3].trim().equals(NULL) ? null : user[3].trim()
        );
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getProfilePicture() {return profilePicture;}
    public void setProfilePicture(String profilePicture) {this.profilePicture = profilePicture;}

    /**
     * Returns a string representation of the User object,
     * prefixed by USER_CLASS and fields separated by '|'.
     * This string format matches the one expected by userFromString method.
     */
    @NonNull
    @Override
    public String toString() {
        return USER_CLASS + COLON +
                getUid() + PIPE +
                getEmail() + PIPE +
                getUsername() + PIPE +
                getProfilePicture();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
