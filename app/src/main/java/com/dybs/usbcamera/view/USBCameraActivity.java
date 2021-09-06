package com.dybs.usbcamera.view;

import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.dybs.usbcamera.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dybs.usbcamera.UVCCameraHelper;
import com.dybs.usbcamera.bean.ConfigBean;
import com.dybs.usbcamera.utils.FileUtils;
import com.dybs.usbcamera.utils.TtsSpeaker;
import com.dybs.usbcamera.utils.Utils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.serenegiant.usb.widget.UVCCameraTextureView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * UVCCamera use demo
 * <p>
 * Created by jiangdongguo on 2017/9/30.
 */

public class USBCameraActivity extends AppCompatActivity implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback {
    private static final String TAG = "Debug";
//    @BindView(R.id.camera_view)
    public TextureView mTextureView;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    @BindView(R.id.seekbar_brightness)
    public SeekBar mSeekBrightness;
    @BindView(R.id.tv_brightness)
    public TextView mTvBrightness;
    @BindView(R.id.seekbar_scale)
    public SeekBar mSeekScale;
    @BindView(R.id.tv_scale)
    public TextView mTvScale;
    @BindView(R.id.seekbar_contrast)
    public SeekBar mSeekContrast;
    @BindView(R.id.tv_contrast)
    public TextView mTvContrast;
    @BindView(R.id.switch_rec_voice)
    public Switch mSwitchVoice;
    @BindView(R.id.ll_setting)
    public LinearLayout mLlSetting;
    @BindView(R.id.rb_foramt)
    public RadioGroup mRbFormat;
    @BindView(R.id.tv_resolution)
    public TextView mTvResolution;
    @BindView(R.id.tv_fps)
    public TextView mTvFps;
    @BindView(R.id.cb_gray)
    public CheckBox mCbGray;

    public AutoScanView mAutoScanView;

    private UVCCameraHelper mCameraHelper;
    private UVCCameraTextureView mUVCCameraView;
    private AlertDialog mDialog;
    // 控制视频流的变换
    private Matrix mMatrix;
    private int mCenterX,mCenterY;
//    private float[] mMatrixValue = new float[9];

    private boolean isRequest;
    private boolean isPreview;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            mMatrix = new Matrix(mUVCCameraView.getMatrix());
            int scale = ConfigBean.getInstance().getScale();
            mMatrix.setScale(scale + 1 , scale + 1, mCenterX, mCenterY);
            mCameraHelper.startPreview(mUVCCameraView);
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                isPreview = true;
                showShortMsg("connecting");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

//                        Looper.prepare();
                        // initialize seekbar
                        // need to wait UVCCamera initialize over
                        if(mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                            mCameraHelper.startCameraFoucs();
//                            int brightProcess = mCameraHelper.getModelValue(UVCCameraHelper.MODE_BRIGHTNESS);
//                            int contrastProcess = mCameraHelper.getModelValue(UVCCameraHelper.MODE_CONTRAST);
                            int bright = ConfigBean.getInstance().getBrightness();
                            int contrast = ConfigBean.getInstance().getContrast();
                            mCameraHelper.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS, bright);
                            mCameraHelper.setModelValue(UVCCameraHelper.MODE_CONTRAST, contrast);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    mTvBrightness.setText("" + bright + "%");
//                                    mTvContrast.setText("" +  contrast + "%");
                                    mSeekBrightness.setProgress(bright);
                                    mSeekContrast.setProgress(contrast);
                                    mUVCCameraView.setTransform(mMatrix);
                                    boolean grayShow = ConfigBean.getInstance().isGrayShow();
//                                    mUVCCameraView.changeGray(grayShow);
                                    mUVCCameraView.postInvalidate();
                                    mCbGray.setChecked(grayShow);
//                                    mTvScale.setText("x " + (scale == 5 ? 1 : scale - 5));
                                    mSeekScale.setProgress(scale);
                                    mCenterX = (mUVCCameraView.getRight() + mUVCCameraView.getLeft()) / 2;
                                    mCenterY = (mUVCCameraView.getBottom() + mUVCCameraView.getTop()) /2;
                                    logger.debug("-----mCenterX:{}, mCenterY{}", mCenterX, mCenterY);

                                    int boxWidth =  Utils.dp2px(USBCameraActivity.this, scale + 40);
                                    // 红色矩形的宽度
                                    float width = (mUVCCameraView.getHeight() - boxWidth) * (1.0f * scale / mSeekScale.getMax()) + boxWidth;
                                    mAutoScanView.freash(mCenterX - width / 2, mCenterY - width / 2, mCenterX + width / 2, mCenterY + width / 2);

//                                    int boxWidth =  Utils.dp2px(USBCameraActivity.this, scale + 10);
//                                    mAutoScanView.freash(mCenterX - boxWidth, mCenterY - boxWidth, mCenterX + boxWidth, mCenterY + boxWidth);
//                                    int format = ConfigBean.getInstance().getFormat();
//                                    mRbFormat.check(format == UVCCamera.FRAME_FORMAT_YUYV ? R.id.rb_YUY : R.id.rb_MJPEG);
                                }
                            });

                        }
