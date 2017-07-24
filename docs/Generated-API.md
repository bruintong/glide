# Generated API
Glide v4通过注解处理器生成一个API，允许程序以在一个优雅的API访问[RequestBuilder](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html)、[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)的所有选项跟包含的集成库。
生成的API服务于两个目的：
1. 集成库可以扩展Glide的API自定义选项。
2. 程序可以扩展Glide的API添加方法绑定常用的选项。
虽然这些工作都可以通过手动编写自定义[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)子类来实现，但这样做更具有挑战性，并且会产生一个不太优雅的API。
## 开始使用
### Java
为了使用Generated API，需要两个步骤：
1. 添加Glide的注解处理器依赖：
```
repositories {
  mavenCentral()
}
   
dependencies {
  annotationProcessor 'com.github.bumptech.glide:compiler:4.0.0-RC1'
}
```
2. 在程序中包含[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现类：
```
package com.example.myapp;
   
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
   
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {}
```
[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现类必须使用[@GlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideModule.html)注解。如果没有使用注解，该模块将不会被发现并且你会在**Glide**的log标记中看到一条无法找到模块的警告。
使用Generated API
生成的API跟[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)在同一包下，默认的类名为GlideApp。使用该API，程序可以在所有的加载中用**GlideApp.with()**代替**Glide.with()**。
```
GlideApp.with(fragment)
   .load(myUrl)
   .placeholder(R.drawable.placeholder)
   .fitCenter()
   .into(imageView);
```
Generated API可以直接调用**fitCenter()**和**placeholder()**方法，而不必作为单独的[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)对象传入到**Glide.with()**中。
### GlideExtension
Glide的Generated API可以被程序跟库扩展。扩展使用静态注解的方法添加新的选项，修改已有的选项或者新增类型。
[GlideExtension](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideExtension.html)注解标记一个类扩展了Glide的API。注解需要出现在任何扩展了Glide API的类。如果该注解不存在，那么注解方法会被忽略。
通过GlideExtension注解标记的类被认为是工具类。这些类应该包含私有的空的构造函数，应该是final的并且只包含静态方法，可能包含静态变量跟引用其他类或者对象。
一个程序可以实现多个GlideExtension注解类。库也可以实现任意数量的GlideExtension注解类。当一个AppGlideModule被找到时，所有的有效的GlideExtensions将被合并到一个的API里。冲突会导致Glide注解处理器错误。
GlideExtension注解类可以定义两种类型的扩展方法：
1. [GlideOption]() - 给RequestOptions添加自定义的选项。
2. [GlideType]() - 添加新的资源类型的支持（GIFs，SVG etc）。
### GlideOption
[GlideOption]()注解静态方法扩展[RequestOptions]()。**GlideOption**被用在：
1. 定义一个在程序中频繁被用到选项组。
2. 添加新的选项，通常结合Glide的[Option]()类。
定义选项组，你可以这样写：
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
在[RequestOptions]()的子类中生成的方法看起来像这样：
```
public class GlideOptions extends RequestOptions {
  
  public GlideOptions miniThumb() {
    MyAppExtension.miniThumb(this);
  }

  ...
}
```
你可以在方法包含许多希望添加的参数，只需要保证第一个参数是[RequestOptions]()：
```
@GlideOption
public static void miniThumb(RequestOptions options, int size) {
  options
    .fitCenter()
    .override(size);
}
```
新增的参数将会添加到生成方法中作为参数
```
public GlideOptions miniThumb(int size) {
  MyAppExtension.miniThumb(this);
}
```
接着，你可以使用生成的GlideApp类使用你自定义类型：
```
GlideApp.with(fragment)
   .load(url)
   .miniThumb(thumbnailSize)
   .into(imageView);
```
使用**GlideOption**注解的方法要求是静态的，返回类型为void。注意：生成的方法不会出现在标准的**Glide**跟**RequestOptions**类中。
### GlideType
[GlideType](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideType.html)注解扩展[RequestManager](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html)的静态方法。GlideType注解的方法允许你添加新类型的支持，包括指定默认值。
比如，添加GIF的支持，你可以增加GlideType方法：
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
这样将会生成一个包含如下方法的[RequestManager](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html)实现类。
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
接着你可以使用生成的GlideApp类来调用自定义的方法：
```
GlideApp.with(fragment)
  .asGif()
  .load(url)
  .into(imageView);
```
由**GlideType**注解的方法必须将[RequestBuilder<T>](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html)作为第一个参数，<T>是符合GlideType注解提供的类型。注解方法必须定义在[GlideExtension](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideExtension.html)注解的类中并且要求是静态的，返回类型为void。







