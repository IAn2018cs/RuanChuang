package com.ruanchuang.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.ruanchuang.activity.designlibrary.R;
import com.runachuang.massorganizationsignin.utils.LocationDB;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by joho on 2016/5/27.
 */
public class SignActivity extends AppCompatActivity {
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private TextView tv_location;

    //用户姓名
    private String mName;
    private String username;
    private SharedPreferences sp;

    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        tv_location = (TextView) findViewById(R.id.tv_location);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        initLocation();
        mLocationClient.registerLocationListener( myListener );    //注册监听函数

        sp = getSharedPreferences("user", MODE_PRIVATE);
        mName = sp.getString("name", "");
        username = sp.getString("username","");

        //判断是否有网络
        //获取网络状态
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo == null) {
            Toast.makeText(SignActivity.this, "定位失败，请检查网络", Toast.LENGTH_SHORT).show();
        }

        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            StringBuffer tv_sb = new StringBuffer(256);
            sb.append("time : ");
            tv_sb.append("签到时间 : ");

            sb.append(location.getTime());
            tv_sb.append(location.getTime());

            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度

                sb.append("\naddr : ");
                tv_sb.append("\n位置 : ");
                sb.append(location.getAddrStr());
                tv_sb.append(location.getAddrStr());

                sb.append("\ndescribe : ");
                tv_sb.append("\n定位模式 : ");
                sb.append("gps定位成功");
                tv_sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                tv_sb.append("\n位置 : ");
                sb.append(location.getAddrStr());
                tv_sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                tv_sb.append("\n定位模式 : ");
                sb.append("网络定位成功");
                tv_sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                tv_sb.append("\n定位模式 : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                tv_sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                tv_sb.append("\n定位模式 : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                tv_sb.append("服务端网络定位失败，请重新尝试");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                tv_sb.append("\n定位模式 : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
                tv_sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                tv_sb.append("\n定位模式 : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                tv_sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            tv_sb.append("\n详细位置 : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            tv_sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                tv_sb.append("\n\n\n附近位置 : ");
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    tv_sb.append("\n" + p.getName());
                }
            }
            Log.i("SignActivity", sb.toString());
            tv_location.setText(tv_sb.toString());

            /*location.getTime(); // 时间
            location.getLocType(); //状态码
            location.getAddrStr(); //位置
            location.getLocationDescribe(); //位置描述*/

            //获取详细地址信息
            String addrStr = location.getAddrStr() + location.getLocationDescribe();
            //获取定位时间
            String time = location.getTime();
            String address = mName + "于" + time + "在" + addrStr + "签到";
            Toast.makeText(SignActivity.this, address, Toast.LENGTH_LONG).show();

            LocationDB locationDB = new LocationDB();
            locationDB.setLocation(address);
            locationDB.setUserName(username);
            locationDB.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if(e==null){
                        Log.i("bmob","创建数据成功：" +username+"-----"+ objectId);
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }
}