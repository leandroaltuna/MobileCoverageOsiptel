package com.example.mobilecoverageosiptel.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilecoverageosiptel.app.db.DBHelper;
import com.example.mobilecoverageosiptel.app.entities.Acta;
import com.example.mobilecoverageosiptel.app.models.MainBusinessLogic;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    ProgressDialog progressDialog;

    EditText editDepartamento;
    EditText editProvincia;
    EditText editDistrito;
    EditText editLocalidad;
    EditText editLatitud;
    EditText editLongitud;

    Button btnGuardar;
    Button btnSincronizar;
    Button btnGeo;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2;
    private static boolean cargando = false;

    ArrayList<Acta> actaArrayList = new ArrayList<Acta>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editDepartamento = (EditText) findViewById(R.id.editDepartamento);
        editProvincia = (EditText) findViewById(R.id.editProvincia);
        editDistrito = (EditText) findViewById(R.id.editDistrito);
        editLocalidad = (EditText) findViewById(R.id.editLocalidad);
        editLatitud = (EditText) findViewById(R.id.editLatitud);
        editLongitud = (EditText) findViewById(R.id.editLongitud);

        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnSincronizar = (Button) findViewById(R.id.btnSincronizar);
        btnGeo = (Button) findViewById(R.id.btnGeo);


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editDepartamento = (EditText) findViewById(R.id.editDepartamento);
                editProvincia = (EditText) findViewById(R.id.editProvincia);
                editDistrito = (EditText) findViewById(R.id.editDistrito);
                editLocalidad = (EditText) findViewById(R.id.editLocalidad);

                RegistraDataDB tarea = new RegistraDataDB();
                tarea.execute();
            }
        });

        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                actaArrayList = MainBusinessLogic.ListaActaAll(getApplicationContext());

                if (actaArrayList.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "No existen datos a sincronizar", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SincronizarDataDB tarea = new SincronizarDataDB();
                    tarea.execute();
                }

            }
        });

        btnGeo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (cargando == false)
                {
                    progressDialog = ProgressDialog.show(MainActivity.this, "Geolocalizando","Obteniendo tu localizacion...",true);
                }

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                LocationListener locationListener = new myLocationListener();

                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,locationListener);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class RegistraDataDB extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = progressDialog.show(MainActivity.this, getString(R.string.msg_porfavor_espere), getString(R.string.msg_guardando), true);

        }

        @Override
        protected String doInBackground(Void... voids) {

            String resultado;

            Acta oActa = new Acta();
            oActa.departamento = editDepartamento.getText().toString();
            oActa.provincia = editProvincia.getText().toString();
            oActa.distrito = editDistrito.getText().toString();
            oActa.localidad = editLocalidad.getText().toString();
            oActa.latitud = editLatitud.getText().toString();
            oActa.longitud = editLongitud.getText().toString();

            DBHelper dbHelper = DBHelper.getUtilDb(getApplicationContext());

            resultado = MainBusinessLogic.RegistrarData(oActa, getApplicationContext());

            return resultado;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            if (s.equals("1"))
            {
                Toast.makeText(getApplicationContext(), "se guardo correctamente", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Ocurrio un error al guardar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SincronizarDataDB extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this, "Por favor espere", "Sincronizando...",true);

        }

        @Override
        protected String doInBackground(Void... voids) {

            String result = "";

            String url = "http://iacs-sac.com/index.php/acta_service";

            try
            {
                HttpPost request = new HttpPost(url);

                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");

                Acta oActa = new Acta();
                oActa.departamento = actaArrayList.get(0).departamento;
                oActa.provincia = actaArrayList.get(0).provincia;
                oActa.distrito = actaArrayList.get(0).distrito;
                oActa.localidad = actaArrayList.get(0).localidad;
                oActa.latitud = actaArrayList.get(0).latitud;
                oActa.longitud = actaArrayList.get(0).longitud;

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("tipo","objeto");

                JsonArray jsonArray = new JsonArray();

                JsonObject dataset = new JsonObject();
                dataset.addProperty("departamento", oActa.departamento);
                dataset.addProperty("provincia", oActa.provincia);
                dataset.addProperty("distrito", oActa.distrito);
                dataset.addProperty("localidad", oActa.localidad);
                dataset.addProperty("latitud", oActa.latitud);
                dataset.addProperty("longitud", oActa.longitud);

                jsonArray.add(dataset);
                jsonObject.add("data", jsonArray);
                //Gson gson = new Gson();
                //String json = gson.toJson(oActa);

                Gson gson = new Gson();
                String json = gson.toJson(jsonObject);

                Log.d(TAG, json);

                StringEntity entity = new StringEntity(json,HTTP.UTF_8);
                request.setEntity(entity);

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);

                result = EntityUtils.toString(response.getEntity());

            }
            catch (Exception ex)
            {
                Log.e(TAG, "SYNC_DATA_BD", ex);
            }

            return  result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            Log.d(TAG, "SYNC_POST_EXECUTE = " + s);

            if (s.equals("1"))
            {
                Toast.makeText(getApplicationContext(), "Sincronizacion exitosa", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Ocurrio un error al sincronizar", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class myLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {

            Double latitud = location.getLatitude();
            Double longitud = location.getLongitude();

            String Text = "My current location is: " +
                    "Latitud: " + latitud.toString() + "Longitud: " + longitud.toString();

            editLatitud.setText(latitud.toString());
            editLongitud.setText(longitud.toString());

            cargando = true;
            progressDialog.dismiss();

            Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
        }
    }

}