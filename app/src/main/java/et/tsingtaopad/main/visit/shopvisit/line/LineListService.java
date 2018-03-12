package et.tsingtaopad.main.visit.shopvisit.line;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstRouteMDao;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.main.visit.shopvisit.line.domain.MstRouteMStc;


/**
 * Created by yangwenmin on 2017/12/27.
 * 功能描述: 巡店拜访_线路列表的业务逻辑</br>
 */
public class LineListService {
    
    private final String TAG = "LineListService";
    
    private Context context;
    
    public LineListService(Context context) {
        this.context = context;
    }
    
    /**
     *  获取相应的数据列表数据
     */
    public List<MstRouteMStc> queryLine() {
        
        List<MstRouteMStc> lst = new ArrayList<MstRouteMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstRouteMDao dao = helper.getDao(MstRouteM.class);
            lst = dao.queryLine(helper);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        
        // 获取线路ID (即:所有路线routekey集合)
        List<String> lineIdLst = FunUtil .getPropertyByName(lst, "routekey", String.class);
        
        // 获取每条线路上次拜访日期
        Map<String, String> visitMap = this.queryPrevVisitDate(lineIdLst);

        for (MstRouteMStc item : lst) {
            item.setPrevDate(visitMap.get(item.getRoutekey()));
        }
        
        return lst;
    }
    
    /**
     * 获取线路上次拜访日期
     * 
     * @param lineIdLst
     */
    public Map<String, String> queryPrevVisitDate(List<String> lineIdLst) {
        Map<String, String> visitMap = new HashMap<String, String>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstRouteMDao dao = helper.getDao(MstRouteM.class);
            visitMap = dao.queryPrevVisitDate(helper, lineIdLst);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return visitMap;
    }

}
