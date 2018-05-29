package et.tsingtaopad.dd.ddweekplan;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.AlertKeyValueAdapter;
import et.tsingtaopad.adapter.DayDetailSelectKeyValueAdapter;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnDismissListener;
import et.tsingtaopad.db.table.MitPlanweekM;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.dd.ddweekplan.domain.DayDetailStc;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectService;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.listviewintf.ILongClick;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class DayDetailAdapter extends BaseAdapter implements View.OnClickListener {

    private final String TAG = "DayDetailAdapter";

    private Activity context;
    private List<DayDetailStc> dataLst;
    private ILongClick listener;
    private AlertView mAlertViewExt;//窗口拓展例子
    private DayDetailStc dayDetailStc;
    private XtTermSelectService xtSelectService;
    private MitPlanweekM weekplan;
    private boolean isChecking;// true:正在审核/审核通过   false:未制定,未提交,未通过

    public DayDetailAdapter(Activity context, List<DayDetailStc> dataLst, MitPlanweekM weekplan,ILongClick listener) {
        this.context = context;
        this.dataLst = dataLst;
        this.listener = listener;
        this.weekplan = weekplan;
        xtSelectService = new XtTermSelectService(context);

        if ("2".equals(weekplan.getStatus()) || "3".equals(weekplan.getStatus())) {// 0未制定1未提交2待审核3审核通过4未通过
            isChecking = true;
        } else {
            isChecking = false;
        }
    }

    @Override
    public int getCount() {
        if (CheckUtil.IsEmpty(dataLst)) {
            return 0;
        } else {
            return dataLst.size();
        }
    }

    @Override
    public Object getItem(int arg0) {
        if (CheckUtil.IsEmpty(dataLst)) {
            return 0;
        } else {
            return dataLst.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("check ", position + "  " + DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weekplan_daydetail, null);
            holder.ll_all = (LinearLayout) convertView.findViewById(R.id.item_daydetail_ll_all);
            holder.rl_check = (RelativeLayout) convertView.findViewById(R.id.item_daydetail_rl_check);
            holder.tv_checkname = (TextView) convertView.findViewById(R.id.item_daydetail_tv_check);
            holder.rl_areaid = (RelativeLayout) convertView.findViewById(R.id.item_daydetail_rl_areaid);
            holder.tv_areaname = (TextView) convertView.findViewById(R.id.item_daydetail_tv_areaid);
            holder.tv_gridkey = (RelativeLayout) convertView.findViewById(R.id.item_daydetail_tv_gridkey);
            holder.tv_valgridname = (TextView) convertView.findViewById(R.id.item_daydetail_tv_valgridkey);
            holder.rl_routekey = (RelativeLayout) convertView.findViewById(R.id.item_daydetail_rl_routekey);
            holder.tv_routename = (TextView) convertView.findViewById(R.id.item_daydetail_tv_routekey);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DayDetailStc item = dataLst.get(position);

        holder.ll_all.setTag(position);
        holder.ll_all.setOnLongClickListener(listener);

        // 追溯项
        //holder.tv_checkname.setText(item.getValchecknameLv().toString().substring(1,item.getValchecknameLv().toString().length()-1));
        if(item.getValcheckname()!=null){
            //holder.tv_routename.setText(item.getValroutenames().substring(0,item.getValroutenames().length()-1));
            holder.tv_checkname.setText(item.getValcheckname());
        }else{
            holder.tv_checkname.setText("");
        }

        // 追溯区域
        holder.tv_areaname.setText(item.getValareaname());
        // 追溯定格
        holder.tv_valgridname.setText(item.getValgridname());
        // 追溯路线
        if(item.getValroutenames()!=null){
            //holder.tv_routename.setText(item.getValroutenames().substring(0,item.getValroutenames().length()-1));
            holder.tv_routename.setText(item.getValroutenames());
        }else{
            holder.tv_routename.setText("");
        }

        holder.rl_check.setTag(position);
        holder.rl_areaid.setTag(position);
        holder.tv_gridkey.setTag(position);
        holder.rl_routekey.setTag(position);
        holder.rl_check.setOnClickListener(this);
        holder.rl_areaid.setOnClickListener(this);
        holder.tv_gridkey.setOnClickListener(this);
        holder.rl_routekey.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int posi = (int) v.getTag();
        switch (v.getId()) {
            case R.id.item_daydetail_rl_check:// 追溯项
                // alertShow3(posi);
                if(!isChecking){
                    alertShow7(posi);
                }else{
                    Toast.makeText(context,"审核状态下不可修改",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.item_daydetail_rl_areaid:// 追溯区域

                if(!isChecking){
                    alertShow4(posi);
                }else{
                    Toast.makeText(context,"审核状态下不可修改",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_daydetail_tv_gridkey:// 追溯定格
                if(!isChecking){
                    alertShow5(posi);
                }else{
                    Toast.makeText(context,"审核状态下不可修改",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_daydetail_rl_routekey:// 追溯路线
                if(!isChecking){
                    alertShow6(posi);
                }else{
                    Toast.makeText(context,"审核状态下不可修改",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private class ViewHolder {
        private LinearLayout ll_all;
        private RelativeLayout rl_check;
        private TextView tv_checkname;
        private RelativeLayout rl_areaid;
        private TextView tv_areaname;
        private RelativeLayout tv_gridkey;
        private TextView tv_valgridname;
        private RelativeLayout rl_routekey;
        private TextView tv_routename;
    }

    // 追溯项
    public void alertShow3(final int position) {

        mAlertViewExt = new AlertView(null, null, null, null,
                null, context, AlertView.Style.ActionSheet, null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.alert_daydetail_form, null);

        RelativeLayout rl_back1 = (RelativeLayout) extView.findViewById(R.id.top_navigation_rl_back1);
        android.support.v7.widget.AppCompatTextView bt_back1 = (android.support.v7.widget.AppCompatTextView) extView.findViewById(R.id.top_navigation_bt_back1);
        RelativeLayout rl_confirm1 = (RelativeLayout) extView.findViewById(R.id.top_navigation_rl_confirm1);
        android.support.v7.widget.AppCompatTextView bt_confirm1 = (android.support.v7.widget.AppCompatTextView) extView.findViewById(R.id.top_navigation_bt_confirm1);

        final CheckBox cb1 = (CheckBox) extView.findViewById(R.id.result_daydetail_cb1);
        final CheckBox cb2 = (CheckBox) extView.findViewById(R.id.result_daydetail_cb2);
        final CheckBox cb3 = (CheckBox) extView.findViewById(R.id.result_daydetail_cb3);
        final CheckBox cb4 = (CheckBox) extView.findViewById(R.id.result_daydetail_cb4);

        DayDetailStc dayDetailStc = dataLst.get(position);
        final List<String> checkNames = dayDetailStc.getValchecknameLv();
        if(checkNames!=null){
            for (String s : checkNames) {
                if (context.getString(R.string.workplan_basedata).equals(s)) {// 基础数据群
                    cb1.setChecked(true);
                } else if (context.getString(R.string.workplan_netdata).equals(s)) {// 网络数据群
                    cb2.setChecked(true);
                } else if (context.getString(R.string.workplan_viedata).equals(s)) {// 竞品数据群
                    cb3.setChecked(true);
                } else if (context.getString(R.string.workplan_privicedata).equals(s)) {// 价格数据群
                    cb4.setChecked(true);
                }
            }
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
                if ((!cb1.isChecked()) && (!cb2.isChecked()) && (!cb3.isChecked()) && (!cb4.isChecked())) {
                    Toast.makeText(context, "请至少勾选一项", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkNames.clear();
                if (cb1.isChecked()) {// 基础数据群
                    checkNames.add(context.getString(R.string.workplan_basedata));
                }
                if (cb2.isChecked()) {// 网络数据群
                    checkNames.add(context.getString(R.string.workplan_netdata));
                }
                if (cb3.isChecked()) {// 竞品数据群
                    checkNames.add(context.getString(R.string.workplan_viedata));
                }
                if (cb4.isChecked()) {// 价格数据群
                    checkNames.add(context.getString(R.string.workplan_privicedata));
                }
                mAlertViewExt.dismiss();
                notifyDataSetChanged();
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

    // 追溯区域
    public void alertShow4(final int position) {

        final DayDetailStc item = dataLst.get(position);
        final List<MstMarketareaM> valueLst = xtSelectService.getMstMarketareaMList(PrefUtils.getString(context, "departmentid", ""));

        mAlertViewExt = new AlertView("请选择区域", null, null, null,
                null, context, AlertView.Style.ActionSheet, null);

        ViewGroup lvextView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.alert_list_form, null);
        ListView lvlistview = (ListView) lvextView.findViewById(R.id.alert_list);
        AlertKeyValueAdapter lvkeyValueAdapter = new AlertKeyValueAdapter(context, valueLst,
                new String[]{"areaid", "areaname"}, item.getValareakey());
        lvlistview.setAdapter(lvkeyValueAdapter);
        lvlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item.setValareaname(valueLst.get(position).getAreaname());
                item.setValareakey(valueLst.get(position).getAreaid());
                mAlertViewExt.dismiss();

                item.setValgridkey("");
                item.setValgridname("");
                item.setValroutekeys("");
                item.setValroutenames("");

                notifyDataSetChanged();
            }
        });


        mAlertViewExt.addExtView(lvextView);
        mAlertViewExt.setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                DbtLog.logUtils(TAG, "取消选择结果");
            }
        });
        mAlertViewExt.show();
    }

    // 追溯定格
    public void alertShow5(final int position) {

        final DayDetailStc item = dataLst.get(position);
        final List<MstGridM> valueLst = xtSelectService.getMstGridMList(FunUtil.isBlankOrNullTo(item.getValareakey(),""));

        mAlertViewExt = new AlertView("请选择定格", null, null, null,
                null, context, AlertView.Style.ActionSheet, null);

        ViewGroup lvextView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.alert_list_form, null);
        ListView lvlistview = (ListView) lvextView.findViewById(R.id.alert_list);
        AlertKeyValueAdapter lvkeyValueAdapter = new AlertKeyValueAdapter(context, valueLst,
                new String[]{"gridkey", "gridname"}, item.getValgridkey());
        lvlistview.setAdapter(lvkeyValueAdapter);
        lvlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item.setValgridkey(valueLst.get(position).getGridkey());
                item.setValgridname(valueLst.get(position).getGridname());
                mAlertViewExt.dismiss();
                item.setValroutekeys("");
                item.setValroutenames("");
                notifyDataSetChanged();
            }
        });


        mAlertViewExt.addExtView(lvextView);
        mAlertViewExt.setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                DbtLog.logUtils(TAG, "取消选择结果");
            }
        });
        mAlertViewExt.show();
    }

    // 追溯路线
    public void alertShow6(final int position) {

        final DayDetailStc item = dataLst.get(position);
        List<MstRouteM> valueLst = xtSelectService.getMstRouteMList(FunUtil.isBlankOrNullTo(item.getValgridkey(),""));
        final List<KvStc> typeLst = new ArrayList<KvStc>();
        for (MstRouteM routeM : valueLst) {
            KvStc kvStc =new KvStc();
            kvStc.setKey(routeM.getRoutekey());
            kvStc.setValue(routeM.getRoutename());
            kvStc.setIsDefault("");
            typeLst.add(kvStc);
        }

        mAlertViewExt = new AlertView(null, null, null, null,
                null, context, AlertView.Style.ActionSheet, null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.alert_dayroute_form, null);

        final ListView dataLv = (ListView) extView.findViewById(R.id.alert_dayroute_lv);
        RelativeLayout rl_back1 = (RelativeLayout) extView.findViewById(R.id.top_navigation_rl_back1);
        android.support.v7.widget.AppCompatTextView bt_back1 = (android.support.v7.widget.AppCompatTextView) extView.findViewById(R.id.top_navigation_bt_back1);
        RelativeLayout rl_confirm1 = (RelativeLayout) extView.findViewById(R.id.top_navigation_rl_confirm1);
        android.support.v7.widget.AppCompatTextView bt_confirm1 = (android.support.v7.widget.AppCompatTextView) extView.findViewById(R.id.top_navigation_bt_confirm1);

        // 获取已选中的集合
        List<String>  selectedId = new ArrayList<String>();
        if(!TextUtils.isEmpty(item.getValroutekeys())){
            selectedId = Arrays.asList(item.getValroutekeys().split(","));
        }

        // 标记选中状态
        for (KvStc kvstc : typeLst) {
            for (String itemselect : selectedId) {
                if (kvstc.getKey().equals(itemselect)) {
                    kvstc.setIsDefault("1");// 0:没选中 1已选中
                }
            }
        }

        final DayDetailSelectKeyValueAdapter sadapter = new DayDetailSelectKeyValueAdapter(context,
                typeLst, new String[]{"key","value"}, item.getValroutekeys());
        dataLv.setAdapter(sadapter);
        dataLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                CheckBox itemCB = (CheckBox) arg1.findViewById(R.id.common_multiple_cb_lvitem);
                TextView itemTv = (TextView) arg1.findViewById(R.id.common_multiple_tv_lvitem);
                itemCB.toggle();//点击整行可以显示效果

                String routekey = FunUtil.isBlankOrNullTo(itemTv.getHint(), " ") + ",";
                String routename = FunUtil.isBlankOrNullTo(itemTv.getText().toString(), " ") + ",";
                if (itemCB.isChecked()) {
                    item.setValroutekeys(FunUtil.isBlankOrNullTo(item.getValroutekeys(),"")  + routekey);
                    item.setValroutenames(FunUtil.isBlankOrNullTo(item.getValroutenames(),"") + routename);
                    ((KvStc)typeLst.get(arg2)).setIsDefault("1");
                } else {
                    item.setValroutekeys(FunUtil.isBlankOrNullTo(item.getValroutekeys(),"") .replace(routekey, ""));
                    item.setValroutenames(FunUtil.isBlankOrNullTo(item.getValroutenames(),"").replace(routename, ""));
                    ((KvStc)typeLst.get(arg2)).setIsDefault("0");
                }
                sadapter.notifyDataSetChanged();
            }
        });


        // 确定
        rl_confirm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                /*item.setValroutekeys(item.getValroutekeys().substring(0,item.getValroutekeys().length()-1));
                item.setValroutenames(item.getValroutenames().substring(0,item.getValroutenames().length()-1));*/

                mAlertViewExt.dismiss();
                notifyDataSetChanged();
            }
        });

        // 取消
        rl_back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

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

    // 新追溯项
    public void alertShow7(final int position) {

        final DayDetailStc item = dataLst.get(position);

        final List<KvStc> typeLst = new ArrayList<KvStc>();
        /*List<MstRouteM> valueLst = xtSelectService.getMstRouteMList(FunUtil.isBlankOrNullTo(item.getValgridkey(),""));
        for (MstRouteM routeM : valueLst) {
            KvStc kvStc =new KvStc();
            kvStc.setKey(routeM.getRoutekey());
            kvStc.setValue(routeM.getRoutename());
            kvStc.setIsDefault("");
            typeLst.add(kvStc);
        }*/

        KvStc kvStc1 =new KvStc();
        kvStc1.setKey("check001");
        kvStc1.setValue("check001");
        kvStc1.setIsDefault("");
        KvStc kvStc2 =new KvStc();
        kvStc2.setKey("check002");
        kvStc2.setValue("check002");
        kvStc2.setIsDefault("");
        KvStc kvStc3 =new KvStc();
        kvStc3.setKey("check003");
        kvStc3.setValue("check003");
        kvStc3.setIsDefault("");
        KvStc kvStc4 =new KvStc();
        kvStc4.setKey("check004");
        kvStc4.setValue("check004");
        kvStc4.setIsDefault("");
        typeLst.add(kvStc1);
        typeLst.add(kvStc2);
        typeLst.add(kvStc3);
        typeLst.add(kvStc4);

        mAlertViewExt = new AlertView(null, null, null, null,
                null, context, AlertView.Style.ActionSheet, null);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.alert_dayroute_form, null);

        final ListView dataLv = (ListView) extView.findViewById(R.id.alert_dayroute_lv);
        RelativeLayout rl_back1 = (RelativeLayout) extView.findViewById(R.id.top_navigation_rl_back1);
        android.support.v7.widget.AppCompatTextView bt_back1 = (android.support.v7.widget.AppCompatTextView) extView.findViewById(R.id.top_navigation_bt_back1);
        RelativeLayout rl_confirm1 = (RelativeLayout) extView.findViewById(R.id.top_navigation_rl_confirm1);
        android.support.v7.widget.AppCompatTextView bt_confirm1 = (android.support.v7.widget.AppCompatTextView) extView.findViewById(R.id.top_navigation_bt_confirm1);

        // 获取已选中的集合
        List<String>  selectedId = new ArrayList<String>();
        if(!TextUtils.isEmpty(item.getValcheckkey())){
            selectedId = Arrays.asList(item.getValcheckkey().split(","));
        }

        // 标记选中状态
        for (KvStc kvstc : typeLst) {
            for (String itemselect : selectedId) {
                if (kvstc.getKey().equals(itemselect)) {
                    kvstc.setIsDefault("1");// 0:没选中 1已选中
                }
            }
        }

        final DayDetailSelectKeyValueAdapter sadapter = new DayDetailSelectKeyValueAdapter(context,
                typeLst, new String[]{"key","value"}, item.getValroutekeys());
        dataLv.setAdapter(sadapter);
        dataLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                CheckBox itemCB = (CheckBox) arg1.findViewById(R.id.common_multiple_cb_lvitem);
                TextView itemTv = (TextView) arg1.findViewById(R.id.common_multiple_tv_lvitem);
                itemCB.toggle();//点击整行可以显示效果

                String checkkey = FunUtil.isBlankOrNullTo(itemTv.getHint(), " ") + ",";
                String checkname = FunUtil.isBlankOrNullTo(itemTv.getText().toString(), " ") + ",";
                if (itemCB.isChecked()) {
                    item.setValcheckkey(FunUtil.isBlankOrNullTo(item.getValcheckkey(),"")  + checkkey);
                    item.setValcheckname(FunUtil.isBlankOrNullTo(item.getValcheckname(),"") + checkname);
                    ((KvStc)typeLst.get(arg2)).setIsDefault("1");
                } else {
                    item.setValcheckkey(FunUtil.isBlankOrNullTo(item.getValcheckkey(),"") .replace(checkkey, ""));
                    item.setValcheckname(FunUtil.isBlankOrNullTo(item.getValcheckname(),"").replace(checkname, ""));
                    ((KvStc)typeLst.get(arg2)).setIsDefault("0");
                }
                sadapter.notifyDataSetChanged();
            }
        });


        // 确定
        rl_confirm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                /*item.setValroutekeys(item.getValroutekeys().substring(0,item.getValroutekeys().length()-1));
                item.setValroutenames(item.getValroutenames().substring(0,item.getValroutenames().length()-1));*/

                mAlertViewExt.dismiss();
                notifyDataSetChanged();
            }
        });

        // 取消
        rl_back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

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

}