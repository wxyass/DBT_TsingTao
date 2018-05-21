package et.tsingtaopad.business.operation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.dd.ddaddterm.DdAddTermFragment;
import et.tsingtaopad.dd.ddagencycheck.DdAgencyCheckSelectFragment;
import et.tsingtaopad.dd.ddagencyres.DdAgencySelectFragment;
import et.tsingtaopad.dd.ddxt.term.cart.XtTermCartFragment;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectFragment;
import et.tsingtaopad.dd.ddzs.zsterm.zscart.ZsTermCartFragment;
import et.tsingtaopad.dd.ddzs.zsterm.zsselect.ZsTermSelectFragment;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class OperationFragment extends BaseFragmentSupport implements View.OnClickListener{

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    LinearLayout weekPlan;
    RelativeLayout summaryBtn;
    RelativeLayout dealBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_operation, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){

        backBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_tv_title);
        backBtn.setVisibility(View.INVISIBLE);
        confirmBtn.setVisibility(View.INVISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        weekPlan = (LinearLayout)view.findViewById(R.id.dd_operation_btn_plan);
        summaryBtn = (RelativeLayout)view.findViewById(R.id.dd_operation_btn_summer);
        dealBtn = (RelativeLayout)view.findViewById(R.id.dd_operation_btn_zhenggai);

        weekPlan.setOnClickListener(this);
        summaryBtn.setOnClickListener(this);
        dealBtn.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("运营管理");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dd_operation_btn_plan:
                changeHomeFragment(new DdAgencySelectFragment(), "ddagencyselectfragment");
                break;
            case R.id.dd_operation_btn_summer:
                changeHomeFragment(new DdAgencyCheckSelectFragment(), "ddagencycheckselectfragment");
                break;
            case R.id.dd_operation_btn_zhenggai:
                changeHomeFragment(new DdAddTermFragment(), "ddaddtermfragment");
                break;
        }

    }

}
