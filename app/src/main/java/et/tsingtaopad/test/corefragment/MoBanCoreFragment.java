package et.tsingtaopad.test.corefragment;

import android.os.Bundle;
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
 */

public class MoBanCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "ShopVisitCoreFragment";
    private TextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    public MoBanCoreFragment() {
    }

    public static MoBanCoreFragment newInstance() {

        Bundle args = new Bundle();

        MoBanCoreFragment fragment = new MoBanCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_line, null);
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
    public void onClick(View v) {

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            //pop();
        // 确定
        }else if(i == R.id.banner_navigation_rl_confirm){

        }
    }
}
