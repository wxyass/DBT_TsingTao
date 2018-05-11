package et.tsingtaopad.dd.ddagencyres;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.SoftReference;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.table.MitValcheckterM;
import et.tsingtaopad.db.table.MstAgencyKFM;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;

/**
 * 经销商资料库 经销商数据修改
 * Created by yangwenmin on 2018/3/12.
 */

public class DdAgencyAmendFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "DdAgencyAmendFragment";

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    MyHandler handler;

    public static final int DD_AGENCY_AMEND_SUC = 2321;//
    public static final int DD_AGENCY_AMEND_FAIL = 2322;//

    private MstAgencyKFM mstAgencyKFM;
    private String type;

    private RelativeLayout agency_res_amend_rl;
    private TextView agency_res_amend_rl_yd_title;
    private TextView agency_res_amend_rl_con1;
    private TextView agency_res_amend_rl_con2;
    private TextView agency_res_amend_rl_statue;
    private LinearLayout agency_res_amend_rl_ll_head;
    private RelativeLayout agency_res_amend_rl_dd_head;
    private RelativeLayout agency_res_amend_rl_dd_et;
    private TextView agency_res_amend_rl_dd_title_et;
    private EditText agency_res_amend_rl_dd_con1_et;
    private TextView agency_res_amend_rl_con2_et;
    private RelativeLayout agency_res_amend_rl_dd_sp;
    private TextView agency_res_amend_rl_dd_title_sp;
    private TextView agency_res_amend_rl_dd_con1_sp;
    private EditText agency_res_amend_rl_dd_con1_et_sp;
    private TextView agency_res_amend_rl_dd_con2_sp;
    private EditText agency_res_amend_dd_et_report;
    private Button agency_res_amend_dd_bt_save;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_agencyamend, container, false);
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
        //confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        agency_res_amend_rl = (RelativeLayout) view.findViewById(R.id.agency_res_amend_rl);
        agency_res_amend_rl_yd_title = (TextView) view.findViewById(R.id.agency_res_amend_rl_yd_title);
        agency_res_amend_rl_con1 = (TextView) view.findViewById(R.id.agency_res_amend_rl_con1);
        agency_res_amend_rl_con2 = (TextView) view.findViewById(R.id.agency_res_amend_rl_con2);
        agency_res_amend_rl_statue = (TextView) view.findViewById(R.id.agency_res_amend_rl_statue);

        agency_res_amend_rl_ll_head = (LinearLayout) view.findViewById(R.id.agency_res_amend_rl_ll_head);

        agency_res_amend_rl_dd_head = (RelativeLayout) view.findViewById(R.id.agency_res_amend_rl_dd_head);

        agency_res_amend_rl_dd_et = (RelativeLayout) view.findViewById(R.id.agency_res_amend_rl_dd_et);
        agency_res_amend_rl_dd_title_et = (TextView) view.findViewById(R.id.agency_res_amend_rl_dd_title_et);
        agency_res_amend_rl_dd_con1_et = (EditText) view.findViewById(R.id.agency_res_amend_rl_dd_con1_et);
        agency_res_amend_rl_con2_et = (TextView) view.findViewById(R.id.agency_res_amend_rl_con2_et);

        agency_res_amend_rl_dd_sp = (RelativeLayout) view.findViewById(R.id.agency_res_amend_rl_dd_sp);
        agency_res_amend_rl_dd_title_sp = (TextView) view.findViewById(R.id.agency_res_amend_rl_dd_title_sp);
        agency_res_amend_rl_dd_con1_sp = (TextView) view.findViewById(R.id.agency_res_amend_rl_dd_con1_sp);
        agency_res_amend_rl_dd_con1_et_sp = (EditText) view.findViewById(R.id.agency_res_amend_rl_dd_con1_et_sp);
        agency_res_amend_rl_dd_con2_sp = (TextView) view.findViewById(R.id.agency_res_amend_rl_dd_con2_sp);

        agency_res_amend_dd_et_report = (EditText) view.findViewById(R.id.agency_res_amend_dd_et_report);

        agency_res_amend_dd_bt_save = (Button) view.findViewById(R.id.agency_res_amend_dd_bt_save);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);

        // 初始化数据
        initData();

    }

    private void initData() {
        titleTv.setText("更正信息");

        // 获取传递过来的数据
        Bundle bundle = getArguments();
        type = bundle.getString("type");
        mstAgencyKFM = (MstAgencyKFM) bundle.getSerializable("mstAgencyKFM");

        if (DdAgencyContentFragment.AGENCYNAME.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_agencyname);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_agencyname);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_agencyname);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getAgencyname());

            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.CONTACT.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_contact);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_contact);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_contact);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getContact());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.MOBILE.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_mobile);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_mobile);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_mobile);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getMobile());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.ADDRESS.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_address);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_address);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_address);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getAddress());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.AREA.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_area);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_area);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_area);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getArea());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.MONEY.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_money);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_money);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_money);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getMoney());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.PERSION.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_persion);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_persion);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_persion);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getPersion());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.CARNUM.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_carnum);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_carnum);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_carnum);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getCarnum());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.ISONE.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_isone);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_isone);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_isone);

            agency_res_amend_rl_con1.setText(parseIsone(mstAgencyKFM.getIsone()));

            // 是否数一数二
            agency_res_amend_rl_dd_sp.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.KFDATA.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_kfdata);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_kfdata);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_kfdata);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getKfdate());

            // 开发时间
            agency_res_amend_rl_dd_sp.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.PASSDATA.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_passdate);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_passdate);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_passdate);
            agency_res_amend_rl_con1.setText(mstAgencyKFM.getPassdate());

            // 达成时间
            agency_res_amend_rl_dd_sp.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.PRODUCTNAME.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_productname);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_productname);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_productname);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getProductname());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.BUSINESS.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_business);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_business);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_business);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getBusiness());
        } else if (DdAgencyContentFragment.COVERTERMS.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_coverterms);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_coverterms);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_coverterms);

            agency_res_amend_rl_con1.setText(mstAgencyKFM.getCoverterms());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.SUPPLYTERMS.equals(type)) {
            agency_res_amend_rl_yd_title.setText(R.string.agencykf_supplyterms);
            agency_res_amend_rl_dd_title_et.setText(R.string.agencykf_supplyterms);
            agency_res_amend_rl_dd_title_sp.setText(R.string.agencykf_supplyterms);
            agency_res_amend_rl_con1.setText(mstAgencyKFM.getSupplyterms());
            agency_res_amend_rl_dd_et.setVisibility(View.VISIBLE);
        }

    }

    // 判定是否数一数二经销商
    private String parseIsone(int type){
        String isone ="";
        if(0 == type){
            isone = "否";
        }else{
            isone = "是";
        }
        return isone;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // 返回
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.top_navigation_rl_confirm:// 确定

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
        SoftReference<DdAgencyAmendFragment> fragmentRef;

        public MyHandler(DdAgencyAmendFragment fragment) {
            fragmentRef = new SoftReference<DdAgencyAmendFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DdAgencyAmendFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }

            // 处理UI 变化
            switch (msg.what) {
                case DD_AGENCY_AMEND_SUC:
                    //fragment.showAddProSuc(products, agency);
                    break;
                case DD_AGENCY_AMEND_FAIL: // 督导输入数据后
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
