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

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class VisitFragment extends BaseFragmentSupport implements View.OnClickListener{
    AppCompatButton button;
    AppCompatButton xtTermBtn;
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
        button.setOnClickListener(this);
        xtTermBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_test:
                changeHomeFragment(new XtTermSelectFragment(), "xttermlistfragment");
                break;
            case R.id.btn_xt_term:
                changeHomeFragment(new XtTermCartFragment(), "xttermlistfragment");
                break;
        }

    }

}
