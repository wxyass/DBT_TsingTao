package et.tsingtaopad.dd.ddzs.zssayhi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.AlertKeyValueAdapter;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.db.table.MitValterMTemp;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.dd.ddxt.sayhi.XtSayhiFragment;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * Created by yangwenmin on 2018/3/12.
 * <p>
 * 打招呼 录入正确数据
 */

public class ZsAmendFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;
    ZsSayhiFragment.MyHandler handler;

    private Button sureBtn;

    protected String titleName="";// 标题内容,比如:是否有效终端
    protected String ydkey="";// 对象属性,比如: vidroutekey(属性)
    protected String setDdValue ="";// 对象方法名,比如: setVidrtekeyval(督导输入的值)
    protected String setDdFlag ="";// 对象方法名,比如: setVidrtekeyflag(正确与否)
    protected String setDdRemark ="";// 对象方法名,比如: setVidroutremark(备注)
    protected String type="";// 页面类型,比如: "1"
    protected String termId="";// 终端id
    protected String mitValterMTempKey="";// 追溯主表临时表key
    protected MitValterMTemp mitValterMTemp;// 追溯主表信息


    private RelativeLayout zdzs_sayhi_amend_rl;
    private TextView zdzs_sayhi_amend_rl_yd_title;
    private TextView zdzs_sayhi_amend_rl_con1;
    private TextView zdzs_sayhi_amend_rl_con2;
    private TextView zdzs_sayhi_amend_rl_statue;

    private LinearLayout zdzs_sayhi_amend_rl_ll_head;
    private RelativeLayout zdzs_sayhi_amend_rl_dd_head;

    private RelativeLayout zdzs_sayhi_amend_rl_dd_et;
    private TextView zdzs_sayhi_amend_rl_dd_title_et;
    private EditText zdzs_sayhi_amend_rl_dd_con1_et;
    private TextView zdzs_sayhi_amend_rl_con2_et;

    private RelativeLayout zdzs_sayhi_amend_rl_dd_sp;
    private TextView zdzs_sayhi_amend_rl_dd_title_sp;
    private TextView zdzs_sayhi_amend_rl_dd_con1_sp;
    private TextView zdzs_sayhi_amend_rl_dd_con2_sp;

    private EditText zdzs_sayhi_amend_dd_et_report;

    private ZsSayhiService xtSayhiService;

    // 0,1类型
    String oneType ="termstatus,visitstatus";
    // 字符串类型
    String twoType ="termname,termcode,address,person,tel,sequence";
    // 下拉类型
    String thridType ="51,52,53,54,55,56,57,58,59,60";
    // 天/次
    String fourType ="cycle";
    // 件/年
    String fiveType ="hvolume,mvolume,pvolume,lvolume";
    // 所属路线
    String routeType ="belongline";
    // 终端等级
    String LvType ="level";
    // 县
    String countryType ="country";
    // 区域类型
    String areaType ="areatype";
    // 次渠道
    String channerlType ="minorchannel";
    // 拜访对象
    String persionType ="visitperson";


    public ZsAmendFragment() {

    }

    @SuppressLint("ValidFragment")
    public ZsAmendFragment(ZsSayhiFragment.MyHandler handler) {
        this.handler = handler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_sayhi_amend, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        backBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_tv_title);
        confirmBtn.setVisibility(View.INVISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        zdzs_sayhi_amend_rl = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_amend_rl);// 业代数据Rl
        zdzs_sayhi_amend_rl_yd_title = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_yd_title);//
        zdzs_sayhi_amend_rl_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_con1);
        zdzs_sayhi_amend_rl_con2 = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_con2);
        zdzs_sayhi_amend_rl_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_statue);

        zdzs_sayhi_amend_rl_ll_head = (LinearLayout) view.findViewById(R.id.zdzs_sayhi_amend_rl_ll_head);
        zdzs_sayhi_amend_rl_dd_head = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_amend_rl_dd_head);

        zdzs_sayhi_amend_rl_dd_et = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_amend_rl_dd_et);
        zdzs_sayhi_amend_rl_dd_title_et = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_dd_title_et);
        zdzs_sayhi_amend_rl_dd_con1_et = (EditText) view.findViewById(R.id.zdzs_sayhi_amend_rl_dd_con1_et);
        zdzs_sayhi_amend_rl_con2_et = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_con2_et);

        zdzs_sayhi_amend_rl_dd_sp = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_amend_rl_dd_sp);
        zdzs_sayhi_amend_rl_dd_title_sp = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_dd_title_sp);
        zdzs_sayhi_amend_rl_dd_con1_sp = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_dd_con1_sp);
        zdzs_sayhi_amend_rl_dd_con2_sp = (TextView) view.findViewById(R.id.zdzs_sayhi_amend_rl_dd_con2_sp);

        zdzs_sayhi_amend_dd_et_report = (EditText) view.findViewById(R.id.zdzs_sayhi_amend_dd_et_report);

        sureBtn = (Button) view.findViewById(R.id.zdzs_sayhi_amend_dd_bt_save);

        sureBtn.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("录入正确数据");

        xtSayhiService = new ZsSayhiService(getActivity(), handler);

        // 获取传递过来的数据
        Bundle bundle = getArguments();
        titleName = bundle.getString("titleName");
        ydkey = bundle.getString("ydkey");
        setDdValue = bundle.getString("setDdValue");
        setDdFlag = bundle.getString("setDdFlag");
        setDdRemark = bundle.getString("setDdRemark");
        type = bundle.getString("type");
        termId = bundle.getString("termId");
        mitValterMTempKey = bundle.getString("mitValterMTempKey");//bundle.putSerializable("mitValterMTempKey", mitValterMTempKey);// 追溯主键
        mitValterMTemp = (MitValterMTemp) bundle.getSerializable("mitValterMTemp");

        initData();

    }

    private void initData() {
        // 标题
        zdzs_sayhi_amend_rl_yd_title.setText(titleName);
        zdzs_sayhi_amend_rl_dd_title_et.setText(titleName);
        zdzs_sayhi_amend_rl_dd_title_sp.setText(titleName);

        // 业代信息
        if(oneType.contains(type)){
            if (ConstValues.FLAG_1.equals(getFieldValueByFieldName(ydkey,mitValterMTemp))) {
                zdzs_sayhi_amend_rl_con1.setText("是");
            } else {
                zdzs_sayhi_amend_rl_con1.setText("否");
            }
        }

        if(twoType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(getFieldValueByFieldName(ydkey,mitValterMTemp));
        }

        if(thridType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(getFieldValueByFieldName(ydkey,mitValterMTemp));
        }

        if(fourType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(getFieldValueByFieldName(ydkey,mitValterMTemp));
            zdzs_sayhi_amend_rl_con2.setText("天/次");
        }

        if(fiveType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(getFieldValueByFieldName(ydkey,mitValterMTemp));
            zdzs_sayhi_amend_rl_con2.setText("件/年");
        }

        // 所属路线
        if(routeType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(xtSayhiService.getRouteName(getFieldValueByFieldName(ydkey,mitValterMTemp)));
        }
        // 终端等级
        if(LvType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(xtSayhiService.getDatadicName(getFieldValueByFieldName(ydkey,mitValterMTemp)));
        }
        // 县
        if(countryType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(getFieldValueByFieldName(ydkey,mitValterMTemp));
        }
        // 区域类型
        if(areaType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(xtSayhiService.getDatadicName(getFieldValueByFieldName(ydkey,mitValterMTemp)));
        }
        // 次渠道
        if(channerlType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(xtSayhiService.getDatadicName(getFieldValueByFieldName(ydkey,mitValterMTemp)));
        }
        // 拜访对象
        if(persionType.contains(type)){
            zdzs_sayhi_amend_rl_con1.setText(getFieldValueByFieldName(ydkey,mitValterMTemp));
        }

        // 督导数据
        // 是否显示督导数据,显示输入框
        if(twoType.contains(type)||fourType.contains(type)||fiveType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_et.setVisibility(View.VISIBLE);
        }
        if(fourType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_et.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_con1_et.setMaxWidth(10);
            zdzs_sayhi_amend_rl_con2_et.setText("天/次");
        }
        if(fiveType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_et.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_con1_et.setWidth(10);
            zdzs_sayhi_amend_rl_con2_et.setText("件/年");
        }

        // 所属路线
        if(routeType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_sp.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_sp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectRouteMRight(xtSayhiService.initXtMstRoute(termId),getFieldValueByFieldName(ydkey,mitValterMTemp));
                }
            });
        }
        // 终端等级
        if(LvType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_sp.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_sp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectValueRight(xtSayhiService.initDataDicByTermLevel(),getFieldValueByFieldName(ydkey,mitValterMTemp));
                }
            });
        }
        // 县
        if(countryType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_sp.setVisibility(View.VISIBLE);
        }
        // 区域类型
        if(areaType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_sp.setVisibility(View.VISIBLE);

            zdzs_sayhi_amend_rl_dd_sp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectValueRight(xtSayhiService.initDataDicByAreaType(),getFieldValueByFieldName(ydkey,mitValterMTemp));
                }
            });
        }
        // 次渠道
        if(channerlType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_sp.setVisibility(View.VISIBLE);
        }
        // 拜访对象
        if(persionType.contains(type)){
            zdzs_sayhi_amend_rl_ll_head.setVisibility(View.VISIBLE);
            zdzs_sayhi_amend_rl_dd_sp.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_tv_title:// 标题
                break;
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();// 取消
                break;

            case R.id.zdzs_sayhi_amend_dd_bt_save:// 确定
                saveValue();
                supportFragmentManager.popBackStack();
                break;

            default:
                break;
        }
    }

    private void saveValue() {


        // 输入框
        if(twoType.contains(type)||fourType.contains(type)||fiveType.contains(type)){
            String  termName = zdzs_sayhi_amend_rl_dd_con1_et.getText().toString();
            setFieldValue(mitValterMTemp, setDdValue,termName);
        }

        // 所属路线
        if(routeType.contains(type)){
            String  key = (String)zdzs_sayhi_amend_rl_dd_con1_sp.getTag();
            setFieldValue(mitValterMTemp, setDdValue,key);
        }
        // 终端等级
        if(LvType.contains(type)){
            String  key = (String)zdzs_sayhi_amend_rl_dd_con1_sp.getTag();
            setFieldValue(mitValterMTemp, setDdValue,key);
        }
        // 县
        if(countryType.contains(type)){
            String  key = (String)zdzs_sayhi_amend_rl_dd_con1_sp.getTag();
            setFieldValue(mitValterMTemp, setDdValue,key);
        }
        // 区域类型
        if(areaType.contains(type)){
            String  key = (String)zdzs_sayhi_amend_rl_dd_con1_sp.getTag();
            setFieldValue(mitValterMTemp, setDdValue,key);
        }
        // 次渠道
        if(channerlType.contains(type)){
            String  key = (String)zdzs_sayhi_amend_rl_dd_con1_sp.getTag();
            setFieldValue(mitValterMTemp, setDdValue,key);
        }
        // 拜访对象
        if(persionType.contains(type)){
            String  key = (String)zdzs_sayhi_amend_rl_dd_con1_sp.getTag();
            setFieldValue(mitValterMTemp, setDdValue,key);
        }

        // 保存是否正确,备注内容
        String remark = zdzs_sayhi_amend_dd_et_report.getText().toString();
        setFieldValue(mitValterMTemp, setDdFlag,"N");
        setFieldValue(mitValterMTemp, setDdRemark,remark);

        handler.sendEmptyMessage(ZsSayhiFragment.INIT_DATA);

    }


    // 写入
    private <T> void setFieldValue(T data,String key,String value) {
        Class<? extends Object> clazz = data.getClass();
        try {
            Method padisconsistentMethod = data.getClass().getMethod(key, String.class);
            padisconsistentMethod.invoke(data, value);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName
     * @param object
     * @return
     */
    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return  (String)field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    AlertView mAlertViewExt;
    // 路线选择
    private void setSelectRouteMRight(final List<MstRouteM> mstRouteList, String routekey){
        mAlertViewExt = new AlertView("请选择路线", null, null, null, null, getActivity(), AlertView.Style.ActionSheet, null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
        ListView listview = (ListView) extView.findViewById(R.id.alert_list);
        AlertKeyValueAdapter keyValueAdapter = new AlertKeyValueAdapter(getActivity(), mstRouteList,
                new String[]{"routekey", "routename"}, routekey);
        listview.setAdapter(keyValueAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                zdzs_sayhi_amend_rl_dd_con1_sp.setText(mstRouteList.get(position).getRoutename());
                zdzs_sayhi_amend_rl_dd_con1_sp.setTag(mstRouteList.get(position).getRoutekey());
                mAlertViewExt.dismiss();
            }
        });
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.setCancelable(true).setOnDismissListener(null);
        mAlertViewExt.show();
    }

    // 路线选择
    private void setSelectValueRight(final List<KvStc> dataDic, String routekey){
        mAlertViewExt = new AlertView("请正确值", null, null, null, null, getActivity(), AlertView.Style.ActionSheet, null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
        ListView listview = (ListView) extView.findViewById(R.id.alert_list);
        AlertKeyValueAdapter keyValueAdapter = new AlertKeyValueAdapter(getActivity(), dataDic,
                new String[]{"key", "value"}, routekey);
        listview.setAdapter(keyValueAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                zdzs_sayhi_amend_rl_dd_con1_sp.setText(dataDic.get(position).getValue());
                zdzs_sayhi_amend_rl_dd_con1_sp.setTag(dataDic.get(position).getKey());
                mAlertViewExt.dismiss();
            }
        });
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.setCancelable(true).setOnDismissListener(null);
        mAlertViewExt.show();
    }
}
