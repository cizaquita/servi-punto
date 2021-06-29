package com.micaja.servipunto.providers;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cristian dot izaquita at gmail dot com on 18/04/2016.
 */
public class SalesContract {
    /**
     * Autoridad del Content Provider
     */
    public final static String AUTHORITY = "com.micaja.servipunto.providers.SalesProvider";
    /**
     * Representación de la tabla a consultar
     */
    public static final String ACTIVIDAD = "tbl_sales";
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
        // sale_id text primary key,     **** ID
        // invoice_number text
        // gross_amount text
        // net_amount text               **** TOTAL VALUE
        // discount text
        // client_id text                ***
        // delivery_code TEXT           **** Motero
        // trans_delivery INTEGER       **** Check if value > 0 to get delivery data
        // amount_paid text
        // payment_type text
        // date_time text
        // sync_status integer
        // fecha_inicial TEXT
        // fecha_final TEXT


        public final static String SALE_ID = "sale_id";
        public final static String NET_AMOUNT = "net_amount";
        public final static String DELIVERY_CODE = "delivery_code";
        public final static String TRANS_DELIVERY = "trans_delivery";
        public final static String CLIENT_ID = "client_id";
        public final static String DATE_TIME = "date_time";
        public final static String FECHA_INICIAL = "fecha_inicial";
        public final static String FECHA_FINAL = "fecha_final";

    }


}
