package et.tsingtaopad.dd.ddxt.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;

/**
 * Created by yangwenmin on 2018/3/26.
 */

public class XtBaseVisitFragment extends BaseFragmentSupport{

    protected String visitId;
    protected String termId;
    protected String termName;
    protected String seeFlag;
    protected String visitDate;
    protected String lastTime;
    protected XtTermSelectMStc termStc;
    protected String channelId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 获取传递过来的数据
        Bundle bundle = getArguments();
        visitId = bundle.getString("visitKey");
        termId = bundle.getString("termId");
        termName = bundle.getString("termname");
        visitDate = bundle.getString("visitDate");
        lastTime = bundle.getString("lastTime");
        seeFlag = bundle.getString("seeFlag");
        channelId = bundle.getString("channelId");
        termStc = (XtTermSelectMStc) bundle.getSerializable("termStc");
    }
}
