package et.tsingtaopad.dd.dddaysummary.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.dd.ddweekplan.domain.DayDetailStc;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.listviewintf.IClick;
import et.tsingtaopad.listviewintf.ILongClick;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class BaseDataAdapter extends BaseAdapter implements View.OnClickListener{

    private final String TAG = "DayDetailAdapter";

    private Activity context;
    private List<KvStc> dataLst;
    private IClick listener;

    public BaseDataAdapter(Activity context, List<KvStc> dataLst, IClick listener) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_operation_basedata, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.operation_basedata_title);
            holder.tv_num = (TextView) convertView.findViewById(R.id.operation_basedata_num);
            holder.tv_percent = (TextView) convertView.findViewById(R.id.operation_basedata_percent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final KvStc item = dataLst.get(position);

        // 标题
        holder.tv_title.setText(item.getKey());
        // 真实/追溯
        holder.tv_num.setText(item.getValue());
        // 真实率
        holder.tv_percent.setText(item.getParentKey());

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int posi = (int) v.getTag();
        switch (v.getId()) {

            default:
                break;
        }
    }

    private class ViewHolder {
        private TextView tv_title;
        private TextView tv_num;
        private TextView tv_percent;
    }
}