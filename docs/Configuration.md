# 配置
为了Glide的配置可以正常的工作，库跟程序需要执行一序列的步骤。注意库不希望注册不需要的附加的组件。
## 库
库必须：
1. 添加一个或多个[LibraryGlideModule]()实现
2. 添加[@GlideModule]()注解给每个[LibraryGlideModule]()实现
3. 添加Glide依赖注解处理器
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
使用[@GlideModule]()注解需要Glide注解依赖库：
```
compile 'com.github.bumptech.glide:annotations:4.0.0-RC1'
```
### 应用
应用必须：
1. 添加一个合适的[AppGlideModule]()实现
2. 添加一个或多个[LibraryGlideModule]()实现
3. 给[AppGlideModule]()实现类和所有的[LibraryGlideModule]()实现类添加[@GlideModule]()注解.
4. 添加Glide注解处理类依赖
5. 为[AppGlideModules]()添加混淆保持
在Glide的[Flickr sample app]()的一个[AppGlideModule]()例子：
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
最后，你应该在**proguard.cfg**保持AppGlideModule实现：
```
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
```
## 应用选项
Glide允许应用使用[AppGlideModule]()实现完全控制Glide的内存跟磁盘缓存用法。Glide尝试给大多数应用提供合理的默认值，但是对于某些应用，它需要自定义这些值。一定要衡量避免任何性能下降的改变。
### 内存缓存
默认情况下，Glide使用[LruResourceCache]()，一个内存缓存接口的默认实现使用固定的内存和LRU算法。[LruResourceCache]()的尺寸由Glide的[MemorySizeCalculator]()类决定，它可以查看设备内存是否不足跟屏幕分辨率。
应用可以在[AppGlideModule]()类的[applyOptions(Context, GlideBuilder)]()方法中配置[MemorySizeCalculator]()定制化[内存缓存]()尺寸。
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
应用可以直接重写缓存大小：
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
应用可以提供他们自己的[内存缓存]()实现类：
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
Glide使用[DiskLruCacheWrapper]()作为默认的[磁盘缓存]()。[DiskLruCacheWrapper]()是使用LRU算法的固定的磁盘缓存大小。默认的磁盘缓存大小是250MB并且在程序缓存文件夹的特定的目录。
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
程序可以改变磁盘缓存大小，不管是内部的还是外部的：
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
程序可以改变外部存储或者内部存储的缓存文件夹的名字：
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
程序可以选择实现[DiskCache]()接口并提供他们自己的[DiskCache.Factory]()实例。Glide使用工厂接口在后台线程开启[磁盘缓存]()。缓存可以做I/O操作比如：检查目标目录的存在没有违反在严格模式。
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
应用和库都可以注册一些继承Glide方法的组件，可用的组件包括：
1. [ModelLoader]()加载自定义模型（URL， URI， 任意的POJO）和数据（输入流，文件描述）
2. [ResourceDecoder]()解码新的资源（Drawable，Bitmap）或者新的数据类型（输入流，文件描述）
3. [Encoder]()写数据（输入流，文件描述）Glide的磁盘缓存
4. [ResourceTranscoder]()从一种资源（BitmapResource）转换为其他类型的资源（DrawableResource）
5. [ResourceEncoder]()写资源（BitmapResource, DrawableResource）Glide的磁盘缓存
注册组件使用[Registry]()类。比如：添加**ModelLoader**可以为自定义模型对象获得一个输入流。
```
@GlideModule
public class YourAppGlideModule extends AppGlideModule {
  @Override
  public void registerComponents(Context context, Registry registry) {
    registry.append(Photo.class, InputStream.class, new CustomModelLoader.Factory());
  }
}
```
任意数量的组件可以注册在单一的**GlideModule**。[ModelLoader]()和[ResourceDecoder]()可以有多个相同类型参数的实现类。
注册的组件列表，包括那些Glide默认注册的和那些在模型中注册的定义的负载路径。每个附件路径是一步步模型提供的负载资源类型通过[as()]()指定的类型。负载路径粗略符合下一步骤：
1. 模型->数据（由**ModelLoader**处理）
2. 数据->资源（由**ResourceDecoder**处理）
3. 资源->转换资源（可选，由**ResourceTranscoder**处理）
**Encoder**第一步时，写入数据到Glide磁盘缓存。**ResourceEncoder**在第二步时，写资源到Glide磁盘缓存。
当一个请求开启时，Glide将尝试从模型到请求资源的所有可用的路径。如果任何负载路径成功则请求成功。










