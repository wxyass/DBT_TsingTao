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
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
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
public class XtTermCartService {

    private final String TAG = "TermListService";

    private Context context;

    public XtTermCartService(Context context) {
        this.context = context;
    }

    /**
     * 获取购物车终端列表 获取相应的数据列表数据
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


}
