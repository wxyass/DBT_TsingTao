package et.tsingtaopad.business.first;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
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
import et.tsingtaopad.http.HttpParseJson;
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
    void ceshiHttp(String optcode,String table) {
        String loginjson = "{usercode:'20000', password:'b1234567',version:'2.5',padid:'dsfwerolkjqiwurywhl'}";

        // 组建请求Json
        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(getContext());
        requestHeadStc.setUsercode("20000");
        requestHeadStc.setPassword("b1234567");
        requestHeadStc.setOptcode(optcode);
        RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, loginjson);

        // 压缩请求数据
        String jsonZip = HttpParseJson.parseRequestJson(reqObj);

        RestClient.builder()
                .url(HttpUrl.IP_END)
                .params("data", jsonZip)
                //.loader(getContext())
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
                            String formjson = resObj.getResBody().getContent();
                            String name = "";

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

    }

}
