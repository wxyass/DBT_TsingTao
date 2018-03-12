package et.tsingtaopad.main.system.question;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MstQuestionsanswersInfo;

/**
 * Created by yangwenmin on 2017/12/25.
 * 第一个界面(由巡店管理主页跳转过来)  用来拷贝的
 */

public class QueryFeedbackCoreFragment extends BaseCoreFragment implements View.OnClickListener , AbsListView.OnScrollListener, RadioGroup.OnCheckedChangeListener{

    public static final String TAG = "QueryFeedbackCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private RadioGroup queryfeedback_rg;
    private RadioButton[] radioButton;
    //	private RadioButton querfeedback_cb1_qs;
    //	private RadioButton querfeedback_cb2_qs;
    //	private RadioButton querfeedback_cb3_qs;
    private EditText querfeedback_et_question;
    private EditText mobileEt;
    private Button querfeedback_bt_submit;
    private ListView querfeedback_listview;
    private QueryFeedbackService service;
    private RelativeLayout querfeedback_rl_form;
    private RelativeLayout bg_up_arrow_rl;
    private Button bg_up_arrow;
    private Button bg_up_arrow_top_line;

    private QueryFeedbackAdapter Query_adapter= null;

    private List<MstQuestionsanswersInfo> list = new ArrayList<MstQuestionsanswersInfo>();

    private List<CmmDatadicM> cmmDatadicM = new ArrayList<CmmDatadicM>();

