package et.tsingtaopad.dd.ddxt.shopvisit;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseActivity;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.dd.ddxt.camera.XtCameraFragment;
import et.tsingtaopad.dd.ddxt.chatvie.XtChatvieFragment;
import et.tsingtaopad.dd.ddxt.checking.XtCheckIndexFragment;
import et.tsingtaopad.dd.ddxt.invoicing.XtInvoicingFragment;
import et.tsingtaopad.dd.ddxt.sayhi.XtSayhiFragment;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtVisitShopActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    private AppCompatButton mSayhiBtn;
    private AppCompatButton mInvoicingBtn;
    private AppCompatButton mCheckindexBtn;
    private AppCompatButton mChatviewBtn;
    private AppCompatButton mCameraBtn;


    private AppCompatTextView mDataTv;
    private AppCompatTextView mDayTv;
    private AppCompatTextView mTextTv;
    private FrameLayout mContentFl;
    private AppCompatImageView mMemoImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xtvisitshop);
        initView();
        initData();
    }

    private void initData() {
        titleTv.setText("超级门店2号店");
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

        mSayhiBtn = (AppCompatButton) findViewById(R.id.xtvisit_btn_sayhi);
        mInvoicingBtn = (AppCompatButton) findViewById(R.id.xtvisit_btn_invoicing);
        mCheckindexBtn = (AppCompatButton) findViewById(R.id.xtvisit_btn_checkindex);
        mChatviewBtn = (AppCompatButton) findViewById(R.id.xtvisit_btn_chatvie);
        mCameraBtn = (AppCompatButton) findViewById(R.id.xtvisit_btn_camera);
        mSayhiBtn.setOnClickListener(this);
        mInvoicingBtn.setOnClickListener(this);
        mCheckindexBtn.setOnClickListener(this);
        mChatviewBtn.setOnClickListener(this);
        mCameraBtn.setOnClickListener(this);


        mDataTv = (AppCompatTextView) findViewById(R.id.xtvisit_tv_date);
        mDayTv = (AppCompatTextView) findViewById(R.id.xtvisit_tv_day);
        mTextTv = (AppCompatTextView) findViewById(R.id.xtvisit_tv_textView2);
        mContentFl = (FrameLayout) findViewById(R.id.xtvisit_fl_content);
        mMemoImg = (AppCompatImageView) findViewById(R.id.xtvisit_bt_memo);

        changeFragment(new XtSayhiFragment(),"xtsayhifragment");
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

            case R.id.xtvisit_btn_sayhi:
                changeFragment(new XtSayhiFragment(),"xtsayhifragment");
                break;
            case R.id.xtvisit_btn_invoicing:
                changeFragment(new XtInvoicingFragment(),"xtinvoicingfragment");
                break;
            case R.id.xtvisit_btn_checkindex:
                changeFragment(new XtCheckIndexFragment(),"xtcheckindexfragment");
                break;
            case R.id.xtvisit_btn_chatvie:
                changeFragment(new XtChatvieFragment(),"xtchatviefragment");
                break;
            case R.id.xtvisit_btn_camera:
                changeFragment(new XtCameraFragment(),"xtcamerafragment");
                break;
            default:
                break;
        }
    }

    public void changeFragment(BaseFragmentSupport fragment, String tag) {
        // 获取Fragment管理者
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // 开启事物
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        // 当前要添加的Fragment需要放的容器位置
        // 要替换的fragment
        // 标记
        beginTransaction.replace(R.id.xtvisit_fl_content, fragment, tag);
        // 提交事物
        beginTransaction.commit();
    }

}
