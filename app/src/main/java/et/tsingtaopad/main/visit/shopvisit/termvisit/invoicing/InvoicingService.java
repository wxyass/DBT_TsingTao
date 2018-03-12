package et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.dao.MstVistproductInfoTempDao;
import et.tsingtaopad.db.table.MstAgencysupplyInfo;
import et.tsingtaopad.db.table.MstAgencysupplyInfoTemp;
import et.tsingtaopad.db.table.MstCheckexerecordInfoTemp;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.MstVistproductInfoTemp;
import et.tsingtaopad.main.visit.shopvisit.termvisit.ShopVisitService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.domain.InvoicingStc;


/**
 * 功能描述: 巡店拜访进销存业务逻辑</br>
 */
public class InvoicingService extends ShopVisitService {
    
    private final String TAG = "InvoicingService";

    public InvoicingService(Context context, Handler handler) {
        super(context, handler);
    }

    /***
     * 删除重复拜访产品（相同产品、供货商只保留一个） 废弃(使用shopvisitservice中的方法)
     * @param //helper
     * @param //visitId
     * @param //isChatViw
     */
    public void delRepeatVistProduct12(String visitkey){
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            QueryBuilder<MstVistproductInfo, String> qb=dao.queryBuilder();
            Where<MstVistproductInfo, String> where=  qb.where();
            where.eq("visitkey", visitkey);
            where.and();
            where.isNotNull("productkey");
            where.and();
            where.ne("deleteflag", "1");
            qb.orderBy("productkey", true);
            qb.orderBy("agencykey", true);
            qb.orderBy("updatetime", false);
            List<MstVistproductInfo> valueLst =qb.query();
            Map<String, MstVistproductInfo> map=new HashMap<String, MstVistproductInfo>();
            if (!CheckUtil.IsEmpty(valueLst)) {
                for (MstVistproductInfo item : valueLst) {
                	String key=item.getProductkey()+item.getAgencykey();
                	if(!map.containsKey(key)){
                		map.put(key, item);
                	}else{
                		dao.delete(item);
                	}
                }
            }
        } catch (SQLException e) {
        	e.printStackTrace();
            Log.e(TAG, "删除重复拜访产品", e);
        }
    }

    /**
     * 获取某次拜访的我品的进销存数据情况
     *
     * @param //helper
     * @param visitId   拜访主键
     * @return
     */
    public List<InvoicingStc> queryMinePro(String visitId, String termKey) {

        List<InvoicingStc> lst = new ArrayList<InvoicingStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            lst = dao.queryMinePro(helper, visitId,termKey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
        return lst;
    }

    /**
     * 从临时表中 获取某次拜访的我品的进销存数据情况
     *
     * @param //helper
     * @param visitId   拜访主键
     * @return
     */
    public List<InvoicingStc> queryMineProFromTemp(String visitId, String termKey) {

        List<InvoicingStc> lst = new ArrayList<InvoicingStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            lst = dao.queryMineProByTemp(helper, visitId,termKey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
        return lst;
    }
    
    /**
     * 保存销存页面数据，MST_VISTPRODUCT_INFO、MST_AGENCYSUPPLY_INFO
     * 
     * @param dataLst       我品进度销存数据
     * @param visitId       拜访主键
     * @param termId        终端主键
     */
    public void saveInvoicing(List<InvoicingStc> dataLst, String visitId, String termId) {
        if (CheckUtil.IsEmpty(dataLst) || CheckUtil.isBlankOrNull(visitId) || CheckUtil.isBlankOrNull(termId)) {
            return;
        }
        
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVistproductInfo, String> proDao = helper.getDao(MstVistproductInfo.class);
            Dao<MstVistproductInfoTemp, String> proTempDao = helper.getDao(MstVistproductInfoTemp.class);
            Dao<MstAgencysupplyInfo, String> supplyDao = helper.getDao(MstAgencysupplyInfo.class);
            Dao<MstAgencysupplyInfoTemp, String> supplyTempDao = helper.getDao(MstAgencysupplyInfoTemp.class);

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            
            // 维护拜访产品-我品记录
            StringBuffer buffer;
            MstVistproductInfoTemp vistproductInfoTemp;
            for(InvoicingStc item : dataLst) {
                // 新增
                buffer = new StringBuffer();
                if (CheckUtil.isBlankOrNull(item.getRecordId())) {
                    vistproductInfoTemp = new MstVistproductInfoTemp();
                    vistproductInfoTemp.setRecordkey(FunUtil.getUUID());
                    vistproductInfoTemp.setVisitkey(visitId);
                    vistproductInfoTemp.setProductkey(item.getProId());
                    vistproductInfoTemp.setAgencykey(item.getAgencyId());
                    vistproductInfoTemp.setPurcprice(Double.valueOf(FunUtil.isNullToZero(item.getChannelPrice())));// 渠道价
                    vistproductInfoTemp.setRetailprice(Double.valueOf(FunUtil.isNullToZero(item.getSellPrice())));// 零售价
                    vistproductInfoTemp.setPurcnum(Double.valueOf(FunUtil.isNullToZero(item.getPrevNum())));// 订单量
                    vistproductInfoTemp.setAddcard(Double.valueOf(FunUtil.isNullToZero(item.getAddcard())));// 累计卡
                    vistproductInfoTemp.setPronum(Double.valueOf(FunUtil.isNullToZero(item.getPrevStore())));
                    vistproductInfoTemp.setCurrnum(Double.valueOf(FunUtil.isNullToZero(item.getCurrStore())));
                    vistproductInfoTemp.setSalenum(Double.valueOf(FunUtil.isNullToZero(item.getDaySellNum())));
                    vistproductInfoTemp.setPadisconsistent(ConstValues.FLAG_0);
                    vistproductInfoTemp.setDeleteflag(ConstValues.FLAG_0);
                    vistproductInfoTemp.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    vistproductInfoTemp.setUpdateuser(PrefUtils.getString(context, "userCode", ""));

                    vistproductInfoTemp.setFristdate(item.getFristdate());
                    proTempDao.create(vistproductInfoTemp);
                    
                // 更新
                } else {
                    buffer.append("update MST_VISTPRODUCT_INFO_TEMP set ");
                    buffer.append("PURCPRICE=?, RETAILPRICE=?, PURCNUM=?, ");
                    buffer.append("PRONUM=?, ADDCARD=?, FRISTDATE=?, CURRNUM=?, SALENUM=?,  ");
                    buffer.append("PADISCONSISTENT='0' ");
                    buffer.append("where RECORDKEY=? ");
                    String[] args = new String[9];
                    args[0] = FunUtil.isNullToZero(item.getChannelPrice());
                    args[1] = FunUtil.isNullToZero(item.getSellPrice());
                    args[2] = FunUtil.isNullToZero(item.getPrevNum());
                    args[3] = FunUtil.isNullToZero(item.getPrevStore());
                    args[4] = FunUtil.isNullToZero(item.getAddcard());// 累计卡
                    args[5] = item.getFristdate();
                    args[6] = FunUtil.isNullToZero(item.getCurrStore());
                    args[7] = FunUtil.isNullToZero(item.getDaySellNum());
                    args[8] = FunUtil.isNullToZero(item.getRecordId());
                    proTempDao.executeRaw(buffer.toString(), args);
                }
            }
            
            // 维护经销商供货关系表
          /*  List<MstAgencysupplyInfo> supplyLst = supplyDao.queryForEq("lowerkey", termId);*/
            //修改经销存界面问货源产品重复的问题
            List<MstAgencysupplyInfoTemp> supplyLst = supplyTempDao.queryBuilder()
                    .where().eq("lowerkey", termId).and().ne("status", "1").query();

            MstAgencysupplyInfoTemp supply;
            for (InvoicingStc pro : dataLst) {
                supply = null;
                for (MstAgencysupplyInfoTemp item : supplyLst) {
                    if (pro.getProId().equals(item.getProductkey())) {
                        supply = item;
                        break;
                    }
                }
                if (supply == null) {
                    supply = new MstAgencysupplyInfoTemp();
                    supply.setAsupplykey(FunUtil.getUUID());
                    supply.setProductkey(pro.getProId());
                    //supply.setCredate(DateUtil.getDateTimeDte(0));
                    supply.setOrderbyno("0");// 0:新增  1:  这个字段用于判断 删除供货关系时,是否是今天新增的
                    supply.setStatus(ConstValues.FLAG_2);// 2新增
                    supply.setLowerkey(termId);
                    supply.setCreuser(PrefUtils.getString(context, "userCode", ""));
                }else{
                    supply.setStatus(ConstValues.FLAG_0);// 0:有效
                }
                supply.setLowertype(ConstValues.FLAG_2);
                supply.setUpperkey(pro.getAgencyId());
                supply.setUppertype(ConstValues.FLAG_1);
                supply.setInprice(pro.getChannelPrice());
                supply.setReprice(pro.getSellPrice());
                supply.setPadisconsistent(ConstValues.FLAG_0);
                supply.setDeleteflag(ConstValues.FLAG_0);
                supply.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                supplyTempDao.createOrUpdate(supply);
            }
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "保存进销存数据发生异常", e);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                Log.e(TAG, "回滚进销存数据发生异常", e1);
            }
        }
    }
    /**
     * 新增供货关系指标记录表进行更新
     *
     * 在这里只做了一个处理, 当有用户在进销存 删除一个供货关系时,又再次添加了这个产品的供货关系,
     * 这个方法对这种情况作了处理,会将这个产品的指标设为空白或不合格(都是-1)
     *
     * @param visitId
     * @param proId
     */
    public void updateMstcheckexerecordInfoTemp(String visitId,String proId){
        // 新增供货关系时，对指标记录表进行更新
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstCheckexerecordInfoTemp, String> tempDao = helper.getDao(MstCheckexerecordInfoTemp.class);

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            QueryBuilder<MstCheckexerecordInfoTemp, String> qb = tempDao.queryBuilder();
            qb.where().eq("visitkey", visitId).and().eq("productkey", proId).and().eq("deleteflag", "1");
            List<MstCheckexerecordInfoTemp> list = qb.query();
            StringBuffer buffer = new StringBuffer();           
            buffer = new StringBuffer();
            //
            if (list != null && list.size()>0)
            {   
                //acresult='-1'如果是产品删除后  再新增产品  状态值就改为-1  请选择(指标状态查询处可体现)
                buffer.append("update mst_checkexerecord_info_temp set deleteflag='0', acresult='-1'");
                //buffer.append("update mst_checkexerecord_info_temp set deleteflag='0' ");
            }else{
                buffer.append("update mst_checkexerecord_info_temp set deleteflag='0' ");
            }
            buffer.append("where visitkey=? and productkey=? ");
            tempDao.executeRaw(buffer.toString(), new String[] {visitId, proId});           
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "新增供货关系时指标记录表数据更新发生异常", e);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                Log.e(TAG, "回滚指标记录表数据更新发生异常", e1);
            }
        }
    }
    
    /***
     * 是否存在产品
     * @param //productKey
     * @return
     */
    public MstVistproductInfo getExistProduct(String visitKey,String productkey){
    	MstVistproductInfo vistproductInfo=null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVistproductInfo, String> proDao = helper.getDao(MstVistproductInfo.class);
            QueryBuilder<MstVistproductInfo, String> qb=proDao.queryBuilder();
            Where<MstVistproductInfo, String> where=qb.where();
            where.eq("visitkey", visitKey);
            where.and();
            where.eq("productkey", productkey);
            where.and();
            where.ne("deleteflag", "1");
            where.and();
            where.isNull("cmpproductkey");
            vistproductInfo=qb.queryForFirst();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return vistproductInfo;
    }
    
    /**
     * 删除经销商与终端的产品供应关系
     * 
     * @param recordKey 拜访产品-竞品我品记录表主键
     * @param termId    终端ID
     * @param visitId   拜访主键
     * @param proId     产品ID
     */
    public boolean deleteSupply(String recordKey, String termId, String visitId, String proId) {
        boolean isFlag=false;
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVistproductInfo, String> proDao = helper.getDao(MstVistproductInfo.class);
            Dao<MstAgencysupplyInfo, String> supplyDao =  helper.getDao(MstAgencysupplyInfo.class);

            Dao<MstAgencysupplyInfoTemp, String> supplyTempDao =  helper.getDao(MstAgencysupplyInfoTemp.class);

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            // 删除拜访产品-竞品我品记录表，相关数据
            StringBuffer buffer = new StringBuffer();
            buffer.append("delete from mst_vistproduct_info_temp ");
            buffer.append("where RECORDKEY=? ");
            proDao.executeRaw(buffer.toString(), new String[] {recordKey});
            
            // 删除经销商供货关系相关数据
            /*buffer = new StringBuffer();
            buffer.append("update MST_AGENCYSUPPLY_INFO set ");
            buffer.append("STATUS ='1',padisconsistent ='0' where LOWERKEY=? and PRODUCTKEY=? ");
            supplyDao.executeRaw(buffer.toString(), new String[] {termId, proId});
            
            // 删除产品相关的指标数据
            buffer = new StringBuffer();
            buffer.append("update mst_checkexerecord_info_temp set deleteflag='1' ");
            buffer.append("where visitkey=? and productkey=? ");
            supplyDao.executeRaw(buffer.toString(), new String[] {visitId, proId});*/
            
            //↓-------------------------------------------------------------------------------------------------------------
            QueryBuilder<MstAgencysupplyInfoTemp, String> qb = supplyTempDao.queryBuilder();
            qb.where().eq("lowerkey", termId).and().eq("productkey", proId).and().eq("orderbyno", "0");//
            List<MstAgencysupplyInfoTemp> list = qb.query();
            
            // 如果是今天新加的供货关系 但今天又删除了,直接物理删除该供货关系记录及临时指标记录 根据orderbyno
            if(list!=null&&list.size()>0){
            	// 删除经销商供货关系相关数据(业代误操作会造成,上传失效的供货关系,所以直接删除该条记录不坐更新 orderbyno的值为当前时间)
                buffer = new StringBuffer();
                buffer.append("delete from MST_AGENCYSUPPLY_INFO_TEMP ");
                buffer.append("where LOWERKEY=? and PRODUCTKEY=? and  orderbyno = '0' ");
                proDao.executeRaw(buffer.toString(), new String[] {termId, proId});
                
                // 删除产品相关的指标数据
                buffer = new StringBuffer();
                buffer.append("delete from  mst_checkexerecord_info_temp  ");
                buffer.append("where visitkey=? and productkey=? ");
                supplyDao.executeRaw(buffer.toString(), new String[] {visitId, proId});
            }else{

                // 删除经销商供货关系相关数据
                buffer = new StringBuffer();
                buffer.append("update MST_AGENCYSUPPLY_INFO_TEMP set ");
                buffer.append("STATUS ='1',padisconsistent ='0' where LOWERKEY=? and PRODUCTKEY=? ");
                supplyDao.executeRaw(buffer.toString(), new String[] {termId, proId});
                
                // 删除产品相关的指标数据
                buffer = new StringBuffer();
                buffer.append("update mst_checkexerecord_info_temp set deleteflag='1' ");
                buffer.append("where visitkey=? and productkey=? ");
                supplyDao.executeRaw(buffer.toString(), new String[] {visitId, proId});
            }
            
            //↑-------------------------------------------------------------------------------------------------------------
            
            
            //↑-------------------------------------------------------------------------------------------------------------
            buffer = new StringBuffer();
            buffer.append("delete from mst_collectionexerecord_info_temp ");
            buffer.append("where visitkey=? and productkey=? ");
            supplyDao.executeRaw(buffer.toString(), new String[] {visitId, proId});


            connection.commit(null);
            isFlag=true;

        } catch (Exception e) {
            isFlag=false;
            DbtLog.logUtils(TAG,"解除供货关系失败");
            DbtLog.logUtils(TAG,e.getMessage());
            e.printStackTrace();
            Log.e(TAG, "保存进销存数据发生异常", e);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                Log.e(TAG, "回滚进销存数据发生异常", e1);
            }
        }
        return isFlag;
    }
    
