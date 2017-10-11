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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.ImageHelper;
import com.microsoft.projectoxford.face.samples.helper.LogHelper;
import com.microsoft.projectoxford.face.samples.helper.SampleApp;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.log.VerificationLogActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PersonVerificationActivity extends AppCompatActivity {
    // Background task for face verification.
    private class VerificationTask extends AsyncTask<Void, String, VerifyResult> {
        // The IDs of two face to verify.
        private UUID mFaceId;
        private UUID mPersonId;
        private String mPersonGroupId;

        VerificationTask (UUID faceId, String personGroupId, UUID personId1) {
            mFaceId = faceId;
            mPersonGroupId = personGroupId;
            mPersonId = personId1;
        }

        @Override
        protected VerifyResult doInBackground(Void... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Verifying...");

                // Start verification.
                return faceServiceClient.verifyInLargePersonGroup(
                        mFaceId,      /* The face ID to verify */
                        mPersonGroupId, /* The person group ID of the person*/
                        mPersonId);     /* The person ID to verify */
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            addLog("Request: Verifying face " + PersonVerificationActivity.this.mFaceId + " and person " + mPersonId);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            progressDialog.setMessage(progress[0]);
            setInfo(progress[0]);
        }

        @Override
        protected void onPostExecute(VerifyResult result) {
            if (result != null) {
                addLog("Response: Success. Face " + PersonVerificationActivity.this.mFaceId + " "
                        + mPersonId + (result.isIdentical ? " " : " don't ")
                        + "belong to person "+ PersonVerificationActivity.this.mPersonId);
            }

            // Show the result on screen when verification is done.
            setUiAfterVerification(result);
        }
    }

    // Background task of face detection.
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        // Index indicates detecting in which of the two images.
        private boolean mSucceed = true;

        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
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
            progressDialog.show();
            addLog("Request: Detecting in image");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            progressDialog.setMessage(progress[0]);
            setInfo(progress[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            // Show the result on screen when detection is done.
            setUiAfterDetection(result, mSucceed);
        }
    }

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The IDs of the two faces to be verified.
    private UUID mFaceId;

    // The two images from where we get the two faces to verify.
    private Bitmap mBitmap;

    // The adapter of the ListView which contains the detected faces from the two images.
    protected FaceListAdapter mFaceListAdapter;

    // Progress dialog popped up when communicating with server.
    ProgressDialog progressDialog;

    String mPersonGroupId;
    UUID mPersonId;

    PersonListAdapter mPersonListAdapter;

    // When the activity is created, set all the member variables to initial state.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_person);
        // Initialize the two ListViews which contain the thumbnails of the detected faces.
        initializeFaceList();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.progress_dialog_title));

        clearDetectedFaces();

        // Disable button "verify" as the two face IDs to verify are not ready.
        setVerifyButtonEnabledStatus(false);

        LogHelper.clearVerificationLog();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ListView listView = (ListView) findViewById(R.id.list_persons);
        mPersonListAdapter = new PersonListAdapter();
        listView.setAdapter(mPersonListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setPersonSelected(position);
            }
        });

        if (mPersonListAdapter.personIdList.size() != 0) {
            setPersonSelected(0);
        } else {
            setPersonSelected(-1);
        }
    }

    // select a person for verification
    void setPersonSelected(int position) {
        TextView textView = (TextView) findViewById(R.id.text_person_selected);
        if (position > 0) {
            String personGroupIdSelected = mPersonListAdapter.personGroupIds.get(position);
            mPersonListAdapter.personGroupIds.set(
                    position, mPersonListAdapter.personGroupIds.get(0));
            mPersonListAdapter.personGroupIds.set(0, personGroupIdSelected);

            String personIdSelected = mPersonListAdapter.personIdList.get(position);
            mPersonListAdapter.personIdList.set(
                    position, mPersonListAdapter.personIdList.get(0));
            mPersonListAdapter.personIdList.set(0, personIdSelected);

            ListView listView = (ListView) findViewById(R.id.list_persons);
            listView.setAdapter(mPersonListAdapter);
            setPersonSelected(0);
        } else if (position < 0) {
            setVerifyButtonEnabledStatus(false);
            textView.setTextColor(Color.RED);
            textView.setText("no person selected for verification warning");
        } else {
            mPersonGroupId = mPersonListAdapter.personGroupIds.get(0);
            mPersonId =  UUID.fromString(mPersonListAdapter.personIdList.get(0));
            String personName = StorageHelper.getPersonName(mPersonId.toString(), mPersonGroupId,
                    PersonVerificationActivity.this);
            refreshVerifyButtonEnabledStatus();
            textView.setTextColor(Color.BLACK);
            textView.setText(String.format("Person to use: %s", personName));
        }
    }

    private void refreshVerifyButtonEnabledStatus() {
        if (mFaceId != null && mPersonId != null) {
            setVerifyButtonEnabledStatus(true);
        } else {
            setVerifyButtonEnabledStatus(false);
        }
    }
    // Called when image selection is done. Begin detecting if the image is selected successfully.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Index indicates which of the two images is selected.
        if (requestCode != REQUEST_SELECT_IMAGE) {
            return;
        }

        if(resultCode == RESULT_OK) {
            // If image is selected successfully, set the image URI and bitmap.
            Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    data.getData(), getContentResolver());
            if (bitmap != null) {
                // Image is select but not detected, disable verification button.
                setVerifyButtonEnabledStatus(false);
                clearDetectedFaces();

                // Set the image to detect
                mBitmap = bitmap;
                mFaceId = null;

                // Add verification log.
                addLog("Image"  + ": " + data.getData() + " resized to " + bitmap.getWidth()
                        + "x" + bitmap.getHeight());

                // Start detecting in image.
                detect(bitmap);
            }
        }
    }

    // Clear the detected faces indicated by index.
    private void clearDetectedFaces() {
        ListView faceList = (ListView) findViewById(R.id.list_faces_0);
        faceList.setVisibility(View.GONE);

        ImageView imageView = (ImageView) findViewById(R.id.image_0);
        imageView.setImageResource(android.R.color.transparent);
    }

    // Called when the "Select Image" button is clicked in face face verification.
    public void selectImage(View view) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when the "Verify" button is clicked.
    public void verify(View view) {
        setAllButtonEnabledStatus(false);
        new VerificationTask(mFaceId, mPersonGroupId, mPersonId).execute();
    }

    // View the log of service calls.
    public void viewLog(View view) {
        Intent intent = new Intent(this, VerificationLogActivity.class);
        startActivity(intent);
    }

    // Set the select image button is enabled or not.
    private void setSelectImageButtonEnabledStatus(boolean isEnabled) {
        Button button = (Button) findViewById(R.id.select_image_0);
        button.setEnabled(isEnabled);

        Button viewLog = (Button) findViewById(R.id.view_log);
        viewLog.setEnabled(isEnabled);
    }

    // Set the verify button is enabled or not.
    private void setVerifyButtonEnabledStatus(boolean isEnabled) {
            Button button = (Button) findViewById(R.id.verify);
            button.setEnabled(isEnabled);
    }

    // Set all the buttons are enabled or not.
    private void setAllButtonEnabledStatus(boolean isEnabled) {
        Button selectImage0 = (Button) findViewById(R.id.select_image_0);
        selectImage0.setEnabled(isEnabled);

        Button selectImage1 = (Button) findViewById(R.id.manage_persons);
        selectImage1.setEnabled(isEnabled);

        Button verify = (Button) findViewById(R.id.verify);
        verify.setEnabled(isEnabled);

        Button viewLog = (Button) findViewById(R.id.view_log);
        viewLog.setEnabled(isEnabled);
    }

    // Initialize the ListView which contains the thumbnails of the detected faces.
    private void initializeFaceList() {
        ListView listView =
                (ListView) findViewById(R.id.list_faces_0);

        // When a detected face in the GridView is clicked, the face is selected to verify.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FaceListAdapter faceListAdapter = mFaceListAdapter;

                if (!faceListAdapter.faces.get(position).faceId.equals(mFaceId)) {
                    mFaceId = faceListAdapter.faces.get(position).faceId;

                    ImageView imageView =
                            (ImageView) findViewById( R.id.image_0);
                    imageView.setImageBitmap(faceListAdapter.faceThumbnails.get(position));

                    setInfo("");
                }

                // Show the list of detected face thumbnails.
                ListView listView = (ListView) findViewById(R.id.list_faces_0);
                listView.setAdapter(faceListAdapter);
            }
        });
    }

    // Show the result on screen when verification is done.
    private void setUiAfterVerification(VerifyResult result) {
        // Verification is done, hide the progress dialog.
        progressDialog.dismiss();

        // Enable all the buttons.
        setAllButtonEnabledStatus(true);

        // Show verification result.
        if (result != null) {
            DecimalFormat formatter = new DecimalFormat("#0.00");
            String verificationResult = (result.isIdentical ? "The same person": "Different persons")
                    + ". The confidence is " + formatter.format(result.confidence);
            setInfo(verificationResult);
        }
    }

    // Show the result on screen when detection in image that indicated by index is done.
    private void setUiAfterDetection(Face[] result,boolean succeed) {
        setSelectImageButtonEnabledStatus(true);

        if (succeed) {
            addLog("Response: Success. Detected "
                    + result.length + " face(s) in image");

            setInfo(result.length + " face" + (result.length != 1 ? "s": "")  + " detected");

            // Show the detailed list of detected faces.
            FaceListAdapter faceListAdapter = new FaceListAdapter(result);

            // Set the default face ID to the ID of first face, if one or more faces are detected.
            if (faceListAdapter.faces.size() != 0) {

                mFaceId = faceListAdapter.faces.get(0).faceId;

                // Show the thumbnail of the default face.
                ImageView imageView = (ImageView) findViewById(R.id.image_0);
                imageView.setImageBitmap(faceListAdapter.faceThumbnails.get(0));

                refreshVerifyButtonEnabledStatus();
            }

            // Show the list of detected face thumbnails.
            ListView listView = (ListView) findViewById( R.id.list_faces_0);
            listView.setAdapter(faceListAdapter);
            listView.setVisibility(View.VISIBLE);

            // Set the face list adapters and bitmaps.
            mFaceListAdapter = faceListAdapter;
            mBitmap = null;
        }

        if (result != null && result.length == 0) {
            setInfo("No face detected!");
        }

        progressDialog.dismiss();

        if (mFaceId != null && mPersonGroupId != null) {
            setVerifyButtonEnabledStatus(true);
        }
    }

    // Start detecting in image specified by index.
    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);

        setSelectImageButtonEnabledStatus(false);

        // Set the status to show that detection starts.
        setInfo("Detecting...");
    }

    // Set the information panel on screen.
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }

    // Add a log item.
    private void addLog(String log) {
        LogHelper.addVerificationLog(log);
    }

    // The adapter of the GridView which contains the thumbnails of the detected faces.
    private class FaceListAdapter extends BaseAdapter {
        // The detected faces.
        List<Face> faces;

        // The thumbnails of detected faces.
        List<Bitmap> faceThumbnails;

        // Initialize with detection result and index indicating on which image the result is got.
        FaceListAdapter(Face[] detectionResult) {
            faces = new ArrayList<>();
            faceThumbnails = new ArrayList<>();

            if (detectionResult != null) {
                faces = Arrays.asList(detectionResult);
                for (Face face: faces) {
                    try {
                        // Crop face thumbnail without landmarks drawn.
                        faceThumbnails.add(ImageHelper.generateFaceThumbnail(mBitmap, face.faceRectangle));
                    } catch (IOException e) {
                        // Show the exception when generating face thumbnail fails.
                        setInfo(e.getMessage());
                    }
                }
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
                LayoutInflater layoutInflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_face, parent, false);
            }
            convertView.setId(position);

            Bitmap thumbnailToShow = faceThumbnails.get(position);
            if (faces.get(position).faceId.equals(mFaceId)) {
                thumbnailToShow = ImageHelper.highlightSelectedFaceThumbnail(thumbnailToShow);
            }

            // Show the face thumbnail.
            ((ImageView)convertView.findViewById(R.id.image_face)).setImageBitmap(thumbnailToShow);

            return convertView;
        }
    }

    //manage persons button click.
    public void managePersons(View view) {
        Intent intent = new Intent(this, PersonGroupListActivity.class);
        startActivity(intent);

        if (mFaceId != null && mPersonId != null) {
            setVerifyButtonEnabledStatus(true);
        }
        else
        {
            setVerifyButtonEnabledStatus(false);
        }
    }

    // The adapter of the ListView which contains the person groups.
    private class PersonListAdapter extends BaseAdapter {
        List<String> personIdList;
        List<String> personGroupIds;

        // Initialize with all the persons of all person groups..
        PersonListAdapter() {
            personIdList = new ArrayList<>();
            personGroupIds = new ArrayList<>();

            Set<String> personGroups = StorageHelper.getAllPersonGroupIds(PersonVerificationActivity.this);

            int index = 0;
            for (String personGroupId: personGroups) {
                personIdList.addAll(StorageHelper.getAllPersonIds(personGroupId, PersonVerificationActivity.this));
                for(int i = index; i<personIdList.size(); ++i)
                {
                    personGroupIds.add(personGroupId);
                }
                index = personIdList.size();
            }
        }

        @Override
        public int getCount() {
            return personIdList.size();
        }

        @Override
        public Object getItem(int position) {
            return new String[]{personIdList.get(position), personGroupIds.get(position)};
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
                convertView = layoutInflater.inflate(R.layout.item_person_group, parent, false);
            }
            convertView.setId(position);

            // set the text of the item
            String personName = StorageHelper.getPersonName(
                    personIdList.get(position), personGroupIds.get(position), PersonVerificationActivity.this);
            String personGroupName = StorageHelper.getPersonGroupName( personGroupIds.get(position), PersonVerificationActivity.this);
            ((TextView)convertView.findViewById(R.id.text_person_group)).setText(
                    String.format(
                            "%s - %s",
                            personGroupName,
                            personName
                            ));

            if (position == 0) {
                ((TextView)convertView.findViewById(R.id.text_person_group)).setTextColor(
                        Color.parseColor("#3399FF"));
            }

            return convertView;
        }
    }
}
