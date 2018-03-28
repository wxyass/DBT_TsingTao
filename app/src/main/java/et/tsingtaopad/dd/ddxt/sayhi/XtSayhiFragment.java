package et.tsingtaopad.dd.ddxt.sayhi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtSayhiFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private et.tsingtaopad.view.DdSlideSwitch xttermstatusSw;
    private et.tsingtaopad.view.DdSlideSwitch xtvisitstatusSw;
    private Button xttextBtn;
    private Button xttimeBtn;
    private et.tsingtaopad.view.DdSlideSwitch xtwopindianzhaoSw;
    private CheckBox xtvieCb;
    private CheckBox xtmineCb;
    private CheckBox xtvieprotocolCb;
    private CheckBox xtmineprotocolCb;
    private TextView xttermcode;
    private TextView xttermroude;
    private TextView xttermname;
    private TextView xttermlv;
    private TextView xtprovince;
    private TextView xttermcity;
    private TextView xttermcountry;
    private TextView xttermaddress;
    private TextView xttermcontact;
    private TextView xttermphone;
    private TextView xttermcycle;
    private TextView xttermsequence;
    private TextView xttermhvolume;
    private TextView xttermmvolume;
    private TextView xttermpvolume;
    private TextView xttermlvolume;
    private TextView xttermtvolume;
    private TextView xttermarea;
    private TextView xttermsellchannel;
    private TextView xttermtmainchannel;
    private TextView xttermminorchannel;
    private TextView xttermpersion;
    private Button xtnextBtn;

    private XtSayhiService xtSayhiService;
    private MstTerminalinfoMTemp termInfoTemp;
    private MstVisitMTemp visitMTemp;

    private String aday;
    private Calendar calendar;
    private int yearr;
    private int month;
    private int day;
    private String ifminedate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_sayhi, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

        xttermstatusSw = (et.tsingtaopad.view.DdSlideSwitch) view.findViewById(R.id.xt_sayhi_sw_termstatus);
        xtvisitstatusSw = (et.tsingtaopad.view.DdSlideSwitch) view.findViewById(R.id.xt_sayhi_sw_visitstatus);
        xttextBtn = (Button) view.findViewById(R.id.xt_sayhi_btn_text);
        xttimeBtn = (Button) view.findViewById(R.id.xt_sayhi_btn_time);
        xtwopindianzhaoSw = (et.tsingtaopad.view.DdSlideSwitch) view.findViewById(R.id.xt_sayhi_sw_wopindianzhao);
        xtvieCb = (CheckBox) view.findViewById(R.id.xt_sayhi_cb_vie);
        xtmineCb = (CheckBox) view.findViewById(R.id.xt_sayhi_cb_mine);
        xtvieprotocolCb = (CheckBox) view.findViewById(R.id.xt_sayhi_cb_vieprotocol);
        xtmineprotocolCb = (CheckBox) view.findViewById(R.id.xt_sayhi_cb_mineprotocol);
        xttermcode = (TextView) view.findViewById(R.id.xtbf_sayhi_termcode);
        xttermroude = (TextView) view.findViewById(R.id.xtbf_sayhi_termroude);
        xttermname = (TextView) view.findViewById(R.id.xtbf_sayhi_termname);
        xttermlv = (TextView) view.findViewById(R.id.xtbf_sayhi_termlv);
        xtprovince = (TextView) view.findViewById(R.id.xtbf_sayhi_termprovince);
        xttermcity = (TextView) view.findViewById(R.id.xtbf_sayhi_termcity);
        xttermcountry = (TextView) view.findViewById(R.id.xtbf_sayhi_termcountry);
        xttermaddress = (TextView) view.findViewById(R.id.xtbf_sayhi_termaddress);
        xttermcontact = (TextView) view.findViewById(R.id.xtbf_sayhi_termcontact);
        xttermphone = (TextView) view.findViewById(R.id.xtbf_sayhi_termphone);
        xttermcycle = (TextView) view.findViewById(R.id.xtbf_sayhi_termcycle);
        xttermsequence = (TextView) view.findViewById(R.id.xtbf_sayhi_termsequence);
        xttermhvolume = (TextView) view.findViewById(R.id.xtbf_sayhi_termhvolume);
        xttermmvolume = (TextView) view.findViewById(R.id.xtbf_sayhi_termmvolume);
        xttermpvolume = (TextView) view.findViewById(R.id.xtbf_sayhi_termpvolume);
        xttermlvolume = (TextView) view.findViewById(R.id.xtbf_sayhi_termlvolume);
        xttermtvolume = (TextView) view.findViewById(R.id.xtbf_sayhi_termtvolume);
        xttermarea = (TextView) view.findViewById(R.id.xtbf_sayhi_termarea);
        xttermsellchannel = (TextView) view.findViewById(R.id.xtbf_sayhi_termsellchannel);
        xttermtmainchannel = (TextView) view.findViewById(R.id.xtbf_sayhi_termtmainchannel);
        xttermminorchannel = (TextView) view.findViewById(R.id.xtbf_sayhi_termminorchannel);
        xttermpersion = (TextView) view.findViewById(R.id.xtbf_sayhi_termpersion);
        xtnextBtn = (Button) view.findViewById(R.id.xtbf_sayhi_bt_next);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        initData();
        Toast.makeText(getActivity(), "打招呼" + "/" + termId + "/" + termName, Toast.LENGTH_SHORT).show();
    }

    // 初始化数据
    private void initData() {
        xtSayhiService = new XtSayhiService(getActivity(), null);
        termInfoTemp = xtSayhiService.findTermTempById(termId);// 终端临时表记录
        visitMTemp = xtSayhiService.findVisitTempById(visitId);// 拜访临时表记录

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


        // 设置界面数据
        if (visitMTemp != null) {

            // 是否有效拜访 //进行判断(如果Status拜访状态为1 或者 拜访状态不为空设置为选中状态)
            if (ConstValues.FLAG_1.equals(visitMTemp.getStatus()) || CheckUtil.isBlankOrNull(visitMTemp.getStatus())) {
                xtvisitstatusSw.setStatus(true);
            } else {
                xtvisitstatusSw.setStatus(false);
            }

			// 是否我品店招
            if (ConstValues.FLAG_1.equals(termInfoTemp.getIfmine())) {
                xtwopindianzhaoSw.setStatus(true);
                xttimeBtn.setVisibility(View.VISIBLE);
                xttimeBtn.setText(termInfoTemp.getIfminedate());
                xttextBtn.setVisibility(View.VISIBLE);
            } else {//当店招未选中的时候,隐藏对应的控件 文字
                xtwopindianzhaoSw.setStatus(false);
                xttimeBtn.setText(ifminedate);
                xttimeBtn.setVisibility(View.GONE);
                xttextBtn.setVisibility(View.GONE);
            }

            //销售产品范围 我品: 是1 否0
            xtmineCb.setChecked(ConstValues.FLAG_1.equals( visitMTemp.getIsself()) ? true : false);
            //销售产品范围 竞品: 是1 否0
            xtvieCb.setChecked(ConstValues.FLAG_1.equals(visitMTemp.getIscmp()) ? true : false);
            // 终端合作状态 我品: 是1 否0
            xtmineprotocolCb.setChecked(ConstValues.FLAG_1 .equals(termInfoTemp.getSelftreaty()) ? true : false);
            xtmineprotocolCb.setTag(termInfoTemp.getSelftreaty());
            // 终端合作状态 竞品: 是1 否0
            xtvieprotocolCb.setChecked(ConstValues.FLAG_1.equals(termInfoTemp.getCmpselftreaty()) ? true : false);
            xtvieprotocolCb.setTag(termInfoTemp.getCmpselftreaty());
            // 拜访对象
            xttermpersion.setText(visitMTemp.getVisituser());
        }

        if (termInfoTemp != null) {

            // 保留修改关的拜访顺序，用于判定是不更改同线路下的各终端的拜访顺序
            //prevSequence = termInfoTemp.getSequence();
            xttermcode.setText(termInfoTemp.getTerminalcode());
            xttermname.setText(termInfoTemp.getTerminalname());
            xttermaddress.setText(termInfoTemp.getAddress());
            xttermcontact.setText(termInfoTemp.getContact());
            xttermphone.setText(termInfoTemp.getMobile());
            xttermsequence.setText(termInfoTemp.getSequence());
            xttermcycle.setText(termInfoTemp.getCycle());
            // 高中普低,总
            Long tvolume = 0l;
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getHvolume()))) {
                xttermhvolume.setText(FunUtil.isNullToZero(termInfoTemp.getHvolume()));
            } else {
                xttermhvolume.setText(FunUtil.isNullToZero(termInfoTemp.getHvolume()));
                tvolume = tvolume+ FunUtil.isNullSetZero((termInfoTemp.getHvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getMvolume()))) {
                xttermmvolume.setText(FunUtil.isNullToZero(termInfoTemp.getMvolume()));
            } else {
                xttermmvolume.setText(FunUtil.isNullToZero(termInfoTemp.getMvolume()));
                tvolume = tvolume+ FunUtil.isNullSetZero((termInfoTemp.getMvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getPvolume()))) {
                xttermpvolume.setText(FunUtil.isNullToZero(termInfoTemp.getPvolume()));
            } else {
                xttermpvolume.setText(FunUtil.isNullToZero(termInfoTemp.getPvolume()));
                tvolume = tvolume+ FunUtil.isNullSetZero((termInfoTemp.getPvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfoTemp.getLvolume()))) {
                xttermlvolume.setText(FunUtil.isNullToZero(termInfoTemp.getLvolume()));
            } else {
                xttermlvolume.setText(FunUtil.isNullToZero(termInfoTemp.getLvolume()));
                tvolume = tvolume+ FunUtil.isNullSetZero(termInfoTemp.getLvolume());
            }
            xttermtvolume.setText(String.valueOf(tvolume));

            // 所属线路
            xttermroude.setText(xtSayhiService.getRouteName(termInfoTemp.getRoutekey()));

            // 区域类型
            xttermarea.setText(xtSayhiService.getDatadicName(termInfoTemp.getAreatype()));

            // 老板老板娘
            //xttermpersion.setText(visitMTemp.getVisitposition());
            xttermpersion.setText(visitMTemp.getVisituser());

            // 终端等级
            xttermlv.setText(xtSayhiService.getDatadicName(termInfoTemp.getTlevel()));

            // 销售渠道
            xttermsellchannel.setText(xtSayhiService.getDatadicName(termInfoTemp.getSellchannel()));
            xttermtmainchannel.setText(xtSayhiService.getDatadicName(termInfoTemp.getMainchannel()));
            xttermminorchannel.setText(xtSayhiService.getDatadicName(termInfoTemp.getMinorchannel()));

            // 获取省市县数据
            xtprovince.setText(xtSayhiService.getAreaName(termInfoTemp.getProvince()));
            xttermcity.setText(xtSayhiService.getAreaName(termInfoTemp.getCity()));
            xttermcountry.setText(xtSayhiService.getAreaName(termInfoTemp.getCounty()));

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            default:
                break;
        }
    }
}
