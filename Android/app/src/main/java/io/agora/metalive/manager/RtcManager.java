package io.agora.metalive.manager;

import static io.agora.rtc2.video.VideoCanvas.RENDER_MODE_HIDDEN;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_60;
import static io.agora.rtc2.video.VideoEncoderConfiguration.MIRROR_MODE_TYPE;
import static io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_120x120;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_1280x720;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_160x120;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_180x180;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_240x180;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_240x240;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_320x180;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_320x240;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_360x360;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_424x240;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_480x360;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_480x480;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_640x360;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_640x480;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_840x480;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_960x720;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.agora.base.VideoFrame;
import io.agora.metalive.R;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.DataStreamConfig;
import io.agora.rtc2.IAvatarEngine;
import io.agora.rtc2.IAvatarEngineEventHandler;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.AvatarConfigs;
import io.agora.rtc2.video.AvatarContext;
import io.agora.rtc2.video.AvatarOptionValue;
import io.agora.rtc2.video.CameraCapturerConfiguration;
import io.agora.rtc2.video.IVideoFrameObserver;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class RtcManager implements IAvatarEngineEventHandler {
    private static final String TAG = "RtcManager";

    private static final int LOCAL_RTC_UID = 0;
    private static final int DEFAULT_BITRATE = 700;

    public static final List<VideoEncoderConfiguration.VideoDimensions> sVideoDimensions = Arrays.asList(
            VD_120x120,
            VD_160x120,
            VD_180x180,
            VD_240x180,
            VD_320x180,
            VD_240x240,
            VD_320x240,
            VD_424x240,
            VD_360x360,
            VD_480x360,
            VD_640x360,
            VD_480x480,
            VD_640x480,
            VD_840x480,
            VD_960x720,
            VD_1280x720
    );
    public static final List<FRAME_RATE> sFrameRates = Arrays.asList(
            FRAME_RATE_FPS_15,
            FRAME_RATE_FPS_30,
            FRAME_RATE_FPS_60
    );

    public static final List<AvatarRenderQuality> sRenderQuality = Arrays.asList(
            AvatarRenderQuality.Low,
            AvatarRenderQuality.Medium,
            AvatarRenderQuality.High,
            AvatarRenderQuality.Ultra
    );


    private static final CameraCapturerConfiguration.CAMERA_DIRECTION cameraDirection =
            CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_FRONT;

    public static final VideoEncoderConfiguration encoderConfiguration =
            new VideoEncoderConfiguration(
                    VD_1280x720,
                    FRAME_RATE_FPS_60,
                    DEFAULT_BITRATE,
                    ORIENTATION_MODE_FIXED_PORTRAIT);
    public static AvatarRenderQuality currRenderQuality = AvatarRenderQuality.High;

    public enum AvatarRenderQuality {
        Low, Medium, High, Ultra;

        public String getStringId() {
            String id = "";
            switch (this) {
                case Low:
                    id = "0";
                    break;
                case Medium:
                    id = "1";
                    break;
                case High:
                    id = "2";
                    break;
                case Ultra:
                    id = "3";
                    break;
            }
            return id;
        }
    }

    private volatile boolean isInitialized = false;
    private RtcEngineEx engine;
    private final Map<Integer, Runnable> firstVideoFramePendingRuns = new HashMap<>();

    private final SparseArray<OnStreamMessageListener> dataStreamListener = new SparseArray<>();

    private static volatile RtcManager INSTANCE;
    private IAvatarEngine avatarEngine;

    public Map<String, RtcConnection> connectionMap = new HashMap<>();
    public Map<String, ChannelMediaOptions> mediaOptionsHashMap = new HashMap<>();
    public Map<Integer, String> avatarItemEnableMap = new HashMap<>();

    private boolean isPublishAvatarTrack = true;
    private OnMediaOptionUpdateListener onMediaOptionUpdateListener = null;


    private OnVideoFrameRenderListener onVideoFrameRenderListener;
    private IVideoFrameObserver mVideoFrameObserver = new IVideoFrameObserver() {
        @Override
        public boolean onCaptureVideoFrame(VideoFrame videoFrame) {
            if (onVideoFrameRenderListener != null) {
                onVideoFrameRenderListener.onVideoFrameRender(videoFrame);
            }
            return true;
        }

        @Override
        public boolean onScreenCaptureVideoFrame(VideoFrame videoFrame) {
            return true;
        }

        @Override
        public boolean onMediaPlayerVideoFrame(VideoFrame videoFrame, int i) {
            return true;
        }

        @Override
        public boolean onRenderVideoFrame(String s, int i, VideoFrame videoFrame) {

            return true;
        }

        @Override
        public int getVideoFrameProcessMode() {
            return IVideoFrameObserver.PROCESS_MODE_READ_ONLY;
        }

        @Override
        public int getVideoFormatPreference() {
            return 11;
        }

        @Override
        public boolean getRotationApplied() {
            return false;
        }

        @Override
        public boolean getMirrorApplied() {
            return false;
        }
    };

    private final List<BaseAvatarEventHandler> avatarEventHandlers = new LinkedList<>();

    public synchronized void registerAvatarEventHandler(BaseAvatarEventHandler handler) {
        if (!avatarEventHandlers.contains(handler)) {
            avatarEventHandlers.add(handler);
        }
    }

    public synchronized void removeAvatarEventHandler(BaseAvatarEventHandler handler) {
        avatarEventHandlers.remove(handler);
    }

    private interface OnAvatarHandlerIterateListener {
        void onHandlerIterate(BaseAvatarEventHandler handler);
    }

    private RtcManager() {

    }

    public static RtcManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RtcManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RtcManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context, String appId, @Nullable OnInitializeListener listener) {
        if (isInitialized) {
            return;
        }

        try {
            // 0. create engine
            long startTime = System.currentTimeMillis();
            engine = (RtcEngineEx) RtcEngineEx.create(context.getApplicationContext(), appId, new IRtcEngineEventHandler() {
                @Override
                public void onWarning(int warn) {
                    super.onWarning(warn);
                    Log.w(TAG, String.format("onWarning code %d message %s", warn, RtcEngine.getErrorDescription(warn)));
                }

                @Override
                public void onError(int err) {
                    super.onError(err);
                    Log.e(TAG, String.format("onError code %d", err));
                    if (err == ErrorCode.ERR_OK) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    } else {
                        if (listener != null) {
                            listener.onError(err, err == ErrorCode.ERR_INVALID_TOKEN ? "invalid token" : "");
                        }
                    }
                }

                @Override
                public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
                    super.onFirstLocalVideoFrame(width, height, elapsed);

                    Log.d(TAG, "onFirstLocalVideoFrame");
                    Runnable runnable = firstVideoFramePendingRuns.get(LOCAL_RTC_UID);
                    if (runnable != null) {
                        runnable.run();
                        firstVideoFramePendingRuns.remove(LOCAL_RTC_UID);
                    }
                }


                @Override
                public void onRtcStats(RtcStats stats) {
                    super.onRtcStats(stats);
                }

                @Override
                public void onLastmileProbeResult(LastmileProbeResult result) {
                    super.onLastmileProbeResult(result);

                }

                @Override
                public void onRemoteVideoStats(RemoteVideoStats stats) {
                    super.onRemoteVideoStats(stats);
                }

                @Override
                public void onStreamMessage(int uid, int streamId, byte[] data) {
                    super.onStreamMessage(uid, streamId, data);
                    Log.d(TAG, "onStreamMessage uid=" + uid + ",streamId=" + streamId + ",data=" + new String(data));
                    OnStreamMessageListener listener = dataStreamListener.get(streamId);
                    if (listener != null) {
                        listener.onMessageReceived(streamId, uid, new String(data));
                    }
                }

                @Override
                public void onStreamMessageError(int uid, int streamId, int error, int missed, int cached) {
                    super.onStreamMessageError(uid, streamId, error, missed, cached);
                    Log.d(TAG, "onStreamMessageError uid=" + uid + ",streamId=" + streamId + ",error=" + error + ",missed=" + missed + ",cached=" + cached);
                }

                @Override
                public void onUserJoined(int uid, int elapsed) {
                    super.onUserJoined(uid, elapsed);
                }
            });

            engine.registerVideoFrameObserver(mVideoFrameObserver);
            engine.setLogLevel(Constants.LogLevel.getValue(Constants.LogLevel.LOG_LEVEL_ERROR));
            avatarEngine = engine.queryAvatarEngine();

            AvatarContext avatarContext = new AvatarContext(
                    context.getString(R.string.ai_app_id),
                    context.getString(R.string.ai_token_id));
            avatarEngine.initialize(avatarContext);
            avatarEngine.registerEventHandler(this);
            setLocalAvatarQuality(currRenderQuality);

            AvatarConfigs avatarConfigs = new AvatarConfigs(
                    Constants.MediaSourceType.PRIMARY_CAMERA_SOURCE,
                    context, true, true,
                    Constants.AvatarProcessingMode.AVATAR_PROCESSING_MODE_AVATAR);

            avatarEngine.enableOrUpdateLocalAvatarVideo(true, avatarConfigs);

            engine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            engine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            engine.setLogLevel(Constants.LogLevel.getValue(Constants.LogLevel.LOG_LEVEL_ERROR));

            engine.setAudioProfile(Constants.AUDIO_PROFILE_SPEECH_STANDARD, Constants.AUDIO_SCENARIO_GAME_STREAMING);
            engine.setDefaultAudioRoutetoSpeakerphone(true);
            engine.enableDualStreamMode(false);

            engine.enableVideo();
            engine.enableAudio();

            engine.setCameraCapturerConfiguration(
                    new CameraCapturerConfiguration(cameraDirection,
                            new CameraCapturerConfiguration.CaptureFormat(
                                    encoderConfiguration.dimensions.width,
                                    encoderConfiguration.dimensions.height,
                                    encoderConfiguration.frameRate)));

            if (cameraDirection == CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_FRONT) {
                encoderConfiguration.mirrorMode = MIRROR_MODE_TYPE.MIRROR_MODE_ENABLED;
            } else {
                encoderConfiguration.mirrorMode = MIRROR_MODE_TYPE.MIRROR_MODE_DISABLED;
            }

            isInitialized = true;
            Log.d(TAG, "RTCManager initialize cost time ms=" + (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(-1, "RtcEngine create exception : " + e.toString());
            }
        }
    }

    public void setCameraCaptureResolution(VideoEncoderConfiguration.VideoDimensions dimension){
        encoderConfiguration.dimensions = dimension;
        engine.setCameraCapturerConfiguration(
                new CameraCapturerConfiguration(cameraDirection,
                        new CameraCapturerConfiguration.CaptureFormat(
                                encoderConfiguration.dimensions.width,
                                encoderConfiguration.dimensions.height,
                                encoderConfiguration.frameRate)));
    }

    public void setEncoderVideoFrameRate(FRAME_RATE frameRate){
        encoderConfiguration.frameRate = frameRate.getValue();
        engine.setVideoEncoderConfiguration(encoderConfiguration);
    }

    public void setOnVideoFrameRenderListener(OnVideoFrameRenderListener onVideoFrameRenderListener) {
        this.onVideoFrameRenderListener = onVideoFrameRenderListener;
    }

    public int createDataStream(String channelId, OnStreamMessageListener listener) {
        if (engine == null) {
            return 0;
        }

        RtcConnection rtcConnection = connectionMap.get(channelId);
        if (rtcConnection == null) {
            return 0;
        }
        int dataStream = engine.createDataStreamEx(new DataStreamConfig(), rtcConnection);
        dataStreamListener.put(dataStream, listener);
        return dataStream;
    }

    public void sendDataStreamMsg(String channelId, int streamId, String msg) {
        if (engine == null) {
            return;
        }
        RtcConnection rtcConnection = connectionMap.get(channelId);
        if (rtcConnection == null) {
            return;
        }
        engine.sendStreamMessageEx(streamId, msg.getBytes(StandardCharsets.UTF_8), rtcConnection);
    }

    public void renderLocalAvatarVideo(FrameLayout container) {
        if (engine == null) {
            return;
        }
        container.removeAllViews();
        long startTime = System.currentTimeMillis();
        TextureView avatarSurfaceView = new TextureView(container.getContext());
        avatarSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(avatarSurfaceView);
        engine.startPreview();
        VideoCanvas videoCanvas = new VideoCanvas(avatarSurfaceView, RENDER_MODE_HIDDEN);
        videoCanvas.mirrorMode = MIRROR_MODE_TYPE.MIRROR_MODE_DISABLED.getValue();
        avatarEngine.setupLocalVideoCanvas(videoCanvas);
        Log.d(TAG, "RTCManager renderLocalAvatarVideo cost time ms=" + (System.currentTimeMillis() - startTime));
    }

    public void stopRenderLocalAvatarVideo(FrameLayout container) {
        if (engine == null) {
            return;
        }

        container.removeAllViews();
        VideoCanvas canvas = new VideoCanvas(null, RENDER_MODE_HIDDEN);
        avatarEngine.setupLocalVideoCanvas(canvas);
    }

    public void renderLocalCameraVideo(FrameLayout container) {
        if (engine == null) {
            return;
        }
        TextureView avatarSurfaceView = new TextureView(container.getContext());

        container.addView(avatarSurfaceView);
        engine.startPreview();
        VideoCanvas videoCanvas = new VideoCanvas(avatarSurfaceView, RENDER_MODE_HIDDEN);
        videoCanvas.mirrorMode = MIRROR_MODE_TYPE.MIRROR_MODE_ENABLED.getValue();
        engine.setupLocalVideo(videoCanvas);
    }

    public void joinChannel(String channelId, String uid, String token, boolean publish, OnChannelListener listener) {
        if (engine == null) {
            return;
        }

        int _uid = LOCAL_RTC_UID;
        if (!TextUtils.isEmpty(uid)) {
            try {
                _uid = Integer.parseInt(uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.publishCameraTrack = publish && !isPublishAvatarTrack;
        options.publishAvatarTrack = publish && isPublishAvatarTrack;
        options.publishAudioTrack = publish;
        options.autoSubscribeAudio = true;
        options.autoSubscribeVideo = true;
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptionsHashMap.put(channelId, options);

        RtcConnection connection = new RtcConnection(channelId, _uid);
        connectionMap.put(channelId, connection);
        engine.setVideoEncoderConfigurationEx(encoderConfiguration, connection);

        int ret = engine.joinChannelEx(token, connection, options, new IRtcEngineEventHandler() {
            @Override
            public void onError(int err) {
                super.onError(err);
                if (listener != null) {
                    listener.onError(err, "");
                }
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                super.onJoinChannelSuccess(channel, uid, elapsed);
                if (listener != null) {
                    listener.onJoinSuccess(channel, uid);
                }
            }

            @Override
            public void onUserJoined(int uid, int elapsed) {
                super.onUserJoined(uid, elapsed);
                if (listener != null) {
                    listener.onUserJoined(channelId, uid);
                }
            }

            @Override
            public void onUserOffline(int uid, int reason) {
                super.onUserOffline(uid, reason);
                if (listener != null) {
                    listener.onUserOffline(channelId, uid);
                }
            }

            @Override
            public void onStreamMessage(int uid, int streamId, byte[] data) {
                super.onStreamMessage(uid, streamId, data);
                OnStreamMessageListener listener = dataStreamListener.get(streamId);
                if (listener != null) {
                    listener.onMessageReceived(streamId, uid, new String(data));
                }
            }
        });
        Log.i(TAG, String.format("joinChannel channel %s ret %d", channelId, ret));
    }

    public void leaveChannel(String channelId) {
        if (engine == null) {
            return;
        }

        RtcConnection conn = connectionMap.get(channelId);
        if (conn != null) {
            engine.leaveChannelEx(conn);
        }
    }

    public void setPublishTracks(String channelId, boolean publish) {
        if (engine == null) {
            return;
        }

        ChannelMediaOptions options = new ChannelMediaOptions();
        options.publishCameraTrack = publish && !isPublishAvatarTrack;
        options.publishAvatarTrack = publish && isPublishAvatarTrack;
        options.publishAudioTrack = publish;
        options.autoSubscribeAudio = true;
        options.autoSubscribeVideo = true;
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;

        RtcConnection rtcConnection = connectionMap.get(channelId);
        if (rtcConnection != null) {
            engine.updateChannelMediaOptionsEx(options, rtcConnection);
        } else {
            engine.updateChannelMediaOptions(options);
        }
    }

    public boolean isPublishAvatarTrack() {
        return isPublishAvatarTrack;
    }

    public void setOnMediaOptionUpdateListener(OnMediaOptionUpdateListener onMediaOptionUpdateListener) {
        this.onMediaOptionUpdateListener = onMediaOptionUpdateListener;
        if (onMediaOptionUpdateListener != null) {
            onMediaOptionUpdateListener.onMediaOptionUpdated();
        }
    }

    public void updateChannelTrack(String channelId, boolean isPublishAvatarTrack) {
        RtcConnection rtcConnection = connectionMap.get(channelId);
        if (rtcConnection == null) {
            return;
        }
        if (isPublishAvatarTrack != this.isPublishAvatarTrack) {
            ChannelMediaOptions channelMediaOptions = mediaOptionsHashMap.get(channelId);
            if (channelMediaOptions == null) {
                return;
            }
            channelMediaOptions.publishCameraTrack = !isPublishAvatarTrack;
            channelMediaOptions.publishAvatarTrack = isPublishAvatarTrack;
            engine.updateChannelMediaOptionsEx(channelMediaOptions, rtcConnection);
            engine.setVideoEncoderConfigurationEx(encoderConfiguration, rtcConnection);
            this.isPublishAvatarTrack = isPublishAvatarTrack;
            if (onMediaOptionUpdateListener != null) {
                onMediaOptionUpdateListener.onMediaOptionUpdated();
            }
        }
    }

    public void renderRemoteVideo(FrameLayout container, String channelId, int uid) {
        if (engine == null) {
            return;
        }
        container.removeAllViews();
        TextureView view = new TextureView(container.getContext());
        container.addView(view);
        engine.setupRemoteVideoEx(new VideoCanvas(view, RENDER_MODE_HIDDEN, uid), connectionMap.get(channelId));
    }

    public void reset(boolean isStopPreview) {
        Log.d(TAG, "stopPreview --> reset isStopPreview=" + isStopPreview);

        for (String s : connectionMap.keySet()) {
            engine.leaveChannelEx(connectionMap.get(s));
            mediaOptionsHashMap.remove(s);
        }
        connectionMap.clear();

        if (engine != null) {
            engine.leaveChannel();
            if (isStopPreview) {
                engine.stopPreview();
                engine.setCameraCapturerConfiguration(new CameraCapturerConfiguration(cameraDirection,
                        new CameraCapturerConfiguration.CaptureFormat(
                                encoderConfiguration.dimensions.width,
                                encoderConfiguration.dimensions.height,
                                encoderConfiguration.frameRate)));
            }
        }

        onMediaOptionUpdateListener = null;
    }

    public void muteLocalAudio(boolean mute) {
        if (engine == null) {
            return;
        }
        engine.enableLocalAudio(!mute);
    }

    public void muteRemoteVideo(String roomId, int uid, boolean mute) {
        if (engine == null) {
            return;
        }
        RtcConnection rtcConnection = connectionMap.get(roomId);
        if (rtcConnection != null) {
            engine.muteRemoteVideoStreamEx(uid, mute, rtcConnection);
        } else {
            engine.muteRemoteVideoStream(uid, mute);
        }
    }

    public void muteRemoteAudio(String roomId, int uid, boolean mute) {
        if (engine == null) {
            return;
        }
        RtcConnection rtcConnection = connectionMap.get(roomId);
        if (rtcConnection != null) {
            engine.muteRemoteAudioStreamEx(uid, mute, rtcConnection);
        } else {
            engine.muteRemoteAudioStream(uid, mute);
        }
    }

    public void disableAvatarGeneratorItems(int type) {
        this.enableAvatarGeneratorItems(false, type, "", true);
    }

    public void enableAvatarGeneratorItems(int type, String bundlePath) {
        this.enableAvatarGeneratorItems(true, type, bundlePath, false);
    }

    public void enableAvatarGeneratorItems(int type, String bundlePath, boolean replaceOld) {
        this.enableAvatarGeneratorItems(true, type, bundlePath, replaceOld);
    }

    private void enableAvatarGeneratorItems(boolean enable, int type, String bundlePath, boolean replaceOld) {
        if (avatarEngine == null) {
            return;
        }
        Log.d(TAG, "Avatar >> enableAvatarGeneratorItems enable=" + enable + ", type=" + type + ", bundlePath=" + bundlePath + ", replaceOld=" + replaceOld);
        String oldBundlePath = avatarItemEnableMap.get(type);
        if (Objects.equals(bundlePath, oldBundlePath) && !replaceOld) {
            return;
        }
        String setBundlePath = bundlePath;
        if (TextUtils.isEmpty(setBundlePath)) {
            enable = false;
            setBundlePath = avatarItemEnableMap.get(type);
        }
        avatarItemEnableMap.put(type, bundlePath);
        avatarEngine.enableLocalUserAvatarItems(enable, type, setBundlePath);
    }

    public void setGeneratorOptions(String option, AvatarOptionValue value) {
        if (avatarEngine == null) {
            return;
        }

        Log.d(TAG, "Avatar >> setGeneratorOptions option=" + option + ", value=" + toString(value));
        //avatarEngine.setLocalUserAvatarOptions(option, value);
    }

    public void GetGeneratorOptions(String option, Constants.AvatarValueType type, AvatarOptionValue outValue) {
        if (avatarEngine == null) {
            return;
        }
        //avatarEngine.GetGeneratorOptions(option, type, outValue);
        Log.d(TAG, "Avatar >> GetGeneratorOptions option=" + option + ", type=" + type + ", outValue=" + toString(outValue));
    }

    private String toString(AvatarOptionValue value) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("type=").append(value.type).append(",");

        String valueStr = "";
        if (value.type == Constants.AvatarValueType.DoubleArray) {
            valueStr = Arrays.toString((double[]) value.value);
        } else if (value.type == Constants.AvatarValueType.UInt8Array) {
            valueStr = Arrays.toString((int[]) value.value);
        } else if (value.type == Constants.AvatarValueType.FloatArray) {
            valueStr = Arrays.toString((float[]) value.value);
        } else {
            valueStr = String.valueOf(value.value);
        }
        sb.append("value=").append(valueStr).append("}");
        return sb.toString();
    }

    public void destroy() {
        if (avatarEngine != null) {
            Set<Integer> avatarItemIds = avatarItemEnableMap.keySet();
            for (Integer id : avatarItemIds) {
                if (id == null) {
                    continue;
                }
                String bundlePath = avatarItemEnableMap.get(id);
                if (!TextUtils.isEmpty(bundlePath)) {
                    disableAvatarGeneratorItems(id);
                }
            }
            avatarItemEnableMap.clear();
            avatarEngine.unregisterEventHandler(this);
            avatarEngine = null;
        }
        if (engine != null) {
            engine.leaveChannel();
            engine.stopPreview();
            RtcEngine.destroy();
            engine = null;
        }
        isInitialized = false;
    }

    private void iterateAvatarHandlers(OnAvatarHandlerIterateListener handler) {
        int size = avatarEventHandlers.size();
        for (int i = 0; i < size; i++) {
            handler.onHandlerIterate(avatarEventHandlers.get(i));
        }
    }

    /**
     * The result will be returned in local user avatar event callback
     */
    public void requestDressOptionList() {
        // For current sdk version, we use setXXX method instead of
        // getXX method, will be replaced in future version.
        avatarEngine.setLocalUserAvatarOptions(AvatarConfigManager.AvatarConfig.DRESS_KEY_REQUEST_FULL_LIST, null);
    }

    public int startDressing() {
        return avatarEngine.setLocalUserAvatarOptions(AvatarConfigManager.AvatarConfig.DRESS_KEY_START, null);
    }

    public int stopDressing() {
        return avatarEngine.setLocalUserAvatarOptions(AvatarConfigManager.AvatarConfig.DRESS_KEY_STOP, null);
    }

    /**
     * The result will be returned in local user avatar event callback
     */
    public void requestFaceEditOptionList() {
        // For current sdk version, we use setXXX method instead of
        // getXX method, will be replaced in future version.
        avatarEngine.setLocalUserAvatarOptions(AvatarConfigManager.AvatarConfig.FACE_EDIT_KEY_REQUEST_FULL_LIST, null);
    }

    public int startFaceEdit() {
        return avatarEngine.setLocalUserAvatarOptions(AvatarConfigManager.AvatarConfig.FACE_EDIT_KEY_START, null);
    }

    public int stopFaceEdit() {
        return avatarEngine.setLocalUserAvatarOptions(AvatarConfigManager.AvatarConfig.FACE_EDIT_KEY_STOP, null);
    }

    public int setLocalAvatarOption(String key, String value) {
        Log.d(TAG, "setLocalAvatarOption, key:" + key + ", value:" + value);
        return avatarEngine.setLocalUserAvatarOptions(key, value.getBytes());
    }

    public int setLocalAvatarQuality(AvatarRenderQuality quality) {
        currRenderQuality = quality;
        String id = quality.getStringId();
        return setLocalAvatarOption(AvatarConfigManager.AvatarConfig.KEY_AVATAR_QUALITY, id);
    }

    /**
     * Implementation of IAvatarEngineEventHandler
     */
    @Override
    public void onLocalUserAvatarStarted(boolean b, int i, String s) {
        Log.e(TAG, "onLocalUserAvatarStarted " + b + " " + i + " " + s);
        iterateAvatarHandlers(handler -> handler.onLocalUserAvatarStarted(b, i, s));
    }

    /**
     * Implementation of IAvatarEngineEventHandler
     */
    @Override
    public void onLocalUserAvatarError(int i, String s) {
        Log.e(TAG, "onLocalUserAvatarError " + i + " " + s);
        iterateAvatarHandlers(handler -> handler.onLocalUserAvatarError(i, s));
    }

    /**
     * Implementation of IAvatarEngineEventHandler
     */
    @Override
    public void onLocalUserAvatarEvent(String s, String s1) {
        Log.e(TAG, s1 + "," + s1);
        iterateAvatarHandlers(handler -> handler.onLocalUserAvatarEvent(s, s1));
    }

    /**
     * Implementation of IAvatarEngineEventHandler
     */
    @Override
    public void onRemoteUserAvatarStarted(int i, boolean b, int i1, String s) {
        Log.e(TAG, "onRemoteUserAvatarStarted " + b + " " + i + " " + s);
        iterateAvatarHandlers(handler -> handler.onRemoteUserAvatarStarted(i, b, i1, s));
    }

    /**
     * Implementation of IAvatarEngineEventHandler
     */
    @Override
    public void onRemoteUserAvatarError(int i, int i1, String s) {
        Log.e(TAG, "onRemoteUserAvatarError " + i + " " + s);
        iterateAvatarHandlers(handler -> handler.onRemoteUserAvatarError(i, i1, s));
    }

    /**
     * Implementation of IAvatarEngineEventHandler
     */
    @Override
    public void onRemoteUserAvatarEvent(int i, String s, String s1) {
        Log.e(TAG, "onRemoteUserAvatarEvent " + i + " " + s + " " + s1);
        iterateAvatarHandlers(handler -> handler.onRemoteUserAvatarEvent(i, s, s1));
    }

    public interface OnInitializeListener {
        void onError(int code, String message);

        void onSuccess();
    }

    public interface OnChannelListener {
        void onError(int code, String message);

        void onJoinSuccess(String channelId, int uid);

        void onUserJoined(String channelId, int uid);

        void onUserOffline(String channelId, int uid);
    }

    public interface OnVideoFrameRenderListener {
        void onVideoFrameRender(VideoFrame videoFrame);
    }

    public interface OnStreamMessageListener {
        void onMessageReceived(int dataStreamId, int fromUid, String message);
    }

    public interface OnMediaOptionUpdateListener {
        void onMediaOptionUpdated();
    }

    public static class BaseAvatarEventHandler implements IAvatarEngineEventHandler {
        @Override
        public void onLocalUserAvatarStarted(boolean b, int i, String s) {

        }

        @Override
        public void onLocalUserAvatarError(int i, String s) {

        }

        @Override
        public void onLocalUserAvatarEvent(String s, String s1) {

        }

        @Override
        public void onRemoteUserAvatarStarted(int i, boolean b, int i1, String s) {

        }

        @Override
        public void onRemoteUserAvatarError(int i, int i1, String s) {

        }

        @Override
        public void onRemoteUserAvatarEvent(int i, String s, String s1) {

        }
    }
}
