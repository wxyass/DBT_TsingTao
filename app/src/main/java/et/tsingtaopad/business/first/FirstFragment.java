package et.tsingtaopad.business.first;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.db.table.CmmAreaM;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MstAgencygridInfo;
import et.tsingtaopad.db.table.MstAgencyinfoM;
import et.tsingtaopad.db.table.MstCmpbrandsM;
import et.tsingtaopad.db.table.MstCmpcompanyM;
import et.tsingtaopad.db.table.MstCmproductinfoM;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstPictypeM;
import et.tsingtaopad.db.table.MstProductM;
import et.tsingtaopad.db.table.MstProductareaInfo;
import et.tsingtaopad.db.table.MstPromoproductInfo;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.PadCheckaccomplishInfo;
import et.tsingtaopad.db.table.PadCheckproInfo;
import et.tsingtaopad.db.table.PadCheckstatusInfo;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.home.app.MainService;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.login.domain.BsVisitEmpolyeeStc;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class FirstFragment extends BaseFragmentSupport implements View.OnClickListener {

    private final String TAG = "FirstFragment";

    AppCompatButton login;
    AppCompatButton syncgrid;
    AppCompatButton syncindex;
    AppCompatButton syncpro;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        login = view.findViewById(R.id.btn_first_login);
        syncgrid = view.findViewById(R.id.btn_first_sync_grid);
        syncindex = view.findViewById(R.id.btn_first_sync_index);
        syncpro = view.findViewById(R.id.btn_first_sync_pro);
        login.setOnClickListener(this);
        syncgrid.setOnClickListener(this);
        syncindex.setOnClickListener(this);
        syncpro.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_first_login:// 登录
                String   loginjson = "{usercode:'50000', password:'a1234567',version:'2.5',padid:'dsfwerolkjqiwurywhl'}";
                toLogin("opt_get_login","","",loginjson);
                break;
            case R.id.btn_first_sync_grid:// 同步 定格,路线
                String content  = "{"+
                        "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                        "tablename:'"+"MST_MARKETAREA_GRID_ROUTE_M"+"'" +
                        "}";
                ceshiHttp("opt_get_dates2","MST_MARKETAREA_GRID_ROUTE_M",content);
                break;
            case R.id.btn_first_sync_index:// 同步 指标模板


                String content2  = "{"+
                        "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                        "tablename:'"+"MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO"+"'" +
                        "}";
                ceshiHttp("opt_get_dates2","MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO",content2);
                break;
            case R.id.btn_first_sync_pro:// 同步 基础数据表
                String content1  = "{"+
                        "areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                        "tablename:'"+"MST_BASEDATA_M"+"'" +
                        "}";
                ceshiHttp("opt_get_dates2","MST_BASEDATA_M",content1);
                break;
            default:
                break;
        }
    }

    // 测试登录网络框架

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
        if("opt_get_login".equals(optcode)){
            requestHeadStc.setUsercode("50000");
            requestHeadStc.setPassword("a1234567");
        }
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
                        //Toast.makeText(getActivity(), resObj.getResBody().getContent()+""+resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                        // 保存登录信息
                        if(ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())){
                            // 保存信息
                            if("opt_get_dates2".equals(optcode)&&"MST_MARKETAREA_GRID_ROUTE_M".equals(table)){
                                String formjson = resObj.getResBody().getContent();
                                parseTableJson(formjson);
                                Toast.makeText(getActivity(), "区域定格路线同步成功", Toast.LENGTH_SHORT).show();
                            }
                            if("opt_get_dates2".equals(optcode)&&"MST_BASEDATA_M".equals(table)){
                                String formjson = resObj.getResBody().getContent();
                                parseDatadicTableJson(formjson);
                                Toast.makeText(getActivity(), "基础数据同步成功", Toast.LENGTH_SHORT).show();
                            }
                            if("opt_get_dates2".equals(optcode)&&"MST_COLLECTIONTEMPLATE_CHECKSTATUS_INFO".equals(table)){
                                String formjson = resObj.getResBody().getContent();
                                parseIndexTableJson(formjson);
                                Toast.makeText(getActivity(), "指标数据同步成功", Toast.LENGTH_SHORT).show();
                            }
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
        if("opt_get_login".equals(optcode)){
            requestHeadStc.setUsercode("50000");
            requestHeadStc.setPassword("a1234567");
        }
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

}
