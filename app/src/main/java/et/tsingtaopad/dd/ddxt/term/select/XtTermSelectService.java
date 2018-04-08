package et.tsingtaopad.dd.ddxt.term.select;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencysupplyInfoDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.dao.MstVisitMTempDao;
import et.tsingtaopad.db.table.MstAgencysupplyInfo;
import et.tsingtaopad.db.table.MstAgencysupplyInfoTemp;
import et.tsingtaopad.db.table.MstCheckexerecordInfo;
import et.tsingtaopad.db.table.MstCheckexerecordInfoTemp;
import et.tsingtaopad.db.table.MstCmpsupplyInfo;
import et.tsingtaopad.db.table.MstCmpsupplyInfoTemp;
import et.tsingtaopad.db.table.MstCollectionexerecordInfo;
import et.tsingtaopad.db.table.MstCollectionexerecordInfoTemp;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstPromotermInfo;
import et.tsingtaopad.db.table.MstPromotermInfoTemp;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.MstVistproductInfoTemp;
import et.tsingtaopad.db.table.PadCheckaccomplishInfo;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;


/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermListService.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-28</br>
 * 功能描述: 巡店拜访_终端列表的业务逻辑</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("DefaultLocale")
public class XtTermSelectService {

    private final String TAG = "TermListService";

    private Context context;

    public XtTermSelectService(Context context) {
        this.context = context;
    }

    /**
     * 根据路线获取终端 获取相应的数据列表数据
     *
     * @param lineKeys 线路表主键
     * @return
     */
    public List<XtTermSelectMStc> queryTerminal(List<String> lineKeys) {

        List<XtTermSelectMStc> terminalList = new ArrayList<XtTermSelectMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            for (int i = 0; i < lineKeys.size(); i++) {
                List<XtTermSelectMStc> termlst = dao.queryLineTermLst(helper, lineKeys.get(i));
                terminalList.addAll(termlst);
            }
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return terminalList;
    }

    // 复制终端表 到终端表临时表
    public void toCopyMstTerminalinfoMData(MstTerminalinfoM xtTermSelectMStc) {

        // 事务控制
        AndroidDatabaseConnection connection = null;
        // 开始复制
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstTerminalinfoMTemp, String> terminalinfoMTempDao = helper.getMstTerminalinfoMTempDao();

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            // 复制终端临时表
            MstTerminalinfoM term = xtTermSelectMStc;
            MstTerminalinfoMTemp terminalinfoMTemp = null;
            if (term != null) {
                terminalinfoMTemp = new MstTerminalinfoMTemp();
                terminalinfoMTemp.setTerminalkey(term.getTerminalkey());
                terminalinfoMTemp.setRoutekey(term.getRoutekey());
                terminalinfoMTemp.setTerminalcode(term.getTerminalcode());
                terminalinfoMTemp.setTerminalname(term.getTerminalname());
                terminalinfoMTemp.setProvince(term.getProvince());
                terminalinfoMTemp.setCity(term.getCity());
                terminalinfoMTemp.setCounty(term.getCounty());
                terminalinfoMTemp.setAddress(term.getAddress());
                terminalinfoMTemp.setContact(term.getContact());
                terminalinfoMTemp.setMobile(term.getMobile());
                terminalinfoMTemp.setTlevel(term.getTlevel());
                terminalinfoMTemp.setSequence(term.getSequence());
                terminalinfoMTemp.setCycle(term.getCycle());
                terminalinfoMTemp.setHvolume(term.getHvolume());
                terminalinfoMTemp.setMvolume(term.getMvolume());
                terminalinfoMTemp.setPvolume(term.getPvolume());
                terminalinfoMTemp.setLvolume(term.getLvolume());
                terminalinfoMTemp.setStatus(term.getStatus());
                terminalinfoMTemp.setSellchannel(term.getSellchannel());
                terminalinfoMTemp.setMainchannel(term.getMainchannel());
                terminalinfoMTemp.setMinorchannel(term.getMinorchannel());
                terminalinfoMTemp.setAreatype(term.getAreatype());
                terminalinfoMTemp.setSisconsistent(term.getSisconsistent());
                terminalinfoMTemp.setScondate(term.getScondate());
                terminalinfoMTemp.setPadisconsistent(term.getPadisconsistent());
                terminalinfoMTemp.setPadcondate(term.getPadcondate());
                terminalinfoMTemp.setComid(term.getComid());
                terminalinfoMTemp.setRemarks(term.getRemarks());
                terminalinfoMTemp.setOrderbyno(term.getOrderbyno());
                terminalinfoMTemp.setVersion(term.getVersion());
                terminalinfoMTemp.setCredate(term.getCredate());
                terminalinfoMTemp.setCreuser(term.getCreuser());
                terminalinfoMTemp.setSelftreaty(term.getSelftreaty());
                terminalinfoMTemp.setCmpselftreaty(term.getCmpselftreaty());
                terminalinfoMTemp.setUpdatetime(term.getUpdatetime());
                terminalinfoMTemp.setUpdateuser(term.getUpdateuser());
                terminalinfoMTemp.setDeleteflag(term.getDeleteflag());
                terminalinfoMTemp.setIfminedate(term.getIfminedate());
                terminalinfoMTemp.setIfmine(term.getIfmine());
                terminalinfoMTempDao.createOrUpdate(terminalinfoMTemp);

            }


            connection.commit(null);
        }catch (Exception e){
            Log.e(TAG, "复制数据出错", e);
            try {
                connection.rollback(null);
                //ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    // 删除终端表临时表数据
    public void deleteData(String tabname) {

        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            String sql = "DELETE FROM " + tabname + ";";
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, "删除临时表数据失败", e);
        }
    }

