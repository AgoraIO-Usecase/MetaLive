package io.agora.metalive.manager;

import static io.agora.rtc2.video.VideoCanvas.RENDER_MODE_HIDDEN;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30;
import static io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_60;
import static io.agora.rtc2.video.VideoEncoderConfiguration.MIRROR_MODE_TYPE;
import static io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_1280x720;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_320x240;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_480x360;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_640x360;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_640x480;
import static io.agora.rtc2.video.VideoEncoderConfiguration.VD_960x720;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.agora.metalive.R;
import io.agora.metalive.databinding.AvatarLoadingLayoutBinding;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IAvatarEngine;
import io.agora.rtc2.IAvatarEngineEventHandler;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.AvatarConfigs;
import io.agora.rtc2.video.AvatarContext;
import io.agora.rtc2.video.CameraCapturerConfiguration;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class RtcManager {
    private static final String TAG = "RtcManager";

    private static final int LOCAL_RTC_UID = 0;
    private static final int DEFAULT_BITRATE = 700;
    public static final List<VideoEncoderConfiguration.VideoDimensions> sVideoDimensions = Arrays.asList(
            VD_320x240,
            VD_480x360,
            VD_640x360,
            VD_640x480,
            new VideoEncoderConfiguration.VideoDimensions(960,540),
            VD_960x720,
            VD_1280x720
    );
    public static final List<FRAME_RATE> sFrameRates = Arrays.asList(
            FRAME_RATE_FPS_15,
            FRAME_RATE_FPS_24,
            FRAME_RATE_FPS_30,
            FRAME_RATE_FPS_60
    );
    public static final List<AvatarRenderQuality> sRenderQuality = Arrays.asList(
            AvatarRenderQuality.Low,
            AvatarRenderQuality.Medium,
            AvatarRenderQuality.High,
            AvatarRenderQuality.Ultra
    );

    public static final VideoEncoderConfiguration encoderConfiguration =
            new VideoEncoderConfiguration(
                    VD_640x360,
                    FRAME_RATE_FPS_30,
                    DEFAULT_BITRATE,
                    ORIENTATION_MODE_FIXED_PORTRAIT);
    public static AvatarRenderQuality currRenderQuality = AvatarRenderQuality.High;

    private static final CameraCapturerConfiguration.CAMERA_DIRECTION cameraDirection =
            CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_FRONT;

    private volatile boolean isInitialized = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private RtcEngineEx engine;
    private final Map<String, RtcConnection> connectionMap = new HashMap<>();
    private final Map<String, ChannelMediaOptions> mediaOptionsHashMap = new HashMap<>();

    private IAvatarEngine avatarEngine;
    private boolean avatarIsLoaded = false;
    private final List<Runnable> avatarLoadedPendingRun = new ArrayList<>();
    private final Map<String, DataCallback<String>> localAvatarEventCallbackMap = new HashMap<>();
    private final IAvatarEngineEventHandler avatarEngineEventHandler = new IAvatarEngineEventHandler() {
        /**
         * Implementation of IAvatarEngineEventHandler
         */
        @Override
        public void onLocalUserAvatarStarted(boolean b, int i, String s) {
            Log.e(TAG, "onLocalUserAvatarStarted " + b + " " + i + " " + s);
        }

        /**
         * Implementation of IAvatarEngineEventHandler
         */
        @Override
        public void onLocalUserAvatarError(int i, String s) {
            Log.e(TAG, "onLocalUserAvatarError " + i + " " + s);
        }

        /**
         * Implementation of IAvatarEngineEventHandler
         */
        @Override
        public void onLocalUserAvatarEvent(String key, String value) {
            Log.e(TAG, "onLocalUserAvatarEvent " + key + "," + value);
            if ("set_avatar_success".equalsIgnoreCase(key)) {
                // module loaded successfully
                onAvatarLoaded();
                return;
            }
            DataCallback<String> callback = localAvatarEventCallbackMap.get(key);
            if (callback != null) {
                runOnUiThread(() -> callback.onSuccess(value));
                localAvatarEventCallbackMap.remove(key);
            }
        }

        /**
         * Implementation of IAvatarEngineEventHandler
         */
        @Override
        public void onRemoteUserAvatarStarted(int i, boolean b, int i1, String s) {
            Log.e(TAG, "onRemoteUserAvatarStarted " + b + " " + i + " " + s);
        }

        /**
         * Implementation of IAvatarEngineEventHandler
         */
        @Override
        public void onRemoteUserAvatarError(int i, int i1, String s) {
            Log.e(TAG, "onRemoteUserAvatarError " + i + " " + s);
        }

        /**
         * Implementation of IAvatarEngineEventHandler
         */
        @Override
        public void onRemoteUserAvatarEvent(int i, String s, String s1) {
            Log.e(TAG, "onRemoteUserAvatarEvent " + i + " " + s + " " + s1);
        }
    };

    private static volatile RtcManager INSTANCE;

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
            if (listener != null) {
                listener.onSuccess();
            }
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
                }

                @Override
                public void onRemoteVideoStats(RemoteVideoStats stats) {
                    super.onRemoteVideoStats(stats);
                    Log.d(TAG, String.format("onRemoteVideoStats stats decoderOutputFrameRate=%d, resolution=%dx%d", stats.decoderOutputFrameRate, stats.width, stats.height));
                }
            });

            engine.setLogLevel(Constants.LogLevel.getValue(Constants.LogLevel.LOG_LEVEL_ERROR));
            avatarEngine = engine.queryAvatarEngine();

            AvatarContext avatarContext = new AvatarContext(
                    context.getString(R.string.ai_app_id),
                    context.getString(R.string.ai_token_id));
            avatarEngine.initialize(avatarContext);
            avatarEngine.registerEventHandler(avatarEngineEventHandler);
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
            if (listener != null) {
                listener.onSuccess();
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(-1, "RtcEngine create exception : " + e.toString());
            }
        }
    }

    public void setCameraAndEncoderResolution(VideoEncoderConfiguration.VideoDimensions dimension) {
        encoderConfiguration.dimensions = dimension;
        engine.setCameraCapturerConfiguration(
                new CameraCapturerConfiguration(cameraDirection,
                        new CameraCapturerConfiguration.CaptureFormat(
                                encoderConfiguration.dimensions.width,
                                encoderConfiguration.dimensions.height,
                                encoderConfiguration.frameRate)));
        updateVideoEncoderConfigration();
    }

    public void setEncoderVideoFrameRate(FRAME_RATE frameRate) {
        encoderConfiguration.frameRate = frameRate.getValue();
        engine.setCameraCapturerConfiguration(
                new CameraCapturerConfiguration(cameraDirection,
                        new CameraCapturerConfiguration.CaptureFormat(
                                encoderConfiguration.dimensions.width,
                                encoderConfiguration.dimensions.height,
                                encoderConfiguration.frameRate)));
        updateVideoEncoderConfigration();
    }

    public void setEncoderVideoBitrate(int bitrate) {
        encoderConfiguration.bitrate = bitrate;
        updateVideoEncoderConfigration();
    }

    private void updateVideoEncoderConfigration() {
        for (String key : connectionMap.keySet()) {
            RtcConnection rtcConnection = connectionMap.get(key);
            if(rtcConnection != null){
                engine.setVideoEncoderConfigurationEx(encoderConfiguration, rtcConnection);
            }
        }
        engine.setVideoEncoderConfiguration(encoderConfiguration);
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
        videoCanvas.mirrorMode = MIRROR_MODE_TYPE.MIRROR_MODE_AUTO.getValue();
        avatarEngine.setupLocalVideoCanvas(videoCanvas);
        Log.d(TAG, "RTCManager renderLocalAvatarVideo cost time ms=" + (System.currentTimeMillis() - startTime));
        //container.postDelayed(this::onAvatarLoaded, 10000L);

        // add Loading View
        if (!avatarIsLoaded) {
            AvatarLoadingLayoutBinding inflate = AvatarLoadingLayoutBinding.inflate(LayoutInflater.from(container.getContext()), container, true);
            doOnAvatarLoaded(new WeakRunnable<LinearLayout>(inflate.getRoot()) {
                @Override
                protected void runSafe(@NonNull LinearLayout data) {
                    data.setVisibility(View.GONE);
                }
            });
        }
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

    public void renderRemoteVideo(FrameLayout container, String channelId, int uid) {
        if (engine == null) {
            return;
        }
        container.removeAllViews();
        TextureView view = new TextureView(container.getContext());
        container.addView(view);
        engine.setupRemoteVideoEx(new VideoCanvas(view, RENDER_MODE_HIDDEN, uid), connectionMap.get(channelId));
    }

    public void joinChannel(String channelId, String uid, String token, boolean publishAudio, boolean publishCamera, boolean publishAvatar, OnChannelListener listener) {
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
        options.publishCameraTrack = publishCamera;
        options.publishAvatarTrack = publishAvatar;
        options.publishAudioTrack = publishAudio;
        options.autoSubscribeAudio = true;
        options.autoSubscribeVideo = true;
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptionsHashMap.put(channelId, options);

        RtcConnection connection = new RtcConnection(channelId, _uid);
        connectionMap.put(channelId, connection);

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

        });
        engine.setVideoEncoderConfigurationEx(encoderConfiguration, connection);

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

    public void setPublishAudio(String channelId, boolean publishAudio) {
        if (engine == null) {
            return;
        }
        ChannelMediaOptions options = mediaOptionsHashMap.get(channelId);
        if (options == null || options.publishAudioTrack == publishAudio) {
            return;
        }
        options.publishAudioTrack = publishAudio;
        RtcConnection rtcConnection = connectionMap.get(channelId);
        if (rtcConnection != null) {
            engine.updateChannelMediaOptionsEx(options, rtcConnection);
        } else {
            engine.updateChannelMediaOptions(options);
        }
    }

    public void setPublishVideo(String channelId, boolean publishAvatar, boolean publishCamera) {
        if (engine == null) {
            return;
        }
        ChannelMediaOptions options = mediaOptionsHashMap.get(channelId);
        if (options == null || (options.publishAvatarTrack == publishAvatar && options.publishCameraTrack == publishCamera)) {
            return;
        }
        options.publishAvatarTrack = publishAvatar;
        options.publishCameraTrack = publishCamera;
        RtcConnection rtcConnection = connectionMap.get(channelId);
        if (rtcConnection != null) {
            engine.updateChannelMediaOptionsEx(options, rtcConnection);
        } else {
            engine.updateChannelMediaOptions(options);
        }
    }

    public void enableLocalAudio(boolean enable) {
        if (engine == null) {
            return;
        }
        engine.enableLocalAudio(enable);
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
    }

    public void destroy() {
        if (avatarEngine != null) {
            avatarEngine.unregisterEventHandler(avatarEngineEventHandler);
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

    public void setLocalAvatarOption(String key, String value) {
        setLocalAvatarOption(key, value, null);
    }

    public void setLocalAvatarOption(String key, String value, DataCallback<String> callback) {
        Log.d(TAG, "setLocalAvatarOption, key:" + key + ", value:" + value);
        doOnAvatarLoaded(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    String ret = avatarEngine.getLocalUserAvatarOptions(key, value == null ? "": value);
                    Log.d(TAG, "getLocalUserAvatarOptions >> key=" + key + ",value=" + value + ",ret=" + ret);
                    localAvatarEventCallbackMap.put(key, callback);
                } else {
                    int ret = avatarEngine.setLocalUserAvatarOptions(key, value == null ? null : value.getBytes());
                    Log.d(TAG, "setLocalUserAvatarOptions >> key=" + key + ",value=" + value + ",ret=" + ret);
                    if (ret != 0) {
                        try {
                            throw new RuntimeException("setLocalUserAvatarOptions error >> ret=" + ret + ", key=" + key + ", value=" + value);
                        } catch (Exception e) {
                            Log.e(TAG, "", e);
                        }
                    }
                }
            }
        });
    }

    public void setLocalAvatarQuality(AvatarRenderQuality quality) {
        currRenderQuality = quality;
        String id = quality.getStringId();
        setLocalAvatarOption(AvatarManager.AvatarConfig.KEY_AVATAR_QUALITY, id);
    }

    private void onAvatarLoaded() {
        avatarIsLoaded = true;
        Iterator<Runnable> iterator = avatarLoadedPendingRun.iterator();
        while (iterator.hasNext()) {
            Runnable next = iterator.next();
            if (next != null) {
                runOnUiThread(next);
            }
            iterator.remove();
        }
    }

    private void doOnAvatarLoaded(@NonNull Runnable runnable) {
        if (avatarIsLoaded) {
            runOnUiThread(runnable);
            return;
        }
        avatarLoadedPendingRun.add(runnable);
    }

    private void runOnUiThread(@NonNull Runnable runnable) {
        if (Thread.currentThread() == mainHandler.getLooper().getThread()) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        }
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

}
