package com.dimatechs.ecartAdmin.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dimatechs.ecartAdmin.Interface.ItemClickListner;
import com.dimatechs.ecartAdmin.Model.Cart;
import com.dimatechs.ecartAdmin.R;

import java.util.List;

public class PdfCreateAdapter extends RecyclerView.Adapter<PdfCreateAdapter.CartViewHolder> {

    private List<Cart> pdfModels;

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_items_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {

       Cart model = pdfModels.get(position);
        if (model != null) {
            // for pdf
            holder.txtProductName.setText(model.getName());
            holder.txtProductPrice.setText( " מחיר : " + model.getPrice()+ " ש\"ח ");
            holder.txtProductQuantity.setText(" כמות : " + model.getQuantity());
        }
    }

    @Override
    public int getItemCount() {
        return pdfModels.size();
    }

    /**
     * This is set from PDFCreateByXML class
     * This is my own model. This model have to set data from api
     *
     * @param pdfModels
     */
    public void setListData(List<Cart> pdfModels) {
        this.pdfModels = pdfModels;
        notifyDataSetChanged();
    }



    public static class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView txtProductName,txtProductPrice,txtProductQuantity;
        public ItemClickListner itemClickListner;

        public CartViewHolder(View itemView)
        {
            super(itemView);

            txtProductName = (TextView) itemView.findViewById(R.id.cart_product_name);
            txtProductPrice = (TextView) itemView.findViewById(R.id.cart_product_price);
            txtProductQuantity= (TextView) itemView.findViewById(R.id.cart_product_quantity);

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

}