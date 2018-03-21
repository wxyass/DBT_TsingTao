package et.tsingtaopad.dd.ddxt.checking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.checking.num.XtCaculateFragment;
import et.tsingtaopad.dd.ddxt.checking.num.XtQuickCollectFragment;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtCheckIndexFragment extends BaseFragmentSupport implements View.OnClickListener{

    private AppCompatButton mXtvisit_btn_pro;

    private ListView calculateLv;
    private ListView promotionLv;
    private Button quickCollectBt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_checkindex, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view){

        //采项分集listView
        calculateLv = (ListView)view.findViewById(R.id.xtbf_checkindex_lv_calculate);
        quickCollectBt = (Button)view.findViewById(R.id.xtbf_checkindex_bt_quickcollect);
        promotionLv = (ListView)view.findViewById(R.id.xtbf_checkindex_lv_promotion);

        quickCollectBt.setOnClickListener(this);

    }

    List<XtProIndex> calculateLst = new ArrayList<XtProIndex>();
    List<KvStc> indexValuelst= new ArrayList<KvStc>();
    List<XtProItem> proItemLst= new ArrayList<XtProItem>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 指标模拟数据

        XtCheckIndexService service = new XtCheckIndexService(getActivity(),null);
        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 // 铺货状态,道具生动化,产品生动化,冰冻化
        calculateLst = service.queryCalculateIndex("da85a369f6344b46a8f4579aba5e095a", "1-AW46W7","39DD41A3991E8C68E05010ACE0016FCD", "");
        // 获取分项采集部分的产品指标对应的采集项目数据 // 因为在ShopVisitActivity生成了供货关系,此时就能关联出各个产品的采集项,现有量变化量为0
        proItemLst = service.queryCalculateItem("da85a369f6344b46a8f4579aba5e095a", "39DD41A3991E8C68E05010ACE0016FCD");
        service.initCheckTypeStatus();
        indexValuelst = GlobalValues.indexLst;
        XtCaculateAdapter xtCaculateAdapter = new XtCaculateAdapter(getActivity(),calculateLst,indexValuelst,proItemLst);
        calculateLv.setAdapter(xtCaculateAdapter);
        ViewUtil.setListViewHeight(calculateLv);


        // 促销活动 模拟数据


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xtbf_checkindex_bt_quickcollect:
                XtVisitShopActivity xtVisitShopActivity = (XtVisitShopActivity)getActivity();
                xtVisitShopActivity.changeXtvisitFragment(new XtQuickCollectFragment(),"xtnuminputfragment");
                break;

            default:
                break;
        }
    }
}
