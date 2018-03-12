package et.tsingtaopad.home.delete;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.home.initadapter.MyFragmentPagerAdapter;
import et.tsingtaopad.main.home.HomeCoreFragment;
import et.tsingtaopad.main.operation.OperationCoreFragment;
import et.tsingtaopad.main.system.SystemCoreFragment;
import et.tsingtaopad.main.visit.VisitCoreFragment;


/**
 * 废弃 (没有找到跳转的方法)
 * 原生Fragment写的
 * Created by yangwenmin on 2017/12/11.
 */

public class DbtCoreFragment extends BaseCoreFragment {


    public static final String TAG = "DbtCoreFragment";

    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private RadioButton homeRb, visitRb, operationRb, systemRb;


    public static DbtCoreFragment newInstance() {

        Bundle args = new Bundle();

        DbtCoreFragment fragment = new DbtCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dbt, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG,"initView");
        /**
         * RadioGroup部分
         */
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        homeRb = (RadioButton) view.findViewById(R.id.rb_chat);
        visitRb = (RadioButton) view.findViewById(R.id.rb_contacts);
        operationRb = (RadioButton) view.findViewById(R.id.rb_discovery);
        systemRb = (RadioButton) view.findViewById(R.id.rb_me);
        //RadioGroup选中状态改变监听
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_chat) {
                    // setCurrentItem第二个参数控制页面切换动画  true:打开/false:关闭
                    viewPager.setCurrentItem(0, false);
                } else if (checkedId == R.id.rb_contacts) {
                    viewPager.setCurrentItem(1, false);
                } else if (checkedId == R.id.rb_discovery) {
                    viewPager.setCurrentItem(2, false);
                } else if (checkedId == R.id.rb_me) {
                    viewPager.setCurrentItem(3, false);
                }
            }
        });

        /**
         * ViewPager部分
         */
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        HomeCoreFragment homeFragment = new HomeCoreFragment();
        VisitCoreFragment visitFragment = new VisitCoreFragment();
        OperationCoreFragment operationFragment = new OperationCoreFragment();
        SystemCoreFragment systemFragment = new SystemCoreFragment();
        List<Fragment> alFragment = new ArrayList<Fragment>();
        alFragment.add(homeFragment);
        alFragment.add(visitFragment);
        alFragment.add(operationFragment);
        alFragment.add(systemFragment);





        //ViewPager设置适配器
        viewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), alFragment));
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);
        //ViewPager页面切换监听
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.rb_chat);
                        break;
                    case 1:
                        radioGroup.check(R.id.rb_contacts);
                        break;
                    case 2:
                        radioGroup.check(R.id.rb_discovery);
                        break;
                    case 3:
                        radioGroup.check(R.id.rb_me);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(BaseCoreFragment targetFragment) {
        getSupportDelegate().start(targetFragment);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        //start(HomeCoreFragment.newInstance());
    }
}
