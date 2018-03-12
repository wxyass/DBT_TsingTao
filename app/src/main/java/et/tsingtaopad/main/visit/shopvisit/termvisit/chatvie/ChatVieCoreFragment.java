package et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import et.tsingtaopad.db.table.MstCmpagencyInfo;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.adapter.ChatVieProductAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.adapter.VieSourceAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.adapter.VieStatusAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.domain.ChatVieStc;

/**
 * Created by yangwenmin on 2017/12/25.
 * 聊竞品
 */

public class ChatVieCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "ChatVieCoreFragment";
    private boolean isShow = false;// false:默认   true:加载完数据设为true

    private ChatVieService service;
    private Button addRelationBt;
    private ListView vieStatusLv;
    private ListView vieSourceLv;
    private VieStatusAdapter statusAdapter;
    private VieSourceAdapter sourceAdapter;
    private RadioGroup clearVieRg;
    private EditText remarkEt;

    private List<ChatVieStc> dataLst;
    private String visitId;
    private String termId;
    private MstVisitMTemp visitM;
    private String seeFlag;
    private String visitKey;
    private String isFirstVisit;
    private String termname;
    private String visitDate;
    private String lastTime;

    // 供货关系弹出框相关实例
    private AlertDialog productDialgo;
    private View itemForm;
    private KvStc agency;
    private KvStc product;
    private ListView agencyLv;
    private ListView productLv;
    private ListViewKeyValueAdapter agencyAdapter;
    private ChatVieProductAdapter productAdapter;
    private EditText channelPriceEt;
    private EditText sellPriceEt;

    // 获取用户选择的竞品(多选)
    private ArrayList<KvStc> products;

    public ChatVieCoreFragment() {
    }

    public static ChatVieCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        ChatVieCoreFragment fragment = new ChatVieCoreFragment();
        return fragment;
    }

    public static ChatVieCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, "newInstance(Bundle args)");
        //Bundle args = new Bundle();

        ChatVieCoreFragment fragment = new ChatVieCoreFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_chatvie, null);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        addRelationBt = (Button)view.findViewById(R.id.chatvie_bt_addrelation);// 添加竞品按钮
        vieStatusLv = (ListView)view.findViewById(R.id.chatvie_lv_viestatus);
        vieSourceLv = (ListView)view.findViewById(R.id.chatvie_lv_viesource);
        clearVieRg = (RadioGroup)view.findViewById(R.id.chatvie_rg_clearvie);// 是否瓦解竞品
        remarkEt = (EditText)view.findViewById(R.id.chatvie_et_visitreport);// 拜访记录

        addRelationBt.setOnClickListener(this);
        clearVieRg.setOnClickListener(this);
        clearVieRg.getChildAt(0).setOnClickListener(this);
        ((RadioButton)clearVieRg.getChildAt(1)).setOnClickListener(this);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");

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

        service = new ChatVieService(getActivity(), null);

        //  visitproductTemp临时表中的重复数据
        service.delRepeatVistProduct(visitId);
        // 临时表
        dataLst = service.queryVieProTemp(visitId);

        List<MstCmpagencyInfo> agencyLst = service.queryCmpAgency();
        //竞品来源
        sourceAdapter = new VieSourceAdapter(getActivity(), seeFlag,dataLst, termId, statusAdapter, agencyLst,vieStatusLv,vieSourceLv);
        vieSourceLv.setAdapter(sourceAdapter);
        ViewUtil.setListViewHeight(vieSourceLv);

        //竞品情况
        statusAdapter = new VieStatusAdapter(getActivity(), dataLst);
        vieStatusLv.setAdapter(statusAdapter);
        ViewUtil.setListViewHeight(vieStatusLv);

        // 获取拜访主表信息的拜访记录
        //visitM = service.findVisitById(visitId);
        visitM = service.findVisitTempById(visitId);
        if (visitM != null) {
            if (ConstValues.FLAG_1.equals(visitM.getIscmpcollapse())) {
                clearVieRg.check(R.id.chatvie_rb_clearvie_true);// chatvie_rb_clearvie_true// 控件id
            } else if (ConstValues.FLAG_0.equals(visitM.getIscmpcollapse())) {
                clearVieRg.check(R.id.chatvie_rb_clearvie_false);
            } else {
                clearVieRg.check(R.id.chatvie_rb_clearvie_false);
            }
            remarkEt.setText(visitM.getRemarks());
        } else {
            clearVieRg.check(R.id.chatvie_rb_clearvie_false);
        }

        isShow = true;
    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick()");
        int i = v.getId();
        if (i == R.id.chatvie_bt_addrelation) {
            this.dialogProduct();

        } else if (i == R.id.chatvie_rb_clearvie_true) {
            ViewUtil.initRadioButton2(clearVieRg, v.getId());

        } else if (i == R.id.chatvie_rb_clearvie_false) {
            ViewUtil.initRadioButton2(clearVieRg, v.getId());

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
        productDialgo = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        productDialgo.setView(itemForm, 0, 0, 0, 0);
        productDialgo.show();

        agencyLv = (ListView)itemForm.findViewById(R.id.addrelation_lv_agency);
        productLv = (ListView)itemForm.findViewById(R.id.addrelation_lv_product);
        channelPriceEt = (EditText)itemForm.findViewById(R.id.addrelation_et_channelprice);
        sellPriceEt = (EditText)itemForm.findViewById(R.id.addrelation_et_sellprice);

        // 代理商
        agencyLv.setDividerHeight(0);
        if (!CheckUtil.IsEmpty(GlobalValues.agencyVieLst)) {
            agency = GlobalValues.agencyVieLst.get(0);
        }
        agencyAdapter = new ListViewKeyValueAdapter(
                getActivity(), GlobalValues.agencyVieLst, new String[]{"key","value"},new int[]{R.drawable.bg_agency_up, R.drawable.bg_agency_down});
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
                productAdapter = new ChatVieProductAdapter(
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
        if (!CheckUtil.IsEmpty(GlobalValues.agencyVieLst)) {
            productAdapter = new ChatVieProductAdapter(
                    getActivity(),GlobalValues.agencyVieLst.get(0).getChildLst(), new String[]{"key","value"},new int[]{R.drawable.bg_product_up, R.drawable.bg_product_down});
            productLv.setAdapter(productAdapter);
        }

        productLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
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
                if (product == null) {
                    productDialgo.cancel();
                } else {
                    for (KvStc product : products) {
                        DbtLog.logUtils(TAG,"经销商key:"+agency.getKey()+"、经销商名称:"+agency.getValue()+"-->产品key："+product.getKey()+"、产品名称："+product.getValue());
                        List<String> proIdLst = FunUtil.getPropertyByName(dataLst, "proId", String.class);
                        if (proIdLst.contains(product.getKey())) {
                            //ViewUtil.sendMsg(getActivity(), R.string.addrelation_msg_repetitionadd);
                            Toast.makeText(getActivity(), getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();
                        } else {
                            ChatVieStc supplyStc = new ChatVieStc();
                            supplyStc.setProId(product.getKey());
                            supplyStc.setProName(product.getValue());
                            supplyStc.setCommpayId(agency.getKey());
                            supplyStc.setChannelPrice(channelPriceEt.getText().toString());
                            supplyStc.setSellPrice(sellPriceEt.getText().toString());
                            dataLst.add(supplyStc);

                            statusAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(vieStatusLv);
                            sourceAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(vieSourceLv);
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
        super.onHiddenChanged(hidden);//  hidden true:该界面已隐藏   false: 正在展示

        if(hidden && isShow){
            isShow = false;
            //Toast.makeText(getContext(),"正在保存聊竞品数据",Toast.LENGTH_SHORT).show();
            saveChatVieData();
        }
    }

    // 保存聊竞品页面数据
    private void saveChatVieData() {

        DbtLog.logUtils(TAG, "saveChatVieData()");
        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag)) return;

        View view;
        EditText itemEt;
        ChatVieStc item;
        // 遍历LV,获取采集数据
        for (int i = 0; i < dataLst.size(); i++) {
            item = dataLst.get(i);
            view = vieSourceLv.getChildAt(i);
            if (view == null) continue;

            // 聊竞品-进店价
            itemEt = (EditText)view.findViewById(R.id.viesource_et_channelprice);
            String content = itemEt.getText().toString();
            if(".".equals(content)){//
                item.setChannelPrice("0.0");
            }
            else if(content.length()>1&&content.endsWith(".")){
                item.setChannelPrice(content+"0");

            }
            else if(content.length()>1&&content.startsWith(".")){
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

            // 聊竞品-零售价
            itemEt = (EditText)view.findViewById(R.id.viesource_et_sellprice);
            content = itemEt.getText().toString();
            if(".".equals(content)){//
                item.setSellPrice("0.0");
            }
            else if(content.length()>1&&content.endsWith(".")){
                item.setSellPrice(content+"0");
            }
            else if(content.length()>1&&content.startsWith(".")){
                //item.setSellPrice("0"+content);
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

            // 供销商
            itemEt = (EditText)view.findViewById(R.id.viesource_et_agency);
            item.setAgencyName(itemEt.getText().toString());

            view = vieStatusLv.getChildAt(i);
            if (view == null) continue;
            itemEt = (EditText)view.findViewById(R.id.viestatus_et_currstore);// 当前库存
            item.setCurrStore(itemEt.getText().toString());
            itemEt = (EditText)view.findViewById(R.id.viestatus_et_monthsell);// 销量
            item.setMonthSellNum(itemEt.getText().toString());
            itemEt = (EditText)view.findViewById(R.id.viestatus_et_describle);// 描述
            item.setDescribe(itemEt.getText().toString());
        }

        // 拜访主表相关数据
        if (clearVieRg.getCheckedRadioButtonId()== R.id.chatvie_rb_clearvie_true) {
            visitM.setIscmpcollapse(ConstValues.FLAG_1);
        } else {
            visitM.setIscmpcollapse(ConstValues.FLAG_0);
        }
        visitM.setRemarks(remarkEt.getText().toString());

        service.saveVieTemp(dataLst, visitId, termId, visitM);

    }
}
