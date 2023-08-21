package com.tradplus.privacy;

import com.bytedance.sdk.openadsdk.LocationProvider;
import com.bytedance.sdk.openadsdk.TTCustomController;

// 国内隐私信息控制开关
// 开发者自行控制：true同意，false拒绝
public class CSJCustomUserData extends TTCustomController {

    // 是否允许SDK主动使用地理位置信息
    public boolean isCanUseLocation() {
        return true;
    }

    public LocationProvider getTTLocation() {
        return null;
    }

    // 是否允许SDK主动获取设备上应用安装列表的采集权限
    public boolean alist() {
        return true;
    }

    // 是否允许SDK主动使用手机硬件参数，如：imei
    public boolean isCanUsePhoneState() {
        return true;
    }

    public String getDevImei() {
        return null;
    }

    //是否允许SDK主动使用ACCESS_WIFI_STATE权限
    public boolean isCanUseWifiState() {
        return true;
    }

    public String getMacAddress() {
        return null;
    }

    //是否允许SDK主动使用WRITE_EXTERNAL_STORAGE权限
    public boolean isCanUseWriteExternal() {
        return true;
    }

    public String getDevOaid() {
        return null;
    }

    // 是否允许SDK主动获取ANDROID_ID
    public boolean isCanUseAndroidId() {
        return true;
    }

    public String getAndroidId() {
        return null;
    }

    public boolean isCanUsePermissionRecordAudio() {
        return true;
    }

}
