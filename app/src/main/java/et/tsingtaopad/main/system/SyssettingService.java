package et.tsingtaopad.main.system;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.NetStatusUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.table.MstCheckexerecordInfo;
import et.tsingtaopad.db.table.MstCollectionexerecordInfo;
import et.tsingtaopad.db.table.MstPromotermInfo;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.util.requestHeadUtil;


/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：SyssettingService.java</br> 
 * 作者：@ray </br>
 * 创建时间：2013/11/2</br> 
 * 功能描述: 系统设置的业务逻辑</br> 
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
public class SyssettingService {

	private final String TAG = "SyssettingService";
	private Context context;
	private Handler handler;

	public SyssettingService(Context context,Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/**
	 * 修改密码
	 * 
	 * @param pwd
	 *            密码
	 * @param newpwd
	 *            新密码
	 * @param repeatpwd
	 *            重复密码
	 * @return
	 */
	public void changePwd(final String pwd,
	                final String newpwd, final String repeatpwd) {
		int msg = -1;
		if (CheckUtil.isBlankOrNull(pwd)) {
			msg = R.string.sys_msg_psd1;// 用户密码不能为空
			
		//} else if (!pwd.equals(ConstValues.loginSession.getUserPwd())) {
		} else if (!pwd.equals(PrefUtils.getString(context, "userPwd", ""))) {
            msg = R.string.sys_msg_psd4;// 当前密码输入不正确，请重新输入
            
        } else if ("a1234567".equals(newpwd)) {
        	msg = R.string.sys_msg_psd9;// 新密码不能修改成原始密码
		
        } 
		else if (CheckUtil.isBlankOrNull(newpwd)) {
			msg = R.string.sys_msg_psd2;// 新密码不能为空
			
		} else if (CheckUtil.isBlankOrNull(repeatpwd)) {
			msg = R.string.sys_msg_psd3;// 重复密码不能为空
			
		}  else if (!newpwd.equals(repeatpwd)) {
		    msg = R.string.sys_msg_psd5;//新密码两次设置不一致
			
		}  else if (pwd.equals(repeatpwd)) {
            msg = R.string.sys_msg_psd6;// 新密码与原密码不能相同
            
		}  else if (newpwd.length()<8) {
			msg = R.string.sys_msg_psd7;// 新密码长度需大于等于8位
			
		}  else if (!newpwd.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$")) {
			msg = R.string.sys_msg_psd8;// 新密码必须包含字母和阿拉伯数字
			
        } else if (!NetStatusUtil.isNetValid(context)) {
			msg = R.string.sys_msg_net_fail;// 网络状态不可用，请确保网络状态及网络设置是否正确
		}
		// 弹出提示信息
		if (msg != -1) {
			//ViewUtil.sendMsg(context, msg);
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

		} else {
			StringBuffer buffer = new StringBuffer();
			buffer.append("{userId:'").append(PrefUtils.getString(context, "userCode", ""));
			buffer.append("',oldPwd:'").append(pwd);
			buffer.append("',newPwd:'").append(newpwd);
			buffer.append("',nffirmNewPwd:'").append(repeatpwd).append("'}");

			// 组建请求Json
			RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(context);
			requestHeadStc.setOptcode(PropertiesUtil.getProperties("opt_sys_changepassword"));
			RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, buffer.toString());

			// 压缩请求数据
			String jsonZip = "";
			try {
				jsonZip = JsonUtil.toJson(reqObj);
				//jsonZip = new String(ZipHelper.zipString(JsonUtil.toJson(reqObj)), "ISO-8859-1");
				//配合朱志凯测试给他提取json语句进行压力测试
				//FileTool.writeTxt(JsonUtil.toJson(reqObj),FileTool.getSDPath()+"/ceshi.txt");
			} catch (Exception e1) {
				//Log.e(TAG, "压缩上传数据JSON失败", e1);
			}

			RestClient.builder()
					.url(HttpUrl.IP_END)
					.params("data", jsonZip)
					.loader(context)
					.success(new ISuccess() {
						@Override
						public void onSuccess(String response) {

							/*ResponseStructBean resObj = HttpParseJson.parseRes(response);
							if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
								SharedPreferences sp = context.getSharedPreferences(ConstValues.LOGINSESSIONKEY, 0);
								if (sp != null) {
									sp.edit().putString("userPwd", newpwd).commit();
									//ViewUtil.sendMsg(context, R.string.sys_msg_succes);
									Toast.makeText(context, R.string.sys_msg_succes, 0).show();
								}
								ConstValues.loginSession.setUserPwd(newpwd);
								Message message = new Message();
								message.what = ConstValues.WAIT1;
								handler.sendMessage(message);
							} else {
								//ViewUtil.sendMsg(context, R.string.sys_msg_fail);
								String msg1 = FunUtil.isNullSetSpace(resObj.getResBody().getContent());
								Toast.makeText(context, msg1, 0).show();
							}*/

							Message message = new Message();
							message.what = ConstValues.WAIT1;
							handler.sendMessage(message);
						}
					})
					.error(new IError() {
						@Override
						public void onError(int code, String msg) {
							//Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
							Message message = new Message();
							message.what = ConstValues.WAIT2;
							handler.sendMessage(message);
						}
					})
					.failure(new IFailure() {
						@Override
						public void onFailure() {
							Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
						}
					})
					.builde()
					.post();

		}
	}
	
