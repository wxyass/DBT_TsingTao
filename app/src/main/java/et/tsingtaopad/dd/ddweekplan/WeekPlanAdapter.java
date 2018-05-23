package et.tsingtaopad.dd.ddweekplan;

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
import et.tsingtaopad.dd.ddweekplan.domain.DayPlanStc;
import et.tsingtaopad.listviewintf.IClick;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class WeekPlanAdapter extends BaseAdapter {

    private Activity context;
    private List<DayPlanStc> dataLst;
    private IClick listener;

    public WeekPlanAdapter(Activity context, List<DayPlanStc> dataLst, IClick listener) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weekplan_dayitem, null);
            holder.ll_all = (LinearLayout) convertView.findViewById(R.id.item_weekplan_ll_all);
            holder.weekday = (TextView) convertView.findViewById(R.id.item_weekplan_tv_weekday);
            holder.time = (TextView) convertView.findViewById(R.id.item_weekplan_tv_time);
            holder.statueRl = (RelativeLayout) convertView.findViewById(R.id.item_weekplan_rl_statue);
            holder.statueTv = (TextView) convertView.findViewById(R.id.item_weekplan_tv_statue);
            holder.tv_area = (TextView) convertView.findViewById(R.id.item_weekplan_tv_area);
            holder.tv_route = (TextView) convertView.findViewById(R.id.item_weekplan_tv_route);
            holder.tv_check = (TextView) convertView.findViewById(R.id.item_weekplan_tv_check);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DayPlanStc item = dataLst.get(position);

        holder.ll_all.setTag(position);
        holder.ll_all.setOnClickListener(listener);

        // 周几
        holder.statueTv.setHint(item.getState());
        holder.statueTv.setText(item.getState());

        // 未指定
        holder.weekday.setHint(item.getWeekday());
        holder.weekday.setText(item.getWeekday());

        return convertView;
    }

    private class ViewHolder {
        private LinearLayout ll_all;
        private TextView weekday;
        private TextView time;
        private RelativeLayout statueRl;
        private TextView statueTv;
        private TextView tv_area;
        private TextView tv_route;
        private TextView tv_check;
    }

}