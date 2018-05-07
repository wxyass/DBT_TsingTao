package et.tsingtaopad.dd.ddagencyres;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.ref.SoftReference;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;

/**
 * 经销商资料库 经销商未稽查判断
 * Created by yangwenmin on 2018/3/12.
 */

public class DdAgencyContentFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "DdAgencyContentFragment";

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    MyHandler handler;

    public static final int DD_AGENCY_CONTENT_SUC = 2311;//
    public static final int DD_AGENCY_CONTENT_FAIL = 2312;//



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_agencycontent, container, false);
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

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);

        // 初始化数据
        initData();

    }

    private void initData() {
        titleTv.setText("青岛啤酒北京销售有限公司");
        confirmTv.setText("更正");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // 返回
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.top_navigation_rl_confirm:// 确定
                Bundle bundle = new Bundle();
                //bundle.putSerializable("fromFragment", "XtTermSelectFragment");
                DdAgencyAmendFragment agencyAmendFragment = new DdAgencyAmendFragment();
                agencyAmendFragment.setArguments(bundle);
                // 跳转 经销商库存盘点 填充数据
                changeHomeFragment(agencyAmendFragment, "DdAgencyAmendFragment");
                break;

            default:
                break;
        }
    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<DdAgencyContentFragment> fragmentRef;

        public MyHandler(DdAgencyContentFragment fragment) {
            fragmentRef = new SoftReference<DdAgencyContentFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DdAgencyContentFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }

            // 处理UI 变化
            switch (msg.what) {
                case DD_AGENCY_CONTENT_SUC:
                    //fragment.showAddProSuc(products, agency);
                    break;
                case DD_AGENCY_CONTENT_FAIL: // 督导输入数据后
                    //fragment.showAdapter();
                    break;
            }
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");

        // 保存追溯 进销存数据  MitValsupplyMTemp
        //invoicingService.saveZsInvoicing(dataLst);
    }

}
