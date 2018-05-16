package et.tsingtaopad.business.visit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.dd.ddaddterm.DdAddTermFragment;
import et.tsingtaopad.dd.ddagencycheck.DdAgencyCheckSelectFragment;
import et.tsingtaopad.dd.ddagencyres.DdAgencySelectFragment;
import et.tsingtaopad.dd.ddxt.term.cart.XtTermCartFragment;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectFragment;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectService;
import et.tsingtaopad.dd.ddzs.zsterm.zscart.ZsTermCartFragment;
import et.tsingtaopad.dd.ddzs.zsterm.zsselect.ZsTermSelectFragment;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class VisitFragment extends BaseFragmentSupport implements View.OnClickListener{

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    LinearLayout button;
    RelativeLayout xtTermBtn;
    LinearLayout zdzsBtn;
    RelativeLayout zsTermBtn;
    RelativeLayout agencyresBtn;
    RelativeLayout agencycheckBtn;
    RelativeLayout addtermBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_visit, container, false);
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

        /*button = view.findViewById(R.id.dd_btn_test);
        xtTermBtn = view.findViewById(R.id.dd_btn_xt_term);
        zdzsBtn = view.findViewById(R.id.dd_btn_zdzs);
        zsTermBtn = view.findViewById(R.id.dd_btn_zs_term);
        agencyresBtn = view.findViewById(R.id.dd_btn_zs_agencyres);
        agencycheckBtn = view.findViewById(R.id.dd_btn_zs_agencycheck);
        addtermBtn = view.findViewById(R.id.dd_btn_zs_addterm);

        button.setOnClickListener(this);
        xtTermBtn.setOnClickListener(this);
        zdzsBtn.setOnClickListener(this);
        zsTermBtn.setOnClickListener(this);
        agencyresBtn.setOnClickListener(this);
        agencycheckBtn.setOnClickListener(this);
        addtermBtn.setOnClickListener(this);*/

        button = (LinearLayout)view.findViewById(R.id.dd_btn_test);
        xtTermBtn = (RelativeLayout)view.findViewById(R.id.dd_btn_xt_term);
        zdzsBtn = (LinearLayout)view.findViewById(R.id.dd_btn_zdzs);
        zsTermBtn = (RelativeLayout)view.findViewById(R.id.dd_btn_zs_term);

        agencyresBtn = (RelativeLayout)view.findViewById(R.id.dd_btn_zs_agencyres);
        agencycheckBtn = (RelativeLayout)view.findViewById(R.id.dd_btn_zs_agencycheck);
        addtermBtn = (RelativeLayout)view.findViewById(R.id.dd_btn_zs_addterm);

        button.setOnClickListener(this);
        xtTermBtn.setOnClickListener(this);
        zdzsBtn.setOnClickListener(this);
        zsTermBtn.setOnClickListener(this);

        agencyresBtn.setOnClickListener(this);
        agencycheckBtn.setOnClickListener(this);
        addtermBtn.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("拜访管理");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dd_btn_test:
                changeHomeFragment(new XtTermSelectFragment(), "xttermlistfragment");
                break;
            case R.id.dd_btn_xt_term:
                Bundle bundle = new Bundle();
                bundle.putSerializable("fromFragment", "VisitFragment");
                XtTermCartFragment xtTermCartFragment = new XtTermCartFragment();
                xtTermCartFragment.setArguments(bundle);
                changeHomeFragment(xtTermCartFragment, "xttermcartfragment");
                break;
            case R.id.dd_btn_zdzs:
                changeHomeFragment(new ZsTermSelectFragment(), "zstermselectfragment");
                break;

            case R.id.dd_btn_zs_term:
                Bundle zsBundle = new Bundle();
                zsBundle.putSerializable("fromFragment", "VisitFragment");
                ZsTermCartFragment zsTermCartFragment = new ZsTermCartFragment();
                zsTermCartFragment.setArguments(zsBundle);
                changeHomeFragment(zsTermCartFragment, "zstermcartfragment");
                break;

            case R.id.dd_btn_zs_agencyres:
                changeHomeFragment(new DdAgencySelectFragment(), "ddagencyselectfragment");
                break;
            case R.id.dd_btn_zs_agencycheck:
                changeHomeFragment(new DdAgencyCheckSelectFragment(), "ddagencycheckselectfragment");
                break;
            case R.id.dd_btn_zs_addterm:
                changeHomeFragment(new DdAddTermFragment(), "ddaddtermfragment");
                break;
        }

    }

}
