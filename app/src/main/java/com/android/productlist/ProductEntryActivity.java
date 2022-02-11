package com.android.productlist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.productlist.Database.Product;
import com.android.productlist.Database.ProductDatabase;

import java.util.List;
import java.util.Locale;


public class ProductEntryActivity extends AppCompatActivity {

    EditText et_productid, et_productname, et_productdesc, et_productprice;
    TextView tv_provloc, tv_prodsave, tv_prodcancel;

    Double latitude = 1000.0;
    Double longitude = 1000.0;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        latitude = data.getDoubleExtra("latitude",0);
                        longitude = data.getDoubleExtra("longitude",0);

                        System.out.println(latitude+"/"+longitude);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_entry);

        getSupportActionBar().setTitle("Product Entry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_productid = findViewById(R.id.et_productid);
        et_productname = findViewById(R.id.et_productname);
        et_productdesc = findViewById(R.id.et_productdesc);
        et_productprice = findViewById(R.id.et_productprice);

        tv_provloc = findViewById(R.id.tv_provloc);

        tv_prodsave = findViewById(R.id.tv_prodsave);
        tv_prodcancel = findViewById(R.id.tv_prodcancel);

        ProductDatabase pdtdb = Room.databaseBuilder(getApplicationContext(),ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        String product_name = getIntent().getStringExtra("product_name");
        int prodid = getIntent().getIntExtra("uid",0);

        if(prodid!=0)
        {
            Product allproducts = pdtdb.productDao().loadAllByProductids(prodid);
            et_productid.setText(String.valueOf(prodid));
            et_productid.setFocusable(false);
            et_productname.setText(String.valueOf(product_name));
            et_productdesc.setText(String.valueOf(allproducts.getProductdescription()));
            et_productprice.setText(String.valueOf(allproducts.getProductprice()));

            latitude = allproducts.getLat();
            longitude = allproducts.getLng();


            System.out.println(latitude);

            getSupportActionBar().setTitle("Edit entry : "+product_name);
        }

        tv_provloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(ProductEntryActivity.this, MapsActivity.class);
                    intent.putExtra("movable","true");
                    intent.putExtra("uid",prodid);

                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
                    someActivityResultLauncher.launch(intent);


            }
        });

        tv_prodsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> uid = pdtdb.productDao().getallids();
                System.out.println(uid);
                if (et_productid.getText().toString().equals("")) {
                    et_productid.setError("Please Enter a Product Id");
                } else if (et_productname.getText().toString().equals("")) {
                    et_productname.setError("Please Enter a Product Name");
                } else if (et_productdesc.getText().toString().equals("")) {
                    et_productdesc.setError("Please Enter a Product Descreption");
                } else if (et_productprice.getText().toString().equals("")) {
                    et_productprice.setError("Please Enter a Product Price");
                } else if (latitude == 1000.0 || longitude == 1000.0) {
                    Toast.makeText(getApplicationContext(), "Please select location", Toast.LENGTH_LONG).show();
                }
                else if(prodid == 0 && uid.contains(Integer.parseInt(et_productid.getText().toString())))
                {
                    et_productid.setError("Product Id already exists, please enter a new id");
                    AlertDialog.Builder alert = new AlertDialog.Builder(ProductEntryActivity.this);
                    alert.setMessage("Product Id already exists, please enter a new id");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.create().show();
                }
                else {

                    Product product = new Product(Integer.parseInt(et_productid.getText().toString()),
                            et_productname.getText().toString(), et_productdesc.getText().toString(),
                            Double.parseDouble(et_productprice.getText().toString()),
                            latitude, longitude);

                    AlertDialog.Builder alert = new AlertDialog.Builder(ProductEntryActivity.this);

                    if(prodid!=0)
                    {
                        pdtdb.productDao().updateProduct(product);
                        alert.setMessage("Product updated in the list");
                    }
                    else {
                        pdtdb.productDao().insertProduct(product);
                        alert.setMessage("Product added to the list");
                    }
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            Intent intent = new Intent(ProductEntryActivity.this,ProductListActivity.class);
                            startActivity(intent);
                        }
                    });
                    alert.create().show();
                }
            }
        });

        tv_prodcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProductEntryActivity.this);
                alert.setTitle("Cancel ?");
                alert.setMessage("Do you want to cancel Entry ?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
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