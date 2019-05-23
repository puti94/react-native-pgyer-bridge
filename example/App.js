/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Button, View} from 'react-native';
import Pyger from 'react-native-pgyer-bridge'

export default class App extends Component<Props> {
  
  componentDidMount() {
    Pyger.initWithConfig({
      // themeColor: '#ffff00',
      colorPickerBackgroundColor: '#ff0000',
      colorTitleBg: '#ff0000',
      appId: Platform.select({
        ios: '65076a0976fb632cccc765938012bc97',
        android: '5ebdf640f574c55dd48e65cc52a14b18'
      })
    })
  }
  
  render() {
    return (
        <View style={styles.container}>
          <Button title={'检查更新'} onPress={() => {
            Pyger.checkUpdate();
          }}/>
          <Button title={'获取更新信息'} onPress={() => {
            Pyger.getUpdateInfo().then(res => {
              console.log('结果', res);
              // ios端可以直接打开安装
              // Linking.openURL(res.downloadURL)
            })
          }}/>
          {Platform.OS === 'android' && <Button title={'安卓检查更新配置'} onPress={() => {
            Pyger.checkUpdate({
              forced: false,
              autoInstall: false,
              onProgress: progress => {
                console.log('进度', progress)
              }
            })
                .then(res => {
                  Pyger.installApk(res.path)
                }).catch(e => {
              console.warn('异常', e)
            })
          }}/>}
          <Button title={'代码打开反馈'} onPress={() => {
            Pyger.showFeedbackView({"自定义参数": "我是自定义参数"})
          }}/>
          <Button title={'抛异常'} onPress={() => {
            throw new Error('I AM CUSTOM EXCEPTION')
          }}/>
          <Button title={'抛自定义异常'} onPress={() => {
            Pyger.reportException({
              name: 'MY IS CUSTOM EXCEPTION',
              reason: "CUSTOM",
              userInfo: {
                aa: "21321",
                bb: "sadsad",
                cc: {aa: 'SAD21321321'}
              }
            })
          }}/>
        </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
