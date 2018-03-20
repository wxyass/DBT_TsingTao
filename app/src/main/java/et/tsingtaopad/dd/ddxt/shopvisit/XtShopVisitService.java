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
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import cn.com.benyoyo.manage.bs.IntStc.BsVisitEmpolyeeStc;

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


    

}
