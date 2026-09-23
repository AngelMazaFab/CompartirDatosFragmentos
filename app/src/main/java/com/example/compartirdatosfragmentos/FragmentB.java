package com.example.compartirdatosfragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentB extends Fragment {

    public FragmentB() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);

        TextView tvData = view.findViewById(R.id.tvData);

        Bundle args = getArguments();
        if (args != null) {
            String dato = args.getString("dato");
            if (dato != null) {
                tvData.setText(dato);
            }
        }

        return view;
    }
}
