package et.tsingtaopad.main.system.modifypwd;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.main.system.SyssettingService;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class ModifyPwdCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "ShopVisitCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private EditText currentpsdEt;
    private EditText newpsdEt;
    private EditText repeatpsdEt;
    private Button submitBt;
    private SyssettingService service;

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstValues.WAIT1:
                    currentpsdEt.setText("");
                    newpsdEt.setText("");
                    repeatpsdEt.setText("");
                    Toast.makeText(getContext(),"密码修改成功",Toast.LENGTH_SHORT).show();
                    break;
                case ConstValues.WAIT2:
                    Toast.makeText(getContext(),"密码修改失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };

    public ModifyPwdCoreFragment() {
    }

    public static ModifyPwdCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        ModifyPwdCoreFragment fragment = new ModifyPwdCoreFragment();
        return fragment;
    }

    public static ModifyPwdCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        ModifyPwdCoreFragment fragment = new ModifyPwdCoreFragment();
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
        View view = inflater.inflate(R.layout.system_setting_modifypwd, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (AppCompatTextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);


        currentpsdEt = (EditText) view.findViewById(R.id.syssetting_et_currentpsd);
        newpsdEt = (EditText) view.findViewById(R.id.syssetting_et_newpsd);
        repeatpsdEt = (EditText) view.findViewById(R.id.syssetting_et_repeatpsd);
        submitBt = (Button) view.findViewById(R.id.syssetting_btn_submit);


        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
        submitBt.setOnClickListener(this);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        titleTv.setText("修改密码");
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
        service = new SyssettingService(getActivity(),handler);

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
        // 确定
        }else if(i == R.id.banner_navigation_rl_confirm){

        }
        else if(i == R.id.syssetting_btn_submit){//提交修改密码
        service.changePwd(currentpsdEt.getText().toString(),
                newpsdEt.getText().toString(), repeatpsdEt.getText().toString());}
    }
}
