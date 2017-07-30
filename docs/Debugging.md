# 调试

## 本地日志

如果您已经连接上设备了，您可以使用**adb logcat**或者IDE查看一些日志信息。您可以使用**adb shell setprop log.tag.<tag_name> <VERBOSE|DEBUG>**使用任何标签启用日志信息。VERBOSE日志信息往往更详细，包含更多有用的信息。根据标签，您可以尝试VERBOSE和DEBUG，看哪些提供最佳的日志信息。

### 请求错误

等级最高同时也最容易理解的日志信息是使用**Glide**标签。**Glide**标签会同时提供成功跟失败的请求信息以及不同级别的信息，具体取决于日志级别。VERBOSE应该用来显示成功的请求。DEBUG可以用来显示错误的细节信息。

您可以使用[setLogLevel(int)](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/GlideBuilder.html#setLogLevel-int-)来控制程序显示Glide日志的详细程度。例如，setLogLevel允许您在开发时启用更详细日志，但在发行版中关闭。

### 意外的缓存丢失

**Engine**标签提供关于请求将如何被响应的细节，包括使用内存缓存键存储相应的资源。当您尝试调试一个存放在内存中图像为什么不能在另一个地方使用时，您可以使用**Engine**日志直接比较缓存键是否相同。

对于每个请求，**Engine**标签将记录请求将从缓存，有效的资源，已存在的负载或者新的负载中完成。缓存意味着资源没有被使用，却在内存中存在。有效的资源意味着资源被其他目标使用，尤其是在View中。已存在的负载意味着资源在内存中不可用，但其他目标之前请求过该资源并且正在被加载。最后，一个新的负载意味着资源不在内存中也没有加载过，所以现在是新的负载请求。

### 缺少图像和本地日志

在某种情况下，您也许会看到图像没有被加载，并且没有Glide日志或没有请求的Engine日志。有几种可能的原因。

#### 没有开始请求

请确保您的请求有调用[into](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#into-android.widget.ImageView-)或者[submit](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#submit-int-int-)方法。如果您没有调用过其中任何一个方法，您是无法使用Glide开启请求的。

#### 缺少尺寸

如果您确定您确实调用了[into](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#into-android.widget.ImageView-)或者[submit](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#submit-int-int-)方法，仍然看不到日志信息。那最合理的解释是Glide不能确定您尝试加载的View或者目标的尺寸。

#### 自定义Target

如果您使用自定义Target，确保您实现了[getSize](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/target/Target.html#getSize-com.bumptech.glide.request.target.SizeReadyCallback-)方法并且调用非0的宽度跟高度的回调或者继承Target的子类，如：[ViewTarget](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/target/ViewTarget.html)，它会帮您实现getSize方法。

#### 视图

如果您只是加载资源到视图，那最合理的解释是您的视图不是通过布局也不会给予0的宽度或高度。如果视图的可见性被设置为**View.GONE**或者视图没有依附的父元素，那么将不会被显示。如果视图的父元素的宽度跟高度被设置成**wrap_content**或者**match_parent**中的某一个或组合，那么视图可能接收到无效的或者是0的宽度或高度。您可以尝试给您的视图固定的非0的尺寸或者使用Glide的[override(int, int) API](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestOptions.html#override-int-int-)给每个请求传递指定的尺寸。

### 请求监听和自定义日志

如果要以编程的方式跟踪错误日志和成功的负载，请跟踪应用程序中图像占用缓存的比率或者更好的控制本地日志。您可以使用[RequestListener](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/RequestListener.html)接口。**RequestListener**可以利用[RequestBuilder#listener()](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#listener-com.bumptech.glide.request.RequestListener-)添加到一个单独的负载中。例如：
```
Glide.with(fragment)
   .load(url)
   .listener(new RequestListener() {
       @Override
       boolean onLoadFailed(@Nullable GlideException e, Object model,
           Target<R> target, boolean isFirstResource) {
         // Log errors here.
       }

       @Override
       boolean onResourceReady(R resource, Object model, Target<R> target,
           DataSource dataSource, boolean isFirstResource) {
         // Log successes here or use DataSource to keep track of cache hits and misses.
       }
    })
    .into(imageView);
```

为了减少对象分配，您可以在多个负载中重用**RequestListener**对象。