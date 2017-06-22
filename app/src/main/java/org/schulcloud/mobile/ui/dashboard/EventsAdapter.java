package org.schulcloud.mobile.ui.dashboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<Event> mEvent;

    @Inject
    DashboardPresenter mDashboardPresenter;

    @Inject
    public EventsAdapter() { mEvent = new ArrayList<>(); }

    public void setEvents(List<Event> events) {
        mEvent = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    private String millisToDate(long millis){
        Date date = new Date(millis);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return formatter.format(date);

        //return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMAN).format(millis);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = mEvent.get(position);

        if (!event.title.equals(event.summary))
            holder.summary.setText(event.summary);

        holder.title.setText(event.title);
        holder.startDate.setText(millisToDate(Long.parseLong(event.start)));
    }

    @Override
    public int getItemCount() {
        return mEvent.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.summary)
        TextView summary;
        @BindView(R.id.startDate)
        TextView startDate;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
