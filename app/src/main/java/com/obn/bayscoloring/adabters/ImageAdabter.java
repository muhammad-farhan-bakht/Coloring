package com.obn.bayscoloring.adabters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.obn.bayscoloring.PaintActivity;
import com.obn.bayscoloring.Interface.ImageOnClick;
import com.obn.bayscoloring.R;
import com.obn.bayscoloring.ViewHolder.ImageViewHolder;
import com.obn.bayscoloring.WorkListAct;
import com.obn.bayscoloring.common.Common;

import java.util.ArrayList;
import java.util.List;


public class ImageAdabter extends RecyclerView.Adapter<ImageViewHolder> {

    private Context mContext;
    private List<Integer> listImages;



    public ImageAdabter(Context mContext) {
        this.mContext = mContext;
        this.listImages = getImages();
    }

    private List<Integer> getImages() {

        List<Integer> results = new ArrayList<>();

        results.add(R.drawable.test); // Listeye resimler eklenebilir.
        results.add(R.drawable.test);
        results.add(R.drawable.test2);
        results.add(R.drawable.test3);


        return results;

    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_images, parent, false);
        return new ImageViewHolder(view) ;
    }


    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {

        holder.imageView.setImageResource(listImages.get(position));


        holder.setImageOnClick((pos -> {
            Common.ITEM_SELECTED = ""+(position+1);
            Common.PICTURE_SELECTED = listImages.get(pos);
            mContext.startActivity(new Intent(mContext, PaintActivity.class));

        }));


        holder.imageButton.setOnClickListener(view -> {
            Common.ITEM_SELECTED = ""+(1+position);
            mContext.startActivity(new Intent(mContext, WorkListAct.class));
        });

    }

        @Override
        public int getItemCount() { return listImages.size();  }

}
