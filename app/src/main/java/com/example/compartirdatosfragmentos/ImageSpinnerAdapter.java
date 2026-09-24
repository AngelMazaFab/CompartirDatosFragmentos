package com.example.compartirdatosfragmentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Adapter personalizado para mostrar imágenes y texto en un Spinner.
 */
public class ImageSpinnerAdapter extends ArrayAdapter<ImageSpinnerAdapter.ImageItem> {

    private final Context context;
    private final List<ImageItem> items;

    public ImageSpinnerAdapter(Context context, List<ImageItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent, true);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent, false);
    }

    private View createItemView(int position, View convertView, ViewGroup parent, boolean isCollapsed) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spinner_image, parent, false);
        }

        ImageItem item = items.get(position);

        ImageView imageView = view.findViewById(R.id.ivSpinnerItem);
        TextView textView = view.findViewById(R.id.tvSpinnerItem);

        if (item.imageResId != 0) {
            imageView.setImageResource(item.imageResId);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        textView.setText(item.label);

        return view;
    }

    /**
     * Modelo de datos para cada ítem del Spinner: imagen + etiqueta.
     */
    public static class ImageItem {
        public final int imageResId;  // 0 si es el ítem "hint" (sin imagen)
        public final String label;

        public ImageItem(int imageResId, String label) {
            this.imageResId = imageResId;
            this.label = label;
        }
    }
}
