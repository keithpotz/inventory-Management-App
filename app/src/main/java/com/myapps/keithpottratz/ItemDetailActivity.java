package com.myapps.keithpottratz;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.button.MaterialButton;

public class ItemDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM_ID   = "EXTRA_ITEM_ID";
    public static final String EXTRA_ITEM_NAME = "EXTRA_ITEM_NAME";
    public static final String EXTRA_ITEM_DESC = "EXTRA_ITEM_DESC";
    public static final String EXTRA_ITEM_QTY  = "EXTRA_ITEM_QTY";

    private InventoryDao dao;
    private InventoryItem item;

    private TextView tvName, tvQty, tvDesc, tvCurrentQty;
    private MaterialButton btnInc, btnDec, btnSave, btnDelete;

    private int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_detail);

        // Room setup
        AppDatabase db = AppDatabase.getInstance(this);
        dao = db.inventoryDao();

        long id = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (id <= 0){
            Toast.makeText(this, "Invalid item id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        item = dao.getById(id);
        if(item == null){
            Toast.makeText(this,"Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        quantity = item.getQuantity();

        //  Bind views
        tvName       = findViewById(R.id.detailName);
        tvQty        = findViewById(R.id.detailQty);
        tvDesc       = findViewById(R.id.detailDesc);
        tvCurrentQty = findViewById(R.id.currentQty);
        btnInc       = findViewById(R.id.increaseQty);
        btnDec       = findViewById(R.id.decreaseQty);
        btnSave      = findViewById(R.id.updateItemButton);
        btnDelete    = findViewById(R.id.deleteItemButton);

        // Populate labels
        tvName.setText(item.getName());
        tvDesc.setText(item.getDescription());
        refreshQtyViews();

        // Back label
        TextView back = findViewById(R.id.backLabel);
        back.setOnClickListener(v -> finish());

        // Quantity controls
        btnInc.setOnClickListener(v -> {
            quantity++;
            refreshQtyViews();
        });

        btnDec.setOnClickListener(v -> {
            if (quantity > 0) quantity--;
            refreshQtyViews();
        });

        // Save
        btnSave.setOnClickListener(v -> {
            item.setQuantity(quantity);
            int changed = dao.update(item);
            if(changed >0){
                setResult(RESULT_OK);
                finish();
            }else {
                Toast.makeText(this,"Nothing to Update", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete
        btnDelete.setOnClickListener(v -> {
           int removed = dao.delete(item);
           if (removed > 0){
               setResult(RESULT_OK);
               finish();
           }else{
               Toast.makeText(this,"Delete failed", Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void refreshQtyViews() {
        tvQty.setText("Qty: " + quantity);
        tvCurrentQty.setText(String.valueOf(quantity));
    }
}
