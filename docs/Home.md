# 关于Glide

Glide是Android平台上的一个快速高效的图像加载库，专注于平滑滚动。Glide提供了一个简单易用的API，一个性能可扩展的资源解码管道流和自动资源池。

![glide logo](../images/glide_logo.png)

Glide支持获取，解码和显示视频、图片以及GIF动画。Glide包含一个灵活的API，允许开发人员插入几乎任何网络堆栈。默认情况下，Glide使用基于HttpUrlConnection的自定义堆栈，但是也包含实用程序库插入到Google的Volley项目或Square的OkHttp库替代。

Glide的主要重点是尽可能平滑和快速地滚动任何类型的图像列表，但是对于几乎任何需要获取，调整大小和显示远程图像的情况，Glide也是适用的。

## API

Glide使用简单流畅的API，允许用户在一行中提出大多数请求：
```
Glide.with(fragment)
    .load(url)
    .into(imageView);
````

## 性能

Glide考虑到Android的图像加载性能的两个关键方面：
- 图像解码的速度。
- 解码图像时发生的异常。

为了让用户在应用程序中拥有很好的体验，图像不仅要快速出现，而且还必须这样做，这样才不会导致大量的主线程I/O阻塞和卡顿或过多的垃圾回收。

Glide采取了许多步骤，以确保图像在Android上加载尽可能快速和平滑：
- 智能、自动地下采样和缓存，最大程度地减少了存储开销和解码时间。
- 积极重用资源（如字节数组和Bitmaps）可最大限度地降低昂贵的垃圾回收和堆碎片。
- 深度生命周期集成确保只对有效的Fragment和Activity优先请求，并且应用程序在必要时释放资源，以避免在后台时被杀死。

## 入门

首先查看[下载和安装](../docs/Download-Setup.md)页面，了解如何将Glide整合到您的应用程序中。然后，查看[入门指南](../docs/Getting-Started.md)页面了解基础知识。有关更多帮助和示例，请继续阅读文档部分的其余部分，或查看我们提供的[示例程序](http://bumptech.github.io/glide/ref/samples.html)。

## 要求

Glide v4要求Android版本4.0（API等级14）或更高版本。
