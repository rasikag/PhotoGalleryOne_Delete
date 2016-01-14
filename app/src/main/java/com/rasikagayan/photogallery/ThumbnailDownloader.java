package com.rasikagayan.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.sip.SipSession;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rasika Gayan on 1/4/2016.
 */

// now this will be a image view
public class ThumbnailDownloader<Token> extends HandlerThread {

    // need some object to identify what object to download

    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    Handler mHandler;
    Map<Token,String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;

    public interface Listener<Token>{
        void onThumbnailDownloaded(Token token,Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener){
        mListener = listener;
    }


    public ThumbnailDownloader(Handler handler) {
        super(TAG);
        // this is associated with the UI thread looper
        // UI update code will run on UI thread / main thread
        mResponseHandler = handler;
    }

    // in here get the token and string
    // that set message thing is happen in here

    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token,url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD,token).sendToTarget();
    }
    // this will call before the the looper check queue first time
    // this is the place we have to implement the handler imp:
    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    @SuppressWarnings("unchecked")
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    // in here download the image and put to bit map
                    // now this bit map pass to ui thread
                    handleRequest(token);
                }
            }
        };
    }

    private void handleRequest(final Token token){
        try{
            final String url = requestMap.get(token);
            if(url == null){
                return;
            }

            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(requestMap.get(token) != url){
                        return;
                    }
                    requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token,bitmap);
                }
            });
        }catch (IOException ioe){
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    // this will clean the hash map
    public void cleanQueue(){
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

}
