package com.micaja.servipunto.providers;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cristian dot izaquita at gmail dot com on 18/04/2016.
 */
public class OrdersContract {
    /**
     * Autoridad del Content Provider
     */
    public final static String AUTHORITY = "com.micaja.servipunto.providers.OrdersProvider";
    /**
     * Representación de la tabla a consultar
     */
    public static final String ACTIVIDAD = "tbl_orders";
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
        public final static String ORDER_ID = "order_id";
        public final static String SUPPLIER_ID = "supplier_id";
        public final static String INVOICE_NO = "invoice_no";
        public final static String GROSS_AMOUNT = "gross_amount";
        public final static String NET_AMOUNT = "net_amount";
        public final static String DISCOUNT = "discount";
        public final static String PAYMENT_TYPE = "payment_type";
        public final static String ID_ORDER_CONFIRMED = "is_order_confirmed";
        public final static String DATE_TIME = "date_time";
        public final static String SYNC_STATUS = "sync_status";
        public final static String FECHA_INICIAL = "fecha_inicial";
        public final static String FECHA_FINAL = "fecha_final";

    }


}
