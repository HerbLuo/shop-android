# shop-android
shop-native 的安卓端

### Run It
1. 修改cn.cloudself.weexshop.util.app.Config下的服务端地址ip
2. 确保服务端开着，js端开着，运行即可

### 具体功能
这些具体功能均非必需
1. Activity  
* Welcome Activity 欢迎页
> 启动一些线程，更新线程，超时线程，主要用于JS文件的更新，预渲染  

> 预渲染机制  
在WelcomeActivity中实现IWXRenderListener并等待其回调
WXShpLifecycle(Module)#loadSuccess方法,等待JS的调用
两个调用缺一不可，皆完成后，跳转至下一个Activity，
下一个Activity中调用mInstance.init(this)重新绑定context

2. Module
* 地图Moudle
> 调用LocationSelectorActivity，选择收货地址的界面，采用高德地图实现，原生Java实现
* 二维码识别Module
> 调用二维码识别界面
* WXShpLifecycle
> 包含日志接口，加载成功通知，onStop回调等方法
* WXBroadcastChannel
> 当前SDK(v11)好像对BroadcastChannel支持不佳，用这个弥补