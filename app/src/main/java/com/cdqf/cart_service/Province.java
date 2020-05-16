package com.cdqf.cart_service;


import com.cdqf.cart_city.CityThe;
import com.contrarywind.interfaces.IPickerViewData;

import java.util.ArrayList;
import java.util.List;



/**省
 * Created by liu on 2017/8/8.
 */

public class Province implements IPickerViewData {
    //省id
    private String Id;

    //省名
    private String RegionName;

    private String RegionLevel;

    private String ParentId;

    private List<CityThe> cityTheList = new ArrayList<CityThe>();

    public Province(String id, String regionName, String regionLevel, String parentId) {
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

    public List<CityThe> getCityTheList() {
        return cityTheList;
    }

    public void setCityTheList(List<CityThe> cityTheList) {
        this.cityTheList = cityTheList;
    }

    @Override
    public String getPickerViewText() {
        return this.RegionName;
    }
}
