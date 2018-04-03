package et.tsingtaopad.dd.ddxt.shopvisit;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import cn.com.benyoyo.manage.bs.IntStc.BsVisitEmpolyeeStc;
import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencysupplyInfoDao;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.dao.MstGroupproductMDao;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMTempDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.dao.MstVisitMTempDao;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.table.MstAgencysupplyInfo;
import et.tsingtaopad.db.table.MstAgencysupplyInfoTemp;
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
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.MstVistproductInfoTemp;
import et.tsingtaopad.db.table.PadCheckaccomplishInfo;
import et.tsingtaopad.dd.ddxt.checking.domain.XtCheckIndexCalculateStc;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexQuicklyStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;


/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtShopVisitService {
    
    private final String TAG = "XtShopVisitService";
    
    protected Context context;
    protected Handler handler;

	private String prevVisitId;// 获取上次拜访主键

	private String prevVisitDate; // 上次拜访日期
    
    public XtShopVisitService(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    /**
     * 获取某终端的最新拜访记录信息
     *
     * @param termId     终端ID
     * @param isEndVisit 是否结束拜访-true：上次结束拜访，false：所有拜访
     * @return
     */
    public MstVisitM findNewLastVisit(String termId, boolean isEndVisit) {

        MstVisitM visitM = null;
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        MstVisitMDao visitDao;
        try {
            visitDao = (MstVisitMDao) helper.getMstVisitMDao();
            QueryBuilder<MstVisitM, String> qBuilder = visitDao.queryBuilder();
            if (isEndVisit) {
                qBuilder.where().eq("terminalkey", termId).and().eq("padisconsistent", "1");
            } else {
                qBuilder.where().eq("terminalkey", termId);
            }
            qBuilder.orderBy("visitdate", false);
            visitM = qBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访请表DAO异常", e);
        }
        return visitM;
    }

    // 复制各个临时表
    public String toCopyData(XtTermSelectMStc termStc) {
        // 从拜访主表中获取最后一次拜访数据
        MstVisitM mstVisitM = findNewLastVisit(termStc.getTerminalkey(), false);
        MstVisitMTemp visitMTemp = null;

        // 记录当前时间 作为拜访开始日期
        String visitDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");

        // 获取上次拜访主键
        prevVisitId = mstVisitM.getVisitkey();
        // 上次拜访日期
        prevVisitDate = mstVisitM.getVisitdate();

        // 事务控制
        AndroidDatabaseConnection connection = null;
        // 开始复制
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVisitM, String> visitDao = helper.getMstVisitMDao();
            Dao<MstVisitMTemp, String> visitTempDao = helper.getMstVisitMTempDao();

            Dao<MstTerminalinfoM, String> terminalinfoMDao = helper.getMstTerminalinfoMDao();
            Dao<MstTerminalinfoMTemp, String> terminalinfoMTempDao = helper.getMstTerminalinfoMTempDao();

            Dao<MstVistproductInfo, String> proDao = helper.getMstVistproductInfoDao();
            Dao<MstVistproductInfoTemp, String> proTempDao = helper.getMstVistproductInfoTempDao();

            MstAgencysupplyInfoDao asupplyDao = helper.getDao(MstAgencysupplyInfo.class);
            Dao<MstAgencysupplyInfo, String> agencyDao = helper.getDao(MstAgencysupplyInfo.class);
            Dao<MstAgencysupplyInfoTemp, String> agencyTempDao = helper.getMstAgencysupplyInfoTempDao();

            Dao<MstCmpsupplyInfo, String> cmpSupplyDao = helper.getMstCmpsupplyInfoDao();
            Dao<MstCmpsupplyInfoTemp, String> cmpSupplyTempDao = helper.getMstCmpsupplyInfoTempDao();

            Dao<MstCheckexerecordInfo, String> valueDao = helper.getMstCheckexerecordInfoDao();
            Dao<MstCheckexerecordInfoTemp, String> valueTempDao = helper.getMstCheckexerecordInfoTempDao();

            Dao<MstCollectionexerecordInfo, String> collectionDao = helper.getMstCollectionexerecordInfoDao();
            Dao<MstCollectionexerecordInfoTemp, String> collectionTempDao = helper.getMstCollectionexerecordInfoTempDao();

            Dao<MstPromotermInfo, String> promDao = helper.getMstPromotermInfoDao();
            Dao<MstPromotermInfoTemp, String> promTempDao = helper.getMstPromotermInfoTempDao();

            Dao<PadCheckaccomplishInfo, String> padCheckaccomplishInfoDao = helper.getPadCheckaccomplishInfoDao();

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            // 复制拜访表
            // 如果上次拜访为空，则是本终端的第一次拜访
            if (mstVisitM == null) {
                visitMTemp = new MstVisitMTemp();
                visitMTemp.setVisitkey(FunUtil.getUUID());
                visitMTemp.setTerminalkey(termStc.getTerminalkey());
                visitMTemp.setRoutekey(termStc.getRoutekey());
                visitMTemp.setGridkey(PrefUtils.getString(context, "gridId", ""));
                visitMTemp.setAreaid(PrefUtils.getString(context, "disId", ""));
                visitMTemp.setVisitdate(visitDate);
                visitMTemp.setUserid(PrefUtils.getString(context, "userCode", ""));
                visitMTemp.setStatus(ConstValues.FLAG_1);
                visitMTemp.setSisconsistent(ConstValues.FLAG_0);
                visitMTemp.setPadisconsistent(ConstValues.FLAG_0);
                visitMTemp.setOrderbyno(ConstValues.FLAG_0);// 同一次是0 返回时会改为1
                visitMTemp.setCreuser(PrefUtils.getString(context, "userCode", ""));
                visitMTemp.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                visitMTemp.setUploadFlag(ConstValues.FLAG_0);
                visitTempDao.create(visitMTemp);

                // 复制visitproduct_temp表 //初始化拜访产品表
                addProductInfoToTemp(visitMTemp.getTerminalkey(), visitMTemp.getVisitkey(), agencyDao, proTempDao);

                // 复制到拉链表临时表  只有当天重复拜访才会复制上次数据,若是当天第一次拜访,只复制与产品无关的指标
                if (visitDate.substring(0, 8).equals(prevVisitDate.substring(0, 8))) {
                    // 复制到拉链表临时表 MstCheckexerecordInfoTemp 全部复制
                    createMstCheckexerecordInfoTemp(prevVisitId, visitDate, visitMTemp, valueDao, valueTempDao);
                } else {
                    // 复制到拉链表临时表 当天第一次拜访,MstCheckexerecordInfoTemp 只复制与产品无关的指标
                    createMstCheckexerecordInfoTemp1(prevVisitId, visitDate, visitMTemp, valueDao, valueTempDao);
                }

            } else {
                // 非第一次拜访该终端
                visitMTemp = new MstVisitMTemp();

                // 如果上次拜访日期与本次拜访不是同一天(竞品拜访记录清空)
                if (!visitDate.substring(0, 8).equals(prevVisitDate.substring(0, 8))) {
                    visitMTemp.setRemarks("");
                } else {
                    visitMTemp.setRemarks(mstVisitM.getRemarks());
                }
                // 复制拜访主表数据，到临时表MST_VISIT_M_TEMP(拜访主表)
                visitMTemp.setVisitkey(FunUtil.getUUID());
                visitMTemp.setTerminalkey(termStc.getTerminalkey());
                visitMTemp.setRoutekey(termStc.getRoutekey());
                visitMTemp.setVisitdate(visitDate);
                //visitMTemp.setStatus(ConstValues.FLAG_1);
                visitMTemp.setStatus(mstVisitM.getStatus());// 上次是否有效拜访
                visitMTemp.setLongitude(mstVisitM.getLongitude());
                visitMTemp.setLatitude(mstVisitM.getLatitude());
                visitMTemp.setEnddate(null);
                visitMTemp.setSisconsistent(ConstValues.FLAG_0);
                visitMTemp.setScondate(null);
                visitMTemp.setPadisconsistent(ConstValues.FLAG_0);
                visitMTemp.setOrderbyno(ConstValues.FLAG_0);// 同一次是0 返回时会改为1
                visitMTemp.setPadcondate(null);
                visitMTemp.setUploadFlag(ConstValues.FLAG_0);
                visitMTemp.setCredate(null);
                visitMTemp.setUpdatetime(null);
                visitMTemp.setUserid(PrefUtils.getString(context, "userCode", ""));// 待定
                visitMTemp.setGridkey(PrefUtils.getString(context, "gridId", ""));// 待定
                visitMTemp.setAreaid(PrefUtils.getString(context, "disId", ""));// 待定
                visitTempDao.create(visitMTemp);

                // 复制visitproduct_temp表
                List<MstVistproductInfo> proLst = proDao.queryForEq("visitkey", prevVisitId);
                if (!CheckUtil.IsEmpty(proLst)) {
                    //修复进销存界面因经销商不给该定格供货所以产品不显示 ,但是查指标界面还显示此商品的BUG
                    List<MstAgencysupplyInfo> agencysupply = asupplyDao.agencysupply(helper, visitMTemp.getTerminalkey());
                    // 通过终端获取此终端供货关系
                    // List<MstAgencysupplyInfo> agencysupplyList=getAgencySupplyInfoList(mstVisitM.getTerminalkey(), agencyDao);
                    Set<String> agencysupplySet = new HashSet<String>();
                    for (MstAgencysupplyInfo info : agencysupply) {
                        String key = info.getUpperkey() + info.getProductkey();// 经销商key+产品key
                        agencysupplySet.add(key);
                    }

                    MstVistproductInfoTemp vistproductInfoTemp = null;
                    for (MstVistproductInfo product : proLst) {
                        if (!ConstValues.FLAG_1.equals(product.getDeleteflag())) {
                            String key = product.getAgencykey() + product.getProductkey();
                            if (CheckUtil.isBlankOrNull(product.getProductkey()) || agencysupplySet.contains(key)) {// productkey为空:表示竞品记录
                                vistproductInfoTemp = new MstVistproductInfoTemp();
                                vistproductInfoTemp.setRecordkey(FunUtil.getUUID());
                                vistproductInfoTemp.setVisitkey(visitMTemp.getVisitkey());

                                // (隔天清零)如果上次拜访日期与本次拜访不是同一天
                                if (!visitDate.substring(0, 8).equals(prevVisitDate.substring(0, 8))) {
                                    //上次库存
                                    vistproductInfoTemp.setPronum(product.getCurrnum());
                                    //日销量
                                    vistproductInfoTemp.setSalenum(null);
                                    //订单量(原 上周期进货总量)
                                    vistproductInfoTemp.setPurcnum(null);
                                    //当前库存 (本次库存)
                                    vistproductInfoTemp.setCurrnum(null);

                                } else {// 当日重复拜访
                                    //上次库存
                                    vistproductInfoTemp.setPronum(product.getPronum());
                                    //日销量
                                    vistproductInfoTemp.setSalenum(product.getSalenum());
                                    //订单量(原 上周期进货总量)
                                    vistproductInfoTemp.setPurcnum(product.getPurcnum());
                                    //当前库存 (本次库存)
                                    vistproductInfoTemp.setCurrnum(product.getCurrnum());

                                }

                                vistproductInfoTemp.setProductkey(product.getProductkey());
                                vistproductInfoTemp.setCmpproductkey(product.getCmpproductkey());
                                vistproductInfoTemp.setCmpcomkey(product.getCmpcomkey());
                                vistproductInfoTemp.setAgencykey(product.getAgencykey());
                                vistproductInfoTemp.setAgencyname(product.getAgencyname());// 用户输入的竞品经销商
                                vistproductInfoTemp.setPurcprice(product.getPurcprice());
                                vistproductInfoTemp.setRetailprice(product.getRetailprice());
                                //vistproductInfoTemp.setPronum(product.getPronum());
                                vistproductInfoTemp.setRemarks(product.getRemarks());
                                vistproductInfoTemp.setOrderbyno(product.getOrderbyno());
                                vistproductInfoTemp.setCreuser(product.getCreuser());
                                vistproductInfoTemp.setUpdateuser(product.getUpdateuser());
                                vistproductInfoTemp.setFristdate(product.getFristdate());
                                vistproductInfoTemp.setAddcard(product.getAddcard());// 累计卡
                                //
                                vistproductInfoTemp.setSisconsistent(ConstValues.FLAG_0);
                                vistproductInfoTemp.setScondate(null);
                                vistproductInfoTemp.setPadisconsistent(ConstValues.FLAG_0);
                                vistproductInfoTemp.setPadcondate(null);
                                vistproductInfoTemp.setDeleteflag(ConstValues.FLAG_0);
                                vistproductInfoTemp.setCredate(null);
                                vistproductInfoTemp.setUpdatetime(null);

                                proTempDao.create(vistproductInfoTemp);
                            }
                        }
                    }
                }

                // 重复拜访,复制拉链表,活动表
                if (visitDate.substring(0, 8).equals(prevVisitDate.substring(0, 8))) {
                    // 复制拉链表 MstCheckexerecordInfoTemp 全部复制   只有当天重复拜访才会复制上次数据,若是当天第一次拜访,只复制与产品无关的指标
                    createMstCheckexerecordInfoTemp(prevVisitId, visitDate, visitMTemp, valueDao, valueTempDao);
                    // 复制活动拜访终端表  true:当天重复拜访
                    createMstPromotermInfoTemp(helper, prevVisitId, visitDate, visitMTemp, promDao, promTempDao, true);
                    //

                } else {// 当天第一次拜访
                    // 复制拉链表 MstCheckexerecordInfoTemp 当天第一次拜访,MstCheckexerecordInfoTemp 只复制与产品无关的指标
                    createMstCheckexerecordInfoTemp1(prevVisitId, visitDate, visitMTemp, valueDao, valueTempDao);
                    // 复制活动拜访终端表 false:当天第一次拜访
                    createMstPromotermInfoTemp(helper, prevVisitId, visitDate, visitMTemp, promDao, promTempDao, false);
                    // 复制采集项表
                    createMstCollectionexerecordInfoTemp(collectionDao,collectionTempDao,padCheckaccomplishInfoDao,prevVisitId,termStc,visitMTemp);

                }
            }


            // 复制终端临时表
            /*MstTerminalinfoM term = findTermById(termStc.getTerminalkey());
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
                terminalinfoMTempDao.create(terminalinfoMTemp);
            }*/

            MstTerminalinfoMTemp terminalinfoMTemp = findTermTempById(termStc.getTerminalkey());

            // 复制 产品组合是否达标 (有则复制,没有则新建)
            createMstGroupproductMTemp(terminalinfoMTemp,visitMTemp.getVisitkey());

            // 复制我品供货关系临时表
            List<MstAgencysupplyInfo> agencysupply = asupplyDao.agencysupply(helper, visitMTemp.getTerminalkey());
            int size = agencysupply.size();
            if (size > 0) {
                MstAgencysupplyInfoTemp agencysupplyInfoTemp = null;
                for (MstAgencysupplyInfo supply : agencysupply) {
                    agencysupplyInfoTemp = new MstAgencysupplyInfoTemp();
                    agencysupplyInfoTemp.setAsupplykey(supply.getAsupplykey());
                    agencysupplyInfoTemp.setStatus("0");// 0:供货关系有效  1:供货关系失效  2:新加供货关系  3:值修改的供货关系
                    agencysupplyInfoTemp.setInprice(supply.getInprice());
                    agencysupplyInfoTemp.setReprice(supply.getReprice());
                    agencysupplyInfoTemp.setProductkey(supply.getProductkey());
                    agencysupplyInfoTemp.setLowerkey(supply.getLowerkey());
                    agencysupplyInfoTemp.setLowertype(supply.getLowertype());
                    agencysupplyInfoTemp.setUpperkey(supply.getUpperkey());
                    agencysupplyInfoTemp.setUppertype(supply.getUppertype());
                    agencysupplyInfoTemp.setSiebelkey(supply.getSiebelkey());
                    agencysupplyInfoTemp.setSisconsistent(supply.getSisconsistent());
                    agencysupplyInfoTemp.setScondate(supply.getScondate());
                    agencysupplyInfoTemp.setPadisconsistent("1");// 复制过来为1 ,复制回去时变了的为0
                    agencysupplyInfoTemp.setPadcondate(supply.getPadcondate());
                    agencysupplyInfoTemp.setComid(supply.getComid());
                    agencysupplyInfoTemp.setRemarks(supply.getRemarks());
                    agencysupplyInfoTemp.setOrderbyno(supply.getOrderbyno());
                    agencysupplyInfoTemp.setVersion(supply.getVersion());
                    agencysupplyInfoTemp.setCredate(supply.getCredate());
                    agencysupplyInfoTemp.setCreuser(supply.getCreuser());
                    agencysupplyInfoTemp.setUpdatetime(supply.getUpdatetime());
                    agencysupplyInfoTemp.setUpdateuser(supply.getUpdateuser());
                    agencysupplyInfoTemp.setDeleteflag(supply.getDeleteflag());
                    agencyTempDao.create(agencysupplyInfoTemp);
                }
            }

            // 复制竞品供货关系临时表
            List<MstCmpsupplyInfo> cmpSupplyLst = cmpSupplyDao.queryForEq("terminalkey", termStc.getTerminalkey());
            int cmpSupplySize = cmpSupplyLst.size();
            if (cmpSupplySize > 0) {
                MstCmpsupplyInfoTemp mstCmpsupplyInfoTemp = null;
                for (MstCmpsupplyInfo cmpsupply : cmpSupplyLst) {
                    mstCmpsupplyInfoTemp = new MstCmpsupplyInfoTemp();
                    mstCmpsupplyInfoTemp.setCmpsupplykey(cmpsupply.getCmpsupplykey());
                    mstCmpsupplyInfoTemp.setCmpproductkey(cmpsupply.getCmpproductkey());
                    mstCmpsupplyInfoTemp.setCmpcomkey(cmpsupply.getCmpcomkey());
                    mstCmpsupplyInfoTemp.setTerminalkey(cmpsupply.getTerminalkey());
                    mstCmpsupplyInfoTemp.setStatus("0");// 0:供货关系有效  1:供货关系失效  2:新加供货关系  3:值修改的供货关系
                    mstCmpsupplyInfoTemp.setInprice(cmpsupply.getInprice());
                    mstCmpsupplyInfoTemp.setReprice(cmpsupply.getReprice());
                    mstCmpsupplyInfoTemp.setCmpinvaliddate(cmpsupply.getCmpinvaliddate());
                    mstCmpsupplyInfoTemp.setSisconsistent(cmpsupply.getSisconsistent());
                    mstCmpsupplyInfoTemp.setScondate(cmpsupply.getScondate());
                    mstCmpsupplyInfoTemp.setPadisconsistent("1");// 复制过来为1 ,复制回去时变了的为0
                    mstCmpsupplyInfoTemp.setPadcondate(cmpsupply.getPadcondate());
                    mstCmpsupplyInfoTemp.setComid(cmpsupply.getComid());
                    mstCmpsupplyInfoTemp.setRemarks(cmpsupply.getRemarks());
                    mstCmpsupplyInfoTemp.setOrderbyno(cmpsupply.getOrderbyno());
                    mstCmpsupplyInfoTemp.setVersion(cmpsupply.getVersion());
                    mstCmpsupplyInfoTemp.setCredate(cmpsupply.getCredate());
                    mstCmpsupplyInfoTemp.setCreuser(cmpsupply.getCreuser());
                    mstCmpsupplyInfoTemp.setUpdatetime(cmpsupply.getUpdatetime());
                    mstCmpsupplyInfoTemp.setUpdateuser(cmpsupply.getUpdateuser());
                    mstCmpsupplyInfoTemp.setDeleteflag(cmpsupply.getDeleteflag());
                    cmpSupplyTempDao.create(mstCmpsupplyInfoTemp);
                }
            }


            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "复制数据出错", e);
            try {
                connection.rollback(null);
                //ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        }
        return visitMTemp.getVisitkey();
    }

    //
    /***
     * 初始化拜访产品表
     * @param termid
     * @param Vistid
     * @param agencyDao
     * @param //proDao
     */
    private void addProductInfoToTemp(String termid, String Vistid,
                                      Dao<MstAgencysupplyInfo, String> agencyDao, Dao<MstVistproductInfoTemp, String> proTempDao) {
        try {
            List<MstAgencysupplyInfo> valueLst = getAgencySupplyInfoList(termid, agencyDao);
            if (!CheckUtil.IsEmpty(valueLst)) {
                MstVistproductInfoTemp itemTemp;
                for (MstAgencysupplyInfo item : valueLst) {
                    itemTemp = new MstVistproductInfoTemp();
                    itemTemp.setRecordkey(FunUtil.getUUID());
                    itemTemp.setVisitkey(Vistid);
                    itemTemp.setProductkey(item.getProductkey());
                    itemTemp.setAgencykey(item.getUpperkey());
                    itemTemp.setCredate(DateUtil.getNow());
                    itemTemp.setUpdatetime(DateUtil.getNow());
                    //itemTemp.setCreuser(ConstValues.loginSession.getUserCode());
                    itemTemp.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    //itemTemp.setUpdateuser(ConstValues.loginSession.getUserCode());
                    itemTemp.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                    itemTemp.setPurcprice(0.00);
                    itemTemp.setRetailprice(0.00);
                    itemTemp.setPronum(0.00);
                    itemTemp.setCurrnum(0.00);
                    itemTemp.setSalenum(0.00);
                    proTempDao.create(itemTemp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * 通过终端获取此终端供货关系
     * @param termid
     * @param agencyDao
     * @return
     */
    private List<MstAgencysupplyInfo> getAgencySupplyInfoList(String termid, Dao<MstAgencysupplyInfo, String> agencyDao) {
        List<MstAgencysupplyInfo> list = new ArrayList<MstAgencysupplyInfo>();
        try {
            QueryBuilder<MstAgencysupplyInfo, String> qb = agencyDao.queryBuilder();
            Where<MstAgencysupplyInfo, String> where = qb.where();
            where.eq("lowerkey", termid);
            where.and();
            where.eq("status", "0");
            where.and();
            where.eq("lowertype", "2");
            list = qb.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param prevVisitId  上次拜访主键key
     * @param visitDate
     * @param visitMTemp
     * @param valueDao
     * @param valueTempDao
     * @throws SQLException
     */
    private void createMstCheckexerecordInfoTemp(String prevVisitId, String visitDate,
                                                 MstVisitMTemp visitMTemp, Dao<MstCheckexerecordInfo, String> valueDao,
                                                 Dao<MstCheckexerecordInfoTemp, String> valueTempDao)
            throws SQLException {
        createMstCheckexerecordInfoTemp1(prevVisitId, visitDate, visitMTemp, valueDao, valueTempDao);
        createMstCheckexerecordInfoTemp2(prevVisitId, visitDate, visitMTemp, valueDao, valueTempDao);
    }


    /**
     * @param helper
     * @param prevVisitId 上次拜访主键key
     * @param visitDate   这次拜访时间
     * @param visitMTemp  这次拜访记录
     * @param promDao
     * @param promTempDao
     * @param iscomflag
     * @throws SQLException
     */
    private void createMstPromotermInfoTemp(DatabaseHelper helper, String prevVisitId, String visitDate,
                                            MstVisitMTemp visitMTemp, Dao<MstPromotermInfo, String> promDao,
                                            Dao<MstPromotermInfoTemp, String> promTempDao, boolean iscomflag)
            throws SQLException {

        // 复制终端参加活动状态表，MST_PROMOTERM_INFO(终端参加活动信息表)
        String currDay = DateUtil.formatDate(new Date(), "yyyyMMdd");
        QueryBuilder<MstPromotionsM, String> promoQb = helper.getMstPromotionsMDao().queryBuilder();
        Where<MstPromotionsM, String> promoWr = promoQb.where();
        promoWr.and(promoWr.ge("enddate", currDay), promoWr.le("startdate", currDay));
        List<MstPromotionsM> promoLst = promoQb.query();
        List<String> promoIdLst = FunUtil.getPropertyByName(promoLst, "promotkey", String.class);
        List<MstPromotermInfo> promTermLst = promDao.queryForEq("visitkey", prevVisitId);
        if (!CheckUtil.IsEmpty(promTermLst)) {
            MstPromotermInfoTemp promotermInfoTemp = null;
            for (MstPromotermInfo item : promTermLst) {
                if (!ConstValues.FLAG_1.equals(item.getDeleteflag()) && promoIdLst.contains(item.getPtypekey())) {
                    promotermInfoTemp = new MstPromotermInfoTemp();
                    promotermInfoTemp.setPtypekey(item.getPtypekey());
                    promotermInfoTemp.setTerminalkey(item.getTerminalkey());
                    if (iscomflag) {// 重复拜访
                        promotermInfoTemp.setIsaccomplish(item.getIsaccomplish());// 是否达成
                        promotermInfoTemp.setRemarks(item.getRemarks());// 达成数组
                    } else {
                        promotermInfoTemp.setIsaccomplish("0");// 第一次拜访默认 未达成
                        promotermInfoTemp.setRemarks(null);
                    }
                    promotermInfoTemp.setCreuser(item.getCreuser());
                    promotermInfoTemp.setUpdateuser(item.getUpdateuser());

                    promotermInfoTemp.setRecordkey(FunUtil.getUUID());
                    promotermInfoTemp.setVisitkey(visitMTemp.getVisitkey());
                    promotermInfoTemp.setStartdate(visitDate);
                    promotermInfoTemp.setSisconsistent(ConstValues.FLAG_0);
                    promotermInfoTemp.setScondate(null);
                    promotermInfoTemp.setPadisconsistent(ConstValues.FLAG_0);
                    promotermInfoTemp.setPadcondate(null);
                    promotermInfoTemp.setDeleteflag(ConstValues.FLAG_0);
                    promotermInfoTemp.setCredate(null);
                    promotermInfoTemp.setUpdatetime(null);
                    promTempDao.create(promotermInfoTemp);
                }
            }
        }
    }

    /**
     * 插入非关联产品指标表
     *
     * @param prevVisitId  上次拜访主键key
     * @param valueDao
     * @param valueTempDao
     * @throws SQLException
     */
    private void createMstCheckexerecordInfoTemp1(String prevVisitId, String visitDate,
                                                  MstVisitMTemp visitMTemp, Dao<MstCheckexerecordInfo, String> valueDao,
                                                  Dao<MstCheckexerecordInfoTemp, String> valueTempDao)
            throws SQLException {
        // 复制指标结果表，MST_CHECKEXERECORD_INFO(拜访指标执行记录表)
        QueryBuilder<MstCheckexerecordInfo, String> valueQb = valueDao.queryBuilder();
        Where<MstCheckexerecordInfo, String> valueWr = valueQb.where();
        valueWr.eq("visitkey", prevVisitId);
        valueWr.and();
        valueWr.isNull("productkey");
        List<MstCheckexerecordInfo> valueLst = valueQb.query();
        if (!CheckUtil.IsEmpty(valueLst)) {
            MstCheckexerecordInfoTemp itemTemp;
            for (MstCheckexerecordInfo item : valueLst) {
                itemTemp = new MstCheckexerecordInfoTemp();
                itemTemp.setRecordkey(FunUtil.getUUID());
                itemTemp.setVisitkey(visitMTemp.getVisitkey());
                itemTemp.setProductkey(item.getProductkey());
                itemTemp.setCheckkey(item.getCheckkey());
                itemTemp.setAcresult(item.getAcresult());
                itemTemp.setStartdate(item.getStartdate());
                itemTemp.setEnddate(visitDate);
                itemTemp.setTerminalkey(item.getTerminalkey());
                valueTempDao.create(itemTemp);
            }
        }
    }

    /**
     * 插入产品指标表
     *
     * @param prevVisitId  上次拜访主键key
     * @param visitDate
     * @param visitMTemp
     * @param valueDao
     * @param valueTempDao
     * @throws SQLException
     */
    private void createMstCheckexerecordInfoTemp2(String prevVisitId, String visitDate,
                                                  MstVisitMTemp visitMTemp, Dao<MstCheckexerecordInfo, String> valueDao,
                                                  Dao<MstCheckexerecordInfoTemp, String> valueTempDao)
            throws SQLException {
        // 复制指标结果表，MST_CHECKEXERECORD_INFO(拜访指标执行记录表)
        QueryBuilder<MstCheckexerecordInfo, String> valueQb = valueDao.queryBuilder();
        Where<MstCheckexerecordInfo, String> valueWr = valueQb.where();
        valueWr.eq("terminalkey", visitMTemp.getTerminalkey());
        valueWr.and();
        valueWr.eq("enddate", "30001201");
        valueWr.and();
        valueWr.isNotNull("productkey");
        valueWr.and();
        valueWr.ne("deleteflag", ConstValues.delFlag);

        valueQb.orderBy("terminalkey", true);
        valueQb.orderBy("checkkey", true);
        valueQb.orderBy("productkey", true);
        valueQb.orderBy("updatetime", false);
        valueQb.orderBy("siebelid", false);
        valueQb.orderBy("acresult", false);
        List<MstCheckexerecordInfo> valueLst = valueQb.query();
        Map<String, MstCheckexerecordInfo> map = new HashMap<String, MstCheckexerecordInfo>();
        if (!CheckUtil.IsEmpty(valueLst)) {
            MstCheckexerecordInfoTemp itemTemp;
            for (MstCheckexerecordInfo item : valueLst) {
                String key = item.getTerminalkey() + "-" + item.getCheckkey() + "-" + item.getProductkey();
                if (!map.containsKey(key)) {
                    map.put(key, item);
                    itemTemp = new MstCheckexerecordInfoTemp();
                    itemTemp.setRecordkey(FunUtil.getUUID());
                    itemTemp.setVisitkey(visitMTemp.getVisitkey());
                    itemTemp.setProductkey(item.getProductkey());
                    itemTemp.setCheckkey(item.getCheckkey());
                    itemTemp.setAcresult(item.getAcresult());
                    itemTemp.setStartdate(item.getStartdate());
                    itemTemp.setEnddate(visitDate);
                    itemTemp.setTerminalkey(item.getTerminalkey());
                    valueTempDao.create(itemTemp);
                } else {
                    item.setDeleteflag(ConstValues.delFlag);
                    item.setSisconsistent(ConstValues.FLAG_0);
                    item.setPadisconsistent(ConstValues.FLAG_0);
                    item.setResultstatus(new BigDecimal(1));
                    valueDao.update(item);
                }
            }
        }
    }

    // 复制采集项表
    private void createMstCollectionexerecordInfoTemp(Dao<MstCollectionexerecordInfo, String> collectionDao,
                                                      Dao<MstCollectionexerecordInfoTemp, String> collectionTempDao,
                                                      Dao<PadCheckaccomplishInfo,String> padCheckaccomplishInfoDao,
                                                      String prevVisitId,XtTermSelectMStc termStc,MstVisitMTemp visitMTemp) {
        try {
            // 复制指标结果表，MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)
            QueryBuilder<MstCollectionexerecordInfo, String> collectionQB = collectionDao.queryBuilder();
            Where<MstCollectionexerecordInfo, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", prevVisitId);
            collectionWhere.and();
            collectionWhere.ne("deleteflag", ConstValues.FLAG_1);
            collectionWhere.and();
            collectionWhere.isNotNull("checkkey");
            collectionWhere.and();
            collectionWhere.ne("checkkey", "");
            collectionQB.orderBy("productkey", true);
            collectionQB.orderBy("colitemkey", true);
            collectionQB.orderBy("checkkey", false);
            collectionQB.orderBy("updatetime", false);
            List<MstCollectionexerecordInfo> collectionList = collectionQB.query();

            if (!CheckUtil.IsEmpty(collectionList)) {
                //数据库查询
                //根据渠道查询拜访指标执行采集项记录表,按采集项+产品放入map
                QueryBuilder<PadCheckaccomplishInfo, String> valueQb = padCheckaccomplishInfoDao.queryBuilder();
                Where<PadCheckaccomplishInfo, String> valueWr = valueQb.where();
                valueWr.eq("minorchannel", termStc.getMinorchannel());
                List<PadCheckaccomplishInfo> valueLst = valueQb.query();
                Map<String, String> colitemProductkeys = new HashMap<String, String>();
                if (valueLst != null) {
                    for (int i = 0; i < valueLst.size(); i++) {
                        colitemProductkeys.put(valueLst.get(i).getColitemkey() + valueLst.get(i).getProductkey(), "");
                    }
                }

                List<String> list = new ArrayList<String>();
                //根据有效的采集项和产品进行复制
                for (MstCollectionexerecordInfo item : collectionList) {
                    String key = item.getColitemkey() + item.getProductkey();
                    MstCollectionexerecordInfoTemp mstCollectionexerecordInfoTemp = null;
                    if (colitemProductkeys.containsKey(key)) {//此产品存在于指标采集项模板
                        if (!list.contains(key)) {//防止重复去重
                            list.add(key);
                            mstCollectionexerecordInfoTemp = new MstCollectionexerecordInfoTemp();
                            mstCollectionexerecordInfoTemp.setColrecordkey(FunUtil.getUUID());
                            mstCollectionexerecordInfoTemp.setVisitkey(visitMTemp.getVisitkey());

                            mstCollectionexerecordInfoTemp.setProductkey(item.getProductkey());
                            mstCollectionexerecordInfoTemp.setCheckkey(item.getCheckkey());
                            mstCollectionexerecordInfoTemp.setColitemkey(item.getColitemkey());
                            mstCollectionexerecordInfoTemp.setAddcount(item.getAddcount());
                            mstCollectionexerecordInfoTemp.setTotalcount(item.getTotalcount());
                            mstCollectionexerecordInfoTemp.setComid(item.getComid());
                            mstCollectionexerecordInfoTemp.setRemarks(item.getRemarks());
                            mstCollectionexerecordInfoTemp.setFreshness(item.getFreshness());// 新鲜度
                            mstCollectionexerecordInfoTemp.setOrderbyno(item.getOrderbyno());
                            mstCollectionexerecordInfoTemp.setVersion(item.getVersion());
                            mstCollectionexerecordInfoTemp.setCreuser(item.getCreuser());
                            mstCollectionexerecordInfoTemp.setUpdateuser(item.getUpdateuser());
                            mstCollectionexerecordInfoTemp.setBianhualiang(item.getBianhualiang());//变化量 (用于最后上传时 现有量变化量必须填值,才能上传)
                            mstCollectionexerecordInfoTemp.setXianyouliang(item.getXianyouliang());// 现有量 (用于最后上传时 现有量变化量必须填值,才能上传)

                            mstCollectionexerecordInfoTemp.setSisconsistent(ConstValues.FLAG_0);
                            mstCollectionexerecordInfoTemp.setScondate(null);
                            mstCollectionexerecordInfoTemp.setPadisconsistent(ConstValues.FLAG_0);
                            mstCollectionexerecordInfoTemp.setPadcondate(null);
                            mstCollectionexerecordInfoTemp.setDeleteflag(ConstValues.FLAG_0);
                            mstCollectionexerecordInfoTemp.setCredate(null);
                            mstCollectionexerecordInfoTemp.setUpdatetime(null);
                            collectionTempDao.create(mstCollectionexerecordInfoTemp);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 复制产品组合是否达标临时表
    private void createMstGroupproductMTemp(MstTerminalinfoMTemp termTemp,String visitkey) {
        // 从产品组合是否达标表 搂取该终端的一条数据,今天之前的包含今天 判断该终端产品组合是否达标
        // 先查询MstGroupproductM表  判断终端该指标是否达标
        List<MstGroupproductM> listvo = queryMstGroupproductM(termTemp.getTerminalcode(), (DateUtil.getDateTimeStr(7) + "  00:00:00"));

        MstGroupproductM vo = null;
        MstGroupproductMTemp voTemp = new MstGroupproductMTemp();

        // 有上次数据
        if (listvo.size() > 0) {
            // 数据是今天创建的
            if ((DateUtil.getDateTimeStr(7) + "  00:00:00").equals(listvo.get(0).getStartdate())) {
                vo = listvo.get(0);
                voTemp.setGproductid(vo.getGproductid());
                voTemp.setTerminalcode(vo.getTerminalcode());
                voTemp.setTerminalname(vo.getTerminalname());
                voTemp.setIfrecstand(vo.getIfrecstand());// 取上次的
                voTemp.setStartdate(vo.getStartdate());
                voTemp.setEnddate(vo.getEnddate());
                voTemp.setCreateusereng(vo.getCreateusereng());
                voTemp.setCreatedate(vo.getCreatedate());
                voTemp.setUpdateusereng(vo.getUpdateusereng());
                voTemp.setUpdatetime(vo.getUpdatetime());
                voTemp.setUpdateusereng(vo.getUpdateusereng());
                voTemp.setUploadFlag("0");// 不上传
                voTemp.setPadisconsistent("0");// 未上传
                voTemp.setVisitkey(visitkey);
                createMstGroupproductM(voTemp);
                // 之前创建的一条数据
            } else {
                vo = listvo.get(0);// 先复制再重新赋值
                voTemp.setGproductid(FunUtil.getUUID());
                voTemp.setTerminalcode(termTemp.getTerminalcode());
                voTemp.setTerminalname(termTemp.getTerminalname());
                voTemp.setIfrecstand(vo.getIfrecstand());// 取上次的
                voTemp.setStartdate(DateUtil.getDateTimeStr(7) + "  00:00:00");
                voTemp.setEnddate("3000-12-01" + "  00:00:00");
                voTemp.setCreateusereng(PrefUtils.getString(context, "userGongHao", "21000"));
                voTemp.setCreatedate(DateUtil.getDateTimeStr(6));
                voTemp.setUpdateusereng(PrefUtils.getString(context, "userGongHao", "21000"));
                voTemp.setUpdatetime(DateUtil.getDateTimeStr(6));
                voTemp.setUpdateusereng(PrefUtils.getString(context, "userGongHao", "21000"));
                voTemp.setUploadFlag("0");// 不上传
                voTemp.setPadisconsistent("0");// 未上传
                voTemp.setVisitkey(visitkey);
                createMstGroupproductM(voTemp);
            }
        }
        // 没有上次数据
        else {
            // 插入一条今天新数据
            voTemp.setGproductid(FunUtil.getUUID());
            voTemp.setTerminalcode(termTemp.getTerminalcode());
            voTemp.setTerminalname(termTemp.getTerminalname());
            voTemp.setIfrecstand("N");// 未达标
            voTemp.setStartdate(DateUtil.getDateTimeStr(7) + "  00:00:00");
            voTemp.setEnddate("3000-12-01" + "  00:00:00");
            voTemp.setCreateusereng(PrefUtils.getString(context, "userGongHao", "20000"));
            voTemp.setCreatedate(DateUtil.getDateTimeStr(6));
            voTemp.setUpdateusereng(PrefUtils.getString(context, "userGongHao", "20000"));
            voTemp.setUpdatetime(DateUtil.getDateTimeStr(6));
            voTemp.setUpdateusereng(PrefUtils.getString(context, "userGongHao", "20000"));
            voTemp.setUploadFlag("0");// 不上传
            voTemp.setPadisconsistent("0");// 未上传
            voTemp.setVisitkey(visitkey);
            createMstGroupproductM(voTemp);
        }
    }

    /**
     * @param groupproductMTemp
     */
    public void createMstGroupproductM(MstGroupproductMTemp groupproductMTemp) {
        //AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstGroupproductMTemp, String> indexValueDao = helper.getMstGroupproductMTempDao();

            //connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            //connection.setAutoCommit(false);

            indexValueDao.create(groupproductMTemp);
            //connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取失败", e);
        }
    }

    /**
     * 通过terminalcode查询MstGroupproductM表
     *
     * @return
     */
    public List<MstGroupproductM> queryMstGroupproductM(String terminalcode, String startdate) {
        // 获取分项采集数据
        List<MstGroupproductM> stcLst = new ArrayList<MstGroupproductM>();
        //AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstGroupproductMDao dao = helper.getDao(MstGroupproductM.class);
            //MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            //connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            //connection.setAutoCommit(false);
            stcLst = dao.queryMstGroupproductMByCreatedate(helper, terminalcode, startdate);
            //connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取失败", e);
        }
        return stcLst;
    }

    /**
     * 获取终端临时表 记录信息
     *
     * @param termId 终端ID
     * @return
     */
    public MstTerminalinfoMTemp findTermTempById(String termId) {

        MstTerminalinfoMTemp termInfo = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMTempDao dao = helper.getDao(MstTerminalinfoMTemp.class);
            termInfo = dao.queryForId(termId);

        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }

        return termInfo;
    }

    /**
     * 获取拜访临时表信息
     *
     * @param visitId    拜访请表信息ID
     * @return
     */
    public MstVisitMTemp findVisitTempById(String visitId) {

        MstVisitMTemp visitInfo = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMTempDao dao = helper.getDao(MstVisitMTemp.class);
            visitInfo = dao.queryForId(visitId);

        } catch (SQLException e) {
            Log.e(TAG, "获取拜访主表DAO对象失败", e);
        }
        return visitInfo;
    }

    // 删除临时表数据
    public void deleteData() {
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        try {
            deleteTable(helper, "MST_VISIT_M_TEMP");
            //deleteTable(helper, "MST_TERMINALINFO_M_TEMP");
            deleteTable(helper, "MST_AGENCYSUPPLY_INFO_TEMP");
            deleteTable(helper, "MST_VISTPRODUCT_INFO_TEMP");
            deleteTable(helper, "MST_CMPSUPPLY_INFO_TEMP");
            deleteTable(helper, "MST_CHECKEXERECORD_INFO_TEMP");
            deleteTable(helper, "MST_COLLECTIONEXERECORD_INFO_TEMP");
            deleteTable(helper, "MST_PROMOTERM_INFO_TEMP");
            deleteTable(helper, "MST_CAMERAINFO_M_TEMP");
            deleteTable(helper, "MST_VISITMEMO_INFO_TEMP");
            deleteTable(helper, "MST_GROUPPRODUCT_M_TEMP");
            //deleteTable(helper, "MST_CHECKGROUP_INFO");
            //deleteTable(helper, "MST_CHECKGROUP_INFO_TEMP");
        } catch (Exception e) {
            Log.e(TAG, "删除临时表数据失败", e);
        }
    }

    public static void deleteTable(DatabaseHelper dbHelper, String tabname) {
        String sql = "DELETE FROM " + tabname + ";";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sql);
    }

    //---------------------------------------------
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

            stcLst = dao.queryCalculateIndexTemp(helper, visitId, termId, channelId, seeFlag);

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
            stcLst = dao.queryCalculateItemTemp(helper, visitId, channelId);

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

    /**
     * 获取巡店拜访-查指标的分项采集部分的与产品无关的指标数据
     *
     * @param visitId   拜访ID
     * @param channelId 本次拜访终端的次渠道ID
     * @param seeFlag   查看操作标识
     * @return
     */
    public List<XtCheckIndexCalculateStc> queryNoProIndex2(
            String visitId, String channelId, String seeFlag) {
        // 获取分项采集数据
        List<XtCheckIndexCalculateStc> stcLst = new ArrayList<XtCheckIndexCalculateStc>();
        stcLst.add(new XtCheckIndexCalculateStc("666b74b3-b221-4920-b549-d9ec39a463fd", "1", "合作执行是否到位", "8d36d1e5-c776-452e-8893-589ad786d71d"));
        stcLst.add(new XtCheckIndexCalculateStc("59802090-02ac-4146-9cc3-f09570c36a26", "4", "我品单店占有率", "eeffb1af-51c0-4954-98de-2cc62043e4d2"));
        stcLst.add(new XtCheckIndexCalculateStc("df2e88c9-246f-40e2-b6e5-08cdebf8c281", "1", "是否高质量配送", "bf600cfe-f70d-4170-857d-65dd59740d57"));

        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);

            MstCheckexerecordInfoDao dao = helper.getDao(MstCheckexerecordInfo.class);
            Dao<MstCheckexerecordInfoTemp, String> CheckexerecordInfoTempdao = helper.getMstCheckexerecordInfoTempDao();

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            //
            if (ConstValues.FLAG_1.equals(seeFlag)) {//查看模式

                List<MstCheckexerecordInfo> Checkexerecordlist = dao.queryForEq("visitkey", visitId);
                for (MstCheckexerecordInfo mstCheckexerecordInfo : Checkexerecordlist) {
                    for (XtCheckIndexCalculateStc CheckIndexCalculate : stcLst) {
                        if (mstCheckexerecordInfo.getCheckkey().equals(CheckIndexCalculate.getIndexId())) {
                            CheckIndexCalculate.setVisitId(mstCheckexerecordInfo.getVisitkey());
                            CheckIndexCalculate.setRecordId(mstCheckexerecordInfo.getRecordkey());
                            CheckIndexCalculate.setIndexValueId(mstCheckexerecordInfo.getAcresult());
                        }
                    }
                }
            } else {
                List<MstCheckexerecordInfoTemp> Checkexerecordlist = CheckexerecordInfoTempdao.queryForEq("visitkey", visitId);
                for (MstCheckexerecordInfoTemp mstCheckexerecordInfo : Checkexerecordlist) {
                    for (XtCheckIndexCalculateStc CheckIndexCalculate : stcLst) {
                        if (mstCheckexerecordInfo.getCheckkey().equals(CheckIndexCalculate.getIndexId())) {
                            CheckIndexCalculate.setVisitId(mstCheckexerecordInfo.getVisitkey());
                            CheckIndexCalculate.setRecordId(mstCheckexerecordInfo.getRecordkey());
                            if (mstCheckexerecordInfo.getAcresult() != null) {
                                // 如果上次拜访不为空,此指标取上次拜访的值 // 修改新终端进入时,合作状态为空的bug 20160809
                                CheckIndexCalculate.setIndexValueId(mstCheckexerecordInfo.getAcresult());
                            }
                        }
                    }
                }
            }

            connection.commit(null);

        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }

        return stcLst;
    }

    /**
     * 保存查指标页面数据
     *
     * @param visitId       拜访主键
     * @param termId        终端ID,终端key,
     * @param calculateLst  指标、指标值记录结果
     * @param proItemLst    产品、采集项记录结果
     * @param noProIndexLst 与产品无关的指标采集值
     */
    public void saveCheckIndex(String visitId, String termId, List<XtProIndex> calculateLst,
                               List<XtProItem> proItemLst, List<XtCheckIndexCalculateStc> noProIndexLst) {
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstCheckexerecordInfoTemp, String> indexValueDao = helper.getMstCheckexerecordInfoTempDao();
            Dao<MstCollectionexerecordInfo, String> proItemDao = helper.getMstCollectionexerecordInfoDao();
            Dao<MstCollectionexerecordInfoTemp, String> proItemTempDao = helper.getMstCollectionexerecordInfoTempDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            // 拜访指标执行记录表更新语句及参数
            String[] args = new String[3];
            args[0] = visitId;
            StringBuffer buffer = new StringBuffer();
            buffer.append("update mst_checkexerecord_info_temp set ");
            buffer.append("visitkey = ?, acresult = ?, padisconsistent = '0' ");
            buffer.append("where recordkey = ? ");

            // 保存拜访指标执行记录表
            MstCheckexerecordInfoTemp indexInfo;
            for (XtProIndex indexItem : calculateLst) {
                for (XtProIndexValue valueItem : indexItem.getIndexValueLst()) {

                    // 如果true,是新增
                    if (CheckUtil.isBlankOrNull(valueItem.getIndexResultId())) {
                        valueItem.setIndexResultId(FunUtil.getUUID());
                        indexInfo = new MstCheckexerecordInfoTemp();
                        indexInfo.setRecordkey(valueItem.getIndexResultId());
                        indexInfo.setVisitkey(visitId);
                        indexInfo.setProductkey(valueItem.getProId());
                        indexInfo.setCheckkey(valueItem.getIndexId());
                        indexInfo.setChecktype(valueItem.getIndexType());
                        indexInfo.setAcresult(valueItem.getIndexValueId());
                        indexInfo.setIscom(ConstValues.FLAG_0);
                        indexInfo.setTerminalkey(termId);
                        indexInfo.setPadisconsistent(ConstValues.FLAG_0);
                        indexInfo.setDeleteflag(ConstValues.FLAG_0);
                        //indexInfo.setCreuser(ConstValues.loginSession.getUserCode());
                        indexInfo.setCreuser(PrefUtils.getString(context, "userCode", ""));
                        //indexInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
                        indexInfo.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                        indexValueDao.create(indexInfo);

                        // 否则更新
                    } else {
                        args[1] = valueItem.getIndexValueId();
                        args[2] = valueItem.getIndexResultId();
                        indexValueDao.executeRaw(buffer.toString(), args);
                    }
                }
            }

            // 清楚本地mst_collectionexerecord_info 的数据， 并插入新的数据
            proItemDao.executeRaw("delete from mst_collectionexerecord_info_temp where visitkey = ?", visitId);

            // 保产品及对应的采集项数据
            Map<String, Integer> proStockMap = new HashMap<String, Integer>();
            int currNum, changeNum;
            MstCollectionexerecordInfoTemp itemInfo;
            for (XtProItem proItem : proItemLst) {

                for (String chekcKey : proItem.getIndexIdLst()) {

                    proItem.setColRecordKey(FunUtil.getUUID());
                    itemInfo = new MstCollectionexerecordInfoTemp();
                    itemInfo.setColrecordkey(proItem.getColRecordKey());
                    itemInfo.setVisitkey(visitId);
                    itemInfo.setProductkey(proItem.getProId());
                    itemInfo.setCheckkey(chekcKey);
                    itemInfo.setColitemkey(proItem.getItemId());
                    itemInfo.setAddcount(proItem.getChangeNum());//
                    itemInfo.setTotalcount(proItem.getFinalNum());
                    itemInfo.setBianhualiang(proItem.getBianhualiang());
                    itemInfo.setXianyouliang(proItem.getXianyouliang());
                    //itemInfo.setFreshness(proItem.getFreshness());// 新鲜度
                    itemInfo.setFreshness(FunUtil.isDateString(proItem.getFreshness()));// 新鲜度
                    itemInfo.setPadisconsistent(ConstValues.FLAG_0);
                    itemInfo.setDeleteflag(ConstValues.FLAG_0);
                    //itemInfo.setCreuser(ConstValues.loginSession.getUserCode());
                    itemInfo.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    //itemInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
                    itemInfo.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                    proItemTempDao.create(itemInfo);
                }

                // 记录每个产品的现有库存
                if (PropertiesUtil.getProperties("checkItem_stock", "").equals(proItem.getItemId())
                        || PropertiesUtil.getProperties("checkItem_stock1", "").equals(proItem.getItemId())) {
                    currNum = proItem.getFinalNum() == null ? 0 : proItem.getFinalNum().intValue();
                    changeNum = proItem.getChangeNum() == null ? 0 : proItem.getChangeNum().intValue();
                    proStockMap.put(proItem.getProId(), currNum + changeNum);
                }
            }

            // 更新我品的现有库存
            buffer = new StringBuffer();
            buffer.append("update mst_vistproduct_info_temp set currnum = ?,padisconsistent = '0' ");
            buffer.append("where productkey = ? and visitkey = ? ");
            args = new String[3];
            args[2] = visitId;
            for (String proId : proStockMap.keySet()) {
                args[0] = proStockMap.get(proId).toString();
                args[1] = proId;
                proItemDao.executeRaw(buffer.toString(), args);
            }

            // 保存或更新与产品无关的指标的指标值数据
            args = new String[3];
            args[0] = visitId;
            buffer = new StringBuffer();
            buffer.append("update mst_checkexerecord_info_temp set  ");
            buffer.append("visitkey = ?, acresult = ? ,padisconsistent = '0' ");
            buffer.append("where recordkey = ? ");

            String currDate = DateUtil.formatDate(new Date(), "yyyyMMdd");
            // 保存拜访指标执行记录表
            for (XtCheckIndexCalculateStc itemStc : noProIndexLst) {

                // 如果true,是新增
                if (!visitId.equals(itemStc.getVisitId())) {
                    itemStc.setVisitId(visitId);
                    indexInfo = new MstCheckexerecordInfoTemp();
                    indexInfo.setRecordkey(FunUtil.getUUID());
                    indexInfo.setVisitkey(visitId);
                    indexInfo.setStartdate(currDate);
                    indexInfo.setEnddate(currDate);
                    indexInfo.setCheckkey(itemStc.getIndexId());
                    indexInfo.setChecktype(itemStc.getIndexType());
                    indexInfo.setAcresult(itemStc.getIndexValueId());
                    indexInfo.setIscom(ConstValues.FLAG_0);
                    indexInfo.setTerminalkey(termId);
                    indexInfo.setPadisconsistent(ConstValues.FLAG_0);
                    indexInfo.setDeleteflag(ConstValues.FLAG_0);
                    //indexInfo.setCreuser(ConstValues.loginSession.getUserCode());
                    indexInfo.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    //indexInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
                    indexInfo.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                    indexValueDao.create(indexInfo);

                    // 否则更新
                } else {
                    args[1] = itemStc.getIndexValueId();
                    args[2] = itemStc.getRecordId();
                    indexValueDao.executeRaw(buffer.toString(), args);
                }
            }

            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "保存进销存数据发生异常", e);
            ViewUtil.sendMsg(context, R.string.checkindex_save_fail);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                Log.e(TAG, "回滚进销存数据发生异常", e1);
            }
        }
    }

    /**
     * 获取协同拜访-查指标的促销活动页面部分的数据
     *
     * @param //prevVisitId 拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param termLevel     本次拜访终端的终端类型ID(终端等级)
     * @return
     */
    public List<CheckIndexPromotionStc> queryPromotion(String visitId, String channelId, String termLevel) {
        // 获取分项采集数据
        List<CheckIndexPromotionStc> stcLst = new ArrayList<CheckIndexPromotionStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstPromotionsmDao dao = helper.getDao(MstPromotionsM.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            stcLst = dao.queryXtPromotionByterm(helper, visitId, channelId, termLevel);
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }

        // 处理组合进店的情况
        List<CheckIndexPromotionStc> tempLst = new ArrayList<CheckIndexPromotionStc>();
        String promotionKey = "";
        CheckIndexPromotionStc itemStc = new CheckIndexPromotionStc();
        for (CheckIndexPromotionStc item : stcLst) {
            if (!promotionKey.equals(item.getPromotKey())) {
                promotionKey = item.getPromotKey();
                itemStc = item;
                tempLst.add(itemStc);
            } else {
                itemStc.setProId(itemStc.getProId() + "," + item.getProId());
                itemStc.setProName(itemStc.getProName() + "\n" + item.getProName());
            }

        }

        return tempLst;
    }

}
