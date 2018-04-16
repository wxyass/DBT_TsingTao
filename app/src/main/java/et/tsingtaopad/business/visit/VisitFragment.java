package et.tsingtaopad.business.visit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        button = view.findViewById(R.id.btn_test);
        xtTermBtn = view.findViewById(R.id.btn_xt_term);
        zdzsBtn = view.findViewById(R.id.btn_zdzs);
        zsTermBtn = view.findViewById(R.id.btn_zs_term);
        button.setOnClickListener(this);
        xtTermBtn.setOnClickListener(this);
        zdzsBtn.setOnClickListener(this);
        zsTermBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_test:
                changeHomeFragment(new XtTermSelectFragment(), "xttermlistfragment");
                break;
            case R.id.btn_xt_term:
                Bundle bundle = new Bundle();
                bundle.putSerializable("fromFragment", "VisitFragment");
                XtTermCartFragment xtTermCartFragment = new XtTermCartFragment();
                xtTermCartFragment.setArguments(bundle);
                changeHomeFragment(xtTermCartFragment, "xttermcartfragment");
                break;
            case R.id.btn_zdzs:
                changeHomeFragment(new ZsTermSelectFragment(), "zstermselectfragment");
                break;
            case R.id.btn_zs_term:
                Bundle zsBundle = new Bundle();
                zsBundle.putSerializable("fromFragment", "VisitFragment");
                ZsTermCartFragment zsTermCartFragment = new ZsTermCartFragment();
                zsTermCartFragment.setArguments(zsBundle);
                changeHomeFragment(zsTermCartFragment, "zstermcartfragment");
                break;
        }

    }

}
