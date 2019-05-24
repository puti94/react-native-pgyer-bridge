require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "react-native-pgyer-bridge"
  s.version      = package['version']
  s.summary      = "RNPgyerBridge"
  s.homepage     = "https://github.com/puti94/react-native-pgyer-bridge#README.md"
  s.license      = "MIT"
  s.author             = { "author" => "1059592160@qq.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/puti94/react-native-pgyer-bridge.git", :tag => "master" }
  s.source_files  = "ios/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  s.dependency "Pgyer"
  s.dependency "PgyUpdate"

end

