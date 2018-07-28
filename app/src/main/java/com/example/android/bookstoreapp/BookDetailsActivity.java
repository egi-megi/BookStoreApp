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

//        if (mCurrentBookUri == null) {
//            setTitle(getString(R.string.editor_activity_title_new_book));
//            // Invalidate the options menu, so the "Delete" menu option can be hidden.
//            // (It doesn't make sense to delete a pet that hasn't been created yet.)
//            invalidateOptionsMenu();
//        } else {
//            setTitle(getString(R.string.editor_activity_title_edit_book));
//
//
//            // Initialize a loader to read the pet data from the database
//            // and display the current values in the editor
//            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
//        }

        // Setup FAB to open EditorActivity
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

        FloatingActionButton deleteBook = (FloatingActionButton) findViewById(R.id.fab_delete);
        deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });


        // Find all relevant views that we will need to read user input from
        mTitleTextView = (TextView) findViewById(R.id.text_view_book_title);
        mAuthorTextView = (TextView) findViewById(R.id.text_view_book_author);
        mPriceTextView = (TextView) findViewById(R.id.text_view_book_price);
        mQuantityTextView = (TextView) findViewById(R.id.text_view_book_quantity);
        mSupplierNameTextView = (TextView) findViewById(R.id.text_view_supplier_name);
        mSupplierPhoneNumberTextView = (TextView) findViewById(R.id.text_view_supplier_phone_number);


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

//        mSupplierPhoneNumberTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:" + Integer.parseInt(mSupplierPhoneNumberTextView.getText().toString().trim())));
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
//            }
//        });


//        mTitleTextView.setOnTouchListener(mTouchListener);
//        mAuthorTextView.setOnTouchListener(mTouchListener);
//        mPriceTextView.setOnTouchListener(mTouchListener);
//        mQuantityTextView.setOnTouchListener(mTouchListener);
//        mSupplierNameTextView.setOnTouchListener(mTouchListener);
//        mSupplierPhoneNumberTextView.setOnTouchListener(mTouchListener);

        //getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mBookHasChanged boolean to true.

//    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            mBookHasChanged = true;
//            return false;
//        }
//    };

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


    private void updateBook() {

//        String titleString = mTitleTextView.getText().toString().trim();
//        String authorString = mAuthorTextView.getText().toString().trim();
//        String priceString = mPriceTextView.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
//        String supplierNameString = mQuantityTextView.getText().toString().trim();
//        String supplierPhoneNumberString = mQuantityTextView.getText().toString().trim();

//        if (TextUtils.isEmpty(priceString)) {
//            priceString = "0";
//            mPriceTextView.setText(priceString);
//        }
//
//        int priceInteger = Integer.parseInt(priceString);


        int quantityInteger = Integer.parseInt(quantityString);
        ContentValues values = new ContentValues();
        values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY, quantityInteger);
        int upInf = getContentResolver().update(mCurrentBookUri, values, null, null);
        if (upInf != 1) {
            Log.e("UPDATE", "Wrong update number: " + upInf);
        }

//        if (!TextUtils.isEmpty(titleString)) {
//            // Create a ContentValues object where column names are the keys,
//            // and pet attributes from the editor are the values.
//            ContentValues values = new ContentValues();
//            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE, titleString);
//            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR, authorString);
//            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE, priceInteger);
//            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY, quantityInteger);
//            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME, quantityInteger);
//            values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE, quantityInteger);
//
//
//            // Insert a new pet into the provider, returning the content URI for the new pet.
//            if (mCurrentBookUri == null) {
//                Uri newUri = getContentResolver().insert(BookContract.BookDatabaseTitles.CONTENT_URI, values);
//
//                if (newUri == null) {
//                    // If the new content URI is null, then there was an error with insertion.
//                    Toast.makeText(this, getString(R.string.editor_insert_book_failed),
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    // Otherwise, the insertion was successful and we can display a toast.
//                    Toast.makeText(this, getString(R.string.editor_insert_book_successful),
//                            Toast.LENGTH_SHORT).show();
//                    mCurrentBookUri = newUri;
//                }
//
//            } else {
//                int upInf = getContentResolver().update(mCurrentBookUri, values, null, null);
//                if (upInf != 1) {
//                    Log.e("UPDATE", "Wrong update number: " + upInf);
//                } else {
//                    Toast.makeText(this, getString(R.string.editor_insert_book_successful),
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu options from the res/menu/menu_editor.xml file.
//        // This adds menu items to the app bar.
//        getMenuInflater().inflate(R.menu.menu_editor, menu);
//        return true;
//    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        // If this is a new pet, hide the "Delete" menu item.
//        if (mCurrentBookUri == null) {
//            MenuItem menuItem = menu.findItem(R.id.action_delete);
//            menuItem.setVisible(false);
//        }
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
//            // Respond to a click on the "Delete" menu option
//            case R.id.action_delete:
//                showDeleteConfirmationDialog();
//                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
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
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
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

        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the pet that we want.
// Deletes the words that match the selection criteria
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
        // If the pet hasn't changed, continue with handling back button press
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
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
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
                mCurrentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String bookTitle = cursor.getString(titleColumnIndex);
            String bookAuthor = cursor.getString(authorColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleTextView.setText(bookTitle);
            mAuthorTextView.setText(bookAuthor);
            mPriceTextView.setText(Integer.toString(price));
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