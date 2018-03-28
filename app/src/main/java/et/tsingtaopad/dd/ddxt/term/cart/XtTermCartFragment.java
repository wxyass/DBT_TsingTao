package et.tsingtaopad.dd.ddxt.term.cart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.dd.ddxt.term.cart.adapter.XtTermCartAdapter;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.TermListAdapter;
import et.tsingtaopad.main.visit.shopvisit.term.domain.TermSequence;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtTermCartFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    private TextView lineTv;
    private TextView dateTv;
    private TextView dayTv;
    private EditText searchEt;
    private Button searchBtn;
    private Button updateBtn;
    private Button addBtn;
    private ListView termCartLv;

    private XtTermCartService cartService;
    private List<XtTermSelectMStc> termList= new ArrayList<XtTermSelectMStc>();;
    private XtTermCartAdapter termCartAdapter;
    private List<XtTermSelectMStc> seqTermList;

    private XtTermSelectMStc termStc;
    private String termId;
    private Map<String, String> termPinyinMap;
    private List<XtTermSelectMStc> tempLst;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_termcart, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        backBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_tv_title);
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        lineTv = (TextView) view.findViewById(R.id.xtbf_termcart_tv_visitline);
        dateTv = (TextView) view.findViewById(R.id.xtbf_termcart_tv_visitline_date);
        dayTv = (TextView) view.findViewById(R.id.xtbf_termcart_tv_visitline_day);
        searchEt = (EditText) view.findViewById(R.id.xtbf_termcart_et_search);
        searchBtn = (Button) view.findViewById(R.id.xtbf_termcart_bt_search);
        updateBtn = (Button) view.findViewById(R.id.xtbf_termcart_bt_update);
        addBtn = (Button) view.findViewById(R.id.xtbf_termcart_bt_add);
        termCartLv = (ListView) view.findViewById(R.id.xtbf_termcart_lv);

        searchBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("今日拜访终端列表");
        confirmTv.setText("拜访");
        cartService = new XtTermCartService(getActivity());
        // 设置终端数据 假数据
        termList = cartService.queryCartTermList();
        // 终端拼音集合
        termPinyinMap = cartService.getAllTermPinyin(termList);

        // 若巡店拜访页面销毁了,
        if (termStc != null) {
            for (XtTermSelectMStc term : termList) {
                if (termStc.getTerminalkey().equals(term.getTerminalkey())) {
                    termStc = term;
                }
            }
        }

        // 设置数据,适配器
        searchTerm();
        //seqTermList = getNewMstTermListMStc();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.top_navigation_rl_confirm:
                termStc = (XtTermSelectMStc)confirmBtn.getTag();
                Intent intent = new Intent(getActivity(), XtVisitShopActivity.class);
                intent.putExtra("isFirstVisit", "1");// 非第一次拜访1
                intent.putExtra("termStc", termStc);
                intent.putExtra("seeFlag", "0"); // 0拜访 1查看标识
                startActivity(intent);
                break;
            case R.id.xtbf_termcart_bt_search:
                searchTerm();
                break;
            case R.id.xtbf_termcart_bt_update:
                termCartAdapter.setUpdate(true);
                String s = updateBtn.getText().toString();
                if ("编辑排序".equals(s)) {
                    updateBtn.setText("保存");
                    termCartAdapter.setUpdate(true);
                    termCartAdapter.notifyDataSetChanged();
                } else {
                    updateTermSeq();
                    updateBtn.setText("编辑排序");
                    termCartAdapter.setUpdate(false);
                    searchTerm();
                }
                break;
            case R.id.xtbf_termcart_bt_add:

                break;
            default:
                break;
        }
    }

    /**
     * 查询
     *
     * @return
     */
    public void searchTerm() {

        seqTermList = getNewMstTermListMStc();
        if (termStc == null || "3".equals(termStc.getStatus())) {
            termId = "";
        } else {
            termId = termStc.getTerminalkey();
            if (confirmBtn.getTag() != null) {
                XtTermSelectMStc termStc2 = (XtTermSelectMStc) confirmBtn.getTag();
                if (termStc2.getTerminalkey().equals(termStc.getTerminalkey())) {
                    confirmBtn.setTag(termStc);
                }
            }
        }

        // 根据搜索,查找终端列表
        if (!CheckUtil.isBlankOrNull(searchEt.getText().toString())) {
            tempLst = cartService.searchTermByname(getNewMstTermListMStc(), searchEt.getText().toString().replace(" ", ""), termPinyinMap);
        } else {// 没有搜索
            tempLst = getNewMstTermListMStc();
        }

        // 设置适配器
        termCartAdapter = new XtTermCartAdapter(getActivity(), seqTermList, tempLst, confirmBtn, termId);
        termCartLv.setAdapter(termCartAdapter);

        // 若巡店拜访页面销毁了,根据下面判断 拜访按钮是否出现
        List<String> termIdLst = FunUtil.getPropertyByName(tempLst, "terminalkey", String.class);
        if (termStc != null && termIdLst.contains(termId)) {
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setTag(termStc);
        } else {
            confirmBtn.setVisibility(View.INVISIBLE);
        }
    }


    /***
     * 终端集合封装一个新的终端集合（防御联动修改）
     * @return
     */
    private List<XtTermSelectMStc> getNewMstTermListMStc() {
        List<XtTermSelectMStc> termList_new = new ArrayList<XtTermSelectMStc>();
        if (termList != null) {
            for (XtTermSelectMStc item : termList) {
                XtTermSelectMStc item_new = new XtTermSelectMStc();
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

    /***
     * 修改终端顺序
     */
    private void updateTermSeq() {
        termList = seqTermList;// seqTermList:修改后的
        List<TermSequence> termSequenceList = new ArrayList<TermSequence>();
        for (int i = 0; i < termList.size(); i++) {
            XtTermSelectMStc term = termList.get(i);
            if (!((i + 1) + "").equals(term.getSequence())) {
                // 根据终端在集合中的顺序,重新排序(从1开始)
                term.setSequence((i + 1) + "");
            }
            TermSequence termSequence = new TermSequence();
            termSequence.setSequence(term.getSequence());
            termSequence.setTerminalkey(term.getTerminalkey());
            termSequenceList.add(termSequence);
        }
        // 将排序上传服务器
        cartService.updateTermSequence(termSequenceList);
    }
}
