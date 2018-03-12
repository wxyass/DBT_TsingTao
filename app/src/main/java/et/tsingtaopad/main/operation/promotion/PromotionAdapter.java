package et.tsingtaopad.main.operation.promotion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;

/**
 * 功能描述: 促销活动查询Adapter</br>
 */
public class PromotionAdapter extends BaseAdapter {
    
    private Context context;
    private List<String> dataLst;
    private int imgId;

    /**
     * 构造函数
     * 
     * @param context
     * @param dataLst       要显示的数据源
     * @param imgId         达成、未达成图片的资源Id
     */
    public PromotionAdapter(Context context, List<String> dataLst, int imgId) {
        this.context = context;
        this.dataLst = dataLst;
        this.imgId = imgId;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.promotion_lv_item, null);
            holder.numTv = (TextView)convertView.findViewById(R.id.promotion_lv_tv_num);
            holder.termTv = (TextView)convertView.findViewById(R.id.promotion_lv_tv_term);
            holder.flagImg = (ImageView)convertView.findViewById(R.id.promotion_lv_img_flag);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String item = dataLst.get(position);
        holder.numTv.setText(String.valueOf(position + 1));
        holder.termTv.setText(item);
        holder.flagImg.setBackgroundResource(imgId);
        return convertView;
    }

    private class ViewHolder {
        private TextView numTv;
        private TextView termTv;
        private ImageView flagImg;
    }
}