	/***
	 * 保留最新拜访记录
	 */
	public boolean delVisit(){
	    boolean isFlag=false;
	    try
	    {
	        DatabaseHelper helper=DatabaseHelper.getHelper(context);
            List<String> visitKeyList = new ArrayList<String>();
            StringBuffer buffer = new StringBuffer();
            buffer.append("select vm1.visitkey visitkey from mst_visit_m vm1, ");
            buffer.append("(select vm.terminalkey,max(vm.visitdate) visitdate from mst_visit_m vm group by vm.terminalkey)vm2 ");
            buffer.append("where vm1.terminalkey=vm2.terminalkey ");
            buffer.append("and vm1.visitdate=vm2.visitdate ");
            buffer.append("order by vm1.terminalkey,vm1.visitdate desc ");
            
            Cursor cursor = helper.getReadableDatabase().rawQuery(buffer.toString(), null);
            while (cursor.moveToNext()) {
                String visitkey=cursor.getString(cursor.getColumnIndex("visitkey"));
                visitKeyList.add(visitkey);
            }
            
            String keys = FunUtil.brackReplace(visitKeyList);
            //拜访产品表
            //Dao<MstVistproductInfoDao, String> visitProductDao = helper.getDao(MstVistproductInfo.class);
            Dao<MstVistproductInfo, String> visitProductDao = helper.getDao(MstVistproductInfo.class);
            buffer = new StringBuffer();
            buffer.append("delete from MST_VISTPRODUCT_INFO ");
            buffer.append("where visitkey not in (");
            buffer.append(keys);
            buffer.append(")");
            visitProductDao.executeRaw(buffer.toString());
            //获取最新拜访指标状态（非产品）
            //Dao<MstCheckexerecordInfoDao, String> checkExeRecordInfoDao = helper.getDao(MstCheckexerecordInfo.class);
            Dao<MstCheckexerecordInfo, String> checkExeRecordInfoDao = helper.getDao(MstCheckexerecordInfo.class);
            buffer = new StringBuffer();
            buffer.append("delete from MST_CHECKEXERECORD_INFO ");
            buffer.append("where productkey is null and visitkey not in (");
            buffer.append(keys);
            buffer.append(")");
            checkExeRecordInfoDao.executeRaw(buffer.toString());
            //获取最新拜访指标采集项
            Dao<MstCollectionexerecordInfo, String> collectionExeRecordInfoDao = helper.getDao(MstCollectionexerecordInfo.class);
            buffer = new StringBuffer();
            buffer.append("delete from mst_collectionexerecord_info ");
            buffer.append("where visitkey not in (");
            buffer.append(keys);
            buffer.append(")");
            collectionExeRecordInfoDao.executeRaw(buffer.toString());
            //获取最新拜访指标采集项
            Dao<MstPromotermInfo, String> mstPromotermInfoDao = helper.getDao(MstPromotermInfo.class);
            buffer = new StringBuffer();
            buffer.append("delete from mst_promoterm_info ");
            buffer.append("where visitkey not in (");
            buffer.append(keys);
            buffer.append(")");
            mstPromotermInfoDao.executeRaw(buffer.toString());
            //拜访
            //Dao<MstVisitMDao, String> visitDao = helper.getDao(MstVisitM.class);
            Dao<MstVisitM, String> visitDao = helper.getDao(MstVisitM.class);
            buffer = new StringBuffer();
            buffer.append("delete from MST_VISIT_M ");
            buffer.append("where visitkey not in (");
            buffer.append(keys);
            buffer.append(")");
            visitDao.executeRaw(buffer.toString());
            isFlag=true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	    return isFlag;
	}
}
