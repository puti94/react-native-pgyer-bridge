/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Button} from 'react-native';
import * as Pyger from 'react-native-pyger-bridge'

export default class App extends Component<Props> {
  
  componentDidMount() {
    if (Platform.OS === "ios") {
      Pyger.initWidthAppId('e1f86c0a7738e96746df22694975dfa4')
    }
  }
  
  render() {
    return (
        <View style={styles.container}>
          <Button title={'手动汇报错误'} onPress={() => {
            Pyger.reportException({
              name: '我是错误',
              reason: '我是1111',
              userInfo: {
                aa: '就很骚的'
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
  }
});
