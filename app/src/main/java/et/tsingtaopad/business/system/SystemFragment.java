package et.tsingtaopad.business.system;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class SystemFragment extends BaseFragmentSupport{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_system, container, false);
        return view;
    }

}
