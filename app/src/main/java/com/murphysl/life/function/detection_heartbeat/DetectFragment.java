package com.murphysl.life.function.detection_heartbeat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.murphysl.life.R;
import com.murphysl.life.base.BaseFragment;
import com.murphysl.life.util.HeartBeatUtil;
import com.murphysl.life.view.AutoFitTextureView;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * DetectFragment
 *
 * author: MurphySL
 * time: 2017/5/16 19:23
 */


public class DetectFragment extends BaseFragment {

    private AutoFitTextureView cameraView;
    private TextView textView;
    private TextView textView2;
    private ImageReader mImageReader;
    private CameraDevice mcamera;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;
    private Handler timer = new Handler();
    private int PREVIEW_WEIGHT = 50;
    private int PREVIEW_HEIGHT = 50;
    private WAVE current = WAVE.Trough;
    private enum WAVE{
        Crest,Trough
    }

    private Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            textView.setText(msg.arg1 +"");
            textView2.setText(beat+"");
        }
    };
    private int AVG_SAMPLE_NUM = 5;
    private int[] record = new int[AVG_SAMPLE_NUM];
    private int record_pos = 0;
    private int beat = 0;

    public static DetectFragment newInstance(){
        return new DetectFragment();
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mcamera =camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera,  int error) {

        }
    };

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = cameraView.getSurfaceTexture();
            assert texture != null;
            Surface surface = new Surface(texture);
            mPreviewRequestBuilder = mcamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            mPreviewRequestBuilder.addTarget(surface);
            mPreviewRequestBuilder.addTarget(mImageReader.getSurface());

            mcamera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mcamera) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                //闪光灯
                                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE,
                                        CaptureRequest.FLASH_MODE_TORCH);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();

                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        null, null);

                                timer.postDelayed(new Timer(), 100);

                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {

                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        cameraView = (AutoFitTextureView) view.findViewById(R.id.camera_view);
        textView = (TextView) view.findViewById(R.id.textView);
        textView2 = (TextView) view.findViewById(R.id.avg);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detection_heartbeat;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(cameraView.isAvailable()){
            openCamera();
        }else{
            cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    openCamera();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        }

    }

    @Override
    public void onPause() {
        closeCamera();
        super.onPause();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //requestCameraPermission();
            return;
        }

        mImageReader = ImageReader.newInstance(PREVIEW_WEIGHT, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 2);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(final ImageReader reader) {
                Image img = reader.acquireNextImage();
                ByteBuffer buffer = img.getPlanes()[0].getBuffer();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                img.close();
                detect(data);

            }
        } , null);

        String cameraId = CameraCharacteristics.LENS_FACING_FRONT + "";
        CameraManager manager = (CameraManager) getHoldingActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            manager.openCamera(cameraId,mStateCallback , null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void detect(byte[] data) {
        int red_avg = HeartBeatUtil.decodeImageReader2RedAvg(data, PREVIEW_WEIGHT , PREVIEW_HEIGHT);
        int avg = calculateAvg(red_avg);

        if(red_avg > avg){
            if(WAVE.Crest != current){
                beat ++;
                current = WAVE.Crest;
            }
        }else{
            current = WAVE.Trough;
        }

        Message message = new Message();
        message.arg1 = red_avg;
        message.arg2 = avg;
        UIHandler.sendMessage(message);
    }

    private int calculateAvg(int red_avg) {
        int sum = 0;
        int record_num = 0;
        for (int aRecord : record) {
            if (aRecord > 0) {
                sum += aRecord;
                record_num++;
            }
        }
        int avg = (record_num > 0) ? (sum/record_num) : 0;
        if (record_pos == AVG_SAMPLE_NUM)
            record_pos = 0;
        record[record_pos] = red_avg;
        record_pos++;

        return avg;
    }

    private void closeCamera() {
        if (null != mCaptureSession) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (null != mcamera) {
            mcamera.close();
            mcamera = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    private class Timer implements Runnable {

        @Override
        public void run() {
            timer.postDelayed(this, 100);
            try {
                mCaptureSession.capture(mPreviewRequestBuilder.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

    }
}
