package et.tsingtaopad.home.delete;

import android.os.Bundle;
import android.support.annotation.Nullable;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreActivity;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.home.delete.DbtCoreFragment;
import et.tsingtaopad.home.delete.MainCoreFragment;

import et.tsingtaopad.initconstvalues.InitConstValues;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by yangwenmin on 2017/12/6.
 * 底部导航主Activity
 */
public class DbtCoreActivity extends BaseCoreActivity {

    public static final String TAG = "DbtCoreActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG,"onCreate");
        setContentView(R.layout.activity_dbt);

        // 应用打开时,创建或更新数据库
        new DatabaseHelper(this).getWritableDatabase();
        // 登录后的处理操作
        dealOther();
        // 测试SharedPreferences
        PrefUtils.putString(getApplicationContext(),"1","1");
        // 加载MainFragment
        if (findFragment(DbtCoreFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainCoreFragment.newInstance());
        }
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
        DbtLog.logUtils(TAG,"onBackPressedSupport");
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        DbtLog.logUtils(TAG,"onCreateFragmentAnimator");
        // 设置横向(和安卓4.x动画相同)
        // return new DefaultHorizontalAnimator();
        // 设置无动画
        return new DefaultNoAnimator();
        // 设置自定义动画
        // return new FragmentAnimator(enter,exit,popEnter,popExit);
    }

    /**
     * 登录后的处理操作
     */
    private void dealOther() {
        DbtLog.logUtils(TAG,"dealOther");
        // 初始化静态变量
        new InitConstValues(getApplicationContext()).start();
    }


}
