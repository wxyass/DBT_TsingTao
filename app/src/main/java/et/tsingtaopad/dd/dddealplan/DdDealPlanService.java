package et.tsingtaopad.dd.dddealplan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitRepairterMDao;
import et.tsingtaopad.db.dao.MitValterMDao;
import et.tsingtaopad.db.dao.MitVisitMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.table.MitRepairM;
import et.tsingtaopad.db.table.MitRepaircheckM;
import et.tsingtaopad.db.table.MitRepairterM;
import et.tsingtaopad.db.table.MitValcheckterM;
import et.tsingtaopad.db.table.MitValterM;
import et.tsingtaopad.db.table.MitVisitM;
import et.tsingtaopad.db.table.MstCheckexerecordInfoTemp;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMCart;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.dd.dddealplan.domain.DealStc;
import et.tsingtaopad.dd.dddealplan.make.domain.DealPlanMakeStc;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;


/**
 * 项目名称：营销移动智能工作平台 </br>
 */
@SuppressLint("DefaultLocale")
public class DdDealPlanService {

    private final String TAG = "DdDealPlanService";

    private Context context;

    public DdDealPlanService(Context context) {
        this.context = context;
    }

    public List<DealStc> getDealPlanTerminal() {
        AndroidDatabaseConnection connection = null;
        List<DealStc> valueLst = null;
        List<DealStc> termNameLst = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MitRepairterMDao valueDao = helper.getDao(MitRepairterM.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            valueLst = valueDao.queryDealPan(helper);//

            for (DealStc dealStc : valueLst){
                termNameLst = valueDao.queryDealPlanTermName(helper,dealStc.getRepairid());//
                dealStc.setTerminalname(listToString(termNameLst));
                dealStc.setRoutename(listToRouteString(termNameLst));
                dealStc.setIsshow("0");// 0不展开  1展开
            }

            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "获取整顿计划选择终端出错3", e);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return valueLst;
    }

    /**
     * 将集合 转成字符串以逗号隔开
     *
     * @param list
     * @return
     */
    public static String listToString(List<DealStc> list) {
        StringBuffer listToString = new StringBuffer();
        if (!list.isEmpty()) {
            /* 输出list值 */
            for (int i = 0; i < list.size(); i++) {
                listToString.append(FunUtil.isBlankOrNullTo(list.get(i).getTerminalname(),""));
                if (i != list.size() - 1) {
                    listToString.append(",");
                }
            }
        }
        return listToString.toString();
    }

    /**
     * 取出路线
     *
     * @param list
     * @return
     */
    public static String listToRouteString(List<DealStc> list) {
        String listToString = "";
        //StringBuffer listToString = new StringBuffer();
        if (!list.isEmpty()) {
            /* 输出list值 */
            for (int i = 0; i < list.size(); i++) {
                if(!listToString.contains(list.get(i).getRoutename())){
                    listToString += list.get(i).getRoutename();
                    listToString += ",";
                }
            }
        }

        if (listToString.endsWith(",")) {
            listToString = listToString.substring(0,listToString.length() - 1);
        }
        return listToString;
    }



    // 设置整改计划审核表
    public void setStatus(String repairid,String repaircheckid, String status) {

        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MitRepaircheckM, String> indexValueDao = helper.getMitRepaircheckMDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            StringBuffer buffer = new StringBuffer();
            buffer.append("update MIT_REPAIRCHECK_M set status = "+status+" ,uploadflag = 1  , padisconsistent = 0 where id = '"+repaircheckid+"'   ");
            indexValueDao.executeRaw(buffer.toString());

            buffer = new StringBuffer();
            buffer.append("update MIT_REPAIR_M set status = "+status+" ,uploadflag = 1  , padisconsistent = 0 where id = '"+repairid+"'   ");
            indexValueDao.executeRaw(buffer.toString());

            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "设置整改计划审核表", e);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
