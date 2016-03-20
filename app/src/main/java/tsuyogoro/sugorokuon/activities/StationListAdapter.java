package tsuyogoro.sugorokuon.activities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.StationListViewHolder> {

    @Override
    public StationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(StationListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class StationListViewHolder extends RecyclerView.ViewHolder {
        public StationListViewHolder(View itemView) {
            super(itemView);
        }
    }
}
