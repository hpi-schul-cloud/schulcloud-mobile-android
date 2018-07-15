# Architecture

- [Architecture](#architecture)
  - [General](#general)
  - [Activities and Fragments](#activities-and-fragments)
  - [Data structure](#data-structure)
    - [Repositories](#repositories)
  - [Common features](#common-features)
    - [HTML-Content](#html-content)
    - [Share](#share)
    - [Refresh](#refresh)
    - [Feedback to the user](#feedback-to-the-user)
      - [Success/Error](#successerror)
      - [Progress dialog](#progress-dialog)
    - [Request permission](#request-permission)

## General

**IDE:** [Android Studio]  
**Build system:** Gradle  
**Programming language:** Kotlin  
**Primary libraries:** [Jetpack], [Realm]

- Use [LiveData] for a reactive data flow.
- Use [KTX synthetic properties][ktx-synthetic] for accessing views from code.
- Use [DataBinding] for displaying simple data. If a more complex expression is required for preparing/formatting the data, set it from code or use a static (`@JvmStatic`) utility method in the respective Fragment/Activity/ViewModel/View.
- Use [ViewModel] for passing data from repository to view. If an Id is required (e.g. show data for a specific course), retrieve it using `.viewmodels.IdViewModelFactory`.
- Use `.*Utils`-classes, extension functions/properties and `suspend` when applicable!


## Activities and Fragments

Top-level activities and fragments inherit from `BaseActivity` or `BaseFragment`, respectively. These base classes e.g. handle refresh and sharing actions.

`MainActivity` uses a `NavigationDrawer` to change between fragments. Any further views should rest inside additional activities.

*Please make sure that up and back navigation [work as expected](https://developer.android.com/training/design-navigation/ancestral-temporal)!*


## Data structure

### Repositories

Data is managed by so-called *repository*. They exist as singletons (Kotlin-`object`) and are invoked to perform any data-manipulating action. Repositories forward requests to a DAO or a storage. DAOs interact with the Realm-database, whereas storages wrap Android's native `SharedPreference`s.

*For an example of Repository + DAO, see the implementation of `.models.course.CourseRepository`*  
*For an example of Repository + Storage, see the implementation of `.models.user.UserRepository`*

Syncing of data (e.g. to the Schul-Cloud server) is done via sync-methods in the repository. They call a `Job` (inside `.jobs`), which itself calls a method of `.network.ApiServiceInterface`. The retrieved data is then stored in Realm using `.models.Sync`.

*For an example of Job + Sync, see the implementation of `.jobs.ListUserCoursesJob`*


## Common features

### HTML-Content

For displaying HTML-based formatted content, please use `ContentWebView`. It takes care of loading internal content (using authentication), correct opening of links (external vs internal) and resizes content designed for larger screens.

```xml
<org.schulcloud.mobile.views.ContentWebView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:content="@{content.text}" />
```

External links are opened using `openUrl`, which creates a `CustomTab` behind the scenes.


### Share

For sharing the view of an activity or fragment, just overwrite the property `url`. That's it! The base classes create a menu item handle the share action, passing the `url`-String. The current toolbar title is used as the share-`Intent`s subject.

```kotlin
class CourseListFragment : BaseFragment() {
    override var url: String? = "$HOST/courses"
}
```

If the url can change, use the following syntax:

```kotlin
class CourseListFragment : BaseFragment() {
    override var url: String? = null
        get() = "$HOST/courses/${viewModel.course.value?.id}"
}
```



### Refresh

To implement refreshing behaviour in an activity or fragment, simply set the property `swipeRefreshLayout` to the `SwipeRefreshLayout` (SRL from now on) instance, and override refresh(). The base classes handle SRL styling, toggle SRL state, add a menu item for refresh and perform the actual refresh.

```kotlin
class CourseListFragment : BaseFragment() {
    override var url: String? = "$HOST/courses"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = swipeRefresh

        // ...
    }

    override suspend fun refresh() {
        CourseRepository.syncCourses()
    }
}
```


### Feedback to the user

#### Success/Error

Indicating a successful action to the user is done by calling `showGenericSuccess`, passing a string or string resource. This will create a toast.

```kotlin
showGenericSuccess("File was downloaded!")
```

An error can be shown by calling `showGenericError`, passing a string or string resource. This will create a toast, **and the error message is automagically prefixed with "Error: " for consistency**.

*If other ways of notifying the user of an error are available, they should generally be preferred. Examples include `@style/Content_Text.Empty` for empty content, `TextView.setError()` for inputs, etc.*

```kotlin
showGenericError("File not found")
```

#### Progress dialog

Showing an indeterminate progress dialog is as simple as wrapping a `suspend`-function with `withProgressDialog`, passing a string or string resource shown in the dialog to inform the user.

```kotlin
withProgressDialog("Downloading file") {
    val result = performDownload().await()
    if (!result) {
        showGenericError("Download encountered an error. Please try again.")
        return@withProgressDialog
    }
    showGenericSuccess("File downloaded!")
}
```


### Request permission

`requestPermission` is available as a `suspend`-function taking the permission as a `String` and returning the result as a `Boolean`.

```kotlin
fun downloadFile() {
    launch(UI) {
        if (!requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showGenericError("Can't download file without permission")
            return@launch
        }
        performDownload()
    }
}
```

[android studio]: https://developer.android.com/studio/
[jetpack]: https://developer.android.com/jetpack
[realm]: https://realm.io/
[databinding]: https://developer.android.com/topic/libraries/data-binding/
[livedata]: https://developer.android.com/topic/libraries/architecture/livedata
[viewmodel]: https://developer.android.com/topic/libraries/architecture/viewmodel
[ktx-synthetic]: https://kotlinlang.org/docs/tutorials/android-plugin.html#importing-synthetic-properties
