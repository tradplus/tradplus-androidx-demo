package com.tradplus.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class NetworkUtils {

    /**
     * 这个值对应TelephonyManager.NETWORK_TYPE_LTE_CA，该变量被@hide注解
     */
    private final static int NETWORK_TYPE_LTE_CA = 19;

    public static boolean checkWifiAndGPRS(Context context) {
        try {
            // 检测网络连接
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED || anInfo.getState() == NetworkInfo.State.CONNECTING) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    public enum NetworkType {
        NONE(0),
        MOBILE(1),
        MOBILE_2G(2),
        MOBILE_3G(3),
        WIFI(4),
        MOBILE_4G(5);

        NetworkType(int ni) {
            nativeInt = ni;
        }

        public int getValue() {
            return nativeInt;
        }

        final int nativeInt;
    }

    public static NetworkType getNetworkType(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            //noinspection ConstantConditions
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null || !info.isAvailable()) {
                return NetworkType.NONE;
            }
            int type = info.getType();
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkType.WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    TelephonyManager mgr = (TelephonyManager) context.getSystemService(
                            Context.TELEPHONY_SERVICE);
                    //noinspection ConstantConditions
                    switch (mgr.getNetworkType()) {
                        case TelephonyManager.NETWORK_TYPE_GSM:
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NetworkType.MOBILE_2G;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                        case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                            return NetworkType.MOBILE_3G;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                        case TelephonyManager.NETWORK_TYPE_IWLAN:
                        case NETWORK_TYPE_LTE_CA:
                            return NetworkType.MOBILE_4G;
                        default:
                            String subTypeName = info.getSubtypeName();
                            if (!TextUtils.isEmpty(subTypeName)) {
                                if (subTypeName.equalsIgnoreCase("TD-SCDMA")
                                        || subTypeName.equalsIgnoreCase("WCDMA")
                                        || subTypeName.equalsIgnoreCase("CDMA2000")) {
                                    return NetworkType.MOBILE_3G;
                                }
                            }
                            return NetworkType.MOBILE;
                    }
                default:
                    return NetworkType.MOBILE;
            }
        } catch (Throwable e) {
            return NetworkType.MOBILE;
        }
    }

    public static boolean isWifi(Context context) {
        return NetworkUtils.getNetworkType(context) == NetworkUtils.NetworkType.WIFI;
    }

    public static boolean isMobile4G(Context context) {
        return NetworkUtils.getNetworkType(context) == NetworkType.MOBILE_4G;
    }
}
