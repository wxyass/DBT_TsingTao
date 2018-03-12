package et.tsingtaopad.test.datatonext;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;

/**
 * Created by yangwenmin on 2017/12/25.
 * 由第三个界面跳转过来 可重复拷贝
 */

public class FourCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "ShopVisitCoreFragment";
    private TextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private static final String TERM_NAME = "term_name";
    private static final String TERM_KEY = "term_key";
    private String termname;
    private String termkey;

    public FourCoreFragment() {
    }

    public static FourCoreFragment newInstance() {
        FourCoreFragment fragment = new FourCoreFragment();
        return fragment;
    }

    public static FourCoreFragment newInstance(Bundle bundle) {
        //Bundle args = new Bundle();

        FourCoreFragment fragment = new FourCoreFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            termname = bundle.getString(TERM_NAME);
            termkey = bundle.getString(TERM_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.visit_shopvisit_main, null);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        titleTv.setText(termname);
        confirmRl.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
        // 确定
        }else if(i == R.id.banner_navigation_rl_confirm){
            
        }
    }
}
