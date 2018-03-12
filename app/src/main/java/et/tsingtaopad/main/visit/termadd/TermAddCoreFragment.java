package et.tsingtaopad.main.visit.termadd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.test.datatonext.SecondCoreFragment;


/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class TermAddCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "TermAddCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private static final String TERM_NAME = "term_name";
    private static final String TERM_KEY = "term_key";
    private String termname = "测试终端";
    private String termkey = "1-LLI23IG";

    public TermAddCoreFragment() {
    }

    public static TermAddCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        TermAddCoreFragment fragment = new TermAddCoreFragment();
        return fragment;
    }

    public static TermAddCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        TermAddCoreFragment fragment = new TermAddCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // 在这个方法中 只处理从上个Fragment传递过来的数据,若没数据传递过来,可删除该方法
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            //termname = bundle.getString(TERM_NAME);
            //termkey = bundle.getString(TERM_KEY);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DbtLog.logUtils(TAG, "onActivityCreated");

    }

    // 只用来初始化控件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_line, null);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (AppCompatTextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        titleTv.setText("新增终端");
        //confirmRl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
        // 确定
        }else if(i == R.id.banner_navigation_rl_confirm){
            Bundle args = new Bundle();
            args.putString(TERM_NAME, termname);
            args.putString(TERM_KEY, termkey);
            getSupportDelegate().start(SecondCoreFragment.newInstance());
        }
    }
}
