package com.panduka.quickcricketinfo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.panduka.quickcricketinfo.adapters.CricMatchAdapter;
import com.panduka.quickcricketinfo.app.AppController;
import com.panduka.quickcricketinfo.service.DataDownloader;
import com.panduka.quickcricketinfo.utils.Assistance;
import com.panduka.quickcricketinfo.utils.DataResolver;

import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DataDownloader.ResponseHandler {
    private static final String MSG_NO_INTERNET_ERROR = "Oops! Internet Not available";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mMatchList;
    private TextView mEmptyView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        /*
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        */

        mMatchList = (RecyclerView) rootView.findViewById(R.id.kwickieList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMatchList.setLayoutManager(layoutManager);
        mMatchList.setHasFixedSize(true);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);


        //to minimise complexities in refresh layout and recycle view scroll getting overlapped
        mMatchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!DataResolver.getInstance().isDataLoaded()) {
            getData();
        } else {
            setAdapter();
        }

    }

    private void getData() {
        if (Assistance.isNetworkAvailable(getActivity())) {
            mSwipeRefreshLayout.setRefreshing(true);
            DataDownloader dataLoader = new DataDownloader(this);
            dataLoader.getKwickies();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(MSG_NO_INTERNET_ERROR);
            Toast.makeText(getActivity(), MSG_NO_INTERNET_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

    @Override
    public void onStop() {
        super.onStop();
        AppController.getInstance().cancelPendingRequests(DataDownloader.TAG_REQ_GET_CRICKET_MATCHES);
    }

    @Override
    public void sendResponse(Map<String, String> response) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (response != null) {
            if (response.containsKey(DataDownloader.DATA_RETRIEVE_ERROR)) {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(response.get(DataDownloader.DATA_RETRIEVE_ERROR));
                Toast.makeText(getActivity(), response.get(DataDownloader.DATA_RETRIEVE_ERROR), Toast.LENGTH_LONG).show();
            } else if (response.containsKey(DataDownloader.DATA_RESOLVER_ERROR)) {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(response.get(DataDownloader.DATA_RESOLVER_ERROR));
                Toast.makeText(getActivity(), response.get(DataDownloader.DATA_RESOLVER_ERROR), Toast.LENGTH_LONG).show();
            } else if (response.containsKey(DataDownloader.DATA_RESOLVER_OK)) {
                mEmptyView.setVisibility(View.GONE);
                setAdapter();
                Toast.makeText(getActivity(), response.get(DataDownloader.DATA_RESOLVER_OK), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setAdapter() {
        try {
            mMatchList.setAdapter(new CricMatchAdapter(DataResolver.getInstance().getCricketMatchList()));
        } catch (IllegalStateException e) {
            Toast.makeText(getActivity(), DataDownloader.MSG_OBJ_RETRIEVE_ERROR, Toast.LENGTH_LONG);
        }
    }
}
