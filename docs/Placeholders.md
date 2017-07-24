# 占位符
## 类型
Glide允许用户指定三种不同的占位符以是适应不同的场景：
- placeholder
- error
- fallback
### Placeholder
占位符是在请求过程中显示的图片。当一个请求被成功执行，占位符将会被请求的资源替代。如果请求的资源在内容中，占位符可能永远不会显示。如果请求失败而错误图片没有设置，占位符会一直显示。同样的如果请求的URL/Model为空，错误图片跟回调图片都没有设置，占位符也会一直显示。 
使用Generated API：
```
GlideApp.with(fragment)
  .load(url)
  .placeholder(R.drawable.placeholder)
  .into(view);
```
或者：
```
GlideApp.with(fragment)
  .load(url)
  .placeholder(new ColorDrawable(Color.BLACK))
  .into(view);
```
### Error
当请求最终失败的时候会显示错误图片。当请求的URL/Model为空并且没有设置回调的时候也会显示错误图片。
使用Generated API：
```
GlideApp.with(fragment)
  .load(url)
  .error(R.drawable.error)
  .into(view);
```
或者：
```
GlideApp.with(fragment)
  .load(url)
  .error(new ColorDrawable(Color.RED))
  .into(view);
```
### Fallback
当一个请求的URL/Model为null时会显示回调图片。回调图片的主要目的是允许用户表明参数是否允许为null。比如，个人网站为null表明用户没有设置个人照片。然而，null也可以表明元数据是无效的或者无法获取。默认情况下，Glide认为URL/Model为null是一种错误，用户如果希望自己处理null，应该设置回调图片。
使用Generated API：
```
GlideApp.with(fragment)
  .load(url)
  .fallback(R.drawable.fallback)
  .into(view);
```
或者：
```
GlideApp.with(fragment)
  .load(url)
  .fallback(new ColorDrawable(Color.GREY))
  .into(view);
```
FAQ
1. 占位符是异步加载的吗？
答：不是。占位符的加载是在Android的主线程中处理的。我们希望占位符尽可能小并且容易被系统缓存。
2. Transformations可以作为占位符吗？
答：不行。Transformations只适用于请求资源，不能作为占位符。比如，你加载一个圆形的图片，你可能希望使用圆形的占位符资源。你可以考虑自定义View来截取占位符作为你的Transformations。
3. 在多个View中可以使用同一个占位符图片吗？
答：通常可以，任何非静态的图片（像BitmapDrawable）是可以的在多个View中显示的。然而有状态的图片在多个View中同时显示是不安全的，因为View的状态会被改变。对于有状态的图片，请使用资源id，或者使用**newDrawable()**传递给每个请求一个新的拷贝。