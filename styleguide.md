# Styleguide

Most important:

- [1.1.2 Resources files](#112-resources-files)
- [2.2.9 Logging guidelines](#229-logging-guidelines)
- [2.2.10 No unnecessary if nesting](#2210-no-unnecessary-if-nesting)
- [2.2.14 Arguments in Fragments and Activities](#2214-arguments-in-fragments-and-activities)
- [2.3.1 Use self closing tags](#231-use-self-closing-tags)
- [2.3.2 Resources naming](#232-resources-naming)

All Contents:

- [Styleguide](#styleguide)
  - [1. Project guidelines](#1-project-guidelines)
    - [1.1 File naming](#11-file-naming)
      - [1.1.1 Class files](#111-class-files)
      - [1.1.2 Resources files](#112-resources-files)
        - [1.1.2.1 Drawable files](#1121-drawable-files)
        - [1.1.2.2 Layout files](#1122-layout-files)
        - [1.1.2.3 Menu files](#1123-menu-files)
        - [1.1.2.4 Values files](#1124-values-files)
        - [1.1.2.5 Namespaces](#1125-namespaces)
  - [2 Code guidelines](#2-code-guidelines)
    - [2.1 Kotlin language rules](#21-kotlin-language-rules)
      - [2.1.1 Don't ignore exceptions](#211-dont-ignore-exceptions)
      - [2.1.2 Don't catch generic exception](#212-dont-catch-generic-exception)
      - [2.1.3 Don't use finalizers](#213-dont-use-finalizers)
      - [2.1.4 Fully qualify imports](#214-fully-qualify-imports)
    - [2.2 Kotlin style rules](#22-kotlin-style-rules)
      - [2.2.1 Properties definition and naming](#221-properties-definition-and-naming)
      - [2.2.2 Treat acronyms as words](#222-treat-acronyms-as-words)
      - [2.2.3 Use spaces for indentation](#223-use-spaces-for-indentation)
      - [2.2.4 No semicolons](#224-no-semicolons)
      - [2.2.5 Use standard brace style](#225-use-standard-brace-style)
      - [2.2.6 Annotations](#226-annotations)
        - [2.2.6.1 Annotations practices](#2261-annotations-practices)
        - [2.2.6.2 Annotations style](#2262-annotations-style)
      - [2.2.7 Limit variable scope](#227-limit-variable-scope)
      - [2.2.8 Order import statements](#228-order-import-statements)
      - [2.2.9 Logging guidelines](#229-logging-guidelines)
      - [2.2.10 No unnecessary if nesting](#2210-no-unnecessary-if-nesting)
      - [2.2.11 Class member ordering](#2211-class-member-ordering)
      - [2.2.12 Parameter ordering in methods](#2212-parameter-ordering-in-methods)
      - [2.2.13 String constants, naming, and values](#2213-string-constants-naming-and-values)
      - [2.2.14 Arguments in Fragments and Activities](#2214-arguments-in-fragments-and-activities)
      - [2.2.15 Line length limit](#2215-line-length-limit)
        - [2.2.15.1 Line-wrapping strategies](#22151-line-wrapping-strategies)
    - [2.3 XML style rules](#23-xml-style-rules)
      - [2.3.1 Use self closing tags](#231-use-self-closing-tags)
      - [2.3.2 Resources naming](#232-resources-naming)
        - [2.3.2.1 ID naming](#2321-id-naming)
        - [2.3.2.2 Strings](#2322-strings)
        - [2.3.2.3 Styles and Themes](#2323-styles-and-themes)
      - [2.3.3 Attributes ordering](#233-attributes-ordering)
    - [2.4 Tests style rules](#24-tests-style-rules)
      - [2.4.1 Unit tests](#241-unit-tests)


## 1. Project guidelines

### 1.1 File naming

#### 1.1.1 Class files

Class names are written in [UpperCamelCase](http://en.wikipedia.org/wiki/CamelCase).

For classes that extend an Android component, the name of the class should end with the name of the component, e.g. `SignInActivity`, `SignInFragment`, `ImageUploaderService`, `ChangePasswordDialog`.

#### 1.1.2 Resources files

Resources file names are written in __lowercase_underscore__.

##### 1.1.2.1 Drawable files

Naming conventions for drawables:

| Asset Type | Prefix    | Example           |
| ---------- | --------- | ----------------- |
| Button     | `button_` | `button_gray.xml` |
| Icon       | `ic_`     | `ic_star.xml`     |

##### 1.1.2.2 Layout files

Layout files should match the name of the Android components that they are intended for but moving the top level component name to the beginning. For example, if we are creating a layout for the `SignInActivity`, the name of the layout file should be `activity_sign_in.xml`.

| Component         | Class Name             | Layout Name                  |
| ----------------- | ---------------------- | ---------------------------- |
| Activity          | `UserProfileActivity`  | `activity_user_profile.xml`  |
| Fragment          | `SignUpFragment`       | `fragment_sign_up.xml`       |
| Dialog            | `ChangePasswordDialog` | `dialog_change_password.xml` |
| RecyclerView item | ---                    | `item_person.xml`            |
| Partial layout    | ---                    | `partial_stats_bar.xml`      |

Note that there are cases where these rules will not be possible to apply. For example, when creating layout files that are intended to be part of other layouts. In this case you should use the prefix `partial_`.

##### 1.1.2.3 Menu files

Similar to layout files, menu files should match the name of the component. For example, if we are defining a menu file that is going to be used in the `UserActivity`, then the name of the file should be `activity_user.xml`

`menu` should not be part of the name because these files are already located in the `menu` directory.

##### 1.1.2.4 Values files

Resource files in the values folder should be __plural__, e.g. `strings.xml`, `styles.xml`, `colors.xml`, `dimens.xml`, `attrs.xml`.

##### 1.1.2.5 Namespaces

Many related resources can be grouped using namespaces. Examples include `brand` and `material`. Resources belonging to such namespaces should be stored in separate files using the namespace as a suffix (e.g. `colors_brand.xml`, `styles_material.xml`). The resources itself are then prefixed with the namespace (e.g. `@color/brand_primary`, `@style/Material.Icon`).

## 2 Code guidelines

### 2.1 Kotlin language rules

#### 2.1.1 Don't ignore exceptions

You must never do the following:

```kotlin
fun setServerPort(value: String) {
    try {
        serverPort = Integer.parseInt(value)
    } catch (e: NumberFormatException) { }
}
```

_While you may think that your code will never encounter this error condition or that it is not important to handle it, ignoring exceptions like above creates mines in your code for someone else to trip over some day. You must handle every Exception in your code in some principled way. The specific handling varies depending on the case._ - ([Android code style guidelines](https://source.android.com/source/code-style.html))

See alternatives [here](https://source.android.com/source/code-style.html#dont-ignore-exceptions).


#### 2.1.2 Don't catch generic exception

You should not do this:

```kotlin
try {
    someComplicatedIOFunction()        // may throw IOException
    someComplicatedParsingFunction()   // may throw ParsingException
    someComplicatedSecurityFunction()  // may throw SecurityException
    // phew, made it all the way
} catch (e: Exception) {               // I'll just catch all exceptions
    handleError()                      // with one generic handler!
}
```

See the reason why and some alternatives [here](https://source.android.com/source/code-style.html#dont-catch-generic-exception).


#### 2.1.3 Don't use finalizers

_We don't use finalizers. There are no guarantees as to when a finalizer will be called, or even that it will be called at all. In most cases, you can do what you need from a finalizer with good exception handling. If you absolutely need it, define a `close()` method (or the like) and document exactly when that method needs to be called. See `InputStream` for an example. In this case it is appropriate but not required to print a short log message from the finalizer, as long as it is not expected to flood the logs._ - ([Android code style guidelines](https://source.android.com/source/code-style.html#dont-use-finalizers))


#### 2.1.4 Fully qualify imports

This is bad: `import foo.*`  
This is good: `import foo.Bar`

An exception is made for KTX Synthetic (e.g. `import kotlinx.android.synthetic.main.<layout>.*`) and packages from which at least 5 names are used.

See more info [here](https://source.android.com/source/code-style.html#fully-qualify-imports).

### 2.2 Kotlin style rules

#### 2.2.1 Properties definition and naming

Properties should be defined at the __top of the file__ and they should follow the naming rules listed below.

- Private property names are **lowerCamelCase**
- Constants are **ALL_CAPS_WITH_UNDERSCORES**

Example:

```kotlin
open class MyClass {
    companion object {
        const val SOME_CONSTANT = 42
    }

    val publicField: Int? = null
    internal val internalField: Int? = null
    private val privateField: Int? = null
    protected val protectedField: Int? = null
}
```


#### 2.2.2 Treat acronyms as words

| Good             | Bad              |
| ---------------- | ---------------- |
| `XmlHttpRequest` | `XMLHTTPRequest` |
| `getCustomerId`  | `getCustomerID`  |
| `url: String`    | `URL: String`    |
| `id: Long`       | `ID: Long`       |


#### 2.2.3 Use spaces for indentation

Use __4 space__ indents for blocks:

```kotlin
if (x == 1) {
    x++
}
```

Use __8 space__ indents for line wraps:

```kotlin
val i =
        someLongExpression(that, wouldNotFit, on, one, line)
```


#### 2.2.4 No semicolons

Semicolons should not be used to end expressions.


#### 2.2.5 Use standard brace style

Braces go on the same line as the code before them.

```kotlin
fun func(): Int {
    if (something) {
        // ...
    } else if (somethingElse) {
        // ...
    } else {
        // ...
    }
}
```

Braces around the statements are required unless the condition and the body fit on one line.

If the body fits on one line, then braces are not required, e.g.

```kotlin
if (condition)
    body()
```

This is __bad__:

```kotlin
if (condition) body()  // bad!
```


#### 2.2.6 Annotations

##### 2.2.6.1 Annotations practices

Information about annotation guidelines can be found [here](http://source.android.com/source/code-style.html#use-standard-java-annotations).

##### 2.2.6.2 Annotations style

When annotations are applied to a class, secondary constructor, function or property, they are listed after the documentation block and should appear as __one annotation per line__. This does not apply to parameter annotations, which shoud appear inline.

```kotlin
/**
 * This is the documentation block about the class
 */
@AnnotationA
@AnnotationB
class MyAnnotatedClass { }
```


#### 2.2.7 Limit variable scope

_The scope of local variables should be kept to a minimum (Effective Java Item 29). By doing so, you increase the readability and maintainability of your code and reduce the likelihood of error. Each variable should be declared in the innermost block that encloses all uses of the variable._

_Local variables should be declared at the point they are first used. Nearly every local variable declaration should contain an initializer. If you don't yet have enough information to initialize a variable sensibly, you should postpone the declaration until you do._ - ([Android code style guidelines](https://source.android.com/source/code-style.html#limit-variable-scope))


#### 2.2.8 Order import statements

If you are using an IDE such as Android Studio, you don't have to worry about this because your IDE is already obeying these rules. If not, the import statements are listed alphabetically without any blank lines.


#### 2.2.9 Logging guidelines

Use the logging functions provided by the `AndroidUtils` class to print out error messages or other information that may be useful for developers to identify issues. These functions automatically disable all logging on release builds, hence **don't use methods from `Log` directly!**

- `logv(tag: String, msg: String, e: Exception? = null)` (verbose)
- `logd(tag: String, msg: String, e: Exception? = null)` (debug)
- `logi(tag: String, msg: String, e: Exception? = null)` (information)
- `logw(tag: String, msg: String, e: Exception? = null)` (warning)
- `loge(tag: String, msg: String, e: Exception? = null)` (error)

As a general rule, we use the class name as tag and we define it as a `val` in the companion object (or at the top of the file for top-level functions). For example:

```kotlin
class MyClass {
    companion object {
        private val TAG = MyClass::class.java.simpleName
    }

    fun myMethod() {
        loge(TAG, "My error message")
    }
}
```


#### 2.2.10 No unnecessary if nesting

If some code requires preconditions to apply before it can run, those conditions should all be checked **and handled** first, and only then the actual action is performed. While it may sometimes require less lines of code, deep nesting of conditions makes the code harder to read. Using the flat structure it is instantly clear how each error is handled.

```kotlin
fun login(username: String, password: String) {
    if (username.isBlank()) {
        showGenericError("Please enter your username")
        return
    }
    if (password.isEmpty()) {
        showGenericError("Please enter your password")
        return
    }

    performLogin()
}
```

This is **bad**:

```kotlin
fun login(username: String, password: String) {
    if (username.isNotBlank()) {
        if (!password.isNotEmpty()) {
            performLogin()
        } else
            showGenericError("Please enter your password")
    } else
        showGenericError("Please enter your username")
}
```


#### 2.2.11 Class member ordering

There is no single correct solution for this but using a __logical__ and __consistent__ order will improve code learnability and readability. It is recommendable to use the following order:

1. Companion object
    1. Properties
    2. Functions
2. Properties
3. Secondary Constructors
4. Override methods and callbacks (public or private)
5. Public methods
6. Private methods
7. Inner classes or interfaces

Example:

```kotlin
class MainActivity : Activity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var title: String
    private lateinit var titleView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        ...
    }

    fun setTitle(title: String) {
    	  this.title = title
    }

    private fun setUpView() {
        ...
    }

    class AnInnerClass {

    }
}
```

If your class is extending an __Android component__ such as an Activity or a Fragment, it is a good practice to order the override methods so that they __match the component's lifecycle__. For example, if you have an Activity that implements `onCreate()`, `onDestroy()`, `onPause()` and `onResume()`, then the correct order is:

```kotlin
class MainActivity : Activity() {

    //Order matches Activity lifecycle
    override fun onCreate() {}

    override fun onResume() {}

    override fun onPause() {}

    override fun onDestroy() {}
}
```


#### 2.2.12 Parameter ordering in methods

When programming for Android, it is quite common to define methods that take a `Context`. If you are writing a method like this, then the __Context__ must be the __first__ parameter.

The opposite case are __callback__ interfaces that should always be the __last__ parameter.

Examples:

```kotlin
// Context always goes first
fun loadUser(context: Context, userId: int): User

// Callbacks always go last
fun registerCallback(context: Context, userId: int, callback: (User) -> Unit)
```


#### 2.2.13 String constants, naming, and values

Many elements of the Android SDK such as `SharedPreferences`, `Bundle`, or `Intent` use a key-value pair approach so it's very likely that even for a small app you end up having to write a lot of String constants.

When using one of these components, you __must__ define the keys as a `const val` in the companion object and they should be prefixed as indicated below.

| Element            | Field Name Prefix |
| ------------------ | ----------------- |
| SharedPreferences  | `KEY_`           |
| Bundle             | `BUNDLE_`         |
| Fragment Arguments | `ARGUMENT_`       |
| Intent Extra       | `EXTRA_`          |
| Intent Action      | `ACTION_`         |

Note that the arguments of a Fragment - `Fragment.getArguments()` - are also a Bundle. However, because this is a quite common use of Bundles, we define a different prefix for them.

Example:

```kotlin
class Foo {
    companion object {
      // Note the value of the field is the same as the name to avoid duplication issues
      const val KEY_EMAIL = "KEY_EMAIL"
      const val BUNDLE_AGE = "BUNDLE_AGE"
      const val ARGUMENT_ID = "ARGUMENTID"

      // Intent-related items use full package name as value
      const val EXTRA_ID = "org.schulcloud.extras.EXTRA_ID"
      const val ACTION_OPEN_USER = "org.schulcloud.action.ACTION_OPEN_USER"
    }
}
```


#### 2.2.14 Arguments in Fragments and Activities

When data is passed into an `Activity` or `Fragment` via an `Intent` or a `Bundle`, the keys for the different values __must__ follow the rules described in the section above.

When an `Activity` or `Fragment` expects arguments, it should provide a `public` method in the companion object that facilitates the creation of the relevant `Intent` or `Fragment`.

In the case of Activities the method is usually called `newIntent()`:

```kotlin
fun newIntent(context: Context, id: String): Intent {
    return Intent(context, CourseActivity::class.java)
            .apply { putExtra(EXTRA_ID, id) }
}
```

For Fragments it is named `newInstance()` and handles the creation of the `Fragment` with the right arguments:

```kotlin
fun newInstance(id: String): FooFragment {
    return FooFragment()
            .apply {
                mapOf(ARGUMENT_ID to id).asBundle()
            }
}
```

__Note__: As we provide the functions described above, the keys for extras and arguments should be `private` because there is no need for them to be exposed outside the class.


#### 2.2.15 Line length limit

Code lines should not exceed __100 characters__. If the line is longer than this limit there are usually two options to reduce its length:

- Extract a local variable or function (preferable).
- Apply line-wrapping to divide a single line into multiple ones.

There are two __exceptions__ where it is possible to have lines longer than 100:

- Lines that are not possible to split, e.g. long URLs in comments.
- `package` and `import` statements.

##### 2.2.15.1 Line-wrapping strategies

There isn't an exact formula that explains how to line-wrap and quite often different solutions are valid. However there are a few rules that can be applied to common cases.

__Break at operators__

When the line is broken at an operator, the break comes __before__ the operator. For example:

```kotlin
val longName = anotherVeryLongVariable + anEvenLongerOne - thisRidiculousLongOne
        + theFinalOne
```

__Assignment Operator Exception__

An exception to the `break at operators` rule is the assignment operator `=`, where the line break should happen __after__ the operator.

```kotlin
val longName =
        anotherVeryLongVariable + anEvenLongerOne - thisRidiculousLongOne + theFinalOne
```

__Method chain case__

When multiple methods are chained in the same line - for example when using Builders - every call to a method should go in its own line, breaking the line before the `.`

```kotlin
Picasso.with(context).load("https://schul-cloud.org/images/partner/hpi-logo.jpg").into(imageView)
```

```kotlin
Picasso.with(context)
        .load("https://schul-cloud.org/images/partner/hpi-logo.jpg")
        .into(imageView)
```

__Long parameters case__

When a method has many parameters or its parameters are very long, we should break the line after every comma `,`

```kotlin
loadPicture(context, "https://schul-cloud.org/images/partner/hpi-logo.jpg", profilePictureView, clickListener, "Title of the picture")
```

```kotlin
loadPicture(context,
        "https://schul-cloud.org/images/partner/hpi-logo.jpg",
        profilePictureView,
        clickListener,
        "Title of the picture")
```


### 2.3 XML style rules

#### 2.3.1 Use self closing tags

When an XML element doesn't have any contents, you __must__ use self closing tags.

This is good:

```xml
<TextView
    android:id="@+id/name"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

This is __bad__:

```xml
<!-- Don\'t do this! -->
<TextView
    android:id="@+id/name"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">
</TextView>
```


#### 2.3.2 Resources naming

Resource IDs and names are written in a mix of __lowerCamelCase__ (for finer separation) and __lowercase_underscore__ (for broader separation/categorization).

```xml
<string name="login_forgotPassword" />
<string name="content_etherpad_openInBrowser" />
```

##### 2.3.2.1 ID naming

View-IDs should mirror the content they display, e.g. `name`, `description`. Example:

```xml
<ImageView
    android:id="@+id/profilePicture"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

Menu/menu item-IDs should be prefixed with the broader section they belong to (e.g. `course`, `topic`), followed by `_action_` and then continue with the description of the action.

```xml
<menu>
    <item android:id="@+id/course_action_share" />
    <item android:id="@+id/topic_action_openInBrowser" />
</menu>
```

##### 2.3.2.2 Strings

String names start with a prefix that identifies the section they belong to. For example `registration_email_hint` or `registration_name_hint`. If a string __doesn't belong__ to any section, then you should follow the rules below:

| Prefix     | Description                                  |
| ---------- | -------------------------------------------- |
| `general_` | Generally useful strings                     |
| `dialog_`  | Strings belonging to dialogs (e.g. "Cancel") |

Those bigger sections are separated by 1 or 2 blank lines, and in the beginning of each section the name is written as a comment. In addition, subsections can also be separated by a single blank line. Error messages must begin with `error_` after the sections:

```xml
<resources>
    <!-- General -->
    <string name="general_action_share">Share</string>
    <string name="general_action_refresh">Refresh</string>


    <!-- Login -->
    <string name="login_email_hint">Email</string>
    <string name="login_forgotPassword">Forgot password?</string>
    <string name="login_error_emailInvalid">The email-address is invalid</string>

    <string name="login_demo">Don't have an account? Try it!</string>
    <string name="login_demo_error_unavailable">Please try again later!</string>

    <!-- Main -->
    <string name="main_drawer_open">Open navigation drawer</string>
    <string name="main_drawer_close">Close navigation drawer</string>
</resources>
```

##### 2.3.2.3 Styles and Themes

Unlike the rest of resources, style names are written in __UpperCamelCase__.

```xml
<style name="Material.ListItem.Simple" parent="...">
    <!-- ... -->
</style>
```

When a hierarchy implied by `.`-separation is inappropriate, __UpperCamel_WithUnderscore__ may also be used for parts of the name:

```xml
<style name="Content_Text" />
<style name="Content_Text.Empty">
    <!-- ... -->
</style>
```


#### 2.3.3 Attributes ordering

As a general rule you should try to group similar attributes together. A good way of ordering the most common attributes is:

1. `android:id`
2. `android:style`
3. `android:layout_width` and `android:layout_height`
4. Other layout attributes, sorted alphabetically
5. Remaining attributes, sorted alphabetically


### 2.4 Tests style rules

#### 2.4.1 Unit tests

Test classes should match the name of the class the tests are targeting, followed by `Test`. For example, if we create a test class that contains tests for the `RequestJob`, we should name it `RequestJobTest`.

Test methods are annotated with `@Test` and should generally start with the name of the method that is being tested, followed by a precondition and/or expected behaviour.

- Template: `@Test fun methodName_precondition_expectedBehaviour()`
- Example: `@Test fun signIn_withEmptyEmail_fails()`

Precondition and/or expected behaviour may not always be required if the test is clear enough without them.

Sometimes a class may contain a large amount of methods, that at the same time require several tests for each method. In this case, it's recommendable to split up the test class into multiple ones. For example, if the `Repository` contains a lot of methods we may want to divide it into `RepositorySignInTest`, `RepositoryLoadUsersTest`, etc. Generally you will be able to see what tests belong together because they have common [test fixtures](https://en.wikipedia.org/wiki/Test_fixture).


---

This styleguide was adapted from [Ribot](https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md).
