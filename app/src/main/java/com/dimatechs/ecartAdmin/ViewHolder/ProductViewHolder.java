package com.dimatechs.ecartAdmin.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimatechs.ecartAdmin.Interface.ItemClickListner;
import com.dimatechs.ecartAdmin.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName,txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductViewHolder(View itemView)
    {
        super(itemView);

        txtProductName = (TextView) itemView.findViewById(R.id.product_name_item);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description_item);
        txtProductPrice= (TextView) itemView.findViewById(R.id.product_price_item);
        imageView = (ImageView) itemView.findViewById(R.id.product_image_item);

    }

    public void setItemClickListner(ItemClickListner listner )
    {
        this.listner=listner;
    }


    @Override
    public void onClick(View view)
    {
        listner.onClick(view,getAdapterPosition(),false);
    }
}
