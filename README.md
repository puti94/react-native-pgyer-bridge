
# react-native-pgyer-bridge

## Getting started

`$ npm install react-native-pgyer-bridge --save`

### Mostly automatic installation

`$ react-native link react-native-pgyer-bridge`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-pgyer-bridge` and add `RNPgyerBridge.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNPgyerBridge.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNPgyerBridgePackage;` to the imports at the top of the file
  - Add `new RNPgyerBridgePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-pgyer-bridge'
  	project(':react-native-pgyer-bridge').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-pgyer-bridge/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-pgyer-bridge')
  	```

## Usage
```javascript
import RNPgyerBridge from 'react-native-pgyer-bridge';

// TODO: What to do with the module?
RNPgyerBridge;
```
