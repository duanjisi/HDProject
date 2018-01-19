package com.atgc.hd.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.atgc.hd.R;
import com.atgc.hd.client.widgethelper.BarHelper;

import com.atgc.hd.comm.utils.SysManager;

/**
 * Created by duanjisi on 2018/1/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public Context context;

//    private NiftyDialogBuilder progressDialog;

    private ViewSwitcher contentViewSwitcher;

    private TextView tvAtyTips;

    protected BarHelper barHelper;

    @Override
    public void setContentView(int layoutResID) {

        LinearLayout parentLayout
                = (LinearLayout) getLayoutInflater().inflate(R.layout.base_activity_contentview, null);

        View childView = getLayoutInflater().inflate(layoutResID, null);
        contentViewSwitcher = findViewById(parentLayout, R.id.pannel_content_view);
        contentViewSwitcher.addView(childView);

        super.setContentView(parentLayout);

        tvAtyTips = findViewById(parentLayout, R.id.tv_error_tips);

        showContentView();

        initStatusBar();

        initToolBar();
//
//        initLoadingTips();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    private void initStatusBar() {
        View tempStatusBar = findViewById(R.id.view_status_bar);

        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            ViewGroup.LayoutParams params = tempStatusBar.getLayoutParams();
            params.height = SysManager.getStatusBarHeight();
            tempStatusBar.setLayoutParams(params);
        } else {
            tempStatusBar.setVisibility(View.GONE);
        }
    }

    private void initToolBar() {
        View toolbarView = findViewById(R.id.layout_custom_toolbar);

        barHelper = new BarHelper(this, toolbarView);
        barHelper.setTitle(toolBarTitle());

        barHelper.setActionLeftDrawable(R.drawable.ic_svg_back);

        barHelper.setActionLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.finish();
            }
        });
    }


    public void openActivity(Class<?> clazz) {
        openActivity(clazz, null);
    }

    public void openActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void openActvityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    public void openActvityForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    protected void setTextView(TextView tv, String str) {
        if (!TextUtils.isEmpty(str)) {
            tv.setText(str);
        }
    }

    public void showContentView() {
        if (contentViewSwitcher.getDisplayedChild() == 0) {
            contentViewSwitcher.setDisplayedChild(1);
        }
    }

    /**
     * @param msg    内容
     * @param length true为长时间，false为短时间
     * @return: void
     */
    protected void showToast(String msg, boolean length) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    protected String getText(TextView textView) {
        return textView.getText().toString().trim();
    }


    /**
     * @param msg 内容
     * @return: void
     */
    protected void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public String toolBarTitle() {
        return getString(R.string.app_name);
    }

    public static <T extends View> T findViewById( View view, int id) {
        return (T) view.findViewById(id);
    }
}
