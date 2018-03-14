package et.tsingtaopad.test.dd;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseActivity;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xtvisitshop);
        initView();
    }

    // 初始化控件
    private void initView(){
        backBtn = (RelativeLayout)findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout)findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView)findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView)findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView)findViewById(R.id.top_navigation_tv_title);
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_rl_back:
                this.finish();
                break;
            case R.id.top_navigation_rl_confirm:
                /*Intent intent = new Intent(getActivity(),XtVisitShopActivity.class);
                startActivity(intent);*/
                break;
            default:
                break;
        }
    }

}
