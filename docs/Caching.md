# 缓存

## Glide缓存

默认情况下，Glide会在请求图片之前进行层层检查：
1. 活跃的资源 - 该图片是否正在其他视图显示？
2. 内存缓存 - 图片是否最近被加载并且仍然在内存中？
3. 资源 - 图片是否已经解码，转换并且已经写入到磁盘中？
4. 数据 - 图片的数据曾经在磁盘缓存中出现？
前两步是检查资源是否在内存中。如果是立即返回该图片。后两步是检查该图片是否在磁盘中，如果是也很快返回，不过是异步的。
如果所有四步都失败了，Glide会返回原始资源去检索数据（原始文件，URI，URL等等）。
缓存键
在Glide 4中，所有的缓存键包含至少两个元素：
1. 必要的模型负载（File， URI， URL）
2. 可选的签名
事实上，对于1-3的缓存键（活跃资源，内存缓存，磁盘资源缓存）还包括其他的一些数据包括：
1. 宽度和高度
2. 可选的装换
3. 任何添加的选项
4. 请求类型（Bitmap， GIF等等）
活跃资源和内存缓存中使用的键跟缓存在磁盘中的资源的内存选项有些微不同，像影响bitmap的配置或者解码时间参数。
为了生成磁盘缓存键的名字，各个元素生成唯一的字符串散列键，然后作为磁盘缓存文件的名称。
## 缓存配置
Glide提供了一些选项，允许你选择在Glide的每次基础请求时怎么跟负载交互。
### 磁盘缓存策略
使用[diskCacheStrategy]()方法可以为每个请求应用[磁盘缓存策略]()，可用的策略可以防止负载使用或者写入磁盘高速缓存或者选择那些负载返回的未修改原始数据来缓存，或者转换你的负载产生的缩略图，或者两者都具备。
默认策略，[自动匹配](), 尝试为本地或者远程图片使用最优策略。当你加载远程数据（像从URL加载）时，**自动匹配**只会保存负载返回的未修改的原始数据，因为相比调整磁盘数据的尺寸，下载远程数据更加昂贵。对于本地资源，自动匹配只会存储转换缩略图，因为如果你需要生成缩略图尺寸或者类型，检索原始数据花费更少。
应用磁盘缓存策略的例子：
```
GlideApp.with(fragment)
  .load(url)
  .diskCacheStrategy(DiskCacheStrategy.ALL)
  .into(imageView);
```
### 只从缓存中加载
在一些情况下，如果图片不在缓存中，你可能希望加载失败。因此，你可以在每个基础负载中使用[onlyRetrieveFromCache]()方法：
```
GlideApp.with(fragment)
  .load(url)
  .onlyRetrieveFromCache(true)
  .into(imageView);
```
如果图片可以在内存缓存或者磁盘缓存中找到，那么它会被成功加载。否则，如果选项设置成true，加载会失败。
### 跳过缓存
如果你希望确保特定的请求跳过磁盘缓存跟内存缓存，Glide提供了一些替代选择。只是跳过内存缓存，可以使用[skipMemoryCache]()：
```
GlideApp.with(fragment)
  .load(url)
  .skipMemoryCache(true)
  .into(view);
```
只是跳过磁盘缓存，使用[DiskCacheStrategy.NONE]()：
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
一般来说，你要尽量避免跳过缓存。从缓存中加载图像要比检索，解码，转换并创建一个新的缩略图快得多。
如果你想为缓存中的某一项更新条目，你可以查看文档[invalidation]()
#### 实现
如果可用的选项不满足你的需求，你可以自定义你的[DiskCache]()实现。查看[configuration]()获取细节。
## 缓存失效
由于磁盘缓存是散列键，也没有好的办法简单的删除在磁盘上的所有的对应特定的URL或者文件路径的缓存文件。比较简单的方式是如果你只允许加载或者缓存原始图像，但只要Glide缓存缩略图并且提供各种转换，它们每个都将在磁盘中生成新的文件，跟踪下载并且删除每个版本的缓存图像是困难的。
### 自定义缓存失效
因为改变标识符是困难的或者说不可能的，所以Glide提供了[signature()]() API和额外的数据使你可以控制缓存键。签名对于媒体存储内容很有效，以及任何你可以维护版本的元数据。
- 媒体存储内容。对于媒体存储内容，你可以使用Glide的[MediaStoreSignature]()类作为你的签名。**MediaStoreSignature**允许你添加数据的修改日期时间，文件类型，和一个媒体存储项目为缓存键定位。这三个属性可靠的捕获编辑和更新允许你缓存媒体存储缩略图。
- 文件。你可以使用[ObjectKey]()添加文件日期修改时间。
- URL。虽然使URL失效最好的方式是确保服务器改变URL并且URL指代的内容改变是更新客户端。你可以使用[Objectkey]()添加任意的元数据（如版本号）代替。
传入签名加载的例子：
```
GlideApp.with(yourFragment)
    .load(yourFileDataModel)
    .signature(new ObjectKey(yourVersionMetadata))
    .into(yourImageView);
```
媒体存储签名也是媒体存储的简单数据
```
GlideApp.with(fragment)
    .load(mediaStoreUri)
    .signature(new MediaStoreSignature(mimeType, dateModified, orientation))
    .into(view);
```
你可以实现[Key]()接口来定义自己的签名。确保实现了**equals()**，**hashCode()**和**updateDiskCacheKey**方法。
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
记住，避免降低性能，当你想要加载图像时，你在后台批量加载任何版本的元数据时可行的。
如果所有都失败了你不能改变标识符也不能跟踪任何版本的元数据，你可以使用[diskCacheStrategy()]()和[DiskCacheStrategy.NONE]()关闭磁盘缓存。







