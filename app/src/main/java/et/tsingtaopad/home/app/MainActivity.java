package et.tsingtaopad.home.app;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseActivity;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.dd.ddxt.sayhi.XtSayhiFragment;
import et.tsingtaopad.dd.ddxt.term.cart.XtTermCartFragment;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectFragment;
import et.tsingtaopad.home.homefragment.MainFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 应用打开时,创建或更新数据库
        new DatabaseHelper(this).getWritableDatabase();

        //一开始进入程序,就往容器中替换Fragment
        //changeFragment(MainFragment.newInstance(), "mainfragment");
        changeFragment(new XtTermCartFragment(), "mainfragment");
        //changeFragment(new XtTermSelectFragment(), "mainfragment");
        //changeFragment(new XtSayhiFragment(), "mainfragment");
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
    @Override
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

    }
}
