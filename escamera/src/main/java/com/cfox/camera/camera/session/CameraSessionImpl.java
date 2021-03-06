package com.cfox.camera.camera.session;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;

import androidx.annotation.NonNull;

import com.cfox.camera.EsException;
import com.cfox.camera.camera.device.EsCameraDevice;
import com.cfox.camera.log.EsLog;
import com.cfox.camera.surface.SurfaceManager;
import com.cfox.camera.utils.EsError;
import com.cfox.camera.EsParams;
import com.cfox.camera.utils.WorkerHandlerManager;

import io.reactivex.Observable;

public class CameraSessionImpl implements CameraSession {
    private final EsCameraDevice mEsCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private String mCameraId;

    CameraSessionImpl(EsCameraDevice cameraDevice) {
        mEsCameraDevice = cameraDevice;
    }

    @Override
    public Observable<EsParams> onOpenCamera(final EsParams esParams) {
        mCameraId = esParams.get(EsParams.Key.CAMERA_ID);
        EsLog.d("open camera device ===> camera id:" + mCameraId);
        return mEsCameraDevice.openCameraDevice(esParams).map(esParams1 -> {
            mCameraDevice = esParams1.get(EsParams.Key.CAMERA_DEVICE);
            return esParams1;
        });
    }

    @Override
    public CaptureRequest.Builder onCreateRequestBuilder(int templateType) throws CameraAccessException {
        return mCameraDevice.createCaptureRequest(templateType);
    }

    public Observable<EsParams> onCreateCaptureSession(final EsParams esParams) {
        final SurfaceManager surfaceManager = esParams.get(EsParams.Key.SURFACE_MANAGER);
        return Observable.create(emitter ->
                mCameraDevice.createCaptureSession(surfaceManager.getTotalSurface(), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                EsLog.d("onConfigured: create session success ....." + esParams);
                mCaptureSession = session;
                emitter.onNext(esParams);
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                emitter.onError(new EsException("Create Preview Session failed  " + esParams, EsError.ERROR_CODE_CREATE_PREVIEW_SESSION));

            }
        }, WorkerHandlerManager.getHandler(WorkerHandlerManager.Tag.T_TYPE_CAMERA)));
    }

    @Override
    public Observable<EsParams> onRepeatingRequest(EsParams esParams) {
        final CaptureRequest.Builder requestBuilder = esParams.get(EsParams.Key.REQUEST_BUILDER);
        final CameraCaptureSession.CaptureCallback captureCallback  = esParams.get(EsParams.Key.CAPTURE_CALLBACK);
        return Observable.create(emitter -> mCaptureSession.setRepeatingRequest(requestBuilder.build(), captureCallback,
                WorkerHandlerManager.getHandler(WorkerHandlerManager.Tag.T_TYPE_CAMERA)));
    }

    @Override
    public Observable<EsParams> onClose() {
        return mEsCameraDevice.closeCameraDevice(mCameraId).doOnNext(esParams -> {
            EsLog.d("Camera session close " + mCameraId);
            if (mCaptureSession != null) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
        });
    }

    @Override
    public void capture(EsParams esParams) throws CameraAccessException {
        EsLog.d("capture ==>" + esParams);
        CaptureRequest.Builder requestBuilder = esParams.get(EsParams.Key.REQUEST_BUILDER);
        CameraCaptureSession.CaptureCallback captureCallback =  esParams.get(EsParams.Key.CAPTURE_CALLBACK);
        mCaptureSession.capture(requestBuilder.build(), captureCallback, WorkerHandlerManager.getHandler(WorkerHandlerManager.Tag.T_TYPE_CAMERA));
    }

    @Override
    public void stopRepeating() throws CameraAccessException {
        mCaptureSession.stopRepeating();
        mCaptureSession.abortCaptures();
    }
}
