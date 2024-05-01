package com.example.uea;

import com.android.volley.VolleyError;

public interface RegCertAsyncPost {
    void processFinished(String opLocation);
    void processFailed(VolleyError error);
}
