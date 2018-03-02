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
package com.microsoft.projectoxford.face;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.microsoft.projectoxford.face.common.RequestMethod;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceList;
import com.microsoft.projectoxford.face.contract.FaceListMetadata;
import com.microsoft.projectoxford.face.contract.FaceMetadata;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.GroupResult;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.LargeFaceList;
import com.microsoft.projectoxford.face.contract.LargePersonGroup;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.PersonFace;
import com.microsoft.projectoxford.face.contract.PersonGroup;
import com.microsoft.projectoxford.face.contract.SimilarFace;
import com.microsoft.projectoxford.face.contract.SimilarPersistedFace;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.contract.VerifyResult;
import com.microsoft.projectoxford.face.rest.ClientException;
import com.microsoft.projectoxford.face.rest.WebServiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FaceServiceRestClient implements FaceServiceClient {
    private final WebServiceRequest mRestCall;
    private Gson mGson = new GsonBuilder().setDateFormat("MM/dd/yyyy HH:mm:ss").create();

    private static final String DEFAULT_API_ROOT = "https://westus.api.cognitive.microsoft.com/face/v1.0";
    private final String mServiceHost;

    private static final String DETECT_QUERY = "detect";
    private static final String VERIFY_QUERY = "verify";
    private static final String TRAIN_QUERY = "train";
    private static final String TRAINING_QUERY = "training";
    private static final String IDENTIFY_QUERY = "identify";
    private static final String PERSON_GROUPS_QUERY = "persongroups";
    private static final String LARGE_PERSON_GROUPS_QUERY = "largepersongroups";
    private static final String PERSONS_QUERY = "persons";
    private static final String FACE_LISTS_QUERY = "facelists";
    private static final String LARGE_FACE_LISTS_QUERY = "largefacelists";
    private static final String PERSISTED_FACES_QUERY = "persistedfaces";
    private static final String GROUP_QUERY = "group";
    private static final String FIND_SIMILARS_QUERY = "findsimilars";
    private static final String STREAM_DATA = "application/octet-stream";
    private static final String DATA = "data";

    public FaceServiceRestClient(String subscriptionKey) {
        this(DEFAULT_API_ROOT, subscriptionKey);
    }

    public FaceServiceRestClient(String serviceHost, String subscriptionKey) {
        mServiceHost = serviceHost.replaceAll("/$", "");
        mRestCall = new WebServiceRequest(subscriptionKey);
    }

    /*
    * =============================================================
    * ============================== Face =========================
    * =============================================================
    */

    @Override
    public Face[] detect(String url, boolean returnFaceId, boolean returnFaceLandmarks, FaceAttributeType[] returnFaceAttributes) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("returnFaceId", returnFaceId);
        params.put("returnFaceLandmarks", returnFaceLandmarks);
        if (returnFaceAttributes != null && returnFaceAttributes.length > 0) {
            StringBuilder faceAttributesStringBuilder = new StringBuilder();
            boolean firstAttribute = true;
            for (FaceAttributeType faceAttributeType: returnFaceAttributes)
            {
                if (firstAttribute) {
                    firstAttribute = false;
                } else {
                    faceAttributesStringBuilder.append(",");
                }

                faceAttributesStringBuilder.append(faceAttributeType);
            }
            params.put("returnFaceAttributes", faceAttributesStringBuilder.toString());
        }

        String path = String.format("%s/%s", mServiceHost, DETECT_QUERY);
        String uri = WebServiceRequest.getUrl(path, params);

        params.clear();
        params.put("url", url);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        Type listType = new TypeToken<List<Face>>() {
        }.getType();
        List<Face> faces = mGson.fromJson(json, listType);

        return faces.toArray(new Face[faces.size()]);
    }

    @Override
    public Face[] detect(InputStream imageStream, boolean returnFaceId, boolean returnFaceLandmarks, FaceAttributeType[] returnFaceAttributes) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("returnFaceId", returnFaceId);
        params.put("returnFaceLandmarks", returnFaceLandmarks);
        if (returnFaceAttributes != null && returnFaceAttributes.length > 0) {
            StringBuilder faceAttributesStringBuilder = new StringBuilder();
            boolean firstAttribute = true;
            for (FaceAttributeType faceAttributeType: returnFaceAttributes)
            {
                if (firstAttribute) {
                    firstAttribute = false;
                } else {
                    faceAttributesStringBuilder.append(",");
                }

                faceAttributesStringBuilder.append(faceAttributeType);
            }
            params.put("returnFaceAttributes", faceAttributesStringBuilder.toString());
        }

        String path = String.format("%s/%s", mServiceHost, DETECT_QUERY);
        String uri = WebServiceRequest.getUrl(path, params);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] bytes = new byte[1024];
        while ((bytesRead = imageStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, bytesRead);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        params.clear();
        params.put(DATA, data);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, STREAM_DATA);
        Type listType = new TypeToken<List<Face>>() {
        }.getType();
        List<Face> faces = mGson.fromJson(json, listType);

        return faces.toArray(new Face[faces.size()]);
    }

    @Override
    public VerifyResult verify(UUID faceId1, UUID faceId2) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s", mServiceHost, VERIFY_QUERY);
        params.put("faceId1", faceId1);
        params.put("faceId2", faceId2);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, VerifyResult.class);
    }

    @Override
    public VerifyResult verify(UUID faceId, String personGroupId, UUID personId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s", mServiceHost, VERIFY_QUERY);
        params.put("faceId", faceId);
        params.put("personGroupId", personGroupId);
        params.put("personId", personId);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, VerifyResult.class);
    }

    @Override
    public VerifyResult verifyInPersonGroup(UUID faceId, String personGroupId, UUID personId) throws ClientException, IOException {
        return verify(faceId,personGroupId,personId);
    }

    @Override
    public VerifyResult verifyInLargePersonGroup(UUID faceId, String largePersonGroupId, UUID personId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s", mServiceHost, VERIFY_QUERY);
        params.put("faceId", faceId);
        params.put("largePersonGroupId", largePersonGroupId);
        params.put("personId", personId);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, VerifyResult.class);
    }

    @Override
    public IdentifyResult[] identity(String personGroupId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        return identity(personGroupId, faceIds, 0.5f, maxNumOfCandidatesReturned);
    }

    @Override
    public IdentifyResult[] identity(String personGroupId, UUID[] faceIds, float confidenceThreshold, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s", mServiceHost, IDENTIFY_QUERY);
        params.put("personGroupId", personGroupId);
        params.put("faceIds", faceIds);
        params.put("maxNumOfCandidatesReturned", maxNumOfCandidatesReturned);
        params.put("confidenceThreshold", confidenceThreshold);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        Type listType = new TypeToken<List<IdentifyResult>>() {
        }.getType();
        List<IdentifyResult> result = mGson.fromJson(json, listType);

        return result.toArray(new IdentifyResult[result.size()]);
    }

    @Override
    public IdentifyResult[] identityInPersonGroup(String personGroupId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        return identity(personGroupId, faceIds, 0.5f, maxNumOfCandidatesReturned);
    }

    @Override
    public IdentifyResult[] identityInPersonGroup(String personGroupId, UUID[] faceIds, float confidenceThreshold, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        return identity(personGroupId, faceIds, confidenceThreshold, maxNumOfCandidatesReturned);
    }

    @Override
    public IdentifyResult[] identityInLargePersonGroup(String largePersonGroupId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        return identityInLargePersonGroup(largePersonGroupId, faceIds, 0.5f, maxNumOfCandidatesReturned);
    }

    @Override
    public IdentifyResult[] identityInLargePersonGroup(String largePersonGroupId, UUID[] faceIds, float confidenceThreshold, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s", mServiceHost, IDENTIFY_QUERY);
        params.put("largePersonGroupId", largePersonGroupId);
        params.put("faceIds", faceIds);
        params.put("maxNumOfCandidatesReturned", maxNumOfCandidatesReturned);
        params.put("confidenceThreshold", confidenceThreshold);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        Type listType = new TypeToken<List<IdentifyResult>>() {
        }.getType();
        List<IdentifyResult> result = mGson.fromJson(json, listType);

        return result.toArray(new IdentifyResult[result.size()]);
    }

    @Override
    public SimilarFace[] findSimilar(UUID faceId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        return findSimilar(faceId, faceIds, maxNumOfCandidatesReturned, FindSimilarMatchMode.matchPerson);
    }

    @Override
    public SimilarFace[] findSimilar(UUID faceId, UUID[] faceIds, int maxNumOfCandidatesReturned, FindSimilarMatchMode mode) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s", mServiceHost, FIND_SIMILARS_QUERY);
        params.put("faceId", faceId);
        params.put("faceIds", faceIds);
        params.put("maxNumOfCandidatesReturned", maxNumOfCandidatesReturned);
        params.put("mode", mode.toString());
        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        Type listType = new TypeToken<List<SimilarFace>>() {
        }.getType();
        List<SimilarFace> result = mGson.fromJson(json, listType);
        return result.toArray(new SimilarFace[result.size()]);
    }

    @Override
    public SimilarPersistedFace[] findSimilar(UUID faceId, String faceListId, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        return findSimilar(faceId, faceListId, maxNumOfCandidatesReturned, FindSimilarMatchMode.matchPerson);
    }

    @Override
    public SimilarPersistedFace[] findSimilar(UUID faceId, String faceListId, int maxNumOfCandidatesReturned, FindSimilarMatchMode mode) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s", mServiceHost, FIND_SIMILARS_QUERY);
        params.put("faceId", faceId);
        params.put("faceListId", faceListId);
        params.put("maxNumOfCandidatesReturned", maxNumOfCandidatesReturned);
        params.put("mode", mode.toString());
        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        Type listType = new TypeToken<List<SimilarPersistedFace>>() {
        }.getType();
        List<SimilarPersistedFace> result = mGson.fromJson(json, listType);
        return result.toArray(new SimilarPersistedFace[result.size()]);
    }

    @Override
    public SimilarPersistedFace[] findSimilarInFaceList(UUID faceId, String faceListId, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        return findSimilar(faceId, faceListId, maxNumOfCandidatesReturned, FindSimilarMatchMode.matchPerson);
    }

    @Override
    public SimilarPersistedFace[] findSimilarInFaceList(UUID faceId, String faceListId, int maxNumOfCandidatesReturned, FindSimilarMatchMode mode) throws ClientException, IOException {
        return findSimilar(faceId, faceListId, maxNumOfCandidatesReturned, mode);
    }

    @Override
    public SimilarPersistedFace[] findSimilarInLargeFaceList(UUID faceId, String largeFaceListId, int maxNumOfCandidatesReturned) throws ClientException, IOException {
        return findSimilarInLargeFaceList(faceId, largeFaceListId, maxNumOfCandidatesReturned, FindSimilarMatchMode.matchPerson);
    }

    @Override
    public SimilarPersistedFace[] findSimilarInLargeFaceList(UUID faceId, String largeFaceListId, int maxNumOfCandidatesReturned, FindSimilarMatchMode mode) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s", mServiceHost, FIND_SIMILARS_QUERY);
        params.put("faceId", faceId);
        params.put("largeFaceListId", largeFaceListId);
        params.put("maxNumOfCandidatesReturned", maxNumOfCandidatesReturned);
        params.put("mode", mode.toString());
        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        Type listType = new TypeToken<List<SimilarPersistedFace>>() {
        }.getType();
        List<SimilarPersistedFace> result = mGson.fromJson(json, listType);
        return result.toArray(new SimilarPersistedFace[result.size()]);
    }

    @Override
    public GroupResult group(UUID[] faceIds) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s", mServiceHost, GROUP_QUERY);
        params.put("faceIds", faceIds);
        String json = (String)this.mRestCall.request(uri, RequestMethod.POST, params, null);
        return this.mGson.fromJson(json, GroupResult.class);
    }

    /*
    * =============================================================
    * ======================= Person Group ========================
    * =============================================================
    */

    @Override
    public void createPersonGroup(String personGroupId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId);
        params.put("name", name);
        if (userData != null) {
            params.put("userData", userData);
        }

        mRestCall.request(uri, RequestMethod.PUT, params, null);
    }

    @Override
    public void deletePersonGroup(String personGroupId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public void updatePersonGroup(String personGroupId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId);
        params.put("name", name);
        if (userData != null) {
            params.put("userData", userData);
        }

        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public PersonGroup getPersonGroup(String personGroupId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, PersonGroup.class);
    }

    @Override
    public PersonGroup[] getPersonGroups() throws ClientException, IOException {
        return listPersonGroups("", 1000);
    }

    @Override
    public PersonGroup[] listPersonGroups(String start, int top) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s?start=%s&top=%s", mServiceHost, PERSON_GROUPS_QUERY, start, top);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);

        Type listType = new TypeToken<List<PersonGroup>>() {
        }.getType();
        List<PersonGroup> result = mGson.fromJson(json, listType);
        return result.toArray(new PersonGroup[result.size()]);
    }

    @Override
    public PersonGroup[] listPersonGroups(String start) throws ClientException, IOException {
        return listPersonGroups(start, 1000);
    }

    @Override
    public PersonGroup[] listPersonGroups(int top) throws ClientException, IOException{
        return listPersonGroups("", top);
    }

    @Override
    public PersonGroup[] listPersonGroups() throws ClientException, IOException {
        return listPersonGroups("", 1000);
    }

    @Override
    public void trainPersonGroup(String personGroupId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, TRAIN_QUERY);
        mRestCall.request(uri, RequestMethod.POST, params, null);
    }

    @Override
    public TrainingStatus getPersonGroupTrainingStatus(String personGroupId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, TRAINING_QUERY);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, TrainingStatus.class);
    }

    /*
    * =============================================================
    * ===================== Large Person Group ====================
    * =============================================================
    */

    @Override
    public void createLargePersonGroup(String largePersonGroupId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId);
        params.put("name", name);
        if(userData != null){
            params.put("userData", userData);
        }
        mRestCall.request(uri, RequestMethod.PUT, params, null);
    }

    @Override
    public void deleteLargePersonGroup(String largePersonGroupId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public void updateLargePersonGroup(String largePersonGroupId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId);
        params.put("name", name);
        if (userData != null) {
            params.put("userData", userData);
        }

        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public LargePersonGroup getLargePersonGroup(String largePersonGroupId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, LargePersonGroup.class);
    }

    @Override
    public LargePersonGroup[] listLargePersonGroups(String start, int top) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s?start=%s&top=%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, start, top);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);

        Type listType = new TypeToken<List<LargePersonGroup>>() {
        }.getType();
        List<LargePersonGroup> result = mGson.fromJson(json, listType);
        return result.toArray(new LargePersonGroup[result.size()]);
    }

    @Override
    public LargePersonGroup[] listLargePersonGroups(String start) throws ClientException, IOException {
        return listLargePersonGroups(start, 1000);
    }

    @Override
    public LargePersonGroup[] listLargePersonGroups(int top) throws ClientException, IOException {
        return listLargePersonGroups("", top);
    }

    @Override
    public LargePersonGroup[] listLargePersonGroups() throws ClientException, IOException {
        return listLargePersonGroups("", 1000);
    }

    @Override
    public void trainLargePersonGroup(String largePersonGroupId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, TRAIN_QUERY);
        mRestCall.request(uri, RequestMethod.POST, params, null);
    }

    @Override
    public TrainingStatus getLargePersonGroupTrainingStatus(String largePersonGroupId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, TRAINING_QUERY);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, TrainingStatus.class);
    }

    /*
    * =============================================================
    * ============================ Person =========================
    * =============================================================
    */

    @Override
    public CreatePersonResult createPerson(String personGroupId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY);
        params.put("name", name);
        if (userData != null) {
            params.put("userData", userData);
        }

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, CreatePersonResult.class);
    }

    @Override
    public void deletePerson(String personGroupId, UUID personId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, personId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public void updatePerson(String personGroupId, UUID personId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        if(name != null) {
            params.put("name", name);
        }

        if (userData != null) {
            params.put("userData", userData);
        }

        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, personId);
        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public Person getPerson(String personGroupId, UUID personId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, personId.toString());
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, Person.class);
    }

    @Deprecated
    @Override
    public Person[] getPersons(String personGroupId) throws ClientException, IOException {
        return listPersons(personGroupId, "", 1000);
    }

    @Override
    public Person[] listPersons(String personGroupId, String start, int top) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s/%s/%s?start=%s&top=%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, start, top);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        Type listType = new TypeToken<List<Person>>() {
        }.getType();
        List<Person> result = mGson.fromJson(json, listType);
        return result.toArray(new Person[result.size()]);
    }

    @Override
    public Person[] listPersons(String personGroupId, String start) throws ClientException, IOException {
        return listPersons(personGroupId, start, 1000);
    }

    @Override
    public Person[] listPersons(String personGroupId, int top) throws ClientException, IOException {
        return listPersons(personGroupId, "", top);
    }

    public Person[] listPersons(String personGroupId) throws ClientException, IOException {
        return listPersons(personGroupId, "", 1000);
    }

    /*
    * =============================================================
    * ======================== Person Face ========================
    * =============================================================
    */

    @Override
    public AddPersistedFaceResult addPersonFace(String personGroupId, UUID personId, String url, String userData, FaceRectangle targetFace) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String path = String.format("%s/%s/%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY);
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        if (targetFace != null) {
            String targetFaceString = String.format(Locale.ENGLISH, "%1d,%2d,%3d,%4d", targetFace.left, targetFace.top, targetFace.width, targetFace.height);
            params.put("targetFace", targetFaceString);
        }

        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, AddPersistedFaceResult.class);
    }

    @Override
    public AddPersistedFaceResult addPersonFace(String personGroupId, UUID personId, InputStream imageStream, String userData, FaceRectangle targetFace) throws ClientException, IOException
    {
        Map<String, Object> params = new HashMap<>();

        String path = String.format("%s/%s/%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY);
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        if (targetFace != null) {
            String targetFaceString = String.format(Locale.ENGLISH, "%1d,%2d,%3d,%4d", targetFace.left, targetFace.top, targetFace.width, targetFace.height);
            params.put("targetFace", targetFaceString);
        }

        String uri = WebServiceRequest.getUrl(path, params);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] bytes = new byte[1024];
        while ((bytesRead = imageStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, bytesRead);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        params.clear();
        params.put(DATA, data);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, STREAM_DATA);
        return mGson.fromJson(json, AddPersistedFaceResult.class);
    }

    @Override
    public void deletePersonFace(String personGroupId, UUID personId, UUID persistedFaceId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY, persistedFaceId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public void updatePersonFace(String personGroupId, UUID personId, UUID persistedFaceId, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        if (userData != null) {
            params.put("userData", userData);
        }

        String uri = String.format("%s/%s/%s/%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY,  persistedFaceId);
        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public PersonFace getPersonFace(String personGroupId, UUID personId, UUID persistedFaceId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s/%s/%s/%s", mServiceHost, PERSON_GROUPS_QUERY, personGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY,  persistedFaceId);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, PersonFace.class);
    }

    /*
    * =============================================================
    * ================ Person in Large Person Group ===============
    * =============================================================
    */

    @Override
    public CreatePersonResult createPersonInLargePersonGroup(String largePersonGroupId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY);
        params.put("name", name);
        if (userData != null) {
            params.put("userData", userData);
        }

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, CreatePersonResult.class);
    }

    @Override
    public void deletePersonInLargePersonGroup(String largePersonGroupId, UUID personId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, personId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public void updatePersonInLargePersonGroup(String largePersonGroupId, UUID personId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        if(name != null) {
            params.put("name", name);
        }

        if (userData != null) {
            params.put("userData", userData);
        }

        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, personId);
        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public Person getPersonInLargePersonGroup(String largePersonGroupId, UUID personId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, personId.toString());
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, Person.class);
    }

    @Override
    public Person[] listPersonsInLargePersonGroup(String largePersonGroupId, String start, int top) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String uri = String.format("%s/%s/%s/%s?start=%s&top=%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, start, top);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        Type listType = new TypeToken<List<Person>>() {
        }.getType();
        List<Person> result = mGson.fromJson(json, listType);
        return result.toArray(new Person[result.size()]);
    }

    @Override
    public Person[] listPersonsInLargePersonGroup(String largePersonGroupId, String start) throws ClientException, IOException {
        return listPersonsInLargePersonGroup(largePersonGroupId, start, 1000);
    }

    @Override
    public Person[] listPersonsInLargePersonGroup(String largePersonGroupId, int top) throws ClientException, IOException {
        return listPersonsInLargePersonGroup(largePersonGroupId, "", top);
    }

    @Override
    public Person[] listPersonsInLargePersonGroup(String largePersonGroupId) throws ClientException, IOException {
        return listPersonsInLargePersonGroup(largePersonGroupId, "", 1000);
    }

    /*
    * =============================================================
    * ============== Person Face in Large Person Group ============
    * =============================================================
    */

    @Override
    public AddPersistedFaceResult addPersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, String url, String userData, FaceRectangle targetFace) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String path = String.format("%s/%s/%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY);
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        if (targetFace != null) {
            String targetFaceString = String.format(Locale.ENGLISH, "%1d,%2d,%3d,%4d", targetFace.left, targetFace.top, targetFace.width, targetFace.height);
            params.put("targetFace", targetFaceString);
        }

        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, AddPersistedFaceResult.class);
    }

    @Override
    public AddPersistedFaceResult addPersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, InputStream imageStream, String userData, FaceRectangle targetFace) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String path = String.format("%s/%s/%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY);
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        if (targetFace != null) {
            String targetFaceString = String.format(Locale.ENGLISH, "%1d,%2d,%3d,%4d", targetFace.left, targetFace.top, targetFace.width, targetFace.height);
            params.put("targetFace", targetFaceString);
        }

        String uri = WebServiceRequest.getUrl(path, params);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] bytes = new byte[1024];
        while ((bytesRead = imageStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, bytesRead);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        params.clear();
        params.put(DATA, data);

        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, STREAM_DATA);
        return mGson.fromJson(json, AddPersistedFaceResult.class);
    }

    @Override
    public void updatePersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, UUID persistedFaceId, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("userData", userData);
        String uri = String.format("%s/%s/%s/%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY,  persistedFaceId);
        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public void deletePersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, UUID persistedFaceId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY, persistedFaceId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public PersonFace getPersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, UUID persistedFaceId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s/%s/%s/%s", mServiceHost, LARGE_PERSON_GROUPS_QUERY, largePersonGroupId, PERSONS_QUERY, personId, PERSISTED_FACES_QUERY,  persistedFaceId);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, PersonFace.class);
    }

    /*
    * =============================================================
    * ========================= Face List =========================
    * =============================================================
    */

    @Override
    public void createFaceList(String faceListId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, FACE_LISTS_QUERY, faceListId);
        params.put("name", name);
        params.put("userData", userData);
        this.mRestCall.request(uri, RequestMethod.PUT, params, null);
    }

    @Override
    public void deleteFaceList(String faceListId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, FACE_LISTS_QUERY, faceListId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public void updateFaceList(String faceListId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        if(name != null) {
            params.put("name", name);
        }

        if (userData != null) {
            params.put("userData", userData);
        }

        String uri = String.format("%s/%s/%s", mServiceHost, FACE_LISTS_QUERY, faceListId);
        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public FaceList getFaceList(String faceListId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, FACE_LISTS_QUERY, faceListId);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, FaceList.class);
    }

    @Override
    public FaceListMetadata[] listFaceLists() throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s", mServiceHost, FACE_LISTS_QUERY);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        Type listType = new TypeToken<List<FaceListMetadata>>() {
        }.getType();
        List<FaceListMetadata> result = mGson.fromJson(json, listType);
        return result.toArray(new FaceListMetadata[result.size()]);
    }

    @Override
    public AddPersistedFaceResult addFacesToFaceList(String faceListId, String url, String userData, FaceRectangle targetFace) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String path = String.format("%s/%s/%s/%s", mServiceHost, FACE_LISTS_QUERY, faceListId, PERSISTED_FACES_QUERY);
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        if (targetFace != null) {
            String targetFaceString = String.format(Locale.ENGLISH, "%1d,%2d,%3d,%4d", targetFace.left, targetFace.top, targetFace.width, targetFace.height);
            params.put("targetFace", targetFaceString);
        }

        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);
        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, AddPersistedFaceResult.class);
    }

    @Override
    public AddPersistedFaceResult AddFaceToFaceList(String faceListId, InputStream imageStream, String userData, FaceRectangle targetFace) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String path = String.format("%s/%s/%s/%s", mServiceHost, FACE_LISTS_QUERY, faceListId, PERSISTED_FACES_QUERY);
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        if (targetFace != null) {
            String targetFaceString = String.format(Locale.ENGLISH, "%1d,%2d,%3d,%4d", targetFace.left, targetFace.top, targetFace.width, targetFace.height);
            params.put("targetFace", targetFaceString);
        }

        String uri = WebServiceRequest.getUrl(path, params);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] bytes = new byte[1024];
        while ((bytesRead = imageStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, bytesRead);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        params.clear();
        params.put(DATA, data);
        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, STREAM_DATA);
        return mGson.fromJson(json, AddPersistedFaceResult.class);
    }

    @Override
    public void deleteFacesFromFaceList(String faceListId, UUID persistedFaceId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, FACE_LISTS_QUERY, faceListId, PERSISTED_FACES_QUERY, persistedFaceId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    /*
    * =============================================================
    * ====================== Large Face List ======================
    * =============================================================
    */

    @Override
    public void createLargeFaceList(String largeFaceListId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId);
        params.put("name", name);
        params.put("userData", userData);
        this.mRestCall.request(uri, RequestMethod.PUT, params, null);
    }

    @Override
    public void deleteLargeFaceList(String largeFaceListId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public void updateLargeFaceList(String largeFaceListId, String name, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        if(name != null) {
            params.put("name", name);
        }

        if (userData != null) {
            params.put("userData", userData);
        }

        String uri = String.format("%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId);
        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public LargeFaceList getLargeFaceList(String largeFaceListId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, LargeFaceList.class);
    }

    @Override
    public LargeFaceList[] listLargeFaceLists(String start, int top) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s?start=%s&top=%s", mServiceHost, LARGE_FACE_LISTS_QUERY, start, top);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);

        Type listType = new TypeToken<List<LargeFaceList>>() {
        }.getType();
        List<LargeFaceList> result = mGson.fromJson(json, listType);
        return result.toArray(new LargeFaceList[result.size()]);
    }

    @Override
    public LargeFaceList[] listLargeFaceLists(String start) throws ClientException, IOException {
        return listLargeFaceLists(start, 1000);
    }

    @Override
    public LargeFaceList[] listLargeFaceLists(int top) throws ClientException, IOException {
        return listLargeFaceLists("", top);
    }

    @Override
    public LargeFaceList[] listLargeFaceLists() throws ClientException, IOException {
        return listLargeFaceLists("", 1000);
    }

    @Override
    public void trainLargeFaceList(String largeFaceListId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId, TRAIN_QUERY);
        mRestCall.request(uri, RequestMethod.POST, params, null);
    }

    @Override
    public TrainingStatus getLargeFaceListTrainingStatus(String largeFaceListId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId, TRAINING_QUERY);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);
        return mGson.fromJson(json, TrainingStatus.class);
    }

    @Override
    public AddPersistedFaceResult addFacesToLargeFaceList(String largeFaceListId, String url, String userData, FaceRectangle targetFace) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String path = String.format("%s/%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId, PERSISTED_FACES_QUERY);
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        if (targetFace != null) {
            String targetFaceString = String.format(Locale.ENGLISH, "%1d,%2d,%3d,%4d", targetFace.left, targetFace.top, targetFace.width, targetFace.height);
            params.put("targetFace", targetFaceString);
        }

        String uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);
        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, null);
        return mGson.fromJson(json, AddPersistedFaceResult.class);
    }

    @Override
    public AddPersistedFaceResult AddFaceToLargeFaceList(String largeFaceListId, InputStream imageStream, String userData, FaceRectangle targetFace) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String path = String.format("%s/%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId, PERSISTED_FACES_QUERY);
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        if (targetFace != null) {
            String targetFaceString = String.format(Locale.ENGLISH, "%1d,%2d,%3d,%4d", targetFace.left, targetFace.top, targetFace.width, targetFace.height);
            params.put("targetFace", targetFaceString);
        }

        String uri = WebServiceRequest.getUrl(path, params);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] bytes = new byte[1024];
        while ((bytesRead = imageStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, bytesRead);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        params.clear();
        params.put(DATA, data);
        String json = (String)mRestCall.request(uri, RequestMethod.POST, params, STREAM_DATA);
        return mGson.fromJson(json, AddPersistedFaceResult.class);
    }

    @Override
    public void deleteFaceFromLargeFaceList(String largeFaceListId, UUID persistedFaceId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId, PERSISTED_FACES_QUERY, persistedFaceId);
        mRestCall.request(uri, RequestMethod.DELETE, params, null);
    }

    @Override
    public void updateFaceFromLargeFaceList(String largeFaceListId, UUID persistedFaceId, String userData) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        if (userData != null) {
            params.put("userData", userData);
        }
        String uri = String.format("%s/%s/%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId, PERSISTED_FACES_QUERY, persistedFaceId);
        mRestCall.request(uri, RequestMethod.PATCH, params, null);
    }

    @Override
    public FaceMetadata getFaceFromLargeFaceList(String largeFaceListId, UUID persistedFaceId) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        String path = String.format("%s/%s/%s/%s/%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId, PERSISTED_FACES_QUERY, persistedFaceId);
        String json = (String)mRestCall.request(path, RequestMethod.GET, params, null);
        return mGson.fromJson(json, FaceMetadata.class);
    }

    @Override
    public FaceMetadata[] listFacesFromLargeFaceList(String largeFaceListId, String start, int top) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();
        String uri = String.format("%s/%s/%s/%s?start=%s&top=%s", mServiceHost, LARGE_FACE_LISTS_QUERY, largeFaceListId, PERSISTED_FACES_QUERY, start, top);
        String json = (String)mRestCall.request(uri, RequestMethod.GET, params, null);

        Type listType = new TypeToken<List<FaceMetadata>>() {
        }.getType();
        List<FaceMetadata> result = mGson.fromJson(json, listType);
        return result.toArray(new FaceMetadata[result.size()]);
    }

    @Override
    public FaceMetadata[] listFacesFromLargeFaceList(String largeFaceListId, String start) throws ClientException, IOException {
        return listFacesFromLargeFaceList(largeFaceListId, start, 1000);
    }

    @Override
    public FaceMetadata[] listFacesFromLargeFaceList(String largeFaceListId, int top) throws ClientException, IOException {
        return listFacesFromLargeFaceList(largeFaceListId, "", top);
    }

    @Override
    public FaceMetadata[] listFacesFromLargeFaceList(String largeFaceListId) throws ClientException, IOException {
        return listFacesFromLargeFaceList(largeFaceListId, "", 1000);
    }
}
