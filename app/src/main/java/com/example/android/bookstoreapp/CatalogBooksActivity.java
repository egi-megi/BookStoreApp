package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract;
import com.example.android.bookstoreapp.data.BookDbHelper;

import java.lang.reflect.Method;

public class CatalogBooksActivity extends AppCompatActivity {

    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_books);

        mDbHelper = new BookDbHelper(this);

        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }


    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Select all columns
        String[] projection = {
                BookContract.BookDatabaseTitles._ID,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE};

        Cursor cursor = db.query(
                BookContract.BookDatabaseTitles.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = (TextView) findViewById(R.id.text_view_book);
        try {
            // Create a header in the Text View that looks like this:
            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            // Display titles fo columns
            displayView.append(
                    BookContract.BookDatabaseTitles._ID + " - " +
                            BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE + ", " +
                            BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR + ", " +
                            BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE + ", " +
                            BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY + ", " +
                            BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME + ", " +
                            BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles._ID);
            int titleBookColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE);
            int authorBookColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentBookTitle = cursor.getString(titleBookColumnIndex);
                String currentBookAuthor = cursor.getString(authorBookColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                String currentQuantity = cursor.getString(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" +
                        currentID + " - " +
                        currentBookTitle + "," +
                        currentBookAuthor + "," +
                        currentPrice + "," +
                        currentQuantity + "," +
                        currentSupplierName + "," +
                        currentSupplierPhone));
            }
        } finally {
            // Always close the cursor when you're done reading from it.
            cursor.close();
        }
    }

    //Method insertBook should be in this Activity if in the future we will make action which calls this method
    private void insertBook() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE, "Zawód wiedźma");
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR, "Gromyko Olga");
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE, 8);
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY, 10);
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME, "Papierowy Księżyc");
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE, "+48 222 222 222");


        long newRowId;
        newRowId = db.insert(
                BookContract.BookDatabaseTitles.TABLE_NAME,
                null,
                values);

        Log.v("CatalogBooksActivity", "New row ID " + newRowId);
    }

}