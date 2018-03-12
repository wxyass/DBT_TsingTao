package et.tsingtaopad.main.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import et.tsingtaopad.R;
import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.net.domain.ResponseStructBean;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.home.delete.BaseBottomCoreFragment;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.util.requestHeadUtil;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: 首页首页</br>
 */
public class HomeCoreFragment extends BaseBottomCoreFragment {

    public static final String TAG = "HomeCoreFragment";

    public HomeCoreFragment() {
        // Required empty public constructor
    }

    public static HomeCoreFragment newInstance() {

        Bundle args = new Bundle();

        HomeCoreFragment fragment = new HomeCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DbtLog.logUtils(TAG,"onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        DbtLog.logUtils(TAG,"onLazyInitView");
        super.onLazyInitView(savedInstanceState);


    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG,"onSupportVisible");
        // 每次页面出现后都重新加载数据
        initData();
    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG,"initData");
        //ceshiHttp();
    }

    // 测试登录网络框架
    void ceshiHttp(){
        String loginjson = "{usercode:'20000', password:'b1234567',version:'2.3.4.3.2',padid:'dsfwerolkjqiwurywhl'}";

        // 组建请求Json
        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(getContext());
        requestHeadStc.setUsercode("20000");
        requestHeadStc.setPassword("b1234567");
        requestHeadStc.setOptcode("get_login_3");
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
                        String json  = HttpParseJson.parseJsonResToString(response);
                        ResponseStructBean resObj = new ResponseStructBean();
                        resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                        Toast.makeText(getActivity(),resObj.getResBody().getContent(),Toast.LENGTH_SHORT).show();
                        // 保存登录信息
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

}
