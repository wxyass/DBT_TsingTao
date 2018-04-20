package et.tsingtaopad.dd.ddzs.zsinvoicing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
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
import et.tsingtaopad.db.table.MitValsupplyMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.invoicing.XtInvoicingService;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddzs.zsinvoicing.zsaddinvoicing.ZsAddInvoicingFragment;
import et.tsingtaopad.dd.ddzs.zssayhi.ZsSayhiAmendFragment;
import et.tsingtaopad.dd.ddzs.zsshopvisit.ZsVisitShopActivity;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.listviewintf.IClick;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class ZsInvoicingFragment extends XtBaseVisitFragment implements View.OnClickListener{

    private final String TAG = "ZsInvoicingFragment";

    //List<XtInvoicingStc> dataLst;
    List<MitValsupplyMTemp> dataLst;
    ZsInvoicingService invoicingService;
    MyHandler handler;

    public static final int ZS_ADD_SUC = 2;// 追溯-新增我品供货关系成功
    public static final int INIT_AMEND = 22;// 督导输入数据成功

    ZsInvoicingAdapter zsInvoicingAdapter;



    private ScrollView zdzs_invoicing_scrollView;
    private ImageView zdzs_invoicing_point1;
    private TextView zdzs_invocing_textview01;
    private RelativeLayout zdzs_invoicing_rl_addrelation;
    private Button zdzs_invoicing_bt_addrelation;
    private ListView zsInvoicingLv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_invoicing, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

        zdzs_invoicing_scrollView = (ScrollView) view.findViewById(R.id.zdzs_invoicing_scrollView);
        zdzs_invoicing_point1 = (ImageView) view.findViewById(R.id.zdzs_invoicing_point1);
        zdzs_invocing_textview01 = (TextView) view.findViewById(R.id.zdzs_invocing_textview01);
        zdzs_invoicing_rl_addrelation = (RelativeLayout) view.findViewById(R.id.zdzs_invoicing_rl_addrelation);
        zdzs_invoicing_bt_addrelation = (Button) view.findViewById(R.id.zdzs_invoicing_bt_addrelation);
        zsInvoicingLv = (ListView) view.findViewById(R.id.zdzs_invoicing_lv_invoicing);


        zdzs_invoicing_bt_addrelation.setOnClickListener(this);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);
        invoicingService = new ZsInvoicingService(getActivity(), null);

        // 初始化产品数据
        initProData();

    }

    // 初始化产品数据
    private void initProData() {
        //删除重复拜访产品
        invoicingService.delRepeatVistProduct(visitId);
        //获取某次拜访的我品的进销存数据情况
        //dataLst = invoicingService.queryMineProToValSupplyTemp(visitId, termId,mitValterMTempKey);
        //mitValsupplyMTemps = invoicingService.queryMineProToValSupplyTemp(visitId, termId,mitValterMTempKey);
        dataLst = invoicingService.queryValSupplyTemp(mitValterMTempKey);

        // 订单推荐
        zsInvoicingAdapter = new ZsInvoicingAdapter(getActivity(), dataLst,new IClick(){
            @Override
            public void listViewItemClick(int position, View v) {
                alertShow3(position);
            }
        });
        zsInvoicingLv.setAdapter(zsInvoicingAdapter);
        ViewUtil.setListViewHeight(zsInvoicingLv);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zdzs_invoicing_bt_addrelation:
                Bundle bundle = new Bundle();
                bundle.putSerializable("termId", termStc.getTerminalkey());
                bundle.putSerializable("termname", termStc.getTerminalname());
                bundle.putSerializable("channelId", termStc.getMinorchannel());// 次渠道
                bundle.putSerializable("termStc", termStc);
                bundle.putSerializable("visitKey", visitId);//visitId
                bundle.putSerializable("seeFlag", seeFlag);// 默认0   0:拜访 1:查看

                ZsAddInvoicingFragment xtaddinvoicingfragment = new ZsAddInvoicingFragment(handler);
                xtaddinvoicingfragment.setArguments(bundle);

                ZsVisitShopActivity xtVisitShopActivity = (ZsVisitShopActivity) getActivity();
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
        SoftReference<ZsInvoicingFragment> fragmentRef;

        public MyHandler(ZsInvoicingFragment fragment) {
            fragmentRef = new SoftReference<ZsInvoicingFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ZsInvoicingFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            ArrayList<KvStc> products = (ArrayList<KvStc>) msg.obj;
            Bundle bundle = msg.getData();
            KvStc agency = (KvStc) bundle.getSerializable("agency");

            // 处理UI 变化
            switch (msg.what) {
                case ZS_ADD_SUC:
                    fragment.showAddProSuc(products, agency);
                    break;
                case INIT_AMEND: // 督导输入数据后
                    fragment.showAdapter();
                    break;
            }
        }
    }

    private void showAdapter() {
        zsInvoicingAdapter.notifyDataSetChanged();
    }

    /**
     * 新增供货关系 添加产品成功 UI
     */
    public void showAddProSuc(ArrayList<KvStc> products, KvStc agency) {
        for (KvStc product : products) {
            //DbtLog.logUtils(TAG,"经销商key:"+agency.getKey()+"、经销商名称:"+agency.getValue()+"-->产品key："+product.getKey()+"、产品名称："+product.getValue());
            List<String> proIdLst = FunUtil.getPropertyByName(dataLst, "proId", String.class);
            if (proIdLst.contains(product.getKey())) {
                DbtLog.logUtils(TAG, "产品重复提示");
                Toast.makeText(getActivity(), getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();
            } else {
                /*XtInvoicingStc supplyStc = new XtInvoicingStc();
                supplyStc.setProId(product.getKey());
                supplyStc.setProName(product.getValue());
                supplyStc.setAgencyId(agency.getKey());
                supplyStc.setAgencyName(agency.getValue());
                supplyStc.setPrevStore("0");
                dataLst.add(supplyStc);
                //新增供货关系指标记录表进行更新
                invoicingService.updateMstcheckexerecordInfoTempDeleteflag(visitId,product.getKey());*/





                MitValsupplyMTemp valsupplyMTemp = new MitValsupplyMTemp();
                valsupplyMTemp.setId(FunUtil.getUUID());
                valsupplyMTemp.setValterid(mitValterMTempKey);// 终端追溯主表ID
                valsupplyMTemp.setValaddagencysupply("Y");// 是否新增供货关系
                valsupplyMTemp.setValagency(agency.getKey());// 经销商;
                valsupplyMTemp.setValpro(product.getKey());// 产品
                dataLst.add(valsupplyMTemp);

                zsInvoicingAdapter.setDelPosition(-1);
                zsInvoicingAdapter.notifyDataSetChanged();
                ViewUtil.setListViewHeight(zsInvoicingLv);

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        /*// 如果是查看操作，则不做数据校验及数据处理
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
        invoicingService.saveXtInvoicing(dataLst, visitId, termId);*/
    }

    private AlertView mAlertViewExt;//窗口拓展例子

    // 长按删除我品供货关系弹窗
    private void deletesupply(int position,final XtInvoicingStc xtInvoicingStc) {
        String proName = xtInvoicingStc.getProName();
        // 普通窗口
        mAlertViewExt = new AlertView("解除我品: "+proName, null, "取消", new String[]{"确定"}, null, getActivity(), AlertView.Style.Alert,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        //Toast.makeText(getApplicationContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                        if (0 == position) {// 确定按钮:0   取消按钮:-1
                            //if (ViewUtil.isDoubleClick(v.getId(), 2500)) return;
                            DbtLog.logUtils(TAG, "删除供货关系：是");
                            // 删除对应的数据
                            if (!CheckUtil.isBlankOrNull(xtInvoicingStc.getRecordId())) {
                                DbtLog.logUtils(TAG,"解除供货关系");
                                XtInvoicingService service = new XtInvoicingService(getActivity(), null);
                                boolean isFlag = service.deleteSupply(xtInvoicingStc.getRecordId(), termId, visitId, xtInvoicingStc.getProId());
                                if(isFlag){
                                    // 删除界面listView相应行
                                    dataLst.remove(position);
                                    zsInvoicingAdapter.notifyDataSetChanged();

                                    ViewUtil.setListViewHeight(zsInvoicingLv);
                                }else{
                                    Toast.makeText(getActivity(), "删除产品失败!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                // 删除界面listView相应行
                                dataLst.remove(position);
                                zsInvoicingAdapter.notifyDataSetChanged();

                                ViewUtil.setListViewHeight(zsInvoicingLv);
                            }
                        }

                    }
                })
                .setCancelable(true)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(Object o) {
                        DbtLog.logUtils(TAG, "删除供货关系：否");
                    }
                });
        mAlertViewExt.show();
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
    MitValsupplyMTemp valsupplyMTemp;
    public void alertShow3(final int position) {

        mAlertViewExt = new AlertView(null, null, null, null,
                null, getActivity(), AlertView.Style.ActionSheet, null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_result_form, null);

        RelativeLayout rl_back1 = (RelativeLayout) extView.findViewById(R.id.top_navigation_rl_back1);
        android.support.v7.widget.AppCompatTextView bt_back1 = (android.support.v7.widget.AppCompatTextView) extView.findViewById(R.id.top_navigation_bt_back1);
        RelativeLayout rl_confirm1 = (RelativeLayout) extView.findViewById(R.id.top_navigation_rl_confirm1);
        android.support.v7.widget.AppCompatTextView bt_confirm1 = (android.support.v7.widget.AppCompatTextView) extView.findViewById(R.id.top_navigation_bt_confirm1);


        final RadioButton right_rb = (RadioButton) extView.findViewById(R.id.right_rb);
        final RadioGroup right_rg = (RadioGroup) extView.findViewById(R.id.right_rg);
        final CheckBox cb1 = (CheckBox) extView.findViewById(R.id.cb1);
        final CheckBox cb2 = (CheckBox) extView.findViewById(R.id.cb2);
        final CheckBox cb3 = (CheckBox) extView.findViewById(R.id.cb3);
        final CheckBox cb4 = (CheckBox) extView.findViewById(R.id.cb4);

        valsupplyMTemp = dataLst.get(position);
        String flag = valsupplyMTemp.getValagencysupplyflag();
        if("Y".equals(flag)){
            right_rb.setChecked(true);
        }
        String flag1 = valsupplyMTemp.getValproerror();
        String flag2 = valsupplyMTemp.getValagencyerror();
        String flag3 = valsupplyMTemp.getValdataerror();
        String flag4 = valsupplyMTemp.getValiffleeing();
        if("Y".equals(flag1)){
            cb1.setChecked(true);
            right_rg.clearCheck();
        }
        if("Y".equals(flag2)){
            cb2.setChecked(true);
            right_rg.clearCheck();
        }
        if("Y".equals(flag3)){
            cb3.setChecked(true);
            right_rg.clearCheck();
        }
        if("Y".equals(flag4)){
            cb4.setChecked(true);
            right_rg.clearCheck();
        }

        // 取消按钮
        rl_back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertViewExt.dismiss();
            }
        });

        // 确定按钮
        rl_confirm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 请至少勾选一项
                if((!right_rb.isChecked())&&(!cb1.isChecked())&&(!cb2.isChecked())&&(!cb3.isChecked())&&(!cb4.isChecked())){
                    Toast.makeText(getActivity(),"请至少勾选一项",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(right_rb.isChecked()){
                    valsupplyMTemp.setValagencysupplyflag("Y");// 供货关系正确与否
                    valsupplyMTemp.setValproerror("N");// 品项有误
                    valsupplyMTemp.setValagencyerror("N");// 经销商有误
                    valsupplyMTemp.setValdataerror("N");// 数据有误
                    valsupplyMTemp.setValiffleeing("N");// 窜货
                    mAlertViewExt.dismiss();
                    handler.sendEmptyMessage(ZsInvoicingFragment.INIT_AMEND);
                }else{
                    if(cb1.isChecked()){// 品项有误
                        valsupplyMTemp.setValagencysupplyflag("N");// 供货关系正确与否
                        valsupplyMTemp.setValproerror("Y");// 品项有误
                        valsupplyMTemp.setValagencyerror("N");// 经销商有误
                        valsupplyMTemp.setValdataerror("N");// 数据有误
                        valsupplyMTemp.setValiffleeing("N");// 窜货
                        mAlertViewExt.dismiss();
                        handler.sendEmptyMessage(ZsInvoicingFragment.INIT_AMEND);
                    }else{
                        valsupplyMTemp.setValagencysupplyflag("N");// 供货关系正确与否
                        valsupplyMTemp.setValproerror("N");// 品项有误

                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);// 页面类型,比如: "1"
                        bundle.putString("termId", termId);// 页面类型,比如: "1"
                        bundle.putString("mitValterMTempKey", mitValterMTempKey);
                        bundle.putBoolean("valagencyerror",cb2.isChecked());
                        bundle.putBoolean("valdataerror",cb3.isChecked());
                        bundle.putBoolean("valiffleeing",cb4.isChecked());
                        bundle.putSerializable("dataLst", (Serializable) dataLst);
                        ZsInvocingAmendFragment zsInvocingAmendFragment = new ZsInvocingAmendFragment(handler);
                        zsInvocingAmendFragment.setArguments(bundle);
                        ZsVisitShopActivity zsVisitShopActivity = (ZsVisitShopActivity)getActivity();
                        zsVisitShopActivity.changeXtvisitFragment(zsInvocingAmendFragment,"zsamendfragment");
                        mAlertViewExt.dismiss();
                    }
                }
            }
        });

        // 正确
        right_rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb1.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                }
            }
        });

        // 品项有误
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    right_rg.clearCheck();
                }
            }
        });

        // 经销商有误
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    right_rg.clearCheck();
                }
            }
        });

        // 数据有误
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    right_rg.clearCheck();
                }
            }
        });

        // 窜货
        cb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    right_rg.clearCheck();
                }
            }
        });

        mAlertViewExt.addExtView(extView);
        mAlertViewExt.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                DbtLog.logUtils(TAG, "取消选择结果");
            }
        });
        mAlertViewExt.show();
    }

}
