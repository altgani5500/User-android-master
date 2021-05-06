package com.partime.user.uicomman;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

/**
 * Created by vikash on 8/9/17.
 */

public class BaseFragment extends Fragment {
    public BaseActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
    }

    protected void launchActivityForResult(int reqCode, Bundle bundle, Class<? extends BaseActivity> activityClass) {

        Intent intent = new Intent(activity, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, reqCode);
    }
}
