package com.reactlibrary;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.pgyersdk.Pgyer;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyerFeedbackManager;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class RNPgyerBridgeModule extends ReactContextBaseJavaModule {
    ReactApplicationContext reactContext;
    int progress;

    public RNPgyerBridgeModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Nonnull
    @Override
    public String getName() {
        return "RNPgyerBridge";
    }

    private ReadableMap baseConfig;

    /**
     * 初始化,安卓的
     *
     * @param config
     */
    @ReactMethod
    public void initWithConfig(ReadableMap config, Promise promise) {
        String appId = config.getString("appId");
        if (appId == null) promise.reject("not appId", "需要配置appId");
        this.baseConfig = config;
        Pgyer.setAppId(appId);
        PgyCrashManager.register();
        getFeedbackBuild(config).builder().register();
    }


    /**
     * 显示反馈页面
     */
    @ReactMethod
    public void showFeedbackView() {
        getFeedbackBuild(this.baseConfig)
                .setShakeInvoke(false) //设置取消摇一摇
                .builder()
                .invoke();
    }

    @ReactMethod
    public void getUpdateInfo(final Promise promise) {
        new PgyUpdateManager.Builder()
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        promise.resolve(null);
                    }

                    @Override
                    public void onUpdateAvailable(AppBean appBean) {
                        //有更新回调此方法
                        Log.d("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());

                        WritableMap info = Arguments.createMap();
                        info.putString("downloadURL", appBean.getDownloadURL());
                        info.putString("releaseNote", appBean.getReleaseNote());
                        info.putString("versionCode", appBean.getVersionCode());
                        info.putString("versionName", appBean.getVersionName());
                        promise.resolve(info);
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        //更新拒绝（应用被下架，过期，不在安装有效期，下载次数用尽）以及无网络情况会调用此接口
                        Log.e("pgyer", "check update failed ", e);
                        promise.reject(e);
                    }
                })
                .register();
    }

    /**
     * 检查更新
     */
    @ReactMethod
    public void checkUpdate(final ReadableMap config, final Promise promise) {
        PgyUpdateManager.Builder builder = new PgyUpdateManager.Builder();

        //简单模式下直接调用默认的更新


        if (config.hasKey("forced")) {
            builder.setForced(config.getBoolean("forced"));
        }

        if (config.hasKey("userCanRetry")) {
            builder.setUserCanRetry(config.getBoolean("userCanRetry"));
        }

        if (config.hasKey("deleteHistoryApk")) {
            builder.setDeleteHistroyApk(config.getBoolean("deleteHistoryApk"));
        }
        final boolean listerProgress = config.hasKey("onProgress") && config.getBoolean("onProgress");

        if (!listerProgress) {
            builder.register();
            promise.resolve(null);
            return;
        }
        // 判断是否监听进度


        builder.setUpdateManagerListener(new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                //没有更新是回调此方法
                Log.d("pgyer", "there is no new version");
                promise.resolve(null);
            }

            @Override
            public void onUpdateAvailable(AppBean appBean) {
                //有更新回调此方法
                Log.d("pgyer", "there is new version can update"
                        + "new versionCode is " + appBean.getVersionCode());
                //调用以下方法，DownloadFileListener 才有效；
                //如果完全使用自己的下载方法，不需要设置DownloadFileListener
                PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
//                WritableMap info = Arguments.createMap();
//                info.putString("downloadURL", appBean.getDownloadURL());
//                info.putString("releaseNote", appBean.getReleaseNote());
//                info.putString("versionCode", appBean.getVersionCode());
//                info.putString("versionName", appBean.getVersionName());
//                if (!listerProgress) promise.resolve(info);
            }

            @Override
            public void checkUpdateFailed(Exception e) {
                //更新检测失败回调
                //更新拒绝（应用被下架，过期，不在安装有效期，下载次数用尽）以及无网络情况会调用此接口
                promise.reject(e);
            }
        });

        if (listerProgress) {

            builder.setDownloadFileListener(new DownloadFileListener() {
                @Override
                public void downloadFailed() {
                    //下载失败
                    Log.e("pgyer", "download apk failed");
                    promise.reject("pgyer", "download apk failed");
                }

                @Override
                public void downloadSuccessful(Uri uri) {
                    Log.e("pgyer", "download apk Successful" + uri.toString());
                    // 使用蒲公英提供的安装方法提示用户 安装apk
                    if (config.hasKey("autoInstall") && config.getBoolean("autoInstall")) {
                        PgyUpdateManager.installApk(uri);
                    }
                    WritableMap arg = Arguments.createMap();
                    arg.putString("path", uri.toString());
                    promise.resolve(arg);
                }

                @Override
                public void onProgressUpdate(Integer... integers) {
                    if (progress == integers[0]) return;
                    progress = integers[0];
                    Log.e("pgyer", "update download apk progress" + progress);
                    reactContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("apkDownloadProgress", progress);
                }
            });
        }
        builder.register();
    }


    /**
     * 下载apk
     */
    @ReactMethod
    public void downLoadApk(String url) {
        PgyUpdateManager.downLoadApk(url);
    }

    /**
     * 安装apk
     */
    @ReactMethod
    public void installApk(String uri) {
        PgyUpdateManager.installApk(Uri.parse(uri));
    }

    /**
     * 显示反馈页面，带自定义参数
     *
     * @param config
     */
    @ReactMethod
    public void showFeedbackView(ReadableMap config) {
        PgyerFeedbackManager.PgyerFeedbackBuilder builder = getFeedbackBuild(this.baseConfig)
                .setShakeInvoke(false);//设置取消摇一摇

        //迭代map,设置自定义值
        ReadableMapKeySetIterator iterator = config.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            builder.setMoreParam(key, config.getString(key));
        }
        //手动弹出
        builder.builder()
                .invoke();
    }

    /**
     * 抛出异常
     *
     * @param params
     */
    @ReactMethod
    public void reportException(ReadableMap params) {
        Exception exception;
        if (params.hasKey("name") && params.hasKey("reason") && params.hasKey("userInfo")) {
            exception = new Exception(params.getString("name"), new Throwable(params.getString("reason"), new Throwable(params.getMap("userInfo").toHashMap().toString())));
        } else if (params.hasKey("name") && params.hasKey("reason")) {
            exception = new Exception(params.getString("name"), new Throwable(params.getString("reason")));
        } else {
            exception = new Exception(params.getString("name"));
        }
        PgyCrashManager.reportCaughtException(exception);
    }


    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();
        PackageManager packageManager = this.reactContext.getPackageManager();
        String packageName = this.reactContext.getPackageName();
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            constants.put("version", info.versionName);
            constants.put("build", info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return constants;
    }

    private PgyerFeedbackManager.PgyerFeedbackBuilder getFeedbackBuild(ReadableMap config) {
        PgyerFeedbackManager.PgyerFeedbackBuilder builder = new PgyerFeedbackManager.PgyerFeedbackBuilder();
        if (config.hasKey("shakingThreshold")) {
            builder.setShakingThreshold(config.getInt("shakingThreshold"));
        }
        if (config.hasKey("type")) {
            int type = config.getInt("type");
            if (type == 0) {
                builder.setDisplayType(PgyerFeedbackManager.TYPE.DIALOG_TYPE);
            } else {
                builder.setDisplayType(PgyerFeedbackManager.TYPE.ACTIVITY_TYPE);
            }
        }
        if (config.hasKey("colorDialogTitle")) {
            builder.setColorDialogTitle(config.getString("colorDialogTitle"));
        }
        if (config.hasKey("colorTitleBg")) {
            builder.setColorTitleBg(config.getString("colorTitleBg"));
        }
        if (config.hasKey("barBackgroundColor")) {
            builder.setBarBackgroundColor(config.getString("barBackgroundColor"));
        }
        if (config.hasKey("barButtonPressedColor")) {
            builder.setBarButtonPressedColor(config.getString("barButtonPressedColor"));
        }
        if (config.hasKey("colorPickerBackgroundColor")) {
            builder.setColorPickerBackgroundColor(config.getString("colorPickerBackgroundColor"));
        }
        return builder;
    }
}
