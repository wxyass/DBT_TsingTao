package et.tsingtaopad.dd.ddweekplan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.dd.ddweekplan.domain.DayDetailStc;
import et.tsingtaopad.dd.ddweekplan.domain.DayPlanStc;
import et.tsingtaopad.listviewintf.ILongClick;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class DdDayPlanFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "DdDayPlanFragment";

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;
    private Button addTv;
    private Button submitTv;
    private ListView planLv;
    private DayDetailAdapter detailAdapter;

    //
    public static final int DAYPLAN_UP_SUC = 310001;
    //
    public static final int DAYPLAN_UP_FAIL = 310002;

    List<DayDetailStc> dayDetailStcs = new ArrayList<DayDetailStc>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_dayplan, container, false);
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
        confirmBtn.setVisibility(View.INVISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        addTv = (Button) view.findViewById(R.id.dd_dayplan_bt_add);
        submitTv = (Button) view.findViewById(R.id.dd_dayplan_bt_submit);
        planLv = (ListView) view.findViewById(R.id.dd_dayplan_lv_details);
        addTv.setOnClickListener(this);
        submitTv.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("制定计划");
        handler = new MyHandler(this);
        ConstValues.handler = handler;

        initData();
    }

    private void initData() {

        /*DayDetailStc detailStc = new DayDetailStc();
        dayDetailStcs.add(detailStc);*/
        detailAdapter = new DayDetailAdapter(getActivity(), dayDetailStcs,new ILongClick() {
            @Override
            public void listViewItemLongClick(int position, View v) {
                dayDetailStcs.remove(position);
                Toast.makeText(getActivity(),"删除"+position+"项",Toast.LENGTH_SHORT).show();
                detailAdapter.notifyDataSetChanged();
            }
        });
        planLv.setAdapter(detailAdapter);

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

                break;
            case R.id.dd_dayplan_bt_add:// 确定
                DayDetailStc detailStc = new DayDetailStc();
                List<String> checknames = new ArrayList<>();
                detailStc.setValchecknameLv(checknames);
                dayDetailStcs.add(detailStc);
                detailAdapter.notifyDataSetChanged();
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
        SoftReference<DdDayPlanFragment> fragmentRef;

        public MyHandler(DdDayPlanFragment fragment) {
            fragmentRef = new SoftReference<DdDayPlanFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DdDayPlanFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }


            // 处理UI 变化
            switch (msg.what) {
                case DAYPLAN_UP_SUC://
                    fragment.shuaxinXtTermSelect(1);
                    break;
                case DAYPLAN_UP_FAIL://
                    fragment.shuaxinXtTermSelect(2);
                    break;

            }
        }
    }

    // 结束上传  刷新页面  0:确定上传  1上传成功  2上传失败
    private void shuaxinXtTermSelect(int upType) {

    }

}
