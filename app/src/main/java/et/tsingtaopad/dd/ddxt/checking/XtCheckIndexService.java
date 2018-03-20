package et.tsingtaopad.dd.ddxt.checking;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.dao.MstGroupproductMDao;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexQuicklyStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtCheckIndexService extends XtShopVisitService {
    
    private final String TAG = "XtCheckIndexService";
    
    public XtCheckIndexService(Context context, Handler handler) {
        super(context, handler);
    }

    /**
     * 初始化指标、指标值树级关系 对象
     */
    public void initCheckTypeStatus() {
        List<KvStc> checkTypeStatusList=new ArrayList<KvStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            PadChecktypeMDao dao = helper.getDao(PadChecktypeM.class);
            checkTypeStatusList= dao.queryCheckTypeStatus(helper, ConstValues.FLAG_0);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访指标失败", e);
            e.printStackTrace();
        }
        GlobalValues.indexLst = checkTypeStatusList;
    }

    /**
     * 获取分项采集页面显示数据
     *
     * @param visitId           本次拜访主键
     * @param termId            本次拜访终端ID
     * @param channelId         本次拜访终端所属渠道
     * @param seeFlag           查看标识
     * @return
     */
    public List<XtProIndex> queryCalculateIndex(String visitId, String termId, String channelId, String seeFlag) {

        // 获取分项采集数据
        List<CheckIndexCalculateStc> stcLst = new ArrayList<CheckIndexCalculateStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            stcLst = dao.queryCalculateIndex(helper, visitId, termId, channelId, seeFlag);

            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }

        // 组建成界面显示所需要的数据结构
        List<XtProIndex> proIndexLst = new ArrayList<XtProIndex>();
        String indexId = "";
        XtProIndex indexItem = new XtProIndex();// XtProItem
        XtProIndexValue indexValueItem;
        for (CheckIndexCalculateStc item : stcLst) {
            //
            if (!indexId.equals(item.getIndexId())) {
                indexItem = new XtProIndex();
                indexItem.setIndexId(item.getIndexId());
                indexItem.setIndexName(item.getIndexName());
                indexItem.setIndexType(item.getIndexType());
                indexItem.setIndexValueLst(new ArrayList<XtProIndexValue>());
                proIndexLst.add(indexItem);
                indexId = item.getIndexId();
            }
            indexValueItem = new XtProIndexValue();
            indexValueItem.setIndexResultId(item.getRecordId());
            indexValueItem.setIndexId(item.getIndexId());
            indexValueItem.setIndexType(item.getIndexType());
            indexValueItem.setIndexValueId(item.getIndexValueId());
            indexValueItem.setIndexValueName(item.getIndexValueName());
            indexValueItem.setProId(item.getProId());
            indexValueItem.setProName(item.getProName());
            indexItem.getIndexValueLst().add(indexValueItem);
        }
        return proIndexLst;
    }

    /**
     * 获取分项采集部分的产品指标对应的采集项目数据
     *
     * @param visitId           拜访主键
     * @param channelId         渠道ID
     * @return
     */
    public List<XtProItem> queryCalculateItem(String visitId, String channelId) {

        // 获取分项采集各指标对应的采集项目数据
        List<CheckIndexQuicklyStc> stcLst = new ArrayList<CheckIndexQuicklyStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            stcLst = dao.queryCalculateItem(helper, visitId, channelId);

            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }

        // 组建界面要显示的数据
        List<XtProItem> proItemLst = new ArrayList<XtProItem>();
        XtProItem proItem = new XtProItem();
        String Id = "", proItemId;
        for (CheckIndexQuicklyStc stc : stcLst) {
            proItemId = stc.getProductkey() + stc.getColitemkey();
            if (!Id.equals(proItemId)) {
                proItem = new XtProItem();
                proItem.setColRecordKey(stc.getColRecordId());
                proItem.setCheckkey(stc.getCheckkey());
                proItem.setItemId(stc.getColitemkey());
                proItem.setItemName(stc.getColitemname());
                proItem.setProId(stc.getProductkey());
                proItem.setProName(stc.getProName());
                proItem.setChangeNum(stc.getChangeNum());
                proItem.setFinalNum(stc.getFinalNum());
                proItem.setXianyouliang(stc.getXianyouliang());
                proItem.setBianhualiang(stc.getBianhualiang());
                proItem.setFreshness(FunUtil.isNullSetDate(stc.getFreshness()));// 新鲜度
                proItem.setIndexIdLst(new ArrayList<String>());
                proItemLst.add(proItem);
                Id = proItemId;
            }
            if (!proItem.getIndexIdLst().contains(stc.getCheckkey())) {
                proItem.getIndexIdLst().add(stc.getCheckkey());
            }
        }
        return proItemLst;
    }
    

}
