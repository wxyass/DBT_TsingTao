package et.tsingtaopad.dd.ddxt.sayhi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;

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


}
