package et.tsingtaopad.home.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import java.lang.ref.SoftReference;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseActivity;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.dd.ddxt.sayhi.XtSayhiFragment;
import et.tsingtaopad.dd.ddxt.term.cart.XtTermCartFragment;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectFragment;
import et.tsingtaopad.dd.ddzs.zsinvoicing.ZsInvoicingFragment;
import et.tsingtaopad.dd.ddzs.zssayhi.ZsSayhiFragment;
import et.tsingtaopad.fragmentback.HandleBackUtil;
import et.tsingtaopad.home.homefragment.MainFragment;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.InitConstValues;
import et.tsingtaopad.main.system.version.VersionService;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 应用打开时,创建或更新数据库
        // new DatabaseHelper(this).getWritableDatabase();

        // 测试SharedPreferences
        PrefUtils.putString(getApplicationContext(),"ceshi","ceshi");

        // 处理主Activity业务
        showFragmentAll();
    }


    // 处理主Activity业务
    private void showFragmentAll(){

        //一开始进入程序,就往容器中替换Fragment
        changeFragment(MainFragment.newInstance(), "mainfragment");
        //changeFragment(new XtTermCartFragment(), "mainfragment");
        //changeFragment(new XtTermSelectFragment(), "mainfragment");
        //changeFragment(new ZsSayhiFragment(), "mainfragment");
        //changeFragment(new ZsInvoicingFragment(), "mainfragment");
        dealOther();
    }

    /**
     * 登录后的处理操作
     */
    private void dealOther() {

        handler = new MyHandler(this);
        ConstValues.handler = handler;

        //启动服务
        Intent service = new Intent(this, AutoUpService.class);
        startService(service);
    }



    public void changeFragment(BaseFragmentSupport fragment, String tag) {
        // 获取Fragment管理者
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // 开启事物
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        // 当前要添加的Fragment需要放的容器位置
        // 要替换的fragment
        // 标记
        beginTransaction.replace(R.id.home_container, fragment, tag);
        beginTransaction.addToBackStack(null);
        // 提交事物
        beginTransaction.commit();
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
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
        return true;
    }*/

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
        SoftReference<MainActivity> fragmentRef;

        public MyHandler(MainActivity fragment) {
            fragmentRef = new SoftReference<MainActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }


            // 处理UI 变化
            switch (msg.what) {
                case ConstValues.WAIT0://  结束上传  刷新本页面
                    //fragment.shuaxinXtTermSelect(0);
                    break;
                case GlobalValues.SINGLE_UP_SUC://  协同拜访上传成功
                    //fragment.shuaxinXtTermSelect(1);
                    break;
                case GlobalValues.SINGLE_UP_FAIL://  协同拜访上传失败
                    //fragment.shuaxinXtTermSelect(2);
                    break;

            }
        }
    }
}
