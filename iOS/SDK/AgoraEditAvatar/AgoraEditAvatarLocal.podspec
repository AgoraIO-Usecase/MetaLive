
Pod::Spec.new do |s|
  s.name             = 'AgoraEditAvatar'
  s.version          = '0.0.1'
  s.summary          = 'AgoraEditAvatar'
  
  s.description      = <<-DESC
  TODO: Add long description of the pod here.
  DESC
  
  s.homepage         = 'https://github.com/zyp/AgoraEditAvatar'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'ZYP' => 'zhuyuping@agora.io' }
  s.source           = { :git => 'https://github.com/zyp/AgoraEditAvatar.git', :tag => s.version.to_s }
  
  s.ios.deployment_target = '11.0'
  s.source_files = 'Classes/**/*.{h,m}'
  s.dependency 'SDWebImage'
  s.public_header_files = ['Classes/**/*.h']
  s.resource_bundles = {
    'AgoraEditAvatar' => ['resource/*.xcassets']
  }
end
