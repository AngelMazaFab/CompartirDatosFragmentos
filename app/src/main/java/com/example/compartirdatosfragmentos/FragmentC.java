package com.example.compartirdatosfragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentC extends Fragment {

    public FragmentC() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, container, false);

        // Referencias a los TextViews del resumen
        TextView tvNombreC = view.findViewById(R.id.tvNombreC);
        TextView tvEdadC = view.findViewById(R.id.tvEdadC);
        TextView tvCorreoC = view.findViewById(R.id.tvCorreoC);
        TextView tvCalificacionC = view.findViewById(R.id.tvCalificacionC);
        TextView tvSuscritoC = view.findViewById(R.id.tvSuscritoC);
        TextView tvRespuestaC = view.findViewById(R.id.tvRespuestaC);
        Button btnRegresarC = view.findViewById(R.id.btnRegresarC);

        // Leer datos del Bundle
        Bundle args = getArguments();
        if (args != null) {
            tvNombreC.setText("Nombre: " + args.getString("nombre", ""));
            tvEdadC.setText("Edad: " + args.getInt("edad", 0));
            tvCorreoC.setText("Correo: " + args.getString("correo", ""));
            tvCalificacionC.setText("Calificación: " + args.getDouble("calificacion", 0.0));
            tvSuscritoC.setText("Suscrito: " + (args.getBoolean("suscrito", false) ? "Sí" : "No"));

            String respuesta = args.getString("respuesta", "");
            if (!respuesta.isEmpty()) {
                tvRespuestaC.setText("Respuesta: " + respuesta);
            } else {
                tvRespuestaC.setText("Respuesta: (sin respuesta)");
            }
        }

        // Botón regresar
        btnRegresarC.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
