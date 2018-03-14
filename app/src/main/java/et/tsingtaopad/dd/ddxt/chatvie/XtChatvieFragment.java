package et.tsingtaopad.dd.ddxt.chatvie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtChatvieFragment extends BaseFragmentSupport implements View.OnClickListener{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_chatvie, container, false);
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
                supportFragmentManager.popBackStack();
                break;

            default:
                break;
        }
    }
}
