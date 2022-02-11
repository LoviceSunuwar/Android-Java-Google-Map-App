package com.android.productlist.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.productlist.R;
import com.android.productlist.ViewEntryActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class productAdapter extends RecyclerView.Adapter<productAdapter.viewholder> implements Filterable {


    private List<String> products;
    private List<Double> price;
    private List<String> filteredproductList;
    private List<Integer> uid;

    public productAdapter(List<String> products, List<Double> price, List<Integer> uid)
    {
        this.products = products;
        this.price = price;
        this.filteredproductList = products;
        this.uid = uid;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new viewholder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        if (uid.size() > 0) {
            holder.tv_title.setText(filteredproductList.get(position));
            holder.tv_subtitle.setText("Price : $"+price.get(position).toString());
            holder.tv_prodid.setText(String.valueOf(uid.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return filteredproductList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charseq = constraint.toString();
                if(charseq.isEmpty())
                {
                    filteredproductList = products;
                }
                else {
                    List<String> filteredList = new ArrayList<String>();
                    for (String productname : products) {
                        if (productname.toLowerCase().contains(charseq.toLowerCase())) {
                            filteredList.add(productname);
                        }
                        filteredproductList = filteredList;
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredproductList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredproductList = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class viewholder extends RecyclerView.ViewHolder
    {
        TextView tv_title,tv_subtitle,tv_prodid;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subtitle = itemView.findViewById(R.id.tv_subtitle);
            tv_prodid = itemView.findViewById(R.id.tv_prodid);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ViewEntryActivity.class);
                    intent.putExtra("product_name",tv_title.getText().toString());
                    intent.putExtra("uid",Integer.parseInt(tv_prodid.getText().toString()));
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
