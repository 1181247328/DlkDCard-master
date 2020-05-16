package com.lnyp.imgdots.bean;

/**
 * Created by liu on 2018/1/3.
 */

public class ForPoint {
    private double originalPosition;
    private double androidX;
    private double androidY;
    private int imgIndex;
    private int scenicSpotId;
    private String spotName;
    private String brief;
    private String proportionalPositionX;
    private String proportionalPositionY;
    private String longitude;
    private String latitude;
    private String httpPic;
    private String httpVoice;
    //判断是不是最近的位置
    private boolean isLocation = false;

    //判断是不是要去的位置
    private boolean isLoactionMap = false;

    public double getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(double originalPosition) {
        this.originalPosition = originalPosition;
    }

    public double getAndroidX() {
        return androidX;
    }

    public void setAndroidX(double androidX) {
        this.androidX = androidX;
    }

    public double getAndroidY() {
        return androidY;
    }

    public void setAndroidY(double androidY) {
        this.androidY = androidY;
    }

    public int getImgIndex() {
        return imgIndex;
    }

    public void setImgIndex(int imgIndex) {
        this.imgIndex = imgIndex;
    }

    public int getScenicSpotId() {
        return scenicSpotId;
    }

    public void setScenicSpotId(int scenicSpotId) {
        this.scenicSpotId = scenicSpotId;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getProportionalPositionX() {
        return proportionalPositionX;
    }

    public void setProportionalPositionX(String proportionalPositionX) {
        this.proportionalPositionX = proportionalPositionX;
    }

    public String getProportionalPositionY() {
        return proportionalPositionY;
    }

    public void setProportionalPositionY(String proportionalPositionY) {
        this.proportionalPositionY = proportionalPositionY;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getHttpPic() {
        return httpPic;
    }

    public void setHttpPic(String httpPic) {
        this.httpPic = httpPic;
    }

    public String getHttpVoice() {
        return httpVoice;
    }

    public void setHttpVoice(String httpVoice) {
        this.httpVoice = httpVoice;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public boolean isLoactionMap() {
        return isLoactionMap;
    }

    public void setLoactionMap(boolean loactionMap) {
        isLoactionMap = loactionMap;
    }
}
