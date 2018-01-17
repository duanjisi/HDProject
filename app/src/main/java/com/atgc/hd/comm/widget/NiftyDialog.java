package com.atgc.hd.comm.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atgc.hd.R;

/*
 * Copyright 2014 litao
 * https://github.com/sd6352051/NiftyDialogEffects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class NiftyDialog extends Dialog implements DialogInterface {

    private final String defTextColor = "#FFFFFFFF";

    private final String defDividerColor = "#11000000";

    private final String defMsgColor = "#FFFFFFFF";

    private final String defDialogColor = "#FFE74C3C";


    private static Context tmpContext;

    private LinearLayout mLinearLayoutView;

    private RelativeLayout mRelativeLayoutView;

    private LinearLayout mLinearLayoutMsgView;

    private LinearLayout mLinearLayoutTopView;

    private FrameLayout mFrameLayoutCustomView;

    private LinearLayout mButtonPannel;

    private View mDialogView;

    private TextView mTitle;

    private TextView mMessage;

    private ImageView mIcon;

    private Button mButton1;

    private Button mButton2;

    private int mDuration = -1;

    private static int mOrientation = 1;

    private boolean isCancelable = true;

    private static NiftyDialog instance;

    public NiftyDialog(Context context) {
        super(context);
        init(context);

    }

    public NiftyDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

    }

    public static NiftyDialog create(Context context) {

        if (instance == null || !tmpContext.equals(context)) {
            synchronized (NiftyDialog.class) {
                if (instance == null || !tmpContext.equals(context)) {
                    instance = new NiftyDialog(context, R.style.dialog_untran);
                }
            }
        }
        tmpContext = context;
        return instance;
//        return new NiftyDialog(context, R.style.dialog_untran);

    }

    private void init(Context context) {

        mDialogView = View.inflate(context, R.layout.dialog_layout, null);

        mLinearLayoutView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
        mRelativeLayoutView = (RelativeLayout) mDialogView.findViewById(R.id.main);
        mLinearLayoutTopView = (LinearLayout) mDialogView.findViewById(R.id.topPanel);
        mLinearLayoutMsgView = (LinearLayout) mDialogView.findViewById(R.id.contentPanel);
        mFrameLayoutCustomView = (FrameLayout) mDialogView.findViewById(R.id.customPanel);
        mButtonPannel = (LinearLayout) mDialogView.findViewById(R.id.buttonPanel);

        mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
        mMessage = (TextView) mDialogView.findViewById(R.id.message);
        mIcon = (ImageView) mDialogView.findViewById(R.id.icon);

        mButton1 = (Button) mDialogView.findViewById(R.id.button1);
        mButton2 = (Button) mDialogView.findViewById(R.id.button2);

        mLinearLayoutMsgView.setVisibility(View.GONE);
        mLinearLayoutTopView.setVisibility(View.GONE);
        mButtonPannel.setVisibility(View.GONE);

        setOnClickListener();

        setContentView(mDialogView);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                mLinearLayoutView.setVisibility(View.VISIBLE);
            }
        });
        mRelativeLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCancelable) dismiss();
            }
        });
    }

    private void setOnClickListener() {
        mLinearLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void toDefault() {
        mTitle.setTextColor(Color.parseColor(defTextColor));
        mMessage.setTextColor(Color.parseColor(defMsgColor));
        mLinearLayoutView.setBackgroundColor(Color.parseColor(defDialogColor));
    }

    public NiftyDialog withTitle(CharSequence title) {
        toggleView(mLinearLayoutTopView, title);
        mTitle.setText(title);
        return this;
    }

    public NiftyDialog withTitleColor(String colorString) {
        mTitle.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialog withTitleColor(int color) {
        mTitle.setTextColor(color);
        return this;
    }

    public NiftyDialog withMessage(int textResId) {
        mFrameLayoutCustomView.setVisibility(View.GONE);
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);

        toggleView(mLinearLayoutMsgView, textResId);
        mMessage.setText(textResId);
        return this;
    }

    public NiftyDialog withMessage(CharSequence msg) {
        mFrameLayoutCustomView.setVisibility(View.GONE);
        mLinearLayoutMsgView.setVisibility(View.VISIBLE);

        toggleView(mLinearLayoutMsgView, msg);
        mMessage.setText(msg);
        return this;
    }

    /**
     * 必须放在 setCustomView() 后执行
     *
     * @param targetId
     * @param msg
     * @return
     */
    public NiftyDialog withCustomViewMessage(int targetId, CharSequence msg) {
        if (mFrameLayoutCustomView == null) {
            return this;
        } else {
            TextView tvMsg = (TextView) mFrameLayoutCustomView.findViewById(targetId);
            tvMsg.setText(msg);
        }
        return this;
    }

    public NiftyDialog withMessageColor(String colorString) {
        mMessage.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialog withMessageColor(int color) {
        mMessage.setTextColor(color);
        return this;
    }

//    public NiftyDialog withDialogColor(String colorString) {
//        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(Color
//                .parseColor(colorString)));
//        return this;
//    }
//
//    public NiftyDialog withDialogColor(int color) {
//        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(color));
//        return this;
//    }

    public NiftyDialog withIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    public NiftyDialog withIcon(Drawable icon) {
        mLinearLayoutTopView.setVisibility(View.GONE);
        mIcon.setImageDrawable(icon);
        return this;
    }

    public NiftyDialog withDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

//    public NiftyDialog withEffect(Effectstype type) {
//        this.type = type;
//        return this;
//    }

    public NiftyDialog withButtonDrawable(int resid) {
        mButton1.setBackgroundResource(resid);
        mButton2.setBackgroundResource(resid);
        return this;
    }

    public NiftyDialog withButton1Text(CharSequence text) {
        mButtonPannel.setVisibility(View.VISIBLE);
        mButton1.setVisibility(View.VISIBLE);
        mButton1.setText(text);
        return this;
    }

    public NiftyDialog withButton2Text(CharSequence text) {
        mButtonPannel.setVisibility(View.VISIBLE);
        mButton2.setVisibility(View.VISIBLE);
        mButton2.setText(text);
        return this;
    }

    public NiftyDialog setButton1Click(final OnClickActionListener click) {
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(NiftyDialog.this, v);
            }
        });

        return this;
    }

    public NiftyDialog setButton2Click(final OnClickActionListener click) {
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(NiftyDialog.this, v);
            }
        });

        return this;
    }

