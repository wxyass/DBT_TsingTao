package et.tsingtaopad.dd.ddzs.zsinvoicing.zsinvocingtz;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.db.table.MitValaddaccountproMTemp;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.initconstvalues.domain.KvStc;


/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：ZsInvoicingTznumAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-13</br>      
 * 功能描述:
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class ZsInvoicingTznumAdapter extends BaseAdapter implements OnClickListener {

    private Activity context;
    private List<MitValaddaccountproMTemp> dataLst;

    /**
     * 构造函数
     *
     * @param context
     * @param dataLst
     * @param //itemLst
     */
    public ZsInvoicingTznumAdapter(Activity context, List<MitValaddaccountproMTemp> dataLst) {
        this.context = context;
        this.dataLst = dataLst;//
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("calculateItem ", position+"  "+ DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_zdzs_invocing_tz_num, null);
            holder.numTv = (TextView)convertView.findViewById(R.id.item_zs_invocing_tz_num);
            holder.timeTv = (TextView)convertView.findViewById(R.id.item_zs_invocing_tz_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        MitValaddaccountproMTemp item = dataLst.get(position);
        holder.numTv.setText(item.getValpronum());
        holder.timeTv.setText(item.getValprotime().substring(0,10));

        return convertView;
    }

    private class ViewHolder {
        private TextView numTv;
        private TextView timeTv;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        default:
            break;
        }
        
    }
}
