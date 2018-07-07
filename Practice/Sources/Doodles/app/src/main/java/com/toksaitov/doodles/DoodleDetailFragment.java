package com.toksaitov.doodles;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toksaitov.doodles.dummy.DummyContent;

public class DoodleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";

    private DummyContent.DummyItem mItem;

    public DoodleDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle fragmentArguments = getArguments();
        if (fragmentArguments.containsKey(ARG_ITEM_ID)) {
            String itemID = fragmentArguments.getString(ARG_ITEM_ID);
            mItem = DummyContent.ITEM_MAP.get(itemID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.doodle_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.doodle_detail)).setText(mItem.details);
        }

        return rootView;
    }
}
