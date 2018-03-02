package com.atgc.hd.base.adapter.animator;

/**
 * Copyright (C) 2018 Wasabeef
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

public class FadeInDownAnimator extends AbsItemAnimator {
    private Interpolator addInterpolator;
    private Interpolator removeInterpolator;

    public FadeInDownAnimator() {
        addInterpolator = new BounceInterpolator();
        removeInterpolator = new AccelerateInterpolator();
    }

    public FadeInDownAnimator(Interpolator interpolator) {
        this.addInterpolator = interpolator;
    }

    @Override
    protected void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateRemoveImpl(holder);
    }

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationY(-holder.itemView.getHeight() * 0.3f)
                .alpha(0)
                .setInterpolator(removeInterpolator)
                .setDuration(getRemoveDuration())
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        holder.itemView.setTranslationY(-holder.itemView.getHeight() * 0.3f);
        holder.itemView.setAlpha(0);
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {

        ViewCompat.animate(holder.itemView)
                .translationY(0)
                .alpha(1)
                .setInterpolator(addInterpolator)
                .setDuration(getAddDuration())
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start();
    }
}
