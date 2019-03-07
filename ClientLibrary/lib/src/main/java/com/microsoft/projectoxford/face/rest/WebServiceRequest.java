//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.face.rest;

import com.google.gson.Gson;
import com.microsoft.projectoxford.face.common.RequestMethod;
import com.microsoft.projectoxford.face.common.ServiceError;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebServiceRequest {
    private static final String HEADER_KEY = "ocp-apim-subscription-key";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String OCTET_STREAM = "octet-stream";
    private static final String DATA = "data";

    private OkHttpClient mClient = new OkHttpClient();
    private String mSubscriptionKey;
    private Gson mGson = new Gson();

    public WebServiceRequest(String key) {
        this.mSubscriptionKey = key;
    }

    public Object request(String url, RequestMethod method, Map<String, Object> data, String contentType) throws ClientException, IOException {
        switch (method) {
            case GET:
                return get(url);
            case HEAD:
                break;
            case POST:
                return post(url, data, contentType);
            case PATCH:
                return patch(url, data, contentType);
            case DELETE:
                return delete(url, data);
            case PUT:
                return put(url, data);
            case OPTIONS:
                break;
            case TRACE:
                break;
        }

        return null;
    }

    private Object get(String url) throws ClientException, IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .header(HEADER_KEY, mSubscriptionKey)
                .build();

        Response response = this.mClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return readInput(response);
        } else {
            String json = readInput(response);
            if (json != null) {
                ServiceError error = mGson.fromJson(json, ServiceError.class);
                if (error != null) {
                    throw new ClientException(error.error);
                }
            }

            throw new ClientException("Error executing GET request!", response.code());
        }
    }


    private Object patch(String url, Map<String, Object> data, String contentType) throws ClientException, IOException {
        String json = mGson.toJson(data);

        Request request = new Request.Builder()
                .url(url)
                .header(HEADER_KEY, mSubscriptionKey)
                .patch(RequestBody.create(MediaType.get(APPLICATION_JSON), json))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .build();

        Response response = this.mClient.newCall(request).execute();

        if (response.isSuccessful()) {
            return readInput(response);
        } else {
            json = readInput(response);
            if (json != null) {
                ServiceError error = mGson.fromJson(json, ServiceError.class);
                if (error != null) {
                    throw new ClientException(error.error);
                }
            }

            throw new ClientException("Error executing Patch request!", response.code());
        }
    }


    private Object post(String url, Map<String, Object> data, String contentType) throws ClientException, IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header(HEADER_KEY, this.mSubscriptionKey);

        boolean isStream = false;

        if (contentType != null && !(contentType.length() == 0)) {
            builder.header(CONTENT_TYPE, contentType);

            if (contentType.toLowerCase(Locale.ENGLISH).contains(OCTET_STREAM)) {
                isStream = true;
            }
        } else {
            builder.header(CONTENT_TYPE, APPLICATION_JSON);
        }

        if (!isStream) {
            String json = mGson.toJson(data);
            builder.post(RequestBody.create(MediaType.get(APPLICATION_JSON), json));
        } else {
            builder.post(RequestBody.create(MediaType.get(contentType), (byte[]) data.get(DATA)));
        }

        Response response = mClient.newCall(builder.build()).execute();

        if (response.isSuccessful()) {
            return readInput(response);
        } else {
            String json = readInput(response);
            if (json != null) {
                ServiceError error = mGson.fromJson(json, ServiceError.class);
                if (error != null) {
                    throw new ClientException(error.error);
                }
            }

            throw new ClientException("Error executing POST request!", response.code());
        }
    }

    private Object put(String url, Map<String, Object> data) throws ClientException, IOException {
        String json = mGson.toJson(data);

        Request request = new Request.Builder()
                .url(url)
                .header(HEADER_KEY, mSubscriptionKey)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .put(RequestBody.create(MediaType.get(APPLICATION_JSON), json))
                .build();

        Response response = mClient.newCall(request).execute();

        if (response.isSuccessful()) {
            return readInput(response);
        } else {
            json = readInput(response);
            if (json != null) {
                ServiceError error = mGson.fromJson(json, ServiceError.class);
                if (error != null) {
                    throw new ClientException(error.error);
                }
            }

            throw new ClientException("Error executing PUT request!", response.code());
        }
    }


    private Object delete(String url, Map<String, Object> data) throws ClientException, IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header(HEADER_KEY, mSubscriptionKey);

        if (data == null || data.isEmpty()) {
            builder.delete();
        } else {
            String json = mGson.toJson(data);
            builder.delete(RequestBody.create(MediaType.get(APPLICATION_JSON), json));
            builder.header(CONTENT_TYPE, APPLICATION_JSON);
        }

        Response response = mClient.newCall(builder.build()).execute();

        if (!response.isSuccessful()) {
            String json = readInput(response);
            if (json != null) {
                ServiceError error = mGson.fromJson(json, ServiceError.class);
                if (error != null) {
                    throw new ClientException(error.error);
                }
            }

            throw new ClientException("Error executing DELETE request!", response.code());
        }

        return readInput(response);
    }


    public static String getUrl(String path, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(path);

        boolean start = true;
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (start) {
                url.append("?");
                start = false;
            } else {
                url.append("&");
            }

            try {
                url.append(param.getKey());
                url.append("=");
                url.append(URLEncoder.encode(param.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return url.toString();
    }

    private String readInput(Response response) throws IOException {
        if (response.body() == null) {
            return null;
        }

        return response.body().string();
    }
}
