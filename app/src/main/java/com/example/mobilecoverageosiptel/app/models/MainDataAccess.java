package com.example.mobilecoverageosiptel.app.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.mobilecoverageosiptel.app.db.DBHelper;
import com.example.mobilecoverageosiptel.app.entities.Acta;

import java.util.ArrayList;

public class MainDataAccess {

    private static final String TAG = MainDataAccess.class.getSimpleName();

    public String RegistrarData(Acta oActa, Context context)
    {
        DBHelper dbHelper = DBHelper.getUtilDb(context);

        String result = "0";

        try {
            dbHelper.openDataBase();
            dbHelper.beginTransaction();

            ContentValues values = new ContentValues();

            values.put("Departamento", oActa.departamento);
            values.put("Provincia", oActa.provincia);
            values.put("Distrito", oActa.distrito);
            values.put("Localidad", oActa.localidad);
            values.put("Latitud", oActa.latitud);
            values.put("Longitud", oActa.longitud);

            Long id = dbHelper.getDatabase().insertOrThrow("Acta", null, values);

            if (id == 0) {
                result = String.valueOf(0);
            } else {
                result = String.valueOf(1);
            }

            dbHelper.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            Log.e(TAG, "MAIN_DATA_ACCESS", e);
        }
        finally
        {
            dbHelper.endTransaction();
            dbHelper.close();
        }

        return result;
    }

    public ArrayList<Acta> ListaActaAll(Context context)
    {
        ArrayList<Acta> actaArrayList = new ArrayList<Acta>();

        DBHelper dbHelper = DBHelper.getUtilDb(context);

        Cursor cursor = null;

        try
        {
            dbHelper.openDataBase();
            dbHelper.beginTransaction();

            String SQL = "select Departamento, Provincia, Distrito, Localidad, Latitud, Longitud from Acta";

            cursor = dbHelper.getDatabase().rawQuery(SQL, null);

            Acta oActa;

            if (cursor.moveToFirst())
            {
                while (cursor.isAfterLast() == false)
                {
                    oActa = new Acta();
                    oActa.departamento = cursor.getString(cursor.getColumnIndex("Departamento"));
                    oActa.provincia = cursor.getString(cursor.getColumnIndex("Provincia"));
                    oActa.distrito = cursor.getString(cursor.getColumnIndex("Distrito"));
                    oActa.localidad = cursor.getString(cursor.getColumnIndex("Localidad"));
                    oActa.latitud = cursor.getString(cursor.getColumnIndex("Latitud"));
                    oActa.longitud = cursor.getString(cursor.getColumnIndex("Longitud"));

                    actaArrayList.add(oActa);
                    cursor.moveToNext();
                }
            }

        }
        catch (Exception ex)
        {
            Log.d(TAG, "ALL_LIST_ACTA", ex);
        }
        finally
        {
            dbHelper.endTransaction();
            dbHelper.close();
            cursor.close();
        }

        return actaArrayList;
    }

}
