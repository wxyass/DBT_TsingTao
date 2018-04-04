package et.tsingtaopad.dd.ddxt.invoicing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.checking.num.XtCaculateFragment;
import et.tsingtaopad.dd.ddxt.invoicing.addinvoicing.XtAddInvoicingFragment;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.domain.InvoicingStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtInvoicingFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private final String TAG = "XtInvoicingFragment";

    private Button addRelationBt;// 新增供货关系
    private Button nextBt;// 下一页
    private ListView askGoodsLv;
    private ListView checkGoodsLv;

    List<XtInvoicingStc> dataLst;
    XtInvoicingService invoicingService;
    MyHandler handler;

    public static final int ADD_SUC = 2;

    XtInvoicingAskGoodsAdapter askAdapter;
    XtInvoicingCheckGoodsAdapter checkGoodsAdapter;


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

        //Toast.makeText(getActivity(), "进销存" + "/" + termId + "/" + termName, Toast.LENGTH_SHORT).show();

        handler = new MyHandler(this);
        invoicingService = new XtInvoicingService(getActivity(), null);

        // 初始化产品数据
        initProData();

    }

    // 初始化产品数据
    private void initProData() {
        //删除重复拜访产品
        invoicingService.delRepeatVistProduct(visitId);
        //获取某次拜访的我品的进销存数据情况
        dataLst = invoicingService.queryMineProFromTemp(visitId, termId);

        //问货源Adapter
        askAdapter = new XtInvoicingAskGoodsAdapter(getActivity(), "", dataLst, "", "", null, askGoodsLv, checkGoodsLv);//问货源
        askGoodsLv.setAdapter(askAdapter);
        ViewUtil.setListViewHeight(askGoodsLv);

        // 订单推荐
        checkGoodsAdapter = new XtInvoicingCheckGoodsAdapter(getActivity(), dataLst);
        checkGoodsLv.setAdapter(checkGoodsAdapter);
        ViewUtil.setListViewHeight(checkGoodsLv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xtbf_invoicing_bt_addrelation:
                Bundle bundle = new Bundle();
                bundle.putSerializable("termId", termStc.getTerminalkey());
                bundle.putSerializable("termname", termStc.getTerminalname());
                bundle.putSerializable("channelId", termStc.getMinorchannel());// 次渠道
                bundle.putSerializable("termStc", termStc);
                bundle.putSerializable("visitKey", visitId);//visitId
                bundle.putSerializable("seeFlag", seeFlag);// 默认0   0:拜访 1:查看

                XtAddInvoicingFragment xtaddinvoicingfragment = new XtAddInvoicingFragment(handler);
                xtaddinvoicingfragment.setArguments(bundle);

                XtVisitShopActivity xtVisitShopActivity = (XtVisitShopActivity) getActivity();
                xtVisitShopActivity.changeXtvisitFragment(xtaddinvoicingfragment, "xtaddchatviefragment");
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
        SoftReference<XtInvoicingFragment> fragmentRef;

        public MyHandler(XtInvoicingFragment fragment) {
            fragmentRef = new SoftReference<XtInvoicingFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            XtInvoicingFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            ArrayList<KvStc> products = (ArrayList<KvStc>) msg.obj;
            Bundle bundle = msg.getData();
            KvStc agency = (KvStc) bundle.getSerializable("agency");

            // 处理UI 变化
            switch (msg.what) {
                case ADD_SUC:
                    fragment.showAddProSuc(products, agency);
                    break;
            }
        }
    }

    /**
     * 添加产品成功 UI
     */
    public void showAddProSuc(ArrayList<KvStc> products, KvStc agency) {
        for (KvStc product : products) {
            //DbtLog.logUtils(TAG,"经销商key:"+agency.getKey()+"、经销商名称:"+agency.getValue()+"-->产品key："+product.getKey()+"、产品名称："+product.getValue());
            List<String> proIdLst = FunUtil.getPropertyByName(dataLst, "proId", String.class);
            if (proIdLst.contains(product.getKey())) {
                DbtLog.logUtils(TAG, "产品重复提示");
                Toast.makeText(getActivity(), getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();
            } else {
                XtInvoicingStc supplyStc = new XtInvoicingStc();
                supplyStc.setProId(product.getKey());
                supplyStc.setProName(product.getValue());
                supplyStc.setAgencyId(agency.getKey());
                supplyStc.setAgencyName(agency.getValue());
                supplyStc.setPrevStore("0");
                dataLst.add(supplyStc);
                //新增供货关系指标记录表进行更新
                invoicingService.updateMstcheckexerecordInfoTempDeleteflag(visitId,product.getKey());
                askAdapter.setDelPosition(-1);
                askAdapter.notifyDataSetChanged();
                ViewUtil.setListViewHeight(askGoodsLv);

                checkGoodsAdapter.setDelPosition(-1);
                checkGoodsAdapter.notifyDataSetChanged();
                ViewUtil.setListViewHeight(checkGoodsLv);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag)) return;

        View view;
        EditText itemEt;
        Button itemBtn;
        XtInvoicingStc item;
        for (int i = 0; i < dataLst.size(); i++) {
            item = dataLst.get(i);
            view = askGoodsLv.getChildAt(i);
            if (view != null) {
                itemEt = (EditText)view.findViewById(R.id.item_askgoods_et_qudao);//渠道价
                String content = itemEt.getText().toString();
                item.setChannelPrice(FunUtil.getDecimalsData(content));

                itemEt = (EditText)view.findViewById(R.id.item_askgoods_et_lingshou);// 零售价
                content = itemEt.getText().toString();
                item.setSellPrice(FunUtil.getDecimalsData(content));
            }
            view = checkGoodsLv.getChildAt(i);
            if (view != null) {
                itemEt = (EditText)view.findViewById(R.id.item_checkgoods_et_prevnum);// 订单量
                item.setPrevNum(itemEt.getText().toString());
                itemEt = (EditText)view.findViewById(R.id.item_checkgoods_et_daysellnum);// 日销量
                item.setDaySellNum(itemEt.getText().toString());
                itemEt = (EditText)view.findViewById(R.id.item_askgoods_et_addcard);// 累计卡
                item.setAddcard(itemEt.getText().toString());

            }
        }
        invoicingService.saveXtInvoicing(dataLst, visitId, termId);
    }

}
