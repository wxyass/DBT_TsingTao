package et.tsingtaopad.dd.ddxt.invoicing;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.dao.MstVistproductInfoTempDao;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.MstVistproductInfoTemp;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.domain.InvoicingStc;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtInvoicingService extends XtShopVisitService {

    private final String TAG = "XtInvoicingService";

    public XtInvoicingService(Context context, Handler handler) {
        super(context, handler);
    }

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

    /**
     * 从临时表中 获取某次拜访的我品的进销存数据情况
     *
     * @param //helper
     * @param visitId   拜访主键
     * @return
     */
    public List<XtInvoicingStc> queryMineProFromTemp(String visitId, String termKey) {

        List<XtInvoicingStc> lst = new ArrayList<XtInvoicingStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            lst = dao.queryXtMineProByTemp(helper, visitId,termKey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
        return lst;
    }

}
