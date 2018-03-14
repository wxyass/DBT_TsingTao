package et.tsingtaopad.test.corefragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;

/**
 * 用于界面UI搭建 随时删除
 * Created by yangwenmin on 2017/12/11.
 */

public class TestMidCoreFragment extends BaseCoreFragment implements View.OnClickListener{

    public static final String TAG = "TestMidCoreFragment";
    Button button;

    public TestMidCoreFragment() {
        // Required empty public constructor
    }

    public static TestMidCoreFragment newInstance() {

        Bundle args = new Bundle();

        TestMidCoreFragment fragment = new TestMidCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DbtLog.logUtils(TAG,"onCreateView");

        View view = inflater.inflate(R.layout.fragment_test, null);
        this.initView(view);
        return view;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        // 设置横屏
        getSupportDelegate().getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.initData();
    }

    private void initView(View view) {

        button = (Button)view.findViewById(R.id.btn_test);
        button.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_test) {
            DbtLog.logUtils(TAG, "toCameraTestFragment");
            getSupportDelegate().startWithPop(TestCameraCoreFragment.newInstance());


        } else {

        }
    }



}