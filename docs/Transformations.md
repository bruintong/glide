# 转换

Glide中转换接收资源并将其转换，返回转换后的资源。通常，转换用于裁剪或者对Bitmap应用过滤，但也可以用来转换Gif动画，甚至自定义资源类型。

## 内置类型

Glide中包含了一些内置的转换，包括：
- CenterCrop
- FitCenter
- CircieCrop

## 应用转换

使用RequestOption类应用转换：

### 默认转换

```
RequestOptions options = new RequestOptions();
options.centerCrop();

Glide.with(fragment)
    .load(url)
    .apply(options)
    .into(imageView);
```

大多数内置的转换还可以静态导入，以便使用灵活的API。例如，您可以使用静态方法应用FitCenter转换：
```
import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;

Glide.with(fragment)
    .load(url)
    .apply(fitCenterTransform())
    .into(imageView);
```

如果您使用生成的API，转换方法是链式的，所以更加容易：
```
GlideApp.with(fragment)
  .load(url)
  .fitCenter()
  .into(imageView);
```

有关使用RequestOption的信息，请查阅[选项](../docs/Options.md)页面。

### 多重转换

默认情况下，对每一个后续调用transform()或者任何特定对转换方法（fitCenter()，centerCrop()，bitmapTransform()等等）将替换之前的转换。

为了将多重转换应用到单一到负载，请使用MultiTransformation类。

使用生成的API：
```
GlideApp.with(fragment)
  .load(url)
  .transform(new MultiTransformation(new FitCenter(), new YourCustomTransformation())
  .into(imageView);
```

将转换传递给MultiTransformation的构造函数的次序决定了应用转换的次序。

## Glide中特殊行为

### 重新使用转换

转换是无状态的。因此，多重负载中重用转换实例应该总是安全的。通常情况下，一次创建转换，然后将其传递给多重加载是好的做法。

### ImageView的自动转换

在Glide中当您开启加载到ImageView时，Glide可能自动应用FitCenter或CenterCrop，具体取决于视图的缩放类型。如果缩放类型是CENTER_CROP，Glide会自动应用CenterCrop转换。如果缩放类型是FIT_CENTER或者CENTER_INSIDE，则Glide会自动应用FitCenter 转换。

您可以总是通过应用具有转换设置的请求选项覆写默认的转换。另外，您可以使用dontTransform()确保没有自动的转换。

### 自定义资源

因为Glide v4.0允许您指定要解码的资源的超类型。您可能不知道要应用哪种转换类型。例如，当您使用asDrawable()（或者只是with()，因为asDrawable()是默认值）请求Drawable资源时，可能获取到BitmapDrawable的子类或者GifDrawable的子类。

为了确保您添加的任何转换都能被请求选项应用，Glide会将您提供的资源类到转换的映射添加到transform()方法。资源解码后，Glide使用映射来检索相应资源的转换。

Glide可以应用Bitmap转换成BitmapDrawable，GifDrawable，以及Bitmap资源，所以通常您只需要编写以及应用Bitmap转换。但是，如果您添加额外的资源类型，您可能需要考虑子类化RequestOption并且总是给您的自定义资源应用转换，除了内置的Bitmap转换。