package com.example.cchiv.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Cchiv on 25/07/2017.
 */

public class ItemContract {

    public ItemContract() {};

    public class ItemEntry implements BaseColumns {

        public static final String TABLE_NAME = "items";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_ITEM_IMAGE = "image";
        public static final String COL_ITEM_NAME = "name";
        public static final String COL_ITEM_QUANTITY = "quantity";
        public static final String COL_ITEM_PRICE = "price";
        public static final String COL_ITEM_SUPPLIER = "supplier";
    }
}
