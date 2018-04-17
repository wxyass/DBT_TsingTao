package et.tsingtaopad.dd.ddzs.zschatvie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.chatvie.XtChatVieService;
import et.tsingtaopad.dd.ddxt.chatvie.XtVieSourceAdapter;
import et.tsingtaopad.dd.ddxt.chatvie.XtVieStatusAdapter;
import et.tsingtaopad.dd.ddxt.chatvie.domain.XtChatVieStc;
import et.tsingtaopad.dd.ddzs.zsinvoicing.ZsInvoicingAdapter;
import et.tsingtaopad.listviewintf.IClick;
import et.tsingtaopad.listviewintf.ILongClick;
import et.tsingtaopad.dd.ddzs.zschatvie.zsaddchatvie.ZsAddChatVieFragment;
import et.tsingtaopad.dd.ddzs.zsshopvisit.ZsVisitShopActivity;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class ZsChatvieFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private final String TAG = "ZsChatvieFragment";

    private ImageView zdzs_chatvie_point1;
    private TextView zdzs_chatvie_textview01;
    private Button zdzs_chatvie_bt_addrelation;
    private ListView zsChatvieLv;
    private RelativeLayout zdzs_chatvie_rl_clearvie;
    private TextView zdzs_chatvie_rl_clearvie_con1;
    private TextView zdzs_chatvie_rl_clearvie_statue;
    private EditText zdzs_chatvie_et_visitreport;

    ZsChatvieAdapter zsChatvieAdapter;

    public static final int ZS_ADD_VIE_SUC = 3;// 新增竞品关系成功
    MyHandler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_chatvie, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        zdzs_chatvie_point1 = (ImageView) view.findViewById(R.id.zdzs_chatvie_point1);
        zdzs_chatvie_textview01 = (TextView) view.findViewById(R.id.zdzs_chatvie_textview01);
        zdzs_chatvie_bt_addrelation = (Button) view.findViewById(R.id.zdzs_chatvie_bt_addrelation);
        zsChatvieLv = (ListView) view.findViewById(R.id.zdzs_chatvie_lv_viesource);
        zdzs_chatvie_rl_clearvie = (RelativeLayout) view.findViewById(R.id.zdzs_chatvie_rl_clearvie);
        zdzs_chatvie_rl_clearvie_con1 = (TextView) view.findViewById(R.id.zdzs_chatvie_rl_clearvie_con1);
        zdzs_chatvie_rl_clearvie_statue = (TextView) view.findViewById(R.id.zdzs_chatvie_rl_clearvie_statue);
        zdzs_chatvie_et_visitreport = (EditText) view.findViewById(R.id.zdzs_chatvie_et_visitreport);


        zdzs_chatvie_bt_addrelation.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        xtChatVieService = new ZsChatVieService(getActivity(), null);
        handler = new MyHandler(this);

        initProData();


    }

    List<XtChatVieStc> dataLst;
    ZsChatVieService xtChatVieService;
    private MstVisitMTemp visitMTemp;

    // 初始化数据
    private void initProData() {
        xtChatVieService.delRepeatVistProduct(visitId);
        dataLst = xtChatVieService.queryVieProTemp(visitId);
        visitMTemp = xtChatVieService.findVisitTempById(visitId);// 拜访临时表记录

        if (visitMTemp != null) {
            // 是否瓦解竞品  0:未瓦解  1:瓦解
            if (ConstValues.FLAG_1.equals(visitMTemp.getIscmpcollapse()) ) {
                zdzs_chatvie_rl_clearvie_con1.setText("是");
            } else {
                zdzs_chatvie_rl_clearvie_con1.setText("否");
            }
        }

        // 竞品信息
        zsChatvieAdapter = new ZsChatvieAdapter(getActivity(), dataLst,new IClick(){
            @Override
            public void listViewItemClick(int position, View v) {
                alertShow3();
            }
        });
        zsChatvieLv.setAdapter(zsChatvieAdapter);
        ViewUtil.setListViewHeight(zsChatvieLv);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zdzs_chatvie_bt_addrelation:// 新增竞品
                Bundle bundle = new Bundle();
                bundle.putSerializable("termId", termStc.getTerminalkey());
                bundle.putSerializable("termname", termStc.getTerminalname());
                bundle.putSerializable("channelId", termStc.getMinorchannel());// 次渠道
                bundle.putSerializable("termStc", termStc);
                bundle.putSerializable("visitKey", visitId);//visitId
                bundle.putSerializable("seeFlag", seeFlag);// 默认0   0:拜访 1:查看

                ZsAddChatVieFragment xtaddchatviefragment = new ZsAddChatVieFragment(handler);
                xtaddchatviefragment.setArguments(bundle);

                ZsVisitShopActivity xtVisitShopActivity = (ZsVisitShopActivity) getActivity();
                xtVisitShopActivity.changeXtvisitFragment(xtaddchatviefragment, "zsvisitshopactivity");
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
        SoftReference<ZsChatvieFragment> fragmentRef;

        public MyHandler(ZsChatvieFragment fragment) {
            fragmentRef = new SoftReference<ZsChatvieFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ZsChatvieFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            ArrayList<KvStc> products = (ArrayList<KvStc>) msg.obj;
            Bundle bundle = msg.getData();
            KvStc agency = (KvStc) bundle.getSerializable("agency");

            // 处理UI 变化
            switch (msg.what) {
                case ZS_ADD_VIE_SUC:
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

                zsChatvieAdapter.notifyDataSetChanged();
                ViewUtil.setListViewHeight(zsChatvieLv);//

            }
        }
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
    public void alertShow3() {
        List<KvStc> sureOrFail = new ArrayList<>();
        sureOrFail.add(new KvStc("zhengque","正确","-1"));
        sureOrFail.add(new KvStc("cuowu","错误(去修正)","-1"));
        mAlertViewExt = new AlertView(null, null, null, null, null, getActivity(), AlertView.Style.ActionSheet, null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
        ListView listview = (ListView) extView.findViewById(R.id.alert_list);
        AlertKeyValueAdapter keyValueAdapter = new AlertKeyValueAdapter(getActivity(), sureOrFail,
                new String[]{"key", "value"}, "zhengque");
        listview.setAdapter(keyValueAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(1==position){
                    /*Bundle bundle = new Bundle();
                    bundle.putString("proName", "");
                    ZsAmendFragment zsAmendFragment = new ZsAmendFragment(handler);
                    zsAmendFragment.setArguments(bundle);
                    ZsVisitShopActivity zsVisitShopActivity = (ZsVisitShopActivity)getActivity();
                    zsVisitShopActivity.changeXtvisitFragment(zsAmendFragment,"zsamendfragment");*/
                }

                mAlertViewExt.dismiss();
            }
        });
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                DbtLog.logUtils(TAG, "取消选择结果");
            }
        });
        mAlertViewExt.show();
    }


    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag)) return;

        /*View view;
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

        xtChatVieService.saveXtVie(dataLst, visitId, termId, visitMTemp);*/
    }

    private AlertView mAlertViewExt;//窗口拓展例子

    // 长按删除我品供货关系弹窗
    private void deleteviesupply(int position, final XtChatVieStc xtChatVieStc) {
        String proName = xtChatVieStc.getProName();
        // 普通窗口
        mAlertViewExt = new AlertView("删除竞品: " + proName, null, "取消", new String[]{"确定"}, null, getActivity(), AlertView.Style.Alert,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        //Toast.makeText(getApplicationContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                        if (0 == position) {// 确定按钮:0   取消按钮:-1
                            //if (ViewUtil.isDoubleClick(v.getId(), 2500)) return;
                            if (!CheckUtil.isBlankOrNull(xtChatVieStc.getRecordId())) {
                                DbtLog.logUtils(TAG, "删除竞品供货关系：是");
                                XtChatVieService service = new XtChatVieService(getActivity(), null);
                                boolean isFlag = service.deleteSupply(xtChatVieStc.getRecordId(), termId, xtChatVieStc.getProId());
                                if (isFlag) {
                                    // 删除界面listView相应行
                                    dataLst.remove(position);
                                    zsChatvieAdapter.notifyDataSetChanged();
                                    zsChatvieAdapter.setDelPosition(position);
                                    ViewUtil.setListViewHeight(zsChatvieLv);

                                } else {
                                    Toast.makeText(getActivity(), "删除产品失败!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // 删除界面listView相应行
                                dataLst.remove(position);
                                zsChatvieAdapter.notifyDataSetChanged();
                                zsChatvieAdapter.setDelPosition(position);
                                ViewUtil.setListViewHeight(zsChatvieLv);
                            }
                        }
                    }
                })
                .setCancelable(true)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(Object o) {
                        DbtLog.logUtils(TAG, "删除竞品供货关系：否");
                    }
                });
        mAlertViewExt.show();
    }
}
