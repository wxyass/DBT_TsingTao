package et.tsingtaopad.dd.ddweekplan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.dd.ddweekplan.domain.DayPlanStc;
import et.tsingtaopad.listviewintf.IClick;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class DdWeekPlanFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "DdWeekPlanFragment";

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    //
    public static final int WEEKPLAN_UP_SUC = 3101;
    //
    public static final int WEEKPLAN_UP_FAIL = 3102;


    private String time;
    private String aday;
    private Calendar calendar;
    private int yearr;
    private int month;
    private int day;
    private Button submitTv;
    private ListView planLv;
    private WeekPlanAdapter adapter;

    String weekDateStart = "";
    String weekDateEnd = "";

    List<DayPlanStc> workPlanStcs = new ArrayList<DayPlanStc>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_weekplan, container, false);
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
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);


        submitTv = (Button) view.findViewById(R.id.dd_weekplan_bt_submit);
        planLv = (ListView) view.findViewById(R.id.dd_weekplan_lv_);
        submitTv.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("我的计划");
        handler = new MyHandler(this);
        ConstValues.handler = handler;
        confirmTv.setText("日历");

        // 获取系统时间
        calendar = Calendar.getInstance();
        yearr = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }

    private void initData() {

        calendar.setTime(this.calendar.getTime());//把随意设置的时间或者是系统的时间赋值给    caleendar

        // 初始化本周7天计划(即在MstPlanforuserM表中生成7条记录),若这周7条记录存在过则复用这7条记录
        for (int i = 1; i <= 7; i++) {
            calendar.add(Calendar.DAY_OF_WEEK, i - calendar.get(Calendar.DAY_OF_WEEK));
            Date date = calendar.getTime();
            String plandate = DateUtil.formatDate(date, "yyyyMMdd");
            if (i == 1) {
                // 获取本周的开始时间(定义为全局)
                weekDateStart = plandate;
            } else if (i == 7) {
                // 获取本周的结束时间(定义为全局)
                weekDateEnd = plandate;
            }

            // 搭建每周 日计划记录(7条)
            DayPlanStc dayPlanStc = new DayPlanStc();
            dayPlanStc.setPlanKey(FunUtil.getUUID());
            dayPlanStc.setState("0");
            dayPlanStc.setDate(date);
            dayPlanStc.setWeekday("周" + datePreview(i));
            workPlanStcs.add(dayPlanStc);
        }

        adapter = new WeekPlanAdapter(getActivity(), workPlanStcs, new IClick() {
            @Override
            public void listViewItemClick(int position, View v) {
                toDayPlanFragment();
            }
        });
        planLv.setAdapter(adapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

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
                        time = (Integer.toString(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + aday);

                        String workplan_tv_rounds = (getString(R.string.workplan_msg1) + calendar.get(Calendar.WEEK_OF_MONTH) + getString(R.string.workplan_msg2));
                        String tv_plantitle = (calendar.get(Calendar.YEAR) + getString(R.string.workplan_msg3) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.workplan_msg4) + calendar.get(Calendar.WEEK_OF_MONTH) + getString(R.string.workplan_msg5));

                        initData();

                    }
                }, yearr, month, day);
                if (!dateDialog.isShowing()) {
                    dateDialog.show();
                }

                break;
            case R.id.dd_weekplan_bt_submit:// 跳转到日计划
                // toDayPlanFragment();
                break;
            default:
                break;
        }
    }


    MyHandler handler;

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<DdWeekPlanFragment> fragmentRef;

        public MyHandler(DdWeekPlanFragment fragment) {
            fragmentRef = new SoftReference<DdWeekPlanFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DdWeekPlanFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }


            // 处理UI 变化
            switch (msg.what) {
                case WEEKPLAN_UP_SUC://
                    fragment.shuaxinXtTermSelect(1);
                    break;
                case WEEKPLAN_UP_FAIL://
                    fragment.shuaxinXtTermSelect(2);
                    break;

            }
        }
    }

    // 结束上传  刷新页面  0:确定上传  1上传成功  2上传失败
    private void shuaxinXtTermSelect(int upType) {

    }

    // 跳转到日计划
    private void toDayPlanFragment() {
        Bundle bundle = new Bundle();
        // bundle.putSerializable("fromFragment", "ZsTermSelectFragment");
        //bundle.putSerializable("mitValcheckterM", mitValcheckterMs.get(0));
        DdDayPlanFragment ddDayPlanFragment = new DdDayPlanFragment();
        ddDayPlanFragment.setArguments(bundle);
        // 跳转日计划制定
        addHomeFragment(ddDayPlanFragment, "dddayplanfragment");
    }

    private String datePreview(int num) {
        String temp = null;
        switch (num) {
            case 1:
                temp = "日";

                break;
            case 2:
                temp = "一";

                break;
            case 3:
                temp = "二";

                break;
            case 4:
                temp = "三";

                break;
            case 5:
                temp = "四";

                break;
            case 6:
                temp = "五";

                break;
            case 7:
                temp = "六";
                break;

        }
        return temp;
    }

}
