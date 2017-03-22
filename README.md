# Microsoft Face API: Android Client Library & Sample
This repo contains the Android client library & sample for the Microsoft Face API, an offering within [Microsoft Cognitive Services](https://www.microsoft.com/cognitive-services), formerly known as Project Oxford.
* [Learn about the Face API](https://www.microsoft.com/cognitive-services/en-us/face-api)
* [Read the documentation](https://www.microsoft.com/cognitive-services/en-us/face-api/documentation/overview)
* [Find more SDKs & Samples](https://www.microsoft.com/cognitive-services/en-us/SDK-Sample?api=face)


## The Client Library
The Face API client library is a thin Java client wrapper for Microsoft Face API.

The easiest way to consume the client library is to add com.microsoft.projectoxford.face package from Maven Central Repository. To find the latest version of client library, go to http://search.maven.org, and search for "com.microsoft.projectoxford".

To add the client library dependency from build.gradle file, add the following line in dependencies.

```
dependencies {
    //
    // Use the following line to include client library from Maven Central Repository
    // Change the version number from the search.maven.org result
    //
    compile 'com.microsoft.projectoxford:face:1.1.0'

    // Your other Dependencies...
}
```

To do add the client library dependency from Android Studio:
 1. From Menu, Choose File \> Project Structure
 2. Click on your app module
 3. Click on Dependencies tab
 4. Click "+" sign to add new dependency
 5. Pick "Library dependency" from the drop down list
 6. Type "com.microsoft.projectoxford" and hit the search icon from "Choose Library Dependency" dialog
 7. Pick the Project Oxford client library that you intend to use.
 8. Click "OK" to add the new dependency

## The Sample
This sample is an Android application to demonstrate the use of Microsoft Face API. It demonstrates face detection, face verification, face grouping, finding similar faces, and face identification functionalities.

### Requirements

Android OS must be Android 4.1 or higher (API Level 16 or higher)

### Build the Sample
 1. First, you must obtain a Face API subscription key by [following the instructions on our website](<https://www.microsoft.com/cognitive-services/en-us/sign-up>).
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

<img src="SampleScreenshots/SampleRunning1.png" width="30%"/>
<img src="SampleScreenshots/SampleRunning2.png" width="30%"/>
<img src="SampleScreenshots/SampleRunning3.png" width="30%"/>

## Updates
* [Face API Release Notes](https://www.microsoft.com/cognitive-services/en-us/face-api/documentation/ReleaseNotes)

## Contributing
We welcome contributions. Feel free to file issues and pull requests on the repo and we'll address them as we can. Learn more about how you can help on our [Contribution Rules & Guidelines](</CONTRIBUTING.md>). 

You can reach out to us anytime with questions and suggestions using our communities below:
 - **Support questions:** [StackOverflow](<https://stackoverflow.com/questions/tagged/microsoft-cognitive>)
 - **Feedback & feature requests:** [Cognitive Services UserVoice Forum](<https://cognitive.uservoice.com>)

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## License
All Microsoft Cognitive Services SDKs and samples are licensed with the MIT License. For more details, see
[LICENSE](</LICENSE.md>).

Sample images are licensed separately, please refer to [LICENSE-IMAGE](</LICENSE-IMAGE.md>).

## Developer Code of Conduct
Developers using Cognitive Services, including this client library & sample, are expected to follow the “Developer Code of Conduct for Microsoft Cognitive Services”, found at [http://go.microsoft.com/fwlink/?LinkId=698895](http://go.microsoft.com/fwlink/?LinkId=698895).
