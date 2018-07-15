package com.toksaitov.doodles;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class DoodleDetailFragment extends Fragment {
    public static final String ARG_DOODLE_PATH = "item_path";

    private String doodlePath;
    private DrawingView drawingView;

    public DoodleDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle fragmentArguments = getArguments();
        if (fragmentArguments.containsKey(ARG_DOODLE_PATH)) {
            doodlePath = fragmentArguments.getString(ARG_DOODLE_PATH);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(doodlePath);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.doodle_detail, container, false);

        if (doodlePath != null) {
            drawingView = rootView.findViewById(R.id.doodle);

            File file = new File(doodlePath);
            try {
                drawingView.loadDrawing(file);
            } catch (IOException e) {
                reportError(e.getMessage());
                e.printStackTrace();
            }
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (doodlePath != null) {
            File file = new File(doodlePath);
            try {
                drawingView.saveDrawing(file);
            } catch (IOException e) {
                reportError("Failed to save the file.");
                e.printStackTrace();
            }
        }
    }

    private void reportError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
