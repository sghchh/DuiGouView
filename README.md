# DuiGouView  
![Image text](https://user-gold-cdn.xitu.io/2017/10/22/3ff77d9f8de53fbacc0770fbd26fe524?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)   
很早之前就学习过这个自定义View，但是一直没有动手写过，今天趁刚刚学习过“扔物线”大佬的自定义View教程赶紧来实现一下这个自定义View   
## Android Studio中如何使用   

1. 在Project的build.gradle中添加入中心库  

```  
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```  

2. 在moudle（一般是app）的build.gradle中添加classpath  

```  
dependencies {
		compile 'com.github.User:Repo:Tag'
	}
```   

## 实现流程  
每一次的自定义View的时候，效果的分析，分析每一部是如何实现的才是自定义View最值得借鉴和学习的地方。  

### 1. 动画的拆分  

我们根据开头的效果展示，将这个自定义View的动画实现拆分成了三个部分：  

1. 画出圆环  
2. 圆环到实心圆  
3. 缩放动画  

### 2. 动画的实现  
最后的缩放动画很简单，可以用View动画直接来实现；难就难在头两个效果怎么实现。  
#### 2.1 画圆环 
这个动画很明显，只能够用属性动画来实现。既然决定了要用属性动画来实现，要满足两个条件：  

1. 更改的属性值要有setter和getter方法   
2. 这个属性值必须得有作用，也就是说必须在onDraw方法中参与到了控件的绘制中去，否则即使改变了，也没有直观的改变，我们体会不到它改变了。  

第一条很简单，第二条就需要思考了：  
我们想一想这个动画改变的是什么，找到了这个东西，就找到了我们要定义的那个属性。很明显，画圆环这个动画改变的是**圆弧的度数**，所以我们就知道了我们要在自定义View的内部定义一个**表示圆弧度数（本工程用的是degree）**，也知道了这个画圆弧的绘制用的是**canvas.drawArc()**这个方法。

#### 2.2 圆环到圆心  
这个动画也是很头疼，也是只想到用属性动画可以解决，但是怎么用这个属性动画呢？该定义个什么属性呢？圆环变成实心圆，如果分开来看，圆环到实心圆是不存在一个属性通过改变这个属性可以完成圆环到实心圆的转换的，所以我们**不能把这个动画看成是一个圆环到实心圆的动画，可以看成是一个实心白圆缩小到半径为零的动画！！！**哈哈，  
我这里就是这么干的，先绘制一层有颜色的圆，再绘制一个半径相同的实心白圆**覆盖住有颜色的圆**，这样那个看似复杂的动画就变成了一个实心圆缩小的动画。这样一来，我们  
需要定义的属性一下子就明白了--**就是那个实心白圆的半径**，需要的方法是**canvas.drawCircle()**.   

### 3. 实现过程中遇到的问题  
#### 3.1 画圆环  

调用**canvas.drawArc(getLeft(),getTop(),getRight(),getBottom(),0,degree,false,ringPaint)**搭配属性动画没有实现画圆环的效果。  
原因分析：  
对drawArc的前四个参数理解错误：前四个参数的意思是一个矩形区域：  
![image text](http://dl.iteye.com/upload/attachment/356328/c8365f29-5964-35d4-9a0b-e3f360218417.jpg)    

分别对应了ABCD的坐标。  
所以正确的调用方式是这样的：canvas.drawArc(4,4,getMeasuredWidth()-4,getMeasuredHeight()-4,0,degree,false,ringPaint)  

#### 3.2 两种动画先后执行  
前两个动画都是属性动画，我们可以通过**AnimatorSet**来规划他们的执行顺序，然而第三个View动画必须得是执行到他们的的后面，这时候该怎么办呢？  
可以为**AnimatorSet添加动画监听**：  

```
//增加动画监听，在属性动画进行完后进行View动画
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                addScaleAni();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
```
  
## 总结  
这个自定义View很简单了，重要的还是在**分析动画实现的时候的思想**

