package et.tsingtaopad.dd.dddealplan;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnItemClickListener;
import et.tsingtaopad.db.table.MitRepairM;
import et.tsingtaopad.dd.dddealplan.domain.DealStc;
import et.tsingtaopad.dd.dddealplan.make.DdDealMakeFragment;
import et.tsingtaopad.dd.dddealplan.remake.DdReDealMakeFragment;
import et.tsingtaopad.dd.ddxt.updata.XtUploadService;
import et.tsingtaopad.dd.ddzs.zscheckindex.ZsCheckIndexFragment;
import et.tsingtaopad.dd.ddzs.zscheckindex.ZsCheckPromoAmendFragment;
import et.tsingtaopad.dd.ddzs.zsshopvisit.ZsVisitShopActivity;
import et.tsingtaopad.listviewintf.IClick;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class DdDealPlanFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "DdDealPlanFragment";

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;
    private DdDealPlanService ddDealPlanService;

    //
    public static final int DEALPLAN_UP_SUC = 3301;

    //
    public static final int DEALPLAN_UP_FAIL = 3302;

    public static final int DEALPLAN_NEED_UP = 3303;

    private TextView tv_month;
    private Button bt_addplan;
    private et.tsingtaopad.view.NoScrollListView monthplan_lv;
    private DdDealPlanAdapter dealPlanAdapter;
    private List<DealStc> dataLst;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_dealplan, container, false);
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

        tv_month = (TextView) view.findViewById(R.id.zgjh_tv_month);
        bt_addplan = (Button) view.findViewById(R.id.zgjh_bt_addplan);
        monthplan_lv = (et.tsingtaopad.view.NoScrollListView) view.findViewById(R.id.zgjh_monthplan_lv);

        bt_addplan.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("整改计划");
        handler = new MyHandler(this);
        ConstValues.handler = handler;
        confirmTv.setText("");

        ddDealPlanService = new DdDealPlanService(getActivity());
        initData();
    }

    // 初始化数据
    private void initData() {

        dataLst = ddDealPlanService.getDealPlanTerminal();

        dealPlanAdapter = new DdDealPlanAdapter(getActivity(), dataLst, new IClick() {
            @Override
            public void listViewItemClick(int position, View v) {
                DealStc stc = dataLst.get(position);
                String status = stc.getStatus();
                if ("1".equals(status)) {// 未通过
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DealStc", stc);
                    /*bundle.putSerializable("weekplan", mitPlanweekM);
                    bundle.putSerializable("weekDateStart", weekDateStart);
                    bundle.putSerializable("weekDateEnd", weekDateEnd);*/
                    DdReDealMakeFragment ddReDealMakeFragment = new DdReDealMakeFragment();
                    ddReDealMakeFragment.setArguments(bundle);
                    // 跳转 新增整改计划
                    addHomeFragment(ddReDealMakeFragment, "ddredealmakefragment");
                } else if ("2".equals(status)) {// 已通过

                } else {
                    alertShow6(position);// 弹窗: 未通过,已通过
                }

            }
        });
        monthplan_lv.setAdapter(dealPlanAdapter);
        ViewUtil.setListViewHeight(monthplan_lv);

    }

    /**
     * 弹窗6
     * 参数1: 标题 √
     * 参数2: 主体内容    ×
     * 参数3: 取消按钮    √
     * 参数4: 高亮按钮 数组 ×
     * 参数5: 普通按钮 数组 √
     * 参数6: 上下文 √
     * 参数7: 弹窗类型 (只有取消按钮)   √
     * 参数8: 条目点击监听  √
     */
    DealStc stc ;
    public void alertShow6(final int posi) {
        new AlertView("请选择复查结果", null, "取消", new String[]{"未通过"},
                new String[]{"已通过"},
                getActivity(), AlertView.Style.ActionSheet,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        // Toast.makeText(getActivity(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                        stc = dataLst.get(posi);
                        String repaircheckid = stc.getRepaircheckid();
                        String status = stc.getStatus();
                        if (0 == position) {// 未通过
                            ddDealPlanService.setStatus(repaircheckid, "1");// 并修改未未上传
                            handler.sendEmptyMessage(DEALPLAN_NEED_UP);
                        } else if(1 == position) {// 已通过
                            ddDealPlanService.setStatus(repaircheckid, "2");// 并修改未未上传
                            handler.sendEmptyMessage(DEALPLAN_NEED_UP);
                        }

                    }
                }).setCancelable(true).show();
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
            case R.id.zgjh_bt_addplan:// 新增整顿计划
                // 跳转到  新增整改计划
                toDdDealMakeFragment();
                break;

            default:
                break;
        }
    }

    // 跳转到  新增整改计划
    private void toDdDealMakeFragment() {
        Bundle bundle = new Bundle();
        /*bundle.putSerializable("dayplanstc", workPlanStcs.get(position));
        bundle.putSerializable("weekplan", mitPlanweekM);
        bundle.putSerializable("weekDateStart", weekDateStart);
        bundle.putSerializable("weekDateEnd", weekDateEnd);*/
        DdDealMakeFragment ddDealMakeFragment = new DdDealMakeFragment(handler);
        ddDealMakeFragment.setArguments(bundle);
        // 跳转 新增整改计划
        addHomeFragment(ddDealMakeFragment, "dddealmakefragment");
    }


    MyHandler handler;

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<DdDealPlanFragment> fragmentRef;

        public MyHandler(DdDealPlanFragment fragment) {
            fragmentRef = new SoftReference<DdDealPlanFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DdDealPlanFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }


            // 处理UI 变化
            switch (msg.what) {
                case DEALPLAN_UP_SUC://
                    fragment.shuaxinFragment(1);
                    break;
                case DEALPLAN_NEED_UP://
                    fragment.upRepair(1);
                    break;
                /*case DEALPLAN_UP_FAIL://
                    fragment.shuaxinFragment(2);
                    break;*/

            }
        }
    }

    // 上传未通过 或已通过
    private void upRepair(int i) {

        initData();
        // 上传整顿计划
        MitRepairM repairM = new MitRepairM();
        repairM.setId(stc.getRepairid());
        repairM.setGridkey(stc.getGridkey());//定格
        repairM.setUserid(stc.getUserid());// 业代ID
        repairM.setContent(stc.getContent());//问题描述
        repairM.setRepairremark(stc.getRepairremark());//改进计划
        repairM.setCheckcontent(stc.getCheckcontent());//考核措施
        repairM.setCreuser(PrefUtils.getString(getActivity(), "userid", ""));//追溯人
        repairM.setCreuserareaid(PrefUtils.getString(getActivity(), "departmentid", ""));//追溯人所属区域
        //repairM.setCredate(new Date());//创建日期
        repairM.setUpdateuser(PrefUtils.getString(getActivity(), "userid", ""));//更新人
        repairM.setUpdatedate(new Date());//更新时间
        repairM.setUploadflag("1");
        repairM.setPadisconsistent("0");

        XtUploadService xtUploadService = new XtUploadService(getActivity(), null);
        xtUploadService.upload_repair(false, repairM, null, 1);
    }

    // 结束上传  刷新页面
    private void shuaxinFragment(int upType) {
        initData();
    }

}
