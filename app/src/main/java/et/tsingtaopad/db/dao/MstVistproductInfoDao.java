package et.tsingtaopad.db.dao;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.domain.ChatVieStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexQuicklyStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.domain.InvoicingStc;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: </br>
 */
public interface MstVistproductInfoDao extends Dao<MstVistproductInfo, String> {
    
    /**
     * 获取某次拜访的我品的进销存数据情况
     * 
     * @param helper
     * @param visitId   拜访主键
     * @return
     */
    public List<InvoicingStc> queryMinePro(DatabaseHelper helper, String visitId, String termKey);

    /**
     * 从临时表中 获取某次拜访的我品的进销存数据情况
     *
     * @param helper
     * @param visitId   拜访主键
     * @return
     */
    public List<InvoicingStc> queryMineProByTemp(DatabaseHelper helper, String visitId, String termKey);
    
    /**
     * 获取某次拜访的竞品的进销存数据情况
     * 
     * @param helper
     * @param visitId   拜访主键
     * @return
     */
    public List<ChatVieStc> queryViePro(DatabaseHelper helper, String visitId);

    /**
     * 获取某次拜访的竞品的进销存数据情况
     *
     * @param helper
     * @param visitId   拜访主键
     * @return
     */
    public List<ChatVieStc> queryVieProByTemp(DatabaseHelper helper, String visitId);
    
    /**
     * 获取巡店拜访-查指标的分项采集部分的产品指标数据
     * 
     * @param visitId       本次拜访ID
     * @param termId        本次拜访终端ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param seeFlag       查看标识
     * @return
     */
    public List<CheckIndexCalculateStc> queryCalculateIndex(DatabaseHelper helper,
                                                            String visitId, String termId, String channelId, String seeFlag);
    
    /**
     * 获取巡店拜访-查指标的分项采集部分的产品指标对应的采集项目数据
     * 
     * @param visitId       本次拜访ID
     * @param visitId   上次拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @return
     */
    public List<CheckIndexQuicklyStc> queryCalculateItem(
            DatabaseHelper helper, String visitId, String channelId);
    
    /**
     * 获取巡店拜访-查指标的分项采集部分的与产品无关的指标数据
     * 
     * @param visitId       拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param seeFlag       查看操作标识
     * @return
     */
    public List<CheckIndexCalculateStc> queryNoProIndex(
            DatabaseHelper helper, String visitId, String channelId, String seeFlag);

}
