package com.antizikagame.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.antizikagame.R;

/**
 * Created by Thiago on 15/03/2016.
 */
public class ImageFragment extends Fragment {

    private int image;

    public ImageFragment() {
    }

    @SuppressLint("ValidFragment")
    public ImageFragment(int image) {
        this.image = image;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_tuto_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView vImage = (ImageView) view.findViewById(R.id.image);
        vImage.setImageResource(image);
    }
}
