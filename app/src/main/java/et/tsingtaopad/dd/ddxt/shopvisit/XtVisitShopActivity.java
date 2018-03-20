package et.tsingtaopad.dd.ddxt.shopvisit;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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




    private AppCompatTextView mDataTv;
    private AppCompatTextView mDayTv;
    private AppCompatTextView mTextTv;
    private FrameLayout mContentFl;
    private AppCompatImageView mMemoImg;

    private PopupWindow mPopWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xtvisitshop);
        initView();
        initData();
    }

    private void initData() {
        //titleTv.setText("超级门店2号店");
        titleTv.setText("打招呼");
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
        titleTv.setOnClickListener(this);


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
            case R.id.top_navigation_tv_title:
                showPopupWindow();
                break;

            case R.id.pop_sayhi:
                changeFragment(new XtSayhiFragment(),"xtsayhifragment");
                mPopWindow.dismiss();
                titleTv.setText("打招呼");
                break;
            case R.id.pop_invoicing:
                changeFragment(new XtInvoicingFragment(),"xtinvoicingfragment");
                mPopWindow.dismiss();
                titleTv.setText("进销存");
                break;
            case R.id.pop_checkindex:
                changeFragment(new XtCheckIndexFragment(),"xtcheckindexfragment");
                mPopWindow.dismiss();
                titleTv.setText("查指标");
                break;
            case R.id.pop_chatvie:
                changeFragment(new XtChatvieFragment(),"xtchatviefragment");
                mPopWindow.dismiss();
                titleTv.setText("聊竞品");
                break;
            case R.id.pop_camera:
                changeFragment(new XtCameraFragment(),"xtcamerafragment");
                mPopWindow.dismiss();
                titleTv.setText("拍照");
                break;
            default:
                break;
        }
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(XtVisitShopActivity.this).inflate(R.layout.popuplayout_showasdropdown, null);
        mPopWindow = new PopupWindow(contentView);
        mPopWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置外部可点击消失
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());

        // 设置可获取焦点
        mPopWindow.setFocusable(true);

        RelativeLayout tv1 = (RelativeLayout)contentView.findViewById(R.id.pop_sayhi);
        RelativeLayout tv2 = (RelativeLayout)contentView.findViewById(R.id.pop_invoicing);
        RelativeLayout tv3 = (RelativeLayout)contentView.findViewById(R.id.pop_checkindex);
        RelativeLayout tv4 = (RelativeLayout)contentView.findViewById(R.id.pop_chatvie);
        RelativeLayout tv5 = (RelativeLayout)contentView.findViewById(R.id.pop_camera);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);

        // showAsDropDown 相对某个控件的位置 弹出popupwindow
        // showAsDropDown(View anchor, int xoff, int yoff)：来添加相对x轴和y轴的位移量
        mPopWindow.showAsDropDown(titleTv);

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

    //
    public void changeXtvisitFragment(BaseFragmentSupport fragment, String tag) {
        // 获取Fragment管理者
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // 开启事物
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        // 当前要添加的Fragment需要放的容器位置
        // 要替换的fragment
        // 标记
        beginTransaction.replace(R.id.xtvisit_container, fragment, tag);
        beginTransaction.addToBackStack(null);
        // 提交事物
        beginTransaction.commit();
    }

}
