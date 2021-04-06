declare module 'cordova-plugin-camera-preview' {
  type CameraPreviewErrorHandler = (err: any) => any;
  type CameraPreviewSuccessHandler<T = any> = (data: T) => any;

  type CameraPreviewCameraDirection = 'back'|'front';
  type CameraPreviewColorEffect = 'aqua'|'blackboard'|'mono'|'negative'|'none'|'posterize'|'sepia'|'solarize'|'whiteboard';
  type CameraPreviewExposureMode = 'lock'|'auto'|'continuous'|'custom';
  type CameraPreviewFlashMode = 'off'|'on'|'auto'|'red-eye'|'torch';
  type CameraPreviewFocusMode = 'fixed'|'auto'|'continuous'|'continuous-picture'|'continuous-video'|'edof'|'infinity'|'macro';
  type CameraPreviewWhiteBalanceMode = 'lock'|'auto'|'continuous'|'incandescent'|'cloudy-daylight'|'daylight'|'fluorescent'|'shade'|'twilight'|'warm-fluorescent';

  interface CameraPreviewStartCameraOptions {
    alpha?: number;
    camera?: CameraPreviewCameraDirection|string;
    height?: number;
    previewDrag?: boolean;
    tapFocus?: boolean;
    tapPhoto?: boolean;
    toBack?: boolean;
    width?: number;
    x?: number;
    y?: number;
    disableExifHeaderStripping?: boolean;
    storeToFile?: boolean;
    scaleToFit?: boolean;
  }

  interface CameraPreviewTakePictureOptions {
    height?: number;
    quality?: number;
    width?: number;
  }

  interface CameraPreviewTakeSnapshotOptions {
    quality?: number;
  }

  interface CameraPreviewPreviewSizeDimension {
    x?: number;
    y?: number;
    height?: number;
    width?: number;
    scaleToFit?: boolean;
  }

  interface CameraPreviewCameraCharacteristics {
    CAMERA_CHARACTERISTICS: Array<{
      INFO_SUPPORTED_HARDWARE_LEVEL: number;
      LENS_FACING: number;
      SENSOR_INFO_PHYSICAL_SIZE_WIDTH: number;
      SENSOR_INFO_PHYSICAL_SIZE_HEIGHT: number;
      SENSOR_INFO_PIXEL_ARRAY_SIZE_WIDTH: number;
      SENSOR_INFO_PIXEL_ARRAY_SIZE_HEIGHT: number;
      LENS_INFO_AVAILABLE_FOCAL_LENGTHS: Array<{FOCAL_LENGTH: number}>;
    }>;
  }

  interface CameraPreview {
    startCamera(options?: CameraPreviewStartCameraOptions, onSuccess?: CameraPreviewSuccessHandler<string>, onError?: CameraPreviewErrorHandler): void;
    stopCamera(onSuccess?: CameraPreviewSuccessHandler<void>, onError?: CameraPreviewErrorHandler): void;
    switchCamera(onSuccess?: CameraPreviewSuccessHandler<void>, onError?: CameraPreviewErrorHandler): void;
    hide(onSuccess?: CameraPreviewSuccessHandler<void>, onError?: CameraPreviewErrorHandler): void;
    show(onSuccess?: CameraPreviewSuccessHandler<void>, onError?: CameraPreviewErrorHandler): void;
    takePicture(options?: CameraPreviewTakePictureOptions|CameraPreviewSuccessHandler<string>, onSuccess?: CameraPreviewSuccessHandler<string>|CameraPreviewErrorHandler, onError?: CameraPreviewErrorHandler): void;
    takeSnapshot(options?: CameraPreviewTakeSnapshotOptions|CameraPreviewSuccessHandler<string>, onSuccess?: CameraPreviewSuccessHandler<string>|CameraPreviewErrorHandler, onError?: CameraPreviewErrorHandler): void;
    startRecordVideo(options?:any|CameraPreviewSuccessHandler<undefined>, onSuccess?:CameraPreviewSuccessHandler|CameraPreviewErrorHandler, onError?:CameraPreviewErrorHandler):void;
    stopRecordVideo(onSuccess?:CameraPreviewSuccessHandler<string>, onError?:CameraPreviewErrorHandler):void;
    setPreviewSize(dimensions?: CameraPreviewPreviewSizeDimension, onSuccess?: CameraPreviewSuccessHandler<void>, onError?: CameraPreviewErrorHandler): void;
    setPreviewDimensions(dimensions?: CameraPreviewPreviewSizeDimension, onSuccess?: CameraPreviewSuccessHandler<void>, onError?: CameraPreviewErrorHandler): void;
    tapToFocus(xPoint?: number, yPoint?: number, onSuccess?: CameraPreviewSuccessHandler<{x: number, y: number}>, onError?: CameraPreviewErrorHandler): void;
    onBackButton(onSuccess?: CameraPreviewSuccessHandler<string>, onError?: CameraPreviewErrorHandler): void;
    getBlob(path: string, onSuccess?: CameraPreviewSuccessHandler<Blob>, onError?: CameraPreviewErrorHandler): void;
    getCameraCharacteristics(onSuccess?: CameraPreviewSuccessHandler<CameraPreviewCameraCharacteristics>, onError?: CameraPreviewErrorHandler): void;
    getHorizontalFOV(onSuccess?: CameraPreviewSuccessHandler<string>, onError?: CameraPreviewErrorHandler): void;
    getSupportedPictureSizes(onSuccess?: CameraPreviewSuccessHandler<Array<{width: number, height: number, preview: boolean}>>, onError?: CameraPreviewErrorHandler): void;
    getMaxZoom(onSuccess?: CameraPreviewSuccessHandler<number>, onError?: CameraPreviewErrorHandler): void;
    getZoom(onSuccess?: CameraPreviewSuccessHandler<number>, onError?: CameraPreviewErrorHandler): void;
    setZoom(zoom?: number, onSuccess?: CameraPreviewSuccessHandler<number>, onError?: CameraPreviewErrorHandler): void;
    getSupportedColorEffects(onSuccess?: CameraPreviewSuccessHandler<Array<CameraPreviewColorEffect | string>>, onError?: CameraPreviewErrorHandler): void;
    setColorEffect(effect: CameraPreviewColorEffect|string, onSuccess?: CameraPreviewSuccessHandler<CameraPreviewColorEffect | string>, onError?: CameraPreviewErrorHandler): void;
    getSupportedFlashModes(onSuccess?: CameraPreviewSuccessHandler<Array<CameraPreviewFlashMode | string>>, onError?: CameraPreviewErrorHandler): void;
    getFlashMode(onSuccess?: CameraPreviewSuccessHandler<CameraPreviewFlashMode | string>, onError?: CameraPreviewErrorHandler): void;
    setFlashMode(flashMode: CameraPreviewFlashMode|string, onSuccess?: CameraPreviewSuccessHandler<CameraPreviewFlashMode|string>, onError?: CameraPreviewErrorHandler): void;
    getSupportedFocusModes(onSuccess?: CameraPreviewSuccessHandler<Array<CameraPreviewFocusMode | string>>, onError?: CameraPreviewErrorHandler): void;
    getFocusMode(onSuccess?: CameraPreviewSuccessHandler<CameraPreviewFocusMode | string>, onError?: CameraPreviewErrorHandler): void;
    setFocusMode(focusMode?: CameraPreviewFocusMode|string, onSuccess?: CameraPreviewSuccessHandler<CameraPreviewFocusMode | string>, onError?: CameraPreviewErrorHandler): void;
    getExposureModes(onSuccess?: CameraPreviewSuccessHandler<Array<CameraPreviewExposureMode | string>>, onError?: CameraPreviewErrorHandler): void;
    getExposureMode(onSuccess?: CameraPreviewSuccessHandler<CameraPreviewFocusMode | string>, onError?: CameraPreviewErrorHandler): void;
    setExposureMode(exposureMode?: CameraPreviewExposureMode, onSuccess?: CameraPreviewSuccessHandler<CameraPreviewExposureMode | string>, onError?: CameraPreviewErrorHandler): void;
    getExposureCompensationRange(onSuccess?: CameraPreviewSuccessHandler<{min: number, max: number}>, onError?: CameraPreviewErrorHandler): void;
    getExposureCompensation(onSuccess?: CameraPreviewSuccessHandler<number>, onError?: CameraPreviewErrorHandler): void;
    setExposureCompensation(exposureCompensation?: number, onSuccess?: CameraPreviewSuccessHandler<number>, onError?: CameraPreviewErrorHandler): void;
    getSupportedWhiteBalanceModes(onSuccess?: CameraPreviewSuccessHandler<Array<CameraPreviewWhiteBalanceMode | string>>, onError?: CameraPreviewErrorHandler): void;
    getWhiteBalanceMode(onSuccess?: CameraPreviewSuccessHandler<CameraPreviewWhiteBalanceMode | string>, onError?: CameraPreviewErrorHandler): void;
    setWhiteBalanceMode(whiteBalanceMode?: CameraPreviewWhiteBalanceMode|string, onSuccess?: CameraPreviewSuccessHandler<CameraPreviewWhiteBalanceMode | string>, onError?: CameraPreviewErrorHandler): void;
  }
}
