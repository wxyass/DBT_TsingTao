package et.tsingtaopad.home.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.ref.SoftReference;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseActivity;
import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.net.domain.ResponseStructBean;
import et.tsingtaopad.core.ui.loader.LatteLoader;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.fragmentback.HandleBackUtil;
import et.tsingtaopad.home.app.MainActivity;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.login.domain.BsVisitEmpolyeeStc;
import et.tsingtaopad.util.requestHeadUtil;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText uidEt;
    private EditText pwdEt;
    private Button loginBt;
    private Button exitBt;
    private LinearLayout login_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dd_login);

        // 应用打开时,创建或更新数据库
        new DatabaseHelper(LoginActivity.this).getWritableDatabase();

        // 测试SharedPreferences
        PrefUtils.putString(getApplicationContext(), "ceshi", "ceshi");
        this.initView();
        this.initData();
    }

    private void initData() {

        handler = new MyHandler(this);
    }

    private void initView() {

        // 绑定界面组件
        uidEt = (EditText) findViewById(R.id.login_dd_et_uid);
        pwdEt = (EditText) findViewById(R.id.login_dd_et_pwd);
        loginBt = (Button) findViewById(R.id.login_dd_bt_submit);
        exitBt = (Button) findViewById(R.id.login_dd_bt_cancel);
        login_container = (LinearLayout) findViewById(R.id.login_container);

        // 绑定事件
        loginBt.setOnClickListener(LoginActivity.this);
        exitBt.setOnClickListener(LoginActivity.this);

    }

    @Override
    public void onClick(View v) {
        if (ViewUtil.isDoubleClick(v.getId(), 2500))
            return;

        int i = v.getId();
        // 登录
        if (i == R.id.login_dd_bt_submit) {
            LatteLoader.showLoading(LoginActivity.this);// 处理数据中  ,在LoginActivity的handleMessage中关闭
            try {
                this.loginIn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 取消
        } else if (i == R.id.login_dd_bt_cancel) {
            System.exit(0);
        } else {
        }
    }

    /**
     * 登录
     */
    private void loginIn() {

        String name = uidEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        String loginjson = "{usercode:'" + name + "', " +
                "password:'" + pwd + "'," +
                "version:'" + DbtLog.getVersion() + "'," +
                "logindate:'" + DateUtil.getDateTimeStr(8) + "'," +
                "padid:'" + android.os.Build.SERIAL + "'}";
        toLogin("opt_get_login", name, pwd, loginjson);
    }

    /**
     * 登录接口
     *
     * @param optcode  请求码
     * @param username
     * @param pwd
     * @param content  请求json
     */
    void toLogin(final String optcode, String username, String pwd, String content) {

        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(LoginActivity.this);
        if ("opt_get_login".equals(optcode)) {
            requestHeadStc.setUsercode(username);
            requestHeadStc.setPassword(pwd);
        }
        requestHeadStc.setOptcode(PropertiesUtil.getProperties(optcode));
        RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, content);

        // 压缩请求数据
        String jsonZip = HttpParseJson.parseRequestJson(reqObj);

        RestClient.builder()
                .url(HttpUrl.IP_END)
                .params("data", jsonZip)
                // .loader(LoginActivity.this)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        String json = HttpParseJson.parseJsonResToString(response);
                        ResponseStructBean resObj = new ResponseStructBean();
                        resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                        // 保存登录信息
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                            // 保存信息
                            String formjson = resObj.getResBody().getContent();
                            parseLoginJson(formjson);
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            handler.sendEmptyMessage(ConstValues.WAIT0);

                        } else {
                            Toast.makeText(LoginActivity.this, resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                            LatteLoader.stopLoading();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        LatteLoader.stopLoading();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        LatteLoader.stopLoading();
                    }
                })
                .builde()
                .post();
    }

    // 解析登录者信息
    void parseLoginJson(String json) {

        // 保存登录者信息
        BsVisitEmpolyeeStc emp = JsonUtil.parseJson(json, BsVisitEmpolyeeStc.class);

        if (emp == null) {
            Toast.makeText(LoginActivity.this, getString(R.string.login_msg_usererror), Toast.LENGTH_SHORT).show();
        } else {
            // 服务器时间与pad端时间差
            long timeDiff = Math.abs(System.currentTimeMillis() - DateUtil.parse(emp.getLoginDate(), "yyyy-MM-dd HH:mm:ss").getTime());
            String usercode = PrefUtils.getString(LoginActivity.this, "usercode", "");
            // 校验用户的定格是否一致
            if (!CheckUtil.isBlankOrNull(usercode) && !usercode.equals(emp.getUsercode())) {
                Toast.makeText(LoginActivity.this, getString(R.string.login_msg_invalusercode), Toast.LENGTH_SHORT).show();
            } else if (timeDiff > 5 * 60000) {
                Toast.makeText(LoginActivity.this, getString(R.string.login_msg_invaldate), Toast.LENGTH_SHORT).show();
            } else {
                saveLoginSession(emp, "a1234567", "");
            }
        }
    }

    /**
     * 记录登录者信息
     *
     * @param emp 登录成功后，返回的当前用户信息
     */
    private void saveLoginSession(BsVisitEmpolyeeStc emp, String pwd, String padId) {
        if (emp != null) {
            PrefUtils.putString(LoginActivity.this, "bigareaid", emp.getBigareaid());// 1-48L5
            PrefUtils.putString(LoginActivity.this, "departmentid", emp.getDepartmentid());//1-4ASL  // 大区id
            PrefUtils.putString(LoginActivity.this, "isDel", emp.getIsDel());// 0
            PrefUtils.putString(LoginActivity.this, "isrepassword", emp.getIsrepassword());//91
            PrefUtils.putString(LoginActivity.this, "loginDate", emp.getLoginDate());// 2018-04-08 14:22:23
            PrefUtils.putString(LoginActivity.this, "pDiscs", emp.getpDiscs());// 1-4ASL,1-48L5,1-47BW,1-39CR,0-R9NH
            PrefUtils.putString(LoginActivity.this, "positionid", emp.getPositionid());// 55ED98C5C2114282AD2A857AB05A73E2
            PrefUtils.putString(LoginActivity.this, "secareaid", emp.getSecareaid());// 1-4ASL
            PrefUtils.putString(LoginActivity.this, "status", emp.getStatus());// 1
            PrefUtils.putString(LoginActivity.this, "usercode", emp.getUsercode());// 50000
            PrefUtils.putString(LoginActivity.this, "userid", emp.getUserid());// 19b1ded5-f853-48ab-aa2b-b12e963c8f9b
            PrefUtils.putString(LoginActivity.this, "username", emp.getUsername());//督导菲菲
            PrefUtils.putString(LoginActivity.this, "userPwd", pwd);//a1234567
        }
    }

    // 跳转到主界面,,并关闭本界面
    private void startDbtAty() {
        Intent dbt = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(dbt);
        LoginActivity.this.finish();
    }


    // 监听返回键
    @Override
    public void onBackPressed() {
        // 确定返回上一界面  先检查栈中的fragment是否监听了返回键
        if (!HandleBackUtil.handleBackPress(this)) {
            // 获取Fragment管理者
            FragmentManager fragmentManager = getSupportFragmentManager();
            // 获取当前回退栈中的Fragment个数
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            if (backStackEntryCount > 1) {
                // 立即回退一步
                fragmentManager.popBackStackImmediate();
            } else {
                finish();
            }
        }
    }

    MyHandler handler;

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<LoginActivity> fragmentRef;

        public MyHandler(LoginActivity fragment) {
            fragmentRef = new SoftReference<LoginActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }


            // 处理UI 变化
            switch (msg.what) {
                case ConstValues.WAIT0://  登录成功
                    fragment.startDbtAty();
                    LatteLoader.stopLoading();
                    break;
                case GlobalValues.SINGLE_UP_SUC://
                    break;
                case GlobalValues.SINGLE_UP_FAIL://
                    break;

            }
        }
    }
}
