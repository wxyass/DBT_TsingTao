package et.tsingtaopad.dd.ddxt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtTermListFragment extends BaseFragmentSupport implements View.OnClickListener{

    AppCompatButton button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ddxttermlist, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        button = view.findViewById(R.id.btn_toact);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(),XtVisitShopActivity.class);
        startActivity(intent);
    }
}
