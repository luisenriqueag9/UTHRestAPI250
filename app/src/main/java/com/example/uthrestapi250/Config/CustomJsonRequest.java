package com.example.uthrestapi250.Config;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CustomJsonRequest extends Request<JSONObject> {

    private final Response.Listener<JSONObject> listener;
    private final JSONObject requestBody;
    private final Map<String, String> headers;

    public CustomJsonRequest(int method, String url, JSONObject requestBody,
                             Map<String, String> headers,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.requestBody = requestBody;
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return requestBody == null ? null : requestBody.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            throw new AuthFailureError("Unsupported encoding", uee);
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(json), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}

