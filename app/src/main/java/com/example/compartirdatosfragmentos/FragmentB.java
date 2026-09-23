package com.example.compartirdatosfragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentB extends Fragment {

    private EditText etRespuesta;
    private EditText etCarrera, etCiudad, etMatricula;

    public FragmentB() {
        // Constructor público requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);

        // Referencias a vistas que muestran los datos de A
        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvEdad = view.findViewById(R.id.tvEdad);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvCalificacion = view.findViewById(R.id.tvCalificacion);
        TextView tvSuscrito = view.findViewById(R.id.tvSuscrito);

        // Referencias a los campos de respuesta y nuevos datos
        etRespuesta = view.findViewById(R.id.etRespuesta);
        Button btnEnviarRespuesta = view.findViewById(R.id.btnEnviarRespuesta);

        etCarrera = view.findViewById(R.id.etCarrera);
        etCiudad = view.findViewById(R.id.etCiudad);
        etMatricula = view.findViewById(R.id.etMatricula);
        Button btnIrC = view.findViewById(R.id.btnIrC);

        Button btnRegresar = view.findViewById(R.id.btnRegresar);

        // Restaurar estado si hubo rotación
        if (savedInstanceState != null) {
            etRespuesta.setText(savedInstanceState.getString("respuesta_input", ""));
            etCarrera.setText(savedInstanceState.getString("carrera_input", ""));
            etCiudad.setText(savedInstanceState.getString("ciudad_input", ""));
            etMatricula.setText(savedInstanceState.getString("matricula_input", ""));
        }

        // Lectura tipada de los argumentos recibidos desde FragmentA
        Bundle args = getArguments();
        String nombre = "";
        int edad = 0;
        String correo = "";
        double calificacion = 0.0;
        boolean suscrito = false;

        if (args != null) {
            nombre = args.getString("nombre", "");
            edad = args.getInt("edad", 0);
            correo = args.getString("correo", "");
            calificacion = args.getDouble("calificacion", 0.0);
            suscrito = args.getBoolean("suscrito", false);

            tvNombre.setText("👤 Nombre: " + nombre);
            tvEdad.setText("🎂 Edad: " + edad + " años");
            tvCorreo.setText("✉️ Correo: " + correo);
            tvCalificacion.setText("⭐ Calificación: " + calificacion);
            tvSuscrito.setText("🔔 Suscrito a novedades: " + (suscrito ? "Sí" : "No"));
        }

        final String fNombre = nombre;
        final int fEdad = edad;
        final String fCorreo = correo;
        final double fCalificacion = calificacion;
        final boolean fSuscrito = suscrito;

        // 1. Enviar respuesta de vuelta a FragmentA (FragmentResult API)
        btnEnviarRespuesta.setOnClickListener(v -> {
            String respuesta = etRespuesta.getText().toString().trim();
            if (respuesta.isEmpty()) {
                Toast.makeText(requireContext(), "Escribe una respuesta para enviar a A", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle result = new Bundle();
            result.putString("respuesta", respuesta);
            getParentFragmentManager().setFragmentResult("requestKeyRespuesta", result);

            Toast.makeText(requireContext(), "Respuesta enviada a Fragment A", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 2. Enviar datos acumulados (de A y de B) a FragmentC
        btnIrC.setOnClickListener(v -> {
            String carrera = etCarrera.getText().toString().trim();
            String ciudad = etCiudad.getText().toString().trim();
            String matricula = etMatricula.getText().toString().trim();
            String respuesta = etRespuesta.getText().toString().trim();

            if (carrera.isEmpty() || ciudad.isEmpty() || matricula.isEmpty()) {
                Toast.makeText(requireContext(), "Completa los campos de Fragment B para continuar a C", Toast.LENGTH_SHORT).show();
                return;
            }

            FragmentC fragmentC = new FragmentC();
            Bundle argsC = new Bundle();

            // Datos provenientes de Fragment A
            argsC.putString("nombre", fNombre);
            argsC.putInt("edad", fEdad);
            argsC.putString("correo", fCorreo);
            argsC.putDouble("calificacion", fCalificacion);
            argsC.putBoolean("suscrito", fSuscrito);

            // Nuevos datos capturados en Fragment B
            argsC.putString("carrera", carrera);
            argsC.putString("ciudad", ciudad);
            argsC.putString("matricula", matricula);
            argsC.putString("respuesta", respuesta);

            fragmentC.setArguments(argsC);

            // Reemplazo animado hacia FragmentC
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.fragment_container, fragmentC);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // 3. Botón Regresar simple
        btnRegresar.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etRespuesta != null) {
            outState.putString("respuesta_input", etRespuesta.getText().toString());
            outState.putString("carrera_input", etCarrera.getText().toString());
            outState.putString("ciudad_input", etCiudad.getText().toString());
            outState.putString("matricula_input", etMatricula.getText().toString());
        }
    }
}
