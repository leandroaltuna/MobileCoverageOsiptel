package com.example.mobilecoverageosiptel.app;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.mobilecoverageosiptel.app.entities.Acta;
import com.example.mobilecoverageosiptel.app.models.MainBusinessLogic;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;


public class SyncService extends IntentService {

    private static final String TAG = SyncService.class.getSimpleName();

	private int result = Activity.RESULT_CANCELED;

	ArrayList<Acta> actaArrayList = new ArrayList<Acta>();

	public  SyncService()
	{
		super("SyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		actaArrayList = MainBusinessLogic.ListaActaAll(getApplicationContext());
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

			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			request.setEntity(entity);

			result = Activity.RESULT_OK;

		}
		catch (Exception ex)
		{
			Log.e(TAG, "SYNC_DATA_BD", ex);
		}

		publishResults(result);
	}

	private void publishResults(int result)
	{
		Intent intent = new Intent(NOTIFICATION_SERVICE);
		intent.putExtra("RESULT",result);

		sendBroadcast(intent);
	}
}
