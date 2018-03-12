package et.tsingtaopad.main.operation.promotion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.util.DialogUtil;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class PromotionSearchCoreFragment extends BaseCoreFragment implements View.OnClickListener , AdapterView.OnItemSelectedListener{

    public static final String TAG = "PromotionSearchCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private PromotionService service;

    private Button backBt;

    private AlertDialog dialog;
    private Spinner promSp;
    private Button lineBt;
    private Button termTypeBt;
    private Button searchDateBt;
    private Button searchBt;
    private TextView searchDateTv;
    private TextView finishTv;
    private TextView unfinishTv;
    private TextView totalTv;
    private TextView rateTv;
    private TextView promBeginTv;
    private TextView promEndTv;

    private ListView finishLv;
    private ListView unfinishLv;


    private String searchDate;
    private String[] lineIds;
    private String[] termLevels;
    private MstPromotionsM promInfo;
    private List<MstPromotionsM> promLst = new ArrayList<MstPromotionsM>();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();

            super.handleMessage(msg);
            switch (msg.what) {

                // 提示信息
                case ConstValues.WAIT1:
                    Toast.makeText(getContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    };
                    break;

                // 查询本地数据库返回对象
                case ConstValues.WAIT2:
                    dealDateShow(ConstValues.FLAG_0, bundle.getString("termMap"));
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                {
                    dialog.dismiss();
                };
                break;
            }
        }

    };

    public PromotionSearchCoreFragment() {
    }

    public static PromotionSearchCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        PromotionSearchCoreFragment fragment = new PromotionSearchCoreFragment();
        return fragment;
    }

    public static PromotionSearchCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        PromotionSearchCoreFragment fragment = new PromotionSearchCoreFragment();
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

    }

    // 只用来初始化控件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.operation_promotion, null);
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


        dialog = DialogUtil.progressDialog(getContext(), R.string.dialog_msg_search);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        promSp = (Spinner) view.findViewById(R.id.promotion_sp_promotion);
        lineBt = (Button) view.findViewById(R.id.promotion_btn_line);
        searchDateBt = (Button) view.findViewById(R.id.promotion_btn_date);
        termTypeBt = (Button) view.findViewById(R.id.promotion_btn_type);
        searchBt = (Button) view.findViewById(R.id.promotion_btn_search);
        searchDateTv = (TextView)view.findViewById(R.id.promotion_tv_date);
        finishTv = (TextView) view.findViewById(R.id.promotion_tv_finish);
        unfinishTv = (TextView) view.findViewById(R.id.promotion_tv_unfinished);
        totalTv = (TextView) view.findViewById(R.id.promotion_tv_total);
        rateTv = (TextView) view.findViewById(R.id.promotion_tv_rate);
        promBeginTv = (TextView) view.findViewById(R.id.promotion_tv_begin);
        promEndTv = (TextView) view.findViewById(R.id.promotion_tv_to);
        finishLv = (ListView) view.findViewById(R.id.promotion_lv_finish);
        unfinishLv = (ListView) view.findViewById(R.id.promotion_lv_unfinished);



        promSp.setOnItemSelectedListener(this);
        backBt.setOnClickListener(this);
        lineBt.setOnClickListener(this);
        termTypeBt.setOnClickListener(this);
        searchDateBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        titleTv.setText(R.string.promotion_promotion1);
        confirmRl.setVisibility(View.VISIBLE);
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
        ConstValues.msgHandler = this.handler;

        service = new PromotionService(getContext(), handler);
        titleTv.setText(R.string.promotion_promotion1);
        searchDate = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        searchDateBt.setText(searchDate);
        searchDateTv.setText(searchDate);
        promLst = service.queryPromo(searchDate.replace("-", ""), true);
        promSp.setAdapter(new SpinnerKeyValueAdapter(getContext(),
                promLst, new String[]{"promotkey", "promotname"}, null));

    }

    /**
     * 线路对话框
     */
    private void lineDialog(){
        List<MstRouteM> lineLst = new ArrayList<MstRouteM>(GlobalValues.lineLst);
        if (!CheckUtil.IsEmpty(lineLst)) {
            lineLst.remove(0);
        }
        //DialogUtil.showMultipleDialog(getContext(),lineBt, R.string.promotion_title_line, lineLst, new String[]{"routekey","routename"}, R.string.promotion_line1);
    }

    /**
     * 终端类型选择对话框
     */
    private void TypeDialog(){

        List<KvStc> typeLst = new ArrayList<KvStc>(
                GlobalValues.dataDicMap.get("levelLst"));
        if (!CheckUtil.IsEmpty(GlobalValues.dataDicMap.get("levelLst"))) {
            Collections.copy(typeLst, GlobalValues.dataDicMap.get("levelLst"));
            typeLst.remove(0);
        }
        //DialogUtil.showMultipleDialog(getContext(),termTypeBt, R.string.promotion_title_level, typeLst,new String[]{"key","value"}, R.string.promotion_type1);
    }

    /**
     * 促销活动查询界面 数据显示
     *
     * @param type  0：处理本地查询结果， 1：处理网络查询返回结果
     */
    @SuppressWarnings("unchecked")
    private void dealDateShow(String type, String json) {

        if (ConstValues.FLAG_0.equals(type) && !CheckUtil.isBlankOrNull(json)) {
            Map<String, Object> termMap = JsonUtil.parseMap(json);
            List<String> finishLst = new ArrayList<String>();
            if (termMap.get("Y") != null ) {
                finishLst = (List<String>)termMap.get("Y");
            }
            List<String> unfinishLst = new ArrayList<String>();
            if (termMap.get("N") != null ) {
                unfinishLst = (List<String>)termMap.get("N");
            }
            if (termMap != null) {
                int finish = finishLst.size();
                int nufinish = unfinishLst.size();
                int total = finish + nufinish;
                finishTv.setText(String.valueOf(finish));
                unfinishTv.setText(String.valueOf(nufinish));
                totalTv.setText(String.valueOf(total));
                if (total == 0) {
                    rateTv.setText("0");
                    ViewUtil.sendMsg(getContext(), "查询范围内无符合该活动的终端");
                } else if (finish == 0 ) {
                    rateTv.setText("0");
                } else {
                    DecimalFormat df = new DecimalFormat("##0.00");
                    rateTv.setText(df.format((double)finish/total * 100.0d) + "%");
                }
                PromotionAdapter finishAdapter = new PromotionAdapter(getContext(), finishLst, R.drawable.ico_plan_right);
                finishLv.setAdapter(finishAdapter);
                PromotionAdapter unfinishAdapter = new PromotionAdapter(getContext(), unfinishLst, R.drawable.ico_plan_wrong);
                unfinishLv.setAdapter(unfinishAdapter);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int id, long position) {

        promInfo = (MstPromotionsM)promSp.getSelectedItem();
        if ("-1".equals(promInfo.getPromotkey())) {
            promBeginTv.setText("");
            promEndTv.setText("");
        } else {
            promBeginTv.setText(DateUtil.formatDate(ConstValues.WAIT1, promInfo.getStartdate()));
            promEndTv.setText(DateUtil.formatDate(ConstValues.WAIT1, promInfo.getEnddate()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
        // 确定
        }else if(i == R.id.banner_navigation_rl_confirm){

        }
    }
}
