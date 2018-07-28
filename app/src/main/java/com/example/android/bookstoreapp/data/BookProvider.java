package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by egi-megi on 16.07.18.
 */

public class BookProvider extends ContentProvider{

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    private BookDbHelper mBookDbHelper;


    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI("com.example.android.bookstoreapp", "books", 100);
        sUriMatcher.addURI("com.example.android.bookstoreapp", "books/#", 101);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mBookDbHelper = new BookDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mBookDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(BookContract.BookDatabaseTitles.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookContract.BookDatabaseTitles._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookContract.BookDatabaseTitles.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the title is not null
        String title = values.getAsString(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book requires a title");
        }

        String author = values.getAsString(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR);
        if (author == null) {
            throw new IllegalArgumentException("Book requires an author");
        }



        Integer price = values.getAsInteger(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Price of book must be bigger than 0");
        }

        Integer quantity = values.getAsInteger(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity of book must be bigger than 0");
        }

        // Check that the title is not null
        String supplierName = values.getAsString(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Book requires a name of supplier");
        }

        String supplierPhoneNumber = values.getAsString(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Book requires a supplier's phone number");
        }

        // Create and/or open a database to read from it
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(BookContract.BookDatabaseTitles.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookContract.BookDatabaseTitles._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE)) {
            String title = values.getAsString(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Book requires a title");
            }
        }

        if (values.containsKey(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR)) {
            String author = values.getAsString(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR);
            if (author == null) {
                throw new IllegalArgumentException("Book requires a author");
            }
        }


        if (values.containsKey(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE)) {
            Integer price = values.getAsInteger(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Price of book must be bigger than 0");
            }
        }

        // Check that the title is not null
        if (values.containsKey(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book requires a name of supplier");
            }
        }

        if (values.containsKey(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE)) {
            String supplierPhoneNumber = values.getAsString(BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE);
            if (supplierPhoneNumber == null) {
                throw new IllegalArgumentException("Book requires a supplier's phone number");
            }
        }

// No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookContract.BookDatabaseTitles.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get writeable database
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                // For  case BOOKS:
                rowsDeleted = database.delete(BookContract.BookDatabaseTitles.TABLE_NAME, selection, selectionArgs);
                // Delete all rows that match the selection and selection args
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookDatabaseTitles._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                rowsDeleted = database.delete(BookContract.BookDatabaseTitles.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookDatabaseTitles.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookDatabaseTitles.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


}