//                        Looper.loop();
                    }
                }).start();
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("disconnecting");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbcamera);
        ButterKnife.bind(this);
        mTextureView = findViewById(R.id.camera_view);
        mAutoScanView = findViewById(R.id.asv);

        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (UVCCameraTextureView) mTextureView;
        initView();
//        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
//        mCameraHelper.setDefaultFrameFormat(ConfigBean.getInstance().getFormat());
        mCameraHelper.setDefaultFrameFormat(UVCCamera.FRAME_FORMAT_MJPEG);
        mCameraHelper.setDefaultPreviewSize(ConfigBean.getInstance().getWidth(), ConfigBean.getInstance().getHeight());
//        mCameraHelper.setDefaultPreviewSize(1280, 720);
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);

        mCameraHelper.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {
//                Log.d(TAG, "onPreviewResult: "+nv21Yuv.length);
            }
        });

    }

    private void initView() {
        setSupportActionBar(mToolbar);

        mSeekBrightness.setMax(100);
        mSeekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS,progress);
                    mTvBrightness.setText("" + (progress) + "%");
                    ConfigBean.getInstance().setBrightness(progress);

                }
            }
        });
        mSeekContrast.setMax(100);
        mSeekContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_CONTRAST,progress);
                    mTvContrast.setText("" + (progress) + "%");
                    ConfigBean.getInstance().setContrast(progress);
                }
            }
        });

        mSeekScale.setMax(44);
        mSeekScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mCameraHelper != null && mCameraHelper.isCameraOpened() && mMatrix != null) {
                    logger.debug("-----mCenterX:{}, mCenterY{}", mCenterX, mCenterY);
                    int scale = progress + 1;
                    int scaleX = (1 - scale) * mUVCCameraView.getWidth() / (scale * 2);
                    int scaleY = (1 - scale) * mUVCCameraView.getHeight() / (scale * 2);
                    Matrix matrix = new Matrix();
                    matrix.setValues(new float[]{
                            scale , 0, scale * scaleX,
                            0 , scale, scale * scaleY,
                            0 , 0, 1,
                    });
                    mMatrix.reset();
                    mMatrix.set(matrix);
//                    mMatrix.setScale(scale, scale, scaleX, scaleY);
                    mUVCCameraView.setTransform(mMatrix);
                    mUVCCameraView.postInvalidate();
                    String showText = "x " + (progress == 5 ? 1 : progress - 5);
                    mTvScale.setText(showText);
                    ConfigBean.getInstance().setScale(progress);

                    int boxWidth =  Utils.dp2px(USBCameraActivity.this, progress + 40);
                    // 红色矩形的宽度
                    float width = (mUVCCameraView.getHeight() - boxWidth) * (1.0f * progress / seekBar.getMax()) + boxWidth;
                    mAutoScanView.freash(mCenterX - width / 2, mCenterY - width / 2, mCenterX + width / 2, mCenterY + width / 2);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(mCameraHelper != null && mCameraHelper.isCameraOpened() && mMatrix != null) {
//                    logger.debug("-----mCenterX:{}, mCenterY{}", mCenterX, mCenterY);
//                    int scale = progress == 0 ? 1 : progress;
//                    mMatrix.setScale(scale, scale, mCenterX, mCenterY);
//                    mUVCCameraView.setTransform(mMatrix);
//                    mUVCCameraView.postInvalidate();
                    int showProcess = (progress == 5 ? 1 : progress - 5);
//                    String showText = "x " + (progress == 5 ? 1 : progress - 5);
                    TtsSpeaker.getInstance().addMessageFlush((showProcess > 0 ? "放大 " : "缩小") + Math.abs(showProcess) + "倍");
//                    mTvScale.setText(showText);
//                    ConfigBean.getInstance().setScale(progress);
//
//                    int boxWidth =  Utils.dp2px(USBCameraActivity.this, progress + 20);
//                    // 红色矩形的宽度
//                    float width = (mUVCCameraView.getWidth() - boxWidth) * (1.0f * progress / seekBar.getMax()) + boxWidth;
//                    //
//                    mAutoScanView.freash(mCenterX - boxWidth, mCenterY - boxWidth, mCenterX + boxWidth, mCenterY + boxWidth);
                }
            }
        });
