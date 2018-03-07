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

1. 画出圆环  
2. 圆环到实心圆  
3. 缩放动画  

