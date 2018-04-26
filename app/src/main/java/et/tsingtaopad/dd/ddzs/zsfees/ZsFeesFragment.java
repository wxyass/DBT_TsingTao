package et.tsingtaopad.dd.ddzs.zsfees;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.AlertKeyValueAdapter;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnDismissListener;
import et.tsingtaopad.core.view.alertview.OnItemClickListener;
import et.tsingtaopad.db.table.MitValsupplyMTemp;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.invoicing.XtInvoicingService;
import et.tsingtaopad.dd.ddxt.invoicing.domain.XtInvoicingStc;
import et.tsingtaopad.dd.ddzs.zsinvoicing.ZsInvocingAmendFragment;
import et.tsingtaopad.dd.ddzs.zsinvoicing.ZsInvoicingAdapter;
import et.tsingtaopad.dd.ddzs.zsinvoicing.ZsInvoicingService;
import et.tsingtaopad.dd.ddzs.zsinvoicing.zsaddinvoicing.ZsAddInvoicingFragment;
import et.tsingtaopad.dd.ddzs.zsinvoicing.zsaddinvoicing.ZsInvocingAddDataFragment;
import et.tsingtaopad.dd.ddzs.zsshopvisit.ZsVisitShopActivity;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.listviewintf.IClick;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class ZsFeesFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private final String TAG = "ZsFeesFragment";

    MyHandler handler;

    public static final int FEES_ZS_ADD_SUC = 71;//
    public static final int FEES_INIT_AMEND = 72;//



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_fees, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);

        // 初始化数据
        initData();

    }

    private void initData() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<ZsFeesFragment> fragmentRef;

        public MyHandler(ZsFeesFragment fragment) {
            fragmentRef = new SoftReference<ZsFeesFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ZsFeesFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }

            // 处理UI 变化
            switch (msg.what) {
                case FEES_ZS_ADD_SUC:
                    //fragment.showAddProSuc(products, agency);
                    break;
                case FEES_INIT_AMEND: // 督导输入数据后
                    //fragment.showAdapter();
                    break;
            }
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");

        // 保存追溯 进销存数据  MitValsupplyMTemp
        //invoicingService.saveZsInvoicing(dataLst);
    }

}
