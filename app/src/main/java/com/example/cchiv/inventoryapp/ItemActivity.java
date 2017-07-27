package com.example.cchiv.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cchiv.inventoryapp.data.ItemContract.ItemEntry;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ItemActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>{

    private Uri uri;

    private ImageView imageViewImage;
    private EditText editTextName;
    private TextView textViewQuantity;
    private EditText editTextPrice;
    private EditText editTextSupplier;

    private boolean editMode;
    private boolean addedImage;
    private final static int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        imageViewImage = (ImageView) findViewById(R.id.item_image);
        editTextName = (EditText) findViewById(R.id.item_name);
        textViewQuantity = (TextView) findViewById(R.id.item_quantity);
        editTextPrice = (EditText) findViewById(R.id.item_price);
        editTextSupplier = (EditText) findViewById(R.id.item_supplier);

        Intent intent = getIntent();
        uri = intent.getData();

        addedImage = false;

        if(uri != null) {
            editMode = true;
            getLoaderManager().initLoader(1, null, this);
        } else {
            invalidateOptionsMenu();
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
                if(currentQuantity > 0)
                    textView.setText(String.valueOf(currentQuantity-1));
            }
        });
        button = (Button) findViewById(R.id.button_order_item);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode) {
                    TextView supplier = (TextView) findViewById(R.id.item_supplier);
                    TextView itemName = (TextView) findViewById(R.id.item_name);
                    Intent intent1 = new Intent(Intent.ACTION_SEND);
                    intent1.setType("plain/text");
                    intent1.putExtra(Intent.EXTRA_EMAIL, new String[] { String.valueOf(supplier.getText()) });
                    intent1.putExtra(Intent.EXTRA_SUBJECT, "ORDER");
                    intent1.putExtra(Intent.EXTRA_TEXT, "I want to order" + itemName.getText());
                    startActivity(intent1);
                }

            }
        });
        button = (Button) findViewById(R.id.button_upload_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    float scaleWidth = ((float) size.x) / width;
                    float scaleHeight = ((float) size.y) / height / 3;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    bitmap = Bitmap.createBitmap(
                            bitmap, 0, 0, width, height, matrix, false);

                    ImageView imageView = (ImageView) findViewById(R.id.item_image);
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag(bitmap);

                    addedImage = true;
                } catch (FileNotFoundException io) {
                    Toast.makeText(this, "Failed to upload image, try again", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(!editMode) {
            MenuItem menuItem = menu.findItem(R.id.menu_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_item : {

                ContentValues contentValues = new ContentValues();

                Bitmap bitmap = ((BitmapDrawable) imageViewImage.getDrawable()).getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                byte[] image = outputStream.toByteArray();
                contentValues.put(ItemEntry.COL_ITEM_IMAGE, image);

                if(TextUtils.isEmpty(editTextName.getEditableText())) {
                    Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
                    return false;
                }
                contentValues.put(ItemEntry.COL_ITEM_NAME, editTextName.getEditableText().toString());
                contentValues.put(ItemEntry.COL_ITEM_QUANTITY, textViewQuantity.getText().toString());
                if(TextUtils.isEmpty(editTextPrice.getEditableText())) {
                    Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_LONG).show();
                    return false;
                }
                contentValues.put(ItemEntry.COL_ITEM_PRICE, editTextPrice.getEditableText().toString());
                if(TextUtils.isEmpty(editTextSupplier.getEditableText())) {
                    Toast.makeText(this, "Please enter a valid supplier", Toast.LENGTH_LONG).show();
                    return false;
                }
                contentValues.put(ItemEntry.COL_ITEM_SUPPLIER, editTextSupplier.getEditableText().toString());
                if(editMode)
                    getContentResolver().update(uri, contentValues, null, null);
                else
                    getContentResolver().insert(uri, contentValues);
                finish();
                break;
            }
            case R.id.menu_delete_item : {
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
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            byte[] byteImage = data.getBlob(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
            imageViewImage.setImageBitmap(bitmap);

            editTextName.setText(data.getString(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_NAME)));
            textViewQuantity.setText(data.getString(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_QUANTITY)));
            editTextPrice.setText(data.getString(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_PRICE)));
            editTextSupplier.setText(data.getString(data.getColumnIndexOrThrow(ItemEntry.COL_ITEM_SUPPLIER)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        imageViewImage.setImageBitmap(null);
        editTextName.setText("");
        textViewQuantity.setText("");
        editTextPrice.setText("");
        editTextSupplier.setText("");
    }

    private void deleteItem() {
        getContentResolver().delete(uri, null, null);
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
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


