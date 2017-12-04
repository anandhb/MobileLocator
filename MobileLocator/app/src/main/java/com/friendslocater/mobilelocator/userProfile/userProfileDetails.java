package com.friendslocater.mobilelocator.userProfile;

public class userProfileDetails {

    public String first_name;
    public String email_ID;
    public String mobileNumber;
    public String password;
    public Double latitude;
    public Double longitude;
    /*public String group_code;
    public Boolean isLocationShareEnabled;*/

    public userProfileDetails()
    {

    }

    public userProfileDetails(String first_name, String mobileNumber, String email_ID, String password,
                              Double latitude, Double longitude/*String group_code,Boolean isLocationShareEnabled*/) {
        this.first_name = first_name;
        this.mobileNumber = mobileNumber;
        this.email_ID = email_ID;
        this.password = password;
        this.latitude = latitude;
        this.longitude=longitude;
        /*this.group_code = group_code;
        this.isLocationShareEnabled = isLocationShareEnabled;*/
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getEmail_ID() {
        return email_ID;
    }

    public void setEmail_ID(String email_ID) {
        this.email_ID = email_ID;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /*public String getGroup_Code() {
        return group_code;
    }

    public void setGroup_Code(String group_code) {
        this.group_code = group_code;
    }

    public Boolean getIsLocationShareEnabled() {
        return isLocationShareEnabled;
    }

    public void setIsLocationShareEnabled(Boolean isLocationShareEnabled) {
        this.isLocationShareEnabled = isLocationShareEnabled;
    }*/
}
