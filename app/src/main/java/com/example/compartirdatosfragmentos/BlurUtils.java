package com.example.compartirdatosfragmentos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Utilidades para difuminar (blur) imágenes de manera eficiente.
 * Usa un algoritmo de Stack Blur en Java puro — no depende de RenderScript
 * ni de ninguna librería externa.
 * Se aplica un down-scale previo para mejorar el rendimiento y la intensidad del blur.
 */
public class BlurUtils {

    private BlurUtils() { }

    /**
     * Genera un bitmap difuminado a partir de un resource ID.
     *
     * @param context   contexto de la app
     * @param resId     ID del recurso drawable (ej. R.drawable.imagen1)
     * @param radius    radio del blur (>= 1)
     * @return          bitmap difuminado, o null si resId == 0
     */
    public static Bitmap blurFromResource(Context context, int resId, float radius) {
        if (resId == 0) return null;

        // Decodificar la imagen con reducción moderada para conservar formas y nitidez
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; // escala 1/2 para mantener detalles reconocibles
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (original == null) return null;

        // Escalar a un ancho óptimo (~400px) para que el monumento o paisaje sea visible
        int targetWidth = Math.min(400, original.getWidth());
        int targetHeight = Math.max((original.getHeight() * targetWidth) / original.getWidth(), 1);
        Bitmap scaledBitmap;
        if (targetWidth != original.getWidth() || targetHeight != original.getHeight()) {
            scaledBitmap = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, true);
            original.recycle();
        } else {
            scaledBitmap = original;
        }

        // Aplicar Stack Blur en una sola pasada con radio suave
        int blurRadius = (int) Math.max(1, Math.min(radius, 25));
        return stackBlur(scaledBitmap, blurRadius);
    }

    /**
     * Implementación del algoritmo Stack Blur en Java puro.
     * Basado en el algoritmo de Mario Klingemann.
     *
     * @param bitmap bitmap de entrada (se copia internamente si no es mutable)
     * @param radius radio del blur (>= 1)
     * @return       bitmap difuminado
     */
    private static Bitmap stackBlur(Bitmap bitmap, int radius) {
        if (radius < 1) return bitmap;

        Bitmap result;
        if (bitmap.isMutable() && bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
            result = bitmap;
        } else {
            result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bitmap.recycle();
        }
        int w = result.getWidth();
        int h = result.getHeight();

        int[] pixels = new int[w * h];
        result.getPixels(pixels, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pixels[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pixels[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pixels[yi] = (0xff000000 & pixels[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        result.setPixels(pixels, 0, w, 0, 0, w, h);
        return result;
    }
}
