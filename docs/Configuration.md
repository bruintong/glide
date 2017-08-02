# 配置

为了Glide的配置可以正常的工作，库跟应用程序需要执行一序列的步骤。请注意，库不希望注册不需要的附加的组件。

## 库

库必须：
1. 添加一个或多个[LibraryGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/LibraryGlideModule.html)实现
2. 添加[@GlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideModule.html)注解给每个[LibraryGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/LibraryGlideModule.html)实现
3. 添加Glide注解处理器依赖关系

一个使用OkHttp集成库的Glide例子如下所示：
```
@GlideModule
public final class OkHttpLibraryGlideModule extends LibraryGlideModule {
  @Override
  public void registerComponents(Context context, Registry registry) {
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
  }
}
```

使用[@GlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideModule.html)注解需要Glide注解依赖库：
```
compile 'com.github.bumptech.glide:annotations:4.0.0-RC1'
```

### 应用程序

应用程序必须：
1. 添加一个合适的[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现
2. 添加一个或多个[LibraryGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/LibraryGlideModule.html)实现
3. 给[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现类和所有的[LibraryGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/LibraryGlideModule.html)实现类添加[@GlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/annotation/GlideModule.html)注解
4. 添加Glide注解处理类依赖关系
5. 为[AppGlideModules](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)添加proguard.cfg的keep

在Glide的[Flickr sample app](https://github.com/bumptech/glide/blob/master/samples/flickr/src/main/java/com/bumptech/glide/samples/flickr/FlickrGlideModule.java)的一个[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)例子：
```
@GlideModule
public class FlickrGlideModule extends AppGlideModule {
  @Override
  public void registerComponents(Context context, Registry registry) {
    registry.append(Photo.class, InputStream.class, new FlickrModelLoader.Factory());
  }
}
```

包含Glide注解处理器要求Glide注解依赖和注解处理器：
```
compile 'com.github.bumptech.glide:annotations:4.0.0-RC1'
annotationProcessor 'com.github.bumptech.glide:compiler:4.0.0-RC1'
```

最后，您应该在**proguard.cfg**保持AppGlideModule实现：
```
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
```

## 应用选项

Glide允许应用程序使用AppGlideModule实现完全控制Glide的内存跟磁盘缓存用法。Glide尝试给大多数应用程序提供合理的默认值，但是对于某些应用程序，需要自定义这些值。一定要衡量避免任何性能下降的修改。

### 内存缓存

默认情况下，Glide使用[LruResourceCache](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/cache/LruResourceCache.html)，一个内存缓存接口的默认实现使用固定的内存和LRU算法。[LruResourceCache](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/cache/LruResourceCache.html)的大小由Glide的[MemorySizeCalculator](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/cache/MemorySizeCalculator.html)类决定，它可以查看设备内存是否不足以及屏幕的分辨率。

应用程序可以在AppGlideModule类的applyOptions(Context, GlideBuilder)方法中配置MemorySizeCalculator定制化内存缓存的大小。
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
        .setMemoryCacheScreens(2)
        .build();
    builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
  }
}
```

应用程序可以直接覆盖缓存大小：
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
    builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
  }
}
```

应用程序可以提供他们自己的内存缓存实现类：
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    builder.setMemoryCache(new YourAppMemoryCacheImpl());
  }
}
```

### 磁盘缓存

Glide使用[DiskLruCacheWrapper](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/cache/DiskLruCacheWrapper.html)作为默认的磁盘缓存。DiskLruCacheWrapper是使用LRU算法的固定的磁盘缓存大小。默认的磁盘缓存大小是250MB并且在程序缓存文件夹的特定的目录。

如果显示的媒体文件是公开的，应用可以将位置改变为外部存储（包括没有认证的网站，搜索引擎等等）：
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    builder.setDiskCache(new ExternalDiskCacheFactory(context));
  }
}
```

应用程序可以改变磁盘缓存大小，不管是内部的还是外部的：
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    int diskCacheSizeBytes = 1024 * 1024 * 100; // 100 MB
    builder.setDiskCache(new InternalDiskCacheFactory(context, diskCacheSizeBytes));
  }
}
```

应用程序可以改变外部存储或者内部存储的缓存文件夹的名字：
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    int diskCacheSizeBytes = 1024 * 1024 * 100; // 100 MB
    builder.setDiskCache(
        new InternalDiskCacheFactory(context, "cacheFolderName", diskCacheSizeBytes));
  }
}
```

