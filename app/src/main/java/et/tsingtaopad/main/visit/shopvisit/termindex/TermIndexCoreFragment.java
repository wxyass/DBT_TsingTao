package et.tsingtaopad.main.visit.shopvisit.termindex;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.ShopVisitCoreFragment;
import et.tsingtaopad.main.visit.shopvisit.termvisit.ShopVisitService;


/**
 * Created by yangwenmin on 2017/12/25.
 * 终端上次拜访详情
 */

public class TermIndexCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "TermIndexCoreFragment";

    private TextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private String seeFlag;
    private String isFirstVisit;
    private MstTermListMStc termStc;
    private MstVisitM visitM;

    private TermIndexService service;
    private ShopVisitService shopVisitService;

    // yyyy-MM-dd HH:mm
    private TextView visitDateTv;
    // 距今几天  整数
    private TextView visitDayTv;

    // 上次拜访时间  yyyy-MM-dd HH:mm
    private String lastTime;


    public TermIndexCoreFragment() {
    }

    public static TermIndexCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, "newInstance(Bundle args)");
        //Bundle args = new Bundle();

        TermIndexCoreFragment fragment = new TermIndexCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            // 获取传送参数
            seeFlag = bundle.getString("seeFlag");
            isFirstVisit = bundle.getString("isFirstVisit");
            termStc = (MstTermListMStc) bundle.getSerializable("termStc");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_termindex, null);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        visitDateTv = (TextView) view.findViewById(R.id.termindex_tv_visitterm_date);// 上传拜访时间  2017-12-28 09:52
        visitDayTv = (TextView) view.findViewById(R.id.termindex_tv_visitterm_day);// 距今几天  3

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG, "initView");
        // 每次页面出现后都重新加载数据
        initData();
    }

    private void initData() {
        DbtLog.logUtils(TAG, "initData");

        titleTv.setText(termStc.getTerminalname());
        confirmRl.setVisibility(View.VISIBLE);

        service = new TermIndexService(getContext());
        shopVisitService = new ShopVisitService(getContext(), null);

        visitM = shopVisitService.findNewVisit(termStc.getTerminalkey(), true);

        visitDateTv.setText(DateUtil.formatDate(0, visitM.getVisitdate()));
        int day = (int) DateUtil.diffDays(new Date(), DateUtil.parse(visitM.getVisitdate().substring(0, 8), "yyyyMMdd"));
        visitDayTv.setText(String.valueOf(day));
        lastTime = DateUtil.formatDate(0, visitM.getVisitdate());

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");
        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
            // 确定
        } else if (i == R.id.banner_navigation_rl_confirm) {

            if (ViewUtil.isDoubleClick(v.getId(), 2500))
                return;
            DbtLog.logUtils(TAG, "进入拜访");
            toStartWithPop();
        }
    }

    // 跳转拜访终端
    private void toStartWithPop() {

        // 删除临时表数据
        shopVisitService.deleteData();

        Bundle args = new Bundle();

        args.putString("isFirstVisit", isFirstVisit);//  是否第一次拜访  0:第一次拜访   1:重复拜访
        args.putString("seeFlag", seeFlag);// 0:拜访   1:查看
        args.putSerializable("termStc", termStc);// 终端信息
        args.putSerializable("visitM", visitM);// 拜访信息
        args.putString("visitDate", visitDateTv.getText().toString());// 上次拜访时间,点击上传的
        args.putString("visitDay", visitDayTv.getText().toString());// 距今几天
        args.putString("lastTime", lastTime);// 上次拜访时间, 不管点没点上传 (可能进去返回了)

        getSupportDelegate().startWithPop(ShopVisitCoreFragment.newInstance(args));
    }
}
