package vincent.com.appwathcher;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vincent.com.appwathcher.util.Util;

public class MainActivity extends AppCompatActivity {

    public TextView showName, showRam,show_watcher;
    public Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showName = (TextView) findViewById(R.id.show_name);
        showRam = (TextView) findViewById(R.id.show_ram);
        show_watcher = (TextView) findViewById(R.id.show_watcher);
        util=new Util();

        List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                showName.append("<User software--> \t" + tmpInfo.appName + ":" + tmpInfo.versionName + ":" + tmpInfo.packageName + "\n");
            }
        }
        timer.schedule(task, 1000, 2000);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

                ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();

                activityManager.getMemoryInfo(info);
                showRam.setText("");
                showRam.append("------------------------------------------------\n");
                showRam.append("系统剩余内存:" + (info.availMem >> 10) + "k\n");
                showRam.append("Native中堆的内存大小:" + (Debug.getNativeHeapAllocatedSize() >> 10) + "k\n");
                showRam.append("------------------------------------------------\n");

                try {
                    StringBuilder sb;
                    sb=util.execCommand("su 0 dumpsys meminfo ");
                    show_watcher.append(sb);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.handleMessage(msg);
        }
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
}

class AppInfo {

    String appName = "";
    String packageName = "";
    String versionName = "";
    int versionCode = 0;
    Drawable appIcon = null;

    public void print() {
        Log.v("app", "Name:" + appName + " Package:" + packageName);
        Log.v("app", "Name:" + appName + " versionName:" + versionName);
        Log.v("app", "Name:" + appName + " versionCode:" + versionCode);
    }

}
