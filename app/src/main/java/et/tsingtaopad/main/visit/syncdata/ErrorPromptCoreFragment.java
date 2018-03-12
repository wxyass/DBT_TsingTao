package et.tsingtaopad.main.visit.syncdata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.NetStatusUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class ErrorPromptCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "ErrorPromptCoreFragment";

    private TextView prompt_content_tv;
    private Button prompt_cancel_bt;
    private Button  prompt_ok_bt;
    private String  prompt_content;

    public ErrorPromptCoreFragment() {
    }

    public static ErrorPromptCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        ErrorPromptCoreFragment fragment = new ErrorPromptCoreFragment();
        return fragment;
    }

    public static ErrorPromptCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        ErrorPromptCoreFragment fragment = new ErrorPromptCoreFragment();
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
            prompt_content = bundle.getString("prompt_content");
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
        View view = inflater.inflate(R.layout.network_error_prompt, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");

        prompt_content_tv=(TextView) view.findViewById(R.id.dialog_prompt_content);
        prompt_cancel_bt=(Button) view.findViewById(R.id.dialog_button_cancel);
        prompt_ok_bt=(Button) view.findViewById(R.id.dialog_button_ok);
        prompt_cancel_bt.setOnClickListener(this);
        prompt_ok_bt.setOnClickListener(this);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
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
        prompt_content_tv.setText(prompt_content);
    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 重新同步
        if (i == R.id.dialog_button_ok) {
            if (NetStatusUtil.isNetValid(getContext())) {
                //getSupportDelegate().startWithPop(ErrorPromptCoreFragment.newInstance(args));
                //((MainCoreFragment) getParentFragment()).startBrotherFragmentDontHideSelf(DownLoadCoreFragment.newInstance());
                pop();
                getSupportDelegate().extraTransaction().startDontHideSelf(DownLoadCoreFragment.newInstance());
                //getSupportDelegate().startWithPop(DownLoadCoreFragment.newInstance());
            } else {
                //提示修改网络
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("网络错误");
                builder.setMessage("请连接好网络再同步数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
                    }
                }).create().show();
            }
        }
        // 取消同步
        else if(i == R.id.dialog_button_cancel){
            pop();
        }
    }
}
