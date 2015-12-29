package com.example.biezhi.biezhivideo.HelpClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by biezhi on 2015/12/28.
 */

public class BitMapCut {

    private static final String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/BiezhiVideo/Image/"; //图片保存路径;
    Bitmap bitmaByUrl;
    String bitmapUrl = "";

    /**
     * 通过资源ID来转换成Bitmap
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitmapById(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        InputStream inputStream = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    /**
     * 通过文件路径来获取bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap readBitmapByPath(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 从网络获取图片并且保存到本地SD卡
     * @param url
     * @return
     * @throws InterruptedException
     */
    public Bitmap readImageByUrl(String url) throws InterruptedException {
        bitmapUrl = url;
        new Thread(new getImage()).start();
        Thread.sleep(1000);
        return  bitmaByUrl;
    }

    /**
     * 获取网络图片的线程
     */
    protected class getImage implements Runnable {
        @Override
        public void run() {
            try {
                byte data[] = ImageService.getImage("");
                bitmaByUrl = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message message = Message.obtain();
            message.what = 1;
            handler.sendMessage(message);
        }
    }

    /**
     * 将图片保存到本地
     */
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
                //将文件保存到本地
                try {
                    saveFile(bitmaByUrl, "welcome");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    public String saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName + ".jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return ALBUM_PATH+fileName + ".jpg";
    }


