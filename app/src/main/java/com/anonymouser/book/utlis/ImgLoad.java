package com.anonymouser.book.utlis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import com.anonymouser.book.BookApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;

/**
 * 图片加载
 * <p>
 * Created by YandZD.
 */

public class ImgLoad {

    private static final Context mContext = BookApp.mContext;

    private static final RequestOptions options = new RequestOptions()
            .dontAnimate().fitCenter();
//            .placeholder(R.drawable.meinv);


    public static void baseLoadImg(String imgUrl, ImageView imageview) {
        Glide.with(mContext).load(imgUrl).apply(options).into(imageview);
    }

//    public static void baseLoadImgShadown(String imgUrl, final ShadowImageView imageview) {
//        Glide.with(mContext).load(imgUrl).apply(options).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                imageview.setImageDrawable(resource);
//            }
//        });
//    }

    //加载成圆形
    public static void toImgViewRound(String imgUrl, ImageView imageview) {
        Glide.with(mContext).load(imgUrl).apply(options).into(imageview);
    }

    //加载成圆形 资源id
    public static void toImgViewRoundById(int id, ImageView imageview) {
        RequestOptions options = new RequestOptions()
                .dontAnimate().fitCenter().transform(new GlideCircleTransform(mContext));

        Glide.with(mContext).load(id).apply(options).into(imageview);
    }


    public static class GlideCircleTransform extends BitmapTransformation {
        public GlideCircleTransform(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update(getClass().getName().getBytes());
        }
    }
}
