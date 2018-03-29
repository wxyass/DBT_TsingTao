package et.tsingtaopad.dd.ddxt.sayhi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.line.domain.MstRouteMStc;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtSayhiService extends XtShopVisitService {

    private final String TAG = "XtSayhiService";

    public XtSayhiService(Context context, Handler handler) {
        super(context, handler);
    }

    // 根据省市县编码 获取省市县名称
    public String getAreaName(String areacode) {

        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String querySql = "SELECT areaname  FROM CMM_AREA_M WHERE areacode = ?";
        Cursor cursor = db.rawQuery(querySql, new String[]{areacode});
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex("areaname");
        String areaname;
        try {
            areaname = cursor.getString(cursor.getColumnIndex("areaname"));
        } catch (Exception e) {
            areaname = "";
        }
        return areaname;
    }


    // 根据渠道编码,获取渠道名称
    public String getDatadicName(String diccode) {

        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String querySql = "SELECT dicname  FROM CMM_DATADIC_M WHERE diccode = ?";
        Cursor cursor = db.rawQuery(querySql, new String[]{diccode});
        cursor.moveToFirst();
        //int columnIndex = cursor.getColumnIndex("dicname");
        String areaname;
        try {
            areaname = cursor.getString(cursor.getColumnIndex("dicname"));
        } catch (Exception e) {
            areaname = "";
        }
        return areaname;
    }

    // 根据路线key,获取路线名称
    public String getRouteName(String diccode) {

        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String querySql = "SELECT routename  FROM MST_ROUTE_M WHERE routekey = ?";
        Cursor cursor = db.rawQuery(querySql, new String[]{diccode});
        cursor.moveToFirst();
        //int columnIndex = cursor.getColumnIndex("dicname");
        String areaname;
        try {
            areaname = cursor.getString(cursor.getColumnIndex("routename"));
        } catch (Exception e) {
            areaname = "";
        }
        return areaname;
    }

    /**
     * 获取并初始化拜访线路
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MstRouteM> initMstRoute() {

        List<MstRouteM> mstRouteList = new ArrayList<MstRouteM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstRouteM, String> mstRouteMDao = helper.getMstRouteMDao();
            QueryBuilder<MstRouteM, String> qBuilder = mstRouteMDao.queryBuilder();
            Where<MstRouteM, String> where = qBuilder.where();
            where.and(where.or(where.isNull("deleteflag"), where.eq("deleteflag", "0")),
                    where.or(where.isNull("routestatus"), where.eq("routestatus", "0"))
            );
            qBuilder.orderBy("orderbyno", true).orderBy("routename", true);
            mstRouteList = qBuilder.query();
        } catch (Exception e) {
            Log.e(TAG, "获取线路信息失败", e);
        }

        return mstRouteList;
    }

    /**
     * 初始化终端等级
     */
    public List<KvStc> initDataDicByTermLevel() {
        List<CmmDatadicM> dataDicLst = initDataDictionary();
        // 获取数据字典表中区域字典对应的父ID
        String termLevel = PropertiesUtil.getProperties("datadic_termLevel");

        List<KvStc> kvLst = new ArrayList<KvStc>();
        for (CmmDatadicM item : dataDicLst) {
            if (termLevel.equals(item.getParentcode())) {
                kvLst.add(new KvStc(item.getDiccode(),
                        item.getDicname(), item.getParentcode()));
            } else if (kvLst.size() > 0) {
                break;
            }
        }
        return kvLst;
    }

    /**
     * 获取数据字典信息
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<CmmDatadicM> initDataDictionary() {
        List<CmmDatadicM> dataDicLst = new ArrayList<CmmDatadicM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            QueryBuilder<CmmDatadicM, String> qBuilder =
                    helper.getCmmDatadicMDao().queryBuilder();
            Where<CmmDatadicM, String> where = qBuilder.where();
            where.or(where.isNull("deleteflag"), where.eq("deleteflag", "0"));
            qBuilder.orderBy("parentcode", true).orderBy("orderbyno", true);
            dataDicLst = qBuilder.query();
        } catch (SQLException e) {
            Log.e(TAG, "初始化数据字典失败", e);
        }

        return dataDicLst;
    }

    /**
     * 初始化终端区域类型
     */
    public List<KvStc> initDataDicByAreaType() {
        List<CmmDatadicM> dataDicLst = initDataDictionary();

        // 获取数据字典表中区域字典对应的父ID
        String areaType=PropertiesUtil.getProperties("datadic_areaType");

        List<KvStc> kvLst = new ArrayList<KvStc>();
        for (CmmDatadicM item : dataDicLst) {
            if (areaType.equals(item.getParentcode())) {
                kvLst.add(new KvStc(item.getDiccode()
                        , item.getDicname(), item.getParentcode()));
            } else if (kvLst.size() > 0) {
                break;
            }
        }
        return kvLst;
    }


}
