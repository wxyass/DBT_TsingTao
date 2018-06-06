package et.tsingtaopad.business.first;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.business.first.bean.FirstDataStc;
import et.tsingtaopad.business.first.bean.GvTop;
import et.tsingtaopad.business.first.bean.LvTop;
import et.tsingtaopad.business.first.bean.XtZsNumStc;
import et.tsingtaopad.business.visit.bean.AreaGridRoute;
import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.net.domain.ResponseStructBean;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.dd.ddagencycheck.DdAgencyCheckSelectFragment;
import et.tsingtaopad.dd.dddaysummary.DdDaySummaryFragment;
import et.tsingtaopad.dd.dddealplan.DdDealPlanFragment;
import et.tsingtaopad.dd.ddweekplan.DdWeekPlanFragment;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectFragment;
import et.tsingtaopad.dd.ddzs.zsterm.zsselect.ZsTermSelectFragment;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.sign.DdSignActivity;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class FirstFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "FirstFragment";


    private TextView tv_username;
    private TextView tv_zhuisu;
    private TextView tv_xietong;
    private ImageView signImg;
    private et.tsingtaopad.view.MyGridView gv_top;
    private et.tsingtaopad.view.NoScrollListView sclv_top;

    private FirstGvTopAdapter gvTopAdapter;
    private FirstLvTopAdapter lvTopAdapter;

    List<GvTop> gvTops = new ArrayList<>();
    List<LvTop> lvTops = new ArrayList<>();
    List<XtZsNumStc> xtZsNumStcs = new ArrayList<>();

    private RelativeLayout rl_xtbf;
    private RelativeLayout rl_zdzs;
    private RelativeLayout rl_kcpd;
    private RelativeLayout rl_zgjh;
    private RelativeLayout rl_jhzd;
    private RelativeLayout rl_gzzj;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        tv_username = (TextView) view.findViewById(R.id.first_tv_username);
        tv_zhuisu = (TextView) view.findViewById(R.id.first_tv_zhuisu);
        tv_xietong = (TextView) view.findViewById(R.id.first_tv_xietong);
        signImg = (ImageView) view.findViewById(R.id.first_img_sign);

         rl_xtbf = (RelativeLayout) view.findViewById(R.id.first_rl_xtbf);
         rl_zdzs = (RelativeLayout) view.findViewById(R.id.first_rl_zdzs);
         rl_kcpd = (RelativeLayout) view.findViewById(R.id.first_rl_kcpd);
         rl_zgjh = (RelativeLayout) view.findViewById(R.id.first_rl_zgjh);
         rl_jhzd = (RelativeLayout) view.findViewById(R.id.first_rl_jhzd);
         rl_gzzj = (RelativeLayout) view.findViewById(R.id.first_rl_gzzj);

        gv_top = (et.tsingtaopad.view.MyGridView) view.findViewById(R.id.first_gv_top);
        sclv_top = (et.tsingtaopad.view.NoScrollListView) view.findViewById(R.id.first_sclv_top);

        signImg.setOnClickListener(this);
        rl_xtbf.setOnClickListener(this);
        rl_zdzs.setOnClickListener(this);
        rl_kcpd.setOnClickListener(this);
        rl_zgjh.setOnClickListener(this);
        rl_jhzd.setOnClickListener(this);
        rl_gzzj.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        //initUrlData();
    }

    private void initData() {

        gvTops.clear();
        gvTops.add(new GvTop("张三","33/63(个)","走访冠军"));
        gvTops.add(new GvTop("李四","13/53(个)","稽查冠军"));
        gvTops.add(new GvTop("王五","53/126(个)","库存盘点冠军"));
        gvTopAdapter = new FirstGvTopAdapter(getActivity(), gvTops);
        gv_top.setAdapter(gvTopAdapter);


        lvTops.clear();
        lvTops.add(new LvTop("1","稽查终端数量","78","99","个"));
        lvTops.add(new LvTop("1","稽查时长","18","23","时"));
        lvTops.add(new LvTop("2","追溯数据群","555","999","天"));
        lvTops.add(new LvTop("3","整改计划","16","23","个"));
        lvTops.add(new LvTop("4","经销商库存盘点","18","23","个"));
        lvTopAdapter = new FirstLvTopAdapter(getActivity(), lvTops, null);
        sclv_top.setAdapter(lvTopAdapter);

        String json = PrefUtils.getString(getActivity(),"firstjson","");
        if(!TextUtils.isEmpty(json)){
            parseFirstJson(json);
        }
    }

    private void initUrlData() {
        String content = "{" +
                "areaid:'" + PrefUtils.getString(getActivity(), "departmentid", "") + "'," +
                "time:'" + DateUtil.getDateTimeStr(8) + "'," +
                "creuser:'" + PrefUtils.getString(getActivity(), "userid", "") + "'" +
                "}";
        ceshiHttp("opt_get_first_data", content);
    }

    /**
     * 发送签到请求
     *
     * @param optcode 请求码
     * @param content 请求json
     */
    void ceshiHttp(final String optcode, String content) {

        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(getActivity());
        requestHeadStc.setOptcode(PropertiesUtil.getProperties(optcode));
        RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, content);
        // 压缩请求数据
        String jsonZip = HttpParseJson.parseRequestJson(reqObj);

        RestClient.builder()
                .url(HttpUrl.IP_END)
                .params("data", jsonZip)
                // .loader(getContext())// 滚动条
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        String json = HttpParseJson.parseJsonResToString(response);

                        if ("".equals(json) || json == null) {
                            Toast.makeText(getActivity(), "后台成功接收,但返回的数据为null", Toast.LENGTH_SHORT).show();
                        } else {
                            ResponseStructBean resObj = new ResponseStructBean();
                            resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                            // 保存登录信息
                            if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                                // 保存信息
                                String formjson = resObj.getResBody().getContent();
                                PrefUtils.putString(getActivity(),"firstjson",formjson);
                                parseFirstJson(formjson);
                                initData();

                            } else {
                                Toast.makeText(getActivity(), resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        /*Message msg1 = new Message();
                        msg1.what = FirstFragment.SYNC_CLOSE;//
                        handler.sendMessage(msg1);*/
                        //initData();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
                        /*Message msg2 = new Message();
                        msg2.what = FirstFragment.SYNC_CLOSE;//
                        handler.sendMessage(msg2);*/
                        //initData();
                    }
                })
                .builde()
                .post();
    }

    private void parseFirstJson(String formjson) {
        FirstDataStc emp = JsonUtil.parseJson(formjson, FirstDataStc.class);
        xtZsNumStcs = JsonUtil.parseList(emp.getHomesynergyandcheck(), XtZsNumStc.class);
        initJsonData();
    }

    private void initJsonData() {
        tv_zhuisu.setText("追溯数量:"+" "+xtZsNumStcs.get(0).getCountnum());
        tv_xietong.setText("协同数量:"+" "+xtZsNumStcs.get(1).getCountnum());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_img_sign:// 跳转打卡
                if (hasPermission(GlobalValues.LOCAL_PERMISSION)) {
                    // 拥有了此权限,那么直接执行业务逻辑
                    startDdSignActivity();
                } else {
                    // 还没有对一个权限(请求码,权限数组)这两个参数都事先定义好
                    requestPermission(GlobalValues.LOCAL_CODE, GlobalValues.LOCAL_PERMISSION);
                }
                break;

            case R.id.first_rl_xtbf://
                //Toast.makeText(getActivity(),"协同拜访",Toast.LENGTH_SHORT).show();
                changeHomeFragment(new XtTermSelectFragment(), "xttermlistfragment");
                break;
            case R.id.first_rl_zdzs://
                //Toast.makeText(getActivity(),"终端追溯",Toast.LENGTH_SHORT).show();
                changeHomeFragment(new ZsTermSelectFragment(), "zstermselectfragment");
                break;
            case R.id.first_rl_kcpd://
                //Toast.makeText(getActivity(),"库存盘点",Toast.LENGTH_SHORT).show();
                changeHomeFragment(new DdAgencyCheckSelectFragment(), "ddagencycheckselectfragment");
                break;
            case R.id.first_rl_zgjh://
                //Toast.makeText(getActivity(),"整改计划",Toast.LENGTH_SHORT).show();
                changeHomeFragment(new DdDealPlanFragment(), "dddealplanfragment");
                break;
            case R.id.first_rl_jhzd://
                //Toast.makeText(getActivity(),"计划制定",Toast.LENGTH_SHORT).show();
                changeHomeFragment(new DdWeekPlanFragment(), "ddweekplanfragment");
                break;
            case R.id.first_rl_gzzj://
                //Toast.makeText(getActivity(),"工作总结",Toast.LENGTH_SHORT).show();
                changeHomeFragment(new DdDaySummaryFragment(), "dddaysummaryfragment");
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
    private void startDdSignActivity() {
        Intent intent = new Intent(getActivity(), DdSignActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initUrlData(); // 在此请求数据
        }
    }
}
