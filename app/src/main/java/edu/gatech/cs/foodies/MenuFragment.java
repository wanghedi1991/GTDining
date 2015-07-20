package edu.gatech.cs.foodies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


public class MenuFragment extends Fragment {


    private String menuUrl;
    private WebView webView;

    /**
     * The fragment's ListView/GridView.
     */
    private RecyclerView menuView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MenuEntryAdapter mAdapter;

    public static MenuFragment newInstance(String menuUrl) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(Constants.RESTAURANT_MENU_URL, menuUrl);
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
            menuUrl = getArguments().getString(Constants.RESTAURANT_MENU_URL);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        webView = (WebView) view.findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(menuUrl);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String id);
    }

}