/*    public void getmonthSum(final String terminalkey){
    			
				// TODO Auto-generated method stub
				HttpUtil httpUtil = new HttpUtil(60*1000);
				httpUtil.configResponseTextCharset("ISO-8859-1");
				StringBuffer buffer =new StringBuffer();
				buffer.append("{terminalkey:'").append(terminalkey).append("'}");
				httpUtil.send("opt_get_sum", buffer.toString(), new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub
						ResponseStructBean resObj = HttpUtil
		                        .parseRes(responseInfo.result);
		                if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
		                    String content = resObj.getResBody().getContent();
		                    
		                    //绑定ListView数据
//		                    List<MonthSum> dataLst = JsonUtil.parseList(content, MonthSum.class);
		                    Message message = new Message();
		                    message.what=ConstValues.WAIT1;
		                    Bundle bundle=new Bundle();
		                    bundle.putString("content", content);
		                    message.setData(bundle);
		                    handler.sendMessage(message);
		                }
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						
					}
				});
			
		
    	
    	
    }*/

    /***
     * 删除重复拜访产品（相同产品、供货商只保留一个）
     * @param //helper
     * @param //visitId
     * @param //isChatViw
     */
    public void delRepeatVistProduct(String visitkey){
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoTempDao dao = helper.getDao(MstVistproductInfoTemp.class);
            QueryBuilder<MstVistproductInfoTemp, String> qb=dao.queryBuilder();
            Where<MstVistproductInfoTemp, String> where=  qb.where();
            where.eq("visitkey", visitkey);
            where.and();
            where.isNotNull("productkey");
            where.and();
            where.ne("deleteflag", "1");
            qb.orderBy("productkey", true);
            qb.orderBy("agencykey", true);
            qb.orderBy("updatetime", false);
            List<MstVistproductInfoTemp> valueLst =qb.query();
            Map<String, MstVistproductInfoTemp> map=new HashMap<String, MstVistproductInfoTemp>();
            if (!CheckUtil.IsEmpty(valueLst)) {
                for (MstVistproductInfoTemp item : valueLst) {
                    String key=item.getProductkey()+item.getAgencykey();
                    if(!map.containsKey(key)){
                        map.put(key, item);
                    }else{
                        dao.delete(item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "删除重复拜访产品", e);
        }
    }
}
