package com.example.cchiv.inventoryapp;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cchiv.inventoryapp.data.ItemContract.ItemEntry;

public class ItemActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>{

    private Uri uri;

    private EditText editTextName;
    private TextView textViewQuantity;
    private EditText editTextPrice;
    private EditText editTextSupplier;

    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        editTextName = (EditText) findViewById(R.id.item_name);
        textViewQuantity = (TextView) findViewById(R.id.item_quantity);
        editTextPrice = (EditText) findViewById(R.id.item_price);
        editTextSupplier = (EditText) findViewById(R.id.item_supplier);

        Intent intent = getIntent();
        uri = intent.getData();

        if(uri != null) {
            editMode = true;
            getLoaderManager().initLoader(1, null, this);
        } else {
            editMode = false;
            uri = Uri.parse("content://com.example.android.items/items");
        }

        Button button;

        button = (Button) findViewById(R.id.item_inc_quantity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.item_quantity);
                int currentQuantity = Integer.valueOf(String.valueOf(textView.getText()));
                textView.setText(String.valueOf(currentQuantity+1));
            }
        });
        button = (Button) findViewById(R.id.item_dec_quantity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.item_quantity);
                int currentQuantity = Integer.valueOf(String.valueOf(textView.getText()));
                if(currentQuantity > 1)
                    textView.setText(String.valueOf(currentQuantity-1));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_item : {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ItemEntry.COL_ITEM_NAME, editTextName.getEditableText().toString());
                contentValues.put(ItemEntry.COL_ITEM_QUANTITY, textViewQuantity.getText().toString());
                contentValues.put(ItemEntry.COL_ITEM_PRICE, editTextPrice.getEditableText().toString());
                contentValues.put(ItemEntry.COL_ITEM_SUPPLIER, editTextSupplier.getEditableText().toString());
                if(editMode)
                    getContentResolver().update(uri, contentValues, null, null);
                else
                    getContentResolver().insert(uri, contentValues);
                finish();
                break;
            }
            case R.id.menu_delete_item : {
                getContentResolver().delete(uri, null, null);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COL_ITEM_NAME,
                ItemEntry.COL_ITEM_QUANTITY,
                ItemEntry.COL_ITEM_PRICE,
                ItemEntry.COL_ITEM_SUPPLIER
        };
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            editTextName.setText(data.getString(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_NAME)));
            textViewQuantity.setText(data.getString(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_QUANTITY)));
            editTextPrice.setText(data.getString(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_PRICE)));
            editTextSupplier.setText(data.getString(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_SUPPLIER)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editTextName.setText("");
        textViewQuantity.setText("");
        editTextPrice.setText("");
        editTextSupplier.setText("");
    }
}
