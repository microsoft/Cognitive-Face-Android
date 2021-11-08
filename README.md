# This repository has been retired. Please visit the [Face QuickStart](https://docs.microsoft.com/en-us/azure/cognitive-services/face/quickstarts/client-libraries?tabs=visual-studio&pivots=programming-language-csharp) to get started with the latest Face SDKs.
### Microsoft Face API: Android Client Library & Sample
This repo contains the Android client library & sample for the Microsoft Face API, an offering within [Microsoft Cognitive Services](https://azure.microsoft.com/en-us/services/cognitive-services/), formerly known as Project Oxford.

* [Learn about the Face API](https://azure.microsoft.com/en-us/services/cognitive-services/face/)
* [Documentation & API Reference & SDKs](https://docs.microsoft.com/en-us/azure/cognitive-services/face/)

## The Client Library
The Face API client library is a thin Java client wrapper for Microsoft Face API.

The client library is indexed on Maven Central Repository with all versions [here](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.microsoft.projectoxford%22%20AND%20a%3A%22face%22).

### Invoke the client library

There are two recommended approaches to introduce the client library in other application projects.

To add the client library dependency via `build.gradle` file, add the following entry in the `dependencies` section.

```
dependencies {
    //
    // Use the following line to include client library from Maven Central Repository
    // Change the version number with the latest version according to the search.maven.org result
    //
    implementation 'com.microsoft.projectoxford:face:1.4.3'

    // Your other Dependencies...
}
```

To add the client library dependency via Android Studio:
 1. From Menu, Choose File \> Project Structure.
 2. Click on your app module.
 3. Click on Dependencies tab.
 4. Click "+" sign to add new dependency.
 5. Pick "Library dependency" from the drop down list.
 6. Type "com.microsoft.projectoxford" and hit the search icon from "Choose Library Dependency" dialog.
 7. Pick the Project Oxford client library that you intend to use.
 8. Click "OK" to add the new dependency.

## The Sample
This sample is an Android application to demonstrate the use of Microsoft Face API. It demonstrates face detection, face verification, face grouping, finding similar faces, and face identification functionalities.

### Requirements

Android OS must be Android 5.1 or higher (API Level 22 or higher).

### Build the Sample
 1. First, you must obtain a Face API subscription key by [following the instructions on our website](https://azure.microsoft.com/en-us/try/cognitive-services/?api=face-api).
 2. Start Android Studio and open project from Face \> Android \> Sample folder.
 3. In Android Studio -\> "Project" panel -\> "Android" view, open file
    "app/res/values/strings.xml", and find the line
    "Please\_add\_the\_subscription\_key\_here;". Replace the
    "Please\_add\_the\_subscription\_key\_here" value with your subscription key
    string from the first step. If you cannot find the file "strings.xml", it is
    in folder "Sample\app\src\main\res\values\string.xml".
 4. In Android Studio, select menu "Build \> Make Project" to build the sample.

### Run the sample
In Android Studio, select menu "Run", and "Run app" to launch this sample app.

Once the app is launched, click on buttons to use samples of between different
scenarios, and follow the instructions on screen.

Microsoft will receive the images you upload and may use them to improve Face
API and related services. By submitting an image, you confirm you have consent
from everyone in it.

<img src="SampleScreenshots/SampleRunning1.png" width="30%"/>	<img src="SampleScreenshots/SampleRunning2.png" width="30%"/>	<img src="SampleScreenshots/SampleRunning3.png" width="30%"/>

## Updates
* [Face API Release Notes](https://docs.microsoft.com/en-us/azure/cognitive-services/face/releasenotes)

## Contributing
We welcome contributions. Feel free to file issues and pull requests on the repo and we'll address them as we can. Learn more about how you can help on our [Contribution Rules & Guidelines](/CONTRIBUTING.md).

You can reach out to us anytime with questions and suggestions using our communities below:
 - **Support questions:** [StackOverflow](https://stackoverflow.com/questions/tagged/microsoft-cognitive)
 - **Feedback & feature requests:** [Cognitive Services UserVoice Forum](https://cognitive.uservoice.com)

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## License
All Microsoft Cognitive Services SDKs and samples are licensed with the MIT License. For more details, see
[LICENSE](/LICENSE.md).

Sample images are licensed separately, please refer to [LICENSE-IMAGE](/LICENSE-IMAGE.md).

## Developer Code of Conduct
Developers using Cognitive Services, including this client library & sample, are expected to follow the “Developer Code of Conduct for Microsoft Cognitive Services”, found at [http://go.microsoft.com/fwlink/?LinkId=698895](http://go.microsoft.com/fwlink/?LinkId=698895).
