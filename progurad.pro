# for createMethod getInstance()
-keepclasseswithmembers @com.lyc.appinject.annotations.ServiceImpl class * {
    public static * getInstance();
}
-keepclasseswithmembers @com.lyc.appinject.annotations.ExtensionImpl class * {
    public static * getInstance();
}
