package com.webandappdevelopment.serviceshop.ServiceProviderList.Slider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.webandappdevelopment.serviceshop.R;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    Context context;
    List<SliderItem> listItem;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context, List<SliderItem> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(com.webandappdevelopment.serviceshop.R.layout.row_slider, container, false);
        SliderItem item = listItem.get(position);
        ImageView slide_imageView = view.findViewById(R.id.sliderRow_ivSlider);
        Picasso.get().load(item.getImg_url())
                .placeholder(R.mipmap.ic_launcher_new)
                .into(slide_imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
