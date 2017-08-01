# Generated API

Glide v4通过注解处理器生成一个API，允许应用程序以一个灵活的API访问[RequestBuilder](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html)、[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)的所有选项以及包含的集成库中定义的选项。

生成的API服务于两个目的：
1. 集成库可以通过自定义选项扩展Glide的API。
2. 应用程序可以通过添加绑定常用选项的方法来扩展Glide的API。

虽然这两个任务都可以通过手动编写自定义[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)的子类来实现，但这样做是有挑战性的，并且会产生不太灵活的API。

## 入门

### Java

要使用生成的API，您需要执行两个步骤：
1. 在Glide的注解处理器上添加依赖关系：
```
repositories {
  mavenCentral()
}
   
dependencies {
  annotationProcessor 'com.github.bumptech.glide:compiler:4.0.0-RC1'
}
```

有关详细信息，请参阅[下载和设置页面](../docs/Download-Setup.md)。

2. 在应用程序中包含一个[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现：
```
package com.example.myapp;
   
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
   
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {}
```

[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现必须始终使用[@GlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideModule.html)注解。如果没有使用该注解，该模块将不会被发现，并且您会在**Glide**的日志标记中看到一条无法找到模块的警告。

### Kotlin

如果您使用Kotlin，您可以：
1. 实现如上所示的所有的Glide的注解类（包括：AppGlideModule，LibraryGlideModule和GlideExtension）。
2. 在Kotlin中实现注解类，需要在Glide中添加kapt依赖关系，而不是annotationProcessor依赖关系：
```
dependencies {
  kapt 'com.github.bumptech.glide:compiler:4.0.0-RC1'
}
```
要使用kapt，请参阅[官方文档](https://kotlinlang.org/docs/reference/kapt.html)。

## 使用生成的API
默认情况下，生成的API会跟您的[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现在同一包下，类名为GlideApp。使用该API，应用程序可以在所有的负载中用**GlideApp.with()**代替**Glide.with()**。
```
GlideApp.with(fragment)
   .load(myUrl)
   .placeholder(R.drawable.placeholder)
   .fitCenter()
   .into(imageView);
```

不同于**Glide.with()**，生成的API可以直接调用**fitCenter()**和**placeholder()**方法，而不必作为单独的[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)对象传入。

## GlideExtension

Glide的生成的API可以由应用程序和库扩展。扩展使用带注解的静态方法来添加新的选项，修改已有的选项或者添加其他类型。

[GlideExtension](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideExtension.html)注解标记一个类扩展了Glide的API。该注解必须出现在任何扩展了Glide API的类上。如果该注解不存在，那么注解方法将会被忽略。

通过GlideExtension注解标记的类被认为是实用程序类。这些类应该包含私有的空的构造函数，也应该是final的并且只包含静态方法。注解类可能包含静态变量跟引用其他类或者对象。

应用程序可以实现多个GlideExtension注解类。库也可以实现任意数量的GlideExtension注解类。当一个AppGlideModule被找到时，所有的有效的GlideExtensions将被合并并创建一个单一的API里。冲突会导致Glide注解处理器错误。

GlideExtension注解类可以定义两种类型的扩展方法：
1. [GlideOption](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideOption.html)：给RequestOptions添加自定义的选项。
2. [GlideType](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideType.html)：添加新的资源类型的支持（GIFs，SVG etc）。

### GlideOption

[GlideOption](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideOption.html)注解静态方法扩展[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)。**GlideOption**被用在：
1. 定义一个在程序中频繁被用到选项组。
2. 添加新的选项，通常结合Glide的[Option](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/Option.html)类使用。

定义选项组，您可以这样写：
```
@GlideExtension
public class MyAppExtension {
  // Size of mini thumb in pixels.
  private static final int MINI_THUMB_SIZE = 100;

  private MyAppExtension() { } // utility class

  @GlideOption
  public static void miniThumb(RequestOptions options) {
    options
      .fitCenter()
      .override(MINI_THUMB_SIZE);
  }
```

在[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)的子类中生成一个如下的方法：
```
public class GlideOptions extends RequestOptions {
  
  public GlideOptions miniThumb() {
    MyAppExtension.miniThumb(this);
  }

  ...
}
```

您可以在方法包含许多希望添加的参数，只需要保证第一个参数是[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)：
```
@GlideOption
public static void miniThumb(RequestOptions options, int size) {
  options
    .fitCenter()
    .override(size);
}
```

附加的参数将作为参数添加到生成方法中：
```
public GlideOptions miniThumb(int size) {
  MyAppExtension.miniThumb(this);
}
```

接着，您可以使用生成的GlideApp类调用您自定义类型：
```
GlideApp.with(fragment)
   .load(url)
   .miniThumb(thumbnailSize)
   .into(imageView);
```

使用**GlideOption**注解的方法要求是静态的，返回类型为void。请注意：生成的方法将不适用于标准的**Glide**跟**RequestOptions**类。

### GlideType

[GlideType](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideType.html)注解扩展[RequestManager](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html)的静态方法。GlideType注解的方法允许您添加新类型的支持，包括指定默认选项。

比如，添加对GIF的支持，您可以增加GlideType方法：
```
@GlideExtension
public class MyAppExtension {
  private static final RequestOptions DECODE_TYPE_GIF = decodeTypeOf(GifDrawable.class).lock();

  @GlideType(GifDrawable.class)
  public static void asGif(RequestBuilder<GifDrawable> requestBuilder) {
    requestBuilder
      .transition(new DrawableTransitionOptions())
      .apply(DECODE_TYPE_GIF);
  }
}
```

这样将会产生一个包含如下方法的[RequestManager](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html)实现。
```
public class GlideRequests extends RequesetManager {

  public RequestBuilder<GifDrawable> asGif() {
    RequestBuilder<GifDrawable> builder = as(GifDrawable.class);
    MyAppExtension.asGif(builder);
    return builder;
  }
  
  ...
}
```

然后，您可以使用生成的GlideApp类来调用自定义的方法：
```
GlideApp.with(fragment)
  .asGif()
  .load(url)
  .into(imageView);
```
由**GlideType**注解的方法必须将[RequestBuilder<T>](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html)作为第一个参数，<T>是符合GlideType注解提供的类型。注解方法必须定义在[GlideExtension](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideExtension.html)注解的类中并且要求是静态的，返回类型为void。