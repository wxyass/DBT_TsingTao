package et.tsingtaopad.dd.ddzs.zschatvie;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.dd.ddxt.chatvie.domain.XtChatVieStc;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.listviewintf.IClick;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：InvoicingCheckGoodsAdapter.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-6</br>      
 * 功能描述: 巡店拜访--进销存，核查进销存Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class ZsChatvieAdapter extends
                    BaseAdapter implements OnFocusChangeListener ,OnClickListener {

    private Activity context;
    List<XtChatVieStc> dataLst;
    private int delPosition = -1;

 // 时间控件
 	private String selectDate;
 	private String aday;
 	private Calendar calendar;
 	private int yearr;
 	private int month;
 	private int day;
 	private String dateselect;
 	private String dateselects;
 	private String dateselectx;
    private IClick listener;



    public ZsChatvieAdapter(Activity context, List<XtChatVieStc> dataLst, IClick listener) {
        this.context = context;
        this.dataLst = dataLst;
        this.listener=listener;
        
        
        calendar = Calendar.getInstance();
    	yearr = calendar.get(Calendar.YEAR);
    	month = calendar.get(Calendar.MONTH);
    	day = calendar.get(Calendar.DAY_OF_MONTH);
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
        Log.d("check ", position+"  "+ DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_zdzs_chatvie, null);
            holder.productNameTv = (TextView)convertView.findViewById(R.id.item_zs_chatvie_tv_proname);// 产品名称
            holder.agencyTv = (TextView)convertView.findViewById(R.id.item_zs_chatvie_tv_agencyname);// 经销商名称
            holder.statueTv = (TextView)convertView.findViewById(R.id.item_zs_chatvie_tv_statue);// 未稽查Tv
            holder.statueRl = (RelativeLayout)convertView.findViewById(R.id.item_zs_chatvie_Rl_statue);// 未稽查Rl
            holder.channelPriceEt = (TextView)convertView.findViewById(R.id.item_zs_chatvie_et_prevstore);// 渠道价
            holder.sellPriceEt = (EditText)convertView.findViewById(R.id.item_zs_chatvie_et_daysellnum);// 零售价
            holder.prevNumEt = (EditText)convertView.findViewById(R.id.item_zs_chatvie_et_prevnum);// 订单量->销量
            holder.addcardEt = (EditText)convertView.findViewById(R.id.item_zs_chatvie_et_addcard);// 累计卡->当前库存
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        final XtChatVieStc item = dataLst.get(position);
        // 产品名称
        holder.productNameTv.setHint(item.getProId());
        holder.productNameTv.setText(item.getProName());

        // 未稽查Rl
        holder.statueRl.setTag(position);
        holder.statueRl.setOnClickListener(listener);

        //销量
        if (ConstValues.FLAG_0.equals(item.getMonthSellNum())) {
            holder.prevNumEt.setHint(item.getMonthSellNum());
        } else {
            holder.prevNumEt.setText(item.getMonthSellNum());
        }
        
        //当前库存
        if (ConstValues.FLAG_0.equals(item.getCurrStore())) {
            holder.addcardEt.setHint("0");
        } else if("0.0".equals(item.getCurrStore())){
            holder.addcardEt.setHint("0");
        }else {
            holder.addcardEt.setText(item.getCurrStore());
        }

        // 经销商名称
        holder.agencyTv.setHint("业代未输入经销商名称");
        holder.agencyTv.setText(item.getAgencyName());


        // 渠道价
        if (ConstValues.FLAG_0.equals(item.getChannelPrice())) {
            holder.channelPriceEt.setHint(item.getChannelPrice());
            holder.channelPriceEt.setText(null);
        } else if(!CheckUtil.isBlankOrNull(item.getChannelPrice())){
            holder.channelPriceEt.setText(item.getChannelPrice());
        }else{
            holder.channelPriceEt.setHint(R.string.hit_input);
            holder.channelPriceEt.setText(null);
        }
        holder.channelPriceEt.setTag(position);
        //holder.channelPriceEt.setOnFocusChangeListener(this);

        // 零售价
        if (ConstValues.FLAG_0.equals(item.getSellPrice())) {
            holder.sellPriceEt.setHint(item.getSellPrice());
            holder.sellPriceEt.setText(null);
        } else if(!CheckUtil.isBlankOrNull(item.getSellPrice())){
            holder.sellPriceEt.setText(item.getSellPrice());
        }else{
            holder.sellPriceEt.setHint(R.string.hit_input);
            holder.sellPriceEt.setText(null);
        }
        holder.sellPriceEt.setTag(position);
        
        return convertView;
    }

    private class ViewHolder {
        private TextView productNameTv;
        private TextView agencyTv;
        private TextView statueTv;
        private RelativeLayout statueRl;
        private EditText prevNumEt;
        //private TextView prevNumSumTV;
        private TextView channelPriceEt;
        private EditText sellPriceEt;
        private EditText addcardEt;
        //private Button firstdate;
    }
    
    public void onFocusChange(View v, boolean hasFocus) {
        EditText et = (EditText)v;
        int position = (Integer)v.getTag();
        if (position == delPosition) return;
        if (position > delPosition && delPosition != -1) {
            position = position -1;
        }
        if (position > -1) {
            XtChatVieStc stc = dataLst.get(position);
            String content = et.getText().toString();
            switch (et.getId()) {
            case R.id.checkgoods_et_prevnum:
                //stc.setPrevNum(content);
                break;
                
            case R.id.checkgoods_et_daysell:
                //stc.setDaySellNum(content);
                break;
                
            default:
    
                break;
            }
        }
    }

    public int getDelPosition() {
        return delPosition;
    }

    public void setDelPosition(int delPosition) {
        this.delPosition = delPosition;
    }

	@Override
	public void onClick(View v) {
		final Button dateBtn = (Button) v;
		int position = (Integer) v.getTag();
		final XtChatVieStc stc = dataLst.get(position);
		switch (dateBtn.getId()) {
		// 最早生产时间
		case R.id.checkgoods_et_firstdate:


			break;

		default:

			break;
		}
	}
}