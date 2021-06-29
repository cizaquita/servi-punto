package com.micaja.servipunto.providers;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cristian dot izaquita at gmail dot com on 18/04/2016.
 */
public class DeliveryContract {
    /**
     * Autoridad del Content Provider
     */
    public final static String AUTHORITY = "com.micaja.servipunto.providers.DeliveryProvider";
    /**
     * Representación de la tabla a consultar
     */
    public static final String ACTIVIDAD = "tbl_delivery";
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
        //delivery_id TEXT          ****
        // cedula TEXT
        // name TEXT                ****
        // telephone TEXT
        // address TEXT
        // email TEXT
        // gender TEXT
        // active_status TEXT       ****
        // initial_debt TEXT
        // balance_amount TEXT
        // last_payment_date TEXT
        // created_date TEXT
        // modified_date TEXT
        // sync_status TEXT

        public final static String DELIVERY_ID = "delivery_id";
        public final static String NAME = "name";
        public final static String ACTIVE_STATUS = "active_status";
        public final static String TRANS_DELIVERY = "trans_delivery";

    }


}
