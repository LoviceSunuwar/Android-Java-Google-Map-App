package com.android.productlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.productlist.Adapters.productAdapter;
import com.android.productlist.Database.Product;
import com.android.productlist.Database.ProductDatabase;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity {

    SearchView sv_provprod;
    RecyclerView rv_provprod;
    ConstraintLayout cl_pviewparent;

    List<String> product_name = new ArrayList<String>();
    List<Double> product_price = new ArrayList<Double>();
    List<Integer> product_uid = new ArrayList<Integer>();

    ProductDatabase pdtdb;
    productAdapter productAda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        String provider_name = getIntent().getStringExtra("provider_name");
        getSupportActionBar().setTitle(provider_name.toUpperCase()); // setting activity's appbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setting home button to appbar

        //Initializing all Views and ViewGroups
        sv_provprod = findViewById(R.id.sv_provprod);
        rv_provprod = findViewById(R.id.rv_provprod);
        cl_pviewparent = findViewById(R.id.cl_pviewparent);

        // initializing room database
        pdtdb = Room.databaseBuilder(getApplicationContext(), ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        if(product_uid.size()>0) {

            // creating layout manager for recyclerview
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            // initializing Product Adapter
            productAda = new productAdapter(product_name, product_price, product_uid);

            // setting Adapter to recyclerview
            rv_provprod.setAdapter(productAda);

            // setting layout manager to recyclerview
            rv_provprod.setLayoutManager(layoutManager);

            // setting search functionality to Product recyclerview
            sv_provprod.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    productAda.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    productAda.getFilter().filter(newText);
                    return false;
                }
            });

            //deleting product on swipe
            ItemTouchHelper.SimpleCallback itscproduct = new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    TextView tv_title = viewHolder.itemView.findViewById(R.id.tv_title);
                    TextView tv_subtitle = viewHolder.itemView.findViewById(R.id.tv_subtitle);
                    TextView tv_prodid = viewHolder.itemView.findViewById(R.id.tv_prodid);
                    int produid = Integer.parseInt(tv_prodid.getText().toString());
                    String prodname = tv_title.getText().toString();
                    Double prodprice = Double.valueOf(tv_subtitle.getText().toString().replace("Price : $",""));

                    Product product = pdtdb.productDao().loadAllByProductids(produid);

                    pdtdb.productDao().deleteproductbyuid(produid);

                    product_name.remove(prodname);
                    product_price.remove(prodprice);
                    product_uid.remove(Integer.valueOf(produid));

                    final int index = viewHolder.getAdapterPosition();

                    productAda.notifyItemRemoved(index);

                    Snackbar.make(cl_pviewparent,"Product Deleted: "+prodname,5000).setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pdtdb.productDao().insertProduct(product);
                            product_name.add(index,prodname);
                            product_price.add(index,prodprice);
                            product_uid.add(index,produid);

                            productAda.notifyItemInserted(index);

                        }
                    }).show();

                }
            };

            ItemTouchHelper ithprod = new ItemTouchHelper(itscproduct);
            ithprod.attachToRecyclerView(rv_provprod); // attaching itemtouchhelper to recyclerview



        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}