package com.muler.covid19;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue requestque;
    private static Context ctx;

    private MySingleton(Context context){
        ctx = context;
        requestque = getRequestque();
    }

    public static synchronized MySingleton getInstance(Context context){
if(mInstance ==null){
    mInstance = new MySingleton(context);

}
return mInstance;
    }

    public RequestQueue getRequestque(){
        if(requestque ==null){
requestque = Volley.newRequestQueue(ctx.getApplicationContext());
        }
       return requestque;
    }
    public <T>void addRequestque(Request<T> request){
                requestque.add(request);
    }
}
