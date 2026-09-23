package com.example.compartirdatosfragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentB extends Fragment {

    public FragmentB() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);

        // Paso 5: Referencias a los TextViews de la tarjeta
        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvEdad = view.findViewById(R.id.tvEdad);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvCalificacion = view.findViewById(R.id.tvCalificacion);
        TextView tvSuscrito = view.findViewById(R.id.tvSuscrito);

        // Paso 6: Referencias para la respuesta
        EditText etRespuesta = view.findViewById(R.id.etRespuesta);
        Button btnEnviarRespuesta = view.findViewById(R.id.btnEnviarRespuesta);

        // Paso 4: Botón Regresar
        Button btnRegresar = view.findViewById(R.id.btnRegresar);

        // Paso 9: Botón Ir a FragmentC
        Button btnIrC = view.findViewById(R.id.btnIrC);

        // Leer datos del Bundle con tipos correctos
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

            // Paso 5: Mostrar datos formateados en la tarjeta
            tvNombre.setText("Nombre: " + nombre);
            tvEdad.setText("Edad: " + edad);
            tvCorreo.setText("Correo: " + correo);
            tvCalificacion.setText("Calificación: " + calificacion);
            tvSuscrito.setText("Suscrito: " + (suscrito ? "Sí" : "No"));
        }

        // Variables finales para usar dentro de los listeners
        final String fNombre = nombre;
        final int fEdad = edad;
        final String fCorreo = correo;
        final double fCalificacion = calificacion;
        final boolean fSuscrito = suscrito;

        // Paso 6: Enviar respuesta de vuelta a FragmentA
        btnEnviarRespuesta.setOnClickListener(v -> {
            String respuesta = etRespuesta.getText().toString().trim();
            if (respuesta.isEmpty()) {
                Toast.makeText(requireContext(), "Escribe una respuesta", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle result = new Bundle();
            result.putString("respuesta", respuesta);
            getParentFragmentManager().setFragmentResult("requestKeyRespuesta", result);
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Paso 4: Regresar a FragmentA
        btnRegresar.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Paso 9: Ir a FragmentC con los mismos datos más la respuesta
        btnIrC.setOnClickListener(v -> {
            String respuesta = etRespuesta.getText().toString().trim();

            FragmentC fragmentC = new FragmentC();
            Bundle argsC = new Bundle();
            argsC.putString("nombre", fNombre);
            argsC.putInt("edad", fEdad);
            argsC.putString("correo", fCorreo);
            argsC.putDouble("calificacion", fCalificacion);
            argsC.putBoolean("suscrito", fSuscrito);
            argsC.putString("respuesta", respuesta);
            fragmentC.setArguments(argsC);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            // Paso 7: Mismas animaciones
            transaction.setCustomAnimations(
                    R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.fragment_container, fragmentC);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
