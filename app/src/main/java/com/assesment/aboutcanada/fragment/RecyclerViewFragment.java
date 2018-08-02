package com.assesment.aboutcanada.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.assesment.aboutcanada.Database.DBHelper;
import com.assesment.aboutcanada.R;
import com.assesment.aboutcanada.adapter.RecyclerViewAdapter;
import com.assesment.aboutcanada.model.CityInfo;
import com.assesment.aboutcanada.app_interface.RequestInterface;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecyclerViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecyclerViewFragment extends Fragment {
    public static final String BASE_URL = "https://dl.dropboxusercontent.com/";
    public static final String MyPREFERENCES = "CityPrefs";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mFragmentListener;
    private RecyclerViewAdapter cityInfoAdapter;
    private RecyclerView mRecyclerView;
    private CompositeDisposable mRxCompositeDisposable;
    private SwipeRefreshLayout swipeContainer;
    private DBHelper mCityDatabaseHelper;
    private SharedPreferences sharedpreferences;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecyclerViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecyclerViewFragment newInstance(String param1, String param2) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mCityDatabaseHelper = new DBHelper(getActivity());
        mRxCompositeDisposable = new CompositeDisposable();
        sharedpreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (checkConnection()) {
            loadJSON();
            rootView.findViewById(R.id.noconnection).setVisibility(View.GONE);
        } else if (mCityDatabaseHelper.numberOfRows() > 0) {
            rootView.findViewById(R.id.noconnection).setVisibility(View.GONE);
            cityInfoAdapter = new RecyclerViewAdapter(getActivity(), mCityDatabaseHelper.getAllData());
            mRecyclerView.setAdapter(cityInfoAdapter);
            getActivity().setTitle(sharedpreferences.getString("cityname", null));
        } else {
            rootView.findViewById(R.id.noconnection).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        // Your code to refresh the list here.
        // Make sure you call swipeContainer.setRefreshing(false)
        // once the network request has completed successfully.
        swipeContainer.setOnRefreshListener(this::refreshContent);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Inflate the layout for this fragment
        return rootView;
    }

    @SuppressLint("CheckResult")
    private void loadJSON() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ProgressDialog downloadProgressDialog = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.pleasewait),
                getResources().getString(R.string.downloadingdata),
                true, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface mRequestInterface = retrofit.create(RequestInterface.class);
        Observable<CityInfo> mCityName = mRequestInterface.register();

        mCityName.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> Toast.makeText(getActivity(), "Error in downloading data. ", Toast.LENGTH_SHORT).show())
                .subscribe(cityData -> {

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("cityname", cityData.getTitle());
                    editor.apply();
                    Objects.requireNonNull(getActivity()).setTitle(cityData.getTitle());

                    if (cityData.getCityInfoRows().size() > 0) {
                        for (int i = 0; i < cityData.getCityInfoRows().size(); i++) {
                            mCityDatabaseHelper.insertCityInfo(cityData.getCityInfoRows().get(i).getTitle(), cityData.getCityInfoRows().get(i).getDescription(), cityData.getCityInfoRows().get(i).getImageHref());
                        }

                        cityInfoAdapter = new RecyclerViewAdapter(getActivity(), cityData.getCityInfoRows());
                        mRecyclerView.setAdapter(cityInfoAdapter);
                    }

                    if (downloadProgressDialog.isShowing()) {
                        downloadProgressDialog.dismiss();
                    }
                });
    }

    private void refreshContent() {
        new Handler().postDelayed(() -> swipeContainer.setRefreshing(false), 5000);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mFragmentListener != null) {
            mFragmentListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mFragmentListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mRxCompositeDisposable.clear();
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else
            return false;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
