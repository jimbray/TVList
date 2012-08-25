package com.jim.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.jim.myinterface.InterruptTask;
import com.jim.myinterface.RequestListener;

public class GetStreamData extends AsyncTask<Void, Void, List<StreamInfo>> implements InterruptTask {
	private String mUrl = null;
	private RequestListener mListener = null;
	boolean isContinue = true;
	
	public GetStreamData(String url, RequestListener listener) {
		this.mUrl = url;
		this.mListener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		mListener.OnGetDataBegin();
	}

	@Override
	protected List<StreamInfo> doInBackground(Void... params) {
    	List<StreamInfo> list = new ArrayList<StreamInfo>();
    	URL urlPath = null;
		try {
			urlPath = new URL(mUrl);
		} catch (MalformedURLException e1) {
			mListener.OnGetDataException(e1);
			e1.printStackTrace();
			isContinue = false;
		}
    	
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			mListener.OnGetDataException(e);
			e.printStackTrace();
			isContinue = false;
		}
		Document doc = null;
		
		try {
			doc = db.parse(new InputSource(urlPath.openStream()));
		} catch (SAXException e) {
			e.printStackTrace();
			isContinue = false;
			mListener.OnGetDataException(e);
		} catch (IOException e) {
			mListener.OnGetDataException(e);
			e.printStackTrace();
			isContinue = false;
		}
		
		if(isContinue) {
			Element root = (Element) doc.getDocumentElement();
			
			NodeList itemList = root.getElementsByTagName("item");
			
			for(int i = 0 ; i < itemList.getLength(); i++){
				StreamInfo info = new StreamInfo();
				Element item = (Element) itemList.item(i);
				
				String title = item.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
				info.setTitle(title);
				
				String url = item.getElementsByTagName("url").item(0).getFirstChild().getNodeValue();
				info.setUrl(url);
				list.add(info);
			}
		} else {
			list = null;
		}
		return list;
	}
	
	@Override
	protected void onPostExecute(List<StreamInfo> result) {
		if(isContinue)
			mListener.OnGetDataComplete(result);
	}

	@Override
	public void interrupt() {
		cancel(true);
	}
}
