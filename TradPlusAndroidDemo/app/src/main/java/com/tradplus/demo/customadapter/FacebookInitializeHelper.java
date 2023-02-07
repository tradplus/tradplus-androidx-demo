package com.tradplus.demo.customadapter;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.tradplus.ads.base.adapter.TPLoadAdapterListener;
import com.tradplus.ads.base.common.TPError;
import com.tradplus.ads.base.util.AppKeyManager;
import com.tradplus.ads.base.util.TestDeviceUtil;


import static com.facebook.ads.BuildConfig.DEBUG;
import static com.tradplus.ads.base.common.TPError.ADAPTER_ACTIVITY_ERROR;
import static com.tradplus.ads.base.common.TPError.NETWORK_NO_FILL;

import java.util.Map;

/**
 * Sample class that shows how to call initialize() method of Audience Network SDK.
 */
public class FacebookInitializeHelper
        implements AudienceNetworkAds.InitListener {

    private final Context mContext;

    private FacebookInitializeHelper(Context context) {
        mContext = context;
    }

    /**
     * It's recommended to call this method from Application.onCreate().
     * Otherwise you can call it from all Activity.onCreate()
     * methods for Activities that contain ads.
     *
     * @param context Application or Activity.
     */
    static void initialize(Context context) {

        // 设置测试模式
        AdSettings.setTestMode(TestDeviceUtil.getInstance().isNeedTestDevice());

        if (!AudienceNetworkAds.isInitialized(context)) {
            if (DEBUG) {
                AdSettings.turnOnSDKDebugger(context);
            }

            AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(new FacebookInitializeHelper(context))
                    .initialize();
        }
    }

    public static void setUserParams(Map<String, Object> userParams, TPLoadAdapterListener loadAdapterListener) {
        if (userParams.size() > 0) {
            if (userParams.containsKey(AppKeyManager.KEY_CCPA)) {
                boolean ccpa = (boolean) userParams.get(AppKeyManager.KEY_CCPA);
                if (ccpa) {
                    AdSettings.setDataProcessingOptions(new String[]{});
                } else {
                    AdSettings.setDataProcessingOptions(new String[]{"LDU"}, 1, 1000);
                }
            } else {
                AdSettings.setDataProcessingOptions(new String[]{"LDU"}, 0, 0);
            }
            if (userParams.containsKey(AppKeyManager.KEY_COPPA)) {
                boolean coppa = (boolean) userParams.get(AppKeyManager.KEY_COPPA);
                if (coppa) {
                    loadAdapterListener.loadAdapterLoadFailed(new TPError(NETWORK_NO_FILL));
                    return;
                }
                AdSettings.setMixedAudience(coppa);
            }
        }
    }

    @Override
    public void onInitialized(AudienceNetworkAds.InitResult result) {
        Log.d(AudienceNetworkAds.TAG, result.getMessage());
    }
}
