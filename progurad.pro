# for createMethod getInstance()
-keepclasseswithmembers @com.lyc.appinject.annotations.InjectApiImpl class * {
    public static * getInstance();
}
