package et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.listviewintf.IClick;
import et.tsingtaopad.view.SlideSwitch;


/**
 * 功能描述: 巡店拜访-查指标-促销活动Adapter</br>
 */
public class PromotionAdapter extends BaseAdapter {
    
    private Activity context;
    private List<CheckIndexPromotionStc> dataLst;
    private int countonoff = 0;
    private String visitDate;
    private String isbigarea;
    private String seeFlag;
    private String visitId;
    private String termId;
    private String twoareaid;
    private CheckIndexService service;
    //Button activityPicBt;
    List<CameraInfoStc> valueLst;
    private IClick mListener;
    
    public PromotionAdapter(Activity context, List<CheckIndexPromotionStc> dataLst, String visitDate,
							List<CameraInfoStc> valueLst, String isbigarea, String seeFlag,CheckIndexService service,
							String visitId, String termId,IClick listener) {
        this.context = context;
        this.dataLst = dataLst;
        this.visitDate = visitDate;
        //this.activityPicBt = activityPicBt;
        this.valueLst = valueLst;
        this.isbigarea = isbigarea;
        this.seeFlag = seeFlag;
        this.twoareaid = PrefUtils.getString(context, "disId", "");
        this.mListener = listener;
        this.service = service;
        this.visitId = visitId;
        this.termId = termId;
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
        Log.d("promotion ", position+"  "+ DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_promotion_lvitem, null);
            holder.promotionNameTv = (TextView)convertView.findViewById(R.id.promotion_tv_name);
            holder.startDateTv = (TextView)convertView.findViewById(R.id.promotion_tv_startdate);
            holder.endDateTv = (TextView)convertView.findViewById(R.id.promotion_tv_enddate);
            holder.proNameTv = (TextView)convertView.findViewById(R.id.promotion_tv_proname);
            holder.statusSw = (SlideSwitch)convertView.findViewById(R.id.promotion_sw_status);
            holder.reachnum = (EditText)convertView.findViewById(R.id.promotion_et_reachnum);
            holder.camera = (TextView)convertView.findViewById(R.id.promotion_btn_camera);
            
            holder.statusSw.setOnSwitchChangedListener(new MySwitchChangedListener(holder) {
    			
    			@Override
    			public void afterTextChanged(SlideSwitch obj, int status, ViewHolder holder) {
    				if(status==SlideSwitch.SWITCH_ON){
    					holder.reachnum.setVisibility(View.VISIBLE);// 达成组数
    					dataLst.get(position).setIsAccomplish(ConstValues.FLAG_1);// 活动设为达成
						// 修改数据库,因为调用相机,会导致软件触发hide方法,会重走数据读取,修改数据库后,就可以读最新的数据了
						CheckIndexPromotionStc checkIndexPromotionStc = dataLst.get(position);
						service.saveOnePromotion(visitId,termId,checkIndexPromotionStc);

    					if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
    						// 根据是否达成 拍照按钮显示
        			        if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
        			        	holder.camera.setVisibility(View.VISIBLE);
        			        	holder.camera.setEnabled(true);
        			        }else{
        			        	//holder.camera.setVisibility(View.INVISIBLE);
        			        	holder.camera.setVisibility(View.GONE);
        			        	holder.camera.setEnabled(false);
        			        }
    					} else {
    						// 根据是否达成 拍照按钮显示
        			        if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
        			        	holder.camera.setVisibility(View.VISIBLE);
        			        	holder.camera.setEnabled(true);
        			        }else{
        			        	//holder.camera.setVisibility(View.INVISIBLE);
        			        	holder.camera.setVisibility(View.GONE);
        			        	holder.camera.setEnabled(false);
        			        }
    					}
    					
    			        
    	            }else{
    	            	holder.reachnum.setVisibility(View.INVISIBLE);// 达成组数
    	            	holder.reachnum.setText(null);// 达成组数
    	            	dataLst.get(position).setIsAccomplish(ConstValues.FLAG_0);// 活动设为未达成
    	            	
    	            	if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
    	            		if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
        			        	//holder.camera.setVisibility(View.VISIBLE);
    	            			//holder.camera.setVisibility(View.INVISIBLE);
    	            			holder.camera.setVisibility(View.GONE);
        			        	holder.camera.setEnabled(false);
        			        }else{
        			        	//holder.camera.setVisibility(View.INVISIBLE);
        			        	holder.camera.setVisibility(View.GONE);
        			        	holder.camera.setEnabled(false);
        			        }
    	            	} else {// 大区
    	            		// 根据是否达成 拍照按钮显示
        			        if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
        			        	//holder.camera.setVisibility(View.VISIBLE);
        			        	//holder.camera.setVisibility(View.INVISIBLE);
        			        	holder.camera.setVisibility(View.GONE);
        			        	holder.camera.setEnabled(false);
        			        }else{
        			        	//holder.camera.setVisibility(View.INVISIBLE);
        			        	holder.camera.setVisibility(View.GONE);
        			        	holder.camera.setEnabled(false);
        			        }
    	            	}
    	            }
    				
    			}
    		});
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        holder.statusSw.setTag(position);
        
        CheckIndexPromotionStc item = dataLst.get(position);
        holder.promotionNameTv.setHint(item.getPromotKey());
        holder.promotionNameTv.setText(item.getPromotName());
        holder.startDateTv.setText(item.getStartDate());
        holder.endDateTv.setText(item.getEndDate());
        holder.proNameTv.setHint(item.getProId());
        holder.proNameTv.setText(item.getProName());
        
		// 对促销活动按钮 初始化
		if (ConstValues.FLAG_1.equals(item.getIsAccomplish())) {
			holder.statusSw.setStatus(true);
			holder.reachnum.setVisibility(View.VISIBLE);
			holder.reachnum.setText(item.getReachNum());

			if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
				// 根据是否达成 拍照按钮显示
				if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
					holder.camera.setVisibility(View.VISIBLE);
					holder.camera.setEnabled(true);
				}else{
					//holder.camera.setVisibility(View.INVISIBLE);
					holder.camera.setVisibility(View.GONE);
					holder.camera.setEnabled(false);
				}
			} else {// 大区配置
				// 根据是否达成 拍照按钮显示
				if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
					holder.camera.setVisibility(View.VISIBLE);
					holder.camera.setEnabled(true);
				}else{
					//holder.camera.setVisibility(View.INVISIBLE);
					holder.camera.setVisibility(View.GONE);
					holder.camera.setEnabled(false);
				}
			}

		} else {
			holder.statusSw.setStatus(false);
			holder.reachnum.setVisibility(View.INVISIBLE);
			holder.reachnum.setText(null);

			if("1".equals(isbigarea)){// 二级配置  0:大区  1:二级区域
				if("1".equals(dataLst.get(position).getIspictype())&&twoareaid.equals(dataLst.get(position).getAreaid())){
					//holder.camera.setVisibility(View.VISIBLE);
					//holder.camera.setVisibility(View.INVISIBLE);
					holder.camera.setVisibility(View.GONE);
					holder.camera.setEnabled(false);
				}else{
					//holder.camera.setVisibility(View.INVISIBLE);
					holder.camera.setVisibility(View.GONE);
					holder.camera.setEnabled(false);
				}
			} else {// 大区
				// 根据是否达成 拍照按钮显示
				if("1".equals(dataLst.get(position).getIspictype())&&(!twoareaid.equals(dataLst.get(position).getAreaid()))){
					//holder.camera.setVisibility(View.VISIBLE);
					//holder.camera.setVisibility(View.INVISIBLE);
					holder.camera.setVisibility(View.GONE);
					holder.camera.setEnabled(false);
				}else{
					//holder.camera.setVisibility(View.INVISIBLE);
					holder.camera.setVisibility(View.GONE);
					holder.camera.setEnabled(false);
				}
			}
		}
        
        //
        holder.camera.setOnClickListener(mListener);
        holder.camera.setTag(position);
        
        return convertView;
    }

    private class ViewHolder {
        private TextView promotionNameTv;
        private TextView startDateTv;
        private TextView endDateTv;
        private TextView proNameTv;
        private SlideSwitch statusSw;
        private EditText reachnum;
        public TextView camera;
    }
    
    abstract class MySwitchChangedListener implements SlideSwitch.OnSwitchChangedListener{

    	private ViewHolder mHolder;
		
		public MySwitchChangedListener(ViewHolder holder) {
			mHolder = holder;
		}
		@Override
		public void onSwitchChanged(SlideSwitch obj, int status) {
			 afterTextChanged(obj, status,mHolder);
		}
		public abstract void afterTextChanged(SlideSwitch obj, int status, ViewHolder holder);
    }

}
