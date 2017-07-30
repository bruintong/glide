# 目标

Glide中的目标作为请求跟请求者之间的传递者。目标负责显示占位符，加载的资源以及为每个请求确定合适的尺寸。最常用目标是使用ImageView显示占位符，Drawable和Bitmap的ImageViewTarget。用户还可以实现自己的目标，或者对任何可用的基类进行子类化。

## 指定目标

[into(Target)](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#into-Y-)方法不仅用于启动每个请求，同时也可以指定将要接收请求结果的目标。Glide提供了一个辅助方法给[into(ImageView)](http://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/RequestBuilder.html#into-android.widget.ImageView-)，其采用ImageView并把它包装在目标于适合所请求的资源类型。

为了方便使用自定义目标，这些into()方法返回提供给他们的目标：
```
Target<Bitmap> target = Glide.with(fragment)
        .asBitmap()
        .load(url)
        .into(imageView);
...
// Some time later:
Glide.with(fragment).clear(target);
```

## 目标和自动取消

目标可以并且通常应该被重新用于相同的地方显示的每个后续请求。重新使用目标允许Glide在新加载启动时自动取消并且重新使用之前负载的资源。未能重新使用目标可能会导致之前请求中的资源替换较新的请求。

### 自定义目标

重新使用自定义目标的一种简单方法是简单地将其作为实例变量：
```
private class WidgetHolder {
  private final Fragment fragment;
  private final Target<Widget> widgetTarget;

  public WidgetHolder(Fragment fragment, Widget widget) {
    this.fragment = fragment;
    widgetTarget = new CustomWidgetTarget(widget);
  }

  public void showInWidget(Uri uri) {
    Glide.with(fragment)
        .load(uri)
        .into(widgetTarget);
  }
}
```

Glide能够使用getRequest()和setRequest()方法查找和取消对目标的请求。这意味着所有自定义的目标都必须实现这些方法。最简单的方法是进行子类化BaseTarget。

### ViewTargets

一些自定义的目标还可以提供更智能的getRequest()和setRequest()实现，避免严格要求重新使用目标。例如，ViewTarget使用Android Framework的getTag（）和setTag（）方法：
```
@Override
public Request getRequest() {
    return (Request) view.getTag();
}

@Override
public void setRequest(Request request) {
    view.setTag(request);
}
```

由于标签是View的属性，因此新加载的ViewTargets可以查找和取消/重新使用先前ViewTargets的请求。因此，当使用into(ImageView)或ViewTarget的子类加载到视图中时，您可以为每个加载传递新的目标：
```
@Override
public void onBindViewHolder(ViewHolder vh, int position) {
  int resourceId = resourceIds.get(position)
  Glide.with(fragment)
      .load(resourceId)
      .into(new CustomViewTarget(vh.imageView));
```

### 尺寸

默认情况下，Glide使用由Targets提供的getSize()大小作为请求的目标大小。这样做可以让Glide选择适当的url，downsample，crop和转换适当的图像，以尽量减少内存使用，并确保负载尽可能快。

最简单的实现方式是在getSize()方法中立即调用回调函数：
```
@Override
public void getSize(SizeReadyCallback cb) {
  cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
}
```

您还可以将尺寸传递给您的Target的构造函数，并将这些尺寸提供给回调：
```
public class CustomTarget<T> implements Target<T> {
  private final int width;
  private final int height;
 
  public CustomTarget(int width, int height) {
    this.width = width;
    this.height = height;
  }

  ...

  @Override
  public void getSize(SizeReadyCallback cb) {
    cb.onSizeReady(width, height);
  }
}
```

### 查看目标

ViewTarget实现了getSize()方法，通过检查View的属性或者使用onPreDrawListener在渲染之前立即测量视图来实现。我们采用下面的逻辑：
1. 如果任一视图的尺寸设置的值>0，则使用这些尺寸。
2. 如果任一视图的尺寸设置成WRAP_CONTENT，则使用屏幕的宽度或高度。
3. 如果至少有一个视图的尺寸的值<=0而不是WRAP_CONTENT，则添加一个OnPreDrawListener来监听布局。

### WRAP_CONTENT

请注意，Glide不能很好的处理WRAP_CONTENT，这是因为对于我们来说很难清楚用户的意图，特别是要求转换时。

我们可以看作WRAP_CONTENT是用户请求原始的未修改的图像，但是这样做在我们加载大图像时有内存溢出的风险。另外，特别是视图不会脱离屏幕，因此Android框架可能会最终缩小任何加载成功的全分辨率图像。

使用屏幕尺寸，我们至少可以对超大图像进行降低采样，而不会完全忽略用户的请求。

### 应用视图大小

一般来说，当在其加载的视图上设置显示dp大小时，Glide提供了最快的和最可预测的结果。然而，当不可能这样时，Glide还为布局权重，MATCH_PARENT和其他相对尺寸提供了强大的支持OnPreDrawListeners。最后，如果这些都没有设置，Glide应该为WRAP_CONTENT提供了合理的行为。

### 备选方案

如果在任何情况下，Glide似乎都会使View大小错误，您可以随时通过扩展ViewTarget和实现自己的逻辑来手动覆盖大小，或者通过使用RequestOptions中的override()方法。