package com.panduka.quickcricketinfo.app;

import android.app.Application;
import android.text.TextUtils;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.panduka.quickcricketinfo.utils.LruBitmapCache;

/**
 * Created by pandukadesilva on  2/22/16.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private TransferUtility mTransferUtility;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //Facebook integration related
        FacebookSdk.sdkInitialize(getApplicationContext());

        //AWS tranfer utility object creation
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                AppConfig.AWS_IDENTITY_POOL_ID,
                Regions.AP_NORTHEAST_1
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        setTransferUtility(new TransferUtility(s3, getApplicationContext()));
    }

    //handling volley related message queues
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ImageLoader getImageLoader() {

        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache(getApplicationContext()));
        }
        return this.mImageLoader;
    }

    public TransferUtility getTransferUtility() {
        return mTransferUtility;
    }

    private void setTransferUtility(TransferUtility mTransferUtility) {
        this.mTransferUtility = mTransferUtility;
    }
}
