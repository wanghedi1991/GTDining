package edu.gatech.cs.foodies;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class GuidedSearchSubFragment extends Fragment {
    private static final String SEARCH_INDEX = "SEARCH_INDEX";

    private int searchIndex;

    private GuidedSearchSubListener mListener;

    public static GuidedSearchSubFragment newInstance(int searchIndex) {
        GuidedSearchSubFragment fragment = new GuidedSearchSubFragment();
        Bundle args = new Bundle();
        args.putInt(SEARCH_INDEX, searchIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public GuidedSearchSubFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchIndex = getArguments().getInt(SEARCH_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guided_search_sub, container, false);
        ListView listView = (ListView) view.findViewById(R.id.guided_search_sub_list);
        int sourceid;
        if (searchIndex == 0) {
            sourceid = R.array.guided_search_cuisine;
        } else {
            sourceid = R.array.guided_search_location;
        }
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(), sourceid, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.selectGuidedSearchItem(((TextView) view).getText().toString());
            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GuidedSearchSubListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GuidedSearchSubListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface GuidedSearchSubListener {
        void selectGuidedSearchItem(String item);
    }

}
