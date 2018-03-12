package et.tsingtaopad.main.system.about;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.FileUtil;
import et.tsingtaopad.core.util.dbtutil.ZipControl;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class AboutInfoCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "ShopVisitCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private static final String TERM_NAME = "term_name";
    private static final String TERM_KEY = "term_key";
    private String termname = "测试终端";
    private String termkey = "1-LLI23IG";



    private TextView tv_update;
    private ImageView tsingtaoLogo;

    private List<Long> times;



    public AboutInfoCoreFragment() {
    }

    public static AboutInfoCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        AboutInfoCoreFragment fragment = new AboutInfoCoreFragment();
        return fragment;
    }

    public static AboutInfoCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        AboutInfoCoreFragment fragment = new AboutInfoCoreFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.system_setting_info, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (AppCompatTextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        tv_update = (TextView) view.findViewById(R.id.syssetting_tv_update);
        tsingtaoLogo = (ImageView) view.findViewById(R.id.sysinfo_logo);

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
        tsingtaoLogo.setOnClickListener(this);
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
        DbtLog.logUtils(TAG, "onSupportVisible");
        // 每次页面出现后都重新加载数据
        initData();
    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG, "initData");

        titleTv.setText(getString(R.string.systerm_info));
        times = new ArrayList<Long>();
        tv_update.setText("V" + DbtLog.getVersion());

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
            // 双击数据库上传
        } else if (i == R.id.sysinfo_logo) {
            doubleClick();
        }
    }

    // 双击事件监听,去复制数据库
    public void doubleClick() {
        times.add(SystemClock.uptimeMillis());
        if (times.size() == 2) {
            //已经完成了一次双击，list可以清空了
            if (times.get(times.size() - 1) - times.get(0) < 1000) {
                times.clear();
                // 复制数据库并上传
                Toast.makeText(getActivity(), "请稍后...", Toast.LENGTH_SHORT).show();
                copyBaseToService();// 上传
            } else {
                //这种情况下，第一次点击的时间已经没有用处了，第二次就是“第一次”
                times.remove(0);
            }
        }
    }

    public void copyBaseToService() {
        try {
            // 压缩文件 (将数据库和日志压缩)
            zip2File();
            // 上传文件
            updatebase();

        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    private String commentString = "Android db Zip";// 压缩包注释
    /**
     * 压缩文件
     */
    private void zip2File() {

        // 创建存放压缩包的文件夹,若没有则创建
        String zipPath = FileUtil.getZipPath();

        // 上传的文件路径  /dbt/et.tsingtaopad/zip/databases.zip
        String zip = zipPath+"databases.zip";

        // 第一个文件的路径 全部崩溃日志
        String bugPath = FileUtil.getBugPath();//srcString = BUGPATH;// /dbt/et.tsingtaopad/bug bug文件夹下所有文件
        // 第二个文件的路径 数据库文件
        String srcTwoString = "/data/data/et.tsingtaopad/databases/FsaDBT.db";
        // 第三个文件的路径 操作日志 /dbt/et.tsingtaopad/log/log.txt
        String srcthreeString = FileUtil.getLogPath() + "log.txt";
        // 第四个文件的路径 缓存shared_prefs
        String srcfourString = "/data/data/et.tsingtaopad/shared_prefs";

        String[] fileSrcStrings = new String[] { bugPath, srcTwoString, srcthreeString, srcfourString };
        ZipControl mZipControl = new ZipControl();

        try {
            // 压缩 (要压缩的文件路径列表,压缩包文件路径,描述)
            mZipControl.writeByApacheZipOutputStream(fileSrcStrings, zip, commentString);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }


    }

    /**
     * 上传压缩包
     */
    private void updatebase() {


    }
}
