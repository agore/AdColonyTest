package org.bitxbit.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyNativeAdView;

import java.lang.ref.WeakReference;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView rv;

    private static final String AD_COLONY_ZONE_ID = "vzcf774f9b9bcd45eeac";
    private static final String AD_COLONY_APP_ID = "app189e7ba1d2ce49f381";
    private AdColonyNativeAdView adColonyAdView;
    private AdColonyTestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.recycler_test);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new AdColonyTestAdapter();
        rv.setAdapter(adapter);

        AdColony.configure(this, "akira, v1.0", AD_COLONY_APP_ID, AD_COLONY_ZONE_ID);
        AdColony.addAdAvailabilityListener(new AdColonyListener(this));

    }

    @Override
    protected void onPause() {
        super.onPause();
        AdColony.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdColony.resume(this);
    }

    @Override
    protected void onStop() {
        if (adColonyAdView != null) {
            adColonyAdView.destroy();
            adColonyAdView = null;
        }
        super.onStop();
    }

    private static class AdColonyTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int AD = 1;
        private static final int TEXT = 2;
        private AdColonyNativeAdView adColonyAdView;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case TEXT:
                    SimpleViewHolder svh = new SimpleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_simple, viewGroup, false));
                    return svh;

                case AD:
                    AdColonyViewHolder avh = new AdColonyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_ad_colony, viewGroup, false));
                    return avh;
            }
            //shouldn't get here
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            switch (getItemViewType(position)) {
                case TEXT:
                    SimpleViewHolder svh = (SimpleViewHolder) viewHolder;
                    svh.tv.setText("I am at position " + position);
                    break;

                case AD:
                    AdColonyViewHolder avh = (AdColonyViewHolder) viewHolder;
                    if (adColonyAdView != null && adColonyAdView.isReady()) {
                        Log.d(TAG, "now adding ad to view");
                        avh.adColonyPlacement.addView(adColonyAdView);
                        adColonyAdView.notifyAddedToListView();
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 3 ? AD : TEXT;
        }

        public void setAdColonyAdView(AdColonyNativeAdView adColonyAdView) {
            this.adColonyAdView = adColonyAdView;
        }
    }

    private static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.txt_simple);
        }
    }

    private static class AdColonyViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup adColonyPlacement;

        public AdColonyViewHolder(View itemView) {
            super(itemView);
            adColonyPlacement = (ViewGroup) itemView.findViewById(R.id.layout_ad_colony_slot);
        }
    }

    private static class AdColonyListener implements AdColonyAdAvailabilityListener {
        private WeakReference<MainActivity> ref;

        public AdColonyListener(MainActivity act) {
            ref = new WeakReference<MainActivity>(act);
        }

        @Override
        public void onAdColonyAdAvailabilityChange(boolean available, String zoneId) {
            Log.d(TAG, "onAdColonyAdAvailabilityChange called with available " + available + " and zoneId " + zoneId);
            MainActivity act = ref.get();
            if (available && act != null) {
                Log.d(TAG, "ad now available");
                float density = act.getResources().getDisplayMetrics().density;
                if (act.adColonyAdView == null) {
                    act.adColonyAdView = new AdColonyNativeAdView(act, AD_COLONY_ZONE_ID, (int) (300 * density));
                    act.adColonyAdView.prepareForListView();
                    act.adapter.setAdColonyAdView(act.adColonyAdView);
                }
            }
        }
    }
}
