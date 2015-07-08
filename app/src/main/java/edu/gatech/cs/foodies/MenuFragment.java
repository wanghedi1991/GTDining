package edu.gatech.cs.foodies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


public class MenuFragment extends Fragment implements AbsListView.OnItemClickListener {


    private static final String IMAGE_URI = "IMAGE_URI";
    private static final String NAME = "NAME";

    private String resName;
    private int imageUri;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private RecyclerView menuView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MenuEntryAdapter mAdapter;

    public static MenuFragment newInstance(String name, int imageUri) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putInt(IMAGE_URI, imageUri);
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            resName = getArguments().getString(NAME);
            imageUri = getArguments().getInt(IMAGE_URI);
        }
        String name = getArguments().getString(NAME);
        if (name.charAt(0) == 'P') {

            MenuEntry entries[] = {new MenuEntry("Orange Chicken", 3.49, 300),
                    new MenuEntry("Fried Rice", 2.99, 520),
                    new MenuEntry("While Rice", 2.99, 380)};
            mAdapter = new MenuEntryAdapter(entries, getActivity());
        }
        if (name.charAt(0) == 'C') {

            MenuEntry entries[] = {new MenuEntry("Chicken Sandwich", 5.99, 600),
                    new MenuEntry("Chicken Nuggets", 5.49, 620),
                    new MenuEntry("Coke", 1.99, 170)};
            mAdapter = new MenuEntryAdapter(entries, getActivity());
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Set the adapter
        menuView = (RecyclerView) view.findViewById(android.R.id.list);
        menuView.setLayoutManager(new LinearLayoutManager(getActivity()));
        menuView.setAdapter(mAdapter);
        ImageView icon = (ImageView) view.findViewById(R.id.restaurant_icon);
        icon.setImageResource(imageUri);
        TextView name = (TextView) view.findViewById(R.id.restaurant_name);
        name.setText(resName);
        // Set OnItemClickListener so we can be notified on item clicks


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
//        View emptyView = menuView.getEmptyView();
//
//        if (emptyView instanceof TextView) {
//            ((TextView) emptyView).setText(emptyText);
//        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String id);
    }

}
