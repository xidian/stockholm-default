package com.stockholm.business.home;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.stockholm.api.business.DisplayBean;
import com.stockholm.business.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayAdapter extends PagerAdapter {

    private Context context;
    private List<DisplayBean> list;

    public DisplayAdapter(Context context, List<DisplayBean> data) {
        this.context = context;
        this.list = data == null ? new ArrayList<>() : data;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.layout_item_image, null);
        ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
        DisplayBean bean = list.get(position);
        if (bean.isGif()) {
            Glide.with(context).load(bean.getImageUrl())
                    .asGif()
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.shape_place_holder)
                    .error(R.drawable.shape_place_holder)
                    .into(ivItem);
        } else {
            Glide.with(context).load(bean.getImageUrl())
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.shape_place_holder)
                    .error(R.drawable.shape_place_holder)
                    .into(ivItem);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setNewData(List<DisplayBean> data) {
        this.list = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    public List<DisplayBean> getData() {
        return list;
    }

}