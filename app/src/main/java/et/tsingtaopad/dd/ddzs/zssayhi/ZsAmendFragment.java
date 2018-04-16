package et.tsingtaopad.dd.ddzs.zssayhi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.dd.ddxt.checking.XtCheckIndexFragment;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.checking.num.XtCalculateIndexItemPuhuoAdapter;

/**
 * Created by yangwenmin on 2018/3/12.
 * <p>
 * 打招呼 录入正确数据
 */

public class ZsAmendFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;
    ZsSayhiFragment.MyHandler handler;

    private Button sureBtn;


    public ZsAmendFragment() {

    }

    @SuppressLint("ValidFragment")
    public ZsAmendFragment(ZsSayhiFragment.MyHandler handler) {
        this.handler = handler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_sayhi_amend, container, false);
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
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        sureBtn = (Button) view.findViewById(R.id.zdzs_sayhi_bt_save);
        sureBtn.setOnClickListener(this);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("录入正确数据");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_tv_title:// 标题
                break;
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();// 取消
                break;

            case R.id.zdzs_sayhi_bt_save:// 确定
                //saveValue(v);
                supportFragmentManager.popBackStack();
                break;

            default:
                break;
        }
    }

    private void saveValue(View v) {

    }
}
