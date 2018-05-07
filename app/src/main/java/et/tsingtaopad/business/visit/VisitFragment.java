package et.tsingtaopad.business.visit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class VisitFragment extends BaseFragmentSupport implements View.OnClickListener{
    AppCompatButton button;
    AppCompatButton xtTermBtn;
    AppCompatButton zdzsBtn;
    AppCompatButton zsTermBtn;
    AppCompatButton agencyresBtn;
    AppCompatButton agencycheckBtn;
    AppCompatButton addtermBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_visit, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        button = view.findViewById(R.id.dd_btn_test);
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
        addtermBtn.setOnClickListener(this);
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
