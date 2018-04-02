package et.tsingtaopad.dd.ddxt.checking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.checking.num.XtCaculateFragment;
import et.tsingtaopad.dd.ddxt.checking.num.XtQuickCollectFragment;
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

        Toast.makeText(getActivity(), "查指标" + "/" + termId + "/" + termName, Toast.LENGTH_SHORT).show();

        // 指标模拟数据
        service = new XtCheckIndexService(getActivity(), null);
        // 查询最新终端临时表数据
        term = service.findTermTempById(termId);


        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 // 铺货状态,道具生动化,产品生动化,冰冻化
        calculateLst = service.queryCalculateIndex(visitId, termId, term.getMinorchannel(), seeFlag);
        // 获取分项采集部分的产品指标对应的采集项目数据 // 因为在ShopVisitActivity生成了供货关系,此时就能关联出各个产品的采集项,现有量变化量为0
        proItemLst = service.queryCalculateItem(visitId, channelId);

        // 初始化指标、指标值树级关系 对象
        service.initCheckTypeStatus();
        indexValuelst = GlobalValues.indexLst;

        XtCaculateAdapter xtCaculateAdapter = new XtCaculateAdapter(getActivity(), calculateLst, indexValuelst, proItemLst);
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


}
