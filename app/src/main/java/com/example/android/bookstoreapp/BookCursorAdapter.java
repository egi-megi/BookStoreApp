package com.example.android.bookstoreapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract;


/**
 * Created by egi-megi on 16.07.18.
 */

public class BookCursorAdapter extends CursorAdapter {

    Uri tableUri;
    ContentResolver contentResolver;
    Activity a;

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c, Uri tableUri, ContentResolver contentResolver, Activity a) {
        super(context, c, 0 /* flags */);
        this.tableUri = tableUri;
        this.contentResolver = contentResolver;
        this.a = a;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        // Find fields to populate in inflated template
        TextView bookTitle = (TextView) view.findViewById(R.id.title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.author);
        TextView price = (TextView) view.findViewById(R.id.price);
        final TextView quantity = (TextView) view.findViewById(R.id.quantity);

        // Extract properties from cursor
        int titleColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY);

        final int id = cursor.getInt(cursor.getColumnIndex(BookContract.BookDatabaseTitles._ID));

        String currentTitle = cursor.getString(titleColumnIndex);
        String currentAuthor = cursor.getString(authorColumnIndex);
        Integer currentPrice = cursor.getInt(priceColumnIndex);
        int currentQuantity = cursor.getInt(quantityColumnIndex);


        bookTitle.setText(currentTitle);

        if (TextUtils.isEmpty(currentAuthor)) {
            bookAuthor.setText(R.string.unknown_author);
        } else {
            bookAuthor.setText(String.valueOf(currentAuthor));
        }

        price.setText(Float.toString(currentPrice));
        quantity.setText(Integer.toString(currentQuantity));

        // A button that subtracts 1 from the quantity of books in the magazine
        Button buyButton = (Button) view.findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = Integer.parseInt(quantity.getText().toString());
                if (currentQuantity <= 0) {
                    currentQuantity = 0;
                } else {
                    currentQuantity = currentQuantity - 1;
                }
                quantity.setText(Integer.toString(currentQuantity));

                ContentValues values = new ContentValues();
                values.put(BookContract.BookDatabaseTitles.COLUMN_BOOK_QUANTITY, currentQuantity);
                contentResolver.update(tableUri, values, BookContract.BookDatabaseTitles._ID + " = " + id, null);
            }
        });

        // Intent from one item with book to activity with details of clicked book
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(a, BookDetailsActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookDatabaseTitles.CONTENT_URI, id);

                intent.setData(currentBookUri);

                a.startActivity(intent);
            }
        });

    }
}

