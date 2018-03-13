package com.atgc.hd.comm.utils;

import android.util.SparseArray;
import android.view.View;

import com.atgc.hd.R;

/**
 *
 * @author lianggk
 * @date 17/7/23
 */
public class ViewHolder {
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray) view.getTag(R.id.viewholder_key);
        if (viewHolder == null) {
            viewHolder = new SparseArray();
            view.setTag(R.id.viewholder_key, viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView != null) {
            return (T) childView;
        }
        childView = view.findViewById(id);
        viewHolder.put(id, childView);
        return (T) childView;
    }
}
