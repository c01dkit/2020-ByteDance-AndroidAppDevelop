# README

2020.7.6\~2020.7.17的大二暑期小学期\~终于要学java和安卓开发了！XD

### 环境配置说明

* demo项目sync报错unable to find valid certification path to requested target：修改项目级gradle里指定的IDE版本com.android.tools.build:gradle:x.x.x成自己的，然后把阿里云的链接换成google()和jcenter()，如果sync还是报错的话按提示修改项目级别gradle\wrapper下gradle-wrapper.properties里指定的gradle版本（在线状态，需科学上网）
* [如何更改Android Studio默认的C盘保存文件的设定](https://blog.csdn.net/Jeff_YaoJie/article/details/80499278?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-5.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-5.nonecase)

## Chapter1

### 作业要求

* 进行Android Studio、git的安装与环境配置
* 利用不少于5个组件编写一个DEMO，并将交互结果输出到log

### 作业成果

<img src="README.assets/image-20200706182351225.png" alt="image-20200706182351225" style="zoom:50%;" />

### 总结

其中最麻烦的是配置Gradle，我的电脑连打开demo都会failed。

采用在线gradle，主路径按.gradle下属init.d的初始化文件，修改目标路径为指定路径

并 不指定具体地址

最后实现了gradle没有报错（真机调试）

[关于修改默认路径（从C盘改到其他盘）的方式](https://blog.csdn.net/Jeff_YaoJie/article/details/80499278?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-5.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-5.nonecase)

[初次coding推荐阅读官方文档](https://developer.android.google.cn/training/basics/firstapp/running-app)

## Chapter2

### 课程简要提纲

* 三种布局：线性布局、相对布局、帧布局（用于图片重叠）
* 图片imageview的scaletype属性可以设置图片的填充方式
* 使用toast创建弹出提示
* recycleview：只对屏幕显示的内容进行加载，并实现内存复用，可以优化性能，避免崩溃
* intent可以用于不同activity之间的信息传递与唤起，也可以拿到返回值，有显式和隐式两种
* 创建Acitivity要new一个activity，不要手动创建class然后手动添加xml、更新manifest

### 课程作业

* 实现一个RecyclerView，要求点击条目可以跳转至新的activity，并传递这是第几个item的信息
* 统计⻚⾯所有view的个数。ViewGroup中的API：getChildCount() ：获取该group下的孩子数。getChildAt()：获取第i个孩子

### 作业成果

<img src="README.assets/image-20200707223926275.png" alt="image-20200707223926275" style="zoom:50%;" />

### DEBUG

* 使用intent启动activity的时候注意manifest有没有说明这个activity，否则闪退
* setText（）接受字符串时是更改内容，如果传入数字会throw new RuntimeException("Stub!")；所以需要Integer.toString转字符串

## Chapter3

### 课程提纲

* fragment：解决了activity需要因为手机和平板不同而重写的问题。将activity视作fragment的容器
* fragment增强了代码重用性，与activity极其相似，并具有生命周期
* 一般使用属性动画，因为补间动画只是视觉效果。很少使用帧动画。
* 一类有趣的动画：[lottie](https://airbnb.io/lottie/#/android?id=from-resraw-lottie_rawres-or-assets-lottie_filename)
* [属性动画的资料说明](https://www.jianshu.com/p/2412d00a0ce4)

### 课程作业

* 完成使用进度条控制lottie动画的TODO
* 使用代码方式制作属性动画，实现大小（scaleX、scaleY）的缩放、透明度（alpha）的变化
* 拓展作业：利用viewpager实现多fragement的滑动切换，利用tablayout添加tab切换功能

### DEBUG

* 使用getSupportFragmentManager().beginTransaction().add(id,new fragment()).commit();的时候，id获取到的container不能和new fragment使用的container相同，否则会闪退
* 属性动画的ofInt（如backgroudcolor）、ofFloat等需要额外学习，其中第二个参数是字符串，没有自动补全，但是任意输入则报错。修改属性时可以点进xml搜索attributes查找可用的属性名称
* 关于viewpager2与tablayout[可以参考](https://blog.csdn.net/Utzw0p0985/article/details/103676010)，以及[这个](https://juejin.im/post/5df4aabe6fb9a0161104c8eb) ,其中TabLayoutMediator无法使用的原因是自动添加的gradle采用的是materials1.0.0版本，手动改成1.1.0版本即可使用。

## Chapter4

### 课程提纲

* 线程被start后通过实现的run方法进行执行，执行完毕后被销毁
* 线程池：执行提交的任务，解耦任务的提交与执行，封装线程使用与调度的细节。

* synctask：安卓sdk提供
* intentservice也可以启动线程

![image-20200709105408638](README.assets/image-20200709105408638.png)

* ViewGroup的绘制过程如下，其中onMeasure、onLayout、onDraw是需要重写的关键函数

![image-20200709133616570](README.assets/image-20200709133616570.png) 

### 课程作业

* 根据数学关系，计算秒针、分针、时针的角度关系，绘制指针
* 在给定的表盘中绘制数字
* 添加秒针的跳动效果

### 作业成果

<img src="README.assets/image-20200709162642399.png" alt="image-20200709162642399" style="zoom:50%;" />

### 总结

* Paint的setAntiAlias(true);用于平滑画笔边缘，抗锯齿；setStrokeWidth用于设定画笔粗细；setStrokeCap用于设定每个画笔落点形状；setStyle用于设定填充方式（外描边、填充、外描边+填充）；setTextAlign设定文本对齐方式；setTextSize设定字体大小（65f比较美观）

![image-20200709161927466](README.assets/image-20200709161927466.png)

文字绘制时，采用Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics(); 各值与设定的size有关。获取metrics。baseline在metrics名为leading，其值为0，是基准线。其他四种线在metrics都有定义，是相对于基线的Y轴偏移量（下为正方向）。从图中表示的话，top = 紫线Y坐标减红线Y坐标，Bottom = 黄线Y坐标减去红线Y坐标，以此类推。如果想把文字中心对齐，需要使用预先计算出textY，然后textY + (bottom - top) / 2 - bottom即可。然后drawText四个参数分别为绘制的文本、起始X坐标、基线坐标、画笔。

*  getWidth(), getHeight()用于获取view的宽高
* 两个重要函数：invalidate(如果布局没变化，只触发draw），requestLayout(触发layout、measure)
* handler的三种使用方法：新建handler并实现handleMessage方法，可以调度thread；直接new一个runnable实现直接调度runnable；通过handler取消已经发送的massage或runnable
* runnable实际上会被打包成message。主线程不能执行耗时较长的任务，否则ANR，应用无响应。