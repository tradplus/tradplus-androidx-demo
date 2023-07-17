package com.tradplus.privacy;

import com.tapsdk.tapad.CustomUser;
import com.tapsdk.tapad.TapAdCustomController;
import com.tapsdk.tapad.TapAdLocation;

public class TapTapUserDataCustomController extends TapAdCustomController {


    // 是否允许 SDK 主动使用地理位置信息
    @Override
    public boolean isCanUseLocation() {
        return super.isCanUseLocation();
    }

    // 当 isCanUseLocation=false 时，可传入地理位置信息，TapAd 使用您传入的地理位置信息
    @Override
    public TapAdLocation getTapAdLocation() {
        return super.getTapAdLocation();
    }

    // 是否允许 SDK 主动使用手机硬件参数，如 imei
    @Override
    public boolean isCanUsePhoneState() {
        return super.isCanUsePhoneState();
    }

    // 当 isCanUsePhoneState=false 时，可传入 imei 信息，TapAd 使用您传入的 imei 信息
    @Override
    public String getDevImei() {
        return super.getDevImei();
    }

    // 是否允许 SDK 主动使用 ACCESS_WIFI_STATE 权限
    @Override
    public boolean isCanUseWifiState() {
        return super.isCanUseWifiState();
    }

    // 是否允许 SDK 主动使用 WRITE_EXTERNAL_STORAGE 权限
    @Override
    public boolean isCanUseWriteExternal() {
        return super.isCanUseWriteExternal();
    }

    // 开发者可以传入 oaid
    // 信通院 OAID 的相关采集——如何获取 OAID：
    // 1. 移动安全联盟官网 http://www.msa-alliance.cn/
    // 2. 信通院统一 SDK 下载 http://msa-alliance.cn/col.jsp?id=120
    @Override
    public String getDevOaid() {
        return super.getDevOaid();
    }

    // 是否允许 SDK 主动获取设备上应用安装列表的采集权限
    @Override
    public boolean alist() {
        return super.alist();
    }

    // 是否允许 SDK 主动获取 ANDROID_ID
    @Override
    public boolean isCanUseAndroidId() {
        return super.isCanUseAndroidId();
    }

    @Override
    public CustomUser provideCustomUser() {
        return super.provideCustomUser();
    }

}
