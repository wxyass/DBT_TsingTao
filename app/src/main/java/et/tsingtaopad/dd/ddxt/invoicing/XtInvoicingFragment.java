package et.tsingtaopad.dd.ddxt.invoicing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.adapter.InvoicingAskGoodsAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.domain.InvoicingStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtInvoicingFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout addRelationBt;
    private Button nextBt;
    private ListView askGoodsLv;
    private ListView checkGoodsLv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_invoicing, container, false);
        initView(view);
        return view;
    }



    // 初始化控件
    private void initView(View view) {

        //新增供货关系按钮
        addRelationBt = (RelativeLayout) view.findViewById(R.id.xtbf_invoicing_rl_addrelation);
        //问货源
        askGoodsLv = (ListView) view.findViewById(R.id.xtbf_invoicing_lv_askgoods);
        //订单推荐(原是核查进销存)
        checkGoodsLv = (ListView) view.findViewById(R.id.xtbf_invoicing_lv_checkgoods);
        //下一页
        nextBt = (Button) view.findViewById(R.id.xtbf_invocing_bt_next);

        addRelationBt.setOnClickListener(this);
        nextBt.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initProData();

        //问货源Adapter
        XtInvoicingAskGoodsAdapter askAdapter = new XtInvoicingAskGoodsAdapter(getActivity(), "" ,lst, "", "", null,askGoodsLv,checkGoodsLv);//问货源
        askGoodsLv.setAdapter(askAdapter);
        ViewUtil.setListViewHeight(askGoodsLv);

        // 订单推荐
        XtInvoicingCheckGoodsAdapter checkGoodsAdapter = new XtInvoicingCheckGoodsAdapter(getActivity(),lst);
        checkGoodsLv.setAdapter(checkGoodsAdapter);
        ViewUtil.setListViewHeight(checkGoodsLv);

    }

    List<XtInvoicingStc> lst;
    private void initProData() {
        lst = new ArrayList<XtInvoicingStc>();
        XtInvoicingStc xtInvoicingStc = new XtInvoicingStc();
        xtInvoicingStc.setProName("青岛啤酒8度12*500箱啤");// 产品名称
        xtInvoicingStc.setAgencyName("北方销售");// 经销商名称
        xtInvoicingStc.setSellPrice("54.00");// 零售价
        xtInvoicingStc.setChannelPrice("48.00");// 渠道价
        xtInvoicingStc.setCurrStore("3");// 当前库存
        xtInvoicingStc.setAddcard("12");// 累计卡
        xtInvoicingStc.setDaySellNum("4");// 日销量
        xtInvoicingStc.setPrevStore("8");// 上次库存
        xtInvoicingStc.setPrevNum("25");// 订单量(原名称是上周期进货总量)
        XtInvoicingStc xtInvoicingStc2 = new XtInvoicingStc();
        xtInvoicingStc2.setProName("青岛啤酒8度12*500箱啤2");// 产品名称
        xtInvoicingStc2.setAgencyName("北方销售2");// 经销商名称
        xtInvoicingStc2.setSellPrice("542.00");// 零售价
        xtInvoicingStc2.setChannelPrice("482.00");// 渠道价
        xtInvoicingStc2.setCurrStore("32");// 当前库存
        xtInvoicingStc2.setAddcard("122");// 累计卡
        xtInvoicingStc2.setDaySellNum("42");// 日销量
        xtInvoicingStc2.setPrevStore("82");// 上次库存
        xtInvoicingStc2.setPrevNum("252");// 订单量(原名称是上周期进货总量)
        lst.add(xtInvoicingStc);
        lst.add(xtInvoicingStc2);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;

            default:
                break;
        }
    }
}
