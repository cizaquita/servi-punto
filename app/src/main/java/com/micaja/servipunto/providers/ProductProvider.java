package com.micaja.servipunto.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.utils.Constants;

/**
 * Created by * Created by Cristian dot izaquita at gmail dot com on 18/04/2016.
 */
public class ProductProvider extends ContentProvider{
    //private Database dbOpener;

    @Override
    public boolean onCreate() {
        return false;
    }
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Obtener base de datos
        SQLiteDatabase db = DBHandler.getDBObj(Constants.WRITABLE);//dbOpener.getWritableDatabase();
        //SQLiteDatabase asd = null;
        // Comparar Uri
        int match = ProductContract.uriMatcher.match(uri);

        Cursor c;

        switch (match) {
            case ProductContract.ALLROWS:
                // Consultando todos los registros
                c = db.query(ProductContract.ACTIVIDAD, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(
                        getContext().getContentResolver(),
                        ProductContract.CONTENT_URI);
                break;
            case ProductContract.SINGLE_ROW:
                // Consultando un solo registro basado en el Id del Uri
                long videoID = ContentUris.parseId(uri);
                c = db.query(ProductContract.ACTIVIDAD, projection,
                        ProductContract.Columnas._ID + " = " + videoID,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(
                        getContext().getContentResolver(),
                        ProductContract.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (ProductContract.uriMatcher.match(uri)) {
            case ProductContract.ALLROWS:
                return ProductContract.MULTIPLE_MIME;
            case ProductContract.SINGLE_ROW:
                return ProductContract.SINGLE_MIME;
            default:
                throw new IllegalArgumentException("Tipo de actividad desconocida: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}