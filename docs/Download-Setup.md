# 下载和设置

## 下载

Glide的公开发行版可以通过多种方式访问。

### Jar

您可以直接从Github下载[最新的Jar包](https://github.com/bumptech/glide/releases/download/v3.6.0/glide-3.6.0.jar)。请注意，您可能还需要包含[Android v4的支持库](https://developer.android.com/topic/libraries/support-library/features.html#v4)。

### Gradle

如果您使用Gradle，您可以使用Maven Central或者JCenter添加Glide的依赖库。您还需要在支持库中包含依赖关系。
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

如果您使用Maven，您还可以在Glide上添加依赖关系。同样地，您还需要在支持库中包含依赖关系。
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

根据您的构建配置，您可能还需要进行一些额外的设置。

#### Proguard

如果您使用proguard，您可能需要添加以下行到您的proguard.cfg文件中：
```
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
```

#### Jack

Glide的构建配置需要Jack目前还不支持特性。Jack最近被弃用了，Glide要求的特性不太可能被添加。

#### Java 8

目前还没有（截止6/2017）稳定发布的Glide允许您在Android工具链中使用Java 8的特性。如果您想使用Java 8并且稳定性要求较低，则这里至少有一个支持Java 8的Android gradle插件的Alpha版本。该插件的alpha版本尚未通过Glide测试。有关更多详细信息，请参阅[Android的Java 8支持页面](https://developer.android.com/studio/write/java8-support.html)。

#### Kotlin

如果您在Kotlin的实现类中使用Glide的注解，则需要在Glide上添加kapt依赖关系，而不是annotationProcessor依赖关系：
```
dependencies {
  kapt 'com.github.bumptech.glide:compiler:4.0.0-RC1'
}
```

有关详细信息，请参阅[Generated API章节](../docs/Generated-API.md)。