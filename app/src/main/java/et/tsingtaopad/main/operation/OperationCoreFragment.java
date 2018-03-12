package et.tsingtaopad.main.operation;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.home.delete.BaseBottomCoreFragment;
import et.tsingtaopad.home.initadapter.FaceAdapter;
import et.tsingtaopad.home.initadapter.ItemClickListener;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述:运营管理首页</br>
 */
public class OperationCoreFragment extends BaseBottomCoreFragment {

    public static final String TAG = "OperationCoreFragment";

    private GridView platform_gv_operation;
    private ArrayList<Integer> images;

    public OperationCoreFragment() {
        // Required empty public constructor
    }

    public static OperationCoreFragment newInstance() {

        Bundle args = new Bundle();

        OperationCoreFragment fragment = new OperationCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DbtLog.logUtils(TAG,"onCreateView");
        View view =  inflater.inflate(R.layout.fragment_operation, container, false);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        platform_gv_operation = (GridView) view.findViewById(R.id.platform_gv_operation);
        // 初始化按钮对应图片
        initAuthority();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG, "onSupportVisible");
        // 每次页面出现后都重新加载数据
        initData();
    }

    private void initData() {
        // 接口获取权限
        String xtgl = PrefUtils.getString(getActivity(), "yxgl", "");
        if (xtgl != null && xtgl.length() > 0) {
            String[] visitAuthority;
            if (xtgl.contains(",")) {
                visitAuthority = xtgl.split(",");
            } else {
                visitAuthority = new String[]{xtgl};
            }
            images = new ArrayList<Integer>();
            images.removeAll(images);
            for (String string : visitAuthority) {
                images.add(authorityMap.get(string));
            }
            //images.add(authorityMap.get("1000024"));// 改行记得删除
            FaceAdapter jieMianAdapter = new FaceAdapter(getContext(), images);
            platform_gv_operation.setSelector(new ColorDrawable(Color.TRANSPARENT));
            platform_gv_operation.setAdapter(jieMianAdapter);
            // 设置item的点击监听
            platform_gv_operation.setOnItemClickListener(new ItemClickListener(this,images));
        }
    }
}
