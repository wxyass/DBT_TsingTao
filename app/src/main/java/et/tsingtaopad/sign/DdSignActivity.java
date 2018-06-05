package et.tsingtaopad.sign;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseActivity;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.net.domain.ResponseStructBean;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnDismissListener;
import et.tsingtaopad.core.view.alertview.OnItemClickListener;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.dd.ddxt.camera.XtCameraFragment;
import et.tsingtaopad.dd.ddxt.chatvie.XtChatvieFragment;
import et.tsingtaopad.dd.ddxt.checking.XtCheckIndexFragment;
import et.tsingtaopad.dd.ddxt.checking.domain.XtCheckIndexCalculateStc;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.invoicing.XtInvoicingFragment;
import et.tsingtaopad.dd.ddxt.sayhi.XtSayhiFragment;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.dd.ddxt.updata.XtShopCopyService;
import et.tsingtaopad.dd.ddxt.updata.XtUploadService;
import et.tsingtaopad.fragmentback.HandleBackUtil;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.sayhi.domain.MstTerminalInfoMStc;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class DdSignActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "XtVisitShopActivity";

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    public static final int DD_AGENCY_CHECKSELECT_SUC = 1101;//
    public static final int DD_AGENCY_CHECKSELECT_FAIL = 1102;//

    private ImageView first_img_on_sign;
    private ImageView first_img_down_sign;
    private ImageView first_img_click_sign;
    private et.tsingtaopad.view.NoScrollListView first_lv_sign;

    MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dd_sign);
        initView();
        initData();
        if (hasPermission(GlobalValues.LOCAL_PERMISSION)) {
            // 拥有了此权限,那么直接执行业务逻辑
            registerGPS();
        } else {
            // 还没有对一个权限(请求码,权限数组)这两个参数都事先定义好
            requestPermission(GlobalValues.LOCAL_CODE, GlobalValues.LOCAL_PERMISSION);
        }
    }

    // 初始化控件
    private void initView() {
        backBtn = (RelativeLayout) findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) findViewById(R.id.top_navigation_tv_title);
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        //titleTv.setOnClickListener(this);

        first_img_on_sign = (ImageView) findViewById(R.id.first_img_on_sign);
        first_img_down_sign = (ImageView) findViewById(R.id.first_img_down_sign);
        first_img_click_sign = (ImageView) findViewById(R.id.first_img_click_sign);
        first_lv_sign = (et.tsingtaopad.view.NoScrollListView) findViewById(R.id.first_lv_sign);

        first_img_click_sign.setOnClickListener(this);

    }

    // 初始化数据
    private void initData() {

        titleTv.setText("考勤管理");

        handler = new MyHandler(this);
        DdSignAdapter signAdapter = new DdSignAdapter(this,null,null);

    }




    // 按钮点击 监听
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.top_navigation_rl_back:// 返回
                DdSignActivity.this.finish();
                break;
            case R.id.top_navigation_rl_confirm://
                //大区所有的
                //confirmUplad();
                // confirmXtUplad();
                break;
            case R.id.first_img_click_sign://
                //发送签到请求
                sendSignHttp();

                break;

            default:
                break;
        }
    }

    private void sendSignHttp() {

        String content = "{" +
                "areaid:'" + PrefUtils.getString(this, "departmentid", "") + "'," +
                "longitude:'" + latitude + "'," +
                "latitude:'" + latitude + "'," +
                "signtime:'" + DateUtil.getDateTimeStr(8) + "'," +
                "creuser:'" + PrefUtils.getString(this, "userid", "") + "'" +
                "}";
        ceshiHttp("opt_get_repair_ter_check",  content);
    }

    /**
     * 发送签到请求
     *
     * @param optcode 请求码
     * @param content 请求json
     */
    void ceshiHttp(final String optcode, String content) {

        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(this);
        requestHeadStc.setOptcode(PropertiesUtil.getProperties(optcode));
        RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, content);
        // 压缩请求数据
        String jsonZip = HttpParseJson.parseRequestJson(reqObj);

        RestClient.builder()
                .url(HttpUrl.IP_END)
                .params("data", jsonZip)
                .loader(DdSignActivity.this)// 滚动条
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        String json = HttpParseJson.parseJsonResToString(response);

                        if ("".equals(json) || json == null) {
                            Toast.makeText(DdSignActivity.this, "后台成功接收,但返回的数据为null", Toast.LENGTH_SHORT).show();
                        } else {
                            ResponseStructBean resObj = new ResponseStructBean();
                            resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                            // 保存登录信息
                            if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                                // 保存信息
                                String formjson = resObj.getResBody().getContent();
                                // parseTableJson(formjson);
                                initData();

                            } else {
                                Toast.makeText(DdSignActivity.this, resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(DdSignActivity.this, msg, Toast.LENGTH_SHORT).show();
                        /*Message msg1 = new Message();
                        msg1.what = FirstFragment.SYNC_CLOSE;//
                        handler.sendMessage(msg1);*/
                        //initData();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(DdSignActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        /*Message msg2 = new Message();
                        msg2.what = FirstFragment.SYNC_CLOSE;//
                        handler.sendMessage(msg2);*/
                        //initData();
                    }
                })
                .builde()
                .post();
    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<DdSignActivity> fragmentRef;

        public MyHandler(DdSignActivity fragment) {
            fragmentRef = new SoftReference<DdSignActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DdSignActivity fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }

            // 处理UI 变化
            switch (msg.what) {
                case DD_AGENCY_CHECKSELECT_SUC:
                    //fragment.showAddProSuc(products, agency);
                    break;
                case DD_AGENCY_CHECKSELECT_FAIL: // 督导输入数据后
                    //fragment.showAdapter();
                    break;
            }
        }
    }







    // 原生经纬度 处理 --------------------------------------------------------

    private double longitude;// 经度
    private double latitude;// 维度
    public LocationManager lm;

    // 拥有定位权限 开启注册定位
    @Override
    public void doLocation() {
        registerGPS();
    }

    private void registerGPS() {

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        //为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        //获取位置信息
        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = lm.getLastKnownLocation(bestProvider);
        //        Location location= lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateView(location);
        //监听状态
        lm.addGpsStatusListener(listener);
        //绑定监听，有4个参数
        //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        //参数2，位置信息更新周期，单位毫秒
        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        //参数4，监听
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        //注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        //        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
    }

    //位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
            updateView(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }
    };

    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG, "卫星状态改变");
                    //获取当前状态
                    /*GpsStatus gpsStatus=lm.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    System.out.println("搜索到："+count+"颗卫星");
                    tv_gps.setText("搜索到："+count+"颗卫星");*/
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    break;
            }
        }

        ;
    };

    /**
     * 实时更新文本内容
     *
     * @param location
     */
    private void updateView(Location location) {
        if (location != null) {
            // 经度
            longitude = location.getLongitude();
            // 纬度
            latitude = location.getLatitude();
        } else {
            //清空EditText对象
            //editText.getEditableText().clear();
        }
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(locationListener);
    }

}
