package com.atgc.hd.comm.widget.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.comm.utils.ViewHolder;

import java.util.List;

/**
 * @author lianggk
 * @date 17/9/29
 */
public class PopupMenuExtendAdapter extends BaseAdapter {
    private Context mContext;
    private List<PopupItem> mPopupItemList;
    private final LayoutInflater mInflater;

    private int positionSelected = -1;

    public PopupMenuExtendAdapter(Context context, List<PopupItem> popupItemList) {
        mContext = context;
        mPopupItemList = popupItemList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPopupItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPopupItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.nim_popup_menu_list_item, null);
        }

        PopupItem item = mPopupItemList.get(position);

        TextView tvContent = ViewHolder.get(convertView, R.id.popup_menu_title);

        tvContent.setText(item.getTitle());

        return convertView;
    }

    public int getPositionSelected() {
        return positionSelected;
    }

    public void setPositionSelected(int positionSelected) {
        this.positionSelected = positionSelected;
        notifyDataSetChanged();
    }
}
