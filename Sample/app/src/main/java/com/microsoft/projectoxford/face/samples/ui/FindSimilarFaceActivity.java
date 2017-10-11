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
package com.microsoft.projectoxford.face.samples.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.SimilarFace;
import com.microsoft.projectoxford.face.contract.SimilarPersistedFace;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.ImageHelper;
import com.microsoft.projectoxford.face.samples.helper.LogHelper;
import com.microsoft.projectoxford.face.samples.helper.SampleApp;
import com.microsoft.projectoxford.face.samples.log.FindSimilarFaceLogActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class FindSimilarFaceActivity extends AppCompatActivity {

    private class FindLargeFaceListSimilarFaceTask extends AsyncTask<UUID, String, SimilarPersistedFace[]> {

        private boolean mSucceed = true;
        @Override
        protected SimilarPersistedFace[] doInBackground(UUID... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            addLog("Request: Find matchPerson similar faces to " + params[0] +
                    " in " + mLargeFaceListId);
            try{
                publishProgress("Finding Similar Faces...");

                // Start find similar faces.
                return faceServiceClient.findSimilarInLargeFaceList(
                        params[0],  /* The target face ID */
                        mLargeFaceListId,    /* large face list id */
                        4 /* max number of candidate returned*/
                );
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background find similar face task on screen.
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(SimilarPersistedFace[] result) {
            if (mSucceed) {
                String resultString = "Found "
                        + (result == null ? "0": result.length)
                        + " matchPerson similar persisted face" + ((result != null && result.length != 1)? "s": "");
                addLog("Response: Success. " + resultString);
                setInfo(resultString);
            }

            // Show the result on screen when verification is done.
            setUiAfterFindLargeFaceListSimilarFaces(result);
        }
    }

    private class FindLargeFaceListFaceSimilarFaceTask extends AsyncTask<UUID, String, SimilarPersistedFace[]> {

        private boolean mSucceed = true;
        @Override
        protected SimilarPersistedFace[] doInBackground(UUID... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            addLog("Request: Find matchPerson similar faces to " + params[0] +
                    " in " + mLargeFaceListId);
            try{
                publishProgress("Finding Similar Faces...");

                // Start find similar faces.
                return faceServiceClient.findSimilarInLargeFaceList(
                        params[0],  /* The target face ID */
                        mLargeFaceListId,    /* large face list id */
                        4, /* max number of candidate returned*/
                        FaceServiceClient.FindSimilarMatchMode.matchFace
                );
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background find similar face task on screen.
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(SimilarPersistedFace[] result) {
            if (mSucceed) {
                String resultString = "Found "
                        + (result == null ? "0": result.length)
                        + " matchFace similar persisted face" + ((result != null && result.length != 1)? "s": "");
                addLog("Response: Success. " + resultString);
                setInfo(resultString);
            }

            // Show the result on screen when verification is done.
            setUiAfterFindFaceLargeFaceListSimilarFaces(result);
        }
    }
    // Background task for face detection
    class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        private boolean mSucceed = true;
        int mRequestCode;
        DetectionTask(int requestCode) {
            mRequestCode = requestCode;
        }

        @Override
        protected Face[] doInBackground(InputStream... params) {
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Detecting...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            if (mSucceed) {
                addLog("Response: Success. Detected " + result.length + " Face(s) in image");
            }
            if (mRequestCode == REQUEST_SELECT_IMAGE) {
                setUiAfterDetectionForSelectImage(result);
            }
        }
    }

    class CreateLargeFaceListTask extends AsyncTask<String, String, String> {
        private boolean mSucceed = true;
        CreateLargeFaceListTask(String largeFaceListId) {
            mLargeFaceListId = largeFaceListId;
        }

        @Override
        protected String doInBackground(String... params) {
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                // Start detection.
                faceServiceClient.createLargeFaceList(mLargeFaceListId,"name","userData");
                return params[0];
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(String... values) {
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (mSucceed) {
                addLog("Response: Success. Create Large Face List " + result);
            }
        }
    }

    class AddFaceInLargeFaceListTask extends AsyncTask<InputStream, String, AddPersistedFaceResult> {

        private boolean mSucceed = true;
        int mRequestCode;
        InputStream[] p;
        AddFaceInLargeFaceListTask(int requestCode) {
            mRequestCode = requestCode;
        }

        @Override
        protected AddPersistedFaceResult doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Add face...");
                p = params;
                // Start detection.
                return faceServiceClient.AddFaceToLargeFaceList(
                        mLargeFaceListId,
                        params[0],  /* Input stream of image to detect */
                        "userData",       /* user data */
                        null);
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(AddPersistedFaceResult result) {
            if (mSucceed) {
                addLog("Response: Success. Add Face " + result + " in image");
                mProgressDialog.dismiss();
                setUiAfterAddFace(result);
            }
        }
    }

    class DeleteLargeFaceListTask extends AsyncTask<String, String, String> {

        private boolean mSucceed = true;

        @Override
        protected String doInBackground(String... params) {
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                // Start detection.
                addLog("Request: Delete Large Face List " + params[0]);
                faceServiceClient.deleteLargeFaceList(params[0]);
                return params[0];
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(String... values) {
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (mSucceed) {
                addLog("Response: Success. Delete Large Face List " + result);
            }
        }
    }

    class TrainLargeFaceListTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            addLog("Request: Training face list " + params[0]);

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                faceServiceClient.trainLargeFaceList(params[0]);
                return params[0];
            } catch (Exception e) {
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                addLog("Response: Success. Large face list " + result + " training completed");
            }
        }
    }

    void setUiAfterFindLargeFaceListSimilarFaces(SimilarPersistedFace[] result) {
        mProgressDialog.dismiss();

        setAllButtonsEnabledStatus(true);

        // Show the result of face finding similar faces.
        GridView similarFaces = (GridView) findViewById(R.id.similar_faces);
        mLargeSimilarFaceListAdapter = new SimilarLargeFaceListAdapter(result);
        similarFaces.setAdapter(mLargeSimilarFaceListAdapter);
    }

    void setUiAfterFindFaceLargeFaceListSimilarFaces(SimilarPersistedFace[] result) {
        mProgressDialog.dismiss();

        setAllButtonsEnabledStatus(true);

        // Show the result of face finding similar faces.
        GridView similarFaces = (GridView) findViewById(R.id.facial_similar_faces);
        mLargeSimilarFaceListAdapter = new SimilarLargeFaceListAdapter(result);
        similarFaces.setAdapter(mLargeSimilarFaceListAdapter);
    }

    void setUiDuringBackgroundTask(String progress) {
        mProgressDialog.setMessage(progress);

        setInfo(progress);
    }

    void setUiAfterAddFace(AddPersistedFaceResult result) {
        setAllButtonsEnabledStatus(true);

        // Show the detailed list of original faces.
        mFaceListAdapter.addFaces(null, result, mBitmap);

        GridView listView = (GridView) findViewById(R.id.all_faces);
        listView.setAdapter(mFaceListAdapter);

        TextView textView = (TextView) findViewById(R.id.text_all_faces);
        textView.setText(String.format(
                "Face database: %d face%s in total",
                mFaceListAdapter.faces.size(),
                mFaceListAdapter.faces.size() != 1 ? "s" : ""));

        refreshFindSimilarFaceButtonEnabledStatus();

        mBitmap = null;

        // Set the status bar.
        setDetectionStatus();
    }

    void setUiAfterDetectionForSelectImage(Face[] result) {
        setAllButtonsEnabledStatus(true);

        // Show the detailed list of detected faces.
        mTargetFaceListAdapter = new FaceListAdapter();
        mTargetFaceListAdapter.addFaces(result, null, mTargetBitmap);

        // Show the list of detected face thumbnails.
        ListView listView = (ListView) findViewById(R.id.list_faces);
        listView.setAdapter(mTargetFaceListAdapter);

        // Set the default face ID to the ID of first face, if one or more faces are detected.
        if (mTargetFaceListAdapter.faces.size() != 0) {
            mFaceId = mTargetFaceListAdapter.faces.get(0);
            // Show the thumbnail of the default face.
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(mTargetFaceListAdapter.faceThumbnails.get(0));
        }

        refreshFindSimilarFaceButtonEnabledStatus();

        mTargetBitmap = null;

        // Set the status bar.
        setDetectionStatus();
    }

    private void setDetectionStatus() {
        if (mBitmap == null && mTargetBitmap == null) {
            mProgressDialog.dismiss();
            setInfo("Detection is done");
        } else {
            mProgressDialog.setMessage("Detecting...");
            setInfo("Detecting...");
        }
    }

    // The faces in this image are added to the face collection in which to find similar faces.
    Bitmap mBitmap;

    // The faces in this image are added to the face collection in which to find similar faces.
    Bitmap mTargetBitmap;

    // The face collection view adapter.
    FaceListAdapter mFaceListAdapter;

    // The face collection view adapter.
    FaceListAdapter mTargetFaceListAdapter;

    // The face collection view adapter.
    SimilarLargeFaceListAdapter mLargeSimilarFaceListAdapter;

    // Flag to indicate which task is to be performed.
    protected static final int REQUEST_ADD_FACE = 0;

    // Flag to indicate which task is to be performed.
    protected static final int REQUEST_SELECT_IMAGE = 1;

    // The ID of the target face to find similar face.
    private UUID mFaceId;

    private String mLargeFaceListId;

    // Progress dialog popped up when communicating with server.
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_similar_face);

        mFaceListAdapter = new FaceListAdapter();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.progress_dialog_title));

        initializeFaceList();

        setFindSimilarFaceButtonEnabledStatus(false);

        mLargeFaceListId = UUID.randomUUID().toString();

        new CreateLargeFaceListTask(mLargeFaceListId).execute(mLargeFaceListId);

        LogHelper.clearFindSimilarFaceLog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_FACE) {
            if(resultCode == RESULT_OK) {
                mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                        data.getData(), getContentResolver());
                if (mBitmap != null) {
                    View originalFaces = findViewById(R.id.all_faces);
                    originalFaces.setVisibility(View.VISIBLE);

                    // Put the image into an input stream for detection.
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    ByteArrayInputStream inputStream
                            = new ByteArrayInputStream(output.toByteArray());

                    setAllButtonsEnabledStatus(false);

                    addLog("Request: Add Face " + data.getData());
                    // Start a background task to add face in the large face list.
                    new AddFaceInLargeFaceListTask(REQUEST_ADD_FACE).execute(inputStream);
                }
            }
        } else if (requestCode == REQUEST_SELECT_IMAGE) {
            if(resultCode == RESULT_OK) {
                mTargetBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                        data.getData(), getContentResolver());
                if (mTargetBitmap != null) {
                    View originalFaces = findViewById(R.id.all_faces);
                    originalFaces.setVisibility(View.VISIBLE);

                    // Put the image into an input stream for detection.
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    mTargetBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    ByteArrayInputStream inputStream
                            = new ByteArrayInputStream(output.toByteArray());

                    setAllButtonsEnabledStatus(false);

                    addLog("Request: Detecting target in image " + data.getData());
                    // Start a background task to detect faces in the image.
                    new DetectionTask(REQUEST_SELECT_IMAGE).execute(inputStream);
                }
            }
        }
    }

    // Set whether the buttons are enabled.
    private void setAllButtonsEnabledStatus(boolean isEnabled) {
        Button addFaceButton = (Button) findViewById(R.id.add_faces);
        addFaceButton.setEnabled(isEnabled);

        Button selectImageButton = (Button) findViewById(R.id.select_image);
        selectImageButton.setEnabled(isEnabled);

        Button detectButton = (Button) findViewById(R.id.find_similar_faces);
        detectButton.setEnabled(isEnabled);

        Button logButton = (Button) findViewById(R.id.view_log);
        logButton.setEnabled(isEnabled);
    }

    // Set the group button is enabled or not.
    private void setFindSimilarFaceButtonEnabledStatus(boolean isEnabled) {
        Button button = (Button) findViewById(R.id.find_similar_faces);
        button.setEnabled(isEnabled);
    }

    // Set the group button is enabled or not.
    private void refreshFindSimilarFaceButtonEnabledStatus() {
        if (mFaceListAdapter.faces.size() != 0 && mFaceId != null) {
            setFindSimilarFaceButtonEnabledStatus(true);
        } else {
            setFindSimilarFaceButtonEnabledStatus(false);
        }
    }

    // Initialize the ListView which contains the thumbnails of the detected faces.
    private void initializeFaceList() {
        ListView listView = (ListView) findViewById(R.id.list_faces);

        // When a detected face in the GridView is clicked, the face is selected to verify.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FaceListAdapter faceListAdapter = mTargetFaceListAdapter;

                if (!faceListAdapter.faces.get(position).equals(mFaceId)) {
                    mFaceId = faceListAdapter.faces.get(position);

                    ImageView imageView = (ImageView) findViewById(R.id.image);
                    imageView.setImageBitmap(faceListAdapter.faceThumbnails.get(position));

                    // Clear the result of finding similar faces.
                    GridView similarFaces = (GridView) findViewById(R.id.similar_faces);
                    mLargeSimilarFaceListAdapter = new SimilarLargeFaceListAdapter(null);
                    similarFaces.setAdapter(mLargeSimilarFaceListAdapter);

                    similarFaces = (GridView) findViewById(R.id.facial_similar_faces);
                    mLargeSimilarFaceListAdapter = new SimilarLargeFaceListAdapter(null);
                    similarFaces.setAdapter(mLargeSimilarFaceListAdapter);

                    setInfo("");
                }

                // Show the list of detected face thumbnails.
                ListView listView = (ListView) findViewById(R.id.list_faces);
                listView.setAdapter(faceListAdapter);
            }
        });
        new DeleteLargeFaceListTask().execute(mLargeFaceListId);
    }

    public void addFaces(View view) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_ADD_FACE);
    }

    public void findSimilarFaces(View view) {
        if (mFaceId == null || mFaceListAdapter.faces.size() == 0) {
            setInfo("Parameters are not ready");
        }

        setAllButtonsEnabledStatus(false);
        new TrainLargeFaceListTask().execute(mLargeFaceListId);
        new FindLargeFaceListSimilarFaceTask().execute(mFaceId);
        new FindLargeFaceListFaceSimilarFaceTask().execute(mFaceId);
    }

    public void viewLog(View view) {
        Intent intent = new Intent(this, FindSimilarFaceLogActivity.class);
        startActivity(intent);
    }

    public void selectImage(View view) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Set the information panel on screen.
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }

    // Append the information panel on screen.
    private void appendInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        String str = (String)textView.getText();
        textView.setText(str + ',' + info);
    }
    // Add a log item.
    private void addLog(String log) {
        LogHelper.addFindSimilarFaceLog(log);
    }

    // The adapter of the GridView which contains the thumbnails of the detected faces.
    private class FaceListAdapter extends BaseAdapter {
        // The detected faces.
        List<UUID> faces;

        // The thumbnails of detected faces.
        List<Bitmap> faceThumbnails;

        Map<UUID, Bitmap> faceIdThumbnailMap;

        FaceListAdapter() {
            faces = new ArrayList<>();
            faceThumbnails = new ArrayList<>();
            faceIdThumbnailMap = new HashMap<>();
        }

        public void addFaces(Face[] detectionResult, AddPersistedFaceResult persistedFaceResult, Bitmap bitmap) {
            if (detectionResult != null) {
                List<Face> detectedFaces = Arrays.asList(detectionResult);
                for (Face face: detectedFaces) {
                    faces.add(face.faceId);
                    try {
                        Bitmap faceThumbnail =ImageHelper.generateFaceThumbnail(
                                bitmap, face.faceRectangle);
                        faceThumbnails.add(faceThumbnail);
                        faceIdThumbnailMap.put(face.faceId, faceThumbnail);
                    } catch (IOException e) {
                        // Show the exception when generating face thumbnail fails.
                        setInfo(e.getMessage());
                    }
                }
            }
            else if(persistedFaceResult != null) {
                faces.add(persistedFaceResult.persistedFaceId);
                Bitmap faceThumbnail = bitmap;
                faceThumbnails.add(faceThumbnail);
                faceIdThumbnailMap.put(persistedFaceResult.persistedFaceId, faceThumbnail);

            }
        }

        @Override
        public int getCount() {
            return faces.size();
        }

        @Override
        public Object getItem(int position) {
            return faces.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_face, parent, false);
            }
            convertView.setId(position);

            Bitmap thumbnailToShow = faceThumbnails.get(position);
            if (faces.get(position).equals(mFaceId)) {
                thumbnailToShow = ImageHelper.highlightSelectedFaceThumbnail(thumbnailToShow);
            }

            // Show the face thumbnail.
            ((ImageView)convertView.findViewById(R.id.image_face)).setImageBitmap(thumbnailToShow);

            return convertView;
        }
    }

    // The adapter of the GridView which contains the details of the detected faces.
    private class SimilarLargeFaceListAdapter extends BaseAdapter {
        // The detected faces.
        List<SimilarPersistedFace> similarFaces;

        // Initialize with detection result.
        SimilarLargeFaceListAdapter(SimilarPersistedFace[] findSimilarFaceResult) {
            if (findSimilarFaceResult != null) {
                similarFaces = Arrays.asList(findSimilarFaceResult);
            } else {
                similarFaces = new ArrayList<>();
            }
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return similarFaces.size();
        }

        @Override
        public Object getItem(int position) {
            return similarFaces.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_face, parent, false);
            }
            convertView.setId(position);

            // Show the face thumbnail.
            ((ImageView)convertView.findViewById(R.id.image_face)).setImageBitmap(
                    mFaceListAdapter.faceIdThumbnailMap.get(similarFaces.get(position).persistedFaceId));

            return convertView;
        }
    }
}
