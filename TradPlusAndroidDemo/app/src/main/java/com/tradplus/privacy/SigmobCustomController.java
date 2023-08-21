package com.tradplus.privacy;

import android.location.Location;

import com.sigmob.windad.WindCustomController;

public class SigmobCustomController extends WindCustomController {


    public boolean isCanUseLocation() {
        return super.isCanUseLocation();
    }

    public Location getLocation() {
        return null;
    }

    public boolean isCanUsePhoneState() {
        return super.isCanUsePhoneState();
    }

    public String getDevImei() {
        return null;
    }

    public String getDevOaid() {
        return null;
    }

    public boolean isCanUseAndroidId() {

        return super.isCanUseAndroidId();
    }

    public String getAndroidId() {
        return null;
    }



}
