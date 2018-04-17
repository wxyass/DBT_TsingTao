package et.tsingtaopad.dd.ddxt.updata;

import android.content.Context;
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
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitVisitMDao;
import et.tsingtaopad.db.dao.MstAgencysupplyInfoDao;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.dao.MstGroupproductMDao;
import et.tsingtaopad.db.dao.MstGroupproductMTempDao;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMCartDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMTempDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.dao.MstVisitMTempDao;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.table.MitAgencysupplyInfo;
import et.tsingtaopad.db.table.MitCameraInfoM;
import et.tsingtaopad.db.table.MitCheckexerecordInfo;
import et.tsingtaopad.db.table.MitCmpsupplyInfo;
import et.tsingtaopad.db.table.MitCollectionexerecordInfo;
import et.tsingtaopad.db.table.MitGroupproductM;
import et.tsingtaopad.db.table.MitPromotermInfo;
import et.tsingtaopad.db.table.MitTerminalinfoM;
import et.tsingtaopad.db.table.MitVisitM;
import et.tsingtaopad.db.table.MitVistproductInfo;
import et.tsingtaopad.db.table.MstAgencysupplyInfo;
import et.tsingtaopad.db.table.MstAgencysupplyInfoTemp;
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
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMCart;
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
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexQuicklyStc;


