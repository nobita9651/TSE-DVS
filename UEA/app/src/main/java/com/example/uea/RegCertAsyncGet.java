package com.example.uea;

import com.android.volley.VolleyError;

public interface RegCertAsyncGet {
    void processFinished(RegCertInfo rcInfo);
    void processFailed(VolleyError error);
}
