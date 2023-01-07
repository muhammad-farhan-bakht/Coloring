package com.obn.bayscoloring.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.obn.bayscoloring.Interface.ImageOnClick;
import com.obn.bayscoloring.R;
import com.obn.bayscoloring.common.Common;

public class WorkViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    private ImageOnClick imageOnClick;

    public void setImageOnClick(ImageOnClick imageOnClick) {
        this.imageOnClick = imageOnClick;
    }

    public WorkViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.image);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageOnClick.onClick(getAbsoluteAdapterPosition());

            }
        });

        itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);
            }


        });

    }
}
