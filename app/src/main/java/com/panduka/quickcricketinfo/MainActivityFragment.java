package com.panduka.quickcricketinfo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.panduka.quickcricketinfo.adapters.CricMatchAdapter;
import com.panduka.quickcricketinfo.app.AppController;
import com.panduka.quickcricketinfo.datastructure.CricketMatch;
import com.panduka.quickcricketinfo.service.DataDownloader;
import com.panduka.quickcricketinfo.utils.Assistance;
import com.panduka.quickcricketinfo.utils.DataResolver;
import com.panduka.quickcricketinfo.utils.ListRefreshViewLayout;

import java.util.ArrayList;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DataDownloader.ResponseHandler, MainActivity.SetAdapter {
    private static final String MSG_NO_INTERNET_ERROR = "Oops! Internet Not available";

    private ListRefreshViewLayout mSwipeRefreshLayout;
    private RecyclerView mMatchList;
    private TextView mEmptyView;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    private void setmEmptyView(String message,int visibility){
        mEmptyView.setVisibility(visibility);
        mEmptyView.setText(message);
    }

    private void setmEmptyView(int visibility){
        mEmptyView.setVisibility(visibility);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!isRefreshing()) {
                    setRefreshing(true);
                }

                getData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMatchList = (RecyclerView) rootView.findViewById(R.id.matchList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMatchList.setLayoutManager(layoutManager);
        mMatchList.setHasFixedSize(true);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);

        //to show refreshing icon in the beginning: passing an empty adapter to the recycleview
        mMatchList.setAdapter(new CricMatchAdapter(new ArrayList<CricketMatch>()));

        //to minimise complexities in refresh layout and recycle view scroll getting overlapped
        /*
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

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        */

        mSwipeRefreshLayout = new ListRefreshViewLayout(getContext(),mMatchList);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.addView(rootView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // Make sure that the SwipeRefreshLayout will fill the fragment
        mSwipeRefreshLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));


        setColorScheme(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorBlack);

        return mSwipeRefreshLayout;
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


    /**
     * Returns whether the {@link android.support.v4.widget.SwipeRefreshLayout} is currently
     * refreshing or not.
     *
     * @see android.support.v4.widget.SwipeRefreshLayout#isRefreshing()
     */
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    /**
     * Set whether the {@link android.support.v4.widget.SwipeRefreshLayout} should be displaying
     * that it is refreshing or not.
     *
     * @see android.support.v4.widget.SwipeRefreshLayout#setRefreshing(boolean)
     */
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    public void setColorScheme(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
        mSwipeRefreshLayout.setColorScheme(colorRes1, colorRes2, colorRes3, colorRes4);
    }

    private void getData() {
        if (Assistance.isNetworkAvailable(getActivity())) {
            setRefreshing(true);
            DataDownloader dataLoader = new DataDownloader(this);
            dataLoader.getMatches();
        } else {
            setRefreshing(false);
            setmEmptyView(MSG_NO_INTERNET_ERROR,View.VISIBLE);
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
        setRefreshing(false);
        if (response != null) {
            if (response.containsKey(DataDownloader.DATA_RETRIEVE_ERROR)) {
                setmEmptyView(response.get(DataDownloader.DATA_RETRIEVE_ERROR),View.VISIBLE);
                Toast.makeText(getActivity(), response.get(DataDownloader.DATA_RETRIEVE_ERROR), Toast.LENGTH_LONG).show();
            } else if (response.containsKey(DataDownloader.DATA_RESOLVER_ERROR)) {
                setmEmptyView(response.get(DataDownloader.DATA_RESOLVER_ERROR),View.VISIBLE);
                Toast.makeText(getActivity(), response.get(DataDownloader.DATA_RESOLVER_ERROR), Toast.LENGTH_LONG).show();
            } else if (response.containsKey(DataDownloader.DATA_RESOLVER_OK)) {
                setmEmptyView(View.GONE);
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

    @Override
    public void setRefreshHandler() {
        getData();

    }
}
