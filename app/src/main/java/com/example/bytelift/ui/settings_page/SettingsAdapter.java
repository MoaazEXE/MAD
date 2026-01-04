package com.example.bytelift.ui.settings_page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget. TextView;
import androidx.annotation. NonNull;
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

    @Override
    public int getItemViewType(int position) {
        SettingsItem. Type type = items.get(position).getType();
        switch (type) {
            case HEADER:  return VIEW_TYPE_HEADER;
            case VALUE: return VIEW_TYPE_VALUE;
            case TOGGLE: return VIEW_TYPE_TOGGLE;
            case ACTION: return VIEW_TYPE_ACTION;
            case DESTRUCTIVE: return VIEW_TYPE_DESTRUCTIVE;
            case INFO: return VIEW_TYPE_INFO;
            default: return VIEW_TYPE_ACTION;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(inflater. inflate(R.layout.item_settings_header, parent, false));
            case VIEW_TYPE_VALUE:
                return new ValueViewHolder(inflater.inflate(R. layout.item_settings_value, parent, false));
            case VIEW_TYPE_TOGGLE:
                return new ToggleViewHolder(inflater.inflate(R.layout.item_settings_toggle, parent, false));
            case VIEW_TYPE_ACTION:
                return new ActionViewHolder(inflater.inflate(R.layout.item_settings_action, parent, false));
            case VIEW_TYPE_DESTRUCTIVE:
                return new DestructiveViewHolder(inflater.inflate(R.layout.item_settings_destructive, parent, false));
            case VIEW_TYPE_INFO:
                return new InfoViewHolder(inflater.inflate(R. layout.item_settings_info, parent, false));
            default:
                return new ActionViewHolder(inflater.inflate(R.layout.item_settings_action, parent, false));
        }
    }

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

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    // ViewHolders
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvHeaderTitle);
        }

        void bind(SettingsItem item) {
            titleText. setText(item.getTitle());
        }
    }

    static class ValueViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView valueText;
        ImageView chevron;

        ValueViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvTitle);
            valueText = itemView.findViewById(R.id.tvValue);
            chevron = itemView.findViewById(R.id. ivChevron);
        }

        void bind(SettingsItem item) {
            titleText.setText(item.getTitle());
            valueText.setText(item.getSubtitle());
            itemView.setOnClickListener(v -> {
                if (item. getListener() != null) {
                    item.getListener().onClick();
                }
            });
        }
    }

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
            titleText. setText(item.getTitle());
            if (item.getSubtitle() != null && !item.getSubtitle().isEmpty()) {
                subtitleText.setVisibility(View.VISIBLE);
                subtitleText.setText(item. getSubtitle());
            } else {
                subtitleText. setVisibility(View.GONE);
            }

            toggle.setOnCheckedChangeListener(null);
            toggle.setChecked(item.getToggleState());
            toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (item.getToggleListener() != null) {
                    item.getToggleListener().onToggle(isChecked);
                }
            });

            itemView.setOnClickListener(v -> toggle.toggle());
        }
    }

    static class ActionViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        ActionViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvTitle);
        }

        void bind(SettingsItem item) {
            titleText. setText(item.getTitle());
            itemView.setOnClickListener(v -> {
                if (item.getListener() != null) {
                    item.getListener().onClick();
                }
            });
        }
    }

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

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        TextView infoText;

        InfoViewHolder(View itemView) {
            super(itemView);
            infoText = itemView. findViewById(R.id.tvInfo);
        }

        void bind(SettingsItem item) {
            infoText. setText(item.getSubtitle());
        }
    }
}