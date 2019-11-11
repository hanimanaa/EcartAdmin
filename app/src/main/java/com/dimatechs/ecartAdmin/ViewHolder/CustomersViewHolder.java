package com.dimatechs.ecartAdmin.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dimatechs.ecartAdmin.Interface.ItemClickListner;
import com.dimatechs.ecartAdmin.R;

public class CustomersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtcustomer_bisiness,txtcustomer_city,txtcustomer_name,txtcustomer_phone;
    public ItemClickListner itemClickListner;

    public CustomersViewHolder(View itemView)
    {
        super(itemView);

        txtcustomer_bisiness = (TextView) itemView.findViewById(R.id.customer_bisiness);
        txtcustomer_city = (TextView) itemView.findViewById(R.id.customer_city);
        txtcustomer_name= (TextView) itemView.findViewById(R.id.customer_name);
        txtcustomer_phone= (TextView) itemView.findViewById(R.id.customer_phone);

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
