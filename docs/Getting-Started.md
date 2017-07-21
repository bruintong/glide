# 入门指南
## 基本用法
使用Glide进行图片加载是非常容易的事，通常只需要一行代码：
```
Glide.with(fragment)
    .load(myUrl)
    .into(imageView);
```
取消不再需要的图片加载也同样非常简单：
```
Glide.with(fragment).clear(imageView);
```
虽然，清除不再需要的加载是好的习惯，但这并非必须。事实上，Glide会在你通过[Glide.with()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/Glide.html#with-android.app.Fragment-)方法传递进来的Activity或者Fragment销毁时自动清除加载并且回收资源
## 程序
程序可以添加合适的注解给[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)实现类来生成一个优雅的API，它可以内联大多数选项，包括由集成库定义的。
```
package com.example.myapp;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {}
```
生成的API跟[AppGlideModule](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/module/AppGlideModule.html)在同一包下，默认的类名为GlideApp。使用该API，程序可以在所有的加载中用**GlideApp.with()**代替**Glide.with()**。
```
GlideApp.with(fragment)
   .load(myUrl)
   .placeholder(placeholder)
   .fitCenter()
   .into(imageView);
```
查看Glide的[Generated API](Generated-API.md)章节获取更多信息。
## ListView和RecyclerView
在ListView或者RecyclerView中加载图片到单一的View中可以使用相同的代码。Glide会处理View的复用跟请求的自动取消。
```
@Override
public void onBindViewHolder(ViewHolder holder, int position) {
    String url = urls.get(position);
    Glide.with(fragment)
        .load(url)
        .into(holder.imageView);
}
```
你不需要检查你传递的URL是否为空，如果URL为空，Glide会清除这个View或者使用你指定的占位图片，或者回调的图片。
Glide唯一的要求是，对于任何可重用的目标View，你可以复用之前的View加载新的图片或者调用**clare()**API来显示的清除。
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
通过调用[clear()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html#clear-com.bumptech.glide.request.target.Target-)或者**into(view)**方法作用在View上，你可以取消加载或者保证Glide在调用完成之后不会改变View的内容。如果你忘记调用[clear()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestManager.html#clear-com.bumptech.glide.request.target.Target-)方法并且没有在该View上开启新的图片加载，那么在复用之前的位置的View时，你将不能指定Drawable，并且可能会加载到老的图片从而改变View的内容。
虽然，刚才使用的是RecyclerView的示例，在ListView中的用法也是一样的。