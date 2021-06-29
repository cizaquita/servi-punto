package com.micaja.servipunto.providers;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cristian dot izaquita at gmail dot com on 18/04/2016.
 */
public class SalesDetailsContract {
    /**
     * Autoridad del Content Provider
     */
    public final static String AUTHORITY = "com.micaja.servipunto.providers.SalesDetailsProvider";
    /**
     * Representación de la tabla a consultar
     */
    public static final String ACTIVIDAD = "tbl_sales_details";
    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME =
            "vnd.android.cursor.item/vnd." + AUTHORITY + ACTIVIDAD;
    /**
     * Tipo MIME que retorna la consulta de CONTENT_URI
     */
    public final static String MULTIPLE_MIME =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + ACTIVIDAD;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + ACTIVIDAD);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS = 1;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW = 2;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, ACTIVIDAD, ALLROWS);
        uriMatcher.addURI(AUTHORITY, ACTIVIDAD + "/#", SINGLE_ROW);
    }

    /**
     * Estructura de la tabla
     */
    public static class Columnas implements BaseColumns {

        private Columnas() {
            // Sin instancias
        }
        // sales_details_id     ****
        // sale_id text         ****
        // product_id text      ****
        // count text           ****
        // uom text
        // price text           ****
        // dish_id text
        // sync_status integer
        public final static String SALES_DETAILS_ID = "sales_details_id";
        public final static String SALE_ID = "sale_id";
        public final static String PRODUCT_ID = "product_id";
        public final static String COUNT = "count";
        public final static String PRICE = "price";

    }


}
