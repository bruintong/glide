# 过渡效果

Glide中的过渡效果可以让您定义Glide应该怎样从占位符过渡到新加载的图像或者从缩略图过渡到全尺寸的图像。过渡效果以单个请求的上下文的方式生效，不是跨多个请求。因此，过渡效果不允许您定义从一个请求到另一个请求的动画（如：淡入淡出）。

## 默认行为

图像可以从Glide的四个地方加载：
1. Glide的内存缓存
2. Glide的磁盘缓存
3. 本地设备可用的资源文件或者URI
4. 远程可用的资源URL

如果从Glide的内存缓存中加载资源，则没有默认的过渡效果。然而，如果从Glide的磁盘缓存，本地资源文件，URI或者远程的资源URL或URI中加载数据，则Glide的默认过渡效果会生效。

想要改变默认的过渡效果的行为，您可以查看下面[自定义过渡效果](../docs/Transitions.md#自定义过渡效果)小节。

### 指定过渡效果

有关概述和代码示例，查看[选项文档](../docs/Options.md#过渡选项)。

过渡选项用于指定特定请求的过渡效果。过渡选项使用RequestBuilder中的transition()方法来为请求设置。特定类型的过渡效果可以使用BitmapTransitionOption或则DrawableTransitionOption指定。对于Bitmap和Drawable外的其他类型，可以使用GenericTransitionOption。

### 性能技巧

Android中的动画是昂贵的，特别是如果有大量的动画同时启动。淡入淡出和涉及透明度变化的动画特别昂贵。另外，动画通常比图像解码花费更多的时间。在列表和表格中无偿的使用动画使图像加载感觉缓慢。为了最大限度的提高性能，请考虑在使用Glide加载图像到ListView，GridView或者RecyclerView时，避免使用动画。特别是当您期望图像缓存或者在大部分时间尽可能快的加载时，请考虑预加载这样在用户滚动到它们时，图像刚好在内存中。

### 常见错误

#### 使用占位符淡入淡出和透明图像

Glide默认淡入淡出动画利用TransitionDrawable。TransitionDrawable提供两种动画模式，由setCrossFadeEnabled()控制。当淡入淡出禁用时，图像从已经显示图像的顶部淡入。当淡入淡出启用时，图像是从不透明过渡到透明的动画淡出，从透明到不透明的动画淡入。

在Glide中，我们默认禁用淡入淡出，因为它通常提供了更好看的动画。一个实际的淡入淡出，其中两个图像的透明度都经常变化，通常会在动画中间产生一个白色的闪光，其中两个图像都是部分不透明的。

不幸的是，禁用淡入淡出通常是更好的默认值。它也会导致一些问题，当图像加载的内容包含透明像素，当占位符比正在加载的图像大或者图像是部分透明的，禁用淡入淡出导致占位符在动画结束之后在图像后面可见。如果您占位符加载的透明的图像，您可以启动淡入淡出通过配置DrawableCrossFadeFactory的参数适配并且将结果传递给transition()。

#### 跨请求淡入淡出

过渡效果不可以在两个不同请求加载的图像之间淡入淡出。Glide默认会取消任何已存在的请求，当您开启一个新的负载在已存在的视图或目标（查看[目标文档](../docs/Targets.md)）。因此，如果您想要加载两个不同的图像并在他们之间淡入淡出，您不能直接在Glide这样做。策略就像等待第一个负载完成， 从视图中抓取Bitmap或Drawable，启动第二个负载，然后在Drawable或Bitmap和新的图像之间手动动画，但这样是不安全的，并且可能导致崩溃或图像损坏。

相反地，在两个不同的请求中加载的两个不同的图像之间最简单的淡入淡出方式是使用ViewSwitcher包含两个ImageView。将第一张图像加载到getNextView()的结果中，接着加载第二张图像到getNextView()的下一个结果中，当第二张图像加载完成时，使用RequestListener调用showNext()。为了更好的控制，您可以遵从开发者文档中的概述的策略。当使用ViewSwitcher时，只有当第二张图像加载完成之后才淡入淡出。

## 自定义过渡效果

自定义过渡效果：
1. 实现[TransitionFactory](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/request/transition/TransitionFactory.html)
2. 通过[DrawableTransitionOption#with](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/resource/drawable/DrawableTransitionOptions.html#with-com.bumptech.glide.request.transition.TransitionFactory-)应用自定义的TransitionFactory加载。

要更改过渡效果的默认行为，以便您可以控制从内存缓存，磁盘缓存或者从源代码加载图像时是否应用该效果，您应该检查在TransitionFactory传递给build()方法的数据源。

示例，请查看[DrawableCrossFadeFactory](https://github.com/bumptech/glide/blob/8f22bd9b82349bf748e335b4a31e70c9383fb15a/library/src/main/java/com/bumptech/glide/request/transition/DrawableCrossFadeFactory.java#L35)。