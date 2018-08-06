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

public class CatalogBooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data

    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mBookCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_books);

        // Setting FAB to open EditorActivity
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogBooksActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Setting FAB to delete all books
        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        // Find the ListView which will be populated with the book data
        ListView booksListView = (ListView) findViewById(R.id.list_view_book);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        booksListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        mBookCursorAdapter = new BookCursorAdapter(this, null, BookContract.BookDatabaseTitles.CONTENT_URI,
                getContentResolver(), this);
        // Attach cursor adapter to the ListView
        booksListView.setAdapter(mBookCursorAdapter);

        // Prepare the loader. Either re-connect with an existing one or start a new one.
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

        // Insert a new row for book "Zawód wiedźma" into the provider using the ContentResolver.
        // Use the {@link BookContract#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        // Receive the new content URI that will allow us to access data of book "Zawód wiedźma" in the future.
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
                insertBook();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg_all_books);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
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
     * Perform the deletion of the book in the database.
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

}