package com.example.compartirdatosfragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentC extends Fragment {

    public FragmentC() {
        // Constructor público requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, container, false);

        // Vistas para datos de Fragment A
        TextView tvNombreC = view.findViewById(R.id.tvNombreC);
        TextView tvEdadC = view.findViewById(R.id.tvEdadC);
        TextView tvCorreoC = view.findViewById(R.id.tvCorreoC);
        TextView tvCalificacionC = view.findViewById(R.id.tvCalificacionC);
        TextView tvSuscritoC = view.findViewById(R.id.tvSuscritoC);

        // Vistas para datos añadidos en Fragment B
        TextView tvCarreraC = view.findViewById(R.id.tvCarreraC);
        TextView tvCiudadC = view.findViewById(R.id.tvCiudadC);
        TextView tvMatriculaC = view.findViewById(R.id.tvMatriculaC);
        TextView tvRespuestaC = view.findViewById(R.id.tvRespuestaC);

        // Botones
        Button btnRegresarC = view.findViewById(R.id.btnRegresarC);
        Button btnInicioC = view.findViewById(R.id.btnInicioC);

        // Leer datos del Bundle acumulativo
        Bundle args = getArguments();
        if (args != null) {
            // Datos de A
            String nombre = args.getString("nombre", "");
            int edad = args.getInt("edad", 0);
            String correo = args.getString("correo", "");
            double calificacion = args.getDouble("calificacion", 0.0);
            boolean suscrito = args.getBoolean("suscrito", false);

            tvNombreC.setText("👤 Nombre: " + nombre);
            tvEdadC.setText("🎂 Edad: " + edad + " años");
            tvCorreoC.setText("✉️ Correo: " + correo);
            tvCalificacionC.setText("⭐ Calificación: " + calificacion);
            tvSuscritoC.setText("🔔 Suscrito a novedades: " + (suscrito ? "Sí" : "No"));

            // Datos de B
            String carrera = args.getString("carrera", "");
            String ciudad = args.getString("ciudad", "");
            String matricula = args.getString("matricula", "");
            String respuesta = args.getString("respuesta", "");

            tvCarreraC.setText("🎓 Carrera: " + carrera);
            tvCiudadC.setText("📍 Ciudad / Sede: " + ciudad);
            tvMatriculaC.setText("🆔 Matrícula: " + matricula);

            if (!respuesta.isEmpty()) {
                tvRespuestaC.setText("💬 Mensaje a Fragment A: " + respuesta);
            } else {
                tvRespuestaC.setText("💬 Mensaje a Fragment A: (No se redactó mensaje)");
            }
        }

        // Regresar a Fragment B
        btnRegresarC.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Volver directo al inicio (Fragment A) vaciando la pila
        btnInicioC.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        return view;
    }
}