//        mSeekScale.setProgress(5);

        // 显示设置控件
        mUVCCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlSetting.setVisibility(mLlSetting.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        // 分辨率选择
        mTvResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                    showShortMsg("sorry,camera open failed");
                }
                showResolutionListDialog();
            }
        });
        // 视频流格式选择
        int format = ConfigBean.getInstance().getFormat();
        mRbFormat.check(format == UVCCamera.FRAME_FORMAT_YUYV ? R.id.rb_YUY : R.id.rb_MJPEG);
        mRbFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) return;
                int format = R.id.rb_YUY == checkedId ? UVCCamera.FRAME_FORMAT_YUYV : UVCCamera.FRAME_FORMAT_MJPEG;
                List<Size> list = mCameraHelper.getSupportedPreviewSizes(format);
                boolean hasSize = false;
                for (Size s : list) {
                    if (s.width == ConfigBean.getInstance().getWidth() && s.height == ConfigBean.getInstance().getHeight()){
                        hasSize = true;
                    }
                }
                if (!hasSize){
                    mCameraHelper.updateFormat(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, format);
                    ConfigBean.getInstance().setWidth(UVCCamera.DEFAULT_PREVIEW_WIDTH);
                    ConfigBean.getInstance().setHeight(UVCCamera.DEFAULT_PREVIEW_HEIGHT);
                }else {
                    mCameraHelper.updateFormat(format);
                }
                ConfigBean.getInstance().setFormat(format);
            }
        });

        mCbGray.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUVCCameraView.changeGray(isChecked);
                ConfigBean.getInstance().setGrayShow(isChecked);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
        runOnUiThread(mFpsTask);
    }
    private final Runnable mFpsTask = new Runnable() {
        @Override
        public void run() {
            float srcFps;
            if (mUVCCameraView != null) {
                mUVCCameraView.updateFps();
                srcFps = mUVCCameraView.getFps();
            } else {
                srcFps = 0.0f;
            }
            String reslut = String.format(Locale.US, "FPS:%4.1f", srcFps);
            Log.d("kim", reslut);
            mTvFps.setText(reslut);
            mTvFps.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    private void showResolutionListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(USBCameraActivity.this);
        View rootView = LayoutInflater.from(USBCameraActivity.this).inflate(R.layout.layout_dialog_list, null);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_dialog);
        // 只读取mjpeg格式
        List<Size> list = mCameraHelper.getSupportedPreviewSizes(UVCCamera.FRAME_FORMAT_MJPEG);
//        List<Size> list2 = mCameraHelper.getSupportedPreviewSizes(UVCCamera.FRAME_FORMAT_YUYV);
//        list.addAll(list2);
        List<String> resolutions = null;
        if (Utils.isEmpty(list)){
            Toast.makeText(this, "该格式下分辨率获取失败", Toast.LENGTH_LONG).show();
            return;
        }
        Map<String, Size> selecs = new HashMap<>();
        if (list != null && list.size() != 0) {
            resolutions = new ArrayList<>();
            for (Size size : list) {
                if (size != null) {
                    if (size.fps.length >= 1){
                        for (int i = 0; i < size.fps.length; i ++){
                            String key = (size.type == 4 ? "YUY2 " : "MJPG ") + size.width + "x" + size.height + "  " + ((int)size.fps[i]) + "fps";
                            resolutions.add(key);
                            selecs.put(key, size);
                        }
                    }
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(USBCameraActivity.this, android.R.layout.simple_list_item_1, resolutions){
            @NonNull
            @Override
            public View getView(int position,  View convertView, @NonNull ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                final String resolution = getItem(position);
                Size size = selecs.get(resolution);
                if (size.width == ConfigBean.getInstance().getWidth()
                        && size.height == ConfigBean.getInstance().getHeight()){
                    view.setBackgroundColor(Color.parseColor("#eaeaea"));
                }else {
                    view.setBackgroundColor(Color.parseColor("#ffffff"));

                }
                return view;
            }
        };
        if (adapter != null) {
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened())
                    return;
                final String resolution = (String) adapterView.getItemAtPosition(position);
                Size size = selecs.get(resolution);
                if (size != null){
                    int widht = Integer.valueOf(size.width);
                    int height = Integer.valueOf(size.height);
                    AbstractUVCCameraHandler.UVCParam param = new AbstractUVCCameraHandler.UVCParam();
                    param.minFrame = size.intervals[0];
                    param.maxFrame = size.intervals[1];
//                    param.minFrame = 30;
//                    param.maxFrame = 30;
                    param.defultFrame = size.intervals[2] > 0 ? 1000 * 10000 / size.intervals[2] : 0;
                    param.frameInterval = size.intervals[2];
                    param.format = resolution.contains("YUY2") ? UVCCamera.FRAME_FORMAT_YUYV : UVCCamera.FRAME_FORMAT_MJPEG;
                    ConfigBean configBean = ConfigBean.getInstance();
                    configBean.setMinFrame(param.minFrame);
                    configBean.setMaxFrame(param.maxFrame);
                    configBean.setDefultFrame(param.defultFrame);
                    configBean.setFrameInterval(param.frameInterval);
                    configBean.setWidth(widht);
                    configBean.setHeight(height);
                    configBean.setFormat(param.format);
                    configBean.save();
                    mCameraHelper.updateResolution(widht, height, param);

                }
//                String[] tmp = resolution.split("x");
//                if (tmp != null && tmp.length >= 2) {
//                    int widht = Integer.valueOf(tmp[0]);
//                    int height = Integer.valueOf(tmp[1]);
//                    mCameraHelper.updateResolution(widht, height);
//                }
                mDialog.dismiss();
                mDialog.cancel();
            }
        });

        builder.setView(rootView);
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.releaseFile();
        // step.4 release uvc camera resources
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            showShortMsg("取消操作");
        }
    }

    public boolean isCameraOpened() {
        return mCameraHelper.isCameraOpened();
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }
}
