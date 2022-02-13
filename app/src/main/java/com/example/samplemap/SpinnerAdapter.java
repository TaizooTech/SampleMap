package com.example.samplemap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final int[] imageIDs;
    private final String[] itemNames;

    public SpinnerAdapter(Context context) {
        inflater = LayoutInflater.from(context);

        // スピナーに登録する画像の名前リストをstring.xmlから取得
        String[] spinnerImages = context.getResources().getStringArray(R.array.spinner_image_names);
        itemNames = context.getResources().getStringArray(R.array.spinner_item_names);

        // 画像のリソースIDリストを取得
        imageIDs = new int[spinnerImages.length];
        for (int i=0; i < spinnerImages.length; i++) {
            imageIDs[i] = context.getResources().getIdentifier(
                    spinnerImages[i],
                    "drawable",
                    context.getPackageName());
        }
    }

    @Override
    public int getCount() {
        return imageIDs.length;
    }

    @Override
    public Object getItem(int position) {
        return imageIDs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.spinner_layout, null);
        }

        ((ImageView) convertView.findViewById(R.id.iv_item)).setImageResource(imageIDs[position]);
        ((TextView) convertView.findViewById(R.id.tv_item)).setText(itemNames[position]);
        return convertView;
    }
}
