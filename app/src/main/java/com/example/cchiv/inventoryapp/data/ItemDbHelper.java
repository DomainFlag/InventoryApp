package com.example.cchiv.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cchiv.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by Cchiv on 25/07/2017.
 */

public class ItemDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "items.db";

    Context context;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntry.COL_ITEM_IMAGE + " BLOB, " +
                ItemEntry.COL_ITEM_NAME + " TEXT NOT NULL, " +
                ItemEntry.COL_ITEM_QUANTITY + " INTEGER, " +
                ItemEntry.COL_ITEM_PRICE + " INTEGER NOT NULL, " +
                ItemEntry.COL_ITEM_SUPPLIER + " TEXT);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
