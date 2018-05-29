package et.tsingtaopad.business.first;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.business.first.bean.AreaGridRoute;
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
import et.tsingtaopad.db.table.CmmAreaM;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MitValcheckterM;
import et.tsingtaopad.db.table.MstAgencyKFM;
import et.tsingtaopad.db.table.MstAgencyinfoM;
import et.tsingtaopad.db.table.MstAgencyvisitM;
import et.tsingtaopad.db.table.MstCmpbrandsM;
import et.tsingtaopad.db.table.MstCmpcompanyM;
import et.tsingtaopad.db.table.MstCmproductinfoM;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstInvoicingInfo;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstPictypeM;
import et.tsingtaopad.db.table.MstProductM;
import et.tsingtaopad.db.table.MstPromoproductInfo;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstVisitauthorizeInfo;
import et.tsingtaopad.db.table.PadCheckstatusInfo;
import et.tsingtaopad.home.app.MainService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.login.domain.BsVisitEmpolyeeStc;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class FirstFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "FirstFragment";

    AppCompatButton startSync;

    MyHandler handler;

    public static final int SYNC_SUCCSE = 1101;// 开始请求
    public static final int SYNC_START = 1102;// 弹出进度弹窗
    public static final int SYNC_CLOSE = 1103;// 请求结束 关闭进度条
    private int count ;

    private List<String> tablenames;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        startSync = view.findViewById(R.id.dd_btn_first_sync_start);
        startSync.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new MyHandler(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dd_btn_first_sync_start:// 同步所有信息
                if (hasPermission(GlobalValues.WRITE_READ_EXTERNAL_PERMISSION)) {
                    // 拥有了此权限,那么直接执行业务逻辑
                    startSyncInfo();
                } else {
                    // 还没有对一个权限(请求码,权限数组)这两个参数都事先定义好
                    requestPermission(GlobalValues.WRITE_READ_EXTERNAL_CODE, GlobalValues.WRITE_READ_EXTERNAL_PERMISSION);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void doWriteSDCard() {
        startSyncInfo();
    }

    private void startSyncInfo() {
        handler.sendEmptyMessage(FirstFragment.SYNC_START); // 弹出进度弹窗
        count = 1;
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    Looper.prepare();
                    getInfo();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    // 测试登录网络框架
    private void getInfo() {

        tablenames = new ArrayList<>();
        tablenames.add("MST_MARKETAREA_GRID_ROUTE_M");
        tablenames.add("MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO");
        tablenames.add("MST_BASEDATA_M");

        //handler.sendEmptyMessage(FirstFragment.SYNC_SUCCSE);
        Bundle bundle = new Bundle();
        bundle.putString("msg", "开始同步数据");
        Message msg = new Message();
        msg.what = FirstFragment.SYNC_SUCCSE;//
        msg.setData(bundle);
        handler.sendMessage(msg);

    }

    private String getJson(String tablename){
        String json = "{" +
                "areaid:'" + PrefUtils.getString(getActivity(), "departmentid", "") + "'," +
                "tablename:'" + tablename + "'" +
                "}";
        return json;
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
                //.loader(getContext())// 滚动条
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        count++;
                        String json = HttpParseJson.parseJsonResToString(response);
                        ResponseStructBean resObj = new ResponseStructBean();
                        resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                        // 保存登录信息
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                            // 保存信息
                            if ("MST_MARKETAREA_GRID_ROUTE_M".equals(table)) {

                                Bundle bundle = new Bundle();
                                bundle.putString("msg", "正在处理区域数据...");
                                Message msg = new Message();
                                msg.what = FirstFragment.SYNC_SUCCSE;//
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                                String formjson = resObj.getResBody().getContent();
                                parseTableJson(formjson);

                            }
                            if ("MST_BASEDATA_M".equals(table)) {

                                Bundle bundle = new Bundle();
                                bundle.putString("msg", "正在处理基础数据...");
                                Message msg = new Message();
                                msg.what = FirstFragment.SYNC_SUCCSE;//
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                                String formjson = resObj.getResBody().getContent();
                                parseDatadicTableJson(formjson);

                            }
                            if ("MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO".equals(table)) {

                                Bundle bundle = new Bundle();
                                bundle.putString("msg", "正在处理指标数据...");
                                Message msg = new Message();
                                msg.what = FirstFragment.SYNC_SUCCSE;//
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                                String formjson = resObj.getResBody().getContent();
                                parseIndexTableJson(formjson);
                            }
                        } else {
                            Toast.makeText(getActivity(), resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                            Message msg = new Message();
                            msg.what = FirstFragment.SYNC_CLOSE;//
                            handler.sendMessage(msg);
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        Message msg1 = new Message();
                        msg1.what = FirstFragment.SYNC_CLOSE;//
                        handler.sendMessage(msg1);
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                        Message msg2 = new Message();
                        msg2.what = FirstFragment.SYNC_CLOSE;//
                        handler.sendMessage(msg2);
                    }
                })
                .builde()
                .post();
    }

    // 解析区域定格路线成功
    private void parseTableJson(String json) {
        // 解析区域定格路线信息
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);
        String mst_grid_m = emp.getMST_GRID_M();
        String mst_marketarea_m = emp.getMST_MARKETAREA_M();
        String mst_route_m = emp.getMST_ROUTE_M();

        MainService service = new MainService(getActivity(), null);
        service.createOrUpdateTable(mst_grid_m, "MST_GRID_M", MstGridM.class);
        service.createOrUpdateTable(mst_marketarea_m, "MST_MARKETAREA_M", MstMarketareaM.class);
        service.createOrUpdateTable(mst_route_m, "MST_ROUTE_M", MstRouteM.class);
    }

    // 解析基础信息成功
    private void parseDatadicTableJson(String json) {
        // 解析基础信息
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);
        String CMM_DATADIC_M = emp.getCMM_DATADIC_M();
        String CMM_AREA_M = emp.getCMM_AREA_M();
        String MST_PROMOTIONS_M = emp.getMST_PROMOTIONS_M();
        String MST_PROMOPRODUCT_INFO = emp.getMST_PROMOPRODUCT_INFO();
        String MST_PICTYPE_M = emp.getMST_PICTYPE_M();
        String MST_PRODUCT_M = emp.getMST_PRODUCT_M();

        String MST_CMPCOMPANY_M = emp.getMST_CMPCOMPANY_M();
        String MST_CMPBRANDS_M = emp.getMST_CMPBRANDS_M();
        String MST_CMPRODUCTINFO_M = emp.getMST_CMPRODUCTINFO_M();
        String MIT_VALCHECKTER_M = emp.getMIT_VALCHECKTER_M();

        String MST_AGENCYKF_M = emp.getMST_AGENCYKF_M();
        String MST_AGENCYVISIT_M = emp.getMST_AGENCYVISIT_M();
        String MST_INVOICING_INFO = emp.getMST_INVOICING_INFO();
        String MST_VISITAUTHORIZE_INFO = emp.getMST_VISITAUTHORIZE_INFO();
        String MST_AGENCYINFO_M = emp.getMST_AGENCYINFO_M();


        MainService service = new MainService(getActivity(), null);
        service.createOrUpdateTable(CMM_DATADIC_M, "CMM_DATADIC_M", CmmDatadicM.class);
        service.createOrUpdateTable(CMM_AREA_M, "CMM_AREA_M", CmmAreaM.class);
        service.createOrUpdateTable(MST_PROMOTIONS_M, "MST_PROMOTIONS_M", MstPromotionsM.class);
        service.createOrUpdateTable(MST_PROMOPRODUCT_INFO, "MST_PROMOPRODUCT_INFO", MstPromoproductInfo.class);
        service.createOrUpdateTable(MST_PICTYPE_M, "MST_PICTYPE_M", MstPictypeM.class);
        service.createOrUpdateTable(MST_PRODUCT_M, "MST_PRODUCT_M", MstProductM.class);
        service.createOrUpdateTable(MST_CMPCOMPANY_M, "MST_CMPCOMPANY_M", MstCmpcompanyM.class);
        service.createOrUpdateTable(MST_CMPBRANDS_M, "MST_CMPBRANDS_M", MstCmpbrandsM.class);
        service.createOrUpdateTable(MST_CMPRODUCTINFO_M, "MST_CMPRODUCTINFO_M", MstCmproductinfoM.class);
        service.createOrUpdateTable(MIT_VALCHECKTER_M, "MIT_VALCHECKTER_M", MitValcheckterM.class);

        service.createOrUpdateTable(MST_AGENCYKF_M, "MST_AGENCYKF_M", MstAgencyKFM.class);
        service.createOrUpdateTable(MST_AGENCYVISIT_M, "MST_AGENCYVISIT_M", MstAgencyvisitM.class);
        service.createOrUpdateTable(MST_INVOICING_INFO, "MST_INVOICING_INFO", MstInvoicingInfo.class);
        service.createOrUpdateTable(MST_VISITAUTHORIZE_INFO, "MST_VISITAUTHORIZE_INFO", MstVisitauthorizeInfo.class);
        service.createOrUpdateTable(MST_AGENCYINFO_M, "MST_AGENCYINFO_M", MstAgencyinfoM.class);

    }

    // 解析指标数据成功
    private void parseIndexTableJson(String json) {
        // 解析指标数据
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);

        String PAD_CHECKSTATUS_INFO = emp.getMST_CHECKSTATUS_INFO();
        String MST_COLLECTIONTEMPLATE_M = emp.getMST_COLLECTIONTEMPLATE_M();

        MainService service = new MainService(getActivity(), null);
        service.createOrUpdateTable(PAD_CHECKSTATUS_INFO, "PAD_CHECKSTATUS_INFO", PadCheckstatusInfo.class);
        service.parsePadCheckType(MST_COLLECTIONTEMPLATE_M);
    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<FirstFragment> fragmentRef;

        public MyHandler(FirstFragment fragment) {
            fragmentRef = new SoftReference<FirstFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FirstFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }

            // 处理UI 变化
            switch (msg.what) {
                case SYNC_SUCCSE:// 开始请求
                    Bundle bundle = msg.getData();
                    String msgdate = (String) bundle.getSerializable("msg");
                    fragment.getJsonProgress(msgdate);
                    break;
                case SYNC_START:// 弹出进度弹窗
                    fragment.showFirstDialog("正在同步数据");
                    break;
                case SYNC_CLOSE:// 请求结束
                    fragment.closeFirstDialog();
                    break;
            }
        }
    }

    // 请求表数据
    private void getJsonProgress(String msgdata) {

        if(count<tablenames.size()+1){
            String tablename = tablenames.get(count-1);
            ceshiHttp("opt_get_dates2", tablename, getJson(tablename));
        }

        showFirstDialog(msgdata);// 刷新进度条

        if (count >= tablenames.size()+1) {
            closeFirstDialog();// 关闭进度条
            Toast.makeText(getActivity(), "同步成功", Toast.LENGTH_SHORT).show();
        }
    }

    private AlertDialog dialog;

    /**
     * 展示滚动条
     */
    public void showFirstDialog(String msgdata) {

        if (dialog != null) {
            ProgressBar progress1 = dialog.findViewById(R.id.progressbar_sync_1);
            TextView text1 = dialog.findViewById(R.id.dialog_tv_sync);
            progress1.setProgress(count * (int)Math.floor(100/tablenames.size()));//设置当前进度
            text1.setText(msgdata);
            dialog.setCancelable(false); // 是否可以通过返回键 关闭
            dialog.show();
        } else {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.sync_progress, null);
            TextView dialog_tv_sync = (TextView) view.findViewById(R.id.dialog_tv_sync);
            // ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressbar_sync_1);
            dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
            dialog_tv_sync.setText(msgdata);
            dialog.setView(view, 0, 0, 0, 0);
            dialog.setCancelable(false); // 是否可以通过返回键 关闭
            dialog.show();
        }
    }

    // 关闭滚动条
    public void closeFirstDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
