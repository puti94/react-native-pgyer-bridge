
Pod::Spec.new do |s|
  s.name         = "RNPgyerBridge"
  s.version      = "1.0.0"
  s.summary      = "RNPgyerBridge"
  s.description  = <<-DESC
                  RNPgyerBridge
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "1059592160@qq.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/puti94/react-native-pgyer-bridge.git", :tag => "master" }
  s.source_files  = "RNPgyerBridge/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  s.dependency "Pgyer"
  s.dependency "PgyUpdate"

end

