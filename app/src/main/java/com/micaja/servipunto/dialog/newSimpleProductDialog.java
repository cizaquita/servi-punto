package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.activities.SalesActivity;
import com.micaja.servipunto.utils.CommonMethods;

/**
 * Created by admin on 22/03/2016.
 */
public class newSimpleProductDialog extends Dialog implements
        android.view.View.OnClickListener,
        android.content.DialogInterface.OnClickListener{

    Button btncancelar, btnguardar;
    EditText nombre, pcompra, pventa;
    private ImageView imgClose;
    public static String preciocompra, precioventa,nombreProducto;
    private Context context;
    public newSimpleProductDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsimpleproduct);
        nombre = (EditText)findViewById(R.id.etxt_Nombre);
        pcompra = (EditText)findViewById(R.id.etxt_preciocompra);
        pventa = (EditText)findViewById(R.id.etxt_precioventa);

        btnguardar = (Button)findViewById(R.id.btn_EndSaleSave);
        btncancelar = (Button)findViewById(R.id.btn_EndSaleCancel);
        imgClose = (ImageView)findViewById(R.id.img_close);
        btncancelar.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        btnguardar.setOnClickListener(this);
        nombre.setText(SalesActivity.nombre);
        pcompra.setText(SalesActivity.preciocompra);
        pventa.setText(SalesActivity.precioventa);


    }




    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_close:
                this.dismiss();
                break;

            case R.id.btn_EndSaleCancel:
                this.dismiss();
                break;
            case R.id.btn_EndSaleSave:
                this.dismiss();
                preciocompra =  pcompra.getText().toString();
                precioventa = pventa.getText().toString();
                nombreProducto = nombre.getText().toString();
                if (CommonMethods.getDoubleFormate(preciocompra) == 0.0 || CommonMethods
                        .getDoubleFormate(precioventa) == 0.0){
                    CommonMethods.displayAlert(true,"Datos Inválidos",
                            "El valor de Compra o Venta no deben ser 0.0","","OK",
                            context,newSimpleProductDialog.this,true);
                    break;

                }
                ((SalesActivity) context).validateAndSave();
                break;



            default:
                break;
        }
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {

    }



}