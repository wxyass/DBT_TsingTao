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
import et.tsingtaopad.db.table.MstAgencyKFM;

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

    private RelativeLayout yd_all_rl;
    private TextView yd_title_tv;
    private TextView yd_con1_tv;
    private TextView yd_con2_tv;
    private TextView yd_statue_tv;
    private LinearLayout dd_head_ll;
    private RelativeLayout dd_all_rl;
    private RelativeLayout dd_et_rl;
    private TextView dd_title_tv;
    private EditText dd_con1_et;
    private TextView dd_con2_tv;
    private RelativeLayout dd_sp_rl;
    private TextView dd_title_sp;
    private TextView dd_con1_sp;
    private EditText dd_con1_et_sp;
    private TextView dd_con2_tv_sp;
    private EditText dd_report_et;
    private Button saveBtn;


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

        yd_all_rl = (RelativeLayout) view.findViewById(R.id.agency_res_amend_rl);
        yd_title_tv = (TextView) view.findViewById(R.id.agency_res_amend_rl_yd_title);
        yd_con1_tv = (TextView) view.findViewById(R.id.agency_res_amend_rl_con1);
        yd_con2_tv = (TextView) view.findViewById(R.id.agency_res_amend_rl_con2);
        yd_statue_tv = (TextView) view.findViewById(R.id.agency_res_amend_rl_statue);

        dd_head_ll = (LinearLayout) view.findViewById(R.id.agency_res_amend_rl_ll_head);

        dd_all_rl = (RelativeLayout) view.findViewById(R.id.agency_res_amend_rl_dd_head);

        dd_et_rl = (RelativeLayout) view.findViewById(R.id.agency_res_amend_rl_dd_et);
        dd_title_tv = (TextView) view.findViewById(R.id.agency_res_amend_rl_dd_title_et);
        dd_con1_et = (EditText) view.findViewById(R.id.agency_res_amend_rl_dd_con1_et);
        dd_con2_tv = (TextView) view.findViewById(R.id.agency_res_amend_rl_con2_et);

        dd_sp_rl = (RelativeLayout) view.findViewById(R.id.agency_res_amend_rl_dd_sp);
        dd_title_sp = (TextView) view.findViewById(R.id.agency_res_amend_rl_dd_title_sp);
        dd_con1_sp = (TextView) view.findViewById(R.id.agency_res_amend_rl_dd_con1_sp);
        dd_con1_et_sp = (EditText) view.findViewById(R.id.agency_res_amend_rl_dd_con1_et_sp);
        dd_con2_tv_sp = (TextView) view.findViewById(R.id.agency_res_amend_rl_dd_con2_sp);

        dd_report_et = (EditText) view.findViewById(R.id.agency_res_amend_dd_et_report);

        saveBtn = (Button) view.findViewById(R.id.agency_res_amend_dd_bt_save);
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
            yd_title_tv.setText(R.string.agencykf_agencyname);
            dd_title_tv.setText(R.string.agencykf_agencyname);
            dd_title_sp.setText(R.string.agencykf_agencyname);

            yd_con1_tv.setText(mstAgencyKFM.getAgencyname());
            dd_et_rl.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.CONTACT.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_contact);
            dd_title_tv.setText(R.string.agencykf_contact);
            dd_title_sp.setText(R.string.agencykf_contact);

            yd_con1_tv.setText(mstAgencyKFM.getContact());
            dd_et_rl.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.MOBILE.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_mobile);
            dd_title_tv.setText(R.string.agencykf_mobile);
            dd_title_sp.setText(R.string.agencykf_mobile);

            yd_con1_tv.setText(mstAgencyKFM.getMobile());
            dd_et_rl.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.ADDRESS.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_address);
            dd_title_tv.setText(R.string.agencykf_address);
            dd_title_sp.setText(R.string.agencykf_address);

            yd_con1_tv.setText(mstAgencyKFM.getAddress());
            dd_et_rl.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.AREA.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_area);
            dd_title_tv.setText(R.string.agencykf_area);
            dd_title_sp.setText(R.string.agencykf_area);

            yd_con1_tv.setText(mstAgencyKFM.getArea());
            dd_et_rl.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.MONEY.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_money);
            dd_title_tv.setText(R.string.agencykf_money);
            dd_title_sp.setText(R.string.agencykf_money);

            yd_con1_tv.setText(mstAgencyKFM.getMoney());
            dd_et_rl.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.PERSION.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_persion);
            dd_title_tv.setText(R.string.agencykf_persion);
            dd_title_sp.setText(R.string.agencykf_persion);

            yd_con1_tv.setText(mstAgencyKFM.getPersion());
            dd_et_rl.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.CARNUM.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_carnum);
            dd_title_tv.setText(R.string.agencykf_carnum);
            dd_title_sp.setText(R.string.agencykf_carnum);

            yd_con1_tv.setText(mstAgencyKFM.getCarnum());
            dd_et_rl.setVisibility(View.VISIBLE);
        } else if (DdAgencyContentFragment.ISONE.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_isone);
            dd_title_tv.setText(R.string.agencykf_isone);
            dd_title_sp.setText(R.string.agencykf_isone);

            yd_con1_tv.setText(parseIsone(mstAgencyKFM.getIsone()));

            // 是否数一数二
            dd_sp_rl.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.KFDATA.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_kfdata);
            dd_title_tv.setText(R.string.agencykf_kfdata);
            dd_title_sp.setText(R.string.agencykf_kfdata);

            yd_con1_tv.setText(mstAgencyKFM.getKfdate());

            // 开发时间
            dd_sp_rl.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.PASSDATA.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_passdate);
            dd_title_tv.setText(R.string.agencykf_passdate);
            dd_title_sp.setText(R.string.agencykf_passdate);
            yd_con1_tv.setText(mstAgencyKFM.getPassdate());

            // 达成时间
            dd_sp_rl.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.PRODUCTNAME.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_productname);
            dd_title_tv.setText(R.string.agencykf_productname);
            dd_title_sp.setText(R.string.agencykf_productname);

            yd_con1_tv.setText(mstAgencyKFM.getProductname());
            dd_et_rl.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.BUSINESS.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_business);
            dd_title_tv.setText(R.string.agencykf_business);
            dd_title_sp.setText(R.string.agencykf_business);

            yd_con1_tv.setText(mstAgencyKFM.getBusiness());
            dd_et_rl.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.COVERTERMS.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_coverterms);
            dd_title_tv.setText(R.string.agencykf_coverterms);
            dd_title_sp.setText(R.string.agencykf_coverterms);

            yd_con1_tv.setText(mstAgencyKFM.getCoverterms());
            dd_et_rl.setVisibility(View.VISIBLE);

        } else if (DdAgencyContentFragment.SUPPLYTERMS.equals(type)) {
            yd_title_tv.setText(R.string.agencykf_supplyterms);
            dd_title_tv.setText(R.string.agencykf_supplyterms);
            dd_title_sp.setText(R.string.agencykf_supplyterms);

            yd_con1_tv.setText(mstAgencyKFM.getSupplyterms());
            dd_et_rl.setVisibility(View.VISIBLE);
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
                saveValue();// 保存并上传数据
                break;
            default:
                break;
        }
    }



    // 保存并上传数据
    private void saveValue() {

        String dd_et = dd_con1_et.getText().toString();// 督导填写的内容
        String dd_tv = dd_con1_sp.getText().toString();// 督导选择的内容

        if (DdAgencyContentFragment.AGENCYNAME.equals(type)) {// 经销商名称

            mstAgencyKFM.setAgencyname(dd_et);

        } else if (DdAgencyContentFragment.CONTACT.equals(type)) {// 法定人
            mstAgencyKFM.setContact(dd_et);

        } else if (DdAgencyContentFragment.MOBILE.equals(type)) {// 电话
            mstAgencyKFM.setMobile(dd_et);

        } else if (DdAgencyContentFragment.ADDRESS.equals(type)) {// 仓库地址
            mstAgencyKFM.setAddress(dd_et);

        } else if (DdAgencyContentFragment.AREA.equals(type)) {// 仓库面积
            mstAgencyKFM.setArea(dd_et);

        } else if (DdAgencyContentFragment.MONEY.equals(type)) {// 资金
            mstAgencyKFM.setMoney(dd_et);

        } else if (DdAgencyContentFragment.PERSION.equals(type)) {// 人员
            mstAgencyKFM.setPersion(dd_et);

        } else if (DdAgencyContentFragment.CARNUM.equals(type)) {// 车辆
            mstAgencyKFM.setCarnum(dd_et);

        } else if (DdAgencyContentFragment.ISONE.equals(type)) {// 是否数一数二经销商
            // mstAgencyKFM.setIsone(dd_et);

        } else if (DdAgencyContentFragment.KFDATA.equals(type)) {// 开发时间
            mstAgencyKFM.setKfdate(dd_tv);

        } else if (DdAgencyContentFragment.PASSDATA.equals(type)) {// 达成时间
            mstAgencyKFM.setPassdate(dd_tv);

        } else if (DdAgencyContentFragment.PRODUCTNAME.equals(type)) {// 销售产品
            mstAgencyKFM.setProductname(dd_et);

        } else if (DdAgencyContentFragment.BUSINESS.equals(type)) {// 经营状况
            mstAgencyKFM.setBusiness(dd_et);

        } else if (DdAgencyContentFragment.COVERTERMS.equals(type)) {// 覆盖终端
            mstAgencyKFM.setCoverterms(dd_et);

        } else if (DdAgencyContentFragment.SUPPLYTERMS.equals(type)) {// 直供终端
            mstAgencyKFM.setSupplyterms(dd_et);
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
}
