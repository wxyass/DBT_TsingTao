/**
 * 
 */
package et.tsingtaopad.db.dao;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import cn.com.benyoyo.manage.bs.IntStc.DataresultTerPurchaseDetailsStc;
import et.tsingtaopad.db.table.MitVisitM;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.main.operation.indexstatus.domain.IndexStatusStc;
import et.tsingtaopad.main.visit.tomorrowworkrecord.domain.DayWorkDetailStc;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: 终端进货详情DAO接口</br>
 */
public interface MitVisitMDao extends Dao<MitVisitM, String> {

}
