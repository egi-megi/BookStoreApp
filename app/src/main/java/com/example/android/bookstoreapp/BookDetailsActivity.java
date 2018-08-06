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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;

/**
 * Created by egi-megi on 28.07.18.
 */

public class BookDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mSupplierNameTextView;
    private TextView mSupplierPhoneNumberTextView;


    private boolean mBookHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        // Setting FAB to open EditorActivity
        FloatingActionButton editBook = (FloatingActionButton) findViewById(R.id.fab_edit);
        editBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this, EditorActivity.class);
                Uri currentBookUri = mCurrentBookUri;
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        // Setting FAB to delete all books
        FloatingActionButton deleteBook = (FloatingActionButton) findViewById(R.id.fab_delete);
        deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });


        // Finding all relevant views that we will need to read user input from
        mTitleTextView = (TextView) findViewById(R.id.text_view_book_title);
        mAuthorTextView = (TextView) findViewById(R.id.text_view_book_author);
        mPriceTextView = (TextView) findViewById(R.id.text_view_book_price);
        mQuantityTextView = (TextView) findViewById(R.id.text_view_book_quantity);
        mSupplierNameTextView = (TextView) findViewById(R.id.text_view_supplier_name);
        mSupplierPhoneNumberTextView = (TextView) findViewById(R.id.text_view_supplier_phone_number);

        // A button that subtracts 1 from the quantity of books in the magazine
        Button decreaseQuantity = (Button) findViewById(R.id.button_decrease);
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(mQuantityTextView.getText().toString().trim());
                if (quantity == 0) {
                    quantity = 0;
                } else {
                    quantity = quantity - 1;

                }
                mQuantityTextView.setText(Integer.toString(quantity));
                updateBook();
            }
        });

        // A button that adds 1 to the quantity of books in the magazine
        Button increaseQuantity = (Button) findViewById(R.id.button_increase);
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(mQuantityTextView.getText().toString().trim());
                quantity = quantity + 1;
                mQuantityTextView.setText(Integer.toString(quantity));
                updateBook();
            }
        });

    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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


    private void updateBook() {

        //here is updating only the quantity
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantityInteger = Integer.parseInt(quantityString);
        ContentValues values = new ContentValues();
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY, quantityInteger);
        int upInf = getContentResolver().update(mCurrentBookUri, values, null, null);
        if (upInf != 1) {
            Log.e("UPDATE", "Wrong update number: " + upInf);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogBooksActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(BookDetailsActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(BookDetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
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

        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int mRowsDeleted = getContentResolver().delete(
                    mCurrentBookUri,   // the user dictionary content URI
                    null,                    // the column to select on
                    null                      // the value to compare to
            );

            if (mRowsDeleted == 0) {
                Toast.makeText(this, "The book is not deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "The book is deleted", Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookContract.BookDatabaseTitles._ID,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME,
                BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,                // Query the content URI for the current book
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection arguments
                null);                // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String bookTitle = cursor.getString(titleColumnIndex);
            String bookAuthor = cursor.getString(authorColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleTextView.setText(bookTitle);
            mAuthorTextView.setText(bookAuthor);
            mPriceTextView.setText(Float.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneNumberTextView.setText(supplierPhone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleTextView.setText("");
        mAuthorTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierNameTextView.setText("");
        mSupplierPhoneNumberTextView.setText("");
    }

}