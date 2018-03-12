package et.tsingtaopad.main.operation.agencystorage;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.InitConstValues;
import et.tsingtaopad.main.operation.agencystorage.domain.AgencystorageStc;
import et.tsingtaopad.util.DialogUtil;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class AgencyStorageCoreFragment extends BaseCoreFragment implements View.OnClickListener , AdapterView.OnItemSelectedListener{

    public static final String TAG = "ShopVisitCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private AgencyStorageService service;

    private AlertDialog searchDialog;
    private Button backBt,btTimeA,btTimeB;

    private String cureentDate;
    private String selectDate;
    private String aday;
    private Calendar calendar;
    private int yearr;
    private int month;
    private int day;
    private String datecureent;
    private String datecureents;
    private String datecureentx;

    private String dateselect;
    private String dateselects;
    private String dateselectx;


    private TextView tvTime;
    private TextView creenttvtTime;

    private ImageButton searchIb;
    private Spinner agencySelectSp;
    private ListView agencyStorageLv;

    Boolean flag = false;

    public AgencyStorageCoreFragment() {
    }

    public static AgencyStorageCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        AgencyStorageCoreFragment fragment = new AgencyStorageCoreFragment();
        return fragment;
    }

    public static AgencyStorageCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        AgencyStorageCoreFragment fragment = new AgencyStorageCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // 在这个方法中 只处理从上个Fragment传递过来的数据,若没数据传递过来,可删除该方法
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            //termname = bundle.getString(TERM_NAME);
            //termkey = bundle.getString(TERM_KEY);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DbtLog.logUtils(TAG, "onActivityCreated");

        // 设置横屏
        getSupportDelegate().getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    // 只用来初始化控件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.visit_agencystorage, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (AppCompatTextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);

        //绑定界面组件
        searchDialog = DialogUtil.progressDialog(getContext(), R.string.dialog_msg_search);
        btTimeA = (Button) view.findViewById(R.id.agencystorage_btn_time);//经销商库存选时间
        btTimeB = (Button) view.findViewById(R.id.agencystorage_btn_timeb);//经销商库存选时间
        tvTime = (TextView) view.findViewById(R.id.listview_tv_title_selecttime);
        creenttvtTime = (TextView) view.findViewById(R.id.listview_tv_title_creenttime);//当前时间
        //查询按钮
        searchIb = (ImageButton) view.findViewById(R.id.agencystorage_ib_search);
        agencySelectSp = (Spinner) view.findViewById(R.id.agencystorage_sp_selectagency);
        agencyStorageLv = (ListView) view.findViewById(R.id.agencystorage_lv_agencystorage);

        //绑定事件
        btTimeA.setOnClickListener(this);
        btTimeB.setOnClickListener(this);
        searchIb.setOnClickListener(this);
        agencySelectSp.setOnItemSelectedListener(this);

    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        titleTv.setText(R.string.agencystorage_banner_title);

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG,"onSupportVisible");

        // 每次页面出现后都重新加载数据
        initData();
    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG,"initData");


        // 数据
        calendar = Calendar.getInstance();
        yearr = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            aday = "0" + day;
        } else {
            aday = Integer.toString(day);
        }
        datecureent=  (Integer.toString(yearr)  + String.format("%02d", month + 1)  + aday);//没转变格式之前de20140908
        dateselect =  (Integer.toString(yearr)  + String.format("%02d", month + 1)  + aday);//没转变格式之前de20140908
        datecureents=  (Integer.toString(yearr)  + String.format("%02d", month + 1)  + aday + "000000");//没转变格式之前de20140908000000 系统时间
        datecureentx=  (Integer.toString(yearr)  + String.format("%02d", month + 1)  + aday + "235959");//没转变格式之前de20140908000000系统
        dateselects = (Integer.toString(yearr) + String.format("%02d", month + 1) + aday + "000000");//选择时间
        dateselectx = (Integer.toString(yearr) + String.format("%02d", month + 1) + aday + "235959");//选择
        cureentDate = (Integer.toString(yearr) + "-" + String.format("%02d", month + 1) + "-" + aday);

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");//把选择控件也设置成系统时间
        Date date = calendar.getTime();
        String dateStr = sDateFormat.format(date);
        btTimeA.setText(dateStr);//选择
        btTimeB.setText(dateStr);//选择
        tvTime.setText(dateStr);//选择
        creenttvtTime.setText(cureentDate);

        service = new AgencyStorageService(getContext());
        //界面title显示内容
        titleTv.setText(R.string.agencystorage_banner_title);
        //绑定Spinner数据
        if (CheckUtil.IsEmpty(GlobalValues.agencyVisitLst)) {
            InitConstValues initService = new InitConstValues(getContext());
            initService.initVisitAgencyPro();
        }
        SpinnerKeyValueAdapter selectSpAdapter = new SpinnerKeyValueAdapter
                (getContext(), GlobalValues.agencyVisitLst, new String[]{"key", "value"}, null);
        agencySelectSp.setAdapter(selectSpAdapter);

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            // 设置竖屏
            getSupportDelegate().getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            pop();
            //选择时间a
        }else if(i == R.id.agencystorage_btn_time){
            DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    yearr = year;
                    month = monthOfYear;
                    day = dayOfMonth;
                    if (dayOfMonth < 10) {
                        aday = "0" + dayOfMonth;
                    } else {
                        aday = Integer.toString(dayOfMonth);
                    }
                    dateselect = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday);
                    dateselects = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "000000");
                    dateselectx = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "235959");
                    selectDate = (Integer.toString(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + aday);
                    btTimeA.setText(selectDate);
                    tvTime.setText(selectDate);
                }
            }, yearr, month, day);
            if (!dateDialog.isShowing()) {
                dateDialog.show();
            }
            //选择时间b
        }else if(i == R.id.agencystorage_btn_timeb){
            DatePickerDialog dateDialog1 = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    yearr = year;
                    month = monthOfYear;
                    day = dayOfMonth;
                    if (dayOfMonth < 10) {
                        aday = "0" + dayOfMonth;
                    } else {
                        aday = Integer.toString(dayOfMonth);
                    }
                    datecureent = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday);
                    datecureents = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "000000");
                    datecureentx = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "235959");
                    cureentDate = (Integer.toString(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + aday);
                    btTimeB.setText(cureentDate);
                    creenttvtTime.setText(cureentDate);
                }
            }, yearr, month, day);
            if (!dateDialog1.isShowing()) {
                dateDialog1.show();
            }

            //界面数据查询  注意这个查询是查询本地数据库,不请求网络
        }else if(i == R.id.agencystorage_ib_search){
            if(agencySelectSp.getTag() == null) {
                ViewUtil.sendMsg(getContext(), R.string.indexstatus_msg_update);

            } else {
                String agencyKey=agencySelectSp.getTag().toString();
                if (searchDialog !=null && !searchDialog.isShowing()) {
                    searchDialog.show();

                    //B时间
                    boolean isBSameDate=true;
                    String aDatecureent=datecureent;
                    //是否存在经销商拜访
                    if(!service.isExistAgencyVisit(agencyKey, datecureent)){
                        // 获取小于此日期的最后一次拜访
                        String newVisitDate=service.getMaxVisitDate(agencyKey, datecureent);
                        if(StringUtils.isNotBlank(newVisitDate)){
                            isBSameDate=false;
                            datecureents=newVisitDate+"000000";
                            datecureentx=newVisitDate+"235959";
                            aDatecureent=newVisitDate;
                        }
                    }

                    //A时间
                    boolean isASameDate=true;
                    String bBateselect=dateselect;
                    //是否存在经销商拜访
                    if(!service.isExistAgencyVisit(agencyKey, dateselect)){
                        //获取小于此日期的最后一次拜访
                        String newVisitDate=service.getMaxVisitDate(agencyKey, dateselect);
                        if(StringUtils.isNotBlank(newVisitDate)){
                            isASameDate=false;
                            dateselects=newVisitDate+"000000";
                            dateselectx=newVisitDate+"235959";
                            bBateselect=newVisitDate;
                        }
                    }
                    List<AgencystorageStc> storLst = service
                            .getAgencyStorage( datecureents,datecureentx, agencyKey, aDatecureent,dateselects,dateselectx,agencyKey,bBateselect);
                    AgencyStorageAdapter storAdapter = new AgencyStorageAdapter(getContext(), storLst,isASameDate,isBSameDate);
                    agencyStorageLv.setAdapter(storAdapter);
                    searchDialog.dismiss();
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Spinner sp = (Spinner)arg0;
        if (arg1 != null) {
            sp.setTag(((TextView)arg1).getHint());
        } else {
            sp.setTag("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public boolean onBackPressedSupport() {

        // 设置横屏
        getSupportDelegate().getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 默认flase，继续向上传递
        return super.onBackPressedSupport();
    }
}
