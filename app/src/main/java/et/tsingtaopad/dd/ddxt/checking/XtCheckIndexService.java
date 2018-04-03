package et.tsingtaopad.dd.ddxt.checking;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.dao.MstGroupproductMDao;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexQuicklyStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtCheckIndexService extends XtShopVisitService {
    
    private final String TAG = "XtCheckIndexService";
    
    public XtCheckIndexService(Context context, Handler handler) {
        super(context, handler);
    }

    /**
     * 初始化指标、指标值树级关系 对象
     */
    public void initCheckTypeStatus() {
        List<KvStc> checkTypeStatusList=new ArrayList<KvStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            PadChecktypeMDao dao = helper.getDao(PadChecktypeM.class);
            checkTypeStatusList= dao.queryCheckTypeStatus(helper, ConstValues.FLAG_0);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访指标失败", e);
            e.printStackTrace();
        }
        GlobalValues.indexLst = checkTypeStatusList;
    }




    /**
     * 自动计算各指标的指标值
     *
     * @param channelId         终端次渠道ID
     * @param proItemLst        各产品对应的采集项对应的现有量及变化量
     * @param calculateLst      分项采集显示的数据源
     * @param productKey        要计算的产品Id,null时计算所有
     * @param currIndexId       当前指标主键
     */
    public void calculateIndex(String channelId,
                               List<XtProItem> proItemLst, List<XtProIndex> calculateLst, String productKey,String currIndexId) {
        //当前指标在数据库里是否存在
        boolean isCurrIndexExsist=false;

        //String proItemLst1 = JSON.toJSONString(proItemLst);
        //String calculateLst1 = JSON.toJSONString(calculateLst);

        // 获取要自动计算的指标的主键的集合
        List<String> calIndexIdLst = new ArrayList<String>();
        if (!CheckUtil.IsEmpty(calculateLst)) {
            for (XtProIndex item : calculateLst) {
                if (ConstValues.FLAG_0.equals(item.getIndexType()) || ConstValues.FLAG_1.equals(item.getIndexType())
                        || ConstValues.FLAG_4.equals(item.getIndexType())) {
                    calIndexIdLst.add(item.getIndexId());
                }
            }
        }

        // 如果无需要计算的则返回
        if (CheckUtil.IsEmpty(calIndexIdLst)) {
            return;
        }

        // 拼接SQL
        String key = "";
        StringBuffer buffer = new StringBuffer();
        Map<String, String> sqlMap = new HashMap<String, String>();
        //Map<String, List<String>> checkItemMap = new HashMap<String, List<String>>();
        Map<String, Map<String, List<String>>> checkcheckItemMap = new HashMap<String, Map<String, List<String>>>();// 指标集合
        DecimalFormat df = new DecimalFormat("0");
        if (!(CheckUtil.IsEmpty(proItemLst) || CheckUtil.IsEmpty(calIndexIdLst))) {
            for (XtProItem proItem : proItemLst) {
                // 如果计算单个产品且，产品不相等时continue
                if ("-1".equals(productKey) || productKey.equals(proItem.getProId())) {// ProId:产品主键
                    for (String indexId : proItem.getIndexIdLst()) {
                        if (calIndexIdLst.contains(indexId)) {

                            // 拼SQL // key: ad3030fb-e42e-47f8-a3ec-4229089aab5d,1-9065(指标主键,产品主键)
                            key = indexId + "," + proItem.getProId();
                            if (sqlMap.containsKey(key)) {
                                buffer = new StringBuffer(sqlMap.get(key));
                                buffer.append(" or ");
                            } else {
                                buffer = new StringBuffer();
                            }
                            buffer.append(" (cc.colitemkey='").append(proItem.getItemId());
                            Double cnum = FunUtil.isNullToZero(proItem.getChangeNum());
                            Double fnum = FunUtil.isNullToZero(proItem.getFinalNum());
                            /*buffer.append("' and coalesce(cc.addcount, 0) <= ").append(proItem.getChangeNum().intValue());
                            buffer.append(" and coalesce(cc.totalcount, 0) <= ").append(
                                    (proItem.getChangeNum().intValue() + proItem.getFinalNum().intValue())).append(") ");*/
                            buffer.append("' and coalesce(cc.addcount, 0) <= ").append(cnum.intValue());
                            buffer.append(" and coalesce(cc.totalcount, 0) <= ").append(
                                    (cnum.intValue()+fnum.intValue())).append(") ");


                            sqlMap.put(key, buffer.toString());


                            // 计算每个指标需要采集的采集几个数据
                            /*if (!checkItemMap.containsKey(indexId)) {
                            	// (指标,集合(堆头,库存啥的))
                                checkItemMap.put(indexId, new ArrayList<String>());
                            }
                            if (!checkItemMap.get(indexId).contains(proItem.getItemId())) {
                                checkItemMap.get(indexId).add(proItem.getItemId());
                            }*/

                            // 每个产品有几个采集项  indexId:指标主键(ad3030fb-e42e-47f8-a3ec-4229089aab5d)
                            if (!checkcheckItemMap.containsKey(indexId)) {// 判断集合中是否有该产品对应的指标,当没有该指标时
                                Map<String, List<String>> procheckItemMap = new HashMap<String, List<String>>();// 产品采集项集合
                                ArrayList<String> proItemIds = new ArrayList<String>();
                                proItemIds.add(proItem.getItemId());// ItemId: 每个采集项的id(比如:101,102)
                                procheckItemMap.put(proItem.getProId(), proItemIds);
                                checkcheckItemMap.put(indexId, procheckItemMap);
                            } else {//当有该指标时
                                Iterator iter = checkcheckItemMap.entrySet().iterator();
                                while (iter.hasNext()) {
                                    Map.Entry entry = (Map.Entry) iter.next();
                                    // 指标id
                                    String checkId = (String) entry.getKey();
                                    if(indexId.equals(checkId)){
                                        Map<String, List<String>> val = (Map<String, List<String>>) entry.getValue();
                                        if(val.keySet().contains(proItem.getProId())){
                                            checkcheckItemMap.get(indexId).get(proItem.getProId()).add(proItem.getItemId());
                                        }else{
                                            ArrayList<String> proItemIds = new ArrayList<String>();
                                            proItemIds.add(proItem.getItemId());
                                            checkcheckItemMap.get(indexId).put(proItem.getProId(), proItemIds);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //String checkcheckItemMap1 = JSON.toJSONString(checkcheckItemMap);

        buffer = new StringBuffer();
        String[] keys = null;
        buffer.append("select cc.checkkey, cc.productkey, cc.cstatuskey, max(cs.cstatusname) cstatusname, count(cc.colitemkey) ciNum, max(cc.orderbyno) orderbyno ");
        buffer.append("from pad_checkaccomplish_info cc, pad_checkstatus_info cs ");
        buffer.append("where cc.cstatuskey = cs.cstatuskey and cc.minorchannel like '%").append(channelId).append("%' and ( 1 != 1 ");
        for (String keyItem : sqlMap.keySet()) {
            keys = keyItem.split(",");
            buffer.append(" or (cc.checkkey = '").append(keys[0]).append("' and cc.productkey = '").append(keys[1]).append("' ");
            buffer.append(" and (").append(sqlMap.get(keyItem)).append(")) ");
        }
        buffer.append(") group by cc.checkkey, cc.productkey, cc.cstatuskey having ( 1 != 1 ");
        /*for (String itemNum : checkItemMap.keySet()) {
            buffer.append(" or (cc.checkkey ='").append(itemNum).append("' and ciNum = ").append(checkItemMap.get(itemNum).size()).append(") ");
        }*/
        for (String itemNum : checkcheckItemMap.keySet()) {
            for (String prokey : checkcheckItemMap.get(itemNum).keySet()) {
                buffer.append(" or (cc.checkkey ='").append(itemNum).append("' and ciNum = ").append(checkcheckItemMap.get(itemNum).get(prokey).size()).append(") ");
            }
        }
        buffer.append(") order by orderbyno desc ");

        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), null);
        String indexId = "";
        String proId = "";
        String valueId = "";
        String valueName = "";
        boolean findFlag = false;
        List<String> indexProIdLst = new ArrayList<String>();

        while (cursor.moveToNext()) {
            indexId = cursor.getString(cursor.getColumnIndex("checkkey"));
            proId = cursor.getString(cursor.getColumnIndex("productkey"));
            valueId = cursor.getString(cursor.getColumnIndex("cstatuskey"));
            valueName = cursor.getString(cursor.getColumnIndex("cstatusname"));

            if ("-1".equals(productKey) || productKey.equals(proId)) {
                if (indexProIdLst.contains(indexId + proId)) continue;
                for (XtProIndex item : calculateLst) {
                    findFlag = false;
                    if (indexId.equals(item.getIndexId())) {
                        for (XtProIndexValue proItem : item.getIndexValueLst()) {
                            if (proId.equals(proItem.getProId())) {
                                //判断修改指标值否是当前修改的指标
                                if(!"-1".equals(currIndexId) && proItem.getIndexId().equals(currIndexId)){
                                    isCurrIndexExsist=true;
                                }
                                proItem.setIndexValueId(valueId);
                                proItem.setIndexValueName(valueName);
                                findFlag = true;
                                indexProIdLst.add(indexId + proId);
                                break;
                            }
                        }
                        if (findFlag) break;
                    }
                }
            }
        }





        //针对铺货状态中没有空白和有效销售
        /*for(ProIndex item : calculateLst){
            for(ProIndexValue proItem : item.getIndexValueLst()){
                String data  = item.getIndexId()+proItem.getProId();
                if("-1".equals(productKey) ){
                    if(!indexProIdLst.contains(data)){
                        proItem.setIndexValueId(null);
                        proItem.setIndexValueName(null);
                    }
                }else{
                    //当前指标状态在数据里不存在，值设置为空
                    if(data.equals(currIndexId+productKey) && !isCurrIndexExsist){
                        proItem.setIndexValueId(null);
                        proItem.setIndexValueName(null);
                    }
                }
            }

        }*/

        //针对铺货状态中没有空白和有效销售  冰冻化没有不合格
        for(XtProIndex item : calculateLst){
            for(XtProIndexValue proItem : item.getIndexValueLst()){
                String data  = item.getIndexId()+proItem.getProId();
                if("-1".equals(productKey) ){
                    if(!indexProIdLst.contains(data)){
                        /*proItem.setIndexValueId(null);
                        proItem.setIndexValueName(null);*/
                        setIndexValue(proItem, item.getIndexId());
                    }
                }else{
                    //当前指标状态在数据里不存在，值设置为空
                    if(data.equals(currIndexId+productKey) && !isCurrIndexExsist){
                        /*proItem.setIndexValueId(null);
                        proItem.setIndexValueName(null); */
                        setIndexValue(proItem, currIndexId);
                    }
                }
            }
        }
    }

    //
    public void setIndexValue(XtProIndexValue proItem, String currIndexId) {
        if ("ad3030fb-e42e-47f8-a3ec-4229089aab5d".equals(currIndexId)) {// 铺货状态
            proItem.setIndexValueId("301");
            proItem.setIndexValueName("空白");
        } else if ("ad3030fb-e42e-47f8-a3ec-4229089aab6d".equals(currIndexId)) {// 道具生动化
            proItem.setIndexValueId("307");
            proItem.setIndexValueName("不合格");
        } else if ("ad3030fb-e42e-47f8-a3ec-4229089aab7d".equals(currIndexId)) {// 产品生动化
            proItem.setIndexValueId("309");
            proItem.setIndexValueName("不合格");
        } else if ("ad3030fb-e42e-47f8-a3ec-4229089aab8d".equals(currIndexId)) {// 冰冻化
            proItem.setIndexValueId("311");
            proItem.setIndexValueName("不合格");
        }
    }

}
