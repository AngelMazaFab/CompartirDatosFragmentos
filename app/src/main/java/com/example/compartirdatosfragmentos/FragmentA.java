package com.example.compartirdatosfragmentos;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class FragmentA extends Fragment {

    private EditText etNombre, etEdad, etCorreo, etEstatura;
    private CheckBox cbSuscrito;
    private TextView tvRespuesta;
    private Spinner spinnerPais;
    private ImageView ivAutoSeleccionado;
    private ImageView ivHeaderBlur;
    private View viewHeaderOverlay;
    private TextView tvAutoLabel;
    private android.view.View cardAutoContainer;

    // Índice del país seleccionado (0 = hint)
    private int selectedPaisIndex = 0;

    // ── Datos del viaje recibidos de Fragment B (para reenviarlos al crear un nuevo Fragment B) ──
    private String savedCiudad        = "";
    private String savedDias          = "";
    private String savedHotel         = "";
    private boolean savedOfertas      = false;
    private int savedAutoIndex        = 0;
    private int savedAutoImageResId   = 0;

    public FragmentA() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);

        // Referencias a los campos de entrada
        etNombre           = view.findViewById(R.id.etNombre);
        etEdad             = view.findViewById(R.id.etEdad);
        etCorreo           = view.findViewById(R.id.etCorreo);
        etEstatura         = view.findViewById(R.id.etEstatura);
        cbSuscrito         = view.findViewById(R.id.cbSuscrito);
        tvRespuesta        = view.findViewById(R.id.tvRespuesta);
        spinnerPais        = view.findViewById(R.id.spinnerPais);
        ivAutoSeleccionado = view.findViewById(R.id.ivAutoSeleccionado);
        tvAutoLabel        = view.findViewById(R.id.tvAutoLabel);
        cardAutoContainer  = view.findViewById(R.id.cardAutoContainer);
        ivHeaderBlur       = view.findViewById(R.id.ivHeaderBlur);
        viewHeaderOverlay  = view.findViewById(R.id.viewHeaderOverlay);
        Button btnNext     = view.findViewById(R.id.btnNext);

        // Configurar el Spinner de países
        configurarSpinnerPaises();

        // Restaurar datos propios al rotar el dispositivo
        if (savedInstanceState != null) {
            etNombre.setText(savedInstanceState.getString("nombre", ""));
            etEdad.setText(savedInstanceState.getString("edad", ""));
            etCorreo.setText(savedInstanceState.getString("correo", ""));
            etEstatura.setText(savedInstanceState.getString("estatura", ""));
            cbSuscrito.setChecked(savedInstanceState.getBoolean("suscrito", false));
            selectedPaisIndex = savedInstanceState.getInt("paisIndex", 0);
            spinnerPais.setSelection(selectedPaisIndex);

            // Restaurar datos del viaje guardados
            savedCiudad       = savedInstanceState.getString("savedCiudad", "");
            savedDias         = savedInstanceState.getString("savedDias", "");
            savedHotel        = savedInstanceState.getString("savedHotel", "");
            savedOfertas      = savedInstanceState.getBoolean("savedOfertas", false);
            savedAutoIndex    = savedInstanceState.getInt("savedAutoIndex", 0);
            savedAutoImageResId = savedInstanceState.getInt("savedAutoImageResId", 0);

            // Restaurar la vista de respuesta si ya había datos del viaje
            if (!savedCiudad.isEmpty()) {
                String resumen = "Ciudad: " + savedCiudad
                        + "\nDías: " + savedDias
                        + "\nHotel: " + savedHotel
                        + "\nRecibir Ofertas: " + (savedOfertas ? "Sí" : "No");
                tvRespuesta.setText("Información del Viaje:\n" + resumen);
            }
            if (savedAutoImageResId != 0) {
                ivAutoSeleccionado.setImageResource(savedAutoImageResId);
                ivAutoSeleccionado.setVisibility(View.VISIBLE);
                tvAutoLabel.setVisibility(View.VISIBLE);
                if (cardAutoContainer != null) cardAutoContainer.setVisibility(View.VISIBLE);
            }
        }

        // Registrar listener para recibir la información del viaje desde Fragment B
        getParentFragmentManager().setFragmentResultListener("requestKeyRespuesta", this, (key, bundle) -> {
            savedCiudad       = bundle.getString("ciudadDestino", "");
            savedDias         = String.valueOf(bundle.getInt("tiempoEstancia", 0));
            savedHotel        = bundle.getString("nombreHotel", "");
            savedOfertas      = bundle.getBoolean("recibirOfertas", false);
            savedAutoIndex    = bundle.getInt("autoIndex", 0);
            savedAutoImageResId = bundle.getInt("autoImageResId", 0);

            // Mostrar resumen en Fragment A
            String resumen = "Ciudad: " + savedCiudad
                    + "\nDías: " + savedDias
                    + "\nHotel: " + savedHotel
                    + "\nRecibir Ofertas: " + (savedOfertas ? "Sí" : "No");
            tvRespuesta.setText("Información del Viaje:\n" + resumen);

            // Mostrar imagen del auto seleccionado en Fragment B (debajo de la info del viaje)
            if (savedAutoImageResId != 0) {
                ivAutoSeleccionado.setImageResource(savedAutoImageResId);
                ivAutoSeleccionado.setVisibility(View.VISIBLE);
                tvAutoLabel.setVisibility(View.VISIBLE);
                if (cardAutoContainer != null) cardAutoContainer.setVisibility(View.VISIBLE);
            }
        });

        btnNext.setOnClickListener(v -> {
            // Leer y validar los campos
            String nombre       = etNombre.getText().toString().trim();
            String edadStr      = etEdad.getText().toString().trim();
            String correo       = etCorreo.getText().toString().trim();
            String estaturaStr  = etEstatura.getText().toString().trim();

            if (nombre.isEmpty() || edadStr.isEmpty() || correo.isEmpty() || estaturaStr.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que haya seleccionado un país (posición 0 es el hint)
            if (spinnerPais.getSelectedItemPosition() == 0) {
                Toast.makeText(requireContext(), "Selecciona un país para viajar", Toast.LENGTH_SHORT).show();
                return;
            }

            int edad;
            try {
                edad = Integer.parseInt(edadStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Edad no es un número válido", Toast.LENGTH_SHORT).show();
                return;
            }

            double estatura;
            try {
                estatura = Double.parseDouble(estaturaStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Estatura no es un número válido", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean suscrito = cbSuscrito.isChecked();

            // Obtener imagen del país seleccionado
            ImageSpinnerAdapter.ImageItem paisItem =
                    (ImageSpinnerAdapter.ImageItem) spinnerPais.getSelectedItem();
            int paisImageResId = (paisItem != null) ? paisItem.imageResId : 0;

            // Construir Bundle hacia Fragment B, incluyendo los datos del viaje previamente guardados
            // para que los campos se pre-rellenen si el usuario ya había llenado antes Fragment B
            FragmentB fragmentB = new FragmentB();
            Bundle args = new Bundle();
            args.putString("nombre", nombre);
            args.putInt("edad", edad);
            args.putString("correo", correo);
            args.putDouble("estatura", estatura);
            args.putBoolean("suscrito", suscrito);
            args.putInt("paisImageResId", paisImageResId);
            args.putString("paisLabel", paisItem != null ? paisItem.label : "");

            // Reenviar datos del viaje guardados para restaurar los campos de Fragment B
            args.putString("savedCiudad", savedCiudad);
            args.putString("savedDias", savedDias);
            args.putString("savedHotel", savedHotel);
            args.putBoolean("savedOfertas", savedOfertas);
            args.putInt("savedAutoIndex", savedAutoIndex);

            fragmentB.setArguments(args);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.fragment_container, fragmentB);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    /**
     * Configura el Spinner de países con las 4 imágenes (imagen1–imagen4).
     * El ítem en posición 0 es el hint "Seleccione un país para viajar".
     */
    private void configurarSpinnerPaises() {
        List<ImageSpinnerAdapter.ImageItem> items = new ArrayList<>();

        // Ítem 0: hint (sin imagen)
        items.add(new ImageSpinnerAdapter.ImageItem(0, "Seleccione un país para viajar"));

        // Ítems 1–4: países con imágenes (imagen1 – imagen4)
        items.add(new ImageSpinnerAdapter.ImageItem(R.drawable.imagen1, "México"));
        items.add(new ImageSpinnerAdapter.ImageItem(R.drawable.imagen2, "España"));
        items.add(new ImageSpinnerAdapter.ImageItem(R.drawable.imagen3, "China"));
        items.add(new ImageSpinnerAdapter.ImageItem(R.drawable.imagen4, "Italia"));

        ImageSpinnerAdapter adapter = new ImageSpinnerAdapter(requireContext(), items);
        spinnerPais.setAdapter(adapter);
        spinnerPais.setSelection(selectedPaisIndex);

        spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaisIndex = position;
                actualizarHeaderBlur(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPaisIndex = 0;
                actualizarHeaderBlur(0);
            }
        });
    }

    /**
     * Actualiza la imagen difuminada del header según el país seleccionado.
     * Si la posición es 0 (hint), oculta la imagen y restablece el color azul marino original.
     */
    private void actualizarHeaderBlur(int position) {
        if (ivHeaderBlur == null) return;

        if (position == 0) {
            ivHeaderBlur.setVisibility(View.GONE);
            if (viewHeaderOverlay != null) {
                viewHeaderOverlay.setAlpha(1.0f);
            }
            return;
        }

        ImageSpinnerAdapter.ImageItem item =
                (ImageSpinnerAdapter.ImageItem) spinnerPais.getItemAtPosition(position);
        if (item != null && item.imageResId != 0) {
            Bitmap blurred = BlurUtils.blurFromResource(requireContext(), item.imageResId, 8f);
            if (blurred != null) {
                ivHeaderBlur.setImageBitmap(blurred);
                ivHeaderBlur.setVisibility(View.VISIBLE);
                if (viewHeaderOverlay != null) {
                    viewHeaderOverlay.setAlpha(0.40f);
                }
            }
        }
    }

    // Guardar todos los datos al rotar el dispositivo
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etNombre != null) {
            outState.putString("nombre", etNombre.getText().toString());
            outState.putString("edad", etEdad.getText().toString());
            outState.putString("correo", etCorreo.getText().toString());
            outState.putString("estatura", etEstatura.getText().toString());
            outState.putBoolean("suscrito", cbSuscrito.isChecked());
            outState.putInt("paisIndex", selectedPaisIndex);

            // Guardar datos del viaje recibidos de Fragment B
            outState.putString("savedCiudad", savedCiudad);
            outState.putString("savedDias", savedDias);
            outState.putString("savedHotel", savedHotel);
            outState.putBoolean("savedOfertas", savedOfertas);
            outState.putInt("savedAutoIndex", savedAutoIndex);
            outState.putInt("savedAutoImageResId", savedAutoImageResId);
        }
    }
}
