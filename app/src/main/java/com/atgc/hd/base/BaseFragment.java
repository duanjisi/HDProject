package com.atgc.hd.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ViewSwitcher;

import com.atgc.hd.R;
import com.atgc.hd.comm.widget.NiftyDialog;

/**
 * <p>描述： fraggment基类
 * <p>作者： liangguokui 2018/1/18
 */
public abstract class BaseFragment extends Fragment {
    protected Bundle bundle;

    protected ViewSwitcher parent;

    protected FragmentActivity parentActivity;

    private NiftyDialog progressDialog;

//    protected LoadingTipsHelper loadingTipsHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(layoutResourceId(), container, false);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLoadingTips();
    }

    private void initLoadingTips() {
//        View parentLoadingView = findViewById(R.id.layout_tips);
//
//        if (parentLoadingView != null) {
//            loadingTipsHelper = new LoadingTipsHelper(parentLoadingView);
//        }
    }

    protected View inflate(int layoutResID) {
        return this.parentActivity.getLayoutInflater().inflate(layoutResID, null);
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    protected void showContent() {
        if (this.parent.getDisplayedChild() < this.parent.getChildCount() - 1) {
            this.parent.showNext();
        }
    }


    protected void hideKeyboard(IBinder binder) {
        if (binder != null) {
            FragmentActivity activity = getActivity();
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binder, 0);
        }
    }

    public void onFragmentSelected() {
    }

    public void showProgressDialog() {
        showProgressDialog("请稍候...");
    }

    public void showProgressDialog(String msg) {
        progressDialog = NiftyDialog.create(parentActivity);
        progressDialog
                .withTitleColor("#FFFFFF")
                .withMessageColor("#FFFFFFFF")
                .isCancelableOnTouchOutside(true)
                .withDuration(500)
                .setCustomView(R.layout.request_progress_layout, parentActivity)
                .withCustomViewMessage(R.id.tv_content, msg)
                .show();
    }

    public void dismissProgressBarDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void runOnParentAtyUIThread(Runnable runnable) {
        parentActivity.runOnUiThread(runnable);
    }

    public final <T extends View> T findViewById(int id) {
        View childView = getView().findViewById(id);
        return (T) childView;
    }

    public final <T extends View> T findViewById(View parent, int id) {
        View childView = parent.findViewById(id);
        return (T) childView;
    }

    public abstract int layoutResourceId();

    public String toString() {
        return getClass().getSimpleName();
    }
}
