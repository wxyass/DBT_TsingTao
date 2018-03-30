package et.tsingtaopad.dd.ddxt.chatvie;

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
import et.tsingtaopad.dd.ddxt.chatvie.domain.XtChatVieStc;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.domain.ChatVieStc;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtChatVieService extends XtShopVisitService {

    private final String TAG = "XtChatVieService";

    public XtChatVieService(Context context, Handler handler) {
        super(context, handler);
    }

    /***
     * 删除竞品重复拜访产品
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
            where.isNull("productkey");
            where.and();
            where.ne("deleteflag", "1");
            qb.orderBy("productkey", true);
            qb.orderBy("updatetime", false);
            List<MstVistproductInfoTemp> valueLst =qb.query();
            Map<String, MstVistproductInfoTemp> map=new HashMap<String, MstVistproductInfoTemp>();
            if (!CheckUtil.IsEmpty(valueLst)) {
                for (MstVistproductInfoTemp item : valueLst) {
                    String key=item.getCmpproductkey();
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
    public List<XtChatVieStc> queryVieProTemp(String visitId) {

        List<XtChatVieStc> lst = new ArrayList<XtChatVieStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            lst = dao.queryXtVieProByTemp(helper, visitId);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }

        return lst;
    }

}
