package et.tsingtaopad.business.system;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.business.first.FirstFragment;
import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.net.domain.ResponseStructBean;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
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

        RestClient.builder()
                .url("http://oss.wxyass.com/private/images/001/Pic_000009.jpg")
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
                .name("wxyass.jpg")
                .dir("wxyass")
                .builde()
                .download();
    }

}
