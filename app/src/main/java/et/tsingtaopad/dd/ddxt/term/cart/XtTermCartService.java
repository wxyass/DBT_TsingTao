package et.tsingtaopad.dd.ddxt.term.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.pinyin.PinYin4jUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitValterMDao;
import et.tsingtaopad.db.dao.MitVisitMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.table.MitValterM;
import et.tsingtaopad.db.table.MitVisitM;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.TermSequence;


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
public class XtTermCartService {

    private final String TAG = "TermListService";

    private Context context;

    public XtTermCartService(Context context) {
        this.context = context;
    }

    /**
     * 获取购物车终端列表 获取相应的数据列表数据(协同)
     *
     * @return
     */
    public List<XtTermSelectMStc> queryCartTermList() {

        List<XtTermSelectMStc> terminalList = new ArrayList<XtTermSelectMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            List<XtTermSelectMStc> termlst = dao.queryCartTermLst(helper);
            terminalList.addAll(termlst);
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return terminalList;
    }
    /**
     * 获取购物车终端列表 获取相应的数据列表数据(协同+追溯 所有的终端)
     *
     * @return
     */
    public List<XtTermSelectMStc> queryAllCartTermList() {

        List<XtTermSelectMStc> terminalList = new ArrayList<XtTermSelectMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            List<XtTermSelectMStc> termlst = dao.queryAllCartTermLst(helper);
            terminalList.addAll(termlst);
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return terminalList;
    }
    /**
     * 获取购物车终端列表 获取相应的数据列表数据(追溯)
     *
     * @return
     */
    public List<XtTermSelectMStc> queryZsCartTermList() {

        List<XtTermSelectMStc> terminalList = new ArrayList<XtTermSelectMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            List<XtTermSelectMStc> termlst = dao.queryZsCartTermLst(helper);
            terminalList.addAll(termlst);
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return terminalList;
    }

    /**
     * 根据终端key 获取上传信息(协同)
     *
     * @param terminalkey 终端key
     * @return
     */
    public List<MitVisitM> getXtMitValterM(String terminalkey) {

        List<MitVisitM> terminalList = new ArrayList<MitVisitM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MitVisitMDao dao = helper.getDao(MitVisitM.class);
            List<MitVisitM> termlst = dao.queryXtMitVisitM(helper, terminalkey);
            terminalList.addAll(termlst);
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return terminalList;
    }

    /**
     * 按条件查询终端列表
     *
     * @param termLst       线路下所有终端
     * @param searchStr     查询条件
     * @param termPinyinMap 各终端名称的拼音
     */
    public List<XtTermSelectMStc> searchTermByname(List<XtTermSelectMStc> termLst, String searchStr, Map<String, String> termPinyinMap) {

        List<XtTermSelectMStc> tempLst = new ArrayList<XtTermSelectMStc>();
        if (!CheckUtil.IsEmpty(termLst)) {
            if (CheckUtil.isBlankOrNull(searchStr)) {
                tempLst = termLst;
            } else {
                searchStr = searchStr.toLowerCase();
                for (XtTermSelectMStc item : termLst) {
                    Pattern pattern = Pattern.compile("[a-z]*");
                    if (pattern.matcher(searchStr).matches()) {
                        String pinyin = termPinyinMap.get(item.getTerminalkey());
                        if (pinyin.indexOf("," + searchStr) > -1 || pinyin.contains(searchStr)) {
                            tempLst.add(item);
                        }
                    } else {
                        if (item.getTerminalname().contains(searchStr)) {
                            tempLst.add(item);
                        }
                    }
                }
            }
        }
        return tempLst;
    }

    /**
     * 获取各终端名称的拼音
     *
     * @param termLst 终端列表
     */
    public Map<String, String> getAllTermPinyin(List<XtTermSelectMStc> termLst) {
        Map<String, String> termPinyinMap = new HashMap<String, String>();
        for (XtTermSelectMStc item : termLst) {
            termPinyinMap.put(item.getTerminalkey(), "," + PinYin4jUtil.converterToFirstSpell(item.getTerminalname()).toLowerCase());
        }

        return termPinyinMap;
    }

    /**
     * 根据终端key 获取上传信息(追溯)
     *
     * @param terminalkey 终端key
     * @return
     */
    public List<MitValterM> getZsMitValterM(String terminalkey) {

        List<MitValterM> terminalList = new ArrayList<MitValterM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MitValterMDao dao = helper.getDao(MitValterM.class);
            List<MitValterM> termlst = dao.queryZsMitValterMData(helper, terminalkey);
            terminalList.addAll(termlst);
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return terminalList;
    }

    /***
     * 更新终端顺序 不上传
     * @param list
     */
    public void updateTermSequence(List<TermSequence> list)
    {
        AndroidDatabaseConnection connection = null;
        try
        {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            SQLiteDatabase database = helper.getWritableDatabase();
            connection = new AndroidDatabaseConnection(database, true);
            Log.e(TAG, "更改巡店拜访顺序");
            connection.setAutoCommit(false);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            //dao.updateTermSequence(helper, list);
            dao.updateTermTempSequence(helper, list);
            connection.commit(null);
            //sendTermSequenceRequest(list);
        }
        catch (SQLException e)
        {
            Log.e(TAG, "更改巡店拜访顺序失败", e);
            try
            {
                connection.rollback(null);
            }
            catch (SQLException e1)
            {
                e1.printStackTrace();
            }
        }
    }


}
