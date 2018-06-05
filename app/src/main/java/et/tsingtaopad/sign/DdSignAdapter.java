package et.tsingtaopad.sign;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.dd.dddealplan.domain.DealStc;
import et.tsingtaopad.listviewintf.IClick;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class DdSignAdapter extends BaseAdapter {

    private Activity context;
    private List<DealStc> dataLst;
    private IClick listener;

    public DdSignAdapter(Activity context, List<DealStc> dataLst, IClick listener) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dd_sign, null);
            holder.termname = (TextView) convertView.findViewById(R.id.item_sign_termname);
            holder.grid = (TextView) convertView.findViewById(R.id.item_sign_grid);
            holder.ydname = (TextView) convertView.findViewById(R.id.item_sign_ydname);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DealStc item = dataLst.get(position);

        holder.termname.setText(item.getTerminalname());
        holder.grid.setText(item.getGridname());
        holder.ydname.setText(item.getUsername());

        return convertView;
    }

    private class ViewHolder {
        private TextView termname;
        private TextView grid;
        private TextView ydname;

    }

}