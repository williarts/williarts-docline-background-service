package br.com.williarts;

import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

import com.docline.br.MainActivity;

import android.util.Log;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

import com.onesignal.OneSignal;
import com.onesignal.OSNotification;

class ServiceNotificationHandler implements OneSignal.NotificationReceivedHandler {
	
	private DoclineService service;
	
	public ServiceNotificationHandler(DoclineService service) {
		this.service = service;
	}
	
	@Override
	public void notificationReceived(OSNotification notification) {
		try {
			
			JSONObject notificationJSON = notification.toJSONObject();
			
			Log.d("Notificação recebida", notification.toJSONObject().toString(2));
			
			JSONObject payload = notificationJSON.getJSONObject("payload");
			JSONObject additionalData = payload.getJSONObject("additionalData");
			String id = additionalData.getString("id");
			
			this.service.setIdNotification(id);
			this.service.changeHandling(false);
			this.service.changeWaiting(false);
			this.service.changeStarting(true);
			this.service.dispara();
			OneSignal.setSubscription(false);
			this.service.openApp();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

public class DoclineService extends BackgroundService {

	private boolean status = true;
	private List<String> situacao = new ArrayList<String>();
	private boolean waiting = false;
	private boolean starting = false;
	private boolean handlingNotification = false; 
	private boolean disparar = false;
	private String idNotification = "";
	
	@Override
	protected JSONObject doWork() {
		
		if (handlingNotification) {
			Log.d("Situacao", "Aplicativo desligado, aguardando push");
			return null;
		}
		
		if(starting) {
			Log.d("Situacao", "Aplicativo startando, work em espera");
			return null;
		}
		
		if (waiting) {
			status = false;
			this.startOneSignal();
		}
		
		Log.d("Status", String.valueOf(status));
		Log.d("Situacao", situacao.toString());
		Log.d("Waiting", String.valueOf(waiting));
		
		if (!status) {
			situacao.add("Aplicativo desligado");
		} else {
			situacao.add("Aplicativo ligado");
		}

		try {
			this.waiting = true;
			JSONArray array = new JSONArray(situacao);
			return array.getJSONObject(array.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected JSONObject getConfig() {
		
		JSONObject status = new JSONObject();
		
		try {
			status.put("disparar", disparar);
			status.put("id", idNotification);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}

	@Override
	protected void setConfig(JSONObject config) {
		try {
			
			if (starting) {
				this.starting = false;
				this.disparar = false;
			}
			
			this.waiting = false;
			boolean value = config.getBoolean("ativo");
			this.status = value;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected JSONObject initialiseLatestResult() {
		return null;
	}
	
	private void startOneSignal() {
		this.handlingNotification = true;
		OneSignal.startInit(this)
        .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
        .setNotificationReceivedHandler(new ServiceNotificationHandler(this))
        .unsubscribeWhenNotificationsAreDisabled(true)
        .init();
		OneSignal.setSubscription(true);
	}
	
	public void changeHandling(boolean value) {
		handlingNotification = value;
	}
	
	public void changeWaiting(boolean value) {
		waiting = value;
	}
	
	public void changeStarting(boolean value) {
		starting = value;
	}
	
	public void dispara() {
		this.disparar = true;
	}
	
	public void setIdNotification(String idNotification) {
		this.idNotification = idNotification;
	}
	
	public void openApp() {
		Intent dialogIntent = new Intent(this, MainActivity.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(dialogIntent);
	}
}


