package com.example.biezhi.biezhivideo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.biezhi.biezhivideo.HelpClass.BitMapCut;
import com.example.biezhi.biezhivideo.HelpClass.NetInfo;


public class Login extends AppCompatActivity {

    NetInfo netInfo = new NetInfo();
    BitMapCut bitMapCut = new BitMapCut();
    Bitmap loginBit;
    Resources resources = getResources();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
    }

    /**
     * 初始化app
     */
    private void initApp() throws InterruptedException {
        //欢迎界面需要处理的问题
        //1、获取当前手机的屏幕大小
        //2、获取当前手机的网络状态
        //3、获取当前手机的唯一标识符
        //4、获取当前手机的首登状态,检查目录下是否有当前版本的文件夹
        //5、获取设备的其他信息
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        if (getNetInfo().equals("mobile")) {
            Toast.makeText(this, "当前是3G/4G网络，请注意流量哦o(>﹏<)o", Toast.LENGTH_SHORT).show();
            loginBit = bitMapCut.readImageByUrl(resources.getString(R.string.initImageUrl));
        } else if (getNetInfo().equals("")) {
            AlertDialog netWorkInfo = new AlertDialog.Builder(this).create();
            netWorkInfo.setTitle("网络开小差了...");
            netWorkInfo.setMessage("无网络连接，快去打开WIFI吧~");
            netWorkInfo.setButton("好", noNetListener);
            netWorkInfo.show();
        } else {
            loginBit = bitMapCut.readImageByUrl(resources.getString(R.string.initImageUrl));
        }
        String phoneID = getPhoneID();
        String SoftwareVersion = getPhoneSoftwareVersion();
        //裁剪bitmap

        loadImage(screenWidth, screenHeight);

    }

    //网络状态弹窗点击listener
    DialogInterface.OnClickListener noNetListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    };

    private String getPhoneID() {
        String phoneID = "";
        //使用IMEI作为唯一标识码
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        phoneID = tm.getDeviceId();
        return phoneID;
    }

    private String getPhoneSoftwareVersion() {
        //获取版本信息
        String phoneSoftwareVersion = "";
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        phoneSoftwareVersion = tm.getDeviceSoftwareVersion();
        return phoneSoftwareVersion;
    }

    private String getNetInfo() {
        return netInfo.checkNetWork(getApplicationContext());
    }

    private void loadImage(int screenWidth, int screenHeight) {
        //判断客户端屏幕大小
        //选择合适的图片。。。。and so on
        ImageView loginImage = (ImageView) findViewById(R.id.imageLogin);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.initpic);
        loginImage.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
