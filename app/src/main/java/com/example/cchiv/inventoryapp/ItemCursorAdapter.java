package com.example.cchiv.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.inventoryapp.data.ItemContract.ItemEntry;

import java.io.ByteArrayInputStream;

/**
 * Created by Cchiv on 25/07/2017.
 */

public class ItemCursorAdapter extends CursorAdapter{

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        int imageId = cursor.getColumnIndexOrThrow(ItemEntry.COL_ITEM_IMAGE);
        int nameId = cursor.getColumnIndexOrThrow(ItemEntry.COL_ITEM_NAME);
        int quantityId = cursor.getColumnIndexOrThrow(ItemEntry.COL_ITEM_QUANTITY);
        int priceId = cursor.getColumnIndexOrThrow(ItemEntry.COL_ITEM_PRICE);

        TextView textView;
        textView = (TextView) view.findViewById(R.id.product_name);
        textView.setText(cursor.getString(nameId));

        textView = (TextView) view.findViewById(R.id.product_quantity);
        textView.setText("Qty: " + cursor.getString(quantityId) + "pc.");
        textView.setTag(cursor.getString(quantityId));

        textView = (TextView) view.findViewById(R.id.product_price);
        textView.setText("Price: " + cursor.getString(priceId) + "$");

        ImageView imageView = (ImageView) view.findViewById(R.id.product_image);
        byte[] byteImage = cursor.getBlob(imageId);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteImage);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        imageView.setImageBitmap(bitmap);

        Button button = (Button) view.findViewById(R.id.product_sale);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) view.findViewById(R.id.product_quantity);

                int quantity = Integer.valueOf(String.valueOf(textView.getTag()));
                String cursorPosition = cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry._ID));

                if(quantity > 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ItemEntry.COL_ITEM_QUANTITY, String.valueOf(quantity-1));
                    context.getContentResolver().update(Uri.parse("content://com.example.android.items/items/" + cursorPosition), contentValues, null, null);
                }
            }
        });
    }
}
