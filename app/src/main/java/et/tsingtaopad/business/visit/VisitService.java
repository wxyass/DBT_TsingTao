package et.tsingtaopad.business.visit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import et.tsingtaopad.R;
import et.tsingtaopad.business.visit.bean.VisitContentStc;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitValaddaccountMDao;
import et.tsingtaopad.db.dao.MitValcmpotherMTempDao;
import et.tsingtaopad.db.dao.MitValterMTempDao;
import et.tsingtaopad.db.dao.MstAgencysupplyInfoDao;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.dao.MstGroupproductMDao;
import et.tsingtaopad.db.dao.MstGroupproductMTempDao;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.dao.MstTermLedgerInfoDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMCartDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMTempDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.dao.MstVisitMTempDao;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.table.MitValaddaccountM;
import et.tsingtaopad.db.table.MitValaddaccountMTemp;
import et.tsingtaopad.db.table.MitValaddaccountproMTemp;
import et.tsingtaopad.db.table.MitValagreeMTemp;
import et.tsingtaopad.db.table.MitValagreedetailMTemp;
import et.tsingtaopad.db.table.MitValcheckitemMTemp;
import et.tsingtaopad.db.table.MitValchecktypeMTemp;
import et.tsingtaopad.db.table.MitValcmpMTemp;
import et.tsingtaopad.db.table.MitValcmpotherMTemp;
import et.tsingtaopad.db.table.MitValgroupproMTemp;
import et.tsingtaopad.db.table.MitValpromotionsMTemp;
import et.tsingtaopad.db.table.MitValsupplyMTemp;
import et.tsingtaopad.db.table.MitValterMTemp;
import et.tsingtaopad.db.table.MstAgencysupplyInfo;
import et.tsingtaopad.db.table.MstAgencysupplyInfoTemp;
import et.tsingtaopad.db.table.MstAgreeDetailTmp;
import et.tsingtaopad.db.table.MstAgreeTmp;
import et.tsingtaopad.db.table.MstCameraInfoM;
import et.tsingtaopad.db.table.MstCameraInfoMTemp;
import et.tsingtaopad.db.table.MstCheckexerecordInfo;
import et.tsingtaopad.db.table.MstCheckexerecordInfoTemp;
import et.tsingtaopad.db.table.MstCmpsupplyInfo;
import et.tsingtaopad.db.table.MstCmpsupplyInfoTemp;
import et.tsingtaopad.db.table.MstCollectionexerecordInfo;
import et.tsingtaopad.db.table.MstCollectionexerecordInfoTemp;
import et.tsingtaopad.db.table.MstGroupproductM;
import et.tsingtaopad.db.table.MstGroupproductMTemp;
import et.tsingtaopad.db.table.MstPromotermInfo;
import et.tsingtaopad.db.table.MstPromotermInfoTemp;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstTermLedgerInfo;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMCart;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.MstVistproductInfoTemp;
import et.tsingtaopad.db.table.PadCheckaccomplishInfo;
import et.tsingtaopad.dd.ddxt.chatvie.domain.XtChatVieStc;
import et.tsingtaopad.dd.ddxt.checking.domain.XtCheckIndexCalculateStc;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.dd.ddzs.zsinvoicing.zsinvocingtz.domain.ZsTzItemIndex;
import et.tsingtaopad.dd.ddzs.zsinvoicing.zsinvocingtz.domain.ZsTzLedger;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexQuicklyStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.sayhi.domain.MstTerminalInfoMStc;


/**
 * 文件名：VisitService.java</br>
 * 功能描述: </br>
 */
public class VisitService {

    private final String TAG = "VisitService";

    protected Context context;
    protected Handler handler;

    public VisitService(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }


}
