package com.mycompany.databasetest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.net.URI;

public class DatabaseProvider extends ContentProvider {

    public static final int BOOK_DIR = 0;
    public static final int BOOK_ITEM = 1;
    public static final int CATEGORY_DIR = 2;
    public static final int CATEGORY_ITEM = 3;
    public static final String AUTHORITY = "com.mycompany.databasetest.provider";

    private static UriMatcher uriMatcher;

    private MyDatabaseHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "book", BOOK_DIR);
        uriMatcher.addURI(AUTHORITY, "book/#", BOOK_ITEM);
        uriMatcher.addURI(AUTHORITY, "category", CATEGORY_DIR);
        uriMatcher.addURI(AUTHORITY, "category/#", CATEGORY_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MyDatabaseHelper(getContext(), "BookStore.db", null, 2);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                cursor = db.query("Book", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                cursor = db.query("Book", projection, "id = ?", new String[]{bookId}, null, null, sortOrder);
                break;
            case CATEGORY_DIR:
                cursor = db.query("Category", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                cursor = db.query("Category", projection, "id = ?", new String[]{categoryId}, null, null, sortOrder);
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                return "vnd.android.cursor.dir/vnd.com.mycompany.databasetest.provider.book";
            case BOOK_ITEM:
                return "vnd.android.cursor.item/vnd.com.mycompany.databasetest.provider.book";
            case CATEGORY_DIR:
                return "vnd.android.cursor.dir/vnd.com.mycompany.databasetest.provider.category";
            case CATEGORY_ITEM:
                return "vnd.android.cursor.item/vnd.com.mycompany.databasetest.provider.category";
        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
            case BOOK_ITEM:
                long newBookId = db.insert("Book", null, contentValues);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/book/" + newBookId);
                break;
            case CATEGORY_DIR:
            case CATEGORY_ITEM:
                long newCategoryId = db.insert("Book", null, contentValues);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/book/" + newCategoryId);
                break;
        }

        return uriReturn;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                deletedRows = db.delete("Book", s, strings);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                deletedRows = db.delete("Book", "id = ?", new String[] {bookId});
                break;
            case CATEGORY_DIR:
                deletedRows = db.delete("Category", s, strings);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                deletedRows = db.delete("Category", "id = ?", new String[] {categoryId});
                break;
        }

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                updatedRows = db.update("Book", contentValues, s, strings);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                updatedRows = db.update("Book", contentValues, "id = ?", new String[] {bookId});
                break;
            case CATEGORY_DIR:
                updatedRows = db.update("Category", contentValues, s, strings);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                updatedRows = db.update("Category", contentValues, "id = ?", new String[] {categoryId});
                break;
        }

        return updatedRows;
    }
}
