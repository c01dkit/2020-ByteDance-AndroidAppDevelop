package com.example.chapter_8;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class VideoRecordingActivity extends AppCompatActivity {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private Handler mHandler;
    private Button mButtonLeft, mButtonRight;
    private int mState;
    private CaptureRequest.Builder mRequestPreviewBuilder; //预览请求的Builder
    private CameraCaptureSession mCameraCaptureSession;
    private ImageReader mImageReader;
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener;
    private MediaRecorder mMediaRecorder;
    private static final String TAG = "VideoRecordingActivity";
    private static final int STATE_PREVIEW = 1;
    private static final int STATE_WAITING_CAPTURE = 2;
    private static final int PERMISSION_REQUEST_CAMERA_PATH_CODE = 3;

    //第0步，初始化页面布局，绑定控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recording);
        mSurfaceView = findViewById(R.id.surfaceView);
        mButtonLeft = findViewById(R.id.buttonLeft);
        mButtonRight = findViewById(R.id.buttonRight);

        mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        mSurfaceHolder = mSurfaceView.getHolder();

        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                init();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });

        mButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButtonLeft.getText().toString().equals(getString(R.string.record_start))) {
                    mMediaRecorder = new MediaRecorder();
                    try {
                        setUpMediaRecorder();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startRecordingVideo();
                    mButtonLeft.setText(R.string.record_stop);
                } else {
                    mButtonLeft.setText(R.string.record_start);
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                }
            }
        });

        mButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mMediaRecorder) return;
                if (mButtonRight.getText().toString().equals(getString(R.string.record_pause))){
                    mButtonRight.setText(R.string.record_resume);
                    mMediaRecorder.pause();
                } else {
                    mButtonRight.setText(R.string.record_pause);
                    mMediaRecorder.resume();
                }
            }
        });
    }

    //第一步，搜索相机，获取摄像头信息并打开摄像头
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void init() {
        //获取摄像头相关属性，ID在manager.getCameraIdList()里，这里直接指定前摄像头
        //获取更多属性需要 CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);

        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        try {
            String cameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;  // "0"
            mImageReader = ImageReader.newInstance(mSurfaceView.getWidth(), mSurfaceView.getHeight(),
                    ImageFormat.JPEG,/*maxImages*/7);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ||ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ||ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(VideoRecordingActivity.this,permissions,
                        PERMISSION_REQUEST_CAMERA_PATH_CODE);
            }
            mCameraManager.openCamera(cameraID, DeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "open camera failed." + e.getMessage());
        }
    }

    //第二步，创建预览界面，DeviceStateCallback为openCamera第二个参数，用于监听摄像头状态
    private CameraDevice.StateCallback DeviceStateCallback = new CameraDevice.StateCallback() {

        //相机打开时调用此方法
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG,"DeviceStateCallback:camera was opened.");
            mCameraDevice = camera;
            try {
                createCameraCaptureSession();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
        }
    };

    //第三步，相机开始预览，创建CameraCaptureSession对象
    private void createCameraCaptureSession() throws CameraAccessException {
        Log.d(TAG,"createCameraCaptureSession");
        //将CaptureRequest构造器与Surface对象绑定
        mRequestPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mRequestPreviewBuilder.addTarget(mSurfaceHolder.getSurface());
        mState = STATE_PREVIEW;

        //为相机预览创建一个CameraCaptureSession对象
        mCameraDevice.createCaptureSession(
                Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()),
                mSessionPreviewStateCallback, mHandler);
    }

    //第四步，预览界面需要间隔刷新
    private CameraCaptureSession.StateCallback mSessionPreviewStateCallback = new
            CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Log.d(TAG,"mSessionPreviewStateCallback onConfigured");
                    mCameraCaptureSession = session;
                    try {
                        mRequestPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        setCameraCaptureSession();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                        Log.e(TAG,"set preview builder failed."+e.getMessage());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            };

    //第五步，为CameraCaptureSession设定特征，只要未开始拍照或录像，则不断重复刷新
    private void setCameraCaptureSession() throws CameraAccessException {
        //设置预览界面的特征，通过mPreviewRequestBuilder.set()方法,例如，闪光灯，zoom调焦等

        //为CameraCaptureSession设置间隔的CaptureRequest，间隔刷新预览界面。
        mCameraCaptureSession.setRepeatingRequest(mRequestPreviewBuilder.build(), mSessionCaptureCallback, mHandler);
    }

    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback =
            new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,@NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    Log.d(TAG,"mSessionCaptureCallback, onCaptureCompleted");
                    mCameraCaptureSession = session;
                    checkState(result);
                }

                @Override
                public void onCaptureProgressed(@NonNull CameraCaptureSession session,@NonNull CaptureRequest request,
                                                @NonNull CaptureResult partialResult) {
                    Log.d(TAG,"mSessionCaptureCallback,  onCaptureProgressed");
                    mCameraCaptureSession = session;
                    checkState(partialResult);
                }

                private void checkState(CaptureResult result) {
                    switch (mState) {
                        case STATE_PREVIEW:
                            // NOTHING
                            break;
                        case STATE_WAITING_CAPTURE:
                            int afState = result.get(CaptureResult.CONTROL_AF_STATE);

                            if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                                    CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState
                                    ||  CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState
                                    || CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED == afState) {
                                //do something like save picture
                            }
                            break;
                    }
                }

            };

    //第六步，使用操作类MediaRecorder实现数据写入
    private void setUpMediaRecorder() throws IOException {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        String mNextVideoAbsolutePath = getOutputMediaPath();
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(5*1024*1024);
        //每秒30帧
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());

//        int rotation = VideoRecordingActivity.this.getWindowManager().getDefaultDisplay().getRotation();
//        switch (mSensorOrientation) {
//            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
//                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
//                break;
//            case SENSOR_ORIENTATION_INVERSE_DEGREES:
//                mMediaRecorder.setOrientationHint(ORIENTATIONS.get(rotation));
//                break;
//            default:
//                break;
//        }
        Log.d(TAG, "setUpMediaRecorder: prepare");
        Log.d(TAG, "setUpMediaRecorder: "+ mNextVideoAbsolutePath);
        mMediaRecorder.prepare();
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "REC_" + timeStamp + ".mp4");
        if (!mediaFile.exists()){
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    /**
     * 开始视频录制。
     */

    private void startRecordingVideo() {
        try {
            //创建录制的session会话中的请求
            mRequestPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mRequestPreviewBuilder.addTarget(mSurfaceHolder.getSurface());
            //设置录制参数，这里省略


            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {

                    mCameraCaptureSession = cameraCaptureSession;
                    Log.i(TAG, " startRecordingVideo  正式开始录制 ");
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
                //该接口的方法，部分省略


            }, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //录制过程中，不断刷新录制界面
    private void updatePreview() {

        try {
            mCameraCaptureSession.setRepeatingRequest(mRequestPreviewBuilder.build(), null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}