应用程序可以选择实现DiskCache接口并提供他们自己的[DiskCache.Factory](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/cache/DiskCache.Factory.html)实例。Glide使用工厂接口在后台线程开启磁盘缓存。缓存可以做I/O操作。例如：检查目标目录的存在没有违反在严格模式。
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    builder.setDiskCache(new DiskCache.Factory() {
        @Override
        public DiskCache build() {
          return new YourAppCustomDiskCache();
        }
    });
  }
}
```

### 注册组件

应用程序和库都可以注册一些继承Glide方法的组件，可用的组件包括：
1. ModelLoader加载自定义模型（URL， URI， 任意的POJO）和数据（输入流，文件描述）
2. ResourceDecoder解码新的资源（Drawable，Bitmap）或者新的数据类型（输入流，文件描述）
3. Encoder写数据（输入流，文件描述）Glide的磁盘缓存
4. ResourceTranscoder从一种资源（BitmapResource）转换为其他类型的资源（DrawableResource）
5. ResourceEncoder将资源（BitmapResource, DrawableResource）写入Glide的磁盘缓存

注册组件使用Registry类。比如：添加**ModelLoader**可以为自定义模型对象获得一个输入流。
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void registerComponents(Context context, Registry registry) {
    registry.append(Photo.class, InputStream.class, new CustomModelLoader.Factory());
  }
}
```

任意数量的组件可以注册在单一的**GlideModule**。ModelLoader和ResourceDecoder可以有多个相同类型参数的实现类。

注册的组件列表，包括那些Glide默认注册的和那些在模型中注册的定义的负载路径。每个附件路径是一步步模型提供的负载资源类型通过[as()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html#as-java.lang.Class-)指定的类型。负载路径粗略符合下一步骤：
1. 模型->数据（由**ModelLoader**处理）
2. 数据->资源（由**ResourceDecoder**处理）
3. 资源->转换资源（可选，由**ResourceTranscoder**处理）

**Encoder**第一步时，写入数据到Glide磁盘缓存。**ResourceEncoder**在第二步时，写资源到Glide磁盘缓存。

当一个请求开启时，Glide将尝试从模型到请求资源的所有可用的路径。只要有任何一个负载路径成功则请求成功。只有所有负载路径都失败请求才失败。

注册表中的prepend()，append()和replace()方法可用于设置Glide将尝试每个ModelLoader和ResourceDecoder的顺序。通过确保首先注册处理最常见类型的ModelLoaders和ResourceDecoders，可以使请求更高效。组件排序还可以允许您注册处理模型或数据的特定子集的组件（即只有某些类型的Uris或仅某些图像格式），同时还具有附加的全部组件来处理其余部分。

### 模块类和注解

Glide v4依赖于两个类AppGlideModule和LibraryGlideModule来配置Glide单例。允许这两个类注册其他组件，如：ModelLoaders，ResourceDecoders等。只有AppGlideModules允许配置应用程序特定的设置，如缓存实现和大小。

#### AppGlideModule

所有应用程序必须添加AppGlideModule实现，即使应用程序没有更改任何其他设置或在AppGlideModule中实现任何方法。 AppGlideModule实现作为一个信号，允许Glide的注解处理器与所有找到的LibraryGlideModules一起生成单个组合类。

在给定的应用程序中只能有一个AppGlideModule实现（在编译时有多个产生错误）。因此，库绝不能提供AppGlideModule实现。

#### @GlideModule

为了让Glide正确找到AppGlideModule和LibraryGlideModule实现，这两个类的所有实现都必须用@GlideModule注解。注解将允许Glide的注解处理器在编译时发现所有实现。

#### 注解处理器

此外，为了找到AppGlideModule和LibraryGlideModules，所有的库和应用程序还必须包含对Glide的注解处理器的依赖。

### 冲突

应用程序可能依赖于多个库，每个库可能包含一个或多个LibraryGlideModules。在极少数情况下，这些LibraryGlideModule可能定义了冲突的选项，或者包括应用程序希望避免的行为。应用程序可以通过将@Excludes注解添加到其AppGlideModule来解决这些冲突或避免不必要的依赖关系。

例如，如果您依赖于您想要避免的LibraryGlideModule的库，请传入com.example.unwanted.GlideModule：
```
@Excludes（ “com.example.unwanted.GlideModule”）
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {}
```

您还可以排除多个模块：

```
@Excludes（{“com.example.unwanted.GlideModule”，“com.example.conflicing.GlideModule”}）
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {}
```

如果您仍在从Glide v3迁移过程中，可以使用@Excludes来排除LibraryGlideModules和已弃用的GlideModule实现。

### 清单解析

为了保持与Glide v3的GlideModules的向后兼容性，Glide仍然从应用程序和任何包含的库中分析AndroidManifest.xml文件，并将包括清单中列出的任何旧的GlideModules。虽然此功能将在以后的版本中被删除，但我们现在已经保留了行为以减轻转换。

如果您已经迁移到Glide v4 AppGlideModule和LibraryGlideModule，则可以完全禁用清单解析。这样做可以提高Glide的初始启动时间，并避免尝试解析元数据时出现一些潜在的问题。要禁用清单解析，请覆盖AppGlideModule实现中的isManifestParsingEnabled()方法：
```
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
  @Override
  public boolean isManifestParsingEnabled（）{
    return false;
  }
}
```