package com.cdqf.cart_service;

/**
 * Created by liu on 2017/8/8.
 */

public class Area {
    //市id
    private String Id;

    //市名
    private String RegionName;

    private String RegionLevel;

    private String ParentId;

    public Area(String id, String regionName, String regionLevel, String parentId) {
        Id = id;
        RegionName = regionName;
        RegionLevel = regionLevel;
        ParentId = parentId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public String getRegionLevel() {
        return RegionLevel;
    }

    public void setRegionLevel(String regionLevel) {
        RegionLevel = regionLevel;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }
}
