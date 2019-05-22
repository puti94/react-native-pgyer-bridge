
import { NativeModules } from 'react-native';

const { RNPgyerBridge } = NativeModules;

export function reportException(map) {
  RNPgyerBridge.reportException(map)
}

export function initWidthAppId(appId) {
  RNPgyerBridge.initWidthAppId(appId)
}

export default RNPgyerBridge;
