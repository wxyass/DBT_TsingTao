package et.tsingtaopad.dd.ddzs.zscheckindex;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.AlertKeyValueAdapter;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnDismissListener;
import et.tsingtaopad.core.view.alertview.OnItemClickListener;
import et.tsingtaopad.db.table.MstGroupproductMTemp;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.checking.XtCaculateAdapter;
import et.tsingtaopad.dd.ddxt.checking.XtCheckIndexService;
import et.tsingtaopad.dd.ddxt.checking.XtPromotionAdapter;
import et.tsingtaopad.dd.ddxt.checking.domain.XtCheckIndexCalculateStc;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.checking.num.XtQuickCollectFragment;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.dd.ddzs.zscheckindex.zsnum.ZsQuickCollectFragment;
import et.tsingtaopad.dd.ddzs.zsshopvisit.ZsVisitShopActivity;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class ZsCheckIndexFragment extends XtBaseVisitFragment implements View.OnClickListener ,OnItemClickListener, OnDismissListener {

    private final String TAG = "ZsCheckIndexFragment";

    private AppCompatButton mXtvisit_btn_pro;

    private ListView calculateLv;
    private ListView promotionLv;
    private Button quickCollectBt;

    private XtCheckIndexService service;

    private List<CheckIndexPromotionStc> promotionLst;
    private MstTerminalinfoMTemp term;

    public static final int INPUT_SUC = 3;
    MyHandler handler;
    ZsCaculateAdapter xtCaculateAdapter;

    private AlertView mAlertViewExt;//窗口拓展例子

    String zhanyoulvIndexValueId;//当前终端单店占有率 对应cstatuskey
    MstGroupproductMTemp vo;


    private RelativeLayout zdzs_sayhi_rl_prostatus;
    private TextView zdzs_sayhi_tv_prostatus_con1;
    private TextView zdzs_sayhi_tv_prostatus_statue;
    private RelativeLayout zdzs_sayhi_rl_hezuo;
    private TextView zdzs_sayhi_tv_hezuo_con1;
    private TextView zdzs_sayhi_tv_hezuo_statue;
    private RelativeLayout zdzs_sayhi_rl_peisong;
    private TextView zdzs_sayhi_tv_peisong_con1;
    private TextView zdzs_sayhi_tv_peisong_statue;
    private RelativeLayout zdzs_sayhi_rl_zhanyoulv;
    private TextView zdzs_sayhi_tv_zhanyoulv_con1;
    private TextView zdzs_sayhi_tv_zhanyoulv_statue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_checkindex, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

        //采项分集listView
        calculateLv = (ListView) view.findViewById(R.id.zdzs_checkindex_lv_calculate);
        quickCollectBt = (Button) view.findViewById(R.id.zdzs_checkindex_bt_quickcollect);
        promotionLv = (ListView) view.findViewById(R.id.zdzs_checkindex_lv_promotion);


        zdzs_sayhi_rl_prostatus = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_prostatus);
        zdzs_sayhi_tv_prostatus_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_prostatus_con1);
        zdzs_sayhi_tv_prostatus_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_prostatus_statue);
        zdzs_sayhi_rl_hezuo = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_hezuo);
        zdzs_sayhi_tv_hezuo_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_hezuo_con1);
        zdzs_sayhi_tv_hezuo_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_hezuo_statue);
        zdzs_sayhi_rl_peisong = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_peisong);
        zdzs_sayhi_tv_peisong_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_peisong_con1);
        zdzs_sayhi_tv_peisong_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_peisong_statue);
        zdzs_sayhi_rl_zhanyoulv = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_zhanyoulv);
        zdzs_sayhi_tv_zhanyoulv_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_zhanyoulv_con1);
        zdzs_sayhi_tv_zhanyoulv_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_zhanyoulv_statue);



        quickCollectBt.setOnClickListener(this);
        zdzs_sayhi_rl_prostatus.setOnClickListener(this);
        zdzs_sayhi_rl_hezuo.setOnClickListener(this);
        zdzs_sayhi_rl_peisong.setOnClickListener(this);
        zdzs_sayhi_rl_zhanyoulv.setOnClickListener(this);
    }

    List<XtProIndex> calculateLst = new ArrayList<XtProIndex>();
    List<KvStc> indexValuelst = new ArrayList<KvStc>();
    List<XtProItem> proItemLst = new ArrayList<XtProItem>();

    List<XtCheckIndexCalculateStc> noProIndexLst;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Toast.makeText(getActivity(), "查指标" + "/" + termId + "/" + termName, Toast.LENGTH_SHORT).show();

        // 指标模拟数据
        service = new XtCheckIndexService(getActivity(), null);
        handler = new MyHandler(this);
        // 查询最新终端临时表数据
        term = service.findTermTempById(termId);
        // 获取最新的终端数据

        channelId = term.getMinorchannel();// 次渠道


        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 // 铺货状态,道具生动化,产品生动化,冰冻化
        calculateLst = service.queryCalculateIndex(visitId, termId, term.getMinorchannel(), seeFlag);
        // 获取分项采集部分的产品指标对应的采集项目数据 // 因为在ShopVisitActivity生成了供货关系,此时就能关联出各个产品的采集项,现有量变化量为0
        proItemLst = service.queryCalculateItem(visitId, channelId);

        // 初始化指标、指标值树级关系 对象
        service.initCheckTypeStatus();
        indexValuelst = GlobalValues.indexLst;

        xtCaculateAdapter = new ZsCaculateAdapter(getActivity(), calculateLst, indexValuelst, proItemLst, handler);
        calculateLv.setAdapter(xtCaculateAdapter);
        ViewUtil.setListViewHeight(calculateLv);

        // 促销活动
        promotionLst = service.queryPromotion(visitId, term.getSellchannel(), term.getTlevel());
        ZsPromotionAdapter xtPromotionAdapter = new ZsPromotionAdapter(getActivity(), promotionLst, "2018-03-30", seeFlag);
        promotionLv.setAdapter(xtPromotionAdapter);

        // 其他信息
        noProIndexLst = service.queryNoProIndex12(visitId, term.getMinorchannel(), seeFlag);
        // 初始化 合作是否到位 占有率 高质量配送数据
        for (XtCheckIndexCalculateStc item : noProIndexLst) {
            // 重新查询指标关联指标值 //因为原先的这3个指标 不再关联指标值(pad_checktype_m不再同步这三个指标)
            // 交由pad直接初始化 // ywm 20160407
            if ("666b74b3-b221-4920-b549-d9ec39a463fd".equals(item.getIndexId())) {// 合作是否到位
                //tempLst = service.queryNoProIndexValueId1();// 此处报错
                if ("9019cf03-4572-4559-9971-48a27a611c3d".equals(item.getIndexValueId())) {// 合作是否到位 是
                    zdzs_sayhi_tv_hezuo_con1.setText("是");
                }else{
                    zdzs_sayhi_tv_hezuo_con1.setText("否");
                }
            } else if ("df2e88c9-246f-40e2-b6e5-08cdebf8c281".equals(item.getIndexId())) {// 是否高质量配送
                //tempLst = service.queryNoProIndexValueId2();
                if ("460647a9-283a-44ea-b11f-42efe1fd62e4".equals(item.getIndexValueId())) {// 是否高质量配送
                    zdzs_sayhi_tv_peisong_con1.setText("是");
                }else{
                    zdzs_sayhi_tv_peisong_con1.setText("否");
                }
            } else if ("59802090-02ac-4146-9cc3-f09570c36a26".equals(item.getIndexId())) {// 我品占有率
                 zhanyoulvIndexValueId = item.getIndexValueId();
                zdzs_sayhi_tv_zhanyoulv_con1.setText(service.getCheckStatusName(zhanyoulvIndexValueId));
            }
        }

        // 产品组合是否达标
        vo = service.findMstGroupproductMTempByid(term.getTerminalcode());
        if (CheckUtil.isBlankOrNull(vo.getIfrecstand()) || "N".equals(vo.getIfrecstand()) || "null".equals(vo.getIfrecstand())) {
            zdzs_sayhi_tv_prostatus_con1.setText("否");
        }else{
            zdzs_sayhi_tv_prostatus_con1.setText("是");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zdzs_checkindex_bt_quickcollect:// 快速采集
                if (ViewUtil.isDoubleClick(v.getId(), 2500)) return;

                Bundle bundle = new Bundle();
                bundle.putSerializable("termId", termStc.getTerminalkey());
                bundle.putSerializable("termname", termStc.getTerminalname());
                bundle.putSerializable("channelId", termStc.getMinorchannel());// 次渠道
                bundle.putSerializable("termStc", termStc);
                bundle.putSerializable("visitKey", visitId);//visitId
                bundle.putSerializable("proItemLst", (Serializable) proItemLst);// 默认0   0:拜访 1:查看

                ZsQuickCollectFragment xtquickcollectfragment = new ZsQuickCollectFragment(handler);
                xtquickcollectfragment.setArguments(bundle);

                ZsVisitShopActivity xtVisitShopActivity = (ZsVisitShopActivity) getActivity();
                xtVisitShopActivity.changeXtvisitFragment(xtquickcollectfragment, "xtnuminputfragment");
                break;

            case R.id.zdzs_sayhi_rl_zhanyoulv:// 单店占有率
                final List<KvStc> tempLst = service.queryNoProIndexValueId31();
                mAlertViewExt = new AlertView("请选择单店占有率", null,
                        null, null, null, getActivity(), AlertView.Style.ActionSheet, this);
                ViewGroup areaextView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
                ListView arealistview = (ListView) areaextView.findViewById(R.id.alert_list);
                AlertKeyValueAdapter areakeyValueAdapter = new AlertKeyValueAdapter(getActivity(), tempLst,
                        new String[]{"key", "value"}, zhanyoulvIndexValueId);
                arealistview.setAdapter(areakeyValueAdapter);
                arealistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //xtZhanyoulvTv.setText(tempLst.get(position).getValue());
                        zhanyoulvIndexValueId = tempLst.get(position).getKey();
                        mAlertViewExt.dismiss();
                    }
                });
                mAlertViewExt.addExtView(areaextView);
                mAlertViewExt.setCancelable(true).setOnDismissListener(this);
                mAlertViewExt.show();

                break;
            case R.id.zdzs_sayhi_rl_prostatus:
            case R.id.zdzs_sayhi_rl_hezuo:
            case R.id.zdzs_sayhi_rl_peisong:
                alertShow3();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {

    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<ZsCheckIndexFragment> fragmentRef;

        public MyHandler(ZsCheckIndexFragment fragment) {
            fragmentRef = new SoftReference<ZsCheckIndexFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ZsCheckIndexFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            Bundle bundle = msg.getData();


            switch (msg.what) {
                case INPUT_SUC:// 自动计算指标值
                    fragment.autoCalculateSuc(bundle);
                    break;
            }
        }
    }

    /**
     * 自动计算指标值
     */
    public void autoCalculateSuc(Bundle bundle) {

        String proId = "";
        String indexId = "";
        if (bundle != null) {
            proId = FunUtil.isBlankOrNullTo(bundle.getString("proId"), "-1");// 产品主键
            indexId = FunUtil.isBlankOrNullTo(bundle.getString("indexId"), "-1");// 指标主键: ad3030fb-e42e-47f8-a3ec-4229089aab5d
        }
        service.calculateIndex(channelId, proItemLst, calculateLst, proId, indexId);
        xtCaculateAdapter.notifyDataSetChanged();
        ViewUtil.setListViewHeight(calculateLv);
    }

    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        long time3 = new Date().getTime();
        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag)) return;
        //if(isLoadingData) return;

        /*
        XtProIndex indexItem;
        XtProIndexValue valueItem;
        ListView indexValueLv;
        KvStc kvItem;
        RadioGroup resultRg;
        RadioButton resultRb;
        EditText resultEt;
        Spinner resultSp;

        // 遍历lv，获取各指标的指标值
        for (int i = 0; i < calculateLst.size(); i++) {
            if (calculateLv == null || calculateLv.getChildAt(i) == null || calculateLv.getChildAt(i).findViewById(R.id.caculate_lv_indexvalue) == null)
                continue;
            indexItem = calculateLst.get(i);
            indexValueLv = (ListView) calculateLv.getChildAt(i).findViewById(R.id.item_xt_caculate_lv_indexvalue);
            if (indexValueLv == null)
                continue;
            for (int j = 0; j < indexItem.getIndexValueLst().size(); j++) {
                valueItem = indexItem.getIndexValueLst().get(j);
                if (valueItem == null)
                    continue;


                if (indexValueLv.getChildAt(j).findViewById(R.id.item_xt_checkindex_indexvalue) == null)
                    continue;
                valueItem.setIndexValueId(FunUtil.isBlankOrNullTo(indexValueLv.getChildAt(j).findViewById(R.id.item_xt_checkindex_indexvalue).getTag(), "-1"));
            }
        }

        // 遍历获取与产品无关指标采集数据部分
        XtCheckIndexCalculateStc itemStc;
        View itemV;

        // 根据页面上用户选的值  保存数据到表
        for(int i = 0; i < noProIndexLst.size(); i++) {
            itemStc = noProIndexLst.get(i);
            if ("666b74b3-b221-4920-b549-d9ec39a463fd".equals(itemStc.getIndexId())) {// 合作是否到位
                if (xtHezuoSw.getStatus()) {// 合作是否到位
                    itemStc.setIndexValueId("9019cf03-4572-4559-9971-48a27a611c3d");//合作是否到位 shi
                } else  {
                    itemStc.setIndexValueId("8d36d1e5-c776-452e-8893-589ad786d71d");		// 合作是否到位否															// 是
                }
            } else if ("df2e88c9-246f-40e2-b6e5-08cdebf8c281".equals(itemStc.getIndexId())) {// 是否高质量配送
                if (xtPeisongSw.getStatus()) {// 合作是否到位
                    itemStc.setIndexValueId("460647a9-283a-44ea-b11f-42efe1fd62e4");//是否高质量配送 shi
                } else  {
                    itemStc.setIndexValueId("bf600cfe-f70d-4170-857d-65dd59740d57");		// 是否高质量配送否															// 是
                }

            } else if ("59802090-02ac-4146-9cc3-f09570c36a26".equals(itemStc.getIndexId())) {// 我品占有率
                //kvItem = (KvStc)zhanyoulvSp.getSelectedItem();
                itemStc.setIndexValueId(zhanyoulvIndexValueId);
            }
        }

        // 保存查指标页面的数据
        service.saveCheckIndex(visitId, termId, calculateLst, proItemLst, noProIndexLst);

        // 获取产品组合是否达标值
        if(xtProstatusSw.getStatus()){
            vo.setIfrecstand("Y");// 产品组合  未达标
        }else{
            vo.setIfrecstand("N");// 产品组合  达标
        }
        // 保存产品组合是否达标
        service.saveMstGroupproductMTemp(vo);


        // 遍历活动状态的达成情况
        et.tsingtaopad.view.DdSlideSwitch statusSw;
        EditText reachNum;
        for (int i = 0; i < promotionLst.size(); i++) {
            itemV = promotionLv.getChildAt(i);
            if (itemV == null || itemV.findViewById(R.id.item_xt_checkindex_sw_isacomplish) == null) continue;
            statusSw = (et.tsingtaopad.view.DdSlideSwitch)itemV.findViewById(R.id.item_xt_checkindex_sw_isacomplish);
            reachNum = (EditText)itemV.findViewById(R.id.item_xt_checkindex_et_zushu);
            if (statusSw.getStatus()) {
                promotionLst.get(i).setIsAccomplish(ConstValues.FLAG_1);//达成
            } else {
                promotionLst.get(i).setIsAccomplish(ConstValues.FLAG_0);//未达成
                // 删除本地照片(以为每次确定上传都会删除文件夹,就不做操作了)
            }
            promotionLst.get(i).setReachNum(reachNum.getText().toString());

        }
        service.saveXtPromotionTemp(visitId, termId, promotionLst);
        long time4= new Date().getTime();
        Log.e("Optimization", "查指标执行数据库"+(time4-time3));*/
    }

    /**
     * 弹窗3
     * 参数1: 标题 ×
     * 参数2: 主体内容    ×
     * 参数3: 取消按钮    ×
     * 参数4: 高亮按钮 数组 √
     * 参数5: 普通按钮 数组 √
     * 参数6: 上下文 √
     * 参数7: 弹窗类型 (正常取消,确定按钮)   √
     * 参数8: 条目点击监听  √
     */
    public void alertShow3() {
        List<KvStc> sureOrFail = new ArrayList<>();
        sureOrFail.add(new KvStc("zhengque","正确","-1"));
        sureOrFail.add(new KvStc("cuowu","错误(去修正)","-1"));
        mAlertViewExt = new AlertView(null, null, null, null,
                null, getActivity(), AlertView.Style.ActionSheet, null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
        ListView listview = (ListView) extView.findViewById(R.id.alert_list);
        AlertKeyValueAdapter keyValueAdapter = new AlertKeyValueAdapter(getActivity(), sureOrFail,
                new String[]{"key", "value"}, "zhengque");
        listview.setAdapter(keyValueAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(1==position){
                    /*Bundle bundle = new Bundle();
                    bundle.putString("proName", "");
                    ZsSayhiAmendFragment zsAmendFragment = new ZsSayhiAmendFragment(handler);
                    zsAmendFragment.setArguments(bundle);
                    ZsVisitShopActivity zsVisitShopActivity = (ZsVisitShopActivity)getActivity();
                    zsVisitShopActivity.changeXtvisitFragment(zsAmendFragment,"zsamendfragment");*/
                }

                mAlertViewExt.dismiss();
            }
        });
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                DbtLog.logUtils(TAG, "取消选择结果");
            }
        });
        mAlertViewExt.show();
    }

}
