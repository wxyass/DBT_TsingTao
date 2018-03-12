package et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.ListViewKeyValueAdapter;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.adapter.InvoicingAskGoodsAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.adapter.InvoicingCheckGoodsAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.adapter.InvoicingProductAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.domain.InvoicingStc;

/**
 * Created by yangwenmin on 2017/12/25.
 * 进销存
 */

public class InvoicingCoreFragment extends BaseCoreFragment implements View.OnClickListener{

    public static final String TAG = "InvoicingCoreFragment";

    private String seeFlag;
    private String termId;
    private String visitKey;
    private String isFirstVisit;
    private String termname;
    private String visitDate;
    private String lastTime;
    private boolean isShow = false;// false:默认   true:加载完数据设为true

    private InvoicingService service;
    private ScrollView invoicingSv;
    private Button addRelationBt;
    private ListView askGoodsLv;
    private ListView checkGoodsLv;
    private InvoicingAskGoodsAdapter askAdapter;
    private InvoicingCheckGoodsAdapter checkAdapter;

    // 进销存数据源
    //InvoicingStc--进销存数据显示的数据结构
    private List<InvoicingStc> dataLst = new ArrayList<InvoicingStc>();
    private String visitId;

    // 供货关系弹出框相关实例
    private AlertDialog productDialgo;
    private View itemForm;
    private KvStc agency;
    private KvStc product;
    private ListView agencyLv;
    private ListView productLv;
    private ListViewKeyValueAdapter agencyAdapter;
    private InvoicingProductAdapter productAdapter;
    private EditText channelPriceEt;
    private EditText sellPriceEt;
    //private List<MonthSum> getmonthSumList;
    // 获取用户选择的产品(多选)
    private ArrayList<KvStc> products;

    public InvoicingCoreFragment() {
    }

