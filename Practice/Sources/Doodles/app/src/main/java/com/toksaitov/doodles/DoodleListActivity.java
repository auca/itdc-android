package com.toksaitov.doodles;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toksaitov.doodles.dummy.Doodle;
import com.toksaitov.doodles.dummy.DoodleViewModel;
import com.toksaitov.doodles.dummy.DummyContent;

import java.util.List;

public class DoodleListActivity extends AppCompatActivity {
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.doodle_detail_container) != null) {
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.doodle_list);
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final DoodleListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private List<Doodle> doodles;
        private final boolean mTwoPane;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Doodle item = (Doodle) view.getTag();
            if (mTwoPane) {
                Bundle fragmentArguments = new Bundle();
                fragmentArguments.putString(DoodleDetailFragment.ARG_ITEM_ID, item.getPath());

                DoodleDetailFragment fragment = new DoodleDetailFragment();
                fragment.setArguments(fragmentArguments);

                FragmentManager fragmentManager = mParentActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.doodle_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, DoodleDetailActivity.class);
                intent.putExtra(DoodleDetailFragment.ARG_ITEM_ID, item.getPath());

                context.startActivity(intent);
            }
            }
        };

        SimpleItemRecyclerViewAdapter(DoodleListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
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
