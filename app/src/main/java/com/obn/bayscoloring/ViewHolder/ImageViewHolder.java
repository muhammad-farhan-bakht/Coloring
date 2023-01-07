package com.obn.bayscoloring.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.obn.bayscoloring.Interface.ImageOnClick;
import com.obn.bayscoloring.R;


public class ImageViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public ImageButton imageButton;

    private ImageOnClick imageOnClick;

    public void setImageOnClick(ImageOnClick imageOnClick) {
        this.imageOnClick = imageOnClick;
    }

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);

         imageView = itemView.findViewById(R.id.image_outline);
        imageButton = itemView.findViewById(R.id.showWork);


         itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 imageOnClick.onClick(getAdapterPosition());
             }
         });

    }
}
