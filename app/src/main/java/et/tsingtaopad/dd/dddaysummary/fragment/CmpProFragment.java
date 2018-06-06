package et.tsingtaopad.dd.dddaysummary.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
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
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.db.table.MitRepairM;
import et.tsingtaopad.db.table.MitRepaircheckM;
import et.tsingtaopad.db.table.MitRepairterM;
import et.tsingtaopad.dd.dddealplan.DdDealPlanAdapter;
import et.tsingtaopad.dd.dddealplan.domain.DealStc;
import et.tsingtaopad.home.app.MainService;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * 竞品追溯
 * Created by yangwenmin on 2018/3/12.
 */

public class CmpProFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "CmpProFragment";


    //
    public static final int DEALPLAN_UP_SUC = 3301;

    //
    public static final int DEALPLAN_UP_FAIL = 3302;

    public static final int DEALPLAN_NEED_UP = 3303;

    private TextView bt_addplan;
    private et.tsingtaopad.view.NoScrollListView monthplan_lv;
    private DdDealPlanAdapter dealPlanAdapter;
    private List<DealStc> dataLst;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_operation_cmppro, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

        bt_addplan = (TextView) view.findViewById(R.id.operation_cmppro_bt_addplan);
        monthplan_lv = (et.tsingtaopad.view.NoScrollListView) view.findViewById(R.id.operation_cmppro_monthplan_lv);

        bt_addplan.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);
        initData();
        initUrlData();
    }

    // 初始化数据
    private void initData() {




    }

    private void initUrlData() {
        String content = "{" +
                "areaid:'" + PrefUtils.getString(getActivity(), "departmentid", "") + "'," +
                "tablename:'" + "MIT_REPAIR_REPAIRTER_REPAIRCHECK_M" + "'," +
                "creuser:'" + PrefUtils.getString(getActivity(), "userid", "") + "'" +
                "}";
        ceshiHttp("opt_get_repair_ter_check", "MIT_REPAIR_REPAIRTER_REPAIRCHECK_M", content);
    }

    /**
     * 同步表数据
     *
     * @param optcode 请求码
     * @param table   请求表名(请求不同的)
     * @param content 请求json
     */
    void ceshiHttp(final String optcode, final String table, String content) {

        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(getContext());
        requestHeadStc.setOptcode(PropertiesUtil.getProperties(optcode));
        RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, content);

        // 压缩请求数据
        String jsonZip = HttpParseJson.parseRequestJson(reqObj);

        RestClient.builder()
                .url(HttpUrl.IP_END)
                .params("data", jsonZip)
                .loader(getContext())// 滚动条
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
                                parseTableJson(formjson);
                                initData();

                            } else {
                                Toast.makeText(getActivity(), resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                            /*Message msg = new Message();
                            msg.what = FirstFragment.SYNC_CLOSE;//
                            handler.sendMessage(msg);*/
                                //initData();
                            }
                        }


                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        /*Message msg1 = new Message();
                        msg1.what = FirstFragment.SYNC_CLOSE;//
                        handler.sendMessage(msg1);*/
                        //initData();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                        /*Message msg2 = new Message();
                        msg2.what = FirstFragment.SYNC_CLOSE;//
                        handler.sendMessage(msg2);*/
                        //initData();
                    }
                })
                .builde()
                .post();
    }

    // 解析区域定格路线成功
    private void parseTableJson(String json) {
        // 解析区域定格路线信息
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);
        String MIT_REPAIR_M = emp.getMIT_REPAIR_M();
        String MIT_REPAIRTER_M = emp.getMIT_REPAIRTER_M();
        String MIT_REPAIRCHECK_M = emp.getMIT_REPAIRCHECK_M();

        MainService service = new MainService(getActivity(), null);
        service.createOrUpdateTable(MIT_REPAIR_M, "MIT_REPAIR_M", MitRepairM.class);
        service.createOrUpdateTable(MIT_REPAIRTER_M, "MIT_REPAIRTER_M", MitRepairterM.class);
        service.createOrUpdateTable(MIT_REPAIRCHECK_M, "MIT_REPAIRCHECK_M", MitRepaircheckM.class);
    }


    // 点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.top_navigation_rl_confirm:// 确定
                Toast.makeText(getActivity(), "弹出日历", Toast.LENGTH_SHORT).show();
                break;
            case R.id.zgjh_bt_addplan:// 新增整顿计划
                // 跳转到  新增整改计划
                toDdDealMakeFragment();
                break;

            default:
                break;
        }
    }

    // 跳转到  新增整改计划
    private void toDdDealMakeFragment() {

    }

    MyHandler handler;

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<CmpProFragment> fragmentRef;

        public MyHandler(CmpProFragment fragment) {
            fragmentRef = new SoftReference<CmpProFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            CmpProFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }

            // 处理UI 变化
            switch (msg.what) {
                case DEALPLAN_UP_SUC://
                    fragment.shuaxinFragment(1);
                    break;
                case DEALPLAN_NEED_UP://
                    fragment.upRepair(1);
                    break;
                /*case DEALPLAN_UP_FAIL://
                    fragment.shuaxinFragment(2);
                    break;*/

            }
        }
    }

    // 上传未通过 或已通过
    private void upRepair(int i) {

    }

    // 结束上传  刷新页面
    private void shuaxinFragment(int upType) {
        initData();
    }

}
