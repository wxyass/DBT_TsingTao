package et.tsingtaopad.dd.dddealplan.remake;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.db.table.MitRepairM;
import et.tsingtaopad.db.table.MitRepaircheckM;
import et.tsingtaopad.db.table.MitRepairterM;
import et.tsingtaopad.dd.dddealplan.DdDealPlanFragment;
import et.tsingtaopad.dd.dddealplan.domain.DealStc;
import et.tsingtaopad.dd.dddealplan.make.DdDealSelectFragment;
import et.tsingtaopad.dd.dddealplan.make.domain.DealPlanMakeStc;
import et.tsingtaopad.dd.dddealplan.remake.domain.ReCheckTimeStc;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectService;
import et.tsingtaopad.dd.ddxt.updata.XtUploadService;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class DdReDealMakeFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "DdDealPlanFragment";

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    private String aday;
    private Calendar calendar;
    private int yearr;
    private int month;
    private int day;

    //
    public static final int MAKEPLAN_UP_SUC = 330001;
    //
    public static final int MAKEPLAN_UP_FAIL = 330002;

    private RelativeLayout rl_termname;
    private TextView termname;
    private RelativeLayout rl_grid;
    private TextView grid;
    private RelativeLayout rl_ydname;
    private TextView ydname;
    private RelativeLayout rl_question;
    private EditText question;
    private RelativeLayout rl_amendplan;
    private EditText amendplan;
    private RelativeLayout rl_measure;
    private EditText measure;
    private ListView lv_altime;
    private RelativeLayout rl_checktime;
    private TextView checktime;
    private Button submit;

    private DealStc dealStc;

    private XtTermSelectService xtSelectService;

    private List<MitRepairterM> mitRepairterMSelects;

    private DdCheckTimeAdapter checkTimeAdapter;

    private DdDealPlanFragment.MyHandler dealplanhandler;

    public DdReDealMakeFragment() {
    }

    @SuppressLint("ValidFragment")
    public DdReDealMakeFragment(DdDealPlanFragment.MyHandler DealPlanhandler) {
        this.dealplanhandler =DealPlanhandler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_redealmake, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        backBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_tv_title);
        //confirmBtn.setVisibility(View.VISIBLE);
        //confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        rl_termname = (RelativeLayout) view.findViewById(R.id.zgjh_remake_rl_termname);
        termname = (TextView) view.findViewById(R.id.zgjh_remake_termname);

        rl_grid = (RelativeLayout) view.findViewById(R.id.zgjh_remake_rl_grid);
        grid = (TextView) view.findViewById(R.id.zgjh_remake_grid);

        rl_ydname = (RelativeLayout) view.findViewById(R.id.zgjh_remake_rl_ydname);
        ydname = (TextView) view.findViewById(R.id.zgjh_remake_ydname);

        rl_question = (RelativeLayout) view.findViewById(R.id.zgjh_remake_rl_question);
        question = (EditText) view.findViewById(R.id.zgjh_remake_question);

        rl_amendplan = (RelativeLayout) view.findViewById(R.id.zgjh_remake_rl_amendplan);
        amendplan = (EditText) view.findViewById(R.id.zgjh_remake_amendplan);

        rl_measure = (RelativeLayout) view.findViewById(R.id.zgjh_remake_rl_measure);
        measure = (EditText) view.findViewById(R.id.zgjh_remake_measure);

        lv_altime = (ListView) view.findViewById(R.id.zgjh_remake_lv_altime);

        rl_checktime = (RelativeLayout) view.findViewById(R.id.zgjh_remake_rl_checktime);
        checktime = (TextView) view.findViewById(R.id.zgjh_remake_checktime);

        submit = (Button) view.findViewById(R.id.zgjh_remake_submit);

        // rl_termname.setOnClickListener(this);
        rl_checktime.setOnClickListener(this);
        submit.setOnClickListener(this);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("新增整改计划");
        handler = new MyHandler(this);

        // 获取系统时间
        calendar = Calendar.getInstance();
        yearr = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        xtSelectService = new XtTermSelectService(getActivity());

        // 获取传递过来的数据
        Bundle bundle = getArguments();
        dealStc = (DealStc) bundle.getSerializable("DealStc");// 整顿计划主表

        initData();

    }

    private void initData() {

        termname.setText(dealStc.getTerminalname());// = (TextView) view.findViewById(R.id.zgjh_remake_termname);
        grid.setText(dealStc.getGridname());// = (TextView) view.findViewById(R.id.zgjh_remake_termname);
        grid.setTag(dealStc.getGridkey());// = (TextView) view.findViewById(R.id.zgjh_remake_termname);
        ydname.setText(dealStc.getUsername());// = (TextView) view.findViewById(R.id.zgjh_remake_termname);
        ydname.setTag(dealStc.getUserid());// = (TextView) view.findViewById(R.id.zgjh_remake_termname);
        question.setText(dealStc.getContent());// = (TextView) view.findViewById(R.id.zgjh_remake_termname);
        amendplan.setText(dealStc.getRepairremark());// = (TextView) view.findViewById(R.id.zgjh_remake_termname);
        measure.setText(dealStc.getCheckcontent());// = (TextView) view.findViewById(R.id.zgjh_remake_termname);

        // 根据整顿计划主键 查询整改计划审核表  MIT_REPAIRCHECK_M
        List<ReCheckTimeStc> reCheckTimeStcs = xtSelectService.getDealPlanStatus(dealStc.getRepairid());

        checkTimeAdapter = new DdCheckTimeAdapter(getActivity(), reCheckTimeStcs, null);
        lv_altime.setAdapter(checkTimeAdapter);
        ViewUtil.setListViewHeight(lv_altime);
    }

    private String selectDate;

    // 点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.top_navigation_rl_confirm:// 确定

                Toast.makeText(getActivity(), "弹出日历", Toast.LENGTH_SHORT).show();

                break;
            case R.id.zgjh_remake_rl_termname:// 选择终端

                // toDdDealSelectFragment();

                break;
            case R.id.zgjh_remake_rl_checktime:// 选择核查时间

                // Toast.makeText(getActivity(), "选择核查时间", Toast.LENGTH_SHORT).show();
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

                        selectDate = (Integer.toString(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + aday);

                        checktime.setText(selectDate);


                    }
                }, yearr, month, day);
                if (!dateDialog.isShowing()) {
                    dateDialog.show();
                }

                break;
            case R.id.zgjh_remake_submit:// 保存数据

                saveValue();


                break;
            default:
                break;
        }
    }

    // 跳转到  选择终端
    private void toDdDealSelectFragment() {
        /*Bundle bundle = new Bundle();
        bundle.putSerializable("repairM", repairM);
        *//*bundle.putSerializable("weekplan", mitPlanweekM);
        bundle.putSerializable("weekDateStart", weekDateStart);
        bundle.putSerializable("weekDateEnd", weekDateEnd);*//*
        DdDealSelectFragment ddDealSelectFragment = new DdDealSelectFragment(handler);
        ddDealSelectFragment.setArguments(bundle);
        // 跳转 新增整改计划
        addHomeFragment(ddDealSelectFragment, "dddealselectfragment");*/
    }

    // 保存数据
    private void saveValue() {

        if (!checkTermName()) {

            return;
        }

        String grid_tag = grid.getTag().toString();

        String ydname_tag = ydname.getTag().toString();

        String question_string = question.getText().toString();

        String amendplan_string = amendplan.getText().toString();

        String measure_string = measure.getText().toString();

        String checktime_string = checktime.getText().toString();

        MitRepairM repairM = new MitRepairM();
        repairM.setId(dealStc.getRepairid());
        repairM.setGridkey(grid_tag);//定格
        repairM.setUserid(ydname_tag);// 业代ID
        repairM.setContent(question_string);//问题描述
        repairM.setRepairremark(amendplan_string);//改进计划
        repairM.setCheckcontent(measure_string);//考核措施
        repairM.setCreuser(PrefUtils.getString(getActivity(), "userid", ""));//追溯人
        repairM.setCreuserareaid(PrefUtils.getString(getActivity(), "departmentid", ""));//追溯人所属区域
        //repairM.setCredate(new Date());//创建日期
        repairM.setUpdateuser(PrefUtils.getString(getActivity(), "userid", ""));//更新人
        repairM.setUpdatedate(new Date());//更新时间
        repairM.setUploadflag("1");
        repairM.setPadisconsistent("0");
        repairM.setStatus("0");//

        MitRepaircheckM mitRepaircheckM = new MitRepaircheckM();
        mitRepaircheckM.setId(FunUtil.getUUID());//
        mitRepaircheckM.setRepairid(dealStc.getRepairid());//整改计划主表ID
        mitRepaircheckM.setStatus("0");//整改状态
        mitRepaircheckM.setRepairtime(checktime_string);//整改日期
        mitRepaircheckM.setUploadflag("1");
        mitRepaircheckM.setPadisconsistent("0");
        //mitRepaircheckM.setCredate(new Date());
        mitRepaircheckM.setCredate(DateUtil.getDateTimeStr(8));

        // 保存到库中
        xtSelectService.saveMitRepairM(repairM, mitRepaircheckM);

        // 上传整顿计划
        XtUploadService xtUploadService = new XtUploadService(getActivity(), null);
        xtUploadService.upload_repair(false, repairM, mitRepaircheckM, 1);


        // Toast.makeText(getActivity(), "整顿计划保存成功", Toast.LENGTH_SHORT).show();
        supportFragmentManager.popBackStack();

        // 发送新增成功的信息
        dealplanhandler.sendEmptyMessage(DdDealPlanFragment.DEALPLAN_UP_SUC);
    }

    private boolean checkTermName() {
        boolean ishaveName = true;
        if (TextUtils.isEmpty(termname.getText().toString())) {
            ishaveName = false;
            Toast.makeText(getActivity(), "请选择整改终端", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(checktime.getText().toString())) {
            ishaveName = false;
            Toast.makeText(getActivity(), "请选择复查时间", Toast.LENGTH_SHORT).show();
        }
        return ishaveName;
    }


    MyHandler handler;

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<DdReDealMakeFragment> fragmentRef;

        public MyHandler(DdReDealMakeFragment fragment) {
            fragmentRef = new SoftReference<DdReDealMakeFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DdReDealMakeFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            // 处理UI 变化
            switch (msg.what) {
                case MAKEPLAN_UP_SUC://
                    fragment.shuaxinDdDealMakeFragment(1);
                    break;
                case MAKEPLAN_UP_FAIL://
                    fragment.shuaxinDdDealMakeFragment(2);
                    break;

            }
        }
    }

    // 结束上传  刷新页面  0:确定上传  1上传成功  2上传失败
    private void shuaxinDdDealMakeFragment(int upType) {
        Toast.makeText(getActivity(), "成功返回保存数据", Toast.LENGTH_SHORT).show();

        // 刷新终端名称
        refreshTermname();

    }

    // 刷新终端名称
    private void refreshTermname() {
        // 查询已选择的终端
        List<DealPlanMakeStc> valueLst = xtSelectService.getSelectTerminal(dealStc.getRepairid());
        if (valueLst.size() > 0) {
            String terminalName = listToString(valueLst);
            termname.setText(terminalName);
            grid.setText(FunUtil.isBlankOrNullTo(valueLst.get(0).getGridname(), ""));
            grid.setTag(FunUtil.isBlankOrNullTo(valueLst.get(0).getGridkey(), ""));
            ydname.setText(FunUtil.isBlankOrNullTo(valueLst.get(0).getUsername(), ""));
            ydname.setTag(FunUtil.isBlankOrNullTo(valueLst.get(0).getUserkey(), ""));
        }
    }

    /**
     * 将集合 转成字符串以逗号隔开
     *
     * @param list
     * @return
     */
    public static String listToString(List<DealPlanMakeStc> list) {
        String listToString = "";
        if (!list.isEmpty()) {
            /* 输出list值 */
            for (int i = 0; i < list.size(); i++) {
                //listToString+="'"+list.get(i)+"'";
                listToString += list.get(i).getTerminalname();
                if (i != list.size() - 1) {
                    listToString += ",";
                }
            }
        }
        return listToString;
    }
}
