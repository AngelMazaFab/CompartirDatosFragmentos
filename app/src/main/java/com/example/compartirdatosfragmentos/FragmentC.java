package com.example.compartirdatosfragmentos;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FragmentC extends Fragment {

    private File pdfCacheFile;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public FragmentC() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        descargarPdf();
                    } else {
                        Toast.makeText(requireContext(),
                                "Permiso denegado: no se puede guardar el PDF",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, container, false);

        ImageView ivPdfPage    = view.findViewById(R.id.ivPdfPage);
        Button btnDescargarPdf = view.findViewById(R.id.btnDescargarPdf);
        Button btnRegresarA    = view.findViewById(R.id.btnRegresarAC);
        Button btnRegresarB    = view.findViewById(R.id.btnRegresarBC);

        // Leer datos del Bundle
        Bundle args = getArguments();
        String nombre        = "";
        int edad             = 0;
        String correo        = "";
        double estatura      = 0.0;
        boolean suscrito     = false;
        int paisImageResId   = 0;
        String paisLabel     = "";
        String ciudadDestino = "";
        int tiempoEstancia   = 0;
        String nombreHotel   = "";
        boolean recibirOfertas = false;
        int autoImageResId   = 0;
        String autoLabel     = "";

        if (args != null) {
            nombre          = args.getString("nombre", "");
            edad            = args.getInt("edad", 0);
            correo          = args.getString("correo", "");
            estatura        = args.getDouble("estatura", 0.0);
            suscrito        = args.getBoolean("suscrito", false);
            paisImageResId  = args.getInt("paisImageResId", 0);
            paisLabel       = args.getString("paisLabel", "");
            ciudadDestino   = args.getString("ciudadDestino", "");
            tiempoEstancia  = args.getInt("tiempoEstancia", 0);
            nombreHotel     = args.getString("nombreHotel", "");
            recibirOfertas  = args.getBoolean("recibirOfertas", false);
            autoImageResId  = args.getInt("autoImageResId", 0);
            autoLabel       = args.getString("autoLabel", "");
        }

        // Generar el PDF en caché y obtener un bitmap de vista previa de alta calidad
        pdfCacheFile = new File(requireContext().getCacheDir(), "resumen_viaje.pdf");

        Bitmap previewBitmap = generarPdf(
                pdfCacheFile,
                nombre, edad, correo, estatura, suscrito,
                paisImageResId, paisLabel,
                ciudadDestino, tiempoEstancia, nombreHotel, recibirOfertas,
                autoImageResId, autoLabel
        );

        if (previewBitmap != null) {
            ivPdfPage.setImageBitmap(previewBitmap);
        } else {
            Toast.makeText(requireContext(), "Error al generar el PDF", Toast.LENGTH_SHORT).show();
        }

        btnDescargarPdf.setOnClickListener(v -> solicitarPermisoYDescargar());

        // Regresar a Fragment A (dos pops)
        btnRegresarA.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Regresar a Fragment B (un pop)
        btnRegresarB.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DESCARGA DEL PDF
    // ─────────────────────────────────────────────────────────────────────────

    private void solicitarPermisoYDescargar() {
        if (pdfCacheFile == null || !pdfCacheFile.exists()) {
            Toast.makeText(requireContext(), "Primero genera el PDF", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            descargarPdf();
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                descargarPdf();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void descargarPdf() {
        String fileName = "resumen_viaje.pdf";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = requireContext().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.IS_PENDING, 1);
                Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri fileUri = resolver.insert(collection, values);
                if (fileUri != null) {
                    try (OutputStream out = resolver.openOutputStream(fileUri);
                         java.io.InputStream in = new java.io.FileInputStream(pdfCacheFile)) {
                        byte[] buf = new byte[4096];
                        int len;
                        while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
                    }
                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    resolver.update(fileUri, values, null, null);
                    Toast.makeText(requireContext(), "PDF guardado en Descargas: " + fileName, Toast.LENGTH_LONG).show();
                }
            } else {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File destFile = new File(downloadsDir, fileName);
                try (java.io.InputStream in = new java.io.FileInputStream(pdfCacheFile);
                     java.io.OutputStream out = new java.io.FileOutputStream(destFile)) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
                }
                Toast.makeText(requireContext(), "PDF guardado en: " + destFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al guardar el PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GENERACIÓN DEL PDF
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Estrategia de dos pasos:
     * 1. Generar el PDF estándar A4 (595×842 pts) para guardar/descargar.
     * 2. Generar un bitmap de alta resolución (3× DPI) dibujando directamente
     *    sobre un Canvas grande — así las imágenes se ven nítidas en pantalla.
     *
     * Ambos pasos usan exactamente la misma lógica de layout, solo cambia la escala.
     */
    private Bitmap generarPdf(
            File destFile,
            String nombre, int edad, String correo, double estatura, boolean suscrito,
            int paisImageResId, String paisLabel,
            String ciudadDestino, int tiempoEstancia, String nombreHotel, boolean recibirOfertas,
            int autoImageResId, String autoLabel) {

        // Obtener los bitmaps de las imágenes antes de escalarlas
        Bitmap paisBitmap = obtenerBitmapDeRecurso(paisImageResId);
        Bitmap autoBitmap = obtenerBitmapDeRecurso(autoImageResId);

        // ── Paso 1: Generar y guardar el PDF estándar A4 ────────────────────
        // Ancho disponible del PDF en puntos
        final int PDF_W = 595;
        final int PDF_H = 842;
        final int MARGIN = 40;
        final int CONTENT_W = PDF_W - 2 * MARGIN;   // 515 puntos útiles

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PDF_W, PDF_H, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        dibujarContenido(page.getCanvas(), PDF_W, MARGIN, CONTENT_W, 1f,
                nombre, edad, correo, estatura, suscrito,
                paisLabel, paisBitmap,
                ciudadDestino, tiempoEstancia, nombreHotel, recibirOfertas,
                autoLabel, autoBitmap);
        pdfDocument.finishPage(page);

        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            pdfDocument.writeTo(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();

        // ── Paso 2: Generar el bitmap de vista previa a 3× DPI ───────────────
        // 595 × 3 = 1785 px ancho, 842 × 3 = 2526 px alto
        final float SCALE = 3f;
        final int BMP_W = (int) (PDF_W * SCALE);
        final int BMP_H = (int) (PDF_H * SCALE);
        final int BMP_MARGIN = (int) (MARGIN * SCALE);
        final int BMP_CONTENT_W = (int) (CONTENT_W * SCALE);

        Bitmap previewBitmap = Bitmap.createBitmap(BMP_W, BMP_H, Bitmap.Config.ARGB_8888);
        previewBitmap.eraseColor(Color.WHITE);
        Canvas bmpCanvas = new Canvas(previewBitmap);

        dibujarContenido(bmpCanvas, BMP_W, BMP_MARGIN, BMP_CONTENT_W, SCALE,
                nombre, edad, correo, estatura, suscrito,
                paisLabel, paisBitmap,
                ciudadDestino, tiempoEstancia, nombreHotel, recibirOfertas,
                autoLabel, autoBitmap);

        return previewBitmap;
    }

    /**
     * Dibuja todo el contenido del documento sobre el canvas dado.
     *
     * @param canvas      Canvas sobre el que dibujar (puede ser el del PDF o un Bitmap)
     * @param pageWidth   Ancho total del canvas en píxeles/puntos
     * @param margin      Margen lateral
     * @param contentW    Ancho útil (pageWidth - 2*margin)
     * @param scale       Factor de escala respecto al tamaño A4 base (1f = 72 DPI, 3f = 216 DPI)
     */
    private void dibujarContenido(
            Canvas canvas, int pageWidth, int margin, int contentW, float scale,
            String nombre, int edad, String correo, double estatura, boolean suscrito,
            String paisLabel, Bitmap paisBitmap,
            String ciudadDestino, int tiempoEstancia, String nombreHotel, boolean recibirOfertas,
            String autoLabel, Bitmap autoBitmap) {

        // Fondo blanco
        canvas.drawColor(Color.WHITE);

        // Paints escalados
        Paint paintTitle   = makePaint("#1A237E", 22f * scale, Typeface.BOLD);
        Paint paintSection = makePaint("#283593", 14f * scale, Typeface.BOLD);
        Paint paintBody    = makePaint("#424242", 12f * scale, Typeface.NORMAL);
        Paint paintFooter  = makePaint("#9E9E9E",  9f * scale, Typeface.NORMAL);

        Paint paintLine = new Paint();
        paintLine.setColor(Color.parseColor("#BDBDBD"));
        paintLine.setStrokeWidth(0.8f * scale);
        paintLine.setAntiAlias(true);

        // lineH = espacio entre líneas de texto (proporcional al textSize)
        float lineH = 18f * scale;
        int pageH = (int) (842f * scale);

        float y = 52f * scale;

        // ── Título ──────────────────────────────────────────────────────────
        canvas.drawText("Resumen del Viaje", margin, y, paintTitle);
        y += 10f * scale;
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine);
        y += 22f * scale;

        // ── DATOS PERSONALES ─────────────────────────────────────────────────
        canvas.drawText("DATOS PERSONALES", margin, y, paintSection);
        y += 5f * scale;
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine);
        y += lineH;

        canvas.drawText("Nombre:           " + nombre,          margin, y, paintBody); y += lineH;
        canvas.drawText("Edad:             " + edad,            margin, y, paintBody); y += lineH;
        canvas.drawText("Correo:           " + correo,          margin, y, paintBody); y += lineH;
        canvas.drawText("Estatura:         " + estatura + " m", margin, y, paintBody); y += lineH;
        canvas.drawText("Suscrito:         " + (suscrito ? "Sí" : "No"), margin, y, paintBody); y += lineH;
        canvas.drawText("País seleccionado: " + paisLabel,       margin, y, paintBody); y += lineH;

        // Imagen del país — respetando su resolución original, ajustada al ancho disponible
        if (paisBitmap != null) {
            y += 4f * scale;
            // Calcular altura proporcional para que ocupe el ancho útil completo
            float imgW = contentW;
            float imgH = imgW * paisBitmap.getHeight() / (float) paisBitmap.getWidth();
            // Limitar la altura máxima a ~200 puntos * scale para no ocupar demasiado
            float maxH = 200f * scale;
            if (imgH > maxH) {
                imgH = maxH;
                imgW = imgH * paisBitmap.getWidth() / (float) paisBitmap.getHeight();
            }
            // Escalar suavemente con filtro de alta calidad
            Bitmap scaled = Bitmap.createScaledBitmap(paisBitmap, (int) imgW, (int) imgH, true);
            canvas.drawBitmap(scaled, margin, y, null);
            y += imgH + 16f * scale;
        } else {
            y += 6f * scale;
        }

        // ── Separador ────────────────────────────────────────────────────────
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine);
        y += 14f * scale;

        // ── DETALLES DEL VIAJE ────────────────────────────────────────────────
        canvas.drawText("DETALLES DEL VIAJE", margin, y, paintSection);
        y += 5f * scale;
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine);
        y += lineH;

        canvas.drawText("Ciudad Destino:   " + ciudadDestino,                   margin, y, paintBody); y += lineH;
        canvas.drawText("Días de estancia: " + tiempoEstancia,                  margin, y, paintBody); y += lineH;
        canvas.drawText("Hotel:            " + nombreHotel,                     margin, y, paintBody); y += lineH;
        canvas.drawText("Recibir Ofertas:  " + (recibirOfertas ? "Sí" : "No"),  margin, y, paintBody); y += lineH;
        canvas.drawText("Auto seleccionado: " + autoLabel,                      margin, y, paintBody); y += lineH;

        // Imagen del auto — mismo tratamiento que la imagen del país
        if (autoBitmap != null) {
            y += 4f * scale;
            float imgW = contentW;
            float imgH = imgW * autoBitmap.getHeight() / (float) autoBitmap.getWidth();
            float maxH = 200f * scale;
            if (imgH > maxH) {
                imgH = maxH;
                imgW = imgH * autoBitmap.getWidth() / (float) autoBitmap.getHeight();
            }
            Bitmap scaled = Bitmap.createScaledBitmap(autoBitmap, (int) imgW, (int) imgH, true);
            canvas.drawBitmap(scaled, margin, y, null);
            y += imgH + 16f * scale;
        } else {
            y += 6f * scale;
        }

        // ── Pie de página ─────────────────────────────────────────────────────
        float footerY = pageH - 28f * scale;
        canvas.drawLine(margin, footerY, pageWidth - margin, footerY, paintLine);
        footerY += 14f * scale;
        canvas.drawText("In God We Trust", margin, footerY, paintFooter);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Obtiene un Bitmap a partir de un resource ID.
     * - Para .jpg/.png usa BitmapFactory.decodeResource (máxima resolución).
     * - Para otros drawables (shapes XML) dibuja el Drawable sobre un Canvas.
     * Devuelve null si resId == 0 o falla.
     */
    private Bitmap obtenerBitmapDeRecurso(int resId) {
        if (resId == 0) return null;
        try {
            // Intentar decodificar como bitmap nativo (jpg, png) — máxima calidad
            Bitmap bmp = BitmapFactory.decodeResource(requireContext().getResources(), resId);
            if (bmp != null) return bmp;

            // Fallback para drawables vectoriales / shapes
            Drawable drawable = ContextCompat.getDrawable(requireContext(), resId);
            if (drawable == null) return null;

            if (drawable instanceof BitmapDrawable) {
                Bitmap b = ((BitmapDrawable) drawable).getBitmap();
                if (b != null && !b.isRecycled()) return b;
            }

            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            if (w <= 0) w = 400;
            if (h <= 0) h = 250;

            Bitmap fallback = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(fallback);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(c);
            return fallback;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Crea un Paint de texto con los parámetros dados. */
    private Paint makePaint(String hexColor, float textSize, int typefaceStyle) {
        Paint p = new Paint();
        p.setColor(Color.parseColor(hexColor));
        p.setTextSize(textSize);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, typefaceStyle));
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        return p;
    }
}
