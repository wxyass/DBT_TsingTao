package et.tsingtaopad.business.first;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.sign.DdSignActivity;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class FirstFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "FirstFragment";

    private ImageView signImg;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
         signImg = (ImageView)view.findViewById(R.id.first_img_sign);
        signImg.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_img_sign:// 同步所有信息
                if (hasPermission(GlobalValues.LOCAL_PERMISSION)) {
                    // 拥有了此权限,那么直接执行业务逻辑
                    //startSignFragment();
                    startDdSignActivity();
                } else {
                    // 还没有对一个权限(请求码,权限数组)这两个参数都事先定义好
                    requestPermission(GlobalValues.LOCAL_CODE, GlobalValues.LOCAL_PERMISSION);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void doLocation() {
        startDdSignActivity();
    }

    // // 签到
    private void startDdSignActivity(){
        Intent intent = new Intent(getActivity(), DdSignActivity.class);
        startActivity(intent);
    }

}
