package com.example.cchiv.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.cchiv.inventoryapp.data.ItemContract.ItemEntry;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    ItemCursorAdapter itemCursorAdapter;
    private final static int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.take_to_action);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                startActivity(intent);
            }
        });

        ListView list_item = (ListView) findViewById(R.id.list_item);
        itemCursorAdapter = new ItemCursorAdapter(this, null);
        list_item.setAdapter(itemCursorAdapter);

        View emptyView = findViewById(R.id.empty_view);
        list_item.setEmptyView(emptyView);

        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                intent.setData(Uri.parse("content://com.example.android.items/items/" + id));
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_insert_dummy : {
                Drawable drawable = getDrawable(R.drawable.ic_box);
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                byte[] bitmapdata = outputStream.toByteArray();

                ContentValues contentValues = new ContentValues();
                contentValues.put(ItemEntry.COL_ITEM_IMAGE, bitmapdata);
                contentValues.put(ItemEntry.COL_ITEM_NAME, "Screwdriver");
                contentValues.put(ItemEntry.COL_ITEM_QUANTITY, "69");
                contentValues.put(ItemEntry.COL_ITEM_PRICE, "69");
                contentValues.put(ItemEntry.COL_ITEM_SUPPLIER, "Screwdrivers corp.");
                getContentResolver().insert(Uri.parse("content://com.example.android.items/items"), contentValues);
                break;
            }
            case R.id.menu_empty_inventory : {
                showDeleteConfirmationDialog();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COL_ITEM_IMAGE,
                ItemEntry.COL_ITEM_NAME,
                ItemEntry.COL_ITEM_QUANTITY,
                ItemEntry.COL_ITEM_PRICE,
                ItemEntry.COL_ITEM_SUPPLIER
        };

        return new CursorLoader(this, Uri.parse("content://com.example.android.items/items"), projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        itemCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemCursorAdapter.swapCursor(null);
    }

    private void deleteItems() {
        getContentResolver().delete(Uri.parse("content://com.example.android.items/items"), null, null);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.main_delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItems();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
