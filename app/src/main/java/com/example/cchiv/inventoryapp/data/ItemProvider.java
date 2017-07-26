package com.example.cchiv.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.cchiv.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by Cchiv on 25/07/2017.
 */

public class ItemProvider extends ContentProvider{

    private static final int MANY = 1;
    private static final int SINGLE = 0;

    private ItemDbHelper itemDbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI("com.example.android.items", "items", MANY);
        uriMatcher.addURI("com.example.android.items", "items/#", SINGLE);
    }

    @Override
    public boolean onCreate() {
        itemDbHelper = new ItemDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String
            selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = itemDbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case SINGLE : {
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                Cursor cursor = db.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            case MANY : {
                Cursor cursor = db.query(ItemEntry.TABLE_NAME, projection, null, null, null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = itemDbHelper.getWritableDatabase();

        String name = values.getAsString(ItemEntry.COL_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }

        int quantity = values.getAsInteger(ItemEntry.COL_ITEM_QUANTITY);
        if (quantity < 0) {
            throw new IllegalArgumentException("Item has to have a positive nb. of quantity");
        }

        int price = values.getAsInteger(ItemEntry.COL_ITEM_PRICE);
        if (price < 0) {
            throw new IllegalArgumentException("Item has to have a real price");
        }

        long idRow;

        switch (uriMatcher.match(uri)) {
            case MANY : {
                idRow = db.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);
                if(idRow != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return ContentUris.withAppendedId(uri, idRow);
            }
            default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]
            selectionArgs) {
        SQLiteDatabase db = itemDbHelper.getWritableDatabase();

        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case SINGLE : {
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted =  db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);

                if(rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return rowsDeleted;
            }
            case MANY : {
                rowsDeleted = db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return rowsDeleted;
            }
            default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = itemDbHelper.getWritableDatabase();

        int rowsUpdated;


        if(values.containsKey(ItemEntry.COL_ITEM_NAME)) {
            String name = values.getAsString(ItemEntry.COL_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if(values.containsKey(ItemEntry.COL_ITEM_QUANTITY)) {
            int quantity = values.getAsInteger(ItemEntry.COL_ITEM_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Item has to have a positive nb. of quantity");
            }
        }

        if(values.containsKey(ItemEntry.COL_ITEM_PRICE)) {
            int price = values.getAsInteger(ItemEntry.COL_ITEM_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Item has to have a real price");
            }
        }

        switch (uriMatcher.match(uri)) {
            case SINGLE : {
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                getContext().getContentResolver().notifyChange(uri, null);
                rowsUpdated = db.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
                if(rowsUpdated != 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return rowsUpdated;
            }
            default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }
}
