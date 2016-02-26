package com.panduka.quickcricketinfo.service;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.panduka.quickcricketinfo.app.AppConfig;
import com.panduka.quickcricketinfo.app.AppController;
import com.panduka.quickcricketinfo.utils.DataResolver;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pandukadesilva on  2/22/16.
 */
public class DataDownloader {

    private static final String TAG = DataDownloader.class.getSimpleName();
    public static final String TAG_REQ_GET_CRICKET_MATCHES = "cric_request";
    public static final String DATA_RESOLVER_OK = "resolver_ok";
    public static final String DATA_RESOLVER_ERROR = "resolver_error";
    public static final String DATA_RETRIEVE_ERROR = "retrieve_error";

    //error messages
    private static final String MSG_JSON_RETRIEVE_ERROR = "Json Not handled Properly!";
    private static final String MSG_DATA_RETRIEVE_OK = "Match info downloaded successfully";
    public static final String MSG_OBJ_RETRIEVE_ERROR = "Match data has not returned propelry. The system state unstable!";
    private static final String MSG_OOPS = "Oops! ";
    private static final String MSG_SERVER_RESPONSE_ERROR = "Oops, Cric Info server did not respond very well";

    Fragment mFrag;

    public DataDownloader(Fragment frag) {
        this.mFrag = frag;
    }

    public interface ResponseHandler {
        void sendResponse(Map<String, String> response);
    }

    public void getKwickies() {
        final ResponseHandler networkResponseHandler = (ResponseHandler) this.mFrag;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_CRIC_INFO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                boolean resFlag_ = false;
                String resolverError = null;
                try {
                    DataResolver.getInstance().initialize(response);
                    DataResolver.getInstance().getCricketMatchList();
                    resFlag_ = true;
                } catch (JSONException e) {
                    resolverError = MSG_JSON_RETRIEVE_ERROR;
                    Log.d(TAG, resolverError);
                    resFlag_ = false;
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    resolverError = MSG_OBJ_RETRIEVE_ERROR;
                    Log.d(TAG, resolverError);
                    resFlag_ = false;
                    e.printStackTrace();
                } finally {
                    Map<String, String> resMap = new HashMap<>();
                    if (resFlag_)
                        resMap.put(DATA_RESOLVER_OK, MSG_DATA_RETRIEVE_OK);
                    else
                        resMap.put(DATA_RESOLVER_ERROR, MSG_OOPS + resolverError);

                    networkResponseHandler.sendResponse(resMap);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Map<String, String> errorMap = new HashMap<>();

                if (error.getMessage() == null)
                    errorMap.put(DATA_RETRIEVE_ERROR, MSG_SERVER_RESPONSE_ERROR);
                else
                    errorMap.put(DATA_RETRIEVE_ERROR, error.getMessage());

                networkResponseHandler.sendResponse(errorMap);
                Log.e(TAG, error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, TAG_REQ_GET_CRICKET_MATCHES);

    }
}
