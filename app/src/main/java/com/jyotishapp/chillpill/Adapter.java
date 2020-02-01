package com.jyotishapp.chillpill;

import com.mapbox.geojson.Point;

public class Adapter {

    private String name ;
    Point loc ;
    private String phone ;

    Adapter(String name , Point loc , String phone )
    {
        this.name = name ;
        this.loc = loc ;
        this.phone = phone ;
    }

    public Point getLoc() {
        return loc;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setLoc(Point loc) {
        this.loc = loc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
