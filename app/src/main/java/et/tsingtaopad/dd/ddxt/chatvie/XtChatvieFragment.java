package et.tsingtaopad.dd.ddxt.chatvie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.chatvie.addchatvie.XtAddChatVieFragment;
import et.tsingtaopad.dd.ddxt.chatvie.domain.XtChatVieStc;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.domain.ChatVieStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtChatvieFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private final String TAG = "XtChatvieFragment";

    private ImageView point1;
    private Button addrelationBtn;
    private ListView viesourceLv;
    private ListView viestatusLv;
    private et.tsingtaopad.view.DdSlideSwitch clearvieSw;
    private EditText visitreportEt;// 拜访记录
    private Button nextBtn;

    public static final int ADD_VIE_SUC = 3;
    MyHandler handler;

    private XtVieSourceAdapter xtVieSourceAdapter;
    private XtVieStatusAdapter xtstatusAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_chatvie, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        addrelationBtn = (Button) view.findViewById(R.id.xtbf_chatvie_bt_addrelation);
        viesourceLv = (ListView) view.findViewById(R.id.xtbf_chatvie_lv_viesource);
        viestatusLv = (ListView) view.findViewById(R.id.xtbf_chatvie_lv_viestatus);
        clearvieSw = (et.tsingtaopad.view.DdSlideSwitch) view.findViewById(R.id.xtbf_chatvie_sw_clearvie);
        visitreportEt = (EditText) view.findViewById(R.id.xtbf_chatvie_et_visitreport);
        nextBtn = (Button) view.findViewById(R.id.xtbf_chatvie_bt_next);

        addrelationBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        xtChatVieService = new XtChatVieService(getActivity(), null);
        handler = new MyHandler(this);

        initProData();


    }

    List<XtChatVieStc> dataLst;
    XtChatVieService xtChatVieService;
    private MstVisitMTemp visitMTemp;

    // 初始化数据
    private void initProData() {
        xtChatVieService.delRepeatVistProduct(visitId);
        dataLst = xtChatVieService.queryVieProTemp(visitId);
        visitMTemp = xtChatVieService.findVisitTempById(visitId);// 拜访临时表记录

        if (visitMTemp != null) {
            // 是否瓦解竞品  0:未瓦解  1:瓦解
            if (ConstValues.FLAG_1.equals(visitMTemp.getIscmpcollapse()) ) {
                clearvieSw.setStatus(true);
            } else {
                clearvieSw.setStatus(false);
            }}

        // 竞品来源
        xtVieSourceAdapter = new XtVieSourceAdapter(
                getActivity(), "", dataLst, "", null, null, null, null);//竞品来源
        viesourceLv.setAdapter(xtVieSourceAdapter);
        ViewUtil.setListViewHeight(viesourceLv);

        // 竞品情况
        xtstatusAdapter = new XtVieStatusAdapter(getActivity(), dataLst);//竞品情况
        viestatusLv.setAdapter(xtstatusAdapter);
        ViewUtil.setListViewHeight(viestatusLv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xtbf_chatvie_bt_addrelation:// 新增竞品
                Bundle bundle = new Bundle();
                bundle.putSerializable("termId", termStc.getTerminalkey());
                bundle.putSerializable("termname", termStc.getTerminalname());
                bundle.putSerializable("channelId", termStc.getMinorchannel());// 次渠道
                bundle.putSerializable("termStc", termStc);
                bundle.putSerializable("visitKey", visitId);//visitId
                bundle.putSerializable("seeFlag", seeFlag);// 默认0   0:拜访 1:查看

                XtAddChatVieFragment xtaddchatviefragment = new XtAddChatVieFragment(handler);
                xtaddchatviefragment.setArguments(bundle);

                XtVisitShopActivity xtVisitShopActivity = (XtVisitShopActivity) getActivity();
                xtVisitShopActivity.changeXtvisitFragment(xtaddchatviefragment, "xtaddchatviefragment");
                break;
            case R.id.xtbf_chatvie_bt_next:// 下一页

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
        SoftReference<XtChatvieFragment> fragmentRef;

        public MyHandler(XtChatvieFragment fragment) {
            fragmentRef = new SoftReference<XtChatvieFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            XtChatvieFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            ArrayList<KvStc> products = (ArrayList<KvStc>) msg.obj;
            Bundle bundle = msg.getData();
            KvStc agency = (KvStc) bundle.getSerializable("agency");

            // 处理UI 变化
            switch (msg.what) {
                case ADD_VIE_SUC:
                    fragment.showAddVieProSuc(products, agency);
                    break;
            }
        }
    }

    /**
     * 添加竞品成功 UI
     */
    public void showAddVieProSuc(ArrayList<KvStc> products, KvStc agency) {

        for (KvStc product : products) {

            //DbtLog.logUtils(TAG,"经销商key:"+agency.getKey()+"、经销商名称:"+agency.getValue()+"-->产品key："+product.getKey()+"、产品名称："+product.getValue());
            List<String> proIdLst = FunUtil.getPropertyByName(dataLst, "proId", String.class);
            if (proIdLst.contains(product.getKey())) {
                DbtLog.logUtils(TAG, "产品重复提示");
                Toast.makeText(getActivity(), getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();
            } else {
                XtChatVieStc supplyStc = new XtChatVieStc();
                supplyStc.setProId(product.getKey());
                supplyStc.setProName(product.getValue());
                supplyStc.setCommpayId(agency.getKey());
                dataLst.add(supplyStc);

                xtstatusAdapter.notifyDataSetChanged();
                ViewUtil.setListViewHeight(viestatusLv);//
                xtVieSourceAdapter.notifyDataSetChanged();
                ViewUtil.setListViewHeight(viesourceLv);
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
        XtChatVieStc item;
        // 遍历LV,获取采集数据
        for (int i = 0; i < dataLst.size(); i++) {
            item = dataLst.get(i);
            view = viesourceLv.getChildAt(i);
            if (view == null) continue;

            // 聊竞品-进店价
            itemEt = (EditText)view.findViewById(R.id.item_viesource_et_qudao);
            String content = itemEt.getText().toString();
            item.setChannelPrice(FunUtil.getDecimalsData(content));
            //item.setChannelPrice(itemEt.getText().toString());

            // 聊竞品-零售价
            itemEt = (EditText)view.findViewById(R.id.item_viesource_et_lingshou);
            content = itemEt.getText().toString();
            item.setSellPrice(FunUtil.getDecimalsData(content));
            //item.setSellPrice(itemEt.getText().toString());

            itemEt = (EditText)view.findViewById(R.id.item_viesource_et_agencyname);
            item.setAgencyName(itemEt.getText().toString());

            view = viestatusLv.getChildAt(i);
            if (view == null) continue;
            itemEt = (EditText)view.findViewById(R.id.item_viestatus_et_currstore);
            item.setCurrStore(itemEt.getText().toString());
            itemEt = (EditText)view.findViewById(R.id.item_viestatus_et_monthsell);
            item.setMonthSellNum(itemEt.getText().toString());
            itemEt = (EditText)view.findViewById(R.id.item_viestatus_et_describe);
            item.setDescribe(itemEt.getText().toString());
        }

        // 是否瓦解竞品 拜访主表相关数据
        if (clearvieSw.getStatus()) {
            visitMTemp.setIscmpcollapse(ConstValues.FLAG_1);// 瓦解
        } else {
            visitMTemp.setIscmpcollapse(ConstValues.FLAG_0);// 未瓦解
        }

        visitMTemp.setRemarks(visitreportEt.getText().toString());

        xtChatVieService.saveXtVie(dataLst, visitId, termId, visitMTemp);
    }


}
