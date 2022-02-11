package com.android.productlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.productlist.Database.Product;
import com.android.productlist.Database.ProductDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewEntryActivity extends AppCompatActivity {

    TextView tv_prodedit,tv_productid,tv_productname,tv_productdesc,tv_productprice,tv_provloc;

    ProductDatabase pdtdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        String product_name = getIntent().getStringExtra("product_name");
        int uid = getIntent().getIntExtra("uid",0);
        getSupportActionBar().setTitle(product_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_prodedit = findViewById(R.id.tv_prodedit);
        tv_productid = findViewById(R.id.tv_productid);
        tv_productname = findViewById(R.id.tv_productname);
        tv_productdesc = findViewById(R.id.tv_productdesc);
        tv_productprice = findViewById(R.id.tv_productprice);
        tv_provloc = findViewById(R.id.tv_provloc);

        pdtdb = Room.databaseBuilder(getApplicationContext(),ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        Product allproducts = pdtdb.productDao().loadAllByProductids(uid);

        tv_productname.setText(String.valueOf(allproducts.getProductname()));
        tv_productdesc.setText(String.valueOf(allproducts.getProductdescription()));
        tv_productprice.setText(String.valueOf(allproducts.getProductprice()));

        tv_provloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEntryActivity.this,MapsActivity.class);
                intent.putExtra("movable","false");
                intent.putExtra("product_name",product_name);

                System.out.println("latitude"+ allproducts.getLat());
                intent.putExtra("latitude",allproducts.getLat());
                intent.putExtra("longitude",allproducts.getLng());
                startActivity(intent);
            }
        });

        tv_prodedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEntryActivity.this,ProductEntryActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("product_name",product_name);
                startActivity(intent);
            }
        });

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