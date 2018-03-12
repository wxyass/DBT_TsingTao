package et.tsingtaopad.main.visit.shopvisit.term;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.main.visit.shopvisit.line.domain.MstRouteMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.TermListAdapter;
import et.tsingtaopad.main.visit.shopvisit.term.domain.TermSequence;
import et.tsingtaopad.main.visit.shopvisit.termindex.TermIndexCoreFragment;
import et.tsingtaopad.main.visit.termadd.TermAddCoreFragment;


/**
 * Created by yangwenmin on 2017/12/25.
 * 终端选择
 */

public class TermListCoreFragment extends BaseCoreFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "TermListCoreFragment";
    private TextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private TextView visitLine;
    private TextView visitDate;
    private TextView visitDay;
    private EditText searchEt;
    private Button searchBt;
    private Button addTermBt;
    private Button updateTermBt;
    private ListView termLv;

    private MstRouteMStc lineStc;

    private TermListService service;

    private List<MstTermListMStc> termLst;
    private List<MstTermListMStc> tempLst;
    private Map<String, String> termPinyinMap;

    private String termId;
    private List<MstTermListMStc> seqTermList;
    private MstTermListMStc termStc;
    private TermListAdapter adapter;

    public TermListCoreFragment() {
    }

    public static TermListCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, "newInstance(Bundle args)");
        //Bundle args = new Bundle();
        TermListCoreFragment fragment = new TermListCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            // 获取参数
            lineStc = (MstRouteMStc) bundle.get("lineStc");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_terminal, null);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        visitLine = (TextView) view.findViewById(R.id.term_tv_visitline);
        visitDate = (TextView) view.findViewById(R.id.term_tv_visitline_date);
        visitDay = (TextView) view.findViewById(R.id.term_tv_visitline_day);
        termLv = (ListView) view.findViewById(R.id.term_lv);
        searchEt = (EditText) view.findViewById(R.id.term_et_search);
        searchBt = (Button) view.findViewById(R.id.term_bt_search);
        addTermBt = (Button) view.findViewById(R.id.term_bt_add);
        updateTermBt = (Button) view.findViewById(R.id.term_bt_update);

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        addTermBt.setOnClickListener(this);
        updateTermBt.setOnClickListener(this);
        termLv.setOnItemClickListener(this);
        searchEt.addTextChangedListener(watcher);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        // 每次页面出现后都重新加载数据
        initData();
    }

    private void initData() {
        DbtLog.logUtils(TAG, "initData");
        service = new TermListService(getContext());

        // 根据后台配置的权限,设置新增终端是否显示
        String bfgl = PrefUtils.getString(getActivity(), "bfgl", "-1");
        if (bfgl.contains("1000001")) {
            addTermBt.setVisibility(View.VISIBLE);
        } else {
            addTermBt.setVisibility(View.GONE);
        }

        titleTv.setText(R.string.termlist_title);// 标题: 选择终端
        visitLine.setText(lineStc.getRoutename());// 06号路线

        if (CheckUtil.isBlankOrNull(lineStc.getPrevDate())) {
            visitDate.setText("");// 该路线上次拜访时间 2017-12-22
            visitDay.setText("0");
        } else if (lineStc.getPrevDate().length() >= 8) {
            Date prevDate = DateUtil.parse(lineStc.getPrevDate(), "yyyyMMddHHmmss");
            visitDate.setText(DateUtil.formatDate(prevDate, "yyyy-MM-dd"));// 该路线上次拜访时间 2017-12-22
            int day = (int) DateUtil.diffDays(new Date(), DateUtil.parse(lineStc.getPrevDate().substring(0, 8), "yyyyMMdd"));
            visitDay.setText(String.valueOf(day));// 距今几天
        }

        updateTermBt.setBackgroundResource(R.drawable.bt_sequence_edit);
        updateTermBt.setText("修改");

        // 绑定TermList数据
        List<String> routes = new ArrayList<String>();
        routes.add(lineStc.getRoutekey());
        termLst = service.queryTerminal(routes);
        termPinyinMap = service.getTermPinyin(termLst);

        if (termStc != null) {
            for (MstTermListMStc term : termLst) {
                if (termStc.getTerminalkey().equals(term.getTerminalkey())) {
                    termStc = term;
                }
            }
        }
        this.searchTerm();

    }

    /**
     * 查询
     *
     * @return
     */
    public void searchTerm() {
        DbtLog.logUtils(TAG, "searchTerm");

        seqTermList = getNewMstTermListMStc();
        if (termStc == null || "3".equals(termStc.getStatus())) {
            termId = "";
        } else {
            termId = termStc.getTerminalkey();
            if (confirmRl.getTag() != null) {
                MstTermListMStc termStc2 = (MstTermListMStc) confirmRl.getTag();
                if (termStc2.getTerminalkey().equals(termStc.getTerminalkey())) {
                    confirmRl.setTag(termStc);
                }
            }
        }

        // 输入框不为空时
        String termNameEt = searchEt.getText().toString();
        if (!CheckUtil.isBlankOrNull(termNameEt)) {
            tempLst = service.searchTerm(getNewMstTermListMStc(), termNameEt.replace(" ", ""), termPinyinMap);
        } else {// 为空时
            tempLst = getNewMstTermListMStc();
        }

        // 列表适配器
        adapter = new TermListAdapter(getContext(), seqTermList, tempLst, confirmRl, termId);
        termLv.setAdapter(adapter);
        updateTermBt.setText("修改");
        List<String> termIdLst = FunUtil.getPropertyByName(tempLst, "terminalkey", String.class);
        if (termStc != null && termIdLst.contains(termId)) {
            confirmRl.setVisibility(View.VISIBLE);
            confirmRl.setTag(termStc);
        } else {
            confirmRl.setVisibility(View.INVISIBLE);
        }
    }

    /***
     * 终端集合封装一个新的终端集合（防御联动修改）
     * @return
     */
    private List<MstTermListMStc> getNewMstTermListMStc() {
        DbtLog.logUtils(TAG, "getNewMstTermListMStc");
        List<MstTermListMStc> termList_new = new ArrayList<MstTermListMStc>();
        if (termLst != null) {
            for (MstTermListMStc item : termLst) {
                MstTermListMStc item_new = new MstTermListMStc();
                item_new.setRoutekey(item.getRoutekey());
                item_new.setTerminalkey(item.getTerminalkey());
                item_new.setTerminalcode(item.getTerminalcode());
                item_new.setTerminalname(item.getTerminalname());
                item_new.setStatus(item.getStatus());
                item_new.setSequence(item.getSequence());
                item_new.setMineFlag(item.getMineFlag());
                item_new.setVieFlag(item.getVieFlag());
                item_new.setMineProtocolFlag(item.getMineProtocolFlag());
                item_new.setVieProtocolFlag(item.getVieProtocolFlag());
                item_new.setSyncFlag(item.getSyncFlag());
                item_new.setUploadFlag(item.getUploadFlag());
                item_new.setMinorchannel(item.getMinorchannel());
                item_new.setTerminalType(item.getTerminalType());
                item_new.setVisitTime(item.getVisitTime());
                termList_new.add(item_new);
            }
        }
        return termList_new;
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

            confirmVisit();
        }
        // 查询终端
        else if (i == R.id.term_bt_search) {
            this.searchTerm();
        } else if (i == R.id.term_bt_add) {
            DbtLog.logUtils(TAG, "新增终端");
            getSupportDelegate().start(TermAddCoreFragment.newInstance(null));
        } else if (i == R.id.term_bt_update) {
            toTermSeq();

        }

    }

    /**
     * 重复拜访二次确认
     */
    private void confirmVisit() {
        DbtLog.logUtils(TAG, "confirmVisit");
        termStc = (MstTermListMStc) confirmRl.getTag();
        if (termStc == null) {
            return;
        }
        // 如果已结束拜访(当天该终端已确定上传,点击了确定按钮)
        if (ConstValues.FLAG_1.equals(termStc.getUploadFlag()) || ConstValues.FLAG_1.equals(termStc.getSyncFlag())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.termlist_confirm_title);
            builder.setMessage(R.string.termlist_confirm_msg);
            builder.setCancelable(false);
            // 重复拜访
            builder.setPositiveButton(R.string.termlist_confirm_sure, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DbtLog.logUtils(TAG, "termkey:" + termStc.getTerminalkey() + " termname：" + termStc.getTerminalname());
                    dialog.dismiss();
                    /*Intent intent = new Intent(getActivity(), TermIndexActivity.class);
                    intent.putExtra("isFirstVisit", "1");// 非第一次拜访1
                    intent.putExtra("termStc", termStc);
                    intent.putExtra("seeFlag", "0");
                    getActivity().startActivity(intent);*/
                    Bundle args = new Bundle();
                    args.putString("isFirstVisit", "1");// 非第一次拜访1
                    args.putSerializable("termStc", termStc);// 终端信息
                    args.putString("seeFlag", "0");// 0:拜访   1:查看
                    getSupportDelegate().start(TermIndexCoreFragment.newInstance(args));
                }
            });
            // 取消重复拜访
            builder.setNegativeButton(R.string.termlist_confirm_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            // 查看
            builder.setNeutralButton(R.string.termlist_confirm_see, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    /*Intent intent = new Intent(getActivity(), TermIndexActivity.class);
                    intent.putExtra("isFirstVisit", "1");// 非第一次拜访1
                    intent.putExtra("termStc", termStc);
                    intent.putExtra("seeFlag", "1"); // 查看标识
                    getActivity().startActivity(intent);*/
                    Bundle args = new Bundle();
                    args.putString("isFirstVisit", "1");// 非第一次拜访1
                    args.putSerializable("termStc", termStc);// 终端信息
                    args.putString("seeFlag", "1");// 0:拜访   1:查看
                    getSupportDelegate().start(TermIndexCoreFragment.newInstance(args));
                }
            });
            builder.create().show();

        }
        // 该终端当天第一次拜访
        else {
            /*Intent intent = new Intent(getActivity(), TermIndexActivity.class);
            intent.putExtra("isFirstVisit", "0");// 第一次拜访0
            intent.putExtra("termStc", termStc);
            getActivity().startActivity(intent);*/
            Bundle args = new Bundle();
            args.putString("isFirstVisit", "0");// 非第一次拜访1
            args.putSerializable("termStc", termStc);// 终端信息
            args.putString("seeFlag", "0");// 0:拜访   1:查看
            getSupportDelegate().start(TermIndexCoreFragment.newInstance(args));
        }
    }


    // 修改按钮图片 修改状态
    private void toTermSeq() {
        DbtLog.logUtils(TAG, "toTermSeq");
        adapter.setUpdate(true);
        String s = updateTermBt.getText().toString();
        if ("修改".equals(s)) {// 修改 -> 将状态修改为可修改状态
            updateTermBt.setText("保存");
            updateTermBt.setBackgroundResource(R.drawable.bt_sequence_fish);
            adapter.setUpdate(true);
            adapter.notifyDataSetChanged();
        } else// 保存 -> 保存序号
        {
            updateTermSeq();
            updateTermBt.setText("修改");
            updateTermBt.setBackgroundResource(R.drawable.bt_sequence_edit);
            adapter.setUpdate(false);
            searchTerm();
        }
    }

    /***
     * 修改终端顺序,保存序号,并上传
     */
    private void updateTermSeq() {
        termLst = seqTermList;
        List<TermSequence> termSequenceList = new ArrayList<TermSequence>();
        for (int i = 0; i < termLst.size(); i++) {
            MstTermListMStc term = termLst.get(i);
            if (!((i + 1) + "").equals(term.getSequence())) // 如果集合的顺序与所填的值不一样 修改
            {
                term.setSequence((i + 1) + "");
            }
            TermSequence termSequence = new TermSequence();
            termSequence.setSequence(term.getSequence());
            termSequence.setTerminalkey(term.getTerminalkey());
            termSequenceList.add(termSequence);
        }
        //service.updateTermSequence(termSequenceList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //这里可以看到变化的内容，可以看到s是否有变化已经s的内容
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            searchTerm();
        }
    };
}
