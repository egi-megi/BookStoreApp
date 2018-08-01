package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;

public class CatalogBooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks <Cursor>{

    // This is the Adapter being used to display the list's data

    private static final int BOOK_LOADER =0;

    BookCursorAdapter mBookCursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_books);

        // Setup FAB to open EditorActivity
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogBooksActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });


        // Find the ListView which will be populated with the pet data
        ListView booksListView = (ListView) findViewById(R.id.list_view_book);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        booksListView.setEmptyView(emptyView);



        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        mBookCursorAdapter = new BookCursorAdapter(this, null, BookContract.BookDatabaseTitles.CONTENT_URI,
                getContentResolver(),this);
// Attach cursor adapter to the ListView
        booksListView.setAdapter(mBookCursorAdapter);

       /* booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogBooksActivity.this, EditorActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookDatabaseTitles.CONTENT_URI, id);

                intent.setData(currentBookUri);

                startActivity(intent);
            }
        });*/

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(BOOK_LOADER, null, this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }




    private void insertBook() {

        ContentValues values = new ContentValues();

        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE, "Zawód wiedźma");
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR, "Gromyko Olga");
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE, 8.0);
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY, 10);
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME, "Papierowy Księżyc");
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE, "+48 222 222 222");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(BookContract.BookDatabaseTitles.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertBook();
//                insertBook(9);
//                insertBook(10);
                return true;
//            // Respond to a click on the "Delete all entries" menu option
//            case R.id.action_delete_all_entries:
//                showDeleteConfirmationDialog();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg_all_books);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteBook() {

        int mRowsDeleted = getContentResolver().delete(
                BookContract.BookDatabaseTitles.CONTENT_URI,   // the user dictionary content URI
                null,                    // the column to select on
                null                      // the value to compare to
        );

        if (mRowsDeleted == 0) {
            Toast.makeText(this, "All the books are not deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "All the books are deleted", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookContract.BookDatabaseTitles._ID,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY};

        return new CursorLoader(
                this,
                BookContract.BookDatabaseTitles.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookCursorAdapter.swapCursor(null);

    }

//    private BookDbHelper mDbHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_catalog_books);
//
//        mDbHelper = new BookDbHelper(this);
//
//        displayDatabaseInfo();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        displayDatabaseInfo();
//    }
//
//
//    /**
//     * Temporary helper method to display information in the onscreen TextView about the state of
//     * the pets database.
//     */
//    private void displayDatabaseInfo() {
//
//        // Create and/or open a database to read from it
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        //Select all columns
//        String[] projection = {
//                BookContract.BookDatabaseTitles._ID,
//                BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE,
//                BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR,
//                BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE,
//                BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY,
//                BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME,
//                BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE};
//
//        Cursor cursor = db.query(
//                BookContract.BookDatabaseTitles.TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null);
//
//        TextView displayView = (TextView) findViewById(R.id.text_view_book);
//        try {
//            // Create a header in the Text View that looks like this:
//            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
//            // Display titles fo columns
//            displayView.append(
//                    BookContract.BookDatabaseTitles._ID + " - " +
//                            BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE + ", " +
//                            BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR + ", " +
//                            BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE + ", " +
//                            BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY + ", " +
//                            BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME + ", " +
//                            BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE + "\n");
//
//            // Figure out the index of each column
//            int idColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles._ID);
//            int titleBookColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE);
//            int authorBookColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR);
//            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE);
//            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY);
//            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME);
//            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE);
//
//            // Iterate through all the returned rows in the cursor
//            while (cursor.moveToNext()) {
//                // Use that index to extract the String or Int value of the word
//                // at the current row the cursor is on.
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentBookTitle = cursor.getString(titleBookColumnIndex);
//                String currentBookAuthor = cursor.getString(authorBookColumnIndex);
//                String currentPrice = cursor.getString(priceColumnIndex);
//                String currentQuantity = cursor.getString(quantityColumnIndex);
//                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
//                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
//                // Display the values from each column of the current row in the cursor in the TextView
//                displayView.append(("\n" +
//                        currentID + " - " +
//                        currentBookTitle + "," +
//                        currentBookAuthor + "," +
//                        currentPrice + "," +
//                        currentQuantity + "," +
//                        currentSupplierName + "," +
//                        currentSupplierPhone));
//            }
//        } finally {
//            // Always close the cursor when you're done reading from it.
//            cursor.close();
//        }
//    }

    //Method insertBook should be in this Activity if in the future we will make action which calls this method

}