package et.tsingtaopad.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreActivity;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.home.app.MainActivity;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.InitConstValues;
import et.tsingtaopad.login.domain.LoginService;
import et.tsingtaopad.login.domain.LoginSession;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by yangwenmin on 2017/12/6.
 * 登录
 */
public class LoginCoreActivity extends BaseCoreActivity implements View.OnClickListener {

    public static final String TAG = "DbtCoreActivity";

    private LoginService service;

    private EditText uidEt;
    private EditText pwdEt;
    private Button loginBt;
    private Button exitBt;
    private AlertDialog dialog;
    private LinearLayout login_container;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            GlobalValues.isDayThingWarn = true;

            super.handleMessage(msg);
            switch (msg.what) {

                // 提示信息
                case ConstValues.WAIT1:
                    //Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    boolean isSuccess = bundle.getBoolean("isSuccess", false);
                    if (isSuccess) {
                        // 登录成功
                        String msg1 = bundle.getString("msg", "登录");//登录信息
                        String isrepassword = bundle.getString("isrepassword", "111111");//还有多少天需更改密码
                        String userPwd = PrefUtils.getString(getApplicationContext(), "userPwd", "");
                        // 1 原始密码-->需修改
                        if ("a1234567".equals(userPwd)) {
                            Toast.makeText(getBaseContext(), "请先修改原始密码", Toast.LENGTH_SHORT).show();
                            startRepasswordAty();
                        }
                        // 2 密码不是8位-->需修改-->跳到修改密码界面
                        else if (userPwd.length() < 8) {
                            Toast.makeText(getBaseContext(), "密码少于8位,请先修改密码", Toast.LENGTH_SHORT).show();
                            startRepasswordAty();
                        }
                        // 3 密码不是数字加英文组合-->需修改-->跳到修改密码界面
                        else if (!userPwd.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$")) {
                            Toast.makeText(getBaseContext(), "密码不是数字加英文组合,请先修改密码", Toast.LENGTH_SHORT).show();
                            startRepasswordAty();
                        }
                        // 4 正常
                        else if (Integer.parseInt(isrepassword) >= 10) {
                            Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                            startDbtAty();
                        }
                        // 5 正常 但密码还有10天到期
                        else if (Integer.parseInt(isrepassword) > 0 && Integer.parseInt(isrepassword) < 10) {
                            Toast.makeText(getBaseContext(), "登录成功,密码还有" + isrepassword + "天到期,请及时修改", Toast.LENGTH_LONG).show();
                            startDbtAty();
                        }
                        // 6 密码已超过3个月 -->需修改
                        else {
                            Toast.makeText(getBaseContext(), "密码需要最少3个月换一次", Toast.LENGTH_SHORT).show();
                            startRepasswordAty();
                        }
                    } else { //
                        // 登录失败 提示信息
                        Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    break;

                // 离线登录 提示信息
                case ConstValues.WAIT2:
                    //Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    boolean noNetSuccess = bundle.getBoolean("isSuccess", false);
                    if (noNetSuccess) {
                        // 离线登录成功
                        Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                        // 跳转
                        startDbtAty();
                    } else {
                        // 登录失败 提示信息
                        Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    // 跳转到修改密码界面,并关闭本界面
    private void startRepasswordAty() {
        Intent intent = new Intent(LoginCoreActivity.this, RepasswordCoreActivity.class);
        startActivity(intent);
        LoginCoreActivity.this.finish();
    }

    // 跳转到主界面,,并关闭本界面
    private void startDbtAty() {
        Intent dbt = new Intent(LoginCoreActivity.this, MainActivity.class);
        startActivity(dbt);
        LoginCoreActivity.this.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        setContentView(R.layout.login_activity);

        // 应用打开时,创建或更新数据库
        new DatabaseHelper(this).getWritableDatabase();

        //
        this.initView();
        this.initData();

    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
        DbtLog.logUtils(TAG, "onBackPressedSupport");
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        DbtLog.logUtils(TAG, "onCreateFragmentAnimator");
        // 设置横向(和安卓4.x动画相同)
        // return new DefaultHorizontalAnimator();
        // 设置无动画
        return new DefaultNoAnimator();
        // 设置自定义动画
        // return new FragmentAnimator(enter,exit,popEnter,popExit);
    }

    private void initView() {

        // 绑定界面组件
        uidEt = (EditText) findViewById(R.id.login_et_uid);
        pwdEt = (EditText) findViewById(R.id.login_et_pwd);
        loginBt = (Button) findViewById(R.id.login_bt_submit);
        exitBt = (Button) findViewById(R.id.login_bt_cancel);
        login_container = (LinearLayout) findViewById(R.id.login_container);

        // 绑定事件
        loginBt.setOnClickListener(this);
        exitBt.setOnClickListener(this);

        uidEt.addTextChangedListener(watcher);
        pwdEt.addTextChangedListener(watcher);
    }

    private void initData() {

        service = new LoginService(this, handler);
        //service.setBackGround(login_container);
        LoginSession loginSession = service.getLoginSession(getApplicationContext());
        uidEt.setText(loginSession.getUserGongHao());
    }

    /**
     * 登录
     */
    private void loginIn() {

        // 弹出进度框
        dialog = new AlertDialog.Builder(this).setCancelable(false).create();
        dialog.setView(getLayoutInflater().inflate(R.layout.login_progress, null), 0, 0, 0, 0);
        dialog.show();
        String version = "V" + DbtLog.getVersion();
        String preUserCode = PrefUtils.getString(this, "userCode", "");
        // 调用登录服务
        service.login(uidEt.getText().toString(), pwdEt.getText().toString(), version, preUserCode);
    }

    /**
     * 登录后的处理操作
     */
    private void dealOther() {
        DbtLog.logUtils(TAG, "dealOther");
        // 初始化静态变量
        new InitConstValues(getApplicationContext()).start();
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        // 登录
        if (i == R.id.login_bt_submit) {
            try {
                this.loginIn();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 取消
        } else if (i == R.id.login_bt_cancel) {
            System.exit(0);
        } else {
        }
    }

    /**
     * 更新登录按钮的状态
     */
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!(CheckUtil.isBlankOrNull(uidEt.getText().toString()) || CheckUtil.isBlankOrNull(pwdEt.getText().toString()))) {
                loginBt.setEnabled(true);
                loginBt.setBackgroundResource(R.drawable.bt_login_submit);
            } else {
                loginBt.setEnabled(false);
                loginBt.setBackgroundResource(R.drawable.bt_login_submit_invalid);
            }
        }
    };
}
