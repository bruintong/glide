# 缓存

## Glide缓存

默认情况下，Glide会在请求图像之前检查多级缓存：
1. 活跃的资源：该图像是否正在其他视图显示？
2. 内存缓存：图像是否最近被加载并且仍然在内存中？
3. 资源：图像是否已经解码，转换并且已经写入到磁盘中？
4. 数据：图像的数据曾经在磁盘缓存中出现？

前两个步骤是检查资源是否在内存中。如果是， 立即返回该图像。后两个步骤是检查该图像是否在磁盘中，如果是也很快返回，不过是异步的。

如果所有四个步骤都没有找到图像，Glide会返回原始资源去检索数据（原始文件，URI，URL等）。

## 缓存键

在Glide 4中，所有的缓存键至少包含两个元素：
1. 请求都模型（File， URI， URL）
2. 可选的签名

实际上，步骤1-3的缓存键（活跃资源，内存缓存，磁盘资源缓存）还包括许多其他数据，其中包括：
1. 宽度和高度
2. 可选的转换
3. 任何添加的选项
4. 请求类型（Bitmap， GIF等）

用于活跃资源和内存缓存中键跟缓存在磁盘中的键有些微不同，像那些影响Bitmap的配置或解码时间参数的选项。

为了生成磁盘缓存键的名字，各个元素生成唯一的散列字符串，然后作为磁盘缓存的文件名。

## 缓存配置

Glide提供了一些选项，允许您选择在Glide的每次基础请求时怎么跟负载交互。

### 磁盘缓存策略

使用[DiskCacheStrategy](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html#diskCacheStrategy-com.bumptech.glide.load.engine.DiskCacheStrategy-)方法可以为每个请求应用[磁盘缓存策略](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/DiskCacheStrategy.html)，可用的策略可以防止负载使用或者写入磁盘高速缓存或者选择那些负载返回的未修改原始数据来缓存，或者转换您的负载产生的缩略图，或者两者都具备。

默认策略是[自动匹配](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/DiskCacheStrategy.html#AUTOMATIC)，尝试为本地或者远程图像使用最优策略。当您加载远程数据（如从URL加载）时，**自动匹配**只会保存负载返回的未修改的原始数据，因为相比调整磁盘数据的尺寸，下载远程数据更加昂贵。对于本地资源，自动匹配只会存储转换缩略图，因为如果您需要生成缩略图尺寸或者类型，检索原始数据花费更少。

应用磁盘缓存策略的例子：
```
GlideApp.with(fragment)
  .load(url)
  .diskCacheStrategy(DiskCacheStrategy.ALL)
  .into(imageView);
```

### 只从缓存中加载

在一些情况下，如果图像不在缓存中，您可能希望加载失败。因此，您可以在每个基础负载中使用[onlyRetrieveFromCache](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html#onlyRetrieveFromCache-boolean-)方法：
```
GlideApp.with(fragment)
  .load(url)
  .onlyRetrieveFromCache(true)
  .into(imageView);
```

如果图像可以在内存缓存或者磁盘缓存中找到，那么它会被成功加载。否则，如果选项设置成true，加载会失败。

### 跳过缓存

如果您希望确保特定的请求跳过磁盘缓存跟内存缓存，Glide提供了一些替代选择。只是跳过内存缓存，可以使用[skipMemoryCache](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html#skipMemoryCache-boolean-)：
```
GlideApp.with(fragment)
  .load(url)
  .skipMemoryCache(true)
  .into(view);
```

只是跳过磁盘缓存，使用[DiskCacheStrategy.NONE](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/DiskCacheStrategy.html#NONE)：
```
GlideApp.with(fragment)
  .load(url)
  .diskCacheStrategy(DiskCacheStrategy.NONE)
  .into(view);
```

这些选项可以一起使用：
```
GlideApp.with(fragment)
  .load(url)
  .diskCacheStrategy(DiskCacheStrategy.NONE)
  .skipMemoryCache(true)
  .into(view);
```

一般来说，您要尽量避免跳过缓存。从缓存中加载图像要比检索，解码，转换并创建一个新的缩略图快得多。

如果您想为缓存中的某一项更新条目，您可以查看文档[invalidation](http://bumptech.github.io/glide/doc/caching.html#cache-invalidation)

#### 实现

如果可用的选项不满足您的需求，您可以自定义您的[DiskCache](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/cache/DiskCache.html)实现。查看[configuration](http://bumptech.github.io/glide/doc/configuration.html#disk-cache)获取细节。

## 缓存失效

由于磁盘缓存是散列键，所以也没有好的办法简单的删除在磁盘上的所有的对应特定的URL或者文件路径的缓存文件。比较简单的方式是如果您只允许加载或者缓存原始图像，但只要Glide缓存缩略图并且提供各种转换，它们每个都将在磁盘中生成新的文件，跟踪下载并且删除每个版本的缓存图像是困难的。

### 自定义缓存失效

通常很难或者不可能改变标识符，所以Glide提供了[signature()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html#signature-com.bumptech.glide.load.Key-) API和额外的数据，使您可以控制缓存键。签名适用于媒体存储内容，以及任何您可以维护版本的元数据。
- 媒体存储内容：对于媒体存储内容，您可以使用Glide的[MediaStoreSignature](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/signature/MediaStoreSignature.html)类作为您的签名。**MediaStoreSignature**允许您添加数据的修改日期时间，文件类型，和一个媒体存储项目为缓存键定位。这三个属性可靠的捕获编辑和更新，从而允许您缓存媒体存储缩略图。
- 文件：您可以使用[ObjectKey](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/signature/ObjectKey.html)添加文件日期修改时间。
- URL：虽然使URL失效最好的方式是确保服务器改变URL并且URL指代的内容改变时更新客户端。您可以使用[Objectkey](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/signature/ObjectKey.html)添加任意的元数据（如版本号）代替。

将签名传递给负载的例子：
```
GlideApp.with(yourFragment)
    .load(yourFileDataModel)
    .signature(new ObjectKey(yourVersionMetadata))
    .into(yourImageView);
```

媒体存储签名也是媒体存储的简单数据：

```
GlideApp.with(fragment)
    .load(mediaStoreUri)
    .signature(new MediaStoreSignature(mimeType, dateModified, orientation))
    .into(view);
```

您可以实现[Key](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/Key.html)接口来定义自己的签名。确保实现了**equals()**，**hashCode()**和**updateDiskCacheKey**方法。
```
public class IntegerVersionSignature implements Key {
    private int currentVersion;

    public IntegerVersionSignature(int currentVersion) {
         this.currentVersion = currentVersion;
    }
   
    @Override
    public boolean equals(Object o) {
        if (o instanceof IntegerVersionSignature) {
            IntegerVersionSignature other = (IntegerVersionSignature) o;
            return currentVersion = other.currentVersion;
        }
        return false;
    }
 
    @Override
    public int hashCode() {
        return currentVersion;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt(signature).array());
    }
}
```

请记住，为了避免降低性能，您需要在后台批量加载任何版本的元数据，以便在加载图像时可用。

如果一切都失败了，您不能改变标识符也不能跟踪任何版本的元数据，您可以使用[diskCacheStrategy()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html#diskCacheStrategy-com.bumptech.glide.load.engine.DiskCacheStrategy-)和[DiskCacheStrategy.NONE](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/DiskCacheStrategy.html#NONE)关闭磁盘缓存。