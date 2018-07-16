package com.example.android.bookstoreapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.bookstoreapp.R;

/**
 * Created by egi-megi on 06.07.18.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bookstore.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String DEFAULT = " DEFAULT ";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BookContract.BookDatabaseTitles.TABLE_NAME + " (" +
                    BookContract.BookDatabaseTitles._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR + TEXT_TYPE + NOT_NULL + DEFAULT + "Unknown" + COMMA_SEP +
                    BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY + INTEGER_TYPE + NOT_NULL + DEFAULT + 0 + COMMA_SEP +
                    BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    BookContract.BookDatabaseTitles.COLUMN_BOOK_SUPPLIER_PHONE + TEXT_TYPE + NOT_NULL + ")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + BookContract.BookDatabaseTitles.TABLE_NAME;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        // make the method insertFirstBook(db) when the database is created for the first time
        insertFirstBook(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    private void insertFirstBook(SQLiteDatabase db) {

        // Create and/or open a database to read from it

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
