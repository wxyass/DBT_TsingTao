package et.tsingtaopad.business.system;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.text.DecimalFormat;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.business.first.FirstFragment;
import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.callback.OnDownLoadProgress;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.net.domain.ResponseStructBean;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.dd.ddagencyres.DdAgencyContentFragment;
import et.tsingtaopad.dd.dddaysummary.DdDaySummaryFragment;
import et.tsingtaopad.dd.dddealplan.DdDealPlanFragment;
import et.tsingtaopad.dd.ddweekplan.DdWeekPlanFragment;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class SystemFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    RelativeLayout repwd;
    RelativeLayout question;
    RelativeLayout upload;
    RelativeLayout about;

    public static  final int SHOWDOWNLOADDIALOG = 88; // 弹出进度条,设置最大值100 //显示正在下载的对话框
    public static  final int UPDATEDOWNLOADDIALOG = 99;// 设置进度条  //刷新正在下载对话框的内容
    public static  final int DOWNLOADFINISHED = 66;// 下载完成开始安装 //下载完成后进行的操作

    MyHandler handler;

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<SystemFragment> fragmentRef;

        public MyHandler(SystemFragment fragment) {
            fragmentRef = new SoftReference<SystemFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            SystemFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }

            // 处理UI 变化
            switch (msg.what) {
                case SHOWDOWNLOADDIALOG:
                    fragment.showDownloadDialog();
                    break;
                case UPDATEDOWNLOADDIALOG: // 督导输入数据后
                    fragment.showDownloading(msg);
                    break;
                case DOWNLOADFINISHED: // 督导输入数据后
                    fragment.stopDownloadDialog();
                    break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_system, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        backBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_tv_title);
        backBtn.setVisibility(View.INVISIBLE);
        confirmBtn.setVisibility(View.INVISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        repwd = (RelativeLayout) view.findViewById(R.id.dd_system_rl_repwd);
        question = (RelativeLayout) view.findViewById(R.id.dd_system_rl_question);
        upload = (RelativeLayout) view.findViewById(R.id.dd_system_rl_upload);
        about = (RelativeLayout) view.findViewById(R.id.dd_system_rl_about);

        repwd.setOnClickListener(this);
        question.setOnClickListener(this);
        upload.setOnClickListener(this);
        about.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("系统管理");
        handler = new MyHandler(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dd_system_rl_repwd:// 修改密码
                Toast.makeText(getActivity(), "修改密码", Toast.LENGTH_SHORT).show();
                //changeHomeFragment(new DdWeekPlanFragment(), "ddweekplanfragment");
                break;
            case R.id.dd_system_rl_question:// 问题反馈
                Toast.makeText(getActivity(), "问题反馈", Toast.LENGTH_SHORT).show();
                //changeHomeFragment(new DdDaySummaryFragment(), "dddaysummaryfragment");
                break;
            case R.id.dd_system_rl_upload:// 检查更新
                Toast.makeText(getActivity(), "检查更新", Toast.LENGTH_SHORT).show();
                //changeHomeFragment(new DdDealPlanFragment(), "dddealplanfragment");

                checkUpload();
                break;
            case R.id.dd_system_rl_about:// 关于系统
                Toast.makeText(getActivity(), "关于系统", Toast.LENGTH_SHORT).show();
                //changeHomeFragment(new DdDealPlanFragment(), "dddealplanfragment");
                break;
        }

    }

    // 下载apk
    private void checkUpload() {

        Message msg = Message.obtain();
        msg.what = SHOWDOWNLOADDIALOG;
        handler.sendMessage(msg);


        RestClient.builder()
                .url(apkUrl)
                // .params("data", jsonZip)
                // .loader(getContext())// 滚动条
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        /*String json = HttpParseJson.parseJsonResToString(response);

                        if ("".equals(json) || json == null) {
                            Toast.makeText(getActivity(), "后台成功接收,但返回的数据为null", Toast.LENGTH_SHORT).show();
                        } else {
                            ResponseStructBean resObj = new ResponseStructBean();
                            resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                            // 保存登录信息
                            if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                                // 保存信息
                                String formjson = resObj.getResBody().getContent();

                            } else {
                                Toast.makeText(getActivity(), resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                            }
                        }*/

                        //用handle通知主线程 下载完成 -> 开始安装
                        Message finishedMsg = Message.obtain();
                        finishedMsg.what = DOWNLOADFINISHED;
                        // finishedMsg.obj = downPath+apkName;// 文件路径
                        handler.sendMessage(finishedMsg);


                    }
                })
                .onDownLoadProgress(new OnDownLoadProgress() {
                    @Override
                    public void onProgressUpdate(long fileLength, int downLoadedLength) {
                        float value = (float) ((downLoadedLength / fileLength) * 100);
                        if(value % 5 ==0){
                            // 用handle通知主线程刷新进度, progress: 是1-100的正整数
                            Message updateMsg = Message.obtain();
                            updateMsg.what = UPDATEDOWNLOADDIALOG;
                            updateMsg.obj = value;
                            handler.sendMessage(updateMsg);
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .name("apkName")
                .dir("wxyass")
                .builde()
                .download();
    }

    /**
     * 显示正在下载的对话框
     *
     */
    String apkUrl = "http://oss.wxyass.com/tscs2.4.3.1.0.apk";
    String apkName = "tscs2.4.3.1.0.apk";
    String downPath = "";// apk的存放位置

    //private HttpHandler httpHandler;
    private AlertDialog downloadDialog;//正在下载的对话框
    private TextView tvCur;//当前下载的百分比
    private ProgressBar pb;//下载的进度条
    private TextView tvCanCel;//停止下载
    private TextView tvHidden;//隐藏对话框的按钮
    private int contentLength;//要下载文件的大小
    private boolean isDownloading = false;//是否正在下载
    private boolean isCancel = false;//是否取消升级
    private DecimalFormat df = new DecimalFormat("###.00");//设置结果保留两位小数
    private void showDownloadDialog(){
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        downloadDialog = adb.create();
        View view = View.inflate(getActivity(), R.layout.download_dialog_layout, null);
        downloadDialog.setView(view, 0, 0, 0, 0);
        tvCur = (TextView) view.findViewById(R.id.tv_cursize);
        tvCanCel = (TextView) view.findViewById(R.id.tv_cancel);
        tvHidden = (TextView) view.findViewById(R.id.tv_hidden);
        pb = (ProgressBar) view.findViewById(R.id.download_pb);
        pb.setMax(100);
        downloadDialog.show();
    }
    private void showDownloading(android.os.Message msg) {
        int curSize = (int)msg.obj;// 获取当前进度
        pb.setProgress(curSize);
        //tvCur.setText(df.format((float)curSize / (float)contentLength * 100) + "%");
        tvCur.setText( curSize + "%");
    }

    private void stopDownloadDialog() {
        Toast.makeText(getActivity(), "下载成功", Toast.LENGTH_SHORT).show();
        isDownloading = false;
        if(downloadDialog.isShowing()){
            downloadDialog.dismiss();
        }
    }

}
