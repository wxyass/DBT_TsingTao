package et.tsingtaopad.test;

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

public class TestCameraCoreFragment extends BaseCoreFragment implements View.OnClickListener{

    public static final String TAG = "TestCameraCoreFragment";
    Button backBtn;
    Button cameraBtn;

    public TestCameraCoreFragment() {
        // Required empty public constructor
    }

    public static TestCameraCoreFragment newInstance() {

        Bundle args = new Bundle();

        TestCameraCoreFragment fragment = new TestCameraCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DbtLog.logUtils(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_ceshi, null);
        this.initView(view);
        return view;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        // 设置横屏
        getSupportDelegate().getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.initData();
    }

    private void initView(View view) {
        backBtn = (Button)view.findViewById(R.id.btn_ceshi1);
        cameraBtn = (Button)view.findViewById(R.id.btn_ceshi2);
        backBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_ceshi1) {
            DbtLog.logUtils(TAG, "Back");


        } else if(i == R.id.btn_ceshi2) {
            DbtLog.logUtils(TAG, "Camera");

        }

    }

    @Override
    public boolean onBackPressedSupport() {

        // 设置横屏
        getSupportDelegate().getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 默认flase，继续向上传递
        return super.onBackPressedSupport();
    }
}