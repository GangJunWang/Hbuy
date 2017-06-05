/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.Timer;
import java.util.TimerTask;

import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.view.draw.DrawAttribute;
import au.com.hbuy.aotong.view.draw.DrawingBoardView;
import au.com.hbuy.aotong.view.draw.FileUtil;
import au.com.hbuy.aotong.view.draw.OperateUtils;
import au.com.hbuy.aotong.view.draw.ScrawlTools;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.assist.FailReason;
import io.rong.imageloader.core.listener.SimpleImageLoadingListener;

/**
 * 涂鸦
 * @author jarlen
 */
public class DrawImgActivity extends BaseActivity implements View.OnClickListener {

    private DrawingBoardView drawView;

    ScrawlTools casualWaterUtil = null;
    private LinearLayout drawLayout;
    String mPath;
    private TextView cancelBtn, okBtn;
    private String mRes;
    private ImageView iv;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_img_edit);

        drawView = (DrawingBoardView) findViewById(R.id.drawView);
        drawLayout = (LinearLayout) findViewById(R.id.drawLayout);

        cancelBtn = (TextView) findViewById(R.id.tv_cancel);
        cancelBtn.setOnClickListener(this);

        iv = (ImageView) findViewById(R.id.iv);

        okBtn = (TextView) findViewById(R.id.tv_save);
        okBtn.setOnClickListener(this);

        Intent intent = getIntent();

        mRes = intent.getStringExtra("url");
        getBitmap(mRes);
    }

    private void getBitmap(String url) {
        if (null != url && !"".equals(url))
   /*         Picasso.with(mContext)
                    .load(mImageUrl)
                //    .placeholder(R.drawable.hbuy_img)
//                    .error(R.drawable.hbuy_img)
                    // .transform(new CropSquareTransformation())
                    .tag(this)
                    .into(mImageView);
        mAttacher.update();*/
            ImageLoader.getInstance().displayImage(url, iv,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        //    mProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "下载错误";
                                    break;
                                case DECODING_ERROR:
                                    message = "图片无法显示";
                                    break;
                                case NETWORK_DENIED:
                                    message = "网络有问题，无法下载";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "图片太大无法显示";
                                    break;
                                case UNKNOWN:
                                    message = "未知的错误";
                                    break;
                            }
                        //    ShowToastUtils.toast(mContext, message);
                         //   mProgressBar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onLoadingComplete(String imageUri, View view,Bitmap loadedImage) {
                       //     mBitmap = loadedImage;
                       //     mProgressBar.setVisibility(View.GONE);
                      //      mAttacher.update();
                            KLog.d(loadedImage + "--imageUri =" + imageUri);
                            mBitmap = loadedImage;
                             timer.schedule(task, 10, 1000);
                        }
                    });
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (drawLayout.getWidth() != 0) {
                    // Log.i("jarlen", drawLayout.getWidth() + "");
                    // Log.i("jarlen", drawLayout.getHeight() + "");
                    // 取消定时器
                    timer.cancel();
                    compressed();
                }
            }
        }
    };

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }
    };

    private void compressed() {

        OperateUtils operateUtils = new OperateUtils(this);

        // Bitmap bit = BitmapFactory.decodeResource(this.getResources(),
        // R.drawable.river);
     //   Bitmap bit = BitmapFactory.decodeFile(mPath);

        Bitmap resizeBmp = operateUtils.compressionFiller(mBitmap, drawLayout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                resizeBmp.getWidth(), resizeBmp.getHeight());

        drawView.setLayoutParams(layoutParams);

        casualWaterUtil = new ScrawlTools(this, drawView, resizeBmp);

        Bitmap paintBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.crayon);

        // int[] res = new int[]{
        // R.drawable.stamp0star,R.drawable.stamp1star,R.drawable.stamp2star,R.drawable.stamp3star
        // };

        casualWaterUtil.creatDrawPainter(DrawAttribute.DrawStatus.PEN_WATER,
                paintBitmap, 0xffadb8bd);

        // casualWaterUtil.creatStampPainter(DrawAttribute.DrawStatus.PEN_STAMP,res,0xff00ff00);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_save:
                Bitmap bit = casualWaterUtil.getBitmap();
                String tempPhotoPath = FileUtil.DCIMCamera_PATH + FileUtil.getNewFileName()
                        + ".jpg";
                if (FileUtil.writeImage(bit, tempPhotoPath, 100)) {
                    ShowToastUtils.toast(this, "图片保存在:" + FileUtil.DCIMCamera_PATH, 3);

                    // 延迟进入
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            finish();
                        }
                    }, 1000);
                } else {
                    ShowToastUtils.toast(this, "图片保存失败", 2);
                }
                break;
        }
    }
}
