package et.tsingtaopad.dd.ddxt.term.select;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;


import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;


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


}
