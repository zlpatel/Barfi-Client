package com.barfi.android;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.os.Looper;
import android.util.Log;

public class SendJSON {

	protected void sendJson(final JSONObject json, final String URL) {
		Thread t = new Thread() {

			public void run() {
				Looper.prepare(); // For Preparing Message Pool for the child
									// Thread
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(),
						10000); // Timeout Limit
				HttpResponse response;

				try {
					HttpPost post = new HttpPost(URL);

					StringEntity se = new StringEntity(json.toString());
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/json"));
					post.setEntity(se);
					response = client.execute(post);

					/* Checking response */
					if (response != null) {
						InputStream in = response.getEntity().getContent(); // Get
																			// the
																			// data
																			// in
																			// the
																			// entity
					}

				} catch (Exception e) {
					e.printStackTrace();
					Log.d("Error", "Cannot Estabilish Connection");
				}

				Looper.loop(); // Loop in the message queue
			}
		};

		t.start();
	}

}
