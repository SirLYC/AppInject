# AppInject

A lightweight cross-module dependency-injection library for Android.

## Import

## Use

Define your interface(`api`) in `module A`. For example:

``` java
// oneToMany default value is false
// if oneToMany == true
// you can create more than one implementation class of this interface
// else you can only create one class
@InjectApi(oneToMany = false)
public interface ITestApi {
    String logMsg();
}
```

Implement this interface in **any module**. We call it `module B`:

``` java
@InjectApiImpl(api = ITestApi.class)
public class TestApiImpl implements ITestApi {
    @Override
    public String logMsg() {
        return "I'm TestApiImpl!";
    }
}
```

Now you can get instance of ITestApi at any module which depends on `module A`. No need to depend on `module B`:

``` kotlin
// use AppInject.getInstance().getOneToManyApiList() if oneToMany == true
val testApi = AppInject.getInstance().getSingleApi(ITestApi::class.java)
Log.d(TAG, testApi.logMsg())
```

## Kotlin Simplify

Use kotlin can simplify call to `AppInject`, for example:

``` kotlin
inline fun <reified T> getSingleApi(): T? {
    return AppInject.getInstance().getSingleApi(T::class.java)
}

inline fun <reified T> getOneToManyApiList(): List<T> {
    return AppInject.getInstance().getOneToManyApiList(T::class.java)
}
```

use it:

``` kotlin
val testApi = getSingleApi<ITestApi>()
// or
val testApi:ITestApi? = getSingleApi()
```

## Proguard

[Proguard File](./progurad.pro)

``` proguard
# for createMethod getInstance()
-keepclasseswithmembers @com.lyc.appinject.annotations.ServiceImpl class * {
    public static * getInstance();
}
-keepclasseswithmembers @com.lyc.appinject.annotations.ExtensionImpl class * {
    public static * getInstance();
}
```

## Run this project in local

**Step1** Modify file `common.properties` at root directory.

``` properties
testInjectPluginLocal=false
testRemoteRepo=false
```

**Step2** Run `gradle updateArchives` to build and publish to local maven (directory `repo` at root directory). `repo` directory should be created.

![repo](./imgs/repo_dir.png)

**Step3** Modify file `common.properties` at root directory.

``` properties
testInjectPluginLocal=true
```

**Step4** Gradle sync. Now you can run `sample` module.

## License

[License.md](./LICENSE.MD)
