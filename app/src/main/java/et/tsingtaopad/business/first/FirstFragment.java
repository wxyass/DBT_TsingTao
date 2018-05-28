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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;

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

    AppCompatButton login;
    AppCompatButton login2;
    AppCompatButton syncgrid;
    AppCompatButton syncindex;
    AppCompatButton syncpro;
    AppCompatButton startSync;

    MyHandler handler;

    public static final int SYNC_SUCCSE = 1101;// 同步成功返回
    public static final int SYNC_START = 1102;// 发起同步请求
    public static final int SYNC_CLOSE = 1103;// 关闭进度条
    private int count = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        login = view.findViewById(R.id.dd_btn_first_login);
        login2 = view.findViewById(R.id.dd_btn_first_login2);
        syncgrid = view.findViewById(R.id.dd_btn_first_sync_grid);
        syncindex = view.findViewById(R.id.dd_btn_first_sync_index);
        syncpro = view.findViewById(R.id.dd_btn_first_sync_pro);
        startSync = view.findViewById(R.id.dd_btn_first_sync_start);
        login.setOnClickListener(this);
        login2.setOnClickListener(this);
        syncgrid.setOnClickListener(this);
        syncindex.setOnClickListener(this);
        syncpro.setOnClickListener(this);
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
            case R.id.dd_btn_first_login:// 登录
                String   loginjson = "{usercode:'50000', password:'a1234567',version:'2.5',padid:'dsfwerolkjqiwurywhl'}";
                toLogin("opt_get_login","","",loginjson);
                break;
            case R.id.dd_btn_first_login2:// 登录
                String   loginjson1 = "{usercode:'2029298', password:'a1234567',version:'2.5',padid:'gfdahtgnfdsddvdsGd'}";
                toLogin("opt_get_login","","",loginjson1);
                break;
            case R.id.dd_btn_first_sync_grid:// 同步 定格,路线
                String content  = "{"+
                        "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                        "tablename:'"+"MST_MARKETAREA_GRID_ROUTE_M"+"'" +
                        "}";
                ceshiHttp("opt_get_dates2","MST_MARKETAREA_GRID_ROUTE_M",content);
                break;
            case R.id.dd_btn_first_sync_index:// 同步 指标模板


                String content2  = "{"+
                        "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                        "tablename:'"+"MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO"+"'" +
                        "}";
                ceshiHttp("opt_get_dates2","MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO",content2);
                break;
            case R.id.dd_btn_first_sync_pro:// 同步 基础数据表
                String content1  = "{"+
                        "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                        "tablename:'"+"MST_BASEDATA_M"+"'" +
                        "}";
                ceshiHttp("opt_get_dates2","MST_BASEDATA_M",content1);
                break;
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
        handler.sendEmptyMessage(FirstFragment.SYNC_START);
        count = 0;
        Thread thread = new Thread() {

            @Override
            public void run() {
                try{
                    Looper.prepare();
                    getInfo();
                    Looper.loop();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    // 测试登录网络框架
    private void getInfo(){
        String gridjson  = "{"+
                "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                "tablename:'"+"MST_MARKETAREA_GRID_ROUTE_M"+"'" +
                "}";
        ceshiHttp("opt_get_dates2","MST_MARKETAREA_GRID_ROUTE_M",gridjson);

        String indexjson  = "{"+
                "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                "tablename:'"+"MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO"+"'" +
                "}";
        ceshiHttp("opt_get_dates2","MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO",indexjson);

        String basejson  = "{"+
                "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                "tablename:'"+"MST_BASEDATA_M"+"'" +
                "}";
        ceshiHttp("opt_get_dates2","MST_BASEDATA_M",basejson);
    }

    /**
     * 同步表数据
     *
     * @param optcode   请求码
     * @param table     请求表名(请求不同的)
     * @param content   请求json
     */
    void ceshiHttp(final String optcode, final String table,String content) {

        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(getContext());
        /*if("opt_get_login".equals(optcode)){
            requestHeadStc.setUsercode("50000");
            requestHeadStc.setPassword("a1234567");
        }*/
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
                        String json = HttpParseJson.parseJsonResToString(response);
                        ResponseStructBean resObj = new ResponseStructBean();
                        resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                        //Toast.makeText(getActivity(), resObj.getResBody().getContent()+""+resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                        // 保存登录信息
                        if(ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())){
                            // 保存信息
                            if("opt_get_dates2".equals(optcode)&&"MST_MARKETAREA_GRID_ROUTE_M".equals(table)){

                                String formjson = resObj.getResBody().getContent();
                                parseTableJson(formjson);

                                Bundle bundle = new Bundle();
                                bundle.putString("msg","正在处理区域数据表");
                                Message msg = new Message();
                                msg.what = FirstFragment.SYNC_SUCCSE;//
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                                //Toast.makeText(getActivity(), "区域定格路线同步成功", Toast.LENGTH_SHORT).show();
                            }
                            if("opt_get_dates2".equals(optcode)&&"MST_BASEDATA_M".equals(table)){


                                String formjson = resObj.getResBody().getContent();
                                parseDatadicTableJson(formjson);

                                Bundle bundle = new Bundle();
                                bundle.putString("msg","正在处理基础数据表");
                                Message msg = new Message();
                                msg.what = FirstFragment.SYNC_SUCCSE;//
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                                //Toast.makeText(getActivity(), "基础数据同步成功", Toast.LENGTH_SHORT).show();
                            }
                            if("opt_get_dates2".equals(optcode)&&"MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO".equals(table)){

                                String formjson = resObj.getResBody().getContent();
                                parseIndexTableJson(formjson);

                                Bundle bundle = new Bundle();
                                bundle.putString("msg","正在处理指标数据表");
                                Message msg = new Message();
                                msg.what = FirstFragment.SYNC_SUCCSE;//
                                msg.setData(bundle);
                                handler.sendMessage(msg);

                                //Toast.makeText(getActivity(), "指标数据同步成功", Toast.LENGTH_SHORT).show();
                            }
                        }else{
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


    /**
     * 登录接口
     *
     * @param optcode   请求码
     * @param username
     * @param pwd
     * @param content   请求json
     */
    void toLogin(final String optcode, String username,String pwd,String content) {

        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(getContext());
        /*if("opt_get_login".equals(optcode)){
            requestHeadStc.setUsercode("50000");
            requestHeadStc.setPassword("a1234567");
        }*/
        requestHeadStc.setOptcode(PropertiesUtil.getProperties(optcode));
        RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, content);

        // 压缩请求数据
        String jsonZip = HttpParseJson.parseRequestJson(reqObj);

        RestClient.builder()
                .url(HttpUrl.IP_END)
                .params("data", jsonZip)
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        String json = HttpParseJson.parseJsonResToString(response);
                        ResponseStructBean resObj = new ResponseStructBean();
                        resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                        // 保存登录信息
                        if(ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())){
                            // 保存信息
                            String formjson = resObj.getResBody().getContent();
                            parseLoginJson(formjson);
                            Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(getActivity(), resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .builde()
                .post();
    }

    // 解析登录者信息
    void parseLoginJson(String json){

        // 保存登录者信息
        BsVisitEmpolyeeStc emp = JsonUtil.parseJson(json, BsVisitEmpolyeeStc.class);

        saveLoginSession(emp, "a1234567", "");

        /*if (emp == null) {
            sendMsg(R.string.login_msg_usererror);//服务器回应的内容为空时界面会收到   用户信息异常，不能正常登录

        } else {
            // 服务器时间与pad端时间差
            long timeDiff = Math.abs(System.currentTimeMillis()- DateUtil.parse(emp.getLoginDate(), "yyyy-MM-dd HH:mm:ss").getTime());

            // 校验用户的定格是否一致
            if (!CheckUtil.isBlankOrNull(loginSession.getGridId())
                    && !loginSession.getGridId().equals(emp.getGridId())) {
                sendMsg(R.string.login_msg_invalgrid);//现在登录的定格与上次的不一样时界面收到      用户所属定格变更，请先清除上次登录账户的缓存数据

            } else if (timeDiff > 5 * 60000) {
                sendMsg(R.string.login_msg_invaldate);

            } else {
                saveLoginSession(emp, pwd, padId);
                ConstValues.loginSession = getLoginSession(context);

                // 保存用户权限到缓存
                PrefUtils.putString(context, "bfgl", emp.getBfgl());
                PrefUtils.putString(context, "yxgl", emp.getYxgl());
                PrefUtils.putString(context, "xtgl", emp.getXtgl());

                // 跳转到平台主界面
                //sendMsg(R.string.login_msg_online, true);
                sendMsg1(R.string.login_msg_online, true,emp.getIsrepassword());//Isrepassword:剩余多少天修改密码 2393版本返回null
            }
        }*/
    }

    /**
     * 记录登录者信息
     *
     * @param emp 登录成功后，返回的当前用户信息
     */
    private void saveLoginSession(BsVisitEmpolyeeStc emp, String pwd, String padId) {
        if (emp != null) {
            PrefUtils.putString(getActivity(), "bigareaid", emp.getBigareaid());// 1-48L5
            PrefUtils.putString(getActivity(), "departmentid", emp.getDepartmentid());//1-4ASL
            PrefUtils.putString(getActivity(), "isDel", emp.getIsDel());// 0
            PrefUtils.putString(getActivity(), "isrepassword", emp.getIsrepassword());//91
            PrefUtils.putString(getActivity(), "loginDate", emp.getLoginDate());// 2018-04-08 14:22:23
            PrefUtils.putString(getActivity(), "pDiscs", emp.getpDiscs());// 1-4ASL,1-48L5,1-47BW,1-39CR,0-R9NH
            PrefUtils.putString(getActivity(), "positionid", emp.getPositionid());// 55ED98C5C2114282AD2A857AB05A73E2
            PrefUtils.putString(getActivity(), "secareaid", emp.getSecareaid());// 1-4ASL
            PrefUtils.putString(getActivity(), "status", emp.getStatus());// 1
            PrefUtils.putString(getActivity(), "usercode", emp.getUsercode());// 50000
            PrefUtils.putString(getActivity(), "userid", emp.getUserid());// 19b1ded5-f853-48ab-aa2b-b12e963c8f9b
            PrefUtils.putString(getActivity(), "username", emp.getUsername());//督导菲菲
            PrefUtils.putString(getActivity(), "userPwd", pwd);//a1234567
        }
    }


    // 解析区域定格路线成功
    private void parseTableJson(String json) {
        // 解析区域定格路线信息
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);
        String mst_grid_m = emp.getMST_GRID_M();
        String mst_marketarea_m = emp.getMST_MARKETAREA_M();
        String mst_route_m = emp.getMST_ROUTE_M();

        MainService service = new MainService(getActivity(),null);
        service.createOrUpdateTable(mst_grid_m,"MST_GRID_M",MstGridM.class);
        service.createOrUpdateTable(mst_marketarea_m,"MST_MARKETAREA_M",MstMarketareaM.class);
        service.createOrUpdateTable(mst_route_m,"MST_ROUTE_M",MstRouteM.class);
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





        MainService service = new MainService(getActivity(),null);
        service.createOrUpdateTable(CMM_DATADIC_M,"CMM_DATADIC_M",CmmDatadicM.class);
        service.createOrUpdateTable(CMM_AREA_M,"CMM_AREA_M",CmmAreaM.class);
        service.createOrUpdateTable(MST_PROMOTIONS_M,"MST_PROMOTIONS_M",MstPromotionsM.class);
        service.createOrUpdateTable(MST_PROMOPRODUCT_INFO,"MST_PROMOPRODUCT_INFO",MstPromoproductInfo.class);
        service.createOrUpdateTable(MST_PICTYPE_M,"MST_PICTYPE_M",MstPictypeM.class);
        service.createOrUpdateTable(MST_PRODUCT_M,"MST_PRODUCT_M",MstProductM.class);
        service.createOrUpdateTable(MST_CMPCOMPANY_M,"MST_CMPCOMPANY_M",MstCmpcompanyM.class);
        service.createOrUpdateTable(MST_CMPBRANDS_M,"MST_CMPBRANDS_M",MstCmpbrandsM.class);
        service.createOrUpdateTable(MST_CMPRODUCTINFO_M,"MST_CMPRODUCTINFO_M",MstCmproductinfoM.class);
        service.createOrUpdateTable(MIT_VALCHECKTER_M,"MIT_VALCHECKTER_M",MitValcheckterM.class);

        service.createOrUpdateTable(MST_AGENCYKF_M,"MST_AGENCYKF_M",MstAgencyKFM.class);
        service.createOrUpdateTable(MST_AGENCYVISIT_M,"MST_AGENCYVISIT_M",MstAgencyvisitM.class);
        service.createOrUpdateTable(MST_INVOICING_INFO,"MST_INVOICING_INFO",MstInvoicingInfo.class);
        service.createOrUpdateTable(MST_VISITAUTHORIZE_INFO,"MST_VISITAUTHORIZE_INFO",MstVisitauthorizeInfo.class);
        service.createOrUpdateTable(MST_AGENCYINFO_M, "MST_AGENCYINFO_M", MstAgencyinfoM.class);

    }

    // 解析指标数据成功
    private void parseIndexTableJson(String json) {
        // 解析指标数据
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);

        String PAD_CHECKSTATUS_INFO = emp.getMST_CHECKSTATUS_INFO();
        String MST_COLLECTIONTEMPLATE_M = emp.getMST_COLLECTIONTEMPLATE_M();

        MainService service = new MainService(getActivity(),null);
        service.createOrUpdateTable(PAD_CHECKSTATUS_INFO,"PAD_CHECKSTATUS_INFO",PadCheckstatusInfo.class);
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
                case SYNC_SUCCSE:// 同步成功返回
                    Bundle bundle = msg.getData();
                    String msgdate = (String) bundle.getSerializable("msg");
                    fragment.closeProgress(msgdate);
                    break;
                case SYNC_START:// 发起同步请求
                    fragment.showFirstDialog("正在同步数据");
                    break;
                case SYNC_CLOSE:// 发起同步请求
                    fragment.closeFirstDialog();
                    break;
            }
        }
    }

    private void  closeProgress(String msgdata){
        count++;
        showFirstDialog(msgdata);
        if(count>=3){
            closeFirstDialog();
            Toast.makeText(getActivity(), "同步成功", Toast.LENGTH_SHORT).show();
        }
        /*closeFirstDialog();
        Toast.makeText(getActivity(), "同步成功", Toast.LENGTH_SHORT).show();*/
    }

    private AlertDialog dialog;
    /**
     * 展示滚动条
     */
    public void showFirstDialog(String msgdata) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.sync_progress, null);
        TextView dialog_tv_sync = (TextView)view.findViewById(R.id.dialog_tv_sync);
        dialog_tv_sync.setText(msgdata);
        if(dialog != null){
            dialog.setView(view, 0, 0, 0, 0);
            dialog.setCancelable(false); // 是否可以通过返回键 关闭
            dialog.show();
        }else{
            dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
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
