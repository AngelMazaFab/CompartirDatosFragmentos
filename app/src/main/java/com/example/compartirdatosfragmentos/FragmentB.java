package com.example.compartirdatosfragmentos;

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

public class FragmentB extends Fragment {

    // Índice del auto seleccionado en el Spinner
    private int selectedAutoIndex = 0;

    // Campos del viaje (para persistencia ante rotación)
    private EditText etCiudadDestino;
    private EditText etTiempoEstancia;
    private EditText etNombreHotel;
    private CheckBox cbRecibirOfertas;
    private Spinner  spinnerAuto;

    public FragmentB() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);

        // Referencias a los TextViews de la tarjeta (datos de Fragment A)
        TextView tvNombre       = view.findViewById(R.id.tvNombre);
        TextView tvEdad         = view.findViewById(R.id.tvEdad);
        TextView tvCorreo       = view.findViewById(R.id.tvCorreo);
        TextView tvEstatura     = view.findViewById(R.id.tvCalificacion); // mismo id, cambia contenido
        TextView tvSuscrito     = view.findViewById(R.id.tvSuscrito);
        ImageView ivPaisSeleccionado = view.findViewById(R.id.ivPaisSeleccionado);

        // Referencias a los campos de entrada del viaje
        etCiudadDestino  = view.findViewById(R.id.etCiudadDestino);
        etTiempoEstancia = view.findViewById(R.id.etTiempoEstancia);
        etNombreHotel    = view.findViewById(R.id.etNombreHotel);
        cbRecibirOfertas = view.findViewById(R.id.cbRecibirOfertas);
        spinnerAuto      = view.findViewById(R.id.spinnerAuto);

        // Botones
        Button btnEnviarRespuesta = view.findViewById(R.id.btnEnviarRespuesta);
        Button btnRegresar        = view.findViewById(R.id.btnRegresar);
        Button btnIrC             = view.findViewById(R.id.btnIrC);

        // Leer datos del Bundle que vienen de Fragment A
        Bundle args = getArguments();
        String nombre      = "";
        int edad           = 0;
        String correo      = "";
        double estatura    = 0.0;
        boolean suscrito   = false;
        int paisImageResId = 0;
        String paisLabel   = "";

        if (args != null) {
            nombre         = args.getString("nombre", "");
            edad           = args.getInt("edad", 0);
            correo         = args.getString("correo", "");
            estatura       = args.getDouble("estatura", 0.0);
            suscrito       = args.getBoolean("suscrito", false);
            paisImageResId = args.getInt("paisImageResId", 0);
            paisLabel      = args.getString("paisLabel", "");

            // Mostrar datos en la tarjeta
            tvNombre.setText("Nombre: " + nombre);
            tvEdad.setText("Edad: " + edad);
            tvCorreo.setText("Correo: " + correo);
            tvEstatura.setText("Estatura: " + estatura + " m");
            tvSuscrito.setText("Suscrito: " + (suscrito ? "Sí" : "No"));

            // Mostrar imagen del país seleccionado en Fragment A
            if (paisImageResId != 0) {
                ivPaisSeleccionado.setImageResource(paisImageResId);
                ivPaisSeleccionado.setVisibility(View.VISIBLE);
            } else {
                ivPaisSeleccionado.setVisibility(View.GONE);
            }
        }

        // Configurar el Spinner de autos
        configurarSpinnerAutos();

        // ── Pre-rellenar campos con datos guardados provenientes de Fragment A ──
        // Esto se aplica cuando el usuario navega A→B→A→B (Fragment B recrea su instancia)
        if (args != null) {
            String sc = args.getString("savedCiudad", "");
            String sd = args.getString("savedDias", "");
            String sh = args.getString("savedHotel", "");
            boolean so = args.getBoolean("savedOfertas", false);
            int sai    = args.getInt("savedAutoIndex", 0);

            if (!sc.isEmpty()) etCiudadDestino.setText(sc);
            if (!sd.isEmpty()) etTiempoEstancia.setText(sd);
            if (!sh.isEmpty()) etNombreHotel.setText(sh);
            cbRecibirOfertas.setChecked(so);
            if (sai != 0) {
                selectedAutoIndex = sai;
                spinnerAuto.setSelection(sai);
            }
        }

        // ── Restaurar datos del viaje al rotar el dispositivo (tiene precedencia) ──
        if (savedInstanceState != null) {
            etCiudadDestino.setText(savedInstanceState.getString("ciudad", ""));
            etTiempoEstancia.setText(savedInstanceState.getString("dias", ""));
            etNombreHotel.setText(savedInstanceState.getString("hotel", ""));
            cbRecibirOfertas.setChecked(savedInstanceState.getBoolean("ofertas", false));
            selectedAutoIndex = savedInstanceState.getInt("autoIndex", 0);
            spinnerAuto.setSelection(selectedAutoIndex);
        }

        // Variables finales para usar en los listeners
        final String fNombre      = nombre;
        final int fEdad           = edad;
        final String fCorreo      = correo;
        final double fEstatura    = estatura;
        final boolean fSuscrito   = suscrito;
        final int fPaisImageResId = paisImageResId;
        final String fPaisLabel   = paisLabel;

        // Botón: Enviar datos del viaje de vuelta a Fragment A
        btnEnviarRespuesta.setOnClickListener(v -> {
            String ciudad   = etCiudadDestino.getText().toString().trim();
            String diasStr  = etTiempoEstancia.getText().toString().trim();
            String hotel    = etNombreHotel.getText().toString().trim();
            boolean ofertas = cbRecibirOfertas.isChecked();

            if (ciudad.isEmpty() || diasStr.isEmpty() || hotel.isEmpty()) {
                Toast.makeText(requireContext(), "Completa los campos del viaje", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que haya seleccionado un auto (posición 0 es el hint)
            if (spinnerAuto.getSelectedItemPosition() == 0) {
                Toast.makeText(requireContext(), "Selecciona un auto para el viaje", Toast.LENGTH_SHORT).show();
                return;
            }

            int dias;
            try {
                dias = Integer.parseInt(diasStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Los días deben ser un número válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener imagen del auto seleccionado
            int autoPos = spinnerAuto.getSelectedItemPosition();
            ImageSpinnerAdapter.ImageItem autoItem =
                    (ImageSpinnerAdapter.ImageItem) spinnerAuto.getSelectedItem();
            int autoImageResId = (autoItem != null) ? autoItem.imageResId : 0;

            // Enviar resultado a Fragment A (incluye autoIndex para que pueda restaurarlo)
            Bundle result = new Bundle();
            result.putString("ciudadDestino", ciudad);
            result.putInt("tiempoEstancia", dias);
            result.putString("nombreHotel", hotel);
            result.putBoolean("recibirOfertas", ofertas);
            result.putInt("autoImageResId", autoImageResId);
            result.putInt("autoIndex", autoPos);
            getParentFragmentManager().setFragmentResult("requestKeyRespuesta", result);
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Botón: Regresar a Fragment A sin enviar datos
        btnRegresar.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        // Botón: Ir a Fragment C con todos los datos
        btnIrC.setOnClickListener(v -> {
            String ciudad   = etCiudadDestino.getText().toString().trim();
            String diasStr  = etTiempoEstancia.getText().toString().trim();
            String hotel    = etNombreHotel.getText().toString().trim();
            boolean ofertas = cbRecibirOfertas.isChecked();

            // Obtener imagen del auto seleccionado
            int autoImageResId = 0;
            String autoLabel   = "";
            if (spinnerAuto.getSelectedItemPosition() != 0) {
                ImageSpinnerAdapter.ImageItem autoItem =
                        (ImageSpinnerAdapter.ImageItem) spinnerAuto.getSelectedItem();
                if (autoItem != null) {
                    autoImageResId = autoItem.imageResId;
                    autoLabel      = autoItem.label;
                }
            }

            int dias = 0;
            if (!diasStr.isEmpty()) {
                try {
                    dias = Integer.parseInt(diasStr);
                } catch (NumberFormatException e) {
                    dias = 0;
                }
            }

            // Construir Bundle hacia Fragment C
            FragmentC fragmentC = new FragmentC();
            Bundle argsC = new Bundle();
            argsC.putString("nombre", fNombre);
            argsC.putInt("edad", fEdad);
            argsC.putString("correo", fCorreo);
            argsC.putDouble("estatura", fEstatura);
            argsC.putBoolean("suscrito", fSuscrito);
            argsC.putInt("paisImageResId", fPaisImageResId);
            argsC.putString("paisLabel", fPaisLabel);
            argsC.putString("ciudadDestino", ciudad);
            argsC.putInt("tiempoEstancia", dias);
            argsC.putString("nombreHotel", hotel);
            argsC.putBoolean("recibirOfertas", ofertas);
            argsC.putInt("autoImageResId", autoImageResId);
            argsC.putString("autoLabel", autoLabel);
            fragmentC.setArguments(argsC);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.fragment_container, fragmentC);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    /**
     * Configura el Spinner de autos con las 4 imágenes (imagen5–imagen8).
     * El ítem en posición 0 es el hint "Seleccione un auto para su viaje".
     */
    private void configurarSpinnerAutos() {
        List<ImageSpinnerAdapter.ImageItem> items = new ArrayList<>();

        // Ítem 0: hint (sin imagen)
        items.add(new ImageSpinnerAdapter.ImageItem(0, "Seleccione un auto para su viaje"));

        // Ítems 1–4: autos con imágenes (imagen5 – imagen8)
        // Las imágenes deben estar en res/drawable/ con nombre imagen5, imagen6, imagen7, imagen8
        items.add(new ImageSpinnerAdapter.ImageItem(R.drawable.imagen5, "Nissan Sentra 2005"));
        items.add(new ImageSpinnerAdapter.ImageItem(R.drawable.imagen6, "Opel Corsa 2024"));
        items.add(new ImageSpinnerAdapter.ImageItem(R.drawable.imagen7, "Fiat Multipla 1998"));
        items.add(new ImageSpinnerAdapter.ImageItem(R.drawable.imagen8, "Mitsubishi Pajero 2026"));

        ImageSpinnerAdapter adapter = new ImageSpinnerAdapter(requireContext(), items);
        spinnerAuto.setAdapter(adapter);
        spinnerAuto.setSelection(selectedAutoIndex);

        spinnerAuto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAutoIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAutoIndex = 0;
            }
        });
    }

    // ── Persistencia de campos del viaje al rotar el dispositivo ──────────────
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etCiudadDestino != null) {
            outState.putString("ciudad", etCiudadDestino.getText().toString());
            outState.putString("dias", etTiempoEstancia.getText().toString());
            outState.putString("hotel", etNombreHotel.getText().toString());
            outState.putBoolean("ofertas", cbRecibirOfertas.isChecked());
            outState.putInt("autoIndex", selectedAutoIndex);
        }
    }
}
