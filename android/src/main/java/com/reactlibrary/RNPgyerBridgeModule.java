
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.pgyersdk.crash.PgyCrashManager;

public class RNPgyerBridgeModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNPgyerBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNPgyerBridge";
    }

    @ReactMethod
    public void reportException(ReadableMap msg) {
        String name = msg.getString("name");
        String reason = msg.getString("reason");
        ReadableMap userInfo = msg.getMap("userInfo");
        PgyCrashManager.reportCaughtException(new Exception(name, new Throwable(reason, new Throwable(userInfo.toString()))));
    }
}