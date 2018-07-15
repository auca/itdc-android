package com.toksaitov.doodles;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.toksaitov.doodles.dummy.Doodle;
import com.toksaitov.doodles.dummy.DoodleViewModel;
import com.toksaitov.doodles.dummy.DummyContent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class DoodleListActivity extends AppCompatActivity {
    private static final String DRAWINGS_DIRECTORY_NAME = "drawings";

    private boolean mTwoPane;

    private DoodleViewModel doodleViewModel;
    private SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        doodleViewModel = ViewModelProviders.of(this).get(DoodleViewModel.class);
        doodleViewModel.getAllDoodles().observe(this, new Observer<List<Doodle>>() {
            @Override
            public void onChanged(@Nullable final List<Doodle> doodles) {
                adapter.setDoodles(doodles);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewDoodle();
            }
        });

        if (findViewById(R.id.doodle_detail_container) != null) {
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.doodle_list);
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void createNewDoodle() {
        File drawingsDirectory =
            getDir(DRAWINGS_DIRECTORY_NAME, MODE_PRIVATE);
        String path =
            drawingsDirectory.getAbsoluteFile() +
                File.separator + UUID.randomUUID() + "_" +
                System.currentTimeMillis();

        File doodleFile = new File(path);
        try {
            doodleFile.createNewFile();
        } catch (IOException e) {
            reportError("Failed to create a new image.");
            return;
        }

        doodleViewModel.insert(new Doodle(path));

        openDoodle(path);
    }

    private void openDoodle(String path) {
        if (mTwoPane) {
            Bundle fragmentArguments = new Bundle();
            fragmentArguments.putString(DoodleDetailFragment.ARG_DOODLE_PATH, path);

            DoodleDetailFragment fragment = new DoodleDetailFragment();
            fragment.setArguments(fragmentArguments);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.doodle_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DoodleDetailActivity.class);
            intent.putExtra(DoodleDetailFragment.ARG_DOODLE_PATH, path);

            startActivity(intent);
        }
    }

    private void deleteDoodle(Doodle doodle) {
        doodleViewModel.delete(doodle);

        File doodleFile = new File(doodle.getPath());
        if (!doodleFile.delete()) {
            reportError("Failed to delete the image file.");
        }
    }

    private void reportError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final DoodleListActivity mParentActivity;
        private List<Doodle> doodles;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Doodle doodle = (Doodle) view.getTag();
                mParentActivity.openDoodle(doodle.getPath());
            }
        };

        private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Doodle doodle = (Doodle) view.getTag();
                mParentActivity.deleteDoodle(doodle);

                return true;
            }
        };

        SimpleItemRecyclerViewAdapter(DoodleListActivity parent) {
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doodle_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (doodles != null) {
                Doodle current = doodles.get(position);
                holder.pathTextView.setText(current.getPath());

                holder.itemView.setTag(current);
                holder.itemView.setOnClickListener(mOnClickListener);
                holder.itemView.setOnLongClickListener(mOnLongClickListener);
            } else {
                holder.pathTextView.setText("Unknown Image Path");
            }
        }

        @Override
        public int getItemCount() {
            return doodles != null ? doodles.size() : 0;
        }

        void setDoodles(List<Doodle> doodles){
            this.doodles = doodles;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView pathTextView;

            ViewHolder(View view) {
                super(view);
                pathTextView = view.findViewById(R.id.content);
            }
        }
    }
}
