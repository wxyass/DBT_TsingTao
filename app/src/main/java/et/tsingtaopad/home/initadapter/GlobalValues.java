package et.tsingtaopad.home.initadapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstSynckvM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.login.domain.LoginSession;

/**
 * Created by yangwenmin on 2017/12/25.
 * 系统常量配置,将旧Dbtplus的ConstValues类,拆成2部分:
 * ①et.tsingtaopad.core.util.dbtutil.ConstValues  //定义一些不变的常量(除handler)
 * ②et.tsingtaopad.GlobalValues. // 放置一些集合,使用前会将数据加载到内存中
 */

public class GlobalValues {

    // 登录信息缓存Key
    public static final String LOGINSESSIONKEY = "loginSesion";
    // 打招呼是否读取完整
    public static final String SAYHIREADY = "sayhiready";
    // 判断购物车是协同,还是追溯  1协同  2追溯
    public static final String DDXTZS = "ddxtzs";
    //今日要事是否允许提醒
    public static boolean isDayThingWarn = false;

    // 协同拜访上传成功
    public static final int SINGLE_UP_SUC = 110;
    // 协同拜访上传失败
    public static final int SINGLE_UP_FAIL = 111;

    // 最后一次点击路线的时间
    public static final String ROUNTE_TIME = "route_time";

    // TODO hongen 删除new LoginSession()  登录Session
    public static LoginSession loginSession = new LoginSession();

    // KV表, K:表名， V：kv对象
    public static Map<String, MstSynckvM> kvMap = new HashMap<String, MstSynckvM>();

    // 数据字典
    public static Map<String, List<KvStc>> dataDicMap = new HashMap<String, List<KvStc>>();

    // 省市县
    public static List<KvStc> provLst = new ArrayList<KvStc>();

    // 所属线路
    public static List<MstRouteM> lineLst = new ArrayList<MstRouteM>();

    // 我品经销商供货关系
    public static List<KvStc> agencyMineLst = new ArrayList<KvStc>();

    // 竞品经销商供货关系
    public static List<KvStc> agencyVieLst = new ArrayList<KvStc>();

    // 指标、指标值关联关系
    public static List<KvStc> indexLst = new ArrayList<KvStc>();

    // 可拜访经销
    public static List<KvStc> agencyVisitLst = new ArrayList<KvStc>();

    //连接mq服务器创建的连接
    //public static IMqttClient mqttClient = null;

    //连接mq服务器创建的连接
    public static HashMap<String, Integer> AuthorityMap = new HashMap<String, Integer>();

    // 打招呼填写信息是否完整
    public static boolean isSayHiSure = true;
}