//    public NiftyDialog setButton1Click(View.OnClickListener click) {
//        mButton1.setOnClickListener(click);
//        return this;
//    }
//
//    public NiftyDialog setButton2Click(View.OnClickListener click) {
//        mButton2.setOnClickListener(click);
//        return this;
//    }

    public NiftyDialog setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        return setCustomView(customView);
    }

    public NiftyDialog setCustomView(View view) {

        if (View.VISIBLE == mLinearLayoutTopView.getVisibility()) {
            mLinearLayoutTopView.setVisibility(View.GONE);
        }

        if (View.VISIBLE == mLinearLayoutMsgView.getVisibility()) {
            mLinearLayoutMsgView.setVisibility(View.VISIBLE);
        }

        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(view);
        mFrameLayoutCustomView.setVisibility(View.VISIBLE);
        mLinearLayoutMsgView.setVisibility(View.GONE);
        return this;
    }

    public NiftyDialog isCancelableOnTouchOutside(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public NiftyDialog isCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCancelable(cancelable);
        return this;
    }

    private void toggleView(View view, Object obj) {
        if (obj == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show();
    }

//    private void start(Effectstype type) {
//        BaseEffects animator = type.getAnimator();
//        if (mDuration != -1) {
//            animator.setDuration(Math.abs(mDuration));
//        }
//        animator.start(mRelativeLayoutView);
//    }

    @Override
    public void dismiss() {
        super.dismiss();
        mButton1.setVisibility(View.GONE);
        mButton2.setVisibility(View.GONE);
    }

    public interface OnClickActionListener {
        void onClick(NiftyDialog dialog, View clickView);
    }

}
