package et.tsingtaopad.dd.ddzs.zssayhi;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.AlertKeyValueAdapter;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DbtUtils;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnDismissListener;
import et.tsingtaopad.core.view.alertview.OnItemClickListener;
import et.tsingtaopad.db.table.MitValterMTemp;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddzs.zsshopvisit.ZsVisitShopActivity;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class ZsSayhiFragment extends XtBaseVisitFragment implements View.OnClickListener,OnItemClickListener, OnDismissListener {

    private final String TAG = "ZsSayhiFragment";

    private ZsSayhiService xtSayhiService;
    private MstTerminalinfoMTemp termInfoTemp;
    private MstVisitMTemp visitMTemp;
    private MitValterMTemp mitValterMTemp;

    private String aday;
    private Calendar calendar;
    private int yearr;
    private int month;
    private int day;
    private String ifminedate;

    private AlertView mAlertViewExt;//窗口拓展例子

    private List<MstRouteM> mstRouteList = new ArrayList<>();
    private List<KvStc> dataDicByTermLevelLst = new ArrayList<>();
    private List<KvStc> dataDicByAreaTypeLst = new ArrayList<>();
    private List<KvStc> sellchanneldataLst = new ArrayList<>();
    private List<KvStc> visitpositionLst = new ArrayList<>();
    private List<KvStc> sellchannelLst = new ArrayList<>();
    private List<KvStc> mainchannelLst = new ArrayList<>();
    private List<KvStc> minorchannelLst = new ArrayList<>();

    //public static final int FINISH_SUC = 11;// 初始化成功发送信息到主UI (废弃)
    public static final int NOT_TERMSTATUS = 12;// 失效终端
    public static final int DATA_ARROR = 13;// 打招呼页面信息不全
    public static final int SHOW_INIT_PROGRESS = 14;// 数据初始化-开启滚动条
    public static final int CLOSE_INIT_PROGRESS = 15;// 初始化成功-关闭滚动条
    public static final int INIT_DATA = 16;// 修改完数据 刷新界面
    MyHandler handler;


    // ------------------------------------------------------------
    private ScrollView sayhi_scrollview;
    private RelativeLayout zdzs_sayhi_rl_termstatus;
    private TextView zdzs_sayhi_tv_termstatus_con1;
    private TextView zdzs_sayhi_tv_termstatus_statue;

    private RelativeLayout zdzs_sayhi_rl_visitstatus;
    private TextView zdzs_sayhi_tv_visitstatus_con1;
    private TextView zdzs_sayhi_tv_visitstatus_statue;

    private RelativeLayout zdzs_sayhi_rl_wopindianzhao;
    private TextView zdzs_sayhi_rl_wopindianzhao_con1;
    private TextView zdzs_sayhi_rl_wopindianzhao_con2;
    private TextView zdzs_sayhi_rl_wopindianzhao_statue;

    private RelativeLayout zdzs_sayhi_rl_selfstatus;
    private TextView zdzs_sayhi_rl_selfstatus_con1;
    private TextView zdzs_sayhi_rl_selfstatus_con2;
    private TextView zdzs_sayhi_rl_selfstatus_statue;

    private RelativeLayout zdzs_sayhi_rl_cmpstatus;
    private TextView zdzs_sayhi_rl_cmpstatus_con1;
    private TextView zdzs_sayhi_rl_cmpstatus_con2;
    private TextView zdzs_sayhi_rl_cmpstatus_statue;

    private RelativeLayout zdzs_sayhi_rl_selfprotocol;
    private TextView zdzs_sayhi_rl_selfprotocol_con1;
    private TextView zdzs_sayhi_rl_selfprotocol_con2;
    private TextView zdzs_sayhi_rl_selfprotocol_statue;

    private RelativeLayout zdzs_sayhi_rl_cmpprotocol;
    private TextView zdzs_sayhi_rl_cmpprotocol_con1;
    private TextView zdzs_sayhi_rl_cmpprotocol_con2;
    private TextView zdzs_sayhi_rl_cmpprotocol_statue;

    private RelativeLayout zdzs_sayhi_rl_termname;
    private TextView zdzs_sayhi_rl_termname_con1;
    private TextView zdzs_sayhi_rl_termname_statue;

    private RelativeLayout zdzs_sayhi_rl_termcode;
    private TextView zdzs_sayhi_rl_termcode_con1;
    private TextView zdzs_sayhi_rl_termcode_statue;
    private RelativeLayout zdzs_sayhi_rl_belongline;
    private TextView zdzs_sayhi_rl_belongline_con1;
    private TextView zdzs_sayhi_rl_belongline_statue;
    private RelativeLayout zdzs_sayhi_rl_level;
    private TextView zdzs_sayhi_rl_level_con1;
    private TextView zdzs_sayhi_rl_level_statue;
    private RelativeLayout zdzs_sayhi_rl_prov;
    private TextView zdzs_sayhi_rl_prov_con1;
    private TextView zdzs_sayhi_rl_prov_statue;
    private RelativeLayout zdzs_sayhi_rl_city;
    private TextView zdzs_sayhi_rl_city_con1;
    private TextView zdzs_sayhi_rl_city_statue;
    private RelativeLayout zdzs_sayhi_rl_country;
    private TextView zdzs_sayhi_rl_country_con1;
    private TextView zdzs_sayhi_rl_country_statue;
    private RelativeLayout zdzs_sayhi_rl_address;
    private TextView zdzs_sayhi_rl_address_con1;
    private TextView zdzs_sayhi_rl_address_statue;
    private RelativeLayout zdzs_sayhi_rl_person;
    private TextView zdzs_sayhi_rl_person_con1;
    private TextView zdzs_sayhi_rl_person_statue;
    private RelativeLayout zdzs_sayhi_rl_tel;
    private TextView zdzs_sayhi_rl_tel_con1;
    private TextView zdzs_sayhi_rl_tel_statue;
    private RelativeLayout zdzs_sayhi_rl_cycle;
    private TextView zdzs_sayhi_rl_cycle_con1;
    private TextView zdzs_sayhi_rl_cycle_statue;
    private RelativeLayout zdzs_sayhi_rl_sequence;
    private TextView zdzs_sayhi_rl_sequence_con1;
    private TextView zdzs_sayhi_rl_sequence_statue;
    private RelativeLayout zdzs_sayhi_rl_hvolume;
    private TextView zdzs_sayhi_rl_hvolume_con1;
    private TextView zdzs_sayhi_rl_hvolume_statue;
    private RelativeLayout zdzs_sayhi_rl_mvolume;
    private TextView zdzs_sayhi_rl_mvolume_con1;
    private TextView zdzs_sayhi_rl_mvolume_statue;
    private RelativeLayout zdzs_sayhi_rl_pvolume;
    private TextView zdzs_sayhi_rl_pvolume_con1;
    private TextView zdzs_sayhi_rl_pvolume_statue;
    private RelativeLayout zdzs_sayhi_rl_lvolume;
    private TextView zdzs_sayhi_rl_lvolume_con1;
    private TextView zdzs_sayhi_rl_lvolume_statue;
    private RelativeLayout zdzs_sayhi_rl_totalvolume;
    private TextView zdzs_sayhi_rl_totalvolume_con1;
    private TextView zdzs_sayhi_rl_totalvolume_statue;
    private RelativeLayout zdzs_sayhi_rl_areatype;
    private TextView zdzs_sayhi_rl_areatype_con1;
    private TextView zdzs_sayhi_rl_areatype_statue;
    private RelativeLayout zdzs_sayhi_rl_sellchannel;
    private TextView zdzs_sayhi_rl_sellchannel_con1;
    private TextView zdzs_sayhi_rl_sellchannel_statue;
    private RelativeLayout zdzs_sayhi_rl_mainchannel;
    private TextView zdzs_sayhi_rl_mainchannel_con1;
    private TextView zdzs_sayhi_rl_mainchannel_statue;
    private RelativeLayout zdzs_sayhi_rl_minorchannel;
    private TextView zdzs_sayhi_rl_minorchannel_con1;
    private TextView zdzs_sayhi_rl_minorchannel_statue;
    private RelativeLayout zdzs_sayhi_rl_visitperson;
    private TextView zdzs_sayhi_rl_visitperson_con1;
    private TextView zdzs_sayhi_rl_visitperson_statue;




    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<ZsSayhiFragment> fragmentRef;

        public MyHandler(ZsSayhiFragment fragment) {
            fragmentRef = new SoftReference<ZsSayhiFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ZsSayhiFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }


            // 处理UI 变化
            switch (msg.what) {
                /*case FINISH_SUC:
                    fragment.initData2();
                    break;*/
                case NOT_TERMSTATUS:
                    fragment.closeXtTermstatusSw();
                    break;
                case DATA_ARROR:
                    int res = (int)msg.obj;
                    fragment.showErrorMsg(res);

                    break;
                case SHOW_INIT_PROGRESS:
                    fragment.showXtSayHiDialog();
                    break;
                case CLOSE_INIT_PROGRESS:
                    fragment.closeXtSayHiDialog();
                    fragment.initData3();
                    break;

                case INIT_DATA:
                    fragment.initData3();
                    break;
            }
        }
    }

    // 展示错误信息
    private void showErrorMsg(int res) {
        Toast.makeText(getActivity(),getString(res),Toast.LENGTH_SHORT).show();
        //requestFocus(res);
    }



    // 取消终端失效
    private void closeXtTermstatusSw() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_sayhi, container, false);

        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

        //-----------------------------------------------------------
        sayhi_scrollview = (ScrollView) view.findViewById(R.id.sayhi_scrollview);
        zdzs_sayhi_rl_termstatus = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_termstatus);
        zdzs_sayhi_tv_termstatus_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_termstatus_con1);
        zdzs_sayhi_tv_termstatus_statue = (TextView)view. findViewById(R.id.zdzs_sayhi_tv_termstatus_statue);

        zdzs_sayhi_rl_visitstatus = (RelativeLayout)view. findViewById(R.id.zdzs_sayhi_rl_visitstatus);
        zdzs_sayhi_tv_visitstatus_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_visitstatus_con1);
        zdzs_sayhi_tv_visitstatus_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_tv_visitstatus_statue);

        zdzs_sayhi_rl_wopindianzhao = (RelativeLayout)view. findViewById(R.id.zdzs_sayhi_rl_wopindianzhao);
        zdzs_sayhi_rl_wopindianzhao_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_wopindianzhao_con1);
        zdzs_sayhi_rl_wopindianzhao_con2 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_wopindianzhao_con2);
        zdzs_sayhi_rl_wopindianzhao_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_wopindianzhao_statue);

        // 我品销售
        zdzs_sayhi_rl_selfstatus = (RelativeLayout)view. findViewById(R.id.zdzs_sayhi_rl_selfstatus);
        zdzs_sayhi_rl_selfstatus_con1 = (TextView)view. findViewById(R.id.zdzs_sayhi_rl_selfstatus_con1);
        zdzs_sayhi_rl_selfstatus_con2 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_selfstatus_con2);
        zdzs_sayhi_rl_selfstatus_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_selfstatus_statue);

        // 竞品销售
        zdzs_sayhi_rl_cmpstatus = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_cmpstatus);
        zdzs_sayhi_rl_cmpstatus_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_cmpstatus_con1);
        zdzs_sayhi_rl_cmpstatus_con2 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_cmpstatus_con2);
        zdzs_sayhi_rl_cmpstatus_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_cmpstatus_statue);


        // 我品合作
        zdzs_sayhi_rl_selfprotocol = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_selfprotocol);
        zdzs_sayhi_rl_selfprotocol_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_selfprotocol_con1);
        zdzs_sayhi_rl_selfprotocol_con2 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_selfprotocol_con2);
        zdzs_sayhi_rl_selfprotocol_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_selfprotocol_statue);


        // 竞品合作
        zdzs_sayhi_rl_cmpprotocol = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_cmpprotocol);
        zdzs_sayhi_rl_cmpprotocol_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_cmpprotocol_con1);
        zdzs_sayhi_rl_cmpprotocol_con2 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_cmpprotocol_con2);
        zdzs_sayhi_rl_cmpprotocol_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_cmpprotocol_statue);


        zdzs_sayhi_rl_termname = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_termname);
        zdzs_sayhi_rl_termname_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_termname_con1);
        zdzs_sayhi_rl_termname_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_termname_statue);

        zdzs_sayhi_rl_termcode = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_termcode);
        zdzs_sayhi_rl_termcode_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_termcode_con1);
        zdzs_sayhi_rl_termcode_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_termcode_statue);

        zdzs_sayhi_rl_belongline = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_belongline);
        zdzs_sayhi_rl_belongline_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_belongline_con1);
        zdzs_sayhi_rl_belongline_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_belongline_statue);

        zdzs_sayhi_rl_level = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_level);
        zdzs_sayhi_rl_level_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_level_con1);
        zdzs_sayhi_rl_level_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_level_statue);

        zdzs_sayhi_rl_prov = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_prov);
        zdzs_sayhi_rl_prov_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_prov_con1);
        zdzs_sayhi_rl_prov_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_prov_statue);

        zdzs_sayhi_rl_city = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_city);
        zdzs_sayhi_rl_city_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_city_con1);
        zdzs_sayhi_rl_city_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_city_statue);

        zdzs_sayhi_rl_country = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_country);
        zdzs_sayhi_rl_country_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_country_con1);
        zdzs_sayhi_rl_country_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_country_statue);

        zdzs_sayhi_rl_address = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_address);
        zdzs_sayhi_rl_address_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_address_con1);
        zdzs_sayhi_rl_address_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_address_statue);

        zdzs_sayhi_rl_person = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_person);
        zdzs_sayhi_rl_person_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_person_con1);
        zdzs_sayhi_rl_person_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_person_statue);

        zdzs_sayhi_rl_tel = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_tel);
        zdzs_sayhi_rl_tel_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_tel_con1);
        zdzs_sayhi_rl_tel_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_tel_statue);

        zdzs_sayhi_rl_cycle = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_cycle);
        zdzs_sayhi_rl_cycle_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_cycle_con1);
        zdzs_sayhi_rl_cycle_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_cycle_statue);

        zdzs_sayhi_rl_sequence = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_sequence);
        zdzs_sayhi_rl_sequence_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_sequence_con1);
        zdzs_sayhi_rl_sequence_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_sequence_statue);

        zdzs_sayhi_rl_hvolume = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_hvolume);
        zdzs_sayhi_rl_hvolume_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_hvolume_con1);
        zdzs_sayhi_rl_hvolume_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_hvolume_statue);

        zdzs_sayhi_rl_mvolume = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_mvolume);
        zdzs_sayhi_rl_mvolume_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_mvolume_con1);
        zdzs_sayhi_rl_mvolume_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_mvolume_statue);

        zdzs_sayhi_rl_pvolume = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_pvolume);
        zdzs_sayhi_rl_pvolume_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_pvolume_con1);
        zdzs_sayhi_rl_pvolume_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_pvolume_statue);

        zdzs_sayhi_rl_lvolume = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_lvolume);
        zdzs_sayhi_rl_lvolume_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_lvolume_con1);
        zdzs_sayhi_rl_lvolume_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_lvolume_statue);

        zdzs_sayhi_rl_totalvolume = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_totalvolume);
        zdzs_sayhi_rl_totalvolume_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_totalvolume_con1);
        zdzs_sayhi_rl_totalvolume_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_totalvolume_statue);

        zdzs_sayhi_rl_areatype = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_areatype);
        zdzs_sayhi_rl_areatype_con1 = (TextView)view. findViewById(R.id.zdzs_sayhi_rl_areatype_con1);
        zdzs_sayhi_rl_areatype_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_areatype_statue);

        zdzs_sayhi_rl_sellchannel = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_sellchannel);
        zdzs_sayhi_rl_sellchannel_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_sellchannel_con1);
        zdzs_sayhi_rl_sellchannel_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_sellchannel_statue);

        zdzs_sayhi_rl_mainchannel = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_mainchannel);
        zdzs_sayhi_rl_mainchannel_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_mainchannel_con1);
        zdzs_sayhi_rl_mainchannel_statue = (TextView)view. findViewById(R.id.zdzs_sayhi_rl_mainchannel_statue);

        zdzs_sayhi_rl_minorchannel = (RelativeLayout)view. findViewById(R.id.zdzs_sayhi_rl_minorchannel);
        zdzs_sayhi_rl_minorchannel_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_minorchannel_con1);
        zdzs_sayhi_rl_minorchannel_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_minorchannel_statue);

        zdzs_sayhi_rl_visitperson = (RelativeLayout) view.findViewById(R.id.zdzs_sayhi_rl_visitperson);
        zdzs_sayhi_rl_visitperson_con1 = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_visitperson_con1);
        zdzs_sayhi_rl_visitperson_statue = (TextView) view.findViewById(R.id.zdzs_sayhi_rl_visitperson_statue);

        zdzs_sayhi_rl_termstatus.setOnClickListener(this);
        zdzs_sayhi_rl_visitstatus.setOnClickListener(this);
        zdzs_sayhi_rl_wopindianzhao.setOnClickListener(this);

        zdzs_sayhi_rl_selfstatus.setOnClickListener(this);
        zdzs_sayhi_rl_cmpstatus.setOnClickListener(this);
        zdzs_sayhi_rl_selfprotocol.setOnClickListener(this);
        zdzs_sayhi_rl_cmpprotocol.setOnClickListener(this);


        zdzs_sayhi_rl_termname.setOnClickListener(this);
        zdzs_sayhi_rl_termcode.setOnClickListener(this);
        zdzs_sayhi_rl_address.setOnClickListener(this);
        zdzs_sayhi_rl_person.setOnClickListener(this);
        zdzs_sayhi_rl_tel.setOnClickListener(this);
        zdzs_sayhi_rl_sequence.setOnClickListener(this);

        zdzs_sayhi_rl_cycle.setOnClickListener(this);
        zdzs_sayhi_rl_hvolume.setOnClickListener(this);
        zdzs_sayhi_rl_mvolume.setOnClickListener(this);
        zdzs_sayhi_rl_pvolume.setOnClickListener(this);
        zdzs_sayhi_rl_lvolume.setOnClickListener(this);

        zdzs_sayhi_rl_belongline.setOnClickListener(this);
        zdzs_sayhi_rl_level.setOnClickListener(this);
        zdzs_sayhi_rl_country.setOnClickListener(this);
        zdzs_sayhi_rl_areatype.setOnClickListener(this);
        zdzs_sayhi_rl_minorchannel.setOnClickListener(this);
        zdzs_sayhi_rl_visitperson.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);
        xtSayhiService = new ZsSayhiService(getActivity(), handler);


        PrefUtils.putBoolean(getActivity(),GlobalValues.SAYHIREADY,true);
        // 初始化页面数据
        initViewData();// 子线程查找数据

    }

    // 子线程查找数据
    private void initViewData(){

        // 弹出提示对话框
        Message message = new Message();
        message.what = SHOW_INIT_PROGRESS;
        handler.sendMessage(message);// 提示:图片正在保存,请稍后

        Thread thread = new Thread() {

            @Override
            public void run() {
                try{
                    initData();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    Message message = new Message();
                    message.what = CLOSE_INIT_PROGRESS;
                    handler.sendMessage(message);// 提示:图片正在保存,请稍后
                }
            }
        };
        thread.start();
    }

    private AlertDialog dialog;
    /**
     * 展示滚动条
     */
    public void showXtSayHiDialog() {
        dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        dialog.setView(getActivity().getLayoutInflater().inflate(R.layout.dealwith_progress, null), 0, 0, 0, 0);
        dialog.setCancelable(false); // 是否可以通过返回键 关闭
        dialog.show();
    }

    public void closeXtSayHiDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        //gridAdapter.notifyDataSetChanged();
    }

    // 子线程-初始化数据
    private void initData() {

        termInfoTemp = xtSayhiService.findTermTempById(termId);// 终端临时表记录
        visitMTemp = xtSayhiService.findVisitTempById(visitId);// 拜访临时表记录
        mitValterMTemp = xtSayhiService.findMitValterMTempById(mitValterMTempKey);// 追溯临时表记录


        /*// 路线集合
        mstRouteList = xtSayhiService.initXtMstRoute(termId);
        // 终端等级集合
        dataDicByTermLevelLst = xtSayhiService.initDataDicByTermLevel();
        // 区域类型集合
        dataDicByAreaTypeLst = xtSayhiService.initDataDicByAreaType();
        // 销售渠道集合
        sellchanneldataLst = xtSayhiService.initDataDicBySellChannel();
        visitpositionLst = xtSayhiService.initVisitPosition();
        // 销售渠道集合
        sellchannelLst = getSellChannelList(sellchanneldataLst);
        // 主渠道集合
        mainchannelLst = getMainchannelLst(sellchannelLst);*/

        //设置时间
        calendar = Calendar.getInstance();
        yearr = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            aday = "0" + day;
        } else {
            aday = Integer.toString(day);
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 把选择控件也设置成系统时间
        Date date = calendar.getTime();
        ifminedate = sDateFormat.format(date);

    }

    // UI线程-展示控件数据(原使用拜访临时表)
    /*private void initData2() {
        // 是否有效终端
        if (ConstValues.FLAG_1.equals(termInfoTemp.getStatus())) {
            zdzs_sayhi_tv_termstatus_con1.setText("是");
        } else {
            zdzs_sayhi_tv_termstatus_con1.setText("否");
        }

        // 设置界面数据
        if (visitMTemp != null) {
            // 是否有效拜访 //进行判断(如果Status拜访状态为1 或者 拜访状态不为空设置为选中状态)
            if (ConstValues.FLAG_1.equals(visitMTemp.getStatus()) || CheckUtil.isBlankOrNull(visitMTemp.getStatus())) {
                zdzs_sayhi_tv_visitstatus_con1.setText("是");
            } else {
                zdzs_sayhi_tv_visitstatus_con1.setText("否");
            }

            // 是否我品店招
            if (ConstValues.FLAG_1.equals(termInfoTemp.getIfmine())) {
                zdzs_sayhi_rl_wopindianzhao_con1.setText("是");
                zdzs_sayhi_rl_wopindianzhao_con2.setVisibility(View.VISIBLE);
                zdzs_sayhi_rl_wopindianzhao_con2.setText(termInfoTemp.getIfminedate());
            } else {//当店招未选中的时候,隐藏对应的控件 文字
                zdzs_sayhi_rl_wopindianzhao_con1.setText("否");
                zdzs_sayhi_rl_wopindianzhao_con2.setText(ifminedate);
                zdzs_sayhi_rl_wopindianzhao_con2.setVisibility(View.GONE);
            }

            //销售产品范围 我品: 是1 否0
            if(ConstValues.FLAG_1.equals(visitMTemp.getIsself())){
                zdzs_sayhi_rl_selfstatus_con1.setText("是");
            }else {
                zdzs_sayhi_rl_selfstatus_con1.setText("否");
            }
            //销售产品范围 竞品: 是1 否0
            if(ConstValues.FLAG_1.equals(visitMTemp.getIscmp())){
                zdzs_sayhi_rl_cmpstatus_con1.setText("是");
            }else {
                zdzs_sayhi_rl_cmpstatus_con1.setText("否");
            }
            // 终端合作状态 我品: 是1 否0
            if(ConstValues.FLAG_1.equals(termInfoTemp.getSelftreaty())){
                zdzs_sayhi_rl_selfprotocol_con1.setText("是");
            }else {
                zdzs_sayhi_rl_selfprotocol_con1.setText("否");
            }
            // 终端合作状态 竞品: 是1 否0
            if(ConstValues.FLAG_1.equals(termInfoTemp.getCmpselftreaty())){
                zdzs_sayhi_rl_cmpprotocol_con1.setText("是");
            }else {
                zdzs_sayhi_rl_cmpprotocol_con1.setText("否");
            }

            // 拜访对象
            zdzs_sayhi_rl_visitperson_con1.setText(visitMTemp.getVisituser());
        }

        if (termInfoTemp != null) {

            // 保留修改关的拜访顺序，用于判定是不更改同线路下的各终端的拜访顺序
            //prevSequence = termInfoTemp.getSequence();
            zdzs_sayhi_rl_termcode_con1.setText(termInfoTemp.getTerminalcode());
            zdzs_sayhi_rl_termname_con1.setText(termInfoTemp.getTerminalname());
            zdzs_sayhi_rl_address_con1.setText(termInfoTemp.getAddress());
            zdzs_sayhi_rl_person_con1.setText(termInfoTemp.getContact());
            zdzs_sayhi_rl_tel_con1.setText(termInfoTemp.getMobile());
            zdzs_sayhi_rl_sequence_con1.setText(termInfoTemp.getSequence());
            zdzs_sayhi_rl_cycle_con1.setText(termInfoTemp.getCycle());
            // 高中普低,总
            Long tvolume = 0l;
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getHvolume()))) {
                zdzs_sayhi_rl_hvolume_con1.setText(FunUtil.isNullToZero(termInfoTemp.getHvolume()));
            } else {
                zdzs_sayhi_rl_hvolume_con1.setText(FunUtil.isNullToZero(termInfoTemp.getHvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfoTemp.getHvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getMvolume()))) {
                zdzs_sayhi_rl_mvolume_con1.setText(FunUtil.isNullToZero(termInfoTemp.getMvolume()));
            } else {
                zdzs_sayhi_rl_mvolume_con1.setText(FunUtil.isNullToZero(termInfoTemp.getMvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfoTemp.getMvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getPvolume()))) {
                zdzs_sayhi_rl_pvolume_con1.setText(FunUtil.isNullToZero(termInfoTemp.getPvolume()));
            } else {
                zdzs_sayhi_rl_pvolume_con1.setText(FunUtil.isNullToZero(termInfoTemp.getPvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfoTemp.getPvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getLvolume()))) {
                zdzs_sayhi_rl_lvolume_con1.setText(FunUtil.isNullToZero(termInfoTemp.getLvolume()));
            } else {
                zdzs_sayhi_rl_lvolume_con1.setText(FunUtil.isNullToZero(termInfoTemp.getLvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero(termInfoTemp.getLvolume());
            }
            zdzs_sayhi_rl_totalvolume_con1.setText(String.valueOf(tvolume));

            // 所属线路
            zdzs_sayhi_rl_belongline_con1.setText(xtSayhiService.getRouteName(termInfoTemp.getRoutekey()));

            // 区域类型
            zdzs_sayhi_rl_areatype_con1.setText(xtSayhiService.getDatadicName(termInfoTemp.getAreatype()));

            // 老板老板娘
            //xttermpersion.setText(visitMTemp.getVisitposition());
            zdzs_sayhi_rl_visitperson_con1.setText(visitMTemp.getVisituser());

            // 终端等级
            zdzs_sayhi_rl_level_con1.setText(xtSayhiService.getDatadicName(termInfoTemp.getTlevel()));

            // 销售渠道
            zdzs_sayhi_rl_sellchannel_con1.setText(xtSayhiService.getDatadicName(termInfoTemp.getSellchannel()));
            zdzs_sayhi_rl_mainchannel_con1.setText(xtSayhiService.getDatadicName(termInfoTemp.getMainchannel()));
            zdzs_sayhi_rl_minorchannel_con1.setText(xtSayhiService.getDatadicName(termInfoTemp.getMinorchannel()));

            // 获取省市县数据
            zdzs_sayhi_rl_prov_con1.setText(xtSayhiService.getAreaName(termInfoTemp.getProvince()));
            zdzs_sayhi_rl_city_con1.setText(xtSayhiService.getAreaName(termInfoTemp.getCity()));
            zdzs_sayhi_rl_country_con1.setText(xtSayhiService.getAreaName(termInfoTemp.getCounty()));


        }
        PrefUtils.putBoolean(getActivity(),GlobalValues.SAYHIREADY,true);
    }*/

    // UI线程-展示控件数据(现使用追溯临时表)
    private void initData3() {

        // 是否有效终端
        //if (ConstValues.FLAG_1.equals(mitValterMTemp.getVidter())) {
        if (ConstValues.FLAG_1.equals(mitValterMTemp.getVidter())) {
            zdzs_sayhi_tv_termstatus_con1.setText("是");
        } else {
            zdzs_sayhi_tv_termstatus_con1.setText("否");
        }

        // 设置界面数据
        if (visitMTemp != null) {
            // 是否有效拜访 //进行判断(如果Status拜访状态为1 或者 拜访状态不为空设置为选中状态)
            if (ConstValues.FLAG_1.equals(mitValterMTemp.getVidvisit()) || CheckUtil.isBlankOrNull(mitValterMTemp.getVidvisit())) {
                zdzs_sayhi_tv_visitstatus_con1.setText("是");
            } else {
                zdzs_sayhi_tv_visitstatus_con1.setText("否");
            }

            // 是否我品店招
            if (ConstValues.FLAG_1.equals(mitValterMTemp.getVidifmine())) {
                zdzs_sayhi_rl_wopindianzhao_con1.setText("是");
                zdzs_sayhi_rl_wopindianzhao_con2.setVisibility(View.VISIBLE);
                zdzs_sayhi_rl_wopindianzhao_con2.setText(mitValterMTemp.getVidifminedate());
            } else {//当店招未选中的时候,隐藏对应的控件 文字
                zdzs_sayhi_rl_wopindianzhao_con1.setText("否");
                zdzs_sayhi_rl_wopindianzhao_con2.setText(ifminedate);
                zdzs_sayhi_rl_wopindianzhao_con2.setVisibility(View.GONE);
            }

            //销售产品范围 我品: 是1 否0
            if(ConstValues.FLAG_1.equals(mitValterMTemp.getVidisself())){
                zdzs_sayhi_rl_selfstatus_con1.setText("是");
            }else {
                zdzs_sayhi_rl_selfstatus_con1.setText("否");
            }
            //销售产品范围 竞品: 是1 否0
            if(ConstValues.FLAG_1.equals(mitValterMTemp.getVidisself())){
                zdzs_sayhi_rl_cmpstatus_con1.setText("是");
            }else {
                zdzs_sayhi_rl_cmpstatus_con1.setText("否");
            }
            // 终端合作状态 我品: 是1 否0
            if(ConstValues.FLAG_1.equals(mitValterMTemp.getVidselftreaty())){
                zdzs_sayhi_rl_selfprotocol_con1.setText("是");
            }else {
                zdzs_sayhi_rl_selfprotocol_con1.setText("否");
            }
            // 终端合作状态 竞品: 是1 否0
            if(ConstValues.FLAG_1.equals(mitValterMTemp.getVidcmptreaty())){
                zdzs_sayhi_rl_cmpprotocol_con1.setText("是");
            }else {
                zdzs_sayhi_rl_cmpprotocol_con1.setText("否");
            }

            // 拜访对象
            //zdzs_sayhi_rl_visitperson_con1.setText(mitValterMTemp.getVidvisituser());
            String visitposition = mitValterMTemp.getVidvisituser();
            if(!("-1".equals(visitposition)||"66AA9D3A55374232891C964350610930".equals(visitposition))){ //"-1",其他 根据原先用户是什么,不做处理
                String visitpositionName = xtSayhiService.getVisitpositionName(visitposition);
                zdzs_sayhi_rl_visitperson_con1.setText(visitpositionName);
            }else{
                zdzs_sayhi_rl_visitperson_con1.setText(mitValterMTemp.getVidvisitotherval());// 其他
            }
        }

        if (termInfoTemp != null) {

            // 保留修改关的拜访顺序，用于判定是不更改同线路下的各终端的拜访顺序
            //prevSequence = termInfoTemp.getSequence();
            zdzs_sayhi_rl_termcode_con1.setText(mitValterMTemp.getVidterminalcode());
            zdzs_sayhi_rl_termname_con1.setText(mitValterMTemp.getVidtername());
            zdzs_sayhi_rl_address_con1.setText(mitValterMTemp.getVidaddress());
            zdzs_sayhi_rl_person_con1.setText(mitValterMTemp.getVidcontact());
            zdzs_sayhi_rl_tel_con1.setText(mitValterMTemp.getVidmobile());
            zdzs_sayhi_rl_sequence_con1.setText(mitValterMTemp.getVidsequence());
            zdzs_sayhi_rl_cycle_con1.setText(mitValterMTemp.getVidcycle());
            // 高中普低,总
            Long tvolume = 0l;
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(mitValterMTemp.getVidhvolume()))) {
                zdzs_sayhi_rl_hvolume_con1.setText(FunUtil.isNullToZero(mitValterMTemp.getVidhvolume()));
            } else {
                zdzs_sayhi_rl_hvolume_con1.setText(FunUtil.isNullToZero(mitValterMTemp.getVidhvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((mitValterMTemp.getVidhvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(mitValterMTemp.getVidzvolume()))) {
                zdzs_sayhi_rl_mvolume_con1.setText(FunUtil.isNullToZero(mitValterMTemp.getVidzvolume()));
            } else {
                zdzs_sayhi_rl_mvolume_con1.setText(FunUtil.isNullToZero(mitValterMTemp.getVidzvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((mitValterMTemp.getVidzvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(mitValterMTemp.getVidpvolume()))) {
                zdzs_sayhi_rl_pvolume_con1.setText(FunUtil.isNullToZero(mitValterMTemp.getVidpvolume()));
            } else {
                zdzs_sayhi_rl_pvolume_con1.setText(FunUtil.isNullToZero(mitValterMTemp.getVidpvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((mitValterMTemp.getVidpvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(mitValterMTemp.getVidlvolume()))) {
                zdzs_sayhi_rl_lvolume_con1.setText(FunUtil.isNullToZero(mitValterMTemp.getVidlvolume()));
            } else {
                zdzs_sayhi_rl_lvolume_con1.setText(FunUtil.isNullToZero(mitValterMTemp.getVidlvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero(mitValterMTemp.getVidlvolume());
            }
            zdzs_sayhi_rl_totalvolume_con1.setText(String.valueOf(tvolume));

            // 所属线路
            zdzs_sayhi_rl_belongline_con1.setText(xtSayhiService.getRouteName(mitValterMTemp.getVidroutekey()));

            // 区域类型
            zdzs_sayhi_rl_areatype_con1.setText(xtSayhiService.getDatadicName(mitValterMTemp.getVidareatype()));

            // 老板老板娘
            //xttermpersion.setText(visitMTemp.getVisitposition());
            //zdzs_sayhi_rl_visitperson_con1.setText(mitValterMTemp.getVidvisituser());

            // 终端等级
            zdzs_sayhi_rl_level_con1.setText(xtSayhiService.getDatadicName(termInfoTemp.getTlevel()));

            // 销售渠道
            zdzs_sayhi_rl_sellchannel_con1.setText(xtSayhiService.getDatadicName(termInfoTemp.getSellchannel()));
            zdzs_sayhi_rl_mainchannel_con1.setText(xtSayhiService.getDatadicName(termInfoTemp.getMainchannel()));
            zdzs_sayhi_rl_minorchannel_con1.setText(xtSayhiService.getDatadicName(mitValterMTemp.getVidminchannel()));

            // 获取省市县数据
            zdzs_sayhi_rl_prov_con1.setText(xtSayhiService.getAreaName(termInfoTemp.getProvince()));
            zdzs_sayhi_rl_city_con1.setText(xtSayhiService.getAreaName(termInfoTemp.getCity()));
            zdzs_sayhi_rl_country_con1.setText(xtSayhiService.getAreaName(mitValterMTemp.getVidcountry()));

        }

        ;

        zdzs_sayhi_tv_termstatus_statue.setText(getYdInfo(mitValterMTemp.getVidterflag()));
        zdzs_sayhi_tv_visitstatus_statue.setText(getYdInfo(mitValterMTemp.getVidvisitflag()));

        zdzs_sayhi_rl_wopindianzhao_statue.setText(getYdInfo(mitValterMTemp.getVidifmineflag()));

        // 我品销售范围
        zdzs_sayhi_rl_selfstatus_statue.setText(getYdInfo(mitValterMTemp.getVidisselfflag()));
        // 竞品销售
        zdzs_sayhi_rl_cmpstatus_statue.setText(getYdInfo(mitValterMTemp.getVidiscmpflag()));
        // 我品合作
        zdzs_sayhi_rl_selfprotocol_statue.setText(getYdInfo(mitValterMTemp.getVidselftreatyflag()));
        // 竞品合作
        zdzs_sayhi_rl_cmpprotocol_statue.setText(getYdInfo(mitValterMTemp.getVidcmptreatyflag()));

        zdzs_sayhi_rl_termname_statue.setText(getYdInfo(mitValterMTemp.getVidternameflag()));
        zdzs_sayhi_rl_termcode_statue.setText(getYdInfo(mitValterMTemp.getVidterminalcode()));
        zdzs_sayhi_rl_belongline_statue.setText(getYdInfo(mitValterMTemp.getVidrtekeyflag()));
        // 终端等级
        zdzs_sayhi_rl_level_statue.setText(getYdInfo(mitValterMTemp.getVidtervidterlevelflag()));//

        // 县
        zdzs_sayhi_rl_country_statue.setText(getYdInfo(mitValterMTemp.getVidcountryflag()));

        zdzs_sayhi_rl_address_statue.setText(getYdInfo(mitValterMTemp.getVidaddressflag()));
        zdzs_sayhi_rl_person_statue.setText(getYdInfo(mitValterMTemp.getVidcontactflag()));
        zdzs_sayhi_rl_tel_statue.setText(getYdInfo(mitValterMTemp.getVidmobileflag()));
        zdzs_sayhi_rl_cycle_statue.setText(getYdInfo(mitValterMTemp.getVidcycleflag()));
        zdzs_sayhi_rl_sequence_statue.setText(getYdInfo(mitValterMTemp.getVidsequenceflag()));

        zdzs_sayhi_rl_hvolume_statue.setText(getYdInfo(mitValterMTemp.getVidhvolumeflag()));
        zdzs_sayhi_rl_mvolume_statue.setText(getYdInfo(mitValterMTemp.getVidzvolumeflag()));
        zdzs_sayhi_rl_pvolume_statue.setText(getYdInfo(mitValterMTemp.getVidpvolumeflag()));
        zdzs_sayhi_rl_lvolume_statue.setText(getYdInfo(mitValterMTemp.getVidlvolumeflag()));
        zdzs_sayhi_rl_totalvolume_statue.setText(getYdInfo(mitValterMTemp.getVidterflag()));//

        zdzs_sayhi_rl_areatype_statue.setText(getYdInfo(mitValterMTemp.getVidareatypeflag()));

        // 次渠道
        zdzs_sayhi_rl_minorchannel_statue.setText(getYdInfo(mitValterMTemp.getVidminchannelflag()));

        zdzs_sayhi_rl_visitperson_statue.setText(getYdInfo(mitValterMTemp.getVidvisituserflag()));

        PrefUtils.putBoolean(getActivity(),GlobalValues.SAYHIREADY,true);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.zdzs_sayhi_rl_termstatus:
                alertShow3(mitValterMTemp,"是否有效终端","vidter","",
                        "","setVidterflag","setVidterremaek","termstatus");
                break;
            case R.id.zdzs_sayhi_rl_visitstatus:
                alertShow3(mitValterMTemp,"是否有效拜访","vidvisit","",
                        "","setVidvisitflag","setVidvisitremark","visitstatus");
                break;
                //
            case R.id.zdzs_sayhi_rl_wopindianzhao:
                alertShow3(mitValterMTemp,"是否我品店招","vidifmine","",
                        "","setVidifmineflag","setVidifminermark","wopindianzhao");
                break;

                // 是否我品销售范围
            case R.id.zdzs_sayhi_rl_selfstatus:
                alertShow3(mitValterMTemp,"是否我品销售范围","vidisself","",
                        "","setVidisselfflag","setVidterremaek","selfstatus");
                break;

            // 是否竞品销售范围
            case R.id.zdzs_sayhi_rl_cmpstatus:
                alertShow3(mitValterMTemp,"是否竞品销售范围","vidiscmp","",
                        "","setVidiscmpflag","setVidterremaek","cmpstatus");
                break;

            // 是否我品终端合作
            case R.id.zdzs_sayhi_rl_selfprotocol:
                alertShow3(mitValterMTemp,"是否我品终端合作","vidselftreaty","",
                        "","setVidselftreatyflag","setVidterremaek","selfprotocol");
                break;

            // 是否竞品终端合作
            case R.id.zdzs_sayhi_rl_cmpprotocol:
                alertShow3(mitValterMTemp,"是否竞品终端合作","vidcmptreaty","",
                        "","setVidcmptreatyflag","setVidterremaek","cmpprotocol");
                break;


                // 终端名称
            case R.id.zdzs_sayhi_rl_termname:
                alertShow3(mitValterMTemp,"终端名称","vidtername","vidternameval",
                        "setVidternameval","setVidternameflag","setVidternameremark","termname");
                break;
            case R.id.zdzs_sayhi_rl_termcode:
                alertShow3(mitValterMTemp,"终端编码","vidterminalcode","vidtercodeval",
                        "setVidtercodeval","setVidtercodeflag","setVidtercoderemark","termcode");
                break;
            case R.id.zdzs_sayhi_rl_address:
                alertShow3(mitValterMTemp,"地址","vidaddress","vidaddressval",
                        "setVidaddressval","setVidaddressflag","setVidaddressremark","address");
                break;
            case R.id.zdzs_sayhi_rl_person:
                alertShow3(mitValterMTemp,"联系人","vidcontact","vidcontactval",
                        "setVidcontactval","setvidcontactflag","setVidcontactremark","person");
                break;
            case R.id.zdzs_sayhi_rl_tel:
                alertShow3(mitValterMTemp,"电话","vidmobile","vidmobileval",
                        "setVidmobileval","setVidmobileflag","setVidmobileremark","tel");
                break;
            case R.id.zdzs_sayhi_rl_sequence:
                alertShow3(mitValterMTemp,"拜访顺序","vidsequence","vidsequenceval",
                        "setVidsequenceval","setVidsequenceflag","setVidvidsequenceremark","sequence");
                break;


            case R.id.zdzs_sayhi_rl_cycle:
                alertShow3(mitValterMTemp,"拜访周期","vidcycle","vidcycleval",
                        "setVidcycleval","setVidcycleflag","setVidcycleremark2","cycle");
                break;
            case R.id.zdzs_sayhi_rl_hvolume:
                alertShow3(mitValterMTemp,"高档容量","vidhvolume","vidhvolumeval",
                        "setVidhvolumeval","setVidhvolumeflag","setVidhvolumeremark","hvolume");
                break;
            case R.id.zdzs_sayhi_rl_mvolume:
                alertShow3(mitValterMTemp,"中档容量","vidzvolume","vidzvolumeval",
                        "setVidzvolumeval","setVidzvolumeflag","setVidxvolumeremark","mvolume");
                break;
            case R.id.zdzs_sayhi_rl_pvolume:
                alertShow3(mitValterMTemp,"普档容量","vidpvolume","vidpvolumeval",
                        "setVidpvolumeval","setVidpvolumeflag","setVidpvolumeremark","pvolume");
                break;
            case R.id.zdzs_sayhi_rl_lvolume:
                alertShow3(mitValterMTemp,"低档容量","vidlvolume","vidlvolumeval",
                        "setVidlvolumeval","setVidlvolumeflag","setVidlvolumeremark","lvolume");
                break;


            case R.id.zdzs_sayhi_rl_belongline:
                alertShow3(mitValterMTemp,"所属路线","vidroutekey","vidrtekeyval",
                        "setVidrtekeyval","setVidrtekeyflag","setVidroutremark","belongline");
                break;
            case R.id.zdzs_sayhi_rl_level:
                alertShow3(mitValterMTemp,"终端等级","vidterlevel","vidtervidterlevelval",
                        "setVidtervidterlevelval","setVidtervidterlevelflag","setidtervidterlevelremark","level");
                break;
            case R.id.zdzs_sayhi_rl_country:
                alertShow4(mitValterMTemp,"县","vidcountry","vidcountryval",
                        termInfoTemp.getCity(),
                        "setVidcountryval","setVidcountryflag","setVidcountryremark","country");
                break;


            case R.id.zdzs_sayhi_rl_areatype:
                alertShow3(mitValterMTemp,"区域类型","vidareatype","vidareatypeval",
                        "setVidareatypeval","setVidareatypeflag","setVidareatyperemark","areatype");
                break;
            case R.id.zdzs_sayhi_rl_minorchannel:
                alertShow4(mitValterMTemp,"次渠道","vidminchannel","vidminchannelval",
                        termInfoTemp.getMainchannel(),
                        "setVidminchannelval","setVidminchannelflag","setVidminchannelremark","minorchannel");
                break;
            case R.id.zdzs_sayhi_rl_visitperson:
                alertShow3(mitValterMTemp,"拜访对象","vidvisituser","vidvisituserval",
                        "setVidvisituserval","setVidvisituserflag","setVidvisituserremark","visitperson");
                break;

            default:
                break;
        }
    }
    /**
     * 弹窗3
     * 参数1: 标题 ×
     * 参数2: 主体内容    ×
     * 参数3: 取消按钮    ×
     * 参数4: 高亮按钮 数组 √
     * 参数5: 普通按钮 数组 √
     * 参数6: 上下文 √
     * 参数7: 弹窗类型 (正常取消,确定按钮)   √
     * 参数8: 条目点击监听  √
     */
    public void alertShow3(final MitValterMTemp mitValterMTemp, final String titleName,
                           final String ydkey,final String onlyddkey,
                           final String ddkey,final String ddflag,
                           final String ddremark,final String type) {
        List<KvStc> sureOrFail = new ArrayList<>();
        sureOrFail.add(new KvStc("zhengque","正确","-1"));
        sureOrFail.add(new KvStc("cuowu","错误(去修正)","-1"));
        mAlertViewExt = new AlertView("请选择结果", null, null, null,
                null, getActivity(), AlertView.Style.ActionSheet, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
        ListView listview = (ListView) extView.findViewById(R.id.alert_list);
        AlertKeyValueAdapter keyValueAdapter = new AlertKeyValueAdapter(getActivity(), sureOrFail,
                new String[]{"key", "value"}, "zhengque");
        listview.setAdapter(keyValueAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(1==position){
                    Bundle bundle = new Bundle();
                    bundle.putString("titleName", titleName);// 标题内容,比如:是否有效终端
                    bundle.putString("ydkey", ydkey);// 对象属性,比如: vidroutekey(属性)
                    bundle.putString("onlyddkey", onlyddkey);// 对象属性,比如: vidroutekey(属性)
                    bundle.putString("setDdValue", ddkey);// 对象方法名,比如: setVidrtekeyval(督导输入的值)
                    bundle.putString("setDdFlag", ddflag);// 对象方法名,比如: setVidrtekeyflag(正确与否)
                    bundle.putString("setDdRemark", ddremark);// 对象方法名,比如: setVidroutremark(备注)
                    bundle.putString("type", type);// 页面类型,比如: "1"
                    bundle.putString("termId", termId);// 页面类型,比如: "1"
                    bundle.putString("mitValterMTempKey", mitValterMTempKey);
                    bundle.putSerializable("mitValterMTemp", mitValterMTemp);
                    ZsSayhiAmendFragment zsSayhiAmendFragment = new ZsSayhiAmendFragment(handler);
                    zsSayhiAmendFragment.setArguments(bundle);
                    ZsVisitShopActivity zsVisitShopActivity = (ZsVisitShopActivity)getActivity();
                    zsVisitShopActivity.changeXtvisitFragment(zsSayhiAmendFragment,"zsamendfragment");
                }

                mAlertViewExt.dismiss();
            }
        });
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.setCancelable(true).setOnDismissListener(this);
        mAlertViewExt.show();
    }
    public void alertShow4(final MitValterMTemp mitValterMTemp, final String titleName,
                           final String ydkey,final String onlyddkey,final String ydparentkey,
                           final String ddkey,final String ddflag,
                           final String ddremark,final String type) {
        List<KvStc> sureOrFail = new ArrayList<>();
        sureOrFail.add(new KvStc("zhengque","正确","-1"));
        sureOrFail.add(new KvStc("cuowu","错误(去修正)","-1"));
        mAlertViewExt = new AlertView("请选择结果", null, null, null,
                null, getActivity(), AlertView.Style.ActionSheet, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_form, null);
        ListView listview = (ListView) extView.findViewById(R.id.alert_list);
        AlertKeyValueAdapter keyValueAdapter = new AlertKeyValueAdapter(getActivity(), sureOrFail,
                new String[]{"key", "value"}, "zhengque");
        listview.setAdapter(keyValueAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(1==position){
                    Bundle bundle = new Bundle();
                    bundle.putString("titleName", titleName);// 标题内容,比如:是否有效终端
                    bundle.putString("ydkey", ydkey);// 对象属性,比如: vidroutekey(属性)
                    bundle.putString("onlyddkey", onlyddkey);// 对象属性,比如: vidroutekey(属性)
                    bundle.putString("ydparentkey", ydparentkey);// 对象属性,比如: vidroutekey(属性)
                    bundle.putString("setDdValue", ddkey);// 对象方法名,比如: setVidrtekeyval(督导输入的值)
                    bundle.putString("setDdFlag", ddflag);// 对象方法名,比如: setVidrtekeyflag(正确与否)
                    bundle.putString("setDdRemark", ddremark);// 对象方法名,比如: setVidroutremark(备注)
                    bundle.putString("type", type);// 页面类型,比如: "1"
                    bundle.putString("termId", termId);// 页面类型,比如: "1"
                    bundle.putString("mitValterMTempKey", mitValterMTempKey);
                    bundle.putSerializable("mitValterMTemp", mitValterMTemp);
                    ZsSayhiAmendFragment zsSayhiAmendFragment = new ZsSayhiAmendFragment(handler);
                    zsSayhiAmendFragment.setArguments(bundle);
                    ZsVisitShopActivity zsVisitShopActivity = (ZsVisitShopActivity)getActivity();
                    zsVisitShopActivity.changeXtvisitFragment(zsSayhiAmendFragment,"zsamendfragment");
                }

                mAlertViewExt.dismiss();
            }
        });
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.setCancelable(true).setOnDismissListener(this);
        mAlertViewExt.show();
    }

    /***
     * 删除无效渠道
     *
     * @param kvStcList
     * @return
     */
    private List<KvStc> getSellChannelList(List<KvStc> kvStcList) {
        List<KvStc> list = new ArrayList<KvStc>();
        for (KvStc kvStc : kvStcList) {
            if (!DbtUtils.getInvalidChannelList().contains(kvStc.getKey())) {
                list.add(kvStc);
            }
        }
        return list;
    }

    /**
     * 获取主渠道集合
     *
     * @param sellchannelLst 销售渠道集合
     * @return
     */
    private List<KvStc> getMainchannelLst(List<KvStc> sellchannelLst) {
        KvStc sell = new KvStc();
        for (KvStc kvstc : sellchannelLst) {
            if (kvstc.getKey().equals(termInfoTemp.getSellchannel())) {
                sell = kvstc;
            }
        }
        // 返回某个销售渠道下的主渠道集合
        return getSellChannelList(sell.getChildLst());
    }

    /**
     * 获取次渠道集合
     *
     * @param mainchannelLst 主渠道集合
     * @return
     */
    private List<KvStc> getMinorchannelLst(List<KvStc> mainchannelLst) {
        KvStc minor = new KvStc();
        for (KvStc kvstc : mainchannelLst) {
            if (kvstc.getKey().equals(termInfoTemp.getMainchannel())) {
                minor = kvstc;
            }
        }
        // 返回某个主渠道下的次渠道集合
        return getSellChannelList(minor.getChildLst());
    }


    @Override
    public void onItemClick(Object o, int position) {
        //判断是否是拓展窗口View，而且点击的是非取消按钮
        /*if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
            String name = etName.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "啥都没填呢", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "hello," + name, Toast.LENGTH_SHORT).show();
            }

            return;
        }
        Toast.makeText(this, "点击了第" + position + "个", Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onDismiss(Object o) {
        //closeKeyboard();
        //Toast.makeText(this, "消失了", Toast.LENGTH_SHORT).show();
    }

    @Override
    //失去焦点时调用
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        Long time5 = new Date().getTime();

        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag))return;

        if(!PrefUtils.getBoolean(getActivity(),GlobalValues.SAYHIREADY,false))return;

        /*// 终端状态为有效时才保存相关信息
        if (xttermstatusSw.getStatus()) {
            // 保存拜访主表信息
            if (xtvisitstatusSw.getStatus()) {
                visitMTemp.setStatus(ConstValues.FLAG_1);// status:1,true时为有效拜访
            } else {
                visitMTemp.setStatus(ConstValues.FLAG_0);
            }



			// 是否我品销售范围
            visitMTemp.setIsself(xtmineCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            // 是否竞品销售范围
            visitMTemp.setIscmp(xtvieCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            // 如果初始状态为选中，保存进为未选中则状态为0：流失
            if (xtmineprotocolCb.getTag() != null&& ConstValues.FLAG_1.equals(xtmineprotocolCb.getTag().toString())) {
                visitMTemp.setSelftreaty(xtmineprotocolCb.isChecked() ? ConstValues.FLAG_1: ConstValues.FLAG_0);
            } else {
                visitMTemp.setSelftreaty(xtmineprotocolCb.isChecked() ? ConstValues.FLAG_1: ConstValues.FLAG_0);
            }

            if (xtvieprotocolCb.getTag() != null&& ConstValues.FLAG_1.equals(xtvieprotocolCb.getTag().toString())) {
                visitMTemp.setCmptreaty(xtvieprotocolCb.isChecked() ? ConstValues.FLAG_1: ConstValues.FLAG_0);
            } else {
                visitMTemp.setCmptreaty(xtvieprotocolCb.isChecked() ? ConstValues.FLAG_1: ConstValues.FLAG_0);
            }
            visitMTemp.setVisituser(xttermpersion.getText().toString());
            visitMTemp.setVisitposition(visitMTemp.getVisitposition());
            xtSayhiService.updateVisit(visitMTemp);

            // 保存终端信息
            if (termInfoTemp != null) {
                termInfoTemp.setTerminalname(FunUtil.isNullSetSpace(xttermname.getText()).toString());
                termInfoTemp.setRoutekey(FunUtil.isBlankOrNullTo(termInfoTemp.getRoutekey(), termInfoTemp.getRoutekey()));
                termInfoTemp.setProvince(termInfoTemp.getProvince());
                termInfoTemp.setCity(termInfoTemp.getCity());
                termInfoTemp.setCounty(termInfoTemp.getCounty());

                termInfoTemp.setAddress(FunUtil.isNullSetSpace(xttermaddress.getText()).toString());
                termInfoTemp.setContact(FunUtil.isNullSetSpace(xttermcontact.getText()).toString());
                if(xttermphone.getText().toString().length()>30)xttermphone.setText(xttermphone.getText().toString().substring(0, 30));
                termInfoTemp.setMobile(FunUtil.isNullSetSpace(xttermphone.getText()).toString());
                termInfoTemp.setTlevel(FunUtil.isBlankOrNullTo(termInfoTemp.getTlevel(),termInfoTemp.getTlevel()));
                termInfoTemp.setSequence(FunUtil.isNullSetSpace(xttermsequence.getText()).toString());
                termInfoTemp.setCycle(FunUtil.isNullSetSpace(xttermcycle.getText()).toString());
                termInfoTemp.setHvolume(FunUtil.isNullToZero(xttermhvolume.getText()).toString());
                termInfoTemp.setMvolume(FunUtil.isNullToZero(xttermmvolume.getText()).toString());
                termInfoTemp.setPvolume(FunUtil.isNullToZero(xttermpvolume.getText()).toString());
                termInfoTemp.setLvolume(FunUtil.isNullToZero(xttermlvolume.getText()).toString());

                // 有效终端
                if (xttermstatusSw.getStatus()) {
                    termInfoTemp.setStatus(ConstValues.FLAG_1); // 有效
                } else {
                    termInfoTemp.setStatus(ConstValues.FLAG_0); // 失效
                }

                // 店招
                if (xtwopindianzhaoSw.getStatus()) {
                    termInfoTemp.setIfmine(ConstValues.FLAG_1);// 是否我品店招 0不是 1是
                    if(xttimeBtn.getText().toString()!=null&&xttimeBtn.getText().toString().length()>0){
                        termInfoTemp.setIfminedate(xttimeBtn.getText().toString());
                    }else{
                        termInfoTemp.setIfminedate(ifminedate);
                    }
                } else {
                    termInfoTemp.setIfmine(ConstValues.FLAG_0);
                }

                termInfoTemp.setSellchannel(FunUtil.isBlankOrNullTo(termInfoTemp.getSellchannel(), termInfoTemp.getSellchannel()));
                termInfoTemp.setMainchannel(FunUtil.isBlankOrNullTo(termInfoTemp.getMainchannel(), termInfoTemp.getMainchannel()));
                termInfoTemp.setMinorchannel(FunUtil.isBlankOrNullTo(termInfoTemp.getMinorchannel(), termInfoTemp.getMinorchannel()));
                termInfoTemp.setAreatype(FunUtil.isBlankOrNullTo(termInfoTemp.getAreatype(), termInfoTemp.getAreatype()));
                //termInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
                termInfoTemp.setUpdateuser(PrefUtils.getString(getActivity(), "userCode", ""));
                termInfoTemp.setPadisconsistent(ConstValues.FLAG_0);
                termInfoTemp.setUpdatetime(DateUtil.getDateTimeDte(1));
                // --- 更改mst_terminalinfo_m表中selftreaty字段 ywm 20160426-----------------
                termInfoTemp.setSelftreaty(visitMTemp.getSelftreaty());
                termInfoTemp.setCmpselftreaty(visitMTemp.getCmptreaty());
                // --- 更改mst_terminalinfo_m表中selftreaty字段 ywm 20160426-----------------
                String prevSequence = xttermsequence.getText().toString();
                xtSayhiService.updateXtTermInfo(termInfoTemp, prevSequence);
            }
        }*/
        Long time6 = new Date().getTime();
        Log.e("Optimization", "保存数据" + (time6 - time5));
    }

    private String getYdInfo(String flag){
        String con ="";
        if("N".equals(flag)){
            con = "业代录错";
        }else if("Y".equals(flag)){
            con = "正确";
        }else{
            con = "未稽查";
        }
        return con;
    }
}
