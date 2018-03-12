package et.tsingtaopad.main.visit.shopvisit.line;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.main.visit.shopvisit.line.domain.MstRouteMStc;
import et.tsingtaopad.main.visit.shopvisit.term.TermListCoreFragment;
import et.tsingtaopad.main.visit.shopvisit.term.TermSearchCoreFragment;


/**
 * Created by yangwenmin on 2017/12/25.
 * 选择线路
 */

public class LineListCoreFragment extends BaseCoreFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "LineListCoreFragment";
    private AppCompatTextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private AppCompatTextView gridNameTv;
    private ListViewCompat lineLv;
    private AppCompatButton searchBt;
    private AppCompatEditText searchEt;

    private LineListService service;
    private MstRouteMStc lineStc;
    private List<MstRouteMStc> lineLst;
    private LineListAdapter adapter;

    public LineListCoreFragment() {
    }

    public static LineListCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        LineListCoreFragment fragment = new LineListCoreFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_line, null);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (AppCompatTextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        gridNameTv = (AppCompatTextView) view.findViewById(R.id.line_tv_gridname);
        lineLv = (ListViewCompat) view.findViewById(R.id.line_lv);
        searchEt = (AppCompatEditText) view.findViewById(R.id.term_et_search);
        searchBt = (AppCompatButton) view.findViewById(R.id.term_bt_search);

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        lineLv.setOnItemClickListener(this);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");
        initData();
    }

    private void initData() {
        DbtLog.logUtils(TAG, "initData()");
        service = new LineListService(getContext());
        titleTv.setText(R.string.linelist_title);
        gridNameTv.setText(PrefUtils.getString(getActivity(), "gridName", ""));
        // 绑定LineList数据
        lineLst = service.queryLine();
        adapter = new LineListAdapter(getActivity(), lineLst, confirmRl);
        lineLv.setAdapter(adapter);
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
            toTermListFragment();
        } else if (i == R.id.term_bt_search) {
            searchTermByName();
        }
    }

    // 跳转终端选择页面
    private void toTermListFragment(){
        DbtLog.logUtils(TAG, "toTermListFragment");
        lineStc = lineLst.get(adapter.getSelectItem());
        Bundle bundle = new Bundle();
        bundle.putSerializable("lineStc", lineStc);
        getSupportDelegate().start(TermListCoreFragment.newInstance(bundle));
    }

    // 根据终端名称 跳转模糊搜索Fragment
    private void searchTermByName(){
        DbtLog.logUtils(TAG, "searchTermByName");
        String termName = searchEt.getText().toString();
        if (CheckUtil.isBlankOrNull(termName)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.linelist_namenull), Toast.LENGTH_SHORT).show();
        } else {
            TermSearchCoreFragment fragment = new TermSearchCoreFragment();
            Bundle bundle = new Bundle();
            bundle.putString("seacrch", termName);
            getSupportDelegate().start(TermSearchCoreFragment.newInstance(bundle));
        }
    }

    // listview条目点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setSelectItem(position);
        adapter.notifyDataSetInvalidated();
        confirmRl.setVisibility(View.VISIBLE);
    }
}
