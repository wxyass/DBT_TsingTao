package et.tsingtaopad.dd.ddxt.checking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.checking.num.XtCaculateFragment;
import et.tsingtaopad.dd.ddxt.checking.num.XtQuickCollectFragment;
import et.tsingtaopad.dd.ddxt.invoicing.XtInvoicingFragment;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtCheckIndexFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private AppCompatButton mXtvisit_btn_pro;

    private ListView calculateLv;
    private ListView promotionLv;
    private Button quickCollectBt;

    private XtCheckIndexService service;

    private List<CheckIndexPromotionStc> promotionLst;
    private MstTerminalinfoMTemp term;

    public static final int INPUT_SUC = 3;
    MyHandler handler;
    XtCaculateAdapter xtCaculateAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_checkindex, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

        //采项分集listView
        calculateLv = (ListView) view.findViewById(R.id.xtbf_checkindex_lv_calculate);
        quickCollectBt = (Button) view.findViewById(R.id.xtbf_checkindex_bt_quickcollect);
        promotionLv = (ListView) view.findViewById(R.id.xtbf_checkindex_lv_promotion);

        quickCollectBt.setOnClickListener(this);

    }

    List<XtProIndex> calculateLst = new ArrayList<XtProIndex>();
    List<KvStc> indexValuelst = new ArrayList<KvStc>();
    List<XtProItem> proItemLst = new ArrayList<XtProItem>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Toast.makeText(getActivity(), "查指标" + "/" + termId + "/" + termName, Toast.LENGTH_SHORT).show();

        // 指标模拟数据
        service = new XtCheckIndexService(getActivity(), null);
        handler = new MyHandler(this);
        // 查询最新终端临时表数据
        term = service.findTermTempById(termId);


        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 // 铺货状态,道具生动化,产品生动化,冰冻化
        calculateLst = service.queryCalculateIndex(visitId, termId, term.getMinorchannel(), seeFlag);
        // 获取分项采集部分的产品指标对应的采集项目数据 // 因为在ShopVisitActivity生成了供货关系,此时就能关联出各个产品的采集项,现有量变化量为0
        proItemLst = service.queryCalculateItem(visitId, channelId);

        // 初始化指标、指标值树级关系 对象
        service.initCheckTypeStatus();
        indexValuelst = GlobalValues.indexLst;

        xtCaculateAdapter = new XtCaculateAdapter(getActivity(), calculateLst, indexValuelst, proItemLst, handler);
        calculateLv.setAdapter(xtCaculateAdapter);
        ViewUtil.setListViewHeight(calculateLv);

        promotionLst = service.queryPromotion(visitId, term.getSellchannel(), term.getTlevel());

        // 促销活动 模拟数据
        XtPromotionAdapter xtPromotionAdapter = new XtPromotionAdapter(getActivity(), promotionLst, "2018-03-30", seeFlag);
        promotionLv.setAdapter(xtPromotionAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xtbf_checkindex_bt_quickcollect:
                XtVisitShopActivity xtVisitShopActivity = (XtVisitShopActivity) getActivity();
                xtVisitShopActivity.changeXtvisitFragment(new XtQuickCollectFragment(), "xtnuminputfragment");
                break;

            default:
                break;
        }
    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<XtCheckIndexFragment> fragmentRef;

        public MyHandler(XtCheckIndexFragment fragment) {
            fragmentRef = new SoftReference<XtCheckIndexFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            XtCheckIndexFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            Bundle bundle = msg.getData();

            // 处理UI 变化
            switch (msg.what) {
                case INPUT_SUC:
                    fragment.showAddProSuc(bundle);
                    break;
            }
        }
    }

    /**
     * 添加产品成功 UI
     */
    public void showAddProSuc(Bundle bundle) {

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

}
