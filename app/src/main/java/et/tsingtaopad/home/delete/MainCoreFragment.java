package et.tsingtaopad.home.delete;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.event.TabSelectedEvent;
import et.tsingtaopad.core.ui.bottombar.BottomBar;
import et.tsingtaopad.core.ui.bottombar.BottomBarTab;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.main.home.HomeCoreFragment;
import et.tsingtaopad.main.operation.OperationCoreFragment;
import et.tsingtaopad.main.system.SystemCoreFragment;
import et.tsingtaopad.main.visit.VisitCoreFragment;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

/**
 * Created by yangwenmin on 2017/12/12.
 * 底部导航主Fragment
 * 第三方Fragment写得完
 */

public class MainCoreFragment extends BaseCoreFragment {

    public static final String TAG = "MainCoreFragment";

    private static final int REQ_MSG = 10;

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOUR = 3;

    private BaseCoreFragment[] mFragments = new BaseCoreFragment[4];

    private BottomBar mBottomBar;


    public static MainCoreFragment newInstance() {
        DbtLog.logUtils(TAG,"newInstance()");
        Bundle args = new Bundle();

        MainCoreFragment fragment = new MainCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        mBottomBar = (BottomBar) view.findViewById(R.id.bottomBar);

        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.drawable.bt_platform_home_down, getString(R.string.platform_home)))
                .addItem(new BottomBarTab(_mActivity, R.drawable.bt_platform_visit_down, getString(R.string.platform_visit)))
                .addItem(new BottomBarTab(_mActivity, R.drawable.bt_platform_operation_down, getString(R.string.platform_operation)))
                .addItem(new BottomBarTab(_mActivity, R.drawable.bt_platform_syseting_down, getString(R.string.platform_system)));

        // 模拟未读消息
        //mBottomBar.getItem(FIRST).setUnreadCount(9);

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                getSupportDelegate().showHideFragment(mFragments[position], mFragments[prePosition]);

                /*BottomBarTab tab = mBottomBar.getItem(FIRST);
                if (position == FIRST) {
                    tab.setUnreadCount(0);
                } else {
                    tab.setUnreadCount(tab.getUnreadCount() + 1);
                }*/
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DbtLog.logUtils(TAG, "onActivityCreated");
        BaseCoreFragment firstFragment = findChildFragment(HomeCoreFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = HomeCoreFragment.newInstance();
            mFragments[SECOND] = VisitCoreFragment.newInstance();
            mFragments[THIRD] = OperationCoreFragment.newInstance();
            mFragments[FOUR] = SystemCoreFragment.newInstance();

            getSupportDelegate().loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOUR]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(VisitCoreFragment.class);
            mFragments[THIRD] = findChildFragment(OperationCoreFragment.class);
            mFragments[FOUR] = findChildFragment(SystemCoreFragment.class);
        }
    }



    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        DbtLog.logUtils(TAG, "onFragmentResult");
        if (requestCode == REQ_MSG && resultCode == RESULT_OK) {

        }
    }

    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(BaseCoreFragment targetFragment) {
        DbtLog.logUtils(TAG,"startBrotherFragment");
        start(targetFragment);
    }

    /**
     * start other BrotherFragment
     * 新Fragment为透明,能看到旧Fragment
     */
    public void startBrotherFragmentDontHideSelf(BaseCoreFragment targetFragment) {
        DbtLog.logUtils(TAG,"startBrotherFragment");
        extraTransaction().startDontHideSelf(targetFragment);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG,"onLazyInitView");
        // 设置竖屏
        getSupportDelegate().getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