    /**
     * 获取终端信息
     *
     * @param termId 终端ID
     * @return
     */
    public MstTerminalinfoM findTermByTerminalkey(String termId) {

        MstTerminalinfoM termInfo = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            termInfo = dao.queryForId(termId);

        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }

        return termInfo;
    }

    /***
     * 获取二级区域列表
     * @param //tempDao
     * @param //visitId
     * @return
     * @throws SQLException
     */
    public List<MstMarketareaM> getMstMarketareaMList(String areapid) {
        AndroidDatabaseConnection connection = null;
        List<MstMarketareaM> valueLst = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstMarketareaM, String> valueDao = helper.getMstMarketareaMDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);


            QueryBuilder<MstMarketareaM, String> valueQb = valueDao.queryBuilder();
            Where<MstMarketareaM, String> valueWr = valueQb.where();
            valueWr.eq("areapid", areapid);
            valueLst = valueQb.query();
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "查看拜访记录去除重复指标出错", e);
            try {
                connection.rollback(null);
                ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return valueLst;
    }

    /***
     * 获取某个二级区域  的定格列表
     * @param //tempDao
     * @param //visitId
     * @return
     * @throwsException
     */
    public List<MstGridM> getMstGridMList(String areaid) {
        AndroidDatabaseConnection connection = null;
        List<MstGridM> valueLst = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstGridM, String> valueDao = helper.getMstGridMDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);


            QueryBuilder<MstGridM, String> valueQb = valueDao.queryBuilder();
            Where<MstGridM, String> valueWr = valueQb.where();
            valueWr.eq("areaid", areaid);
            valueLst = valueQb.query();
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "查看拜访记录去除重复指标出错", e);
            try {
                connection.rollback(null);
                ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return valueLst;
    }

    /***
     * 获取某个定格  的路线列表
     * @param //tempDao
     * @param //visitId
     * @return
     * @throwsException
     */
    public List<MstRouteM> getMstRouteMList(String gridkey) {
        AndroidDatabaseConnection connection = null;
        List<MstRouteM> valueLst = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstRouteM, String> valueDao = helper.getMstRouteMDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);


            QueryBuilder<MstRouteM, String> valueQb = valueDao.queryBuilder();
            Where<MstRouteM, String> valueWr = valueQb.where();
            valueWr.eq("gridkey", gridkey);
            valueLst = valueQb.query();
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "查看拜访记录去除重复指标出错", e);
            try {
                connection.rollback(null);
                ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return valueLst;
    }


}
