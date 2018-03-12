package et.tsingtaopad.main.system.version;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class UpdateCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "UpdateCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private TextView versionTv;
    private Button checkVersionBt;
    private VersionService service;

    public UpdateCoreFragment() {
    }

    public static UpdateCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        UpdateCoreFragment fragment = new UpdateCoreFragment();
        return fragment;
    }

    public static UpdateCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        UpdateCoreFragment fragment = new UpdateCoreFragment();
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
        View view = inflater.inflate(R.layout.system_setting_upload, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (AppCompatTextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        versionTv = (TextView) view.findViewById(R.id.syssetting_tv_update);
        checkVersionBt = (Button) view.findViewById(R.id.syssetting_btn_check);

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
        checkVersionBt.setOnClickListener(this);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        titleTv.setText("检查更新");
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
        service = new VersionService(getActivity(), true, true);
        versionTv.setText(service.getVersion());

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
        // 检查更新
        }else if(i == R.id.syssetting_btn_check){
            service.checkVersion();
        }
    }
}
