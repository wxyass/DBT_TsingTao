package et.tsingtaopad.main.visit.shopvisit.line;

import android.app.Activity;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.main.visit.shopvisit.line.domain.MstRouteMStc;


/**
 * Created by yangwenmin on 2017/12/25.
 * 巡店拜访_线路列表Adapter
 */
public class LineListAdapter extends BaseAdapter implements OnClickListener {
    
    private Activity context;
    private List<MstRouteMStc> dataLst;
    private RelativeLayout confirmBt;
    private int selectItem = -1;
    
    public LineListAdapter(Activity context, 
                List<MstRouteMStc> dataLst, RelativeLayout confirmRl) {
        this.context = context;
        this.dataLst = dataLst;
        this.confirmBt = confirmRl;
    }

    @Override
    public int getCount() {
        
        if (CheckUtil.IsEmpty(this.dataLst)) {
            return 0;
            
        } else {
            return this.dataLst.size();
        }
    }

    @Override
    public Object getItem(int arg0) {
        
        if (CheckUtil.IsEmpty(this.dataLst)) {
            return null;
            
        } else {
            return this.dataLst.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Log.e("message", ">>>>>"+position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.shopvisit_line_lvitem, null);
            holder.lineIdRb = (AppCompatRadioButton)convertView.findViewById(R.id.line_item_rb);
            holder.lineNameTv = (TextView)convertView.findViewById(R.id.line_item_tv_lineName);
            holder.planLabelTv = (TextView)convertView.findViewById(R.id.line_item_tv_plandatelabel);
            holder.planDateTv = (TextView)convertView.findViewById(R.id.line_item_tv_plandate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        MstRouteMStc item = this.dataLst.get(position);
        holder.lineIdRb.setOnClickListener(this);
        holder.lineIdRb.setTag(position);
        holder.lineNameTv.setHint(item.getRoutecode());
        holder.lineNameTv.setText(item.getRoutename());
        holder.planDateTv.setText(item.getPlanDate());
        
        if (position == selectItem) {
            holder.lineIdRb.setChecked(true);
            holder.lineNameTv.setTextColor(context.getResources().getColor(R.color.font_color_green));
            holder.planLabelTv.setTextColor(context.getResources().getColor(R.color.font_color_green));
            holder.planDateTv.setTextColor(context.getResources().getColor(R.color.font_color_green));
        } else {
            holder.lineIdRb.setChecked(false);
            holder.lineNameTv.setTextColor(context.getResources().getColor(R.color.listview_item_font_color));
            holder.planLabelTv.setTextColor(context.getResources().getColor(R.color.listview_item_font_color));
            holder.planDateTv.setTextColor(context.getResources().getColor(R.color.listview_item_font_color));
        }
        return convertView;
    }

    private class ViewHolder {
        private AppCompatRadioButton lineIdRb;
        private TextView lineNameTv;
        private TextView planLabelTv;
        private TextView planDateTv;
    }

    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        setSelectItem(position);
        notifyDataSetChanged();
        confirmBt.setVisibility(View.VISIBLE);
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
}
