package com.atgc.hd.client.platform.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.atgc.hd.R;
import com.atgc.hd.base.adapter.BaseSingleAdapter;
import com.atgc.hd.base.adapter.holder.ViewHolder;
import com.atgc.hd.comm.net.request.DispatchFinishRequest;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.comm.widget.NiftyDialog;
import com.atgc.hd.db.dao.PlatformInfoDao;
import com.atgc.hd.entity.PatInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * <p>描述：派遣人员消息适配器
 * <p>作者：liangguokui 2018/3/12
 */
public class DispatchMemberAdapter extends BaseSingleAdapter<PatInfo> {
    private static final String REQUEST_GROUP_TAG = StringUtils.getRandomString(20);

    public DispatchMemberAdapter(Context context, boolean isOpenLoadMore) {
        super(context, isOpenLoadMore);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_dispatch_msg;
    }

    @Override
    protected void convert(ViewHolder holder, final PatInfo data, int position) {

        ImageView imageReport = holder.getView(R.id.image_report);
        ImageView imagePicture = holder.getView(R.id.image);

        holder.setText(R.id.tv_date, getString(data.getSendTime()));
        holder.setText(R.id.tv_content1, getString(data.getEventAddr()));
        holder.setText(R.id.tv_content2, getString(data.getMessageContent()));

        String pictureUrl = data.getPicUrl();
        if (StringUtils.isEmpty(pictureUrl)) {
            imagePicture.setVisibility(View.GONE);
        } else {
            imagePicture.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(pictureUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                    .placeholder(R.drawable.ic_loading_fail)
                    .thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                    .error(R.drawable.ic_loading_fail)
                    .into(imagePicture);
        }

        if (data.isReported()) {
            imageReport.setEnabled(false);
        } else {
            imageReport.setEnabled(true);
        }

        imageReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(data);
            }
        });
    }

    private void showDialog(final PatInfo patInfo) {
        String message = "已完成 " + patInfo.getSendTime() + " 的派遣内容";
        NiftyDialog.create(mContext)
                .withTitle("提示")
                .withMessage(message)
                .withButton1Text("取消")
                .withButton2Text("上报")
                .setButton1Click(new NiftyDialog.OnClickActionListener() {
                    @Override
                    public void onClick(NiftyDialog dialog, View clickView) {
                        dialog.dismiss();
                    }
                })
                .setButton2Click(new NiftyDialog.OnClickActionListener() {
                    @Override
                    public void onClick(NiftyDialog dialog, View clickView) {
                        patInfo.setReported(true);

                        DispatchFinishRequest request = new DispatchFinishRequest();
                        request.messageId = patInfo.getMessageID();

                        SocketManager.intance().launch(REQUEST_GROUP_TAG, request, null);

                        PlatformInfoDao.getInstance().delete(patInfo.getMessageID());

                        dialog.dismiss();

                        notifyDataSetChanged();
                    }
                })
                .show();
    }

    private String getString(String source) {
        if (StringUtils.isEmpty(source)) {
            return "--";
        } else {
            return source;
        }
    }
}
