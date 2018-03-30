package et.tsingtaopad.dd.ddxt.checking;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.PictypeDataStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.listener.IClick;
import et.tsingtaopad.view.DdSlideSwitch;
import et.tsingtaopad.view.SlideSwitch;

/**
 * 文件名：XtPromotionAdapter.java</br>
 * 功能描述: 巡店拜访-查指标-促销活动Adapter</br>
 */
public class XtPromotionAdapter extends BaseAdapter {

    private Activity context;
    private List<CheckIndexPromotionStc> dataLst;
    private int countonoff = 0;
    private String visitDate;
    private String seeFlag;

    public XtPromotionAdapter(Activity context, List<CheckIndexPromotionStc> dataLst, String visitDate, String seeFlag) {
        this.context = context;
        this.dataLst = dataLst;
        this.visitDate = visitDate;
        this.seeFlag = seeFlag;
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
            return null;
        } else {
            return dataLst.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d("promotion ", position + "  " + DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_xtbf_checkindex_promotion, null);
            holder.promotionNameTv = (TextView) convertView.findViewById(R.id.item_xt_checkindex_tv_promotionname);// 活动名称
            holder.proNameTv = (TextView) convertView.findViewById(R.id.item_xt_checkindex_tv_proname);// 产品名称
            holder.statusSw = (et.tsingtaopad.view.DdSlideSwitch) convertView.findViewById(R.id.item_xt_checkindex_sw_isacomplish);
            holder.reachnum = (EditText) convertView.findViewById(R.id.item_xt_checkindex_et_zushu);//达成组数

            holder.statusSw.setOnLongSwitchChangedListener(new MySwitchChangedListener(holder) {
                @Override
                public void afterTextChanged(DdSlideSwitch obj, int status, ViewHolder holder) {
                    if (status == DdSlideSwitch.SWITCH_ON) {
                        holder.reachnum.setVisibility(View.VISIBLE);// 达成组数
                        dataLst.get(position).setIsAccomplish(ConstValues.FLAG_1);// 活动设为达成

                    } else {
                        holder.reachnum.setVisibility(View.INVISIBLE);// 达成组数
                        holder.reachnum.setText(null);// 达成组数
                        dataLst.get(position).setIsAccomplish(ConstValues.FLAG_0);// 活动设为未达成
                    }
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.statusSw.setTag(position);

        CheckIndexPromotionStc item = dataLst.get(position);
        holder.promotionNameTv.setHint(item.getPromotKey());
        holder.promotionNameTv.setText(item.getPromotName());
        //holder.proNameTv.setHint(item.getProId());
        //holder.proNameTv.setText(item.getProName());

        // 添加促销活动 隔天默认关闭 //20160301(原因:需求设计如此)
        String todaytime = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        String accepttime = visitDate.substring(0, 10);

        if (todaytime.equals(accepttime)) {// 当天 多次进入这家终端
            if (ConstValues.FLAG_1.equals(item.getIsAccomplish())) {
                holder.statusSw.setStatus(true);
                holder.reachnum.setVisibility(View.VISIBLE);
                holder.reachnum.setText(item.getReachNum());

            } else {
                holder.statusSw.setStatus(false);
                holder.reachnum.setVisibility(View.INVISIBLE);
                holder.reachnum.setText(null);
            }
        } else {// 上次拜访 不是当天 (即:当天第一次拜访)
            holder.statusSw.setStatus(false);
            holder.reachnum.setVisibility(View.INVISIBLE);
            holder.reachnum.setText(null);
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView promotionNameTv;
        private TextView proNameTv;
        private et.tsingtaopad.view.DdSlideSwitch statusSw;
        private EditText reachnum;
    }

    abstract class MySwitchChangedListener implements DdSlideSwitch.OnLongSwitchChangedListener {

        private ViewHolder mHolder;

        public MySwitchChangedListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public void onLongSwitchChanged(DdSlideSwitch obj, int status) {
            afterTextChanged(obj, status, mHolder);
        }

        public abstract void afterTextChanged(DdSlideSwitch obj, int status, ViewHolder holder);
    }


}
