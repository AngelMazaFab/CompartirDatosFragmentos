package com.example.compartirdatosfragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentA extends Fragment {

    private EditText etNombre, etEdad, etCorreo, etCalificacion;
    private CheckBox cbSuscrito;
    private TextView tvRespuesta;

    public FragmentA() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);

        // Referencias a los campos de entrada
        etNombre = view.findViewById(R.id.etNombre);
        etEdad = view.findViewById(R.id.etEdad);
        etCorreo = view.findViewById(R.id.etCorreo);
        etCalificacion = view.findViewById(R.id.etCalificacion);
        cbSuscrito = view.findViewById(R.id.cbSuscrito);
        tvRespuesta = view.findViewById(R.id.tvRespuesta);
        Button btnNext = view.findViewById(R.id.btnNext);

        // Paso 8: Restaurar datos al rotar el dispositivo
        if (savedInstanceState != null) {
            etNombre.setText(savedInstanceState.getString("nombre", ""));
            etEdad.setText(savedInstanceState.getString("edad", ""));
            etCorreo.setText(savedInstanceState.getString("correo", ""));
            etCalificacion.setText(savedInstanceState.getString("calificacion", ""));
            cbSuscrito.setChecked(savedInstanceState.getBoolean("suscrito", false));
        }

        // Paso 6: Registrar listener para recibir la respuesta de FragmentB
        getParentFragmentManager().setFragmentResultListener("requestKeyRespuesta", this, (key, bundle) -> {
            String respuesta = bundle.getString("respuesta");
            tvRespuesta.setText("Respuesta recibida: " + respuesta);
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Paso 2: Leer y validar los campos
                String nombre = etNombre.getText().toString().trim();
                String edadStr = etEdad.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String calificacionStr = etCalificacion.getText().toString().trim();

                // Validar que ningún campo esté vacío
                if (nombre.isEmpty() || edadStr.isEmpty() || correo.isEmpty() || calificacionStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que la edad sea un número válido
                int edad;
                try {
                    edad = Integer.parseInt(edadStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Edad no es un número válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que la calificación sea un número decimal válido
                double calificacion;
                try {
                    calificacion = Double.parseDouble(calificacionStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Calificación no es un número válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean suscrito = cbSuscrito.isChecked();

                // Paso 3: Construir el Bundle con múltiples tipos de datos
                FragmentB fragmentB = new FragmentB();
                Bundle args = new Bundle();
                args.putString("nombre", nombre);
                args.putInt("edad", edad);
                args.putString("correo", correo);
                args.putDouble("calificacion", calificacion);
                args.putBoolean("suscrito", suscrito);
                fragmentB.setArguments(args);

                // Reemplazar FragmentA con FragmentB
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                // Paso 7: Animación entre Fragments
                transaction.setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.fragment_container, fragmentB);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    // Paso 8: Guardar datos al rotar el dispositivo
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etNombre != null) {
            outState.putString("nombre", etNombre.getText().toString());
            outState.putString("edad", etEdad.getText().toString());
            outState.putString("correo", etCorreo.getText().toString());
            outState.putString("calificacion", etCalificacion.getText().toString());
            outState.putBoolean("suscrito", cbSuscrito.isChecked());
        }
    }
}
