package et.tsingtaopad.dd.ddxt.checking.num;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.checking.domain.XtQuicklyProItem;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.QuicklyProItem;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtQuickCollectService extends XtShopVisitService {

    private final String TAG = "XtCheckIndexService";

    public XtQuickCollectService(Context context, Handler handler) {
        super(context, handler);
    }

    /**
     * 初始化快速采集所需要数据(将产品按采集项归类)
     *
     * @param proItemLst 分项采集部分的产品指标对应的采集项目数据
     * @return
     */
    public List<XtQuicklyProItem> initQuicklyProItem(List<XtProItem> proItemLst) {

        List<XtQuicklyProItem> quicklyLst = new ArrayList<XtQuicklyProItem>();
        if (!CheckUtil.IsEmpty(proItemLst)) {
            int index;
            XtQuicklyProItem quicklyItem;
            List<String> itemIdLst = new ArrayList<String>();
            for (XtProItem item : proItemLst) {
                if (!itemIdLst.contains(item.getItemId())) {
                    quicklyItem = new XtQuicklyProItem();
                    quicklyItem.setItemId(item.getItemId());
                    quicklyItem.setItemName(item.getItemName());
                    quicklyItem.setProItemLst(new ArrayList<XtProItem>());
                    quicklyLst.add(quicklyItem);
                    itemIdLst.add(item.getItemId());
                }
                index = itemIdLst.indexOf(item.getItemId());
                quicklyItem = quicklyLst.get(index);
                quicklyItem.getProItemLst().add(item);
            }
        }

        return quicklyLst;
    }

}
