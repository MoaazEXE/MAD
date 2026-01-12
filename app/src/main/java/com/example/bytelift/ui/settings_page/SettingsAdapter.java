package com.example.bytelift.ui.settings_page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bytelift.R;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<SettingsItem> items;

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_VALUE = 1;
    private static final int VIEW_TYPE_TOGGLE = 2;
    private static final int VIEW_TYPE_ACTION = 3;
    private static final int VIEW_TYPE_DESTRUCTIVE = 4;
    private static final int VIEW_TYPE_INFO = 5;

    public SettingsAdapter(List<SettingsItem> items) {
        this.items = items;
    }

    /**
     * Decide which layout to use per row based on SettingsItem.Type
     */
    @Override
    public int getItemViewType(int position) {
        SettingsItem.Type type = items.get(position).getType();
        switch (type) {
            case HEADER:
                return VIEW_TYPE_HEADER;
            case VALUE:
                return VIEW_TYPE_VALUE;
            case TOGGLE:
                return VIEW_TYPE_TOGGLE;
            case ACTION:
                return VIEW_TYPE_ACTION;
            case DESTRUCTIVE:
                return VIEW_TYPE_DESTRUCTIVE;
            case INFO:
                return VIEW_TYPE_INFO;
            default:
                return VIEW_TYPE_ACTION;
        }
    }

    /**
     * Inflate the correct row layout and return the matching ViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_settings_header, parent, false));
            case VIEW_TYPE_VALUE:
                return new ValueViewHolder(inflater.inflate(R.layout.item_settings_value, parent, false));
            case VIEW_TYPE_TOGGLE:
                return new ToggleViewHolder(inflater.inflate(R.layout.item_settings_toggle, parent, false));
            case VIEW_TYPE_ACTION:
                return new ActionViewHolder(inflater.inflate(R.layout.item_settings_action, parent, false));
            case VIEW_TYPE_DESTRUCTIVE:
                return new DestructiveViewHolder(inflater.inflate(R.layout.item_settings_destructive, parent, false));
            case VIEW_TYPE_INFO:
                return new InfoViewHolder(inflater.inflate(R.layout.item_settings_info, parent, false));
            default:
                return new ActionViewHolder(inflater.inflate(R.layout.item_settings_action, parent, false));
        }
    }

    /**
     * Bind data into each row and wire up click/toggle callbacks
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SettingsItem item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof ValueViewHolder) {
            ((ValueViewHolder) holder).bind(item);
        } else if (holder instanceof ToggleViewHolder) {
            ((ToggleViewHolder) holder).bind(item);
        } else if (holder instanceof ActionViewHolder) {
            ((ActionViewHolder) holder).bind(item);
        } else if (holder instanceof DestructiveViewHolder) {
            ((DestructiveViewHolder) holder).bind(item);
        } else if (holder instanceof InfoViewHolder) {
            ((InfoViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Convenience method when you update one row (e.g. subtitle changes)
     */
    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    // =========================
    // VIEW HOLDERS
    // =========================

    /**
     * HEADER row: just displays a section label like "ACCOUNT"
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvHeaderTitle);
        }

        void bind(SettingsItem item) {
            titleText.setText(item.getTitle());
        }
    }

    /**
     * VALUE row: title + subtitle/value + chevron (clickable)
     * Example: Theme -> "System"
     */
    static class ValueViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView valueText;
        ImageView chevron;

        ValueViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvTitle);
            valueText = itemView.findViewById(R.id.tvValue);
            chevron = itemView.findViewById(R.id.ivChevron);
        }

        void bind(SettingsItem item) {
            titleText.setText(item.getTitle());
            valueText.setText(item.getSubtitle());

            itemView.setOnClickListener(v -> {
                if (item.getListener() != null) {
                    item.getListener().onClick();
                }
            });
        }
    }

    /**
     * TOGGLE row: title + subtitle + SwitchCompat
     * Important: remove listener before setChecked() to avoid double triggers.
     */
    static class ToggleViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView subtitleText;
        SwitchCompat toggle;

        ToggleViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvTitle);
            subtitleText = itemView.findViewById(R.id.tvSubtitle);
            toggle = itemView.findViewById(R.id.switchToggle);
        }

        void bind(SettingsItem item) {
            titleText.setText(item.getTitle());

            String subtitle = item.getSubtitle();
            if (subtitle != null && !subtitle.isEmpty()) {
                subtitleText.setVisibility(View.VISIBLE);
                subtitleText.setText(subtitle);
            } else {
                subtitleText.setVisibility(View.GONE);
            }

            // Prevent onCheckedChange firing from recycling
            toggle.setOnCheckedChangeListener(null);
            toggle.setChecked(item.getToggleState());

            toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (item.getToggleListener() != null) {
                    item.getToggleListener().onToggle(isChecked);
                }
            });

            // Let the whole row toggle the switch
            itemView.setOnClickListener(v -> toggle.toggle());
        }
    }

    /**
     * ACTION row: clickable item (e.g. Logout, Clear Cache)
     */
    static class ActionViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        ActionViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvTitle);
        }

        void bind(SettingsItem item) {
            titleText.setText(item.getTitle());
            itemView.setOnClickListener(v -> {
                if (item.getListener() != null) {
                    item.getListener().onClick();
                }
            });
        }
    }

    /**
     * DESTRUCTIVE row: clickable but styled as dangerous (e.g. Delete Account)
     */
    static class DestructiveViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        DestructiveViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvTitle);
        }

        void bind(SettingsItem item) {
            titleText.setText(item.getTitle());
            itemView.setOnClickListener(v -> {
                if (item.getListener() != null) {
                    item.getListener().onClick();
                }
            });
        }
    }

    /**
     * INFO row: purely informational text (e.g. "ByteLift v1.0 ...")
     */
    static class InfoViewHolder extends RecyclerView.ViewHolder {
        TextView infoText;

        InfoViewHolder(View itemView) {
            super(itemView);
            infoText = itemView.findViewById(R.id.tvInfo);
        }

        void bind(SettingsItem item) {
            // In your list build: you used subtitle for info text
            infoText.setText(item.getSubtitle());
        }
    }
}