    private int checkedQuerryType = -1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstValues.WAIT1:
                    @SuppressWarnings("unchecked")
                    List<MstQuestionsanswersInfo> list1 = (List<MstQuestionsanswersInfo>) msg.obj;
                    list.addAll(remove(list1));
                    Query_adapter.setData(list);
                    break;
                case ConstValues.WAIT2:
                    List<MstQuestionsanswersInfo> list2 = (List<MstQuestionsanswersInfo>) msg.obj;
                    list2 = remove(list2);
                    list2.addAll(list);
                    list.clear();
                    list = list2;
                    Query_adapter.setData(list);
                    break;
            }
        }
    };

    private List<MstQuestionsanswersInfo> remove(List<MstQuestionsanswersInfo> list1){
        for(int j = 0;j<list.size();j++){
            for(int i =0;i<list1.size();i++){
                if(list1.get(i).getQakey().equals(list.get(j).getQakey())){//主键一致
                    synchronized(list1){
                        list1.remove(i);
                    }
                }
            }
        }
        return list1;
    }

    public QueryFeedbackCoreFragment() {
    }

    public static QueryFeedbackCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        QueryFeedbackCoreFragment fragment = new QueryFeedbackCoreFragment();
        return fragment;
    }

    public static QueryFeedbackCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, " newInstance(Bundle args)");
        //Bundle args = new Bundle();

        QueryFeedbackCoreFragment fragment = new QueryFeedbackCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // 在这个方法中 只处理从上个Fragment传递过来的数据,若没数据传递过来,可删除该方法
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            //termname = bundle.getString(TERM_NAME);
            //termkey = bundle.getString(TERM_KEY);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DbtLog.logUtils(TAG, "onActivityCreated");

    }

    // 只用来初始化控件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.business_queryfeedback_test, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (AppCompatTextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        queryfeedback_rg = (RadioGroup) view.findViewById(R.id.business_queryfeedback_radiogroup);
        //		querfeedback_cb1_qs = (RadioButton) view.findViewById(R.id.querfeedback_cb1_qs);
        //		querfeedback_cb2_qs = (RadioButton) view.findViewById(R.id.querfeedback_cb2_qs);
        //		querfeedback_cb3_qs = (RadioButton) view.findViewById(R.id.querfeedback_cb3_qs);
        querfeedback_et_question = (EditText) view.findViewById(R.id.querfeedback_et_question);
        mobileEt = (EditText) view.findViewById(R.id.querfeedback_et_mobile);
        querfeedback_bt_submit = (Button) view .findViewById(R.id.querfeedback_bt_submit);
        querfeedback_listview = (ListView) view.findViewById(R.id.querfeedback_listview);
        bg_up_arrow_rl = (RelativeLayout) view.findViewById(R.id.ll1);
        bg_up_arrow = (Button) view.findViewById(R.id.bg_up_arrow);
        bg_up_arrow_top_line = (Button) view.findViewById(R.id.bg_up_arrow_top_line);
        querfeedback_rl_form = (RelativeLayout) view.findViewById(R.id.querfeedback_rl_form);

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);

        // 绑定事件
        queryfeedback_rg.setOnCheckedChangeListener(this);
        querfeedback_bt_submit.setOnClickListener(this);
        bg_up_arrow.setOnClickListener(this);
        querfeedback_listview.setOnScrollListener(this);
        Query_adapter = new QueryFeedbackAdapter(getContext(), list);
        querfeedback_listview.setAdapter(Query_adapter);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        titleTv.setText("问题反馈");
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG,"onSupportVisible");
        // 每次页面出现后都重新加载数据
        initData();
    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG,"initData");

        service = new QueryFeedbackService(handler, getActivity());
        service.setQuerfeedback_listview(querfeedback_listview);
        service.setQuerfeedback_et_question(querfeedback_et_question);
        service.setQuerfeedback_rl_form(querfeedback_rl_form);
        service.setBg_up_arrow(bg_up_arrow);
        service.setBg_up_arrow_top_line(bg_up_arrow_top_line);
        service.setBg_up_arrow_rl(bg_up_arrow_rl);
        // 本地读取数据
        service.setStyle(1);
        service.asynchronousDataHandler();

        addRadioBox();

    }

    private void addRadioBox(){
        cmmDatadicM = service.searchLocalQuestionType();
        Log.d("tag","questionType--->"+cmmDatadicM.size());
        radioButton =new RadioButton[cmmDatadicM.size()];
        for(int i = 0;i<cmmDatadicM.size();i++){
            radioButton[i] = new RadioButton(getActivity());
            //			radioButton[i].setTextAlignment(R.style.banner_main);
            radioButton[i].setText(cmmDatadicM.get(i).getDicname());
            radioButton[i].setTextColor(Color.parseColor("#000000"));
            radioButton[i].setId(i);
            queryfeedback_rg.addView(radioButton[i]);
        }
        service.setRadiobuttons(radioButton);
    }

    private void addFootView(){
        View view = View.inflate(getActivity(), R.layout.visit_agencyvisit_footview, null);

        querfeedback_listview.addFooterView(view);

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
        // 向上箭头
        }else if(i == R.id.bg_up_arrow){
            // 隐藏展示 提交按钮
            service.display();
        }
        // 提交
        else if(i == R.id.querfeedback_bt_submit){
            try {
                if(checkedQuerryType==-1){//有选项但是没有选择 cmmDatadicM.size()>0&&
                    Toast.makeText(getActivity(), getString(R.string.business_please_select_question_type), Toast.LENGTH_SHORT).show();
                    return;
                }
                // 选择问题类型
                String type = cmmDatadicM.size()==0?"":cmmDatadicM.get(checkedQuerryType).getDiccode();
                service.newDataInsert(type, FunUtil.isBlankOrNullTo(mobileEt.getText(), ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
        Scroll_arg1=arg1;
        Scroll_arg2=arg2;
        Scroll_arg3=arg3;
    }

    /**滑动状态*/
    private int Scroll_arg1,Scroll_arg2,Scroll_arg3;


    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        if((arg1==SCROLL_STATE_TOUCH_SCROLL||arg1==SCROLL_STATE_FLING)&&(Scroll_arg3==Scroll_arg1+Scroll_arg2)&&service!=null){//当滑动时，触摸时
            if(service.isEnd()){//如果已经到末尾了
                Log.d("tag","end");
                return;
            }else if(!service.isQuerying()){//没有正在查询
                service.QueryFeedbackServiceGet();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d("tag","checked->"+checkedId);
        checkedQuerryType = checkedId;

    }
}
