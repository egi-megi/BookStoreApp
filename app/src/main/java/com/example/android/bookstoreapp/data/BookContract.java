package com.example.android.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by egi-megi on 06.07.18.
 */

public final class BookContract {

    private BookContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";

    public static abstract class BookDatabaseTitles implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

            public static final String TABLE_NAME = "books";
            public static final String _ID = BaseColumns._ID;
            public static final String COLUMN_BOOK_TITLE = "TitleBook";
            public static final String COLUMN_BOOK_AUTHOR = "AuthorBook";
            public static final String COLUMN_BOOK_PRICE = "Price";
            public static final String COLUMN_BOOK_QUANTITY = "Quantity";
            public static final String COLUMN_BOOK_SUPPLIER_NAME = "SupplierName";
            public static final String COLUMN_BOOK_SUPPLIER_PHONE = "SupplierPhoneNumber";

    }
}
