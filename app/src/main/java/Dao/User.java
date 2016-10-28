package Dao;

import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;

/**
 * Created by wsj on 16/7/8.
 */
public class User {
    public static String id;
    public static String username;
    public static String password;
    public static String phoneNum;
    public static String idcardNum;
    public static NaviLatLng mStartPoint ;
    public static NaviLatLng mEndPoing ;

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public void setPhoneNum (String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setIdcardNum (String idcardNum) {
        this.idcardNum = idcardNum;
    }

    public static void setmStartPoint(double lat, double lng){ mStartPoint = new NaviLatLng(lat,lng);}

    public static void setmEndPoing(double lat,double lng) {
        mEndPoing = new NaviLatLng(lat,lng);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getIdcardNum() {
        return idcardNum;
    }

    public static NaviLatLng getmStartPoint() {
        return mStartPoint;
    }
}
