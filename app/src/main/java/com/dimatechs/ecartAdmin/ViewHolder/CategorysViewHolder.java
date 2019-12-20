package com.dimatechs.ecartAdmin.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dimatechs.ecartAdmin.Interface.ItemClickListner;
import com.dimatechs.ecartAdmin.R;

public class CategorysViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtcatName;
    public ItemClickListner itemClickListner;

    public CategorysViewHolder(View itemView)
    {
        super(itemView);

        txtcatName = (TextView) itemView.findViewById(R.id.category_name);

    }

    @Override
    public void onClick(View view)
    {
        itemClickListner.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListner(ItemClickListner listner )
    {
        this.itemClickListner=listner;
    }



}
