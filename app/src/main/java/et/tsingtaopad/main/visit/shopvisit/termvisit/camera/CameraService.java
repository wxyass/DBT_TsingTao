package et.tsingtaopad.main.visit.shopvisit.termvisit.camera;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.dao.MstPictypeMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.table.MstCameraInfoM;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.main.visit.shopvisit.termvisit.ShopVisitService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;


/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：CameraService.java</br>
 * 作者：Administrator   </br>
 * 创建时间：2015-11-19</br>      
 * 功能描述: 巡店拜访拍照业务逻辑</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class CameraService extends ShopVisitService {
    
    private final String TAG = "CameraService";
    //private Context context;

    public CameraService(Context context, Handler handler) {
        super(context, handler);
        //this.context = context;
    }

    /***
     * 查询图片类型表中所有记录
     */
    public List<CameraInfoStc> getPictypeMAll(){
        // 事务控制
        AndroidDatabaseConnection connection = null;
        List<CameraInfoStc> valueLst =new ArrayList<CameraInfoStc>();
        try {

            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstPictypeMDao dao = (MstPictypeMDao) helper.getMstpictypeMDao();

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            valueLst =dao.queryPictypeM(helper);
            connection.commit(null);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "查询图片类型表中所有记录", e);
        }
        return valueLst;
    }

    /**
     * 更新图片表记录上传状态
     * 
     * @param //helper
     * @param //visitId   拜访主键s
     * @return
     */
    public void updataUpstatusbyCameraKey(String camerakey) {
        
    	// 事务控制
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
            
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            
            dao.updataUploadbyCameraKey(helper, camerakey);
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
    }
    
    /**
     * 将图片表的记录 上传状态全部改为1(已上传)
     */
    public void updataUpstatus() {
    	// 事务控制
        AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
    		dao.updataUploadstatus(helper);
    		connection.commit(null);
    	} catch (SQLException e) {
    		Log.e(TAG, "将图片表的记录 上传状态全部改为1失败", e);
    	}
    }
    
    /**
     * 更改此次拜访所有需上传图片sureup状态为 0确定上传
     * 0确定上传  1原始状态未确定上传 null未确定上传
     */
    public void updataSureup(String visitId){
    	// 事务控制
        AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
    		dao.updataSureupstatus(helper, visitId);
    		connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
    }
    
    
    /**
	 * 根据图片主键 获取图片的本地路径
	 * 
	 * @param //helper
	 * @param camerakey
	 */
    public String getPathbyCameraKey(String camerakey){
    	String path = "";
    	// 事务控制
        AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
    		path = dao.getLocalpathbyCameraKey(helper, camerakey);
    		connection.commit(null);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
		return path;
    }
    /**
     * 根据(图片类型,终端key,visitkey) 获取图片的本地路径
     * 
     * @param //helper
     * @param //camerakey
     */
    public String getPathbyCameratype(String cameratype,String termkey,String visitkey){
    	String path = "";
    	// 事务控制
        AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
    		path = dao.getLocalpathbyCameratype(helper, cameratype,termkey,visitkey);
    		connection.commit(null);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	return path;
    }
    /**
     * 根据图片主键删除一条记录
     * 
     * @param //helper
     * @param camerakey
     */
    public void deletePicByCamerakey(String camerakey){
    	// 事务控制
        AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
    		dao.deletePicByCamerakey(helper, camerakey);
    		connection.commit(null);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    }
    /**
     * 根据(图片类型,终端key,visitkey)删除一条记录
     * 
     * @param //helper
     * @param //camerakey
     */
    public void deletePicByCameratype(String cameratype,String termkey,String visitkey){
    	// 事务控制
        AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
    		dao.deletePicByCameratype(helper, cameratype,termkey,visitkey);
    		connection.commit(null);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    }
    
    /**
     * 根据(终端key,visitkey)删除 图片记录
     * 
     * @param //helper
     * @param //camerakey
     */
    public void deletePicBytermkey(String termkey,String visitkey){
    	// 事务控制
        AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
    		dao.deletePicBytermkeyandvisitkey(helper, termkey,visitkey);
    		connection.commit(null);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    }
    
    /**
     * 获取终端信息
     * 
     * @param termId    终端ID
     * @return
     */
    	public MstTerminalinfoM findTermStcBytermId(String termId) {
            
            MstTerminalinfoM termInfo = null;
         // 事务控制
            AndroidDatabaseConnection connection = null;
            try {
                DatabaseHelper helper = DatabaseHelper.getHelper(context);
                MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
                connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
                connection.setAutoCommit(false);
                termInfo = dao.queryForId(termId);
                connection.commit(null);
                
            } catch (SQLException e) {
                Log.e(TAG, "获取终端表DAO对象失败", e);
            }
            
            return termInfo;
        }
    
    /**
     * 添加图片记录到到图片表
     * @param info
     */
	public void saveCamera1(final MstCameraInfoM info) {
		DbtLog.logUtils(TAG, "saveCamera()-保存一条图片表记录");
		// 保存
		AndroidDatabaseConnection connection = null;
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			Dao<MstCameraInfoM, String> mstCameraiInfoMDao = helper.getMstCameraiInfoMDao();
			connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			
			
			// 保存图片记录
			mstCameraiInfoMDao.create(info);
			connection.commit(null);


		} catch (Exception e) {
			Log.e(TAG, "新增图片记录失败", e);
			try {
				//connection.rollback(null);
			} catch (Exception e1) {
				Log.e(TAG, "新增图片记录失败后回滚失败", e1);
			}
		}
	}
	
	/**
     * 添加图片记录到到图片表 
     * 有问题 没事务
     * @param info
     */
	public void saveCamera3(final MstCameraInfoM info) {
		DbtLog.logUtils(TAG, "saveCamera()-保存一条图片表记录");
		// 保存
		AndroidDatabaseConnection connection = null;
		try {
			
			
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
            dao.insertMstCameraiInfoM(helper,info);
            connection.commit(null);
			// 保存图片记录
			//mstCameraiInfoMDao.create(info);
			//connection.commit(null);


		} catch (Exception e) {
			Log.e(TAG, "新增图片记录失败", e);
			try {
				connection.rollback(null);
			} catch (SQLException e1) {
				Log.e(TAG, "新增图片记录失败后回滚失败", e1);
			}
		}
	}
    
}
