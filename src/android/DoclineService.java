package br.com.williarts;

import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

import android.util.Log;

import org.json.JSONObject;

public class DoclineService extends BackgroundService {

	@Override
	protected JSONObject doWork() {
	   JSONObject result = new JSONObject();
	   
	   Log.d("doWork", result);
	   
	   return result;
	}

	@Override
	protected JSONObject getConfig() {
	   return null;
	}

	@Override
	protected void setConfig(JSONObject config) {

	}     

	@Override
	protected JSONObject initialiseLatestResult() {
	   return null;
	}
	
}