/**
 * 将临时表数据 copy到协同表
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtShopCopyService {

    private final String TAG = "XtShopVisitService";

    protected Context context;
    protected Handler handler;

	private String prevVisitId;// 获取上次拜访主键

	private String prevVisitDate; // 上次拜访日期

    public XtShopCopyService(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    /**
     * 更新拜访离店时间及是否要上传标志
     *
     * @param visitId      拜访主键
     * @param termId       终端主键
     * @param visitEndDate 离店时间
     * @param uploadFlag   是否要上传标志
     */
    public void copyXtUpload(String visitId, String termId, String terminalcode,String visitEndDate, String uploadFlag) {
        // 事务控制
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMDao dao = helper.getDao(MstVisitM.class);
            MstVisitMTempDao visitTempDao = helper.getDao(MstVisitMTemp.class);
            MitVisitMDao mitVisitMDao = helper.getDao(MitVisitM.class);

            // 获取当前拜访的指标结果记录情况
            Dao<MstCheckexerecordInfoTemp, String> tempDao = helper.getMstCheckexerecordInfoTempDao();
            Dao<MstCheckexerecordInfo, String> checkInfoDao = helper.getMstCheckexerecordInfoDao();
            Dao<MitCheckexerecordInfo, String> mitCheckInfoDao = helper.getMitCheckexerecordInfoDao();

            Dao<MstCollectionexerecordInfo, String> collectionDao = helper.getMstCollectionexerecordInfoDao();
            Dao<MstCollectionexerecordInfoTemp, String> collectionTempDao = helper.getMstCollectionexerecordInfoTempDao();
            Dao<MitCollectionexerecordInfo, String> mitCollectionDao = helper.getMitCollectionexerecordInfoDao();

            Dao<MstCameraInfoM, String> cameraDao = helper.getMstCameraiInfoMDao();
            Dao<MstCameraInfoMTemp, String> cameraTempDao = helper.getMstCameraInfoMTempDao();
            Dao<MitCameraInfoM, String> mitCameraDao = helper.getMitCameraInfoMDao();

            Dao<MstPromotermInfo, String> promDao = helper.getMstPromotermInfoDao();
            Dao<MstPromotermInfoTemp, String> promTempDao = helper.getMstPromotermInfoTempDao();
            Dao<MitPromotermInfo, String> mitPromotermDao = helper.getMitPromotermInfoDao();

            Dao<MstVistproductInfo, String> proDao = helper.getMstVistproductInfoDao();
            Dao<MstVistproductInfoTemp, String> proTempDao = helper.getMstVistproductInfoTempDao();
            Dao<MitVistproductInfo, String> mitVistproductDao = helper.getMitVistproductInfoDao();

            Dao<MstTerminalinfoM, String> terminalinfoMDao = helper.getMstTerminalinfoMDao();
            Dao<MstTerminalinfoMTemp, String> terminalinfoMTempDao = helper.getMstTerminalinfoMTempDao();
            Dao<MitTerminalinfoM, String> mitTerminalDao = helper.getMitTerminalinfoMDao();

            Dao<MstAgencysupplyInfo, String> agencyDao = helper.getMstAgencysupplyInfoDao();
            Dao<MstAgencysupplyInfoTemp, String> agencyTempDao = helper.getMstAgencysupplyInfoTempDao();
            Dao<MitAgencysupplyInfo, String> mitAgencysupplyDao = helper.getMitAgencysupplyInfoDao();

            Dao<MstCmpsupplyInfo, String> cmpSupplyDao = helper.getMstCmpsupplyInfoDao();
            Dao<MstCmpsupplyInfoTemp, String> cmpSupplyTempDao = helper.getMstCmpsupplyInfoTempDao();
            Dao<MitCmpsupplyInfo, String> mitCmpsupplyDao = helper.getMitCmpsupplyInfoDao();


            Dao<MstGroupproductM, String> groupproDao = helper.getMstGroupproductMDao();
            Dao<MstGroupproductMTemp, String> groupproTempDao = helper.getMstGroupproductMTempDao();
            Dao<MitGroupproductM, String> mitGroupproMDao = helper.getMitGroupproductMDao();


            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            MstVisitMTemp visitTemp = visitTempDao.queryForId(visitId);
            // 复制拜访主表
            //createMstVisitMByMstVisitMTemp(dao,mitVisitMDao, visitTemp);
            createMitVisitMByMstVisitMTemp(dao,mitVisitMDao, visitTemp);

            // 复制拉链表  如果结束拜访, 则生成最新的终端指标结果记录表
            //createMstcheckexerecordinfoByMstCheckexerecordInfoTemp(checkInfoDao, tempDao, visitId, termId, visitEndDate);
            createMitcheckexerecordByMstCheckexerecordTemp(checkInfoDao,mitCheckInfoDao, tempDao, visitId, termId, visitEndDate);
            // 复制采集项表
            //createMstCollectionexerecordInfoByMstCollectionexerecordInfoTemp(collectionDao,collectionTempDao,visitId);
            createMitCollectionexerecordByMstCollectionexerecordTemp(collectionDao,collectionTempDao,mitCollectionDao,visitId);
            // 复制图片表
            //createMstCameraInfoMByMstCameraInfoMTemp(cameraDao,cameraTempDao, visitId, termId, visitEndDate);
            createMitCameraInfoMByMstCameraInfoMTemp(cameraDao,cameraTempDao,mitCameraDao, visitId, termId, visitEndDate);

            // 复制促销活动终端表
            //createMstPromotermInfoByMstPromotermInfoTemp(promDao,promTempDao, visitId, termId, visitEndDate);
            createMitPromotermInfoByMstPromotermInfoTemp(promDao,promTempDao,mitPromotermDao, visitId, termId, visitEndDate);

            // 拜访产品表
            //createMstVistproductInfoByMstVistproductInfoTemp(proDao,proTempDao, visitId, termId, visitEndDate);
            createMitVistproductInfoByMstVistproductInfoTemp(proDao,proTempDao,mitVistproductDao, visitId, termId, visitEndDate);

            // 复制终端表
            //createMstTerminalinfoMByMstTerminalinfoMTemp(terminalinfoMDao,terminalinfoMTempDao, visitId, termId, visitEndDate);
            createMitTerminalinfoMByMstTerminalinfoMTemp(terminalinfoMDao,terminalinfoMTempDao,mitTerminalDao, visitId, termId, visitEndDate);

            // 复制我品供货关系表
            //createMstAgencysupplyInfoByMstAgencysupplyInfoTemp(agencyDao,agencyTempDao, visitId, termId, visitEndDate);
            createMitAgencysupplyInfoByMstAgencysupplyInfoTemp(agencyDao,agencyTempDao,mitAgencysupplyDao, visitId, termId, visitEndDate);

            // 复制竞品供货关系表
            //createMstCmpsupplyInfoByMstCmpsupplyInfoTemp(cmpSupplyDao,cmpSupplyTempDao, visitId, termId, visitEndDate);
            createMitCmpsupplyInfoByMstCmpsupplyInfoTemp(cmpSupplyDao,cmpSupplyTempDao,mitCmpsupplyDao, visitId, termId, visitEndDate);

            // 复制产品组合是否达标表
            //createMstGroupproductMByMstGroupproductMTemp(groupproDao,groupproTempDao, visitId, termId,terminalcode, visitEndDate);
            createMitGroupproductMByMstGroupproductMTemp(groupproDao,groupproTempDao,mitGroupproMDao, visitId, termId,terminalcode, visitEndDate);

            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "更新拜访离店时间及是否要上传标志失败", e);
            try {
                connection.rollback(null);
                ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    // 复制产品组合是否达标表 到业代产品组合是否达标表
    private void createMstGroupproductMByMstGroupproductMTemp(
            Dao<MstGroupproductM, String> groupproDao,
            Dao<MstGroupproductMTemp, String> groupproTempDao,
            String visitId, String termId,String terminalcode, String visitEndDate) {

        try {
            QueryBuilder<MstGroupproductMTemp, String> collectionQB = groupproTempDao.queryBuilder();
            Where<MstGroupproductMTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("terminalcode", terminalcode);
            List<MstGroupproductMTemp> mstGroupproductMTemps = collectionQB.query();
            if(mstGroupproductMTemps.size()>0){
                for (MstGroupproductMTemp item:mstGroupproductMTemps) {
                    MstGroupproductM info = new MstGroupproductM();
                    info.setGproductid(item.getGproductid());
                    info.setTerminalcode(item.getTerminalcode());
                    info.setTerminalname(item.getTerminalname());
                    info.setIfrecstand(item.getIfrecstand());
                    info.setStartdate(item.getStartdate());// 默认: 当天时间 yyyy-MM-dd +"  00:00:00"
                    info.setEnddate(item.getEnddate());//  默认: "3000-12-01"+"  00:00:00"
                    info.setCreateusereng(item.getCreateusereng());
                    info.setCreatedate(item.getCreatedate());
                    info.setUpdateusereng(item.getUpdateusereng());
                    info.setVisitkey(item.getVisitkey());
                    info.setUploadflag("1");//  0:该条记录不上传  1:该条记录需要上传 ;
                    info.setPadisconsistent("0");
                    info.setUpdatetime(item.getUpdatetime());

                    groupproDao.createOrUpdate(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到竞品供货关系正式表失败", e);

        }

    }

    // 复制产品组合是否达标表 到协同产品组合是否达标表
    private void createMitGroupproductMByMstGroupproductMTemp(
            Dao<MstGroupproductM, String> groupproDao,
            Dao<MstGroupproductMTemp, String> groupproTempDao,
            Dao<MitGroupproductM, String> mitGroupproMDao,
            String visitId, String termId,String terminalcode, String visitEndDate) {

        try {
            QueryBuilder<MstGroupproductMTemp, String> collectionQB = groupproTempDao.queryBuilder();
            Where<MstGroupproductMTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("terminalcode", terminalcode);
            List<MstGroupproductMTemp> mstGroupproductMTemps = collectionQB.query();
            if(mstGroupproductMTemps.size()>0){
                for (MstGroupproductMTemp item:mstGroupproductMTemps) {
                    MitGroupproductM info = new MitGroupproductM();
                    info.setGproductid(item.getGproductid());
                    info.setTerminalcode(item.getTerminalcode());
                    info.setTerminalname(item.getTerminalname());
                    info.setIfrecstand(item.getIfrecstand());
                    info.setStartdate(item.getStartdate());// 默认: 当天时间 yyyy-MM-dd +"  00:00:00"
                    info.setEnddate(item.getEnddate());//  默认: "3000-12-01"+"  00:00:00"
                    info.setCreateusereng(item.getCreateusereng());
                    info.setCreatedate(item.getCreatedate());
                    info.setUpdateusereng(item.getUpdateusereng());
                    info.setVisitkey(item.getVisitkey());
                    info.setUploadflag("1");//  0:该条记录不上传  1:该条记录需要上传 ;
                    info.setPadisconsistent("0");
                    info.setUpdatetime(item.getUpdatetime());
                    mitGroupproMDao.createOrUpdate(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到竞品供货关系正式表失败", e);

        }

    }

    // 复制竞品供货关系表 到业代竞品供货关系表
    private void createMstCmpsupplyInfoByMstCmpsupplyInfoTemp(
            Dao<MstCmpsupplyInfo, String> cmpSupplyDao,
            Dao<MstCmpsupplyInfoTemp, String> cmpSupplyTempDao, String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstCmpsupplyInfoTemp, String> collectionQB = cmpSupplyTempDao.queryBuilder();
            Where<MstCmpsupplyInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("terminalkey", termId);
            List<MstCmpsupplyInfoTemp> mstCmpsupplyInfoTemps = collectionQB.query();
            if(mstCmpsupplyInfoTemps.size()>0){
                for (MstCmpsupplyInfoTemp item:mstCmpsupplyInfoTemps) {
                    MstCmpsupplyInfo info = new MstCmpsupplyInfo();
                    info.setCmpsupplykey(item.getCmpsupplykey());
                    info.setStatus(item.getStatus());//0 新增未审核 1 有效 2 无效（通过审核设置） 3 申请未审核(有效->无效)
                    info.setInprice(item.getInprice());
                    info.setReprice(item.getReprice());
                    info.setCmpproductkey(item.getCmpproductkey());
                    info.setCmpcomkey(item.getCmpcomkey());
                    info.setTerminalkey(item.getTerminalkey());
                    info.setCmpinvaliddate(item.getCmpinvaliddate());

                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent("0");
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    cmpSupplyDao.createOrUpdate(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到竞品供货关系正式表失败", e);

        }
    }
    // 复制竞品供货关系表 到协同竞品供货关系表
    private void createMitCmpsupplyInfoByMstCmpsupplyInfoTemp(
            Dao<MstCmpsupplyInfo, String> cmpSupplyDao,
            Dao<MstCmpsupplyInfoTemp, String> cmpSupplyTempDao,
            Dao<MitCmpsupplyInfo, String> mitCmpsupplyDao,
            String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstCmpsupplyInfoTemp, String> collectionQB = cmpSupplyTempDao.queryBuilder();
            Where<MstCmpsupplyInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("terminalkey", termId);
            List<MstCmpsupplyInfoTemp> mstCmpsupplyInfoTemps = collectionQB.query();
            if(mstCmpsupplyInfoTemps.size()>0){
                for (MstCmpsupplyInfoTemp item:mstCmpsupplyInfoTemps) {
                    MitCmpsupplyInfo info = new MitCmpsupplyInfo();
                    info.setCmpsupplykey(item.getCmpsupplykey());
                    info.setStatus(item.getStatus());//0 新增未审核 1 有效 2 无效（通过审核设置） 3 申请未审核(有效->无效)
                    info.setInprice(item.getInprice());
                    info.setReprice(item.getReprice());
                    info.setCmpproductkey(item.getCmpproductkey());
                    info.setCmpcomkey(item.getCmpcomkey());
                    info.setTerminalkey(item.getTerminalkey());
                    info.setCmpinvaliddate(item.getCmpinvaliddate());

                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent("0");
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    mitCmpsupplyDao.createOrUpdate(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到竞品供货关系正式表失败", e);

        }
    }

    // 复制我品供货关系表 到业代我品供货关系表
    private void createMstAgencysupplyInfoByMstAgencysupplyInfoTemp(
            Dao<MstAgencysupplyInfo, String> agencyDao,
            Dao<MstAgencysupplyInfoTemp, String> agencyTempDao, String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstAgencysupplyInfoTemp, String> collectionQB = agencyTempDao.queryBuilder();
            Where<MstAgencysupplyInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("lowerkey", termId);
            List<MstAgencysupplyInfoTemp> mstAgencysupplyInfoTemps = collectionQB.query();
            if(mstAgencysupplyInfoTemps.size()>0){
                for (MstAgencysupplyInfoTemp item:mstAgencysupplyInfoTemps) {
                    MstAgencysupplyInfo info = new MstAgencysupplyInfo();
                    info.setAsupplykey(item.getAsupplykey());
                    info.setStatus(item.getStatus());//0 新增未审核 1 有效 2 无效（通过审核设置） 3 申请未审核(有效->无效)
                    info.setInprice(item.getInprice());
                    info.setReprice(item.getReprice());
                    info.setProductkey(item.getProductkey());
                    info.setLowerkey(item.getLowerkey());
                    info.setLowertype(item.getLowertype());
                    info.setUpperkey(item.getUpperkey());
                    info.setUppertype(item.getUppertype());
                    info.setSiebelkey(item.getSiebelkey());

                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent("0");
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    agencyDao.createOrUpdate(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到我品供货关系正式表失败", e);

        }
    }

    // 复制我品供货关系表 到协同我品供货关系表
    private void createMitAgencysupplyInfoByMstAgencysupplyInfoTemp(
            Dao<MstAgencysupplyInfo, String> agencyDao,
            Dao<MstAgencysupplyInfoTemp, String> agencyTempDao,
            Dao<MitAgencysupplyInfo, String> mitAgencysupplyDao,
            String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstAgencysupplyInfoTemp, String> collectionQB = agencyTempDao.queryBuilder();
            Where<MstAgencysupplyInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("lowerkey", termId);
            List<MstAgencysupplyInfoTemp> mstAgencysupplyInfoTemps = collectionQB.query();
            if(mstAgencysupplyInfoTemps.size()>0){
                for (MstAgencysupplyInfoTemp item:mstAgencysupplyInfoTemps) {
                    MitAgencysupplyInfo info = new MitAgencysupplyInfo();
                    info.setAsupplykey(item.getAsupplykey());
                    info.setStatus(item.getStatus());//0 新增未审核 1 有效 2 无效（通过审核设置） 3 申请未审核(有效->无效)
                    info.setInprice(item.getInprice());
                    info.setReprice(item.getReprice());
                    info.setProductkey(item.getProductkey());
                    info.setLowerkey(item.getLowerkey());
                    info.setLowertype(item.getLowertype());
                    info.setUpperkey(item.getUpperkey());
                    info.setUppertype(item.getUppertype());
                    info.setSiebelkey(item.getSiebelkey());

                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent("0");
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    mitAgencysupplyDao.createOrUpdate(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到我品供货关系正式表失败", e);

        }
    }

    // 复制终端表 到业代终端表
    private void createMstTerminalinfoMByMstTerminalinfoMTemp(
            Dao<MstTerminalinfoM, String> terminalinfoMDao,
            Dao<MstTerminalinfoMTemp, String> terminalinfoMTempDao, String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstTerminalinfoMTemp, String> collectionQB = terminalinfoMTempDao.queryBuilder();
            Where<MstTerminalinfoMTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("terminalkey", termId);
            List<MstTerminalinfoMTemp> mstTerminalinfoMTemps = collectionQB.query();
            if(mstTerminalinfoMTemps.size()>0){
                for (MstTerminalinfoMTemp item:mstTerminalinfoMTemps) {
                    MstTerminalinfoM info = new MstTerminalinfoM();

                    info.setTerminalkey(item.getTerminalkey());
                    info.setRoutekey(item.getRoutekey());
                    info.setTerminalcode(item.getTerminalcode());
                    info.setTerminalname(item.getTerminalname());
                    info.setProvince(item.getProvince());
                    info.setCity(item.getCity());
                    info.setCounty(item.getCounty());
                    info.setAddress(item.getAddress());
                    info.setContact(item.getContact());
                    info.setMobile(item.getMobile());
                    info.setTlevel(item.getTlevel());
                    info.setSequence(item.getSequence());
                    info.setCycle(item.getCycle());
                    info.setHvolume(item.getHvolume());
                    info.setMvolume(item.getMvolume());
                    info.setPvolume(item.getPvolume());
                    info.setLvolume(item.getLvolume());
                    info.setStatus(item.getStatus());//0 新增未审核 1 有效 2 无效（通过审核设置） 3 申请未审核(有效->无效)
                    info.setSellchannel(item.getSellchannel());
                    info.setMainchannel(item.getMainchannel());
                    info.setMinorchannel(item.getMinorchannel());
                    info.setAreatype(item.getAreatype());
                    info.setCmpselftreaty(item.getCmpselftreaty());
                    info.setIfminedate(item.getIfminedate());//店招时间
                    info.setIfmine(item.getIfmine());//店招

                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent(item.getPadisconsistent());
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    terminalinfoMDao.createOrUpdate(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到终端正式表失败", e);

        }

    }

    // 复制终端表 到协同终端表
    private void createMitTerminalinfoMByMstTerminalinfoMTemp(
            Dao<MstTerminalinfoM, String> terminalinfoMDao,
            Dao<MstTerminalinfoMTemp, String> terminalinfoMTempDao,
            Dao<MitTerminalinfoM, String> mitTerminalDao,
            String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstTerminalinfoMTemp, String> collectionQB = terminalinfoMTempDao.queryBuilder();
            Where<MstTerminalinfoMTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("terminalkey", termId);
            List<MstTerminalinfoMTemp> mstTerminalinfoMTemps = collectionQB.query();
            if(mstTerminalinfoMTemps.size()>0){
                for (MstTerminalinfoMTemp item:mstTerminalinfoMTemps) {
                    MitTerminalinfoM info = new MitTerminalinfoM();

                    info.setTerminalkey(item.getTerminalkey());
                    info.setRoutekey(item.getRoutekey());
                    info.setTerminalcode(item.getTerminalcode());
                    info.setTerminalname(item.getTerminalname());
                    info.setProvince(item.getProvince());
                    info.setCity(item.getCity());
                    info.setCounty(item.getCounty());
                    info.setAddress(item.getAddress());
                    info.setContact(item.getContact());
                    info.setMobile(item.getMobile());
                    info.setTlevel(item.getTlevel());
                    info.setSequence(item.getSequence());
                    info.setCycle(item.getCycle());
                    info.setHvolume(item.getHvolume());
                    info.setMvolume(item.getMvolume());
                    info.setPvolume(item.getPvolume());
                    info.setLvolume(item.getLvolume());
                    info.setStatus(item.getStatus());//0 新增未审核 1 有效 2 无效（通过审核设置） 3 申请未审核(有效->无效)
                    info.setSellchannel(item.getSellchannel());
                    info.setMainchannel(item.getMainchannel());
                    info.setMinorchannel(item.getMinorchannel());
                    info.setAreatype(item.getAreatype());
                    info.setCmpselftreaty(item.getCmpselftreaty());
                    info.setIfminedate(item.getIfminedate());//店招时间
                    info.setIfmine(item.getIfmine());//店招

                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent(item.getPadisconsistent());
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    mitTerminalDao.createOrUpdate(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到终端正式表失败", e);

        }

    }

    // 拜访产品表 到业代拜访产品表
    private void createMstVistproductInfoByMstVistproductInfoTemp(
            Dao<MstVistproductInfo, String> proDao,
            Dao<MstVistproductInfoTemp, String> proTempDao, String visitId, String termId, String visitEndDate) {
        try {
            QueryBuilder<MstVistproductInfoTemp, String> collectionQB = proTempDao.queryBuilder();
            Where<MstVistproductInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitId);
            List<MstVistproductInfoTemp> mstVistproductInfoTemps = collectionQB.query();
            if(mstVistproductInfoTemps.size()>0){
                for (MstVistproductInfoTemp item:mstVistproductInfoTemps) {
                    MstVistproductInfo info = new MstVistproductInfo();
                    info.setRecordkey(item.getRecordkey());
                    info.setVisitkey(item.getVisitkey());
                    info.setProductkey(item.getProductkey());
                    info.setCmpproductkey(item.getCmpproductkey());
                    info.setCmpcomkey(item.getCmpcomkey());
                    info.setAgencykey(item.getAgencykey());
                    info.setAgencyname(item.getAgencyname());// 用户输入的竞品经销商
                    info.setPurcprice(item.getPurcprice());
                    info.setRetailprice(item.getRetailprice());
                    info.setPurcnum(item.getPurcnum());
                    info.setPronum(item.getPronum());
                    info.setCurrnum(item.getCurrnum());
                    info.setSalenum(item.getSalenum());
                    info.setFristdate(item.getFristdate());
                    info.setAddcard(item.getAddcard());// 累计卡
                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent(item.getPadisconsistent());
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    proDao.create(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到拜访产品正式表失败", e);

        }

    }

    // 拜访产品表 到协同拜访产品表
    private void createMitVistproductInfoByMstVistproductInfoTemp(
            Dao<MstVistproductInfo, String> proDao,
            Dao<MstVistproductInfoTemp, String> proTempDao,
            Dao<MitVistproductInfo, String> mitVistproductDao,
            String visitId, String termId, String visitEndDate) {
        try {
            QueryBuilder<MstVistproductInfoTemp, String> collectionQB = proTempDao.queryBuilder();
            Where<MstVistproductInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitId);
            List<MstVistproductInfoTemp> mstVistproductInfoTemps = collectionQB.query();
            if(mstVistproductInfoTemps.size()>0){
                for (MstVistproductInfoTemp item:mstVistproductInfoTemps) {
                    MitVistproductInfo info = new MitVistproductInfo();
                    info.setRecordkey(item.getRecordkey());
                    info.setVisitkey(item.getVisitkey());
                    info.setProductkey(item.getProductkey());
                    info.setCmpproductkey(item.getCmpproductkey());
                    info.setCmpcomkey(item.getCmpcomkey());
                    info.setAgencykey(item.getAgencykey());
                    info.setAgencyname(item.getAgencyname());// 用户输入的竞品经销商
                    info.setPurcprice(item.getPurcprice());
                    info.setRetailprice(item.getRetailprice());
                    info.setPurcnum(item.getPurcnum());
                    info.setPronum(item.getPronum());
                    info.setCurrnum(item.getCurrnum());
                    info.setSalenum(item.getSalenum());
                    info.setFristdate(item.getFristdate());
                    info.setAddcard(item.getAddcard());// 累计卡
                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent(item.getPadisconsistent());
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    mitVistproductDao.create(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到拜访产品正式表失败", e);

        }

    }

    // 复制促销活动终端表 到业代促销活动终端表
    private void createMstPromotermInfoByMstPromotermInfoTemp(
            Dao<MstPromotermInfo, String> promDao,
            Dao<MstPromotermInfoTemp, String> promTempDao, String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstPromotermInfoTemp, String> collectionQB = promTempDao.queryBuilder();
            Where<MstPromotermInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitId);
            List<MstPromotermInfoTemp> mstPromotermInfoTemps = collectionQB.query();
            if(mstPromotermInfoTemps.size()>0){
                for (MstPromotermInfoTemp item:mstPromotermInfoTemps) {
                    MstPromotermInfo info = new MstPromotermInfo();
                    info.setRecordkey(item.getRecordkey());
                    info.setPtypekey(item.getPtypekey());
                    info.setTerminalkey(item.getTerminalkey());
                    info.setVisitkey(item.getVisitkey());
                    info.setStartdate(item.getStartdate());
                    info.setIsaccomplish(item.getIsaccomplish());
                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent(item.getPadisconsistent());
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    promDao.create(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到促销活动终端正式表失败", e);

        }


    }

    // 复制促销活动终端表 到协同促销活动终端表
    private void createMitPromotermInfoByMstPromotermInfoTemp(
            Dao<MstPromotermInfo, String> promDao,
            Dao<MstPromotermInfoTemp, String> promTempDao,
            Dao<MitPromotermInfo, String> mitPromotermDao,
            String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstPromotermInfoTemp, String> collectionQB = promTempDao.queryBuilder();
            Where<MstPromotermInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitId);
            List<MstPromotermInfoTemp> mstPromotermInfoTemps = collectionQB.query();
            if(mstPromotermInfoTemps.size()>0){
                for (MstPromotermInfoTemp item:mstPromotermInfoTemps) {
                    MitPromotermInfo info = new MitPromotermInfo();
                    info.setRecordkey(item.getRecordkey());
                    info.setPtypekey(item.getPtypekey());
                    info.setTerminalkey(item.getTerminalkey());
                    info.setVisitkey(item.getVisitkey());
                    info.setStartdate(item.getStartdate());
                    info.setIsaccomplish(item.getIsaccomplish());
                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent(item.getPadisconsistent());
                    info.setPadcondate(item.getPadcondate());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setOrderbyno(item.getOrderbyno());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setVersion(item.getVersion());
                    info.setCredate(item.getCredate());
                    info.setCreuser(item.getCreuser());
                    info.setUpdatetime(item.getUpdatetime());
                    info.setUpdateuser(item.getUpdateuser());
                    mitPromotermDao.create(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到促销活动终端正式表失败", e);

        }


    }

    // // 复制图片表 到业代图片表
    private void createMstCameraInfoMByMstCameraInfoMTemp(
            Dao<MstCameraInfoM, String> cameraDao,
            Dao<MstCameraInfoMTemp, String> cameraTempDao, String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstCameraInfoMTemp, String> collectionQB = cameraTempDao.queryBuilder();
            Where<MstCameraInfoMTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitId);
            List<MstCameraInfoMTemp> cameraInfoMTemps = collectionQB.query();
            if(cameraInfoMTemps.size()>0){
                for (MstCameraInfoMTemp item:cameraInfoMTemps) {
                    MstCameraInfoM info = new MstCameraInfoM();
                    info.setCamerakey(item.getCamerakey());// 图片主键
                    info.setTerminalkey(item.getTerminalkey());// 终端主键
                    info.setVisitkey(item.getVisitkey()); // 拜访主键
                    info.setPictypekey(item.getPictypekey());// 图片类型主键(UUID)
                    info.setPicname(item.getPicname());// 图片名称
                    info.setLocalpath(item.getLocalpath());// 本地图片路径
                    info.setNetpath(item.getNetpath());// 请求网址(原先ftp上传用,现已不用)
                    info.setCameradata(item.getCameradata());// 拍照时间
                    info.setIsupload(item.getIsupload());// 是否已上传 0未上传  1已上传
                    info.setIstakecamera(item.getIstakecamera());//
                    info.setPicindex(item.getPicindex());// 图片所在角标
                    info.setPictypename(item.getPictypename());// 图片类型(中文名称)
                    info.setSureup(item.getSureup());// 确定上传 0确定上传 1未确定上传
                    info.setImagefileString(item.getImagefileString());// 将图片文件转成String保存在数据库
                    cameraDao.create(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到图片正式表失败", e);

        }
    }
    // // 复制图片表 到协同图片表
    private void createMitCameraInfoMByMstCameraInfoMTemp(
            Dao<MstCameraInfoM, String> cameraDao,
            Dao<MstCameraInfoMTemp, String> cameraTempDao,
            Dao<MitCameraInfoM, String> mitCameraDao,
            String visitId, String termId, String visitEndDate) {

        try {
            QueryBuilder<MstCameraInfoMTemp, String> collectionQB = cameraTempDao.queryBuilder();
            Where<MstCameraInfoMTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitId);
            List<MstCameraInfoMTemp> cameraInfoMTemps = collectionQB.query();
            if(cameraInfoMTemps.size()>0){
                for (MstCameraInfoMTemp item:cameraInfoMTemps) {
                    MitCameraInfoM info = new MitCameraInfoM();
                    info.setCamerakey(item.getCamerakey());// 图片主键
                    info.setTerminalkey(item.getTerminalkey());// 终端主键
                    info.setVisitkey(item.getVisitkey()); // 拜访主键
                    info.setPictypekey(item.getPictypekey());// 图片类型主键(UUID)
                    info.setPicname(item.getPicname());// 图片名称
                    info.setLocalpath(item.getLocalpath());// 本地图片路径
                    info.setNetpath(item.getNetpath());// 请求网址(原先ftp上传用,现已不用)
                    info.setCameradata(item.getCameradata());// 拍照时间
                    info.setIsupload(item.getIsupload());// 是否已上传 0未上传  1已上传
                    info.setIstakecamera(item.getIstakecamera());//
                    info.setPicindex(item.getPicindex());// 图片所在角标
                    info.setPictypename(item.getPictypename());// 图片类型(中文名称)
                    info.setSureup(item.getSureup());// 确定上传 0确定上传 1未确定上传
                    info.setImagefileString(item.getImagefileString());// 将图片文件转成String保存在数据库
                    info.setAreaid(item.getAreaid());
                    info.setAreapid(item.getAreapid());
                    info.setGridkey(item.getGridkey());
                    info.setRoutekey(item.getRoutekey());
                    mitCameraDao.create(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到图片正式表失败", e);

        }
    }

    // 复制采集项表 到业代采集项表
    private void createMstCollectionexerecordInfoByMstCollectionexerecordInfoTemp(
            Dao<MstCollectionexerecordInfo, String> collectionDao,
            Dao<MstCollectionexerecordInfoTemp, String> collectionTempDao, String visitId) {
        try {
            QueryBuilder<MstCollectionexerecordInfoTemp, String> collectionQB = collectionTempDao.queryBuilder();
            Where<MstCollectionexerecordInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitId);
            collectionWhere.and();
            //collectionWhere.ne("deleteflag", ConstValues.FLAG_1);
            //collectionWhere.and();
            collectionWhere.isNotNull("checkkey");
            collectionWhere.and();
            collectionWhere.ne("checkkey", "");
            collectionQB.orderBy("productkey", true);
            collectionQB.orderBy("colitemkey", true);
            collectionQB.orderBy("checkkey", false);
            collectionQB.orderBy("updatetime", false);
            List<MstCollectionexerecordInfoTemp> collectiontempList = collectionQB.query();
            if(collectiontempList.size()>0){
                for (MstCollectionexerecordInfoTemp item:collectiontempList) {
                    MstCollectionexerecordInfo info = new MstCollectionexerecordInfo();
                    info.setColrecordkey(item.getColrecordkey());
                    info.setVisitkey(item.getVisitkey());

                    info.setProductkey(item.getProductkey());
                    info.setCheckkey(item.getCheckkey());
                    info.setColitemkey(item.getColitemkey());
                    info.setAddcount(item.getAddcount());
                    info.setTotalcount(item.getTotalcount());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setFreshness(item.getFreshness());// 新鲜度
                    info.setOrderbyno(item.getOrderbyno());
                    info.setVersion(item.getVersion());
                    info.setCreuser(item.getCreuser());
                    info.setUpdateuser(item.getUpdateuser());
                    info.setBianhualiang(item.getBianhualiang());//变化量 (用于最后上传时 现有量变化量必须填值,才能上传)
                    info.setXianyouliang(item.getXianyouliang());// 现有量 (用于最后上传时 现有量变化量必须填值,才能上传)

                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent(item.getPadisconsistent());
                    info.setPadcondate(item.getPadcondate());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setCredate(item.getCredate());
                    info.setUpdatetime(item.getUpdatetime());
                    collectionDao.create(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到采集项正式表失败", e);

        }
    }

    // 复制采集项表 到协同采集项表
    private void createMitCollectionexerecordByMstCollectionexerecordTemp(
            Dao<MstCollectionexerecordInfo, String> collectionDao,
            Dao<MstCollectionexerecordInfoTemp, String> collectionTempDao,
            Dao<MitCollectionexerecordInfo, String> mitCollectionDao,
            String visitId) {
        try {
            QueryBuilder<MstCollectionexerecordInfoTemp, String> collectionQB = collectionTempDao.queryBuilder();
            Where<MstCollectionexerecordInfoTemp, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitId);
            collectionWhere.and();
            //collectionWhere.ne("deleteflag", ConstValues.FLAG_1);
            //collectionWhere.and();
            collectionWhere.isNotNull("checkkey");
            collectionWhere.and();
            collectionWhere.ne("checkkey", "");
            collectionQB.orderBy("productkey", true);
            collectionQB.orderBy("colitemkey", true);
            collectionQB.orderBy("checkkey", false);
            collectionQB.orderBy("updatetime", false);
            List<MstCollectionexerecordInfoTemp> collectiontempList = collectionQB.query();
            if(collectiontempList.size()>0){
                for (MstCollectionexerecordInfoTemp item:collectiontempList) {
                    MitCollectionexerecordInfo info = new MitCollectionexerecordInfo();
                    info.setColrecordkey(item.getColrecordkey());
                    info.setVisitkey(item.getVisitkey());

                    info.setProductkey(item.getProductkey());
                    info.setCheckkey(item.getCheckkey());
                    info.setColitemkey(item.getColitemkey());
                    info.setAddcount(item.getAddcount());
                    info.setTotalcount(item.getTotalcount());
                    info.setComid(item.getComid());
                    info.setRemarks(item.getRemarks());
                    info.setFreshness(item.getFreshness());// 新鲜度
                    info.setOrderbyno(item.getOrderbyno());
                    info.setVersion(item.getVersion());
                    info.setCreuser(item.getCreuser());
                    info.setUpdateuser(item.getUpdateuser());
                    info.setBianhualiang(item.getBianhualiang());//变化量 (用于最后上传时 现有量变化量必须填值,才能上传)
                    info.setXianyouliang(item.getXianyouliang());// 现有量 (用于最后上传时 现有量变化量必须填值,才能上传)

                    info.setSisconsistent(item.getSisconsistent());
                    info.setScondate(item.getScondate());
                    info.setPadisconsistent(item.getPadisconsistent());
                    info.setPadcondate(item.getPadcondate());
                    info.setDeleteflag(item.getDeleteflag());
                    info.setCredate(item.getCredate());
                    info.setUpdatetime(item.getUpdatetime());
                    mitCollectionDao.create(info);
                }
            }

        }catch (Exception e){
            Log.e(TAG, "复制到采集项正式表失败", e);

        }
    }

    // 复制拉链表 到业代拉链表
    private void createMstcheckexerecordinfoByMstCheckexerecordInfoTemp(
            Dao<MstCheckexerecordInfo, String> checkInfoDao, Dao<MstCheckexerecordInfoTemp, String> tempDao,
            String visitId, String termId, String visitEndDate) {

        try {
            // 只留年月日
            String endDate = visitEndDate.substring(0, 8);

            // 临时表 visitEndDate(根据visitkey)
            List<MstCheckexerecordInfoTemp> tempAllList = null;

            tempAllList = getCheckExerecordTempListByVisitid(tempDao, visitId);

            if (!CheckUtil.IsEmpty(tempAllList)) {
                //不关联产品的指标 比如:合作配送占有率（临时表的） false表示没有productkey
                List<MstCheckexerecordInfoTemp> noProTempList = getCheckExerecordTemp(tempAllList, false);
                for (MstCheckexerecordInfoTemp tempItem : noProTempList) {
                    MstCheckexerecordInfo item = new MstCheckexerecordInfo();
                    item.setRecordkey(tempItem.getRecordkey());
                    item.setVisitkey(tempItem.getVisitkey());
                    item.setCheckkey(tempItem.getCheckkey());
                    item.setChecktype(tempItem.getChecktype());
                    item.setAcresult(tempItem.getAcresult());
                    item.setIscom(tempItem.getIscom());
                    item.setStartdate(endDate);
                    item.setEnddate(endDate);
                    item.setTerminalkey(tempItem.getTerminalkey());
                    item.setPadisconsistent(ConstValues.FLAG_0);
                    item.setPadcondate(null);
                    item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    item.setUpdateuser(item.getCreuser());
                    checkInfoDao.create(item);
                }
                //关联产品的指标 比如:铺货状态,冰冻化（临时表的） true表示有productkey
                List<MstCheckexerecordInfoTemp> proTempList = getCheckExerecordTemp(tempAllList, true);

                if (proTempList != null && proTempList.size() > 0) {
                    //关联产品的指标 比如:铺货状态,冰冻化（临时表的） key：终端-指标-产品,value;拉链临时表记录
                    Map<String, MstCheckexerecordInfoTemp> proTempMap = new HashMap<String, MstCheckexerecordInfoTemp>();
                    for (MstCheckexerecordInfoTemp temp : proTempList) {
                        String key = temp.getTerminalkey() + "-" + temp.getCheckkey() + "-" + temp.getProductkey();
                        if (!proTempMap.containsKey(key)) {
                            proTempMap.put(key, temp);
                        }
                    }
                    proTempList.clear();
                    // 去重
                    for (String key : proTempMap.keySet()) {
                        MstCheckexerecordInfoTemp temp = proTempMap.get(key);
                        proTempList.add(temp);
                    }
                    //获取 拉链正式表 本终端上一次的数据 （拉链表记录 关联产品的指标）
                    List<MstCheckexerecordInfo> proList = getCheckExerecordList(checkInfoDao, termId);

                    // 实例化要最终指标结果集对象
                    boolean addFlag = false;// 是否在拉链正式表中插入新纪录
                    MstCheckexerecordInfo item;
                    // 遍历拉链临时表的指标 MstCheckexerecordInfoTemp
                    for (MstCheckexerecordInfoTemp tempItem : proTempList) {
                        addFlag = false;
                        String acresultTemp = tempItem.getAcresult();
                        String checkKeyTemp = tempItem.getCheckkey();
                        if (CheckUtil.isBlankOrNull(acresultTemp) || "-1".equals(acresultTemp)) {
                            if (PropertiesUtil.getProperties("check_puhuo").equals(checkKeyTemp)) {//#铺货状态
                                acresultTemp = "301";
                            } else if (PropertiesUtil.getProperties("check_daoju").equals(checkKeyTemp)) {//#道具生动化
                                acresultTemp = "307";
                            } else if (PropertiesUtil.getProperties("check_chanpin").equals(checkKeyTemp)) {//#产品生动化
                                acresultTemp = "309";
                            } else if (PropertiesUtil.getProperties("check_bingdong").equals(checkKeyTemp)) {//#冰冻化
                                acresultTemp = "311";
                            }
                        }
                        // 遍历正式表的指标
                        for (MstCheckexerecordInfo valueItem : proList) {
                            // MstCheckexerecordInfo
                            // 如果是同一产品的相同指标
                            if (tempItem.getProductkey().equals(valueItem.getProductkey()) && tempItem.getCheckkey().equals(valueItem.getCheckkey())) {

                                addFlag = true;
                                // 拉链临时表deleteflag字段为1的  删除产品的指标
                                if (ConstValues.FLAG_1.equals(tempItem.getDeleteflag())) {
                                    // 如果结束日期与开始日期相同，则只更新不做插入数据
                                    if (endDate.equals(valueItem.getStartdate())) {
                                        valueItem.setAcresult(getDefaultAcresult(valueItem.getCheckkey()));// 默认指标
                                        valueItem.setStartdate(endDate);
                                        valueItem.setEnddate("30001201");
                                        valueItem.setSisconsistent(ConstValues.FLAG_0);
                                        valueItem.setScondate(null);
                                        valueItem.setPadisconsistent(ConstValues.FLAG_0);
                                        valueItem.setPadcondate(null);
                                        valueItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
                                        valueItem.setUpdateuser(valueItem.getCreuser());
                                        valueItem.setResultstatus(new BigDecimal(0));
                                        checkInfoDao.update(valueItem);
                                    } else {
                                        // 更新前一个值的结束时间
                                        valueItem.setEnddate(endDate);
                                        valueItem.setResultstatus(new BigDecimal(1));
                                        valueItem.setSisconsistent(ConstValues.FLAG_0);
                                        valueItem.setScondate(null);
                                        valueItem.setPadisconsistent(ConstValues.FLAG_0);
                                        valueItem.setPadcondate(null);
                                        valueItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
                                        valueItem.setUpdateuser(valueItem.getCreuser());
                                        valueItem.setResultstatus(new BigDecimal(1));
                                        checkInfoDao.update(valueItem);

                                        // 插入一新指标结果
                                        item = new MstCheckexerecordInfo();
                                        item.setRecordkey(FunUtil.getUUID());
                                        item.setVisitkey(tempItem.getVisitkey());// 修改为 "temp" -> tempItem.getVisitkey()
                                        item.setProductkey(valueItem.getProductkey());
                                        item.setCheckkey(valueItem.getCheckkey());
                                        item.setChecktype(valueItem.getChecktype());
                                        item.setAcresult(getDefaultAcresult(valueItem.getCheckkey()));// 默认指标
                                        item.setIscom(valueItem.getIscom());
                                        item.setStartdate(endDate);
                                        item.setEnddate("30001201");
                                        item.setTerminalkey(valueItem.getTerminalkey());
                                        item.setSisconsistent(ConstValues.FLAG_0);
                                        item.setPadisconsistent(ConstValues.FLAG_0);
                                        item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                                        item.setUpdateuser(item.getCreuser());
                                        item.setResultstatus(new BigDecimal(0));
                                        checkInfoDao.create(item);
                                    }
                                } else {
                                    // 如果指标结果不同
                                    if (!acresultTemp.equals(valueItem.getAcresult())) {

                                        // 如果结束日期与开始日期相同，则只更新不做插入数据
                                        if (endDate.equals(valueItem.getStartdate())) {
                                            // 更新前一个值的结束时间
                                            valueItem.setAcresult(acresultTemp);
                                            valueItem.setSisconsistent(ConstValues.FLAG_0);
                                            valueItem.setScondate(null);
                                            valueItem.setPadisconsistent(ConstValues.FLAG_0);
                                            valueItem.setPadcondate(null);
                                            valueItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
                                            valueItem.setUpdateuser(valueItem.getCreuser());
                                            valueItem.setResultstatus(new BigDecimal(0));
                                            checkInfoDao.update(valueItem);
                                        } else {
                                            // 更新前一个值的结束时间
                                            valueItem.setEnddate(endDate);
                                            valueItem.setResultstatus(new BigDecimal(1));
                                            valueItem.setSisconsistent(ConstValues.FLAG_0);
                                            valueItem.setScondate(null);
                                            valueItem.setPadisconsistent(ConstValues.FLAG_0);
                                            valueItem.setPadcondate(null);
                                            valueItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
                                            valueItem.setUpdateuser(valueItem.getCreuser());
                                            valueItem.setResultstatus(new BigDecimal(1));
                                            checkInfoDao.update(valueItem);

                                            // 插入一新指标结果
                                            item = new MstCheckexerecordInfo();
                                            item.setRecordkey(FunUtil.getUUID());
                                            item.setVisitkey(tempItem.getVisitkey());// 修改为 temp -> tempItem.getVisitkey()
                                            item.setProductkey(valueItem.getProductkey());
                                            item.setCheckkey(valueItem.getCheckkey());
                                            item.setChecktype(valueItem.getChecktype());
                                            item.setAcresult(acresultTemp);
                                            item.setIscom(valueItem.getIscom());
                                            item.setStartdate(endDate);
                                            item.setEnddate("30001201");
                                            item.setTerminalkey(valueItem.getTerminalkey());
                                            item.setSisconsistent(ConstValues.FLAG_0);
                                            item.setPadisconsistent(ConstValues.FLAG_0);
                                            item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                                            item.setUpdateuser(item.getCreuser());
                                            item.setResultstatus(new BigDecimal(0));
                                            checkInfoDao.create(item);
                                        }
                                    }
                                }
                                break;
                            }
                        }

                        // 如果之前没有，则为新境
                        if (!addFlag) {
                            item = new MstCheckexerecordInfo();
                            item.setRecordkey(FunUtil.getUUID());
                            item.setVisitkey(tempItem.getVisitkey());// 修改为 temp -> tempItem.getVisitkey()
                            item.setProductkey(tempItem.getProductkey());
                            item.setCheckkey(tempItem.getCheckkey());
                            item.setChecktype(tempItem.getChecktype());
                            item.setAcresult(acresultTemp);
                            item.setIscom(tempItem.getIscom());
                            item.setStartdate(endDate);
                            item.setEnddate("30001201");
                            item.setTerminalkey(tempItem.getTerminalkey());
                            item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                            item.setUpdateuser(item.getCreuser());
                            item.setResultstatus(new BigDecimal(0));
                            checkInfoDao.create(item);
                        }
                    }
                }
                // 删除临时数据
                //tempDao.delete(tempAllList);
            }
            delRepeatMstCollectionexerecordInfo(visitId);
        } catch (SQLException e) {
            Log.e(TAG, "复制到拉链正式表失败", e);
            e.printStackTrace();
        }
    }

    // 复制拉链表 到协同拉链表
    private void createMitcheckexerecordByMstCheckexerecordTemp(
            Dao<MstCheckexerecordInfo, String> mstcheckInfoDao,
            Dao<MitCheckexerecordInfo, String> mitCheckInfoDao,
            Dao<MstCheckexerecordInfoTemp, String> tempDao,
            String visitId, String termId, String visitEndDate) {

        try {
            // 只留年月日
            String endDate = visitEndDate.substring(0, 8);

            // 临时表 visitEndDate(根据visitkey)
            List<MstCheckexerecordInfoTemp> tempAllList = null;

            tempAllList = getCheckExerecordTempListByVisitid(tempDao, visitId);

            if (!CheckUtil.IsEmpty(tempAllList)) {
                //不关联产品的指标 比如:合作配送占有率（临时表的） false表示没有productkey
                List<MstCheckexerecordInfoTemp> noProTempList = getCheckExerecordTemp(tempAllList, false);
                for (MstCheckexerecordInfoTemp tempItem : noProTempList) {
                    MitCheckexerecordInfo item = new MitCheckexerecordInfo();
                    item.setRecordkey(tempItem.getRecordkey());
                    item.setVisitkey(tempItem.getVisitkey());
                    item.setCheckkey(tempItem.getCheckkey());
                    item.setChecktype(tempItem.getChecktype());
                    item.setAcresult(tempItem.getAcresult());
                    item.setIscom(tempItem.getIscom());
                    item.setStartdate(endDate);
                    item.setEnddate(endDate);
                    item.setTerminalkey(tempItem.getTerminalkey());
                    item.setPadisconsistent(ConstValues.FLAG_0);
                    item.setPadcondate(null);
                    item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    item.setUpdateuser(item.getCreuser());
                    mitCheckInfoDao.create(item);
                }
                //关联产品的指标 比如:铺货状态,冰冻化（临时表的） true表示有productkey
                List<MstCheckexerecordInfoTemp> proTempList = getCheckExerecordTemp(tempAllList, true);

                if (proTempList != null && proTempList.size() > 0) {
                    //关联产品的指标 比如:铺货状态,冰冻化（临时表的） key：终端-指标-产品,value;拉链临时表记录
                    Map<String, MstCheckexerecordInfoTemp> proTempMap = new HashMap<String, MstCheckexerecordInfoTemp>();
                    for (MstCheckexerecordInfoTemp temp : proTempList) {
                        String key = temp.getTerminalkey() + "-" + temp.getCheckkey() + "-" + temp.getProductkey();
                        if (!proTempMap.containsKey(key)) {
                            proTempMap.put(key, temp);
                        }
                    }
                    proTempList.clear();
                    // 去重
                    for (String key : proTempMap.keySet()) {
                        MstCheckexerecordInfoTemp temp = proTempMap.get(key);
                        proTempList.add(temp);
                    }
                    //获取 拉链正式表 本终端上一次的数据 （拉链表记录 关联产品的指标）
                    List<MstCheckexerecordInfo> proList = getCheckExerecordList(mstcheckInfoDao, termId);

                    // 实例化要最终指标结果集对象
                    boolean addFlag = false;// 是否在拉链正式表中插入新纪录
                    MitCheckexerecordInfo item;
                    // 遍历拉链临时表的指标 MstCheckexerecordInfoTemp
                    for (MstCheckexerecordInfoTemp tempItem : proTempList) {
                        addFlag = false;
                        String acresultTemp = tempItem.getAcresult();
                        String checkKeyTemp = tempItem.getCheckkey();
                        if (CheckUtil.isBlankOrNull(acresultTemp) || "-1".equals(acresultTemp)) {
                            if (PropertiesUtil.getProperties("check_puhuo").equals(checkKeyTemp)) {//#铺货状态
                                acresultTemp = "301";
                            } else if (PropertiesUtil.getProperties("check_daoju").equals(checkKeyTemp)) {//#道具生动化
                                acresultTemp = "307";
                            } else if (PropertiesUtil.getProperties("check_chanpin").equals(checkKeyTemp)) {//#产品生动化
                                acresultTemp = "309";
                            } else if (PropertiesUtil.getProperties("check_bingdong").equals(checkKeyTemp)) {//#冰冻化
                                acresultTemp = "311";
                            }
                        }
                        // 遍历正式表的指标
                        for (MstCheckexerecordInfo valueItem : proList) {
                            // MstCheckexerecordInfo
                            // 如果是同一产品的相同指标
                            if (tempItem.getProductkey().equals(valueItem.getProductkey()) && tempItem.getCheckkey().equals(valueItem.getCheckkey())) {

                                addFlag = true;
                                // 拉链临时表deleteflag字段为1的  删除产品的指标
                                if (ConstValues.FLAG_1.equals(tempItem.getDeleteflag())) {
                                    // 如果结束日期与开始日期相同，则只更新不做插入数据
                                    // 插入一新指标结果
                                    item = new MitCheckexerecordInfo();
                                    item.setRecordkey(FunUtil.getUUID());
                                    item.setVisitkey(tempItem.getVisitkey());// 修改为 "temp" -> tempItem.getVisitkey()
                                    item.setProductkey(valueItem.getProductkey());
                                    item.setCheckkey(valueItem.getCheckkey());
                                    item.setChecktype(valueItem.getChecktype());
                                    item.setAcresult(getDefaultAcresult(valueItem.getCheckkey()));// 默认指标
                                    item.setIscom(valueItem.getIscom());
                                    item.setStartdate(endDate);
                                    item.setEnddate("30001201");
                                    item.setTerminalkey(valueItem.getTerminalkey());
                                    item.setSisconsistent(ConstValues.FLAG_0);
                                    item.setPadisconsistent(ConstValues.FLAG_0);
                                    item.setCreuser(PrefUtils.getString(context, "userid", ""));
                                    item.setUpdateuser(item.getCreuser());
                                    item.setResultstatus(new BigDecimal(0));
                                    mitCheckInfoDao.create(item);
                                } else {
                                    // 如果指标结果不同
                                    if (!acresultTemp.equals(valueItem.getAcresult())) {

                                        // 插入一新指标结果
                                        item = new MitCheckexerecordInfo();
                                        item.setRecordkey(FunUtil.getUUID());
                                        item.setVisitkey(tempItem.getVisitkey());// 修改为 temp -> tempItem.getVisitkey()
                                        item.setProductkey(valueItem.getProductkey());
                                        item.setCheckkey(valueItem.getCheckkey());
                                        item.setChecktype(valueItem.getChecktype());
                                        item.setAcresult(acresultTemp);
                                        item.setIscom(valueItem.getIscom());
                                        item.setStartdate(endDate);
                                        item.setEnddate("30001201");
                                        item.setTerminalkey(valueItem.getTerminalkey());
                                        item.setSisconsistent(ConstValues.FLAG_0);
                                        item.setPadisconsistent(ConstValues.FLAG_0);
                                        item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                                        item.setUpdateuser(item.getCreuser());
                                        item.setResultstatus(new BigDecimal(0));
                                        mitCheckInfoDao.create(item);
                                    }
                                }
                                break;
                            }
                        }

                        // 如果之前没有，则为新境
                        if (!addFlag) {
                            item = new MitCheckexerecordInfo();
                            item.setRecordkey(FunUtil.getUUID());
                            item.setVisitkey(tempItem.getVisitkey());// 修改为 temp -> tempItem.getVisitkey()
                            item.setProductkey(tempItem.getProductkey());
                            item.setCheckkey(tempItem.getCheckkey());
                            item.setChecktype(tempItem.getChecktype());
                            item.setAcresult(acresultTemp);
                            item.setIscom(tempItem.getIscom());
                            item.setStartdate(endDate);
                            item.setEnddate("30001201");
                            item.setTerminalkey(tempItem.getTerminalkey());
                            item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                            item.setUpdateuser(item.getCreuser());
                            item.setResultstatus(new BigDecimal(0));
                            mitCheckInfoDao.create(item);
                        }
                    }
                }
                // 删除临时数据
                //tempDao.delete(tempAllList);
            }
            delRepeatMstCollectionexerecordInfo(visitId);
        } catch (SQLException e) {
            Log.e(TAG, "复制到拉链正式表失败", e);
            e.printStackTrace();
        }
    }

    // 复制拜访主表  MstVisitMTemp → MstVisitM
    private void createMstVisitMByMstVisitMTemp(MstVisitMDao dao,MitVisitMDao mitVisitMDao, MstVisitMTemp visitTemp) {
        if (visitTemp != null) {
            try {
                MstVisitM mstVisitM = new MstVisitM();
                mstVisitM.setVisitkey(visitTemp.getVisitkey());
                mstVisitM.setTerminalkey(visitTemp.getTerminalkey());
                mstVisitM.setRoutekey(visitTemp.getRoutekey());
                mstVisitM.setGridkey(visitTemp.getGridkey());
                mstVisitM.setAreaid(visitTemp.getAreaid());
                mstVisitM.setVisitdate(visitTemp.getVisitdate());
                mstVisitM.setEnddate(visitTemp.getEnddate());
                mstVisitM.setTempkey(visitTemp.getTempkey());
                mstVisitM.setUserid(visitTemp.getUserid());
                mstVisitM.setVisituser(visitTemp.getVisituser());
                mstVisitM.setIsself(visitTemp.getIsself());
                mstVisitM.setIscmp(visitTemp.getIscmp());
                mstVisitM.setSelftreaty(visitTemp.getSelftreaty());
                mstVisitM.setCmptreaty(visitTemp.getCmptreaty());
                mstVisitM.setStatus(visitTemp.getStatus());

                mstVisitM.setIshdistribution(visitTemp.getIshdistribution());
                mstVisitM.setExetreaty(visitTemp.getExetreaty());
                mstVisitM.setSelfoccupancy(visitTemp.getSelfoccupancy());
                mstVisitM.setIscmpcollapse(visitTemp.getIscmpcollapse());

                mstVisitM.setSisconsistent(visitTemp.getSisconsistent());
                mstVisitM.setScondate(visitTemp.getScondate());

                mstVisitM.setPadcondate(visitTemp.getPadcondate());

                mstVisitM.setComid(visitTemp.getComid());
                mstVisitM.setRemarks(visitTemp.getRemarks());
                mstVisitM.setOrderbyno(visitTemp.getOrderbyno());// 同一次是0 返回时会改为1
                mstVisitM.setDeleteflag(visitTemp.getDeleteflag());
                mstVisitM.setVersion(visitTemp.getVersion());

                mstVisitM.setCredate(visitTemp.getCredate());
                mstVisitM.setCreuser(visitTemp.getCreuser());
                mstVisitM.setUpdatetime(visitTemp.getUpdatetime());
                mstVisitM.setUpdateuser(visitTemp.getUpdateuser());

                mstVisitM.setLongitude(visitTemp.getLongitude());
                mstVisitM.setLatitude(visitTemp.getLatitude());
                mstVisitM.setGpsstatus(visitTemp.getGpsstatus());
                mstVisitM.setIfminedate(visitTemp.getIfminedate());
                mstVisitM.setIfmine(visitTemp.getIfmine());
                mstVisitM.setVisitposition(visitTemp.getVisitposition());
                mstVisitM.setPadisconsistent("0");
                mstVisitM.setUploadFlag("1");
                dao.create(mstVisitM);
                //mitVisitMDao.create(mstVisitM);
            } catch (SQLException e) {
                Log.e(TAG, "复制到拜访主表正式表失败", e);
                e.printStackTrace();
            }
        }
    }

    // 复制拜访主表 到协同拜访主表  MstVisitMTemp → MitVisitM
    private void createMitVisitMByMstVisitMTemp(MstVisitMDao dao,MitVisitMDao mitVisitMDao, MstVisitMTemp visitTemp) {
        if (visitTemp != null) {
            try {
                MitVisitM mstVisitM = new MitVisitM();
                mstVisitM.setVisitkey(visitTemp.getVisitkey());
                mstVisitM.setTerminalkey(visitTemp.getTerminalkey());
                mstVisitM.setRoutekey(visitTemp.getRoutekey());
                mstVisitM.setGridkey(visitTemp.getGridkey());
                mstVisitM.setAreaid(visitTemp.getAreaid());
                mstVisitM.setVisitdate(visitTemp.getVisitdate());
                mstVisitM.setEnddate(visitTemp.getEnddate());
                mstVisitM.setTempkey(visitTemp.getTempkey());
                mstVisitM.setUserid(visitTemp.getUserid());
                mstVisitM.setVisituser(visitTemp.getVisituser());
                mstVisitM.setIsself(visitTemp.getIsself());
                mstVisitM.setIscmp(visitTemp.getIscmp());
                mstVisitM.setSelftreaty(visitTemp.getSelftreaty());
                mstVisitM.setCmptreaty(visitTemp.getCmptreaty());
                mstVisitM.setStatus(visitTemp.getStatus());

                mstVisitM.setIshdistribution(visitTemp.getIshdistribution());
                mstVisitM.setExetreaty(visitTemp.getExetreaty());
                mstVisitM.setSelfoccupancy(visitTemp.getSelfoccupancy());
                mstVisitM.setIscmpcollapse(visitTemp.getIscmpcollapse());

                mstVisitM.setSisconsistent(visitTemp.getSisconsistent());
                mstVisitM.setScondate(visitTemp.getScondate());

                mstVisitM.setPadcondate(visitTemp.getPadcondate());

                mstVisitM.setComid(visitTemp.getComid());
                mstVisitM.setRemarks(visitTemp.getRemarks());
                mstVisitM.setOrderbyno(visitTemp.getOrderbyno());// 同一次是0 返回时会改为1
                mstVisitM.setDeleteflag(visitTemp.getDeleteflag());
                mstVisitM.setVersion(visitTemp.getVersion());

                mstVisitM.setCredate(visitTemp.getCredate());
                mstVisitM.setCreuser(visitTemp.getCreuser());
                mstVisitM.setUpdatetime(visitTemp.getUpdatetime());
                mstVisitM.setUpdateuser(visitTemp.getUpdateuser());

                mstVisitM.setLongitude(visitTemp.getLongitude());
                mstVisitM.setLatitude(visitTemp.getLatitude());
                mstVisitM.setGpsstatus(visitTemp.getGpsstatus());
                mstVisitM.setIfminedate(visitTemp.getIfminedate());
                mstVisitM.setIfmine(visitTemp.getIfmine());
                mstVisitM.setVisitposition(visitTemp.getVisitposition());
                mstVisitM.setPadisconsistent("0");
                mstVisitM.setUploadFlag("1");
                //dao.create(mstVisitM);
                mitVisitMDao.create(mstVisitM);
            } catch (SQLException e) {
                Log.e(TAG, "复制到拜访主表正式表失败", e);
                e.printStackTrace();
            }
        }
    }




    /***
     * 去除拜访指标采集项重复
     * @param visitkey
     */
    public void delRepeatMstCollectionexerecordInfo(String visitkey) {
        try {
            Dao<MstCollectionexerecordInfo, String> collectionDao = DatabaseHelper.getHelper(context).getMstCollectionexerecordInfoDao();
            QueryBuilder<MstCollectionexerecordInfo, String> collectionQB = collectionDao.queryBuilder();
            Where<MstCollectionexerecordInfo, String> collectionWhere = collectionQB.where();
            collectionWhere.eq("visitkey", visitkey);
            collectionWhere.and();
            collectionWhere.ne("deleteflag", ConstValues.FLAG_1);
            collectionQB.orderBy("productkey", true);
            collectionQB.orderBy("colitemkey", true);
            collectionQB.orderBy("checkkey", false);
            collectionQB.orderBy("updatetime", false);
            List<MstCollectionexerecordInfo> collectionList = collectionQB.query();
            List<String> list = new ArrayList<String>();
            for (MstCollectionexerecordInfo collection : collectionList) {
                if (CheckUtil.isBlankOrNull(collection.getCheckkey())) {
                    collectionDao.delete(collection);
                } else {
                    String key = collection.getColitemkey() + collection.getProductkey() + collection.getCheckkey();
                    if (!list.contains(key)) {
                        list.add(key);
                    } else {
                        collectionDao.delete(collection);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取当前指标集合
     * @param tempDao
     * @param visitId
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("unused")
    private List<MstCheckexerecordInfoTemp> getCheckExerecordTempListByVisitid(Dao<MstCheckexerecordInfoTemp, String> tempDao,
                                                                               String visitId) throws SQLException {
        QueryBuilder<MstCheckexerecordInfoTemp, String> tempValueQb = tempDao.queryBuilder();
        Where<MstCheckexerecordInfoTemp, String> tempValueWr = tempValueQb.where();
        tempValueWr.eq("visitkey", visitId);
        tempValueQb.orderBy("terminalkey", true);
        tempValueQb.orderBy("checkkey", true);
        tempValueQb.orderBy("productkey", true);
        tempValueQb.orderBy("enddate", false);
        tempValueQb.orderBy("startdate", false);
        tempValueQb.orderBy("acresult", false);
        List<MstCheckexerecordInfoTemp> tempList = tempValueQb.query();
        return tempList;
    }

    /***
     * 获取指标临时表集合
     * @param tempList   指标临时表集合
     * @param isPro      是否关联产品
     */
    @SuppressWarnings({"unchecked", "unused"})
    private List<MstCheckexerecordInfoTemp> getCheckExerecordTemp(List<MstCheckexerecordInfoTemp> tempList, boolean isPro) {
        List<MstCheckexerecordInfoTemp> temps = new ArrayList<MstCheckexerecordInfoTemp>();
        for (MstCheckexerecordInfoTemp temp : tempList) {

            if (isPro) {// 关联产品
                if (!CheckUtil.isBlankOrNull(temp.getProductkey())) {
                    temps.add(temp);
                }
            } else {// 非关联产品
                if (CheckUtil.isBlankOrNull(temp.getProductkey())) {
                    temps.add(temp);
                }
            }
        }
        return temps;
    }

    /***
     * 获取指标值得默认值
     * @param checkKey
     * @return
     */
    private String getDefaultAcresult(String checkKey) {
        String acresult = "";
        if (PropertiesUtil.getProperties("check_puhuo").equals(checkKey)) {//#铺货状态
            acresult = "301";
        } else if (PropertiesUtil.getProperties("check_daoju").equals(checkKey)) {//#道具生动化
            acresult = "307";
        } else if (PropertiesUtil.getProperties("check_chanpin").equals(checkKey)) {//#产品生动化
            acresult = "309";
        } else if (PropertiesUtil.getProperties("check_bingdong").equals(checkKey)) {//#冰冻化
            acresult = "311";
        }
        return acresult;
    }

    /***
     * 获取指标正式表集合
     获取当前终端各指标的最新指标结果状态
     * @param //tempDao
     * @param //visitId
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("unused")
    private List<MstCheckexerecordInfo> getCheckExerecordList(Dao<MstCheckexerecordInfo, String> valueDao,
                                                              String termId) throws SQLException {
        QueryBuilder<MstCheckexerecordInfo, String> valueQb = valueDao.queryBuilder();
        Where<MstCheckexerecordInfo, String> valueWr = valueQb.where();
        valueWr.eq("terminalkey", termId);
        valueWr.and();
        valueWr.eq("enddate", "30001201");
        valueWr.and();
        valueWr.ne("deleteflag", ConstValues.delFlag);
        valueWr.and();
        valueWr.isNotNull("productkey");
        List<MstCheckexerecordInfo> valueLst = valueQb.query();
        return valueLst;
    }

}
