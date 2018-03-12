package et.tsingtaopad.main.system.notice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class NoticeCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "ShopVisitCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private ImageButton imgNotice,imgToday;

    public NoticeCoreFragment() {
    }

    public static NoticeCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        NoticeCoreFragment fragment = new NoticeCoreFragment();
        return fragment;
    }

    public static NoticeCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        NoticeCoreFragment fragment = new NoticeCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // 在这个方法中 只处理从上个Fragment传递过来的数据,若没数据传递过来,可删除该方法
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            //termname = bundle.getString(TERM_NAME);
            //termkey = bundle.getString(TERM_KEY);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DbtLog.logUtils(TAG, "onActivityCreated");

    }

    // 只用来初始化控件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.business_notice_test, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (AppCompatTextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        imgNotice = (ImageButton)view.findViewById(R.id.notice_bt_noticeicon);
        imgToday = (ImageButton) view.findViewById(R.id.notice_bt_todaythingicon);

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
        imgNotice.setOnClickListener(this);
        imgToday.setOnClickListener(this);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        titleTv.setText(R.string.business_notice);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG,"onSupportVisible");
        // 每次页面出现后都重新加载数据
        initData();
    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG,"initData");

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
        }else if(i == R.id.notice_bt_noticeicon){//通知公告
            titleTv.setText(R.string.business_notice);
            //translate(noticeFragment);
            imgNotice.setBackgroundResource(R.drawable.bg_notice_icon);
            imgToday.setBackgroundResource(R.drawable.bg_todaything_hit);

        }else if(i == R.id.notice_bt_todaythingicon){//今日要事
            titleTv.setText(R.string.business_todaything);
            //translate(todayFragment);
            imgNotice.setBackgroundResource(R.drawable.bg_notice_hit);
            imgToday.setBackgroundResource(R.drawable.bg_todaything_icon);

        }
    }
}
