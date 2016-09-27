# OkHttpUtils
## OkHttp 网路加载方法工具类精简
根据鸿洋大神的库精简一个工具类，当然更好的更全的请参考鸿洋大神的 [https://github.com/hongyangAndroid/okhttp-utils
](https://github.com/hongyangAndroid/okhttp-utils
)
> - 一般的get请求
> - 一般的post请求
> - 基于Http的文件上传
> - 文件下载
> - 图片下载
> - 支持请求回调，直接返回对象、对象集合

### 注意： 使用前需要：
#### okhttp 引入  使用前，
*官方说明：[http://square.github.io/okhttp](http://square.github.io/okhttp/)*

- Android Studio的用户，可以选择添加:
>- Gradle:
``` groovy
compile 'com.squareup.okhttp3:okhttp:3.4.1'
```

- Eclipse的用户，可以下载最新的jar [↓ Latest JAR](https://search.maven.org/remote_content?g=com.squareup.okhttp3&a=okhttp&v=LATEST "LATEST") 添加依赖。
- 注意:okhttp内部依赖okio。
>- Gradle:
``` groovy
compile 'com.squareup.okio:okio:1.10.0'
```

>- 最新的jar地址：[↓ Okio  Latest JAR](https://search.maven.org/remote_content?g=com.squareup.okio&a=okio&v=LATEST)
