# 下载和设置
## 下载
Glide的公开发行版可以通过多中方式访问。
### Jar
你可以直接从Github下载[最新的Jar包](https://github.com/bumptech/glide/releases/download/v3.6.0/glide-3.6.0.jar)。请注意，你可能还需要包含[Android v4的支持库]()。
### Gradle
如果你使用Gradle，你可以使用Maven Central或者JCenter添加Glide的依赖库。你也需要包含支持库的依赖。
```
repositories {
  mavenCentral()
}

dependencies {
    compile 'com.github.bumptech.glide:glide:4.0.0-RC1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.0.0-RC1'
    compile 'com.android.support:support-v4:25.3.1'
}
```
### Maven
如果你使用Maven，你也可以添加Glide依赖。你也需要包含支持库的依赖。
```
<dependency>
  <groupId>com.github.bumptech.glide</groupId>
  <artifactId>glide</artifactId>
  <version>4.0.0</version>
  <type>aar</type>
</dependency>
<dependency>
  <groupId>com.google.android</groupId>
  <artifactId>support-v4</artifactId>
  <version>r7</version>
</dependency>
```
### 设置
根据你的编译配置，你可能需要一些额外的设置。
#### Proguard
如果你使用proguard，你可能需要添加下面代码到你的proguard.cfg文件中：
```
If you use proguard, you may need to add the following lines to your proguard.cfg:

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
```
#### Jack
Glide的构建配置需要的特性Jack目前还不支持。Jack最近被弃用了。Glide要求的特性不太可能被添加。
#### Java 8
目前还没有（截止6/2017）稳定发布的Glide允许你在Android工具链中使用Java 8的特性。如果您想使用Java 8并且稳定性要求较低，则这里至少有一个支持Java 8的Android gradle插件的Alpha版本。该插件的alpha版本尚未通过Glide测试。有关更多详细信息，请参阅[Android的Java 8支持页面]()。
#### Kotlin
如果您在Kotlin的实现类中使用Glide的注解，则需要在Glide的注释处理器上添加kapt依赖关系，而不是annotationProcessor依赖关系：

dependencies {
  kapt 'com.github.bumptech.glide:compiler:4.0.0-RC1'
}
有关详细信息，请参阅[Generated API章节]()。










