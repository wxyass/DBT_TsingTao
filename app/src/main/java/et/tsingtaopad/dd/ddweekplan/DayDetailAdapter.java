package et.tsingtaopad.dd.ddweekplan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnDismissListener;
import et.tsingtaopad.dd.ddweekplan.domain.DayDetailStc;
import et.tsingtaopad.dd.ddweekplan.domain.DayPlanStc;
import et.tsingtaopad.dd.ddzs.zsinvoicing.ZsInvocingAmendFragment;
import et.tsingtaopad.dd.ddzs.zsinvoicing.ZsInvoicingFragment;
import et.tsingtaopad.dd.ddzs.zsshopvisit.ZsVisitShopActivity;
import et.tsingtaopad.listviewintf.IClick;
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

    public DayDetailAdapter(Activity context, List<DayDetailStc> dataLst, ILongClick listener) {
        this.context = context;
        this.dataLst = dataLst;
        this.listener = listener;
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
            holder.tv_check = (TextView) convertView.findViewById(R.id.item_daydetail_tv_check);
            holder.rl_areaid = (RelativeLayout) convertView.findViewById(R.id.item_daydetail_rl_areaid);
            holder.tv_areaid = (TextView) convertView.findViewById(R.id.item_daydetail_tv_areaid);
            holder.tv_gridkey = (RelativeLayout) convertView.findViewById(R.id.item_daydetail_tv_gridkey);
            holder.tv_valgridkey = (TextView) convertView.findViewById(R.id.item_daydetail_tv_valgridkey);
            holder.rl_routekey = (RelativeLayout) convertView.findViewById(R.id.item_daydetail_rl_routekey);
            holder.tv_routekey = (TextView) convertView.findViewById(R.id.item_daydetail_tv_routekey);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DayDetailStc item = dataLst.get(position);

        holder.ll_all.setTag(position);
        holder.ll_all.setOnLongClickListener(listener);

        holder.tv_check.setText(item.getValchecknameLv().toString());

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
                alertShow3(posi);
                //Toast.makeText(context, "点击了第" + posi + "追溯项", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_daydetail_rl_areaid:// 追溯区域
                Toast.makeText(context, "点击了第" + posi + "追溯区域", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_daydetail_tv_gridkey:// 追溯定格
                Toast.makeText(context, "点击了第" + posi + "追溯定格", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_daydetail_rl_routekey:// 追溯路线
                Toast.makeText(context, "点击了第" + posi + "追溯路线", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private class ViewHolder {
        private LinearLayout ll_all;
        private RelativeLayout rl_check;
        private TextView tv_check;
        private RelativeLayout rl_areaid;
        private TextView tv_areaid;
        private RelativeLayout tv_gridkey;
        private TextView tv_valgridkey;
        private RelativeLayout rl_routekey;
        private TextView tv_routekey;
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

}