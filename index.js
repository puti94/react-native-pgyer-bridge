import {NativeModules, Platform, DeviceEventEmitter} from 'react-native';

const {RNPgyerBridge} = NativeModules;

class Pgyer {
  
  
  appId;
  
  /**
   * 构建版本
   */
  version = RNPgyerBridge.version;
  /**
   * 构建号
   */
  build = RNPgyerBridge.build;
  
  /**
   * 初始化配置,在入口处调用
   * @param appId   应用在蒲公英上的appKey
   * @param type ios为触发反馈的模式,[0=摇一摇,1=三指拖动]  android为打开反馈的模式,[0=对话框,1=activity]
   * @param shakingThreshold 激活用户反馈界面的阈值，数字越小灵敏度越高，IOS和安卓不一致,需要自定义设置自己去看蒲公英文档。
   * @param [ios] themeColor  开发者可以通过设置用户反馈界面的颜色主题来改变界面风格，设置之后的颜色会影响到Title的背景颜色和录音按钮的边框颜色，默认为0x37C5A1(绿色)
   * @param [android] colorDialogTitle  设置Dialog 标题的字体颜色，默认为颜色为#ffffff
   * @param [android] colorTitleBg     设置Dialog 标题栏的背景色，默认为颜色为#2E2D2D
   * @param [android] barBackgroundColor  设置顶部按钮和底部背景色，默认颜色为 #2E2D2D
   * @param [android] barButtonPressedColor    设置顶部按钮和底部按钮按下时的反馈色 默认颜色为 #383737
   * @param [android] colorPickerBackgroundColor 设置颜色选择器的背景色,默认颜色为 #272828
   */
  initWithConfig({
                   appId,
                   shakingThreshold,
                   type,
                   themeColor,
                   colorDialogTitle,
                   colorTitleBg,
                   barBackgroundColor,
                   barButtonPressedColor,
                   colorPickerBackgroundColor,
                 } = {}) {
    this.appId = appId;
    RNPgyerBridge.initWithConfig({
      appId,
      shakingThreshold,
      type,
      themeColor,
      colorDialogTitle,
      colorTitleBg,
      barBackgroundColor,
      colorPickerBackgroundColor
    })
  }
  
  /**
   * 手动汇报异常
   * @param name  异常名字
   * @param reason  异常信息
   * @param userInfo  额外信息
   */
  reportException({name, reason, userInfo} = {}) {
    RNPgyerBridge.reportException({name, reason, userInfo})
  }
  
  /**
   * 检查更新,有更新则提示更新
   * @param forced   是否强制更新
   * @param userCanRetry
   * @param deleteHistoryApk
   * @param autoInstall
   * @param onProgress
   * @return {Promise<*> | * | Promise<any> | Promise<T> | Promise<void>}
   * @param simple  是否是默认模式
   */
  checkUpdate({forced = false, userCanRetry, deleteHistoryApk, autoInstall = true, onProgress} = {}) {
    if (Platform.OS === 'ios') {
      RNPgyerBridge.checkUpdate()
      return Promise.resolve();
    } else {
      let sub;
      if (onProgress) {
        sub = DeviceEventEmitter.addListener('apkDownloadProgress', onProgress);
      }
      return RNPgyerBridge.checkUpdate({
        forced,
        userCanRetry,
        deleteHistoryApk,
        autoInstall,
        onProgress: Boolean(onProgress)
      }).then(res=>{
        sub && sub.remove();
        return res;
      })
    }
  }
  
  /**
   * 获取更新信息
   * @returns {Promise<any | never>}
   */
  getUpdateInfo() {
    return fetch('https://www.pgyer.com/apiv2/app/check', {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: [
        '_api_key=305092bc73c180b55c26012a94809131',
        `version=${this.version}`,
        `appKey=${this.appId}`
      ].join('&')
    })
        .then(r => r.json())
        .then(res => {
          if (res.code === 0) return res.data
          return Promise.reject(res)
        })
  }
  
  /**
   * 手动触发反馈弹窗
   * @param params  触发是可以带参数,android有效
   */
  showFeedbackView(params = {}) {
    if (Platform.OS === 'ios') {
      RNPgyerBridge.showFeedbackView()
    } else {
      RNPgyerBridge.showFeedbackView(params)
    }
  }
  
  /**
   * 下载apk android 平台
   * @param url
   */
  downLoadApk(url) {
    RNPgyerBridge.downLoadApk(url)
  }
  
  /**
   * 安装apk 安卓平台
   * @param uri
   */
  installApk(uri) {
    RNPgyerBridge.installApk(uri)
  }
  
}

export default new Pgyer()
