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

import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceList;
import com.microsoft.projectoxford.face.contract.FaceListMetadata;
import com.microsoft.projectoxford.face.contract.FaceMetadata;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.Glasses;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

public interface FaceServiceClient {

    /**
     * Supported face attribute types
     */
    public enum FaceAttributeType
    {
        /**
         * Analyses age
         */
        Age {
            public String toString() {
                return "age";
            }
        },

        /**
         * Analyses gender
         */
        Gender {
            public String toString() {
                return "gender";
            }
        },

        /**
         * Analyses facial hair
         */
        FacialHair {
            public String toString() {
                return "facialHair";
            }
        },

        /**
         * Analyses whether is smiling
         */
        Smile {
            public String toString() {
                return "smile";
            }
        },

        /**
         * Analyses head pose
         */
        HeadPose {
            public String toString() {
                return "headPose";
            }
        },

        /**
         * Analyses glasses type
         */
        Glasses {
            public String toString() {
                return "glasses";
            }
        },

        /**
         * Analyses emotion type
         */
        Emotion {
            public String toString() {
                return "emotion";
            }
        },

        /**
         * Analyses hair type
         */
        Hair {
            public String toString() { return "hair"; }
        },

        /**
         * Analyses makeup type
         */
        Makeup {
            public String toString() { return "makeup"; }
        },

        /**
         * Analyses occlusion type
         */
        Occlusion {
            public String toString() { return "occlusion"; }
        },

        /**
         * Analyses accessories type
         */
        Accessories {
            public String toString() { return "accessories"; }
        },

        /**
         * Analyses noise type
         */
        Noise {
            public String toString() { return "noise"; }
        },

        /**
         * Analyses exposure type
         */
        Exposure {
            public String toString() { return "exposure"; }
        },

        /**
         * Analyses blur type
         */
        Blur {
            public String toString() { return "blur"; }
        }
    }

    /**
     * Supported two working modes of Face - Find Similar
     */
    public enum FindSimilarMatchMode {
        /**
         * matchPerson mode of Face - Find Similar, return the similar faces of the same person with the query face.
         */
        matchPerson {
            public String toString() {
                return "matchPerson";
            }
        },
        /**
         * matchFace mode of Face - Find Similar, return the similar faces of the query face, ignoring if they belong to the same person.
         */
        matchFace {
            public String toString() {
                return "matchFace";
            }
        }
    }

    /*   mark Face   */

    /**
     * Detects faces in an URL image.
     * @param url url.
     * @param returnFaceId If set to <c>true</c> [return face ID].
     * @param returnFaceLandmarks If set to <c>true</c> [return face landmarks].
     * @param returnFaceAttributes Return face attributes.
     * @return detected faces.
     * @throws ClientException
     * @throws IOException
     */
    Face[] detect(String url, boolean returnFaceId, boolean returnFaceLandmarks, FaceAttributeType[] returnFaceAttributes) throws ClientException, IOException;

    /**
     * Detects faces in an uploaded image.
     * @param imageStream The image stream.
     * @param returnFaceId If set to <c>true</c> [return face ID].
     * @param returnFaceLandmarks If set to <c>true</c> [return face landmarks]
     * @param returnFaceAttributes Return face attributes.
     * @return detected faces.
     * @throws ClientException
     * @throws IOException
     */
    Face[] detect(InputStream imageStream, boolean returnFaceId, boolean returnFaceLandmarks, FaceAttributeType[] returnFaceAttributes) throws ClientException, IOException;

    /**
     * Verifies whether the specified two faces belong to the same person.
     * @param faceId1 The face id 1.
     * @param faceId2 The face id 2.
     * @return The verification result.
     * @throws ClientException
     * @throws IOException
     */
    VerifyResult verify(UUID faceId1, UUID faceId2) throws ClientException, IOException;

    /**
     * Verify whether one face belong to a person.
     * @param  faceId The face Id
     * @param personGroupId The person group Id
     * @param personId The person Id
     * @return The verification result.
     * @throws ClientException
     * @throws IOException
     */
    VerifyResult verify(UUID faceId, String personGroupId, UUID personId) throws ClientException, IOException;

    /**
     * Verify whether one face belong to a person.
     * @param faceId The face Id.
     * @param personGroupId The person group Id.
     * @param personId The person Id.
     * @return The verification result.
     * @throws ClientException
     * @throws IOException
     */
    VerifyResult verifyInPersonGroup(UUID faceId, String personGroupId, UUID personId) throws ClientException, IOException;

    /**
     * Verify whether one face belong to a person.-- Million Scale
     * @param faceId The face Id.
     * @param largePersonGroupId The large person group Id.
     * @param personId The person Id.
     * @return The verification result.
     * @throws ClientException
     * @throws IOException
     */
    VerifyResult verifyInLargePersonGroup(UUID faceId, String largePersonGroupId, UUID personId) throws ClientException, IOException;
    /**
     * Identities the faces in a given person group.
     * @param personGroupId The person group id.
     * @param faceIds The face ids.
     * @param maxNumOfCandidatesReturned The maximum number of candidates returned for each face.
     * @return The identification results.
     * @throws ClientException
     * @throws IOException
     */
    IdentifyResult[] identity(String personGroupId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Identities the faces in a given person group.
     * @param personGroupId The person group id.
     * @param faceIds The face ids.
     * @param maxNumOfCandidatesReturned The maximum number of candidates returned for each face.
     * @param confidenceThreshold The user-defined confidence threshold, default as algorithm-specified.
     * @return The identification results.
     * @throws ClientException
     * @throws IOException
     */
    IdentifyResult[] identity(String personGroupId, UUID[] faceIds, float confidenceThreshold, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Identities the faces in a given person group.
     * @param personGroupId The person group id.
     * @param faceIds The face ids.
     * @param maxNumOfCandidatesReturned The maximum number of candidates returned for each face.
     * @return The identification results.
     * @throws ClientException
     * @throws IOException
     */
    IdentifyResult[] identityInPersonGroup(String personGroupId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Identities the faces in a given person group.
     * @param personGroupId The person group id.
     * @param faceIds The face ids.
     * @param maxNumOfCandidatesReturned The maximum number of candidates returned for each face.
     * @param confidenceThreshold The user-defined confidence threshold, default as algorithm-specified.
     * @return The identification results.
     * @throws ClientException
     * @throws IOException
     */
    IdentifyResult[] identityInPersonGroup(String personGroupId, UUID[] faceIds, float confidenceThreshold, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Identities the faces in a given person group.
     * @param largePersonGroupId The large person group id.
     * @param faceIds The face ids.
     * @param maxNumOfCandidatesReturned The maximum number of candidates returned for each face.
     * @return The identification results.
     * @throws ClientException
     * @throws IOException
     */
    IdentifyResult[] identityInLargePersonGroup(String largePersonGroupId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Identities the faces in a given person group.
     * @param largePersonGroupId The large person group id.
     * @param faceIds The face ids.
     * @param maxNumOfCandidatesReturned The maximum number of candidates returned for each face.
     * @param confidenceThreshold The user-defined confidence threshold, default as algorithm-specified.
     * @return The identification results.
     * @throws ClientException
     * @throws IOException
     */
    IdentifyResult[] identityInLargePersonGroup(String largePersonGroupId, UUID[] faceIds, float confidenceThreshold, int maxNumOfCandidatesReturned) throws ClientException, IOException;
    /**
     * Finds the similar faces.
     * @param faceId The face identifier.
     * @param faceIds The face list identifier.
     * @param maxNumOfCandidatesReturned The max number of candidates returned.
     * @return The similar persisted faces.
     * @throws ClientException
     * @throws IOException
     */
    SimilarFace[] findSimilar(UUID faceId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Finds the similar faces.
     * @param faceId The face identifier.
     * @param faceIds The face list identifier.
     * @param maxNumOfCandidatesReturned The max number of candidates returned.
     * @param mode Algorithm mode option, default to be "matchPerson"
     * @return The similar persisted faces.
     * @throws ClientException
     * @throws IOException
     */
    SimilarFace[] findSimilar(UUID faceId, UUID[] faceIds, int maxNumOfCandidatesReturned, FindSimilarMatchMode mode) throws ClientException, IOException;

    /**
     * Finds the similar faces.
     * @param faceId The face identifier.
     * @param faceListId The face list identifier.
     * @param maxNumOfCandidatesReturned The max number of candidates returned.
     * @return The similar persisted faces.
     * @throws ClientException
     * @throws IOException
     */
    SimilarPersistedFace[] findSimilar(UUID faceId, String faceListId, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Finds the similar faces.
     * @param faceId The face identifier.
     * @param faceListId The face list identifier.
     * @param maxNumOfCandidatesReturned The max number of candidates returned.
     * @param mode Algorithm mode option, default to be "matchPerson"
     * @return The similar persisted faces.
     * @throws ClientException
     * @throws IOException
     */
    SimilarPersistedFace[] findSimilar(UUID faceId, String faceListId, int maxNumOfCandidatesReturned, FindSimilarMatchMode mode) throws ClientException, IOException;

    /**
     * Finds the similar faces.
     * @param faceId The face identifier.
     * @param faceListId The face list identifier.
     * @param maxNumOfCandidatesReturned The max number of candidates returned.
     * @return The similar persisted faces.
     * @throws ClientException
     * @throws IOException
     */
    SimilarPersistedFace[] findSimilarInFaceList(UUID faceId, String faceListId, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Finds the similar faces.
     * @param faceId The face identifier.
     * @param faceListId The face list identifier.
     * @param maxNumOfCandidatesReturned The max number of candidates returned.
     * @param mode Algorithm mode option, default to be "matchPerson"
     * @return The similar persisted faces.
     * @throws ClientException
     * @throws IOException
     */
    SimilarPersistedFace[] findSimilarInFaceList(UUID faceId, String faceListId, int maxNumOfCandidatesReturned, FindSimilarMatchMode mode) throws ClientException, IOException;

    /**
     * Finds the similar faces.
     * @param faceId The face identifier.
     * @param largeFaceListId The large face list identifier.
     * @param maxNumOfCandidatesReturned The max number of candidates returned.
     * @return The similar persisted faces.
     * @throws ClientException
     * @throws IOException
     */
    SimilarPersistedFace[] findSimilarInLargeFaceList(UUID faceId, String largeFaceListId, int maxNumOfCandidatesReturned) throws ClientException, IOException;

    /**
     * Finds the similar faces.
     * @param faceId The face identifier.
     * @param largeFaceListId The large face list identifier.
     * @param maxNumOfCandidatesReturned The max number of candidates returned.
     * @param mode Algorithm mode option, default to be "matchPerson"
     * @return The similar persisted faces.
     * @throws ClientException
     * @throws IOException
     */
    SimilarPersistedFace[] findSimilarInLargeFaceList(UUID faceId, String largeFaceListId, int maxNumOfCandidatesReturned, FindSimilarMatchMode mode) throws ClientException, IOException;

    /**
     * Groups the face.
     * @param faceIds The face ids.
     * @return Group result.
     * @throws ClientException
     * @throws IOException
     */
    GroupResult group(UUID[] faceIds) throws ClientException, IOException;



    /*   mark Person Group   */

    /**
     *  Creates the person group.
     * @param personGroupId The person group identifier.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void createPersonGroup(String personGroupId, String name, String userData) throws ClientException, IOException;

    /**
     * Deletes a person group.
     * @param personGroupId The person group id.
     * @throws ClientException
     * @throws IOException
     */
    void deletePersonGroup(String personGroupId) throws ClientException, IOException;

    /**
     * Updates a person group.
     * @param personGroupId The person group id.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void updatePersonGroup(String personGroupId, String name, String userData) throws ClientException, IOException;

    /**
     * Gets a person group.
     * @param personGroupId The person group id.
     * @return The person group entity.
     * @throws ClientException
     * @throws IOException
     */
    PersonGroup getPersonGroup(String personGroupId) throws ClientException, IOException;

    /**
     *  Gets all person groups.
     * @return Person group entity array.
     * @throws ClientException
     * @throws IOException
     * @deprecated use {@link #listPersonGroups(String)} l} instead.
     */
    @Deprecated
    PersonGroup[] getPersonGroups() throws ClientException, IOException;

    /**
     *  List the fist "top" of person groups whose Id is lager than "start".
     *  @param start The person groups Id bar, list person groups whose Id is lager than "start.
     *  @param top The number of person groups to list.
     * @return Person group entity array.
     * @throws ClientException
     * @throws IOException
     */
    PersonGroup[] listPersonGroups(String start, int top) throws ClientException, IOException;

    /**
     *  List the fist "top" of person groups whose Id is lager than "start".
     *  @param start The person groups Id bar, list person groups whose Id is lager than "start.
     * @return Person group entity array.
     * @throws ClientException
     * @throws IOException
     */
    PersonGroup[] listPersonGroups(String start) throws ClientException, IOException;

    /**
     *  List the fist "top" of person groups.
     *  @param top The number of person groups to list.
     * @return Person group entity array.
     * @throws ClientException
     * @throws IOException
     */
    PersonGroup[] listPersonGroups(int top) throws ClientException, IOException;

    /**
     *  List the fist "top" of person groups whose Id is lager than "start".
     * @return Person group entity array.
     * @throws ClientException
     * @throws IOException
     */
    PersonGroup[] listPersonGroups() throws ClientException, IOException;

    /**
     * Trains the person group.
     * @param personGroupId The person group id
     * @throws ClientException
     * @throws IOException
     */
    void trainPersonGroup(String personGroupId) throws ClientException, IOException;

    /**
     * Gets person group training status.
     * @param personGroupId The person group id.
     * @return The person group training status.
     * @throws ClientException
     * @throws IOException
     */
    TrainingStatus getPersonGroupTrainingStatus(String personGroupId) throws ClientException, IOException;



    /*   mark Large Person Group   */

    /**
     *  Creates the large person group.
     * @param largePersonGroupId The large person group identifier.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void createLargePersonGroup(String largePersonGroupId, String name, String userData) throws ClientException, IOException;

    /**
     * Deletes a large person group.
     * @param largePersonGroupId The large person group id.
     * @throws ClientException
     * @throws IOException
     */
    void deleteLargePersonGroup(String largePersonGroupId) throws ClientException, IOException;

    /**
     * Updates a large person group.
     * @param largePersonGroupId The large person group id.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void updateLargePersonGroup(String largePersonGroupId, String name, String userData) throws ClientException, IOException;

    /**
     * Gets a large person group.
     * @param largePersonGroupId The large person group id.
     * @return The large person group entity.
     * @throws ClientException
     * @throws IOException
     */
    LargePersonGroup getLargePersonGroup(String largePersonGroupId) throws ClientException, IOException;

    /**
     * List the fist "top" of large person groups whose Id is lager than "start".
     * @param start The large person groups Id bar, list large person groups whose Id is lager than "start.
     * @param top The number of large person groups to list.
     * @return Large person group entity array.
     * @throws ClientException
     * @throws IOException
     */
    LargePersonGroup[] listLargePersonGroups(String start, int top) throws ClientException, IOException;

    /**
     * List large person groups whose Id is lager than "start".
     * @param start The large person groups Id bar, list large person groups whose Id is lager than "start.
     * @return Large person group entity array.
     * @throws ClientException
     * @throws IOException
     */
    LargePersonGroup[] listLargePersonGroups(String start) throws ClientException, IOException;

    /**
     * List the fist "top" of large person groups.
     * @param top The number of large person groups to list.
     * @return Large person group entity array.
     * @throws ClientException
     * @throws IOException
     */
    LargePersonGroup[] listLargePersonGroups(int top) throws ClientException, IOException;

    /**
     * List large person groups.
     * @return Large person group entity array.
     * @throws ClientException
     * @throws IOException
     */
    LargePersonGroup[] listLargePersonGroups() throws ClientException, IOException;

    /**
     * Trains the large person group.
     * @param largePersonGroupId The large person group id
     * @throws ClientException
     * @throws IOException
     */
    void trainLargePersonGroup(String largePersonGroupId) throws ClientException, IOException;

    /**
     * Gets large person group training status.
     * @param largePersonGroupId The large person group id.
     * @return The person group training status.
     * @throws ClientException
     * @throws IOException
     */
    TrainingStatus getLargePersonGroupTrainingStatus(String largePersonGroupId) throws ClientException, IOException;



    /*   mark Person   */

    /**
     * Creates a person.
     * @param personGroupId The person group id.
     * @param name The name.
     * @param userData The user data.
     * @return The CreatePersonResult entity.
     * @throws ClientException
     * @throws IOException
     */
    CreatePersonResult createPerson(String personGroupId, String name, String userData) throws ClientException, IOException;

    /**
     * Deletes a person.
     * @param personGroupId The person group id.
     * @param personId The person id.
     * @throws ClientException
     * @throws IOException
     */
    void deletePerson(String personGroupId, UUID personId) throws ClientException, IOException;

    /**
     * Updates a person.
     * @param personGroupId The person group id.
     * @param personId The person id.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void updatePerson(String personGroupId, UUID personId, String name, String userData) throws ClientException, IOException;

    /**
     *  Gets a person.
     * @param personGroupId The person group id.
     * @param personId The person id.
     * @return The person entity.
     * @throws ClientException
     * @throws IOException
     */
    Person getPerson(String personGroupId, UUID personId) throws ClientException, IOException;

    /**
     * Gets 1000 persons inside a person group.
     * @param personGroupId The person group id.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     * @deprecated use {@link #listPersons(String)} l} instead.
     */
    @Deprecated
    Person[] getPersons(String personGroupId) throws ClientException, IOException;

    /**
     * List the fist "top" of persons whose Id is lager than "start".
     * @param personGroupId The person group id.
     * @param start The persons Id bar, list persons whose Id is lager than "start.
     * @param top The number of persons to list.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     */
    Person[] listPersons(String personGroupId, String start, int top) throws ClientException, IOException;

    /**
     * List the fist "top" of persons whose Id is lager than "start".
     * @param personGroupId The person group id.
     * @param start The persons Id bar, list persons whose Id is lager than "start.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     */
    Person[] listPersons(String personGroupId, String start) throws ClientException, IOException;

    /**
     * List the fist "top" of persons.
     * @param personGroupId The person group id.
     * @param top The number of persons to list.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     */
    Person[] listPersons(String personGroupId, int top) throws ClientException, IOException;

    /**
     * List the fist 1000 persons.
     * @param personGroupId The person group id.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     */
    Person[] listPersons(String personGroupId) throws ClientException, IOException;

    /**
     * Adds a face to a person.
     * @param personGroupId The person group id.
     * @param personId The person id.
     * @param url The face image URL.
     * @param userData The user data.
     * @param targetFace The target face.
     * @return Add person face result.
     * @throws ClientException
     * @throws IOException
     */
    AddPersistedFaceResult addPersonFace(String personGroupId, UUID personId, String url, String userData, FaceRectangle targetFace) throws ClientException, IOException;

    /**
     * Adds a face to a person.
     * @param personGroupId The person group id.
     * @param personId The person id.
     * @param imageStream The face image stream
     * @param userData The user data.
     * @param targetFace The Target Face.
     * @return Add person face result.
     * @throws ClientException
     * @throws IOException
     */
    AddPersistedFaceResult addPersonFace(String personGroupId, UUID personId, InputStream imageStream, String userData, FaceRectangle targetFace) throws ClientException, IOException;

    /**
     * Deletes a face of a person.
     * @param personGroupId The person group id.
     * @param personId The person id.
     * @param persistedFaceId The persisted face id.
     * @throws ClientException
     * @throws IOException
     */
    void deletePersonFace(String personGroupId, UUID personId, UUID persistedFaceId) throws ClientException, IOException;

    /**
     * Updates a face of a person.
     * @param personGroupId The person group id.
     * @param personId The person id.
     * @param persistedFaceId The persisted face id.
     * @param userData The person face entity.
     * @throws ClientException
     * @throws IOException
     */
    void updatePersonFace(String personGroupId, UUID personId, UUID persistedFaceId, String userData) throws ClientException, IOException;

    /**
     * Gets a face of a person.
     * @param personGroupId The person group id.
     * @param personId The person id.
     * @param persistedFaceId The persisted face id.
     * @return The person face entity.
     * @throws ClientException
     * @throws IOException
     */
    PersonFace getPersonFace(String personGroupId, UUID personId, UUID persistedFaceId) throws ClientException, IOException;



    /*   mark Person in Large Person Group   */

    /**
     * Creates a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param name The name.
     * @param userData The user data.
     * @return The CreatePersonResult entity.
     * @throws ClientException
     * @throws IOException
     */
    CreatePersonResult createPersonInLargePersonGroup(String largePersonGroupId, String name, String userData) throws ClientException, IOException;

    /**
     * Deletes a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param personId The person id.
     * @throws ClientException
     * @throws IOException
     */
    void deletePersonInLargePersonGroup(String largePersonGroupId, UUID personId) throws ClientException, IOException;

    /**
     * Updates a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param personId The person id.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void updatePersonInLargePersonGroup(String largePersonGroupId, UUID personId, String name, String userData) throws ClientException, IOException;

    /**
     * Gets a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param personId The person id.
     * @return The person entity.
     * @throws ClientException
     * @throws IOException
     */
    Person getPersonInLargePersonGroup(String largePersonGroupId, UUID personId) throws ClientException, IOException;

    /**
     * List the fist "top" of persons in large person group whose Id is lager than "start".
     * @param largePersonGroupId The large person group id.
     * @param start The persons Id bar, list persons whose Id is lager than "start.
     * @param top The number of persons to list.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     */
    Person[] listPersonsInLargePersonGroup(String largePersonGroupId, String start, int top) throws ClientException, IOException;

    /**
     * List persons in large person group whose Id is lager than "start".
     * @param largePersonGroupId The large person group id.
     * @param start The persons Id bar, list persons whose Id is lager than "start.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     */
    Person[] listPersonsInLargePersonGroup(String largePersonGroupId, String start) throws ClientException, IOException;

    /**
     * List the fist "top" of persons in large person group.
     * @param largePersonGroupId The large person group id.
     * @param top The number of persons to list.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     */
    Person[] listPersonsInLargePersonGroup(String largePersonGroupId, int top) throws ClientException, IOException;

    /**
     * List persons in large person group.
     * @param largePersonGroupId The large person group id.
     * @return The person entity array.
     * @throws ClientException
     * @throws IOException
     */
    Person[] listPersonsInLargePersonGroup(String largePersonGroupId) throws ClientException, IOException;



    /*   mark Person in Large Person Group   */

    /**
     * Adds a face to a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param personId The person id.
     * @param url The face image URL.
     * @param userData The user data.
     * @param targetFace The target face.
     * @return Add person face result.
     * @throws ClientException
     * @throws IOException
     */
    AddPersistedFaceResult addPersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, String url, String userData, FaceRectangle targetFace) throws ClientException, IOException;

    /**
     * Adds a face to a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param personId The person id.
     * @param imageStream The face image stream
     * @param userData The user data.
     * @param targetFace The Target Face.
     * @return Add person face result.
     * @throws ClientException
     * @throws IOException
     */
    AddPersistedFaceResult addPersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, InputStream imageStream, String userData, FaceRectangle targetFace) throws ClientException, IOException;

    /**
     * Deletes a face of a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param personId The person id.
     * @param persistedFaceId The persisted face id.
     * @throws ClientException
     * @throws IOException
     */
    void deletePersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, UUID persistedFaceId) throws ClientException, IOException;

    /**
     * Updates a face of a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param personId The person id.
     * @param persistedFaceId The persisted face id.
     * @return The person face entity.
     * @throws ClientException
     * @throws IOException
     */
    void updatePersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, UUID persistedFaceId, String userData) throws ClientException, IOException;

    /**
     * Gets a face of a person in large person group.
     * @param largePersonGroupId The large person group id.
     * @param personId The person id.
     * @param persistedFaceId The persisted face id.
     * @return The person face entity.
     * @throws ClientException
     * @throws IOException
     */
    PersonFace getPersonFaceInLargePersonGroup(String largePersonGroupId, UUID personId, UUID persistedFaceId) throws ClientException, IOException;



    /*   mark Face List   */

    /**
     *  Creates the face list.
     * @param faceListId The face list identifier.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void createFaceList(String faceListId, String name, String userData) throws ClientException, IOException;

    /**
     * Deletes the face list
     * @param faceListId The face list identifier.
     * @throws ClientException
     * @throws IOException
     */
    void deleteFaceList(String faceListId) throws ClientException, IOException;

    /**
     * Updates the face list.
     * @param faceListId The face list identifier.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void updateFaceList(String faceListId, String name, String userData) throws ClientException, IOException;

    /**
     * Gets the face list.
     * @param faceListId The face list identifier.
     * @return Face list object.
     * @throws ClientException
     * @throws IOException
     */
    FaceList getFaceList(String faceListId) throws ClientException, IOException;

    /**
     * Lists the face lists.
     * @return Face list metadata objects.
     * @throws ClientException
     * @throws IOException
     */
    FaceListMetadata[] listFaceLists() throws ClientException, IOException;

    /**
     * Adds the face to face list.
     * @param faceListId The face list identifier.
     * @param url The face image URL.
     * @param userData The user data.
     * @param targetFace
     * @return The target face.
     * @throws ClientException
     * @throws IOException
     */
    AddPersistedFaceResult addFacesToFaceList(String faceListId, String url, String userData, FaceRectangle targetFace) throws ClientException, IOException;

    /**
     *  Adds the face to face list
     * @param faceListId The face list identifier.
     * @param imageStream The face image stream.
     * @param userData The user data.
     * @param targetFace The target face.
     * @return Add face result.
     * @throws ClientException
     * @throws IOException
     */
    AddPersistedFaceResult AddFaceToFaceList(String faceListId, InputStream imageStream, String userData, FaceRectangle targetFace) throws ClientException, IOException;

    /**
     * Deletes the face from face list.
     * @param faceListId The face list identifier.
     * @param persistedFaceId The face identifier
     * @throws ClientException
     * @throws IOException
     */
    void deleteFacesFromFaceList(String faceListId, UUID persistedFaceId) throws ClientException, IOException;




    /*   mark Large Face List   */

    /**
     *  Creates the large face list.
     * @param largeFaceListId The face list identifier.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void createLargeFaceList(String largeFaceListId, String name, String userData) throws ClientException, IOException;

    /**
     * Deletes the large face list
     * @param largeFaceListId The large face list identifier.
     * @throws ClientException
     * @throws IOException
     */
    void deleteLargeFaceList(String largeFaceListId) throws ClientException, IOException;

    /**
     * Gets the large face list.
     * @param largeFaceListId The large face list identifier.
     * @return Large face list object.
     * @throws ClientException
     * @throws IOException
     */
    LargeFaceList getLargeFaceList(String largeFaceListId) throws ClientException, IOException;

    /**
     * List the first top of large face lists from the least largeFaceListId greater than the "start".
     * @param start The large face list Id bar.
     * @param top The number of large face lists to list.
     * @return Large face list objects.
     * @throws ClientException
     * @throws IOException
     */
    LargeFaceList[] listLargeFaceLists(String start, int top) throws ClientException, IOException;

    /**
     * List the large face lists from the least largeFaceListId greater than the "start".
     * @param start The large face list Id bar.
     * @return Large face list objects.
     * @throws ClientException
     * @throws IOException
     */
    LargeFaceList[] listLargeFaceLists(String start) throws ClientException, IOException;

    /**
     * List the fist "top" of large face lists
     * @param top The number of large face lists to list.
     * @return Large face list objects.
     * @throws ClientException
     * @throws IOException
     */
    LargeFaceList[] listLargeFaceLists(int top) throws ClientException, IOException;

    /**
     * Lists the large face lists.
     * @return Large face list objects.
     * @throws ClientException
     * @throws IOException
     */
    LargeFaceList[] listLargeFaceLists() throws ClientException, IOException;

    /**
     * Updates the large face list.
     * @param largeFaceListId The large face list identifier.
     * @param name The name.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void updateLargeFaceList(String largeFaceListId, String name, String userData) throws ClientException, IOException;

    /**
     * Trains the large face list.
     * @param largeFaceListId The large face list identifier.
     * @throws ClientException
     * @throws IOException
     */
    void trainLargeFaceList(String largeFaceListId) throws ClientException, IOException;

    /**
     * Gets the large face list training status.
     * @param largeFaceListId The large face list identifier.
     * @return The large face list training status.
     * @throws ClientException
     * @throws IOException
     */
    TrainingStatus getLargeFaceListTrainingStatus(String largeFaceListId) throws ClientException, IOException;

    /**
     * Adds the face to large face list.
     * @param largeFaceListId The large face list identifier.
     * @param url The face image URL.
     * @param userData The user data.
     * @param targetFace
     * @return The target face.
     * @throws ClientException
     * @throws IOException
     */
    AddPersistedFaceResult addFacesToLargeFaceList(String largeFaceListId, String url, String userData, FaceRectangle targetFace) throws ClientException, IOException;

    /**
     *  Adds the face to large face list
     * @param largeFaceListId The large face list identifier.
     * @param imageStream The face image stream.
     * @param userData The user data.
     * @param targetFace The target face.
     * @return Add face result.
     * @throws ClientException
     * @throws IOException
     */
    AddPersistedFaceResult AddFaceToLargeFaceList(String largeFaceListId, InputStream imageStream, String userData, FaceRectangle targetFace) throws ClientException, IOException;

    /**
     * Deletes the face from large face list.
     * @param largeFaceListId The large face list identifier.
     * @param persistedFaceId The face identifier
     * @throws ClientException
     * @throws IOException
     */
    void deleteFaceFromLargeFaceList(String largeFaceListId, UUID persistedFaceId) throws ClientException, IOException;

    /**
     * Updates the face from large face list.
     * @param largeFaceListId The large face list identifier.
     * @param persistedFaceId The face identifier.
     * @param userData The user data.
     * @throws ClientException
     * @throws IOException
     */
    void updateFaceFromLargeFaceList(String largeFaceListId, UUID persistedFaceId, String userData) throws ClientException, IOException;

    /**
     * Gets the face from large face list.
     * @param largeFaceListId The large face list identifier.
     * @param persistedFaceId The face identifier
     * @throws ClientException
     * @throws IOException
     */
    FaceMetadata getFaceFromLargeFaceList(String largeFaceListId, UUID persistedFaceId) throws ClientException, IOException;

    /**
     * List the first top of faces in large face list from the least persistedFaceId greater than the "start".
     * @param largeFaceListId The large face list identifier.
     * @param start The first persistedFaceId .
     * @param top The number of persisted faces to list.
     * @return Face metadata objects.
     * @throws ClientException
     * @throws IOException
     */
    FaceMetadata[] listFacesFromLargeFaceList(String largeFaceListId, String start, int top) throws ClientException, IOException;

    /**
     * List faces in large face list from the least persistedFaceId greater than the "start".
     * @param largeFaceListId The large face list identifier.
     * @param start The first persistedFaceId .
     * @return Face metadata objects.
     * @throws ClientException
     * @throws IOException
     */
    FaceMetadata[] listFacesFromLargeFaceList(String largeFaceListId, String start) throws ClientException, IOException;

    /**
     * List the first top of faces in large face list.
     * @param largeFaceListId The large face list identifier.
     * @param top The number of persisted faces to list.
     * @return Face metadata objects.
     * @throws ClientException
     * @throws IOException
     */
    FaceMetadata[] listFacesFromLargeFaceList(String largeFaceListId, int top) throws ClientException, IOException;

    /**
     * Lists faces from large face list.
     * @param largeFaceListId The large face list identifier.
     * @return Face metadata objects.
     * @throws ClientException
     * @throws IOException
     */
    FaceMetadata[] listFacesFromLargeFaceList(String largeFaceListId) throws ClientException, IOException;
}