    /**
     * 设置背景为圆角
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap removeYuanJiao(Bitmap bitmap, int pixels) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap creBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(creBitmap);
        Paint paint = new Paint();
        float roundPx = pixels;
        RectF rectF = new RectF(0, 0, bitmap.getWidth() - pixels, bitmap.getHeight() - pixels);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return creBitmap;
    }

    /**
     * 裁剪成正方形
     *
     * @param bitmap
     * @param isRecycled
     * @return
     */
    public static Bitmap ImageCrop(Bitmap bitmap, boolean isRecycled) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int finalWidth = width > height ? width : height;
        int retX = width > height ? (width - height) / 2 : 0;
        int retY = width > height ? 0 : (width - height) / 2;
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, finalWidth, finalWidth, null, false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            //return bitmap;
        }
        return bmp;
    }

    /**
     * 按照长方形裁剪
     * @param bitmap
     * @return
     */
    public static Bitmap ImageCropWithRect(Bitmap bitmap) {
        if (bitmap == null) {
            return bitmap;
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int nw, nh, retX, retY;
            if (width > height) {
                nw = height / 2;
                nh = height;
                retX = (width - nw) / 2;
                retY = 0;
            } else {
                nw = width / 2;
                nh = width;
                retX = width / 4;
                retY = (height - width) / 2;
            }
            Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null, false);
            if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())
            {
                bitmap.recycle();
                bitmap = null;
            }
            return bmp;
        }
    }

    /**
     * 将图片转换成byte[]
     * @param bitmap
     * @return
     */
    public static byte[] readBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
        try
        {
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 将图像裁剪成圆形
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height)
        {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else
        {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return output;
    }

    // 将图片变成带圆边的圆形图片
    public static Bitmap getRoundBitmap(Bitmap bitmap, int width, int height)
    {
        if (bitmap == null)
        {
            return null;
        }
        // 将图片变成圆角
        Bitmap roundBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int len = (width > height) ? height : width;
        canvas.drawCircle(width / 2, height / 2, len / 2 - 8, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, len, len, true);
        canvas.drawBitmap(scaledBitmap, 0, 0, paint);
        // 将图片加圆边
        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(outBitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xffffffff);
        canvas.drawCircle(width / 2, height / 2, len / 2 - 4, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(roundBitmap, 0, 0, paint);
        bitmap.recycle();
        bitmap = null;
        roundBitmap.recycle();
        roundBitmap = null;
        scaledBitmap.recycle();
        scaledBitmap = null;
        return outBitmap;
    }

    // 将图片变成带圆边的圆形图片
    public static Bitmap getRoundBitmap(Bitmap bitmap, int width, int height,
                                        int color)
    {
        if (bitmap == null)
        {
            return null;
        }
        // 将图片变成圆角
        Bitmap roundBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int len = (width > height) ? height : width;
        canvas.drawCircle(width / 2, height / 2, len / 2 - 8, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, len, len, true);
        canvas.drawBitmap(scaledBitmap, 0, 0, paint);
        // 将图片加圆边
        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(outBitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas.drawCircle(width / 2, height / 2, len / 2 - 4, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(roundBitmap, 0, 0, paint);
        bitmap.recycle();
        bitmap = null;
        roundBitmap.recycle();
        roundBitmap = null;
        scaledBitmap.recycle();
        scaledBitmap = null;
        return outBitmap;
    }

    /**
     * function:图片转圆角
     *
     * @param bitmap
     *            需要转的bitmap
     * @param pixels
     *            转圆角的弧度
     * @return 转圆角的bitmap
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
        }
        return output;
    }

    /**
     * 获取指定的圆角图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRadiusBitmap(Bitmap bitmap)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xffffffff);
        Bitmap radiusBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(radiusBitmap);
        RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(rectF, 7, 7, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
        }
        return radiusBitmap;
    }

    // 获得指定大小的圆边的bitmap数组
    public static ArrayList<Bitmap> getRadiusBitmapList(String[] pathArray,
                                                        int size, int len, float radius, int color)
    {
        Bitmap canvasBitmap = null;
        Canvas canvas = null;
        Paint paint = null;
        RectF rectF = new RectF(0, 0, len - radius, len - radius);
        File file = null;
        FileInputStream fis = null;
        Bitmap bitmap = null;
        Bitmap scaledBitmap = null;

        ArrayList<Bitmap> list = new ArrayList<Bitmap>();
        for (int i = 0; i < pathArray.length; i++)
        {
            file = new File(pathArray[i]);
            if (!file.exists())
                continue;
            try
            {
                fis = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(fis);
                if (bitmap != null)
                {
                    canvasBitmap = Bitmap.createBitmap(len, len,
                            Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(canvasBitmap);
                    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(color);
                    canvas.drawRoundRect(rectF, radius, radius, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

                    scaledBitmap = Bitmap.createScaledBitmap(bitmap, len, len,
                            true);
                    canvas.drawBitmap(scaledBitmap, 0, 0, paint);
                    list.add(canvasBitmap);
                }
            } catch (FileNotFoundException e)
            {
            } finally
            {
                if (fis != null)
                {
                    try
                    {
                        fis.close();
                    } catch (IOException e1)
                    {
                    }
                }
            }
            if (list.size() == size)
                break;
        }
        if (scaledBitmap != null && !scaledBitmap.isRecycled())
        {
            scaledBitmap.recycle();
            scaledBitmap = null;
        }
        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return list;
    }





    /**
     * 按照一定的宽高比例裁剪图片
     *
     * @param bitmap
     * @param num1
     *            长边的比例
     * @param num2
     *            短边的比例
     * @return
     */
    public static Bitmap ImageCrop(Bitmap bitmap, int num1, int num2,
                                   boolean isRecycled)
    {
        if (bitmap == null)
        {
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int retX, retY;
        int nw, nh;
        if (w > h)
        {
            if (h > w * num2 / num1)
            {
                nw = w;
                nh = w * num2 / num1;
                retX = 0;
                retY = (h - nh) / 2;
            } else
            {
                nw = h * num1 / num2;
                nh = h;
                retX = (w - nw) / 2;
                retY = 0;
            }
        } else
        {
            if (w > h * num2 / num1)
            {
                nh = h;
                nw = h * num2 / num1;
                retY = 0;
                retX = (w - nw) / 2;
            } else
            {
                nh = w * num1 / num2;
                nw = w;
                retY = (h - nh) / 2;
                retX = 0;
            }
        }
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
                false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp)
                && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }


}
