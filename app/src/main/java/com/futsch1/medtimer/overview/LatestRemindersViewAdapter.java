package com.futsch1.medtimer.overview;

import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.futsch1.medtimer.database.ReminderEvent;
import com.futsch1.medtimer.helpers.IdlingListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LatestRemindersViewAdapter extends IdlingListAdapter<ReminderEvent, LatestRemindersViewHolder> implements Filterable {
    private final Filter filter = new ReminderEventFilter();
    private List<ReminderEvent> data;

    public LatestRemindersViewAdapter(@NonNull DiffUtil.ItemCallback<ReminderEvent> diffCallback) {
        super(diffCallback);
        setHasStableIds(true);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public LatestRemindersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LatestRemindersViewHolder.create(parent);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull LatestRemindersViewHolder holder, final int position) {
        ReminderEvent current = getItem(position);
        holder.bind(current);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).reminderEventId;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setData(List<ReminderEvent> data) {
        this.data = data;
    }

    public static class ReminderEventDiff extends DiffUtil.ItemCallback<ReminderEvent> {

        @Override
        public boolean areItemsTheSame(@NonNull ReminderEvent oldItem, @NonNull ReminderEvent newItem) {
            return oldItem.reminderEventId == newItem.reminderEventId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReminderEvent oldItem, @NonNull ReminderEvent newItem) {
            return oldItem.equals(newItem);
        }
    }

    private class ReminderEventFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ReminderEvent> filteredList = new ArrayList<>();
            String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();
            boolean showOnlyOpen = filterPattern.contains("o");
            if (data != null) {
                for (ReminderEvent item : data) {
                    if (isVisible(item, showOnlyOpen)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        private static boolean isVisible(ReminderEvent item, boolean showOnlyOpen) {
            return !showOnlyOpen || item.status == ReminderEvent.ReminderStatus.RAISED;
        }

        /**
         * @noinspection unchecked
         */
        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            submitList((List<ReminderEvent>) results.values);
        }
    }
}
