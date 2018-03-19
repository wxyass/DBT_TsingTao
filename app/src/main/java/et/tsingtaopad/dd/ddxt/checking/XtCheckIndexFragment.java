package et.tsingtaopad.dd.ddxt.checking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.dd.ddxt.checking.num.XtNumInputFragment;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.dd.ddxt.term.cart.XtTermCartFragment;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtCheckIndexFragment extends BaseFragmentSupport implements View.OnClickListener{

    private AppCompatButton mXtvisit_btn_pro;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_checkindex, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view){

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_rl_back:
//                changeXtvisitFragment(new XtNumInputFragment(),"xtnuminputfragment");
                break;

            default:
                break;
        }
    }
}
