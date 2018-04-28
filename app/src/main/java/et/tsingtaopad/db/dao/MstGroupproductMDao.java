/**
 * 
 */
package et.tsingtaopad.db.dao;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import cn.com.benyoyo.manage.bs.IntStc.DataresultTerPurchaseDetailsStc;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.table.MstGroupproductM;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.main.operation.indexstatus.domain.IndexStatusStc;
import et.tsingtaopad.main.visit.tomorrowworkrecord.domain.DayWorkDetailStc;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: 终端进货详情DAO接口</br>
 */
public interface MstGroupproductMDao  extends Dao<MstGroupproductM, String> {

    /**
	 * @param helper
	 * @param terminalcode
	 * @param createdate
	 * @return
	 */
	public List<MstGroupproductM> queryMstGroupproductMByCreatedate(DatabaseHelper helper, String terminalcode, String createdate);

	/**
	 * 追溯 根据终端code查找记录
	 * @param helper
	 * @param terminalcode
	 * @return
	 */
	public List<MstGroupproductM> queryZsMstGroupproductMByCreatedate(DatabaseHelper helper, String terminalcode);
}
