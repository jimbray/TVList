package com.jim.tvs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;

public class AboutActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(MainActivity.Theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl("file:///android_asset/about.htm");
		webView.loadDataWithBaseURL("file:///android_res/raw/", readTextFromResource(R.raw.about), "text/html", "UTF-8", null);
	}
	
	private String readTextFromResource(int resId) {
		InputStream rawIn = getResources().openRawResource(resId);
		ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
		int i ;
		try {
			i = rawIn.read();
			while(i != -1) {
				outPutStream.write(i);
				i = rawIn.read();
			}
			rawIn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return outPutStream.toString();
	}
	
}
