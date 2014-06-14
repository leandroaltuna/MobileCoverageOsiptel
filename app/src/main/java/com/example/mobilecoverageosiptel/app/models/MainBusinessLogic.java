package com.example.mobilecoverageosiptel.app.models;

import android.content.Context;

import com.example.mobilecoverageosiptel.app.entities.Acta;

import java.util.ArrayList;

public class MainBusinessLogic {

    public static String RegistrarData(Acta oActa, Context context)
    {
        return new MainDataAccess().RegistrarData(oActa, context);
    }

    public static ArrayList<Acta> ListaActaAll(Context context)
    {
        return new MainDataAccess().ListaActaAll(context);
    }

}