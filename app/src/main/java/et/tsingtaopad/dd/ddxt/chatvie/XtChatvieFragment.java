package et.tsingtaopad.dd.ddxt.chatvie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.chatvie.addchatvie.XtAddChatVieFragment;
import et.tsingtaopad.dd.ddxt.chatvie.domain.XtChatVieStc;
import et.tsingtaopad.dd.ddxt.checking.num.XtQuickCollectFragment;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.adapter.VieSourceAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.adapter.VieStatusAdapter;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtChatvieFragment extends XtBaseVisitFragment implements View.OnClickListener {


    private ImageView point1;
    private Button addrelationBtn;
    private ListView viesourceLv;
    private ListView viestatusLv;
    private et.tsingtaopad.view.DdSlideSwitch clearvieSw;
    private EditText visitreportEt;
    private Button nextBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_chatvie, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        addrelationBtn = (Button) view.findViewById(R.id.xtbf_chatvie_bt_addrelation);
        viesourceLv = (ListView) view.findViewById(R.id.xtbf_chatvie_lv_viesource);
        viestatusLv = (ListView) view.findViewById(R.id.xtbf_chatvie_lv_viestatus);
        clearvieSw = (et.tsingtaopad.view.DdSlideSwitch) view.findViewById(R.id.xtbf_chatvie_sw_clearvie);
        visitreportEt = (EditText) view.findViewById(R.id.xtbf_chatvie_et_visitreport);
        nextBtn = (Button) view.findViewById(R.id.xtbf_chatvie_bt_next);

        addrelationBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toast.makeText(getActivity(), "聊竞品" + "/" + termId + "/" + termName, Toast.LENGTH_SHORT).show();

        initProData();

        // 竞品来源
        XtVieSourceAdapter xtVieSourceAdapter = new XtVieSourceAdapter(
                getActivity(), "", lst, "", null, null, null, null);//竞品来源
        viesourceLv.setAdapter(xtVieSourceAdapter);
        ViewUtil.setListViewHeight(viesourceLv);

        // 竞品情况
        XtVieStatusAdapter xtstatusAdapter = new XtVieStatusAdapter(getActivity(), lst);//竞品情况
        viestatusLv.setAdapter(xtstatusAdapter);
        ViewUtil.setListViewHeight(viestatusLv);
    }

    List<XtChatVieStc> lst;
    XtChatVieService xtChatVieService;

    private void initProData() {

        xtChatVieService = new XtChatVieService(getActivity(), null);
        xtChatVieService.delRepeatVistProduct(visitId);
        lst = xtChatVieService.queryVieProTemp(visitId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xtbf_chatvie_bt_addrelation:// 新增竞品
                XtVisitShopActivity xtVisitShopActivity = (XtVisitShopActivity) getActivity();
                xtVisitShopActivity.changeXtvisitFragment(new XtAddChatVieFragment(), "xtaddchatviefragment");
                break;
            case R.id.xtbf_chatvie_bt_next:// 下一页

                break;

            default:
                break;
        }
    }
}
