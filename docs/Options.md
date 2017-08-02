# 选项

## 请求选项

Glide中大多数的选项可以使用[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html)类和[apply()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#apply-com.bumptech.glide.request.RequestOptions-)方法来应用。

使用请求选项（其中包括）:
- 占位符
- 转换
- 缓存策略
- 组件特定选项，如编码或解码质量，Bitmap配置。

例如，要应用[CenterCrop](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/resource/bitmap/CenterCrop.html)、[Transformation](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/TransitionOptions.html)，您的代码可以这样编写：
```
import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

Glide.with(fragment)
    .load(url)
    .apply(centerCropTransform(context))
    .into(imageView);
```

从[RequestOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/TransitionOptions.html)引入静态方法将是更加灵活的方式。

如果您始终想要在程序的不同部分共享选项，您可以实例化一个新的**RequestOptions**对象并传递给每个负载操作：
```
RequestOptions cropOptions = new RequestOptions().centerCrop(context);
...
Glide.with(fragment)
    .load(url)
    .apply(cropOptions)
    .into(imageView);
```

[apply()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#apply-com.bumptech.glide.request.RequestOptions-)可以被调用多次，所以**RequestOptions**可以组合。如果两个**RequestOptions**对象包含冲突的，那么最后设置的**RequestOptions**才会生效。

## 过渡选项

过渡选项决定在请求完成后，将会发生什么。

应用过渡选项：
- View淡入
- 占位符淡出
- 没有过渡效果

没有过渡效果，您的图像会突然出现，立即替代之前的图像。为了避免突然改变，您可以淡入View，或者使用过渡选项在Drawable之间淡入淡出。

例如，应用淡入淡出：
```
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

Glide.with(fragment)
    .load(url)
    .transition(withCrossFade())
    .into(view);
```

与**请求选项**不同，**过渡选项**是您使用Glide加载绑定资源的特殊类型。

因此，当您请求**Bitmap**时，您需要使用[BitmapTransitionOption](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/resource/bitmap/BitmapTransitionOptions.html)而不是[DrawableTransitionOptions](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/resource/drawable/DrawableTransitionOptions.html)。因此，当您请求**Bitmap**，您可能需要的是简单的淡入，而不是淡入淡出。

## RequestBuilder

在Glide中[RequestBuilder](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html)是请求的核心，负责携带您的选项跟您请求URL/模型开启新的负载。

使用**RequestBuilder**指定：
- 要加载的资源类型（Bitmap，Drawable等等）
- 从URL/模型中加载资源
- 加载资源到View
- 任何您想申请的**RequestOption**对象
- 任何您想申请的**TransitionOption**对象
- 任何您想加载的缩略图

每次调用**Glide.with()**您都会产生一个**RequestManager**对象：
```
RequestManager requestManager = Glide.with(this);
```

### 选择资源类型

**RequestManager**可以指定加载的资源类型。默认情况下，获取的是Drawable RequestBuilder。您可以使用**as...**方法改变请求类型。例如，您可以调用**asBitmap()**您将获取一个**Bitmap RequestBuilder**。
```
RequestBuilder<Bitmap> requestBuilder = Glide.with(fragment).asBitmap();
```

### 应用请求选项

如上所示，通过[apply()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#apply-com.bumptech.glide.request.RequestOptions-)方法应用**请求选项**，使用[transition()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#transition-com.bumptech.glide.TransitionOptions-)方法应用**过渡选项**：
```
RequestBuilder<Drawable> requestBuilder = Glide.with(this).asDrawable();
requestBuilder.apply(requestOptions);
requestBuilder.transition(transitionOptions);
```

启动多个负载时，RequestBuilder可以被重用：
```
RequestBuilder<Drawable> requestBuilder =
        Glide.with(fragment)
            .asDrawable()
            .apply(requestOptions);

for (int i = 0; i < numViews; i++) {
   ImageView view = viewGroup.getChildAt(i);
   String url = urls.get(i);
   requestBuilder.load(url).into(view);
}
```