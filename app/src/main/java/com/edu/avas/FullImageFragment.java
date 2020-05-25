package com.edu.avas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullImageFragment extends Fragment {
    public ImageView fullImage;

    public FullImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_image, container, false);

        Bundle bundle = this.getArguments();
        String url = bundle.getString("url");

        fullImage = view.findViewById(R.id.showFullImage);
        Picasso.get().load(url).into(fullImage);

        return view;
    }
}