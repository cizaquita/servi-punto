/**
 * 
 * @author 
 * Ybrant Digital
 * Copyright (C) Ybrant Digital
 * http://www.ybrantdigital.com
 *
 */

package com.micaja.servipunto.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.micaja.servipunto.ServiApplication;

public class DownloadAPK extends AsyncTask<Context, Integer, String> 
{
	private String storagePath;
	private String fileName;
	Context context;
	private Handler handler;
	private int totalResponseSize;

	public DownloadAPK(String storagePath,String fileName, Handler handler) 
	{
		this.storagePath	= storagePath;
		this.fileName 		= fileName;
		this.handler		= handler;
	}

	@Override
	protected String doInBackground(Context... arg0)
	{URL url;
	  InputStream input  = null;
	  OutputStream output = null;

	  try 
	  {
	   url    = new URL(ServiApplication.URL+Constants.APK_DOWNLOAD);

	   HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	   httpURLConnection.setConnectTimeout(Constants.HTTP_CONN_TIMEOUT);
	   httpURLConnection.setRequestMethod("POST");

          Map<String,String> parameter = new HashMap<String, String>();
          parameter.put("appName",Constants.SERVIRESTAURENT_APK_NAME);
          String  postParameters = createQueryStringForParameters (parameter);
          PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
          out.print(postParameters);

          out.close();

	   
	   input    = httpURLConnection.getInputStream();
	   totalResponseSize = httpURLConnection.getContentLength();
	   
	   System.out.println("File path :"+storagePath);
	   
	   output    = new FileOutputStream(storagePath + "/" + fileName);
	   
	   byte[] buffer  = new byte[1024*1024];

	   int bytesRead  = 0;
	   long total  = 0;
	   
	   while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
	    output.write(buffer, 0, bytesRead);
	     total += bytesRead;

	    //(int) (total * 100 / fileLength)
	     
	     onProgressUpdate((int) (total * 100 / totalResponseSize));
	   }
	   
	   return "completed";
	  } 
	  catch (Exception e) 
	  { 
	   e.printStackTrace();
	  } 
	  finally
	  {
	   if(output != null)
	   {
	    try 
	    {
	      output.close();
	    }
	    catch (IOException e) 
	    {
	     e.printStackTrace();
	    }
	   }
	   
	   if(input != null)
	   {
	    try
	    {
	     input.close();
	    }
	    catch (IOException e)
	    {
	     e.printStackTrace();
	    }
	   }
	  }
	  
	  return null;}
	
	@Override
	protected void onProgressUpdate(Integer... values)
	{
		super.onProgressUpdate(values);		
		 Message msg	= new Message();
		 msg.arg1		= 6;
		 msg.obj		= String.valueOf(values[0]);
		 handler.sendMessage(msg);
	}
	
	 @Override
	protected void onPostExecute(String result)
	 {
		 if(result != null && result.equals("completed"))
		 {
			 sendMsgToHandler("success");
		 }
		 else
		 {
			 sendMsgToHandler("fail");
			 
			 File file = new File(storagePath + "/" + fileName);
			 if(file.exists())
				 file.delete();
		 }
	 }
	 
	 private void sendMsgToHandler(String message)
	 {
		 Message msg	= new Message();
		 msg.arg1		= 3;
		 msg.obj		= message;
		 handler.sendMessage(msg);
	 }

    private  final char PARAMETER_DELIMITER = '&';
    private  final char PARAMETER_EQUALS_CHAR = '=';
    public  String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(URLEncoder.encode(
                                parameters.get(parameterName)));

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }

}