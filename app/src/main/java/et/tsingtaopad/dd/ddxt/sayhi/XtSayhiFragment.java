package et.tsingtaopad.dd.ddxt.sayhi;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.AlertKeyValueAdapter;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.DbtUtils;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnDismissListener;
import et.tsingtaopad.core.view.alertview.OnItemClickListener;
import et.tsingtaopad.db.table.MstPlanforuserM;
import et.tsingtaopad.db.table.MstProductM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.db.table.PadPlantempcheckM;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.invoicing.XtInvoicingFragment;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.view.DdSlideSwitch;
import et.tsingtaopad.view.SlideSwitch;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtSayhiFragment extends XtBaseVisitFragment implements View.OnClickListener, DdSlideSwitch.OnLongSwitchChangedListener,OnItemClickListener, OnDismissListener {

    private final String TAG = "XtSayhiFragment";

    private et.tsingtaopad.view.DdSlideSwitch xttermstatusSw;
    private et.tsingtaopad.view.DdSlideSwitch xtvisitstatusSw;
    private Button xttextBtn;
    private Button xttimeBtn;
    private et.tsingtaopad.view.DdSlideSwitch xtwopindianzhaoSw;
    private CheckBox xtvieCb;
    private CheckBox xtmineCb;
    private CheckBox xtvieprotocolCb;
    private CheckBox xtmineprotocolCb;
    private TextView xttermcode;
    private TextView xttermroude;
    private RelativeLayout xttermroudeRl;
    private EditText xttermname;
    private TextView xttermlv;
    private RelativeLayout xttermlvRl;
    private TextView xtprovince;
    private TextView xttermcity;
    private TextView xttermcountry;
    private EditText xttermaddress;
    private EditText xttermcontact;
    private EditText xttermphone;
    private EditText xttermcycle;
    private EditText xttermsequence;
    private EditText xttermhvolume;
    private EditText xttermmvolume;
    private EditText xttermpvolume;
    private EditText xttermlvolume;
    private EditText xttermtvolume;
    private TextView xttermarea;
    private RelativeLayout xttermareaRl;
    private TextView xttermsellchannel;
    private RelativeLayout xttermsellchannelRl;
    private TextView xttermtmainchannel;
    private RelativeLayout xttermtmainchannelRl;
    private TextView xttermminorchannel;
    private RelativeLayout xttermminorchannelRl;
    private TextView xttermpersion;
    private RelativeLayout xttermpersionRl;
    private Button xtnextBtn;

    private XtSayhiService xtSayhiService;
    private MstTerminalinfoMTemp termInfoTemp;
    private MstVisitMTemp visitMTemp;

    private String aday;
    private Calendar calendar;
    private int yearr;
    private int month;
    private int day;
    private String ifminedate;

    private AlertView mAlertViewExt;//窗口拓展例子

    private List<MstRouteM> mstRouteList = new ArrayList<>();
    private List<KvStc> dataDicByTermLevelLst = new ArrayList<>();
    private List<KvStc> dataDicByAreaTypeLst = new ArrayList<>();
    private List<KvStc> sellchanneldataLst = new ArrayList<>();
    private List<KvStc> sellchannelLst = new ArrayList<>();
    private List<KvStc> mainchannelLst = new ArrayList<>();
    private List<KvStc> minorchannelLst = new ArrayList<>();

    private ProgressDialog progressDialog;
    public static final int FINISH_SUC = 11;
    public static final int NOT_TERMSTATUS = 12;
    public static final int DATA_ARROR = 13;
    MyHandler handler;




    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<XtSayhiFragment> fragmentRef;

        public MyHandler(XtSayhiFragment fragment) {
            fragmentRef = new SoftReference<XtSayhiFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            XtSayhiFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }


            // 处理UI 变化
            switch (msg.what) {
                case FINISH_SUC:
                    fragment.closeProgressSuc();
                    break;
                case NOT_TERMSTATUS:
                    fragment.closeXtTermstatusSw();
                    break;
                case DATA_ARROR:
                    int res = (int)msg.obj;
                    fragment.showErrorMsg(res);

                    break;
            }
        }
    }

    // 展示错误信息
    private void showErrorMsg(int res) {
        Toast.makeText(getActivity(),getString(res),Toast.LENGTH_SHORT).show();
        //requestFocus(res);
    }

    /**
     * 由错误提示定位焦点
     *
     * @param msgId
     */
    private void requestFocus(int msgId) {
        DbtLog.logUtils(TAG, "requestFocus()");
        switch (msgId) {
            case R.string.termadd_msg_invaltermname:
                xttermname.requestFocus();
                break;

            case R.string.termadd_msg_invaladdress:
                xttermaddress.requestFocus();
                break;

            case R.string.termadd_msg_invalcontact:
                xttermcontact.requestFocus();
                break;
            case R.string.termadd_msg_invalsequence:
                xttermsequence.requestFocus();
                break;

            default:
                break;
        }
    }

    // 取消终端失效
    private void closeXtTermstatusSw() {
        MotionEvent event = MotionEvent.obtain(
                SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, xttermstatusSw.getLeft(),
                xttermstatusSw.getTop(), xttermstatusSw.getId());
        xttermstatusSw.onTouchEvent(event);
    }

    /**
     * 添加产品成功 UI
     */
    public void closeProgressSuc() {
        progressDialog.dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_sayhi, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

        xttermstatusSw = (et.tsingtaopad.view.DdSlideSwitch) view.findViewById(R.id.xt_sayhi_sw_termstatus);
        xtvisitstatusSw = (et.tsingtaopad.view.DdSlideSwitch) view.findViewById(R.id.xt_sayhi_sw_visitstatus);

        xtwopindianzhaoSw = (et.tsingtaopad.view.DdSlideSwitch) view.findViewById(R.id.xt_sayhi_sw_wopindianzhao);
        xttextBtn = (Button) view.findViewById(R.id.xt_sayhi_btn_text);
        xttimeBtn = (Button) view.findViewById(R.id.xt_sayhi_btn_time);

        xtvieCb = (CheckBox) view.findViewById(R.id.xt_sayhi_cb_vie);
        xtmineCb = (CheckBox) view.findViewById(R.id.xt_sayhi_cb_mine);

        xtvieprotocolCb = (CheckBox) view.findViewById(R.id.xt_sayhi_cb_vieprotocol);
        xtmineprotocolCb = (CheckBox) view.findViewById(R.id.xt_sayhi_cb_mineprotocol);

        xttermname = (EditText) view.findViewById(R.id.xtbf_sayhi_termname);
        xttermcode = (TextView) view.findViewById(R.id.xtbf_sayhi_termcode);
        xttermroudeRl = (RelativeLayout) view.findViewById(R.id.xt_sayhi_rl_termroude);
        xttermroude = (TextView) view.findViewById(R.id.xtbf_sayhi_termroude);

        xttermlv = (TextView) view.findViewById(R.id.xtbf_sayhi_termlv);
        xttermlvRl = (RelativeLayout) view.findViewById(R.id.xtbf_sayhi_rl_termlv);
        xtprovince = (TextView) view.findViewById(R.id.xtbf_sayhi_termprovince);
        xttermcity = (TextView) view.findViewById(R.id.xtbf_sayhi_termcity);
        xttermcountry = (TextView) view.findViewById(R.id.xtbf_sayhi_termcountry);
        xttermaddress = (EditText) view.findViewById(R.id.xtbf_sayhi_termaddress);
        xttermcontact = (EditText) view.findViewById(R.id.xtbf_sayhi_termcontact);
        xttermphone = (EditText) view.findViewById(R.id.xtbf_sayhi_termphone);
        xttermcycle = (EditText) view.findViewById(R.id.xtbf_sayhi_termcycle);
        xttermsequence = (EditText) view.findViewById(R.id.xtbf_sayhi_termsequence);
        xttermhvolume = (EditText) view.findViewById(R.id.xtbf_sayhi_termhvolume);
        xttermmvolume = (EditText) view.findViewById(R.id.xtbf_sayhi_termmvolume);
        xttermpvolume = (EditText) view.findViewById(R.id.xtbf_sayhi_termpvolume);
        xttermlvolume = (EditText) view.findViewById(R.id.xtbf_sayhi_termlvolume);
        xttermtvolume = (EditText) view.findViewById(R.id.xtbf_sayhi_termtvolume);
        xttermarea = (TextView) view.findViewById(R.id.xtbf_sayhi_termarea);
        xttermareaRl = (RelativeLayout) view.findViewById(R.id.xtbf_sayhi_rl_termarea);
        xttermsellchannel = (TextView) view.findViewById(R.id.xtbf_sayhi_termsellchannel);
        xttermsellchannelRl = (RelativeLayout) view.findViewById(R.id.xtbf_sayhi_rl_termsellchannel);
        xttermtmainchannel = (TextView) view.findViewById(R.id.xtbf_sayhi_termtmainchannel);
        xttermtmainchannelRl = (RelativeLayout) view.findViewById(R.id.xtbf_sayhi_rl_termtmainchannel);
        xttermminorchannel = (TextView) view.findViewById(R.id.xtbf_sayhi_termminorchannel);
        xttermminorchannelRl = (RelativeLayout) view.findViewById(R.id.xtbf_sayhi_rl_termminorchannel);
        xttermpersion = (TextView) view.findViewById(R.id.xtbf_sayhi_termpersion);
        xttermpersionRl = (RelativeLayout) view.findViewById(R.id.xtbf_sayhi_rl_termpersion);
        xtnextBtn = (Button) view.findViewById(R.id.xtbf_sayhi_bt_next);


        xttermstatusSw.setOnLongSwitchChangedListener(this);
        xtwopindianzhaoSw.setOnLongSwitchChangedListener(this);

        xttermlvRl.setOnClickListener(this);
        xttermroudeRl.setOnClickListener(this);
        xttermareaRl.setOnClickListener(this);
        xttermsellchannelRl.setOnClickListener(this);
        xttermtmainchannelRl.setOnClickListener(this);
        xttermminorchannelRl.setOnClickListener(this);
        xttermpersionRl.setOnClickListener(this);

        xttimeBtn.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);
        xtSayhiService = new XtSayhiService(getActivity(), handler);

        /*progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("初始化中...");
        progressDialog.show();*/

        // 初始化页面数据
        initData();
        //initViewData();
        //Toast.makeText(getActivity(), "打招呼" + "/" + termId + "/" + termName, Toast.LENGTH_SHORT).show();

        // 设置监听

    }

    // 初始化数据
    private void initData() {

        termInfoTemp = xtSayhiService.findTermTempById(termId);// 终端临时表记录
        visitMTemp = xtSayhiService.findVisitTempById(visitId);// 拜访临时表记录
        // 路线集合
        mstRouteList = xtSayhiService.initMstRoute();
        // 终端等级集合
        dataDicByTermLevelLst = xtSayhiService.initDataDicByTermLevel();
        // 区域类型集合
        dataDicByAreaTypeLst = xtSayhiService.initDataDicByAreaType();
        // 销售渠道集合
        sellchanneldataLst = xtSayhiService.initDataDicBySellChannel();
        // 销售渠道集合
        sellchannelLst = getSellChannelList(sellchanneldataLst);
        // 主渠道集合
        mainchannelLst = getMainchannelLst(sellchannelLst);

        //设置时间
        calendar = Calendar.getInstance();
        yearr = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            aday = "0" + day;
        } else {
            aday = Integer.toString(day);
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 把选择控件也设置成系统时间
        Date date = calendar.getTime();
        ifminedate = sDateFormat.format(date);


        // 设置界面数据
        if (visitMTemp != null) {
            // 是否有效拜访 //进行判断(如果Status拜访状态为1 或者 拜访状态不为空设置为选中状态)
            if (ConstValues.FLAG_1.equals(visitMTemp.getStatus()) || CheckUtil.isBlankOrNull(visitMTemp.getStatus())) {
                xtvisitstatusSw.setStatus(true);
            } else {
                xtvisitstatusSw.setStatus(false);
            }

            // 是否我品店招
            if (ConstValues.FLAG_1.equals(termInfoTemp.getIfmine())) {
                xtwopindianzhaoSw.setStatus(true);
                xttimeBtn.setVisibility(View.VISIBLE);
                xttimeBtn.setText(termInfoTemp.getIfminedate());
                xttextBtn.setVisibility(View.VISIBLE);
            } else {//当店招未选中的时候,隐藏对应的控件 文字
                xtwopindianzhaoSw.setStatus(false);
                xttimeBtn.setText(ifminedate);
                xttimeBtn.setVisibility(View.GONE);
                xttextBtn.setVisibility(View.GONE);
            }

            //销售产品范围 我品: 是1 否0
            xtmineCb.setChecked(ConstValues.FLAG_1.equals(visitMTemp.getIsself()) ? true : false);
            //销售产品范围 竞品: 是1 否0
            xtvieCb.setChecked(ConstValues.FLAG_1.equals(visitMTemp.getIscmp()) ? true : false);
            // 终端合作状态 我品: 是1 否0
            xtmineprotocolCb.setChecked(ConstValues.FLAG_1.equals(termInfoTemp.getSelftreaty()) ? true : false);
            xtmineprotocolCb.setTag(termInfoTemp.getSelftreaty());
            // 终端合作状态 竞品: 是1 否0
            xtvieprotocolCb.setChecked(ConstValues.FLAG_1.equals(termInfoTemp.getCmpselftreaty()) ? true : false);
            xtvieprotocolCb.setTag(termInfoTemp.getCmpselftreaty());
            // 拜访对象
            xttermpersion.setText(visitMTemp.getVisituser());
        }

        if (termInfoTemp != null) {

            // 保留修改关的拜访顺序，用于判定是不更改同线路下的各终端的拜访顺序
            //prevSequence = termInfoTemp.getSequence();
            xttermcode.setText(termInfoTemp.getTerminalcode());
            xttermname.setText(termInfoTemp.getTerminalname());
            xttermaddress.setText(termInfoTemp.getAddress());
            xttermcontact.setText(termInfoTemp.getContact());
            xttermphone.setText(termInfoTemp.getMobile());
            xttermsequence.setText(termInfoTemp.getSequence());
            xttermcycle.setText(termInfoTemp.getCycle());
            // 高中普低,总
            Long tvolume = 0l;
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getHvolume()))) {
                xttermhvolume.setText(FunUtil.isNullToZero(termInfoTemp.getHvolume()));
            } else {
                xttermhvolume.setText(FunUtil.isNullToZero(termInfoTemp.getHvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfoTemp.getHvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getMvolume()))) {
                xttermmvolume.setText(FunUtil.isNullToZero(termInfoTemp.getMvolume()));
            } else {
                xttermmvolume.setText(FunUtil.isNullToZero(termInfoTemp.getMvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfoTemp.getMvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getPvolume()))) {
                xttermpvolume.setText(FunUtil.isNullToZero(termInfoTemp.getPvolume()));
            } else {
                xttermpvolume.setText(FunUtil.isNullToZero(termInfoTemp.getPvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfoTemp.getPvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getLvolume()))) {
                xttermlvolume.setText(FunUtil.isNullToZero(termInfoTemp.getLvolume()));
            } else {
                xttermlvolume.setText(FunUtil.isNullToZero(termInfoTemp.getLvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero(termInfoTemp.getLvolume());
            }
            xttermtvolume.setText(String.valueOf(tvolume));

            // 所属线路
            xttermroude.setText(xtSayhiService.getRouteName(termInfoTemp.getRoutekey()));

            // 区域类型
            xttermarea.setText(xtSayhiService.getDatadicName(termInfoTemp.getAreatype()));

            // 老板老板娘
            //xttermpersion.setText(visitMTemp.getVisitposition());
            xttermpersion.setText(visitMTemp.getVisituser());

            // 终端等级
            xttermlv.setText(xtSayhiService.getDatadicName(termInfoTemp.getTlevel()));

            // 销售渠道
            xttermsellchannel.setText(xtSayhiService.getDatadicName(termInfoTemp.getSellchannel()));
            xttermtmainchannel.setText(xtSayhiService.getDatadicName(termInfoTemp.getMainchannel()));
            xttermminorchannel.setText(xtSayhiService.getDatadicName(termInfoTemp.getMinorchannel()));

            // 获取省市县数据
            xtprovince.setText(xtSayhiService.getAreaName(termInfoTemp.getProvince()));
            xttermcity.setText(xtSayhiService.getAreaName(termInfoTemp.getCity()));
            xttermcountry.setText(xtSayhiService.getAreaName(termInfoTemp.getCounty()));


        }

    }

    //
    private void initViewData(){
        Thread thread = new Thread() {

            @Override
            public void run() {
                try{
                    initData();
                }catch (Exception e){

                }finally {
                    handler.sendEmptyMessage(XtSayhiFragment.FINISH_SUC);
                }
            }
        };
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.xt_sayhi_btn_time:// 进店时间选择
                DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                calendar.set(year, monthOfYear, dayOfMonth);
                                yearr = year;
                                month = monthOfYear;
                                day = dayOfMonth;
                                if (dayOfMonth < 10) {
                                    aday = "0" + dayOfMonth;
                                } else {
                                    aday = Integer.toString(dayOfMonth);
                                }
                                ifminedate = (Integer.toString(year) + "-"+ String.format("%02d", monthOfYear + 1)+ "-" + aday);
                                // ifminedate = (Integer.toString(year) +
                                // String.format("%02d", monthOfYear + 1) + aday);
                                xttimeBtn.setText(ifminedate);
                            }
                        }, yearr, month, day);
                if (!dateDialog.isShowing()) {
                    dateDialog.show();
                }
                break;

            case R.id.xt_sayhi_rl_termroude:// 终端路线

                mAlertViewExt = new AlertView("请选择路线", null, null, null, null, getActivity(), AlertView.Style.ActionSheet, this);
                ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
                ListView listview = (ListView) extView.findViewById(R.id.alert_list);
                AlertKeyValueAdapter keyValueAdapter = new AlertKeyValueAdapter(getActivity(), mstRouteList,
                        new String[]{"routekey", "routename"}, termInfoTemp.getRoutekey());
                listview.setAdapter(keyValueAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        xttermroude.setText(mstRouteList.get(position).getRoutename());
                        termInfoTemp.setRoutekey(mstRouteList.get(position).getRoutekey());
                        mAlertViewExt.dismiss();
                    }
                });
                mAlertViewExt.addExtView(extView);
                mAlertViewExt.setCancelable(true).setOnDismissListener(this);
                mAlertViewExt.show();
                break;

            case R.id.xtbf_sayhi_rl_termlv:// 终端等级

                mAlertViewExt = new AlertView("请选择终端等级", null, null, null, null, getActivity(), AlertView.Style.ActionSheet, this);
                ViewGroup lvextView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
                ListView lvlistview = (ListView) lvextView.findViewById(R.id.alert_list);
                AlertKeyValueAdapter lvkeyValueAdapter = new AlertKeyValueAdapter(getActivity(), dataDicByTermLevelLst,
                        new String[]{"key", "value"}, termInfoTemp.getTlevel());
                lvlistview.setAdapter(lvkeyValueAdapter);
                lvlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        xttermlv.setText(dataDicByTermLevelLst.get(position).getValue());
                        termInfoTemp.setTlevel(dataDicByTermLevelLst.get(position).getKey());
                        mAlertViewExt.dismiss();
                    }
                });
                mAlertViewExt.addExtView(lvextView);
                mAlertViewExt.setCancelable(true).setOnDismissListener(this);
                mAlertViewExt.show();

                break;
            case R.id.xtbf_sayhi_rl_termarea:// 区域类型

                mAlertViewExt = new AlertView("请选择区域类型", null, null, null, null, getActivity(), AlertView.Style.ActionSheet, this);
                ViewGroup areaextView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
                ListView arealistview = (ListView) areaextView.findViewById(R.id.alert_list);
                AlertKeyValueAdapter areakeyValueAdapter = new AlertKeyValueAdapter(getActivity(), dataDicByAreaTypeLst,
                        new String[]{"key", "value"}, termInfoTemp.getAreatype());
                arealistview.setAdapter(areakeyValueAdapter);
                arealistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        xttermarea.setText(dataDicByAreaTypeLst.get(position).getValue());
                        termInfoTemp.setAreatype(dataDicByAreaTypeLst.get(position).getKey());
                        mAlertViewExt.dismiss();
                    }
                });
                mAlertViewExt.addExtView(areaextView);
                mAlertViewExt.setCancelable(true).setOnDismissListener(this);
                mAlertViewExt.show();

                break;
            case R.id.xtbf_sayhi_rl_termsellchannel:// 销售渠道

                mAlertViewExt = new AlertView("请选择销售渠道", null, null, null, null, getActivity(), AlertView.Style.ActionSheet, this);
                ViewGroup sellchannelareaextView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
                ListView sellchannelarealistview = (ListView) sellchannelareaextView.findViewById(R.id.alert_list);
                AlertKeyValueAdapter sellchannelareakeyValueAdapter = new AlertKeyValueAdapter(getActivity(), sellchannelLst,
                        new String[]{"key", "value"}, termInfoTemp.getSellchannel());
                sellchannelarealistview.setAdapter(sellchannelareakeyValueAdapter);
                sellchannelarealistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        xttermsellchannel.setText(sellchannelLst.get(position).getValue());
                        termInfoTemp.setSellchannel(sellchannelLst.get(position).getKey());
                        mAlertViewExt.dismiss();
                    }
                });
                mAlertViewExt.addExtView(sellchannelareaextView);
                mAlertViewExt.setCancelable(true).setOnDismissListener(this);
                mAlertViewExt.show();

                break;
            case R.id.xtbf_sayhi_rl_termtmainchannel://主渠道

                mainchannelLst = getMainchannelLst(sellchannelLst);
                mAlertViewExt = new AlertView("请选择主渠道", null, null, null, null, getActivity(), AlertView.Style.ActionSheet, this);
                ViewGroup mainchannelareaextView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
                ListView mainchannelarealistview = (ListView) mainchannelareaextView.findViewById(R.id.alert_list);
                AlertKeyValueAdapter mainchannelareakeyValueAdapter = new AlertKeyValueAdapter(getActivity(), mainchannelLst,
                        new String[]{"key", "value"}, termInfoTemp.getMainchannel());
                mainchannelarealistview.setAdapter(mainchannelareakeyValueAdapter);
                mainchannelarealistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        xttermtmainchannel.setText(mainchannelLst.get(position).getValue());
                        termInfoTemp.setMainchannel(mainchannelLst.get(position).getKey());
                        mAlertViewExt.dismiss();
                    }
                });
                mAlertViewExt.addExtView(mainchannelareaextView);
                mAlertViewExt.setCancelable(true).setOnDismissListener(this);
                mAlertViewExt.show();

                break;
            case R.id.xtbf_sayhi_rl_termminorchannel:// 次渠道

                minorchannelLst = getMinorchannelLst(mainchannelLst);
                mAlertViewExt = new AlertView("请选择次渠道", null, null, null, null, getActivity(), AlertView.Style.ActionSheet, this);
                ViewGroup minorchannelareaextView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
                ListView minorchannelarealistview = (ListView) minorchannelareaextView.findViewById(R.id.alert_list);
                AlertKeyValueAdapter minorchannelareakeyValueAdapter = new AlertKeyValueAdapter(getActivity(), minorchannelLst,
                        new String[]{"key", "value"}, termInfoTemp.getMainchannel());
                minorchannelarealistview.setAdapter(minorchannelareakeyValueAdapter);
                minorchannelarealistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        xttermminorchannel.setText(minorchannelLst.get(position).getValue());
                        termInfoTemp.setMinorchannel(minorchannelLst.get(position).getKey());
                        mAlertViewExt.dismiss();
                    }
                });
                mAlertViewExt.addExtView(minorchannelareaextView);
                mAlertViewExt.setCancelable(true).setOnDismissListener(this);
                mAlertViewExt.show();

                break;
            case R.id.xtbf_sayhi_rl_termpersion:// 拜访对象


                break;


            default:
                break;
        }
    }

    /***
     * 删除无效渠道
     *
     * @param kvStcList
     * @return
     */
    private List<KvStc> getSellChannelList(List<KvStc> kvStcList) {
        List<KvStc> list = new ArrayList<KvStc>();
        for (KvStc kvStc : kvStcList) {
            if (!DbtUtils.getInvalidChannelList().contains(kvStc.getKey())) {
                list.add(kvStc);
            }
        }
        return list;
    }

    /**
     * 获取主渠道集合
     *
     * @param sellchannelLst 销售渠道集合
     * @return
     */
    private List<KvStc> getMainchannelLst(List<KvStc> sellchannelLst) {
        KvStc sell = new KvStc();
        for (KvStc kvstc : sellchannelLst) {
            if (kvstc.getKey().equals(termInfoTemp.getSellchannel())) {
                sell = kvstc;
            }
        }
        // 返回某个销售渠道下的主渠道集合
        return getSellChannelList(sell.getChildLst());
    }

    /**
     * 获取次渠道集合
     *
     * @param mainchannelLst 主渠道集合
     * @return
     */
    private List<KvStc> getMinorchannelLst(List<KvStc> mainchannelLst) {
        KvStc minor = new KvStc();
        for (KvStc kvstc : mainchannelLst) {
            if (kvstc.getKey().equals(termInfoTemp.getMainchannel())) {
                minor = kvstc;
            }
        }
        // 返回某个主渠道下的次渠道集合
        return getSellChannelList(minor.getChildLst());
    }

    @Override
    public void onItemClick(Object o, int position) {
        //判断是否是拓展窗口View，而且点击的是非取消按钮
        /*if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
            String name = etName.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "啥都没填呢", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "hello," + name, Toast.LENGTH_SHORT).show();
            }

            return;
        }
        Toast.makeText(this, "点击了第" + position + "个", Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onDismiss(Object o) {
        //closeKeyboard();
        //Toast.makeText(this, "消失了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongSwitchChanged(DdSlideSwitch obj, int status) {
        switch (obj.getId()) {
            //是否有效终端按钮
            case R.id.xt_sayhi_sw_termstatus:
                if (status == DdSlideSwitch.SWITCH_OFF) {
                    xtSayhiService.dialogInValidTerm(visitMTemp, termInfoTemp, seeFlag);
                }
                break;
            //是否我品店招
            case R.id.xt_sayhi_sw_wopindianzhao:
                if (status == DdSlideSwitch.SWITCH_OFF) {
                    xttimeBtn.setVisibility(View.GONE);
                    xttextBtn.setVisibility(View.GONE);
                } else {
                    xttimeBtn.setVisibility(View.VISIBLE);
                    xttextBtn.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    //失去焦点时调用
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        Long time5 = new Date().getTime();

        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag))
            return;

        // 终端状态为有效时才保存相关信息
        if (xttermstatusSw.getStatus()) {
            // 保存拜访主表信息
            if (xtvisitstatusSw.getStatus()) {
                visitMTemp.setStatus(ConstValues.FLAG_1);// status:1,true时为有效拜访
            } else {
                visitMTemp.setStatus(ConstValues.FLAG_0);
            }

            // 店招
			/*if (mineproductSW.getStatus()) {
				visitM.setIfmine(ConstValues.FLAG_1);// 是否我品店招 0不是 1是
				if(mineProductTime.getText().toString()!=null&&mineProductTime.getText().toString().length()>0){
					visitM.setIfminedate(mineProductTime.getText().toString());
				}else{
					visitM.setIfminedate(ifminedate);
				}
			} else {
				visitM.setIfmine(ConstValues.FLAG_0);
			}*/

			// 是否我品销售范围
            visitMTemp.setIsself(xtmineCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            // 是否竞品销售范围
            visitMTemp.setIscmp(xtvieCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            // 如果初始状态为选中，保存进为未选中则状态为0：流失
            if (xtmineprotocolCb.getTag() != null&& ConstValues.FLAG_1.equals(xtmineprotocolCb.getTag().toString())) {
                visitMTemp.setSelftreaty(xtmineprotocolCb.isChecked() ? ConstValues.FLAG_1: ConstValues.FLAG_0);
            } else {
                visitMTemp.setSelftreaty(xtmineprotocolCb.isChecked() ? ConstValues.FLAG_1: ConstValues.FLAG_0);
            }

            if (xtvieprotocolCb.getTag() != null&& ConstValues.FLAG_1.equals(xtvieprotocolCb.getTag().toString())) {
                visitMTemp.setCmptreaty(xtvieprotocolCb.isChecked() ? ConstValues.FLAG_1: ConstValues.FLAG_0);
            } else {
                visitMTemp.setCmptreaty(xtvieprotocolCb.isChecked() ? ConstValues.FLAG_1: ConstValues.FLAG_0);
            }
            /*visitMTemp.setVisituser(visitPersonEt.getText().toString());

            // 当页面切换太快,页面数据(拜访对象职位)来不及初始化,导致页面数据保存时,系统崩溃
            String visitpotion = "-1";
            try {
                visitpotion = laobanSp.getTag().toString();
            } catch (Exception e) {
                Log.e(TAG, "当页面切换太快,页面数据(拜访对象职位)来不及初始化", e);
            }
            if(!("-1".equals(visitpotion)||"66AA9D3A55374232891C964350610930".equals(visitpotion))){ //"-1",其他 根据原先用户是什么,不做处理
                String visitpositionName = service.getVisitpositionName(visitpotion);
                visitMTemp.setVisituser(visitpositionName);
            }
            visitMTemp.setVisitposition(visitpotion);

            if (belongLineSp.getTag() != null
                    && !CheckUtil.isBlankOrNull(belongLineSp.getTag()
                    .toString())) {
                visitMTemp.setRoutekey(belongLineSp.getTag().toString());
            }
            service.updateVisit(visitMTemp);*/

            // 保存终端信息
            if (termInfoTemp != null) {
                termInfoTemp.setTerminalname(FunUtil.isNullSetSpace(xttermname.getText()).toString());
                termInfoTemp.setRoutekey(FunUtil.isBlankOrNullTo(termInfoTemp.getRoutekey(), termInfoTemp.getRoutekey()));
                termInfoTemp.setProvince(termInfoTemp.getProvince());
                termInfoTemp.setCity(termInfoTemp.getCity());
                termInfoTemp.setCounty(termInfoTemp.getCounty());
                // if (citySp.getSelectedItem() != null) {
                // KvStc cityKvStc = (KvStc)citySp.getSelectedItem();
                // if ("全省".equals(cityKvStc.getValue())) {
                // termInfo.setCounty("");
                // } else {
                // termInfo.setCounty(FunUtil.isBlankOrNullTo(countrySp.getTag(),
                // termInfo.getCounty()));
                // }
                // }
                termInfoTemp.setAddress(FunUtil.isNullSetSpace(xttermaddress.getText()).toString());
                termInfoTemp.setContact(FunUtil.isNullSetSpace(xttermcontact.getText()).toString());
                if(xttermphone.getText().toString().length()>30)xttermphone.setText(xttermphone.getText().toString().substring(0, 30));
                termInfoTemp.setMobile(FunUtil.isNullSetSpace(xttermphone.getText()).toString());
                termInfoTemp.setTlevel(FunUtil.isBlankOrNullTo(termInfoTemp.getTlevel(),termInfoTemp.getTlevel()));
                termInfoTemp.setSequence(FunUtil.isNullSetSpace(xttermsequence.getText()).toString());
                termInfoTemp.setCycle(FunUtil.isNullSetSpace(xttermcycle.getText()).toString());
                termInfoTemp.setHvolume(FunUtil.isNullToZero(xttermhvolume.getText()).toString());
                termInfoTemp.setMvolume(FunUtil.isNullToZero(xttermmvolume.getText()).toString());
                termInfoTemp.setPvolume(FunUtil.isNullToZero(xttermpvolume.getText()).toString());
                termInfoTemp.setLvolume(FunUtil.isNullToZero(xttermlvolume.getText()).toString());

                // 有效终端
                if (xttermstatusSw.getStatus()) {
                    termInfoTemp.setStatus(ConstValues.FLAG_1); // 有效
                } else {
                    termInfoTemp.setStatus(ConstValues.FLAG_0); // 失效
                }

                // 店招
                if (xtwopindianzhaoSw.getStatus()) {
                    termInfoTemp.setIfmine(ConstValues.FLAG_1);// 是否我品店招 0不是 1是
                    if(xttimeBtn.getText().toString()!=null&&xttimeBtn.getText().toString().length()>0){
                        termInfoTemp.setIfminedate(xttimeBtn.getText().toString());
                    }else{
                        termInfoTemp.setIfminedate(ifminedate);
                    }
                } else {
                    termInfoTemp.setIfmine(ConstValues.FLAG_0);
                }

                termInfoTemp.setSellchannel(FunUtil.isBlankOrNullTo(termInfoTemp.getSellchannel(), termInfoTemp.getSellchannel()));
                termInfoTemp.setMainchannel(FunUtil.isBlankOrNullTo(termInfoTemp.getMainchannel(), termInfoTemp.getMainchannel()));
                termInfoTemp.setMinorchannel(FunUtil.isBlankOrNullTo(termInfoTemp.getMinorchannel(), termInfoTemp.getMinorchannel()));
                termInfoTemp.setAreatype(FunUtil.isBlankOrNullTo(termInfoTemp.getAreatype(), termInfoTemp.getAreatype()));
                //termInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
                termInfoTemp.setUpdateuser(PrefUtils.getString(getActivity(), "userCode", ""));
                termInfoTemp.setPadisconsistent(ConstValues.FLAG_0);
                termInfoTemp.setUpdatetime(DateUtil.getDateTimeDte(1));
                // --- 更改mst_terminalinfo_m表中selftreaty字段 ywm 20160426-----------------
                termInfoTemp.setSelftreaty(visitMTemp.getSelftreaty());
                termInfoTemp.setCmpselftreaty(visitMTemp.getCmptreaty());
                // --- 更改mst_terminalinfo_m表中selftreaty字段 ywm 20160426-----------------
                String prevSequence = xttermsequence.getText().toString();
                xtSayhiService.updateXtTermInfo(termInfoTemp, prevSequence);
            }
        }
        Long time6 = new Date().getTime();
        Log.e("Optimization", "保存数据" + (time6 - time5));
    }
}
