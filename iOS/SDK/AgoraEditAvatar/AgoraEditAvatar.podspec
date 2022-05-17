Pod::Spec.new do |s|
  s.name             = 'AgoraEditAvatar'
  s.version          = '0.0.1'
  s.summary          = 'AgoraEditAvatar'
  s.homepage         = 'https://agora.io'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'Agora AD' => 'zhuyuping@agora.io' }
  s.source           = { :git => 'xx.git', :tag => '0.0.0' }
  s.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64', 'DEFINES_MODULE' => 'YES' }
  s.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64', 'DEFINES_MODULE' => 'YES' }
  s.ios.deployment_target = '11.0'
#  s.source_files = '/**/*.{h,m}'
  s.source_files = 'code/AgoraEditAvatar.h'
  s.resource_bundles = {
    'AgoraEditAvatar' => ['*.xcassets', 'SDK/AgoraMeetingUI/AgoraMeetingUI/Assets/Xib/**/*']
  }
  s.swift_versions = "5.0"
end
