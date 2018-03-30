package et.tsingtaopad.dd.ddxt.invoicing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.invoicing.addinvoicing.XtAddInvoicingFragment;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtInvoicingFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private Button addRelationBt;// 新增供货关系
    private Button nextBt;// 下一页
    private ListView askGoodsLv;
    private ListView checkGoodsLv;

    List<XtInvoicingStc> dataLst;
    XtInvoicingService invoicingService;

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
        addRelationBt = (Button) view.findViewById(R.id.xtbf_invoicing_bt_addrelation);
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

        Toast.makeText(getActivity(), "进销存" + "/" + termId + "/" + termName, Toast.LENGTH_SHORT).show();

        // 初始化产品数据
        initProData();

        //问货源Adapter
        XtInvoicingAskGoodsAdapter askAdapter = new XtInvoicingAskGoodsAdapter(getActivity(), "", dataLst, "", "", null, askGoodsLv, checkGoodsLv);//问货源
        askGoodsLv.setAdapter(askAdapter);
        ViewUtil.setListViewHeight(askGoodsLv);

        // 订单推荐
        XtInvoicingCheckGoodsAdapter checkGoodsAdapter = new XtInvoicingCheckGoodsAdapter(getActivity(), dataLst);
        checkGoodsLv.setAdapter(checkGoodsAdapter);
        ViewUtil.setListViewHeight(checkGoodsLv);

    }

    // 初始化产品数据
    private void initProData() {

        invoicingService = new XtInvoicingService(getActivity(), null);
        //删除重复拜访产品
        invoicingService.delRepeatVistProduct(visitId);
        //获取某次拜访的我品的进销存数据情况
        dataLst = invoicingService.queryMineProFromTemp(visitId,termId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xtbf_invoicing_bt_addrelation:
                XtVisitShopActivity xtVisitShopActivity = (XtVisitShopActivity) getActivity();
                xtVisitShopActivity.changeXtvisitFragment(new XtAddInvoicingFragment(), "xtaddchatviefragment");
                break;

            default:
                break;
        }
    }
}
