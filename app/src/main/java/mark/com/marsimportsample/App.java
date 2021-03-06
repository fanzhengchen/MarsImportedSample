package mark.com.marsimportsample;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

import com.tencent.mars.Mars;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

import com.xinguang.MainServiceNative;
import com.xinguang.MainServiceProxy;

/**
 * Created by fanzhengchen on 4/7/17.
 */

public class App extends Application {

    static {
        Mars.loadDefaultMarsLibrary();

    }


    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        openXLog();
        Log.i("fuck", "fuck open");

        MainServiceProxy.getInstance()
                .init(this, Looper.getMainLooper());

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.appenderClose();
        Intent intent = new Intent(this, MainServiceNative.class);
        stopService(intent);
    }

    /**
     * open Xlog init with log file path
     */
    public static void openXLog() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            configureXLog();

        } else {
            int pid = android.os.Process.myPid();
            String processName = null;
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    processName = appProcess.processName;
                    break;
                }
            }
            configureXLog();
        }
        Log.setLogImp(new Xlog());
    }

    private static void configureXLog() {
        final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String logPath = SDCARD + "/marsrider";
        final String logFileName = "marslogfile";
        int logLevel = BuildConfig.DEBUG ? Xlog.LEVEL_VERBOSE : Xlog.LEVEL_INFO;
        Xlog.open(true, logLevel, Xlog.AppednerModeAsync, "", logPath, logFileName);
        Xlog.setConsoleLogOpen(BuildConfig.DEBUG);

    }
}
