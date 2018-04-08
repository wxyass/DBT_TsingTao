package et.tsingtaopad.business.first;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.db.table.MstAgencygridInfo;
import et.tsingtaopad.db.table.MstAgencyinfoM;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstPictypeM;
import et.tsingtaopad.db.table.MstProductM;
import et.tsingtaopad.db.table.MstProductareaInfo;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.home.app.MainService;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.login.domain.BsVisitEmpolyeeStc;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class FirstFragment extends BaseFragmentSupport implements View.OnClickListener {
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
            case R.id.btn_first_login:
                ceshiHttp("get_login_3","");
                break;
            case R.id.btn_first_sync_grid:
                ceshiHttp("get_date_3","MST_MARKETAREA_GRID_ROUTE_M");
                break;
            case R.id.btn_first_sync_index:
                ceshiHttp("get_index_3","");
                break;
            case R.id.btn_first_sync_pro:
                ceshiHttp("get_pro_3","");
                break;
            default:
                break;
        }
    }

    // 测试登录网络框架
    void ceshiHttp(final String optcode, final String table) {

        String loginjson = "";
        if("get_login_3".equals(optcode)&&"".equals(table)){
            loginjson = "{usercode:'50000', password:'a1234567',version:'2.5',padid:'dsfwerolkjqiwurywhl'}";
        }

        if("get_date_3".equals(optcode)&&"MST_MARKETAREA_GRID_ROUTE_M".equals(table)){
            loginjson =  "{areaid:'"+PrefUtils.getString(getActivity(),"departmentid","")+"'," +
                    "gridKey:'163UNDF'," +
                    "syncDay:'0'," +
                    "synctime:''," +
                    "updatetime:''," +
                    "remarks:''," +
                    "tablename:'MST_MARKETAREA_GRID_ROUTE_M'," +
                    "userId:'50000'}";
        }

        // 组建请求Json
        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(getContext());
        requestHeadStc.setUsercode("50000");
        requestHeadStc.setPassword("a1234567");
        requestHeadStc.setOptcode(optcode);
        RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, loginjson);

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
                            if("get_login_3".equals(optcode)&&"".equals(table)){
                                String formjson = resObj.getResBody().getContent();
                                parseJson(formjson);
                                Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
                            }

                            if("get_date_3".equals(optcode)&&"MST_MARKETAREA_GRID_ROUTE_M".equals(table)){
                                String formjson = resObj.getResBody().getContent();
                                parseTableJson(formjson);
                                Toast.makeText(getActivity(), "区域定格路线成功", Toast.LENGTH_SHORT).show();
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

    void parseJson(String json){


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

    // 解析同步表
    private void parseTableJson(String json) {
        // 保存登录者信息
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);
        String mst_grid_m = emp.getMST_GRID_M();
        String mst_marketarea_m = emp.getMST_MARKETAREA_M();
        String mst_route_m = emp.getMST_ROUTE_M();

        MainService service = new MainService(getActivity(),null);
        service.createOrUpdateTable(mst_grid_m,"MST_GRID_M",MstGridM.class);
        service.createOrUpdateTable(mst_marketarea_m,"MST_MARKETAREA_M",MstMarketareaM.class);
        service.createOrUpdateTable(mst_route_m,"MST_ROUTE_M",MstRouteM.class);
    }
}
