# 入门指南

## 基本用法

使用Glide加载图像很容易，通常情况下只需要一行代码：
```
Glide.with(fragment)
    .load(myUrl)
    .into(imageView);
```
取消您不再需要的负载也很简单：
```
Glide.with(fragment).clear(imageView);
```
尽管清除不再需要的负载是好的做法，但您不需要这样做。事实上，当您通过[Glide.with()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/Glide.html#with-android.app.Fragment-)方法传入的Activity或者Fragment被销毁时，Glide将自动清除负载并且回收负载使用的任何资源。

## 应用

应用程序可以添加恰当的注解给[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现，以生成灵活的API，其中包含大多数选项，包括在集成库中定义的选项。
```
package com.example.myapp;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {}
```
默认情况下，生成的API会跟您的[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现在同一包下，类名为GlideApp。使用该API，应用程序可以在所有的负载中用**GlideApp.with()**代替**Glide.with()**。
```
GlideApp.with(fragment)
   .load(myUrl)
   .placeholder(placeholder)
   .fitCenter()
   .into(imageView);
```

有关更多信息，请参阅Glide的[生成的API](../docs/Generated-API.md)页面。

## ListView和RecyclerView

在ListView或者RecyclerView中加载图像使用相同的加载行，就像加载到单个的View一样。Glide会处理View的复用跟请求的自动取消。
```
@Override
public void onBindViewHolder(ViewHolder holder, int position) {
    String url = urls.get(position);
    Glide.with(fragment)
        .load(url)
        .into(holder.imageView);
}
```

您不需要检测您传递的URL是否为空，如果URL为空，Glide会清除这个View或者使用您指定的占位图像，或者回调的图像。

Glide唯一的要求是，对于任何可重用的目标View，您可以复用之前的View加载新的图像或者调用**clare()**API来显示的清除。
```
@Override
public void onBindViewHolder(ViewHolder holder, int position) {
    if (isImagePosition(position)) {
        String url = urls.get(position);
        Glide.with(fragment)
            .load(url)
            .into(holder.imageView);
    } else {
        Glide.with(fragment).clear(holder.imageView);
        holder.imageView.setImageDrawable(specialDrawable);
    }
}
```

通过调用[clear()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html#clear-com.bumptech.glide.request.target.Target-)或者**into(view)**方法作用在View上，您可以取消负载或者保证Glide在调用完成之后不会改变View的内容。如果您忘记调用[clear()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html#clear-com.bumptech.glide.request.target.Target-)方法并且没有在该View上开启新的图像负载，那么在复用之前的位置的View时，您将不能指定Drawable，并且可能会加载到老的图像从而改变View的内容。

虽然，我们在这里展示的示例是RecyclerView，但同样的原则也适用于ListView。