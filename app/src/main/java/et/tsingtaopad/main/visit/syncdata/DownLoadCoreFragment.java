package et.tsingtaopad.main.visit.syncdata;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.initconstvalues.InitConstValues;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class DownLoadCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "DownLoadCoreFragment";

    public static final int SYNDATA_RESULT_Success = 10;
    public static final int SYNDATA_PROGRESS = 11;
    public static final int SYNDATA_RESULT_Failure = 12;
    private ProgressBar download_progressBar;
    private TextView download_progressTV;
    private TextView download_percentTV;
    private int progressMax = 45;//需要同步表的总数  进度总数 按模块划分计算出来的数量/比如 联网 ，更新1表， 更新2表 可以通过计算DownLoadDataService 调用了sendProgressMessage多少次计算出来


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case SYNDATA_PROGRESS:
                    Bundle data = msg.getData();
                    String string = data.getString("progressStr");
                    int progress = data.getInt("progress");
                    download_progressBar.setProgress(progress);
                    download_progressTV.setText(string+"更新中...");
                    //计算百分比
                    float gress = progress;
                    NumberFormat numberFormat = NumberFormat.getPercentInstance();
                    numberFormat.setMinimumFractionDigits(0);
                    String percent = numberFormat.format(gress / progressMax);
                    Log.d(TAG, "percent:"+percent);
                    download_percentTV.setText(percent);
                    break;

                case SYNDATA_RESULT_Success:    //同步成功结束
                    new InitConstValues(getContext()).start();
                    Toast.makeText(getContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    pop();
                    break;

                case SYNDATA_RESULT_Failure:// 同步出错：1，网络连接失败， 2，发生异常
                    // 初始化静态变量
                    new InitConstValues(getContext()).start();
                    pop();
                    Bundle args = new Bundle();
                    args.putString("prompt_content", (String) msg.obj);//  是否第一次拜访  0:第一次拜访   1:重复拜访
                    //getSupportDelegate().startWithPop(ErrorPromptCoreFragment.newInstance(args));
                    getSupportDelegate().extraTransaction().startDontHideSelf(ErrorPromptCoreFragment.newInstance(args));

                    break;

                default:
                    break;
            }
        }

    };

    public DownLoadCoreFragment() {
    }

    public static DownLoadCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        DownLoadCoreFragment fragment = new DownLoadCoreFragment();
        return fragment;
    }

    public static DownLoadCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        DownLoadCoreFragment fragment = new DownLoadCoreFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");

        /*@SuppressLint("RestrictedApi")
        final Context contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.MyDialogStyle);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view =  localInflater.inflate(R.layout.downloadprogress, null);*/

        View view = inflater.inflate(R.layout.downloadprogress, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        download_progressBar = (ProgressBar) view.findViewById(R.id.download_progressBar);
        download_progressTV = (TextView) view.findViewById(R.id.download_progressTV);
        download_percentTV = (TextView) view.findViewById(R.id.download_percentTV);

        download_progressBar.setMax(progressMax);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");

    }

    @Override
        public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG,"onSupportVisible");
        // 每次页面出现后都重新加载数据
        initData();
    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG,"initData");
        DownLoadService loadService = new DownLoadService(getContext(),handler);
        loadService.asyndatas();
    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        /*int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
        // 确定
        }else if(i == R.id.banner_navigation_rl_confirm){

        }*/
    }
}
