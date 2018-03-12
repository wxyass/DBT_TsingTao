package et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;


/**
 * 功能描述: 分项采集列表中计算弹出框中列表的Adapter</br>
 */
public class CalculateIndexItemAdapter extends BaseAdapter {
    
    private Context context;
    private List<ProItem> dataLst;
    
    public CalculateIndexItemAdapter(Context context, List<ProItem> dataLst) {
        this.context = context;
        this.dataLst = dataLst;
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_calculatedialog_lvitem, null);
            holder.itemNameTv = (TextView)convertView.findViewById(R.id.calculatedialog_tv_itemname);
            holder.changeNumEt = (EditText)convertView.findViewById(R.id.calculatedialog_et_changenum);
            holder.finalNumEt = (EditText)convertView.findViewById(R.id.calculatedialog_et_finalnum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        ProItem item = dataLst.get(position);
        holder.itemNameTv.setTag(item.getItemId());
        holder.itemNameTv.setText(item.getItemName());
        DecimalFormat df = new DecimalFormat("0");
        // 变化量
        String changeNum = "";
        if (item.getChangeNum() != null) {
            changeNum = df.format(item.getChangeNum());
        }
        if ("0".equals(changeNum)) {
            holder.changeNumEt.setHint(null);
            holder.changeNumEt.setText("0");
        } else {
            holder.changeNumEt.setText(changeNum);
        }
        // 现有量
        String finalNum = "";
        if (item.getFinalNum() != null) {
            finalNum = df.format(item.getFinalNum());
        }
        if ("0".equals(finalNum)) {
            holder.finalNumEt.setHint(null);
            holder.finalNumEt.setText("0");
        } else {
            holder.finalNumEt.setText(finalNum);
        }
        
        return convertView;
    }

    private class ViewHolder {
        private TextView itemNameTv;
        private EditText changeNumEt;
        private EditText finalNumEt;
    }
}
