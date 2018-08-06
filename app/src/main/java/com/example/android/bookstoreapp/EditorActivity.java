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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;

/**
 * Created by egi-megi on 16.07.18.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    private EditText mTitleEditText;
    private EditText mAuthorEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;

    private boolean mBookHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Get intent from list of books from CatalogBooksActivity
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTitleEditText = (EditText) findViewById(R.id.edit_book_title);
        mAuthorEditText = (EditText) findViewById(R.id.edit_book_author);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);

        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        //Setting FAB to delete chosen books
        FloatingActionButton deleteBook = (FloatingActionButton) findViewById(R.id.fab_delete);
        deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        //Setting FAB to save (accept) chosen books
        FloatingActionButton saveBook = (FloatingActionButton) findViewById(R.id.fab_save);
        saveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertOrUpdateBook();
            }
        });

    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mBookHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

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


    private void insertOrUpdateBook() {

        String titleString = mTitleEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(priceString)) {
            priceString = "0";
            mPriceEditText.setText(priceString);
        }

        float priceFloat = Float.parseFloat(priceString);

        if (TextUtils.isEmpty(quantityString)) {
            quantityString = "0";
            mQuantityEditText.setText(quantityString);
        }

        int quantityInteger = Integer.parseInt(quantityString);

        if (TextUtils.isEmpty(authorString)) {
            mAuthorEditText.setText(R.string.unknown_author);
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            mSupplierNameEditText.setText(R.string.unknown_supplier_name);
        }

        if (TextUtils.isEmpty(supplierPhoneNumberString)) {
            mSupplierPhoneNumberEditText.setText(R.string.unknown_supplier_phone_number);
        }

        if (!TextUtils.isEmpty(titleString)) {
            // Create a ContentValues object where column names are the keys,
            // and book attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE, titleString);
            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR, authorString);
            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE, priceFloat);
            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY, quantityInteger);
            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneNumberString);


            // Insert a new book into the provider, returning the content URI for the new book.
            if (mCurrentBookUri == null) {
                Uri newUri = getContentResolver().insert(BookContract.BookDatabaseTitles.CONTENT_URI, values);

                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                            Toast.LENGTH_SHORT).show();
                    mCurrentBookUri = newUri;
                }

            } else {
                int upInf = getContentResolver().update(mCurrentBookUri, values, null, null);
                if (upInf != 1) {
                    Log.e("UPDATE", "Wrong update number: " + upInf);
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditorActivity.this, CatalogBooksActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        } else {
            showEmptyTitleConfirmationDialog();
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
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
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

    private void showEmptyTitleConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.empty_title_dialog_msg);
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                Intent intent = new Intent(EditorActivity.this, CatalogBooksActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
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
        // and go to CatalogBooksActivity
        Intent intent = new Intent(EditorActivity.this, CatalogBooksActivity.class);
        startActivity(intent);
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
                null,              // No selection arguments
                null);               // Default sort order
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
            mTitleEditText.setText(bookTitle);
            mAuthorEditText.setText(bookAuthor);
            mPriceEditText.setText(Float.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }

}