    public static InvoicingCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        InvoicingCoreFragment fragment = new InvoicingCoreFragment();
        return fragment;
    }

    public static InvoicingCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, "newInstance(Bundle args)");
        //Bundle args = new Bundle();

        InvoicingCoreFragment fragment = new InvoicingCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // 在这个方法中 只处理从上个Fragment传递过来的数据,若没数据传递过来,可删除该方法
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            termId = bundle.getString("termId");
            visitId = visitKey = bundle.getString("visitKey");
            seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");// 0:拜访   1:查看
            isFirstVisit = bundle.getString("isFirstVisit");
            termname = bundle.getString("termname");
            visitDate = bundle.getString("visitDate");// 上一次的拜访时间(用于促销活动 状态隔天关闭)
            lastTime = bundle.getString("lastTime");// 上一次的拜访时间(用于促销活动 状态隔天关闭)
        }
    }

    // 只用来初始化控件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_invoicing, null);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");

        //新增供货关系按钮
        addRelationBt = (Button)view.findViewById(R.id.invoicing_bt_addrelation);
        //问货源
        askGoodsLv = (ListView)view.findViewById(R.id.invoicing_lv_askgoods);
        //订单推荐(原是核查进销存)
        checkGoodsLv = (ListView) view.findViewById(R.id.invoicing_lv_checkgoods);
        invoicingSv = (ScrollView) view.findViewById(R.id.invoicing_scrollView);

        addRelationBt.setOnClickListener(this);
        //invoicingSv.setOnTouchListener(this);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        /*titleTv.setText("路线选择");
        confirmRl.setVisibility(View.VISIBLE);*/
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

        //InvoicingService--进销存业务逻辑
        service = new InvoicingService(getActivity(), null);

        // 获取参数
        //删除重复拜访产品
        service.delRepeatVistProduct(visitId);
        //获取某次拜访的我品的进销存数据情况
        dataLst = service.queryMineProFromTemp(visitId,termId);
        //请求网络获取本月合计数值
        //getmonthSum();

        //问货源Adapter
        askAdapter = new InvoicingAskGoodsAdapter(getActivity(), seeFlag ,dataLst, termId, visitId, checkAdapter,askGoodsLv,checkGoodsLv);//问货源
        askGoodsLv.setAdapter(askAdapter);
        ViewUtil.setListViewHeight(askGoodsLv);

        //订单推荐Adapter(原先名字是核查进销存Adapter //核查进销存)
        checkAdapter = new InvoicingCheckGoodsAdapter(getActivity(), dataLst);
        //给订单推荐(原先是核查进销存)设置数据适配器
        checkGoodsLv.setAdapter(checkAdapter);
        //设置ListView的高度
        ViewUtil.setListViewHeight(checkGoodsLv);

        isShow = true;
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.invoicing_bt_addrelation) {
            DbtLog.logUtils(TAG, "添加产品");
            this.dialogProduct();

        } else {
        }
    }

    /**
     * 产品列表弹出窗口
     */
    public void dialogProduct() {
        DbtLog.logUtils(TAG, "dialogProduct()");
        // 如果已打开，直接返回
        if (productDialgo != null && productDialgo.isShowing()) return;

        // 加载弹出窗口layout
        itemForm = getActivity().getLayoutInflater().inflate(R.layout.shopvisit_addrelation,null);
        //设置对话框不能被取消
        productDialgo = new AlertDialog.Builder(getActivity()).setCancelable(false).create();


        //设置视图显示在对话框中
        productDialgo.setView(itemForm, 0, 0, 0, 0);
        productDialgo.show();

        agencyLv = (ListView)itemForm.findViewById(R.id.addrelation_lv_agency);
        productLv = (ListView)itemForm.findViewById(R.id.addrelation_lv_product);

        channelPriceEt = (EditText)itemForm.findViewById(R.id.addrelation_et_channelprice);
        sellPriceEt = (EditText)itemForm.findViewById(R.id.addrelation_et_sellprice);

        // 经销商集合数据

        // 产品集合数据


        // 代理商
        agencyLv.setDividerHeight(0);
        if (!CheckUtil.IsEmpty(GlobalValues.agencyMineLst)) {
            agency = GlobalValues.agencyMineLst.get(0);
        }
        agencyAdapter = new ListViewKeyValueAdapter(getActivity(),
                GlobalValues.agencyMineLst, new String[]{"key","value"},
                new int[]{R.drawable.bg_agency_up, R.drawable.bg_agency_down});
        agencyLv.setAdapter(agencyAdapter);

        products = new ArrayList<KvStc>();
        // 选择不同的经销商,列出不同的产品表
        agencyLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
                products = new ArrayList<KvStc>();
                agency = (KvStc)agencyLv.getItemAtPosition(position);
                if (!CheckUtil.IsEmpty(agency.getChildLst())) {
                    product = agency.getChildLst().get(0);
                }
                productAdapter = new InvoicingProductAdapter(
                        getActivity(),agency.getChildLst(), new String[]{"key","value"},new int[]{R.drawable.bg_product_up, R.drawable.bg_product_down});
                productLv.setAdapter(productAdapter);
                productLv.refreshDrawableState();

                agencyAdapter.setSelectItemId(position);
                agencyAdapter.notifyDataSetChanged();
            }
        });

        // 产品
        if (!(agency == null || CheckUtil.IsEmpty(agency.getChildLst()))) {
            product = agency.getChildLst().get(0);
        }
        productLv.setDividerHeight(0);
        if (!CheckUtil.IsEmpty(GlobalValues.agencyMineLst)) {
            productAdapter = new InvoicingProductAdapter(
                    getActivity(),GlobalValues.agencyMineLst.get(0).getChildLst(), new String[]{"key","value"},new int[]{R.drawable.bg_product_up, R.drawable.bg_product_down});
            productLv.setAdapter(productAdapter);
        }


        productLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                product = (KvStc)productLv.getItemAtPosition(position);


                // 遍历用户选择的产品集合
                int count = 0;
                for (int i = 0; i < products.size(); i++) {
                    // 该产品用户选择过,数量加1
                    if(product.equals(products.get(i))){
                        count++;
                    }
                }
                // 用户选择过就在集合中移除,没选择过就添加
                if(count==1){
                    products.remove(product);
                }else{
                    products.add(product);
                }

                // 在确定按钮出会对 选择的产品做处理 若是产品已添加过 则不会再添加

                //productAdapter.setSelectItemId(position);
                productAdapter.addSelected(position);
                productAdapter.notifyDataSetChanged();

            }
        });

        // 确定
        Button sureBt = (Button)itemForm.findViewById(R.id.addrelation_bt_confirm);
        sureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (ViewUtil.isDoubleClick(arg0.getId(), 2500)) return;
                DbtLog.logUtils(TAG,"确认添加");
                if (products.size() == 0) {
                    productDialgo.cancel();

                } else {
                    for (KvStc product : products) {

                        DbtLog.logUtils(TAG,"经销商key:"+agency.getKey()+"、经销商名称:"+agency.getValue()+"-->产品key："+product.getKey()+"、产品名称："+product.getValue());
                        List<String> proIdLst = FunUtil.getPropertyByName(dataLst, "proId", String.class);
                        if (proIdLst.contains(product.getKey())) {
                            DbtLog.logUtils(TAG,"产品重复提示");
                            Toast.makeText(getActivity(), getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();
                        } else {
                            InvoicingStc supplyStc = new InvoicingStc();
                            supplyStc.setProId(product.getKey());
                            supplyStc.setProName(product.getValue());
                            supplyStc.setAgencyId(agency.getKey());
                            supplyStc.setAgencyName(agency.getValue());
                            supplyStc.setChannelPrice(channelPriceEt.getText().toString());
                            supplyStc.setSellPrice(sellPriceEt.getText().toString());
                            supplyStc.setPrevStore("0");
                            dataLst.add(supplyStc);
                            //新增供货关系指标记录表进行更新
                            service.updateMstcheckexerecordInfoTemp(visitId,product.getKey());
                            askAdapter.setDelPosition(-1);
                            askAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(askGoodsLv);

                            checkAdapter.setDelPosition(-1);
                            checkAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(checkGoodsLv);
                            productDialgo.cancel();
                        }

                    }
                }
            }
        });

        // 取消
        Button cancelBt = (Button)itemForm.findViewById(R.id.addrelation_bt_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbtLog.logUtils(TAG,"取消");
                productDialgo.cancel();
            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);// hidden true:该界面已隐藏   false: 正在展示

        if(hidden && isShow){
            isShow = false;
            Toast.makeText(getContext(),"正在保存进销存数据",Toast.LENGTH_SHORT).show();

            saveInvoicingData();
        }
    }

    private void saveInvoicingData(){

        DbtLog.logUtils(TAG, "saveInvoicingData()");
        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag)) return;

        View view;
        EditText itemEt;
        Button itemBtn;
        InvoicingStc item;
        for (int i = 0; i < dataLst.size(); i++) {
            item = dataLst.get(i);
            view = askGoodsLv.getChildAt(i);
            if (view != null) {
                itemEt = (EditText)view.findViewById(R.id.askgoods_et_channelprice);//渠道价

                String content = itemEt.getText().toString();
                if(".".equals(content)){//
                    item.setChannelPrice("0.0");
                }
                else if(content.length()>1&&content.endsWith(".")){
                    item.setChannelPrice(content+"0");
                }
                else if(content.length()>1&&content.startsWith(".")){
                    //item.setChannelPrice("0"+content);
                    // 以小数点开头,截取小数点后两位
                    Double d = Double.parseDouble("0"+content);
                    //String num = new DecimalFormat("#0.00").format(d);
                    String[] split = String.valueOf(d+0.000001).split("\\.");
                    String num = split[0]+"."+split[1].substring(0, 2);
                    item.setChannelPrice(num);
                }
                else if("".equals(content)){
                    item.setChannelPrice("0");
                }
                else{
                    //item.setChannelPrice(content);

                    if (content!=null&&(!"".equals(content))) {

                        Double d = Double.parseDouble(content);
                        //String num = new DecimalFormat("#0.00").format(d);
                        String[] split = String.valueOf(d+0.000001).split("\\.");
                        String num = split[0]+"."+split[1].substring(0, 2);
                        item.setChannelPrice(num);

                    }else{
                        item.setChannelPrice(content);
                    }

                }

                //item.setChannelPrice(itemEt.getText().toString());
                itemEt = (EditText)view.findViewById(R.id.askgoods_et_sellproce);// 零售价

                content = itemEt.getText().toString();
                if(".".equals(content)){//
                    item.setSellPrice("0.0");
                }
                else if(content.length()>1&&content.endsWith(".")){
                    item.setSellPrice(content+"0");
                }
                else if(content.length()>1&&content.startsWith(".")){
                    //item.setSellPrice("0"+content);
                    // 以小数点开头,截取小数点后两位
                    Double d = Double.parseDouble("0"+content);
                    //String num = new DecimalFormat("#0.00").format(d);
                    String[] split = String.valueOf(d+0.000001).split("\\.");
                    String num = split[0]+"."+split[1].substring(0, 2);
                    item.setSellPrice(num);
                }
                else if("".equals(content)){
                    item.setSellPrice("0");
                }
                else{
                    //item.setSellPrice(content);

                    if (content!=null&&(!"".equals(content))) {

                        Double d = Double.parseDouble(content);
                        //String num = new DecimalFormat("#0.00").format(d);
                        String[] split = String.valueOf(d+0.000001).split("\\.");
                        String num = split[0]+"."+split[1].substring(0, 2);
                        item.setSellPrice(num);

                    }else{
                        item.setSellPrice(content);
                    }
                }

                //item.setSellPrice(itemEt.getText().toString());
            }
            view = checkGoodsLv.getChildAt(i);
            if (view != null) {
                //itemBtn = (Button)view.findViewById(R.id.checkgoods_et_firstdate);
                //item.setFristdate(itemBtn.getText().toString());
                itemEt = (EditText)view.findViewById(R.id.checkgoods_et_prevnum);// 订单量
                item.setPrevNum(itemEt.getText().toString());
                itemEt = (EditText)view.findViewById(R.id.checkgoods_et_daysell);// 日销量
                item.setDaySellNum(itemEt.getText().toString());
                itemEt = (EditText)view.findViewById(R.id.checkgoods_et_addcard);// 累计卡
                item.setAddcard(itemEt.getText().toString());

            }
        }
        service.saveInvoicing(dataLst, visitId, termId);

    }


}
