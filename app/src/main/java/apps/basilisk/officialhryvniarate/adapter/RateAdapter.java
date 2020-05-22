package apps.basilisk.officialhryvniarate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.basilisk.officialhryvniarate.R;
import apps.basilisk.officialhryvniarate.databinding.ItemHeaderBinding;
import apps.basilisk.officialhryvniarate.databinding.ItemRateBinding;
import apps.basilisk.officialhryvniarate.helper.FavoriteHelper;

public class RateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = "RateAdapter";
    private static final String PREFIX_X = "X";
    private static final int ITEMS_PER_AD = 20;

    private List<Object> listItems;
    private List<Object> listItemsFull;

    private Context context;
    private OnClickListener clickListener;

    public RateAdapter(OnClickListener onClickListener) {
        this.clickListener = onClickListener;
        this.listItems = new ArrayList<>();
        this.listItemsFull = new ArrayList<>();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return EItemType.get(viewType).viewHolder(parent, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == RecyclerView.NO_POSITION || listItems == null) return;

        Object item = listItems.get(position);
        EItemType.get(item).bind(holder, item);
    }

    @Override
    public int getItemCount() {
        return listItems != null ? listItems.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return EItemType.get(listItems.get(position)).type();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    public void setRates(List<RateItem> rates) {
        if (listItems == null) {
            listItems = new ArrayList<>();
            listItemsFull = new ArrayList<>();
        } else {
            listItems.clear();
            listItemsFull.clear();
        }

        sortingRates(rates);
        int favoritesSize = calculateFavorites(rates);

        listItems.addAll(rates);

        addHeaderItems(favoritesSize);
/*
        addAdItems();
        loadBannerAds();
*/

        listItemsFull.addAll(listItems);

        notifyDataSetChanged();
    }

    private int calculateFavorites(List<RateItem> rates) {
        int count = 0;
        for (RateItem rateItem: rates) {
            if (rateItem.isFavorite()) count++;
        }
        return count;
    }

    private void addHeaderItems(int favoritesSize) {
        int itemsSize = listItems.size();
        if (favoritesSize <= itemsSize) {
            listItems.add(favoritesSize, new HeaderItem(
                    String.format(context.getString(R.string.header_foreign_currencies), itemsSize - favoritesSize, itemsSize)
            ));
        }
        if (favoritesSize > 0) {
            listItems.add(0, new HeaderItem(
                    String.format(context.getString(R.string.header_favorites), favoritesSize, itemsSize)
            ));
        }
    }

    private void addAdItems() {
        for (int i = ITEMS_PER_AD; i <= listItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(context);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(context.getString(R.string.adaptive_banner_ad_test_unit_id));
            listItems.add(i, adView);
        }
    }

    private void loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(ITEMS_PER_AD);
    }

    private void loadBannerAd(final int index) {

        if (index >= listItems.size()) {
            return;
        }

        Object item = listItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad"
                    + " ad.");
        }

        final AdView adView = (AdView) item;

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to"
                        + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });

        // Load the banner ad.
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void sortingRates(List<RateItem> rates) {
        if (rates == null) return;
        Collections.sort(rates, (o1, o2) -> {
            int result;

            Boolean b1 = o1.isFavorite();
            Boolean b2 = o2.isFavorite();
            result = b2.compareTo(b1);
            if (result != 0) return result;

            if (o1.getCodeDigital() == 960) return 1;
            if (o2.getCodeDigital() == 960) return -1;

            Boolean bX1 = o1.getCodeLiteral().startsWith(PREFIX_X);
            Boolean bX2 = o2.getCodeLiteral().startsWith(PREFIX_X);
            result = bX1.compareTo(bX2);
            if (result != 0) return result;

            Collator collator = Collator.getInstance();
            collator.setStrength(Collator.PRIMARY);
            return collator.compare(o1.getName(), o2.getName());
        });
    }

    @Override
    public Filter getFilter() {
        return ratesFilter;
    }

    private Filter ratesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Object> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listItemsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Object itemList : listItemsFull) {
                    if (EItemType.RATE.is(itemList)) {
                        RateItem item = (RateItem) itemList;
                        if (item.getName().toLowerCase().contains(filterPattern) ||
                                item.getCodeLiteral().toLowerCase().contains(filterPattern) ||
                                String.valueOf(item.getCodeDigital()).toLowerCase().contains(filterPattern)
                        ) {
                            filteredList.add(item);
                        }
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (listItems != null) {
                listItems.clear();
                listItems.addAll((List) results.values);
                notifyDataSetChanged();
            }
        }
    };

    static class RateItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemRateBinding binding;
        private final RateAdapter.OnClickListener onClickListener;

        RateItemViewHolder(@NonNull ItemRateBinding binding, OnClickListener onClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onClickListener = onClickListener;
        }

        void bind(RateItem rateItem) {
            binding.setRate(rateItem);
            binding.setClick(new OnClickListener() {
                @Override
                public void onItemClick(View view, RateItem r) {
                    if (onClickListener != null) {
                        onClickListener.onItemClick(view, r);
                    }
                }

                @Override
                public void onFavoriteIconClick(View view, RateItem r) {
                    if (onClickListener != null) {
                        if (r.isFavorite()) {
                            FavoriteHelper.removeFavorite(view.getContext(), r.getCodeLiteral());
                        } else {
                            FavoriteHelper.addFavorite(view.getContext(), r.getCodeLiteral());
                        }
                        r.setFavorite(!r.isFavorite());
                        onClickListener.onFavoriteIconClick(view, r);
                    }
                }
            });
            binding.executePendingBindings();
        }
    }

    static class HeaderItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemHeaderBinding binding;

        HeaderItemViewHolder(@NonNull ItemHeaderBinding binding, OnClickListener onClickListener) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(HeaderItem headerItem) {
            binding.setHeader(headerItem);
            binding.executePendingBindings();
        }
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        public AdViewHolder(View view, OnClickListener onClickListener) {
            super(view);
        }

        public void bind(RecyclerView.ViewHolder holder, AdView item) {
            AdViewHolder bannerHolder = (AdViewHolder) holder;
            AdView adView = (AdView) item;
            ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
            if (adCardView.getChildCount() > 0) {
                adCardView.removeAllViews();
            }
            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }

            adCardView.addView(adView);
        }
    }

    private enum EItemType {
        RATE {
            @Override
            int type() {
                return R.layout.item_rate;
            }

            @Override
            RecyclerView.ViewHolder viewHolder(ViewGroup parent, OnClickListener onClickListener) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                ItemRateBinding binding = ItemRateBinding.inflate(inflater, parent, false);
                return new RateItemViewHolder(binding, onClickListener);
            }

            @Override
            boolean is(Object item) {
                return item instanceof RateItem;
            }

            @Override
            void bind(RecyclerView.ViewHolder holder, Object item) {
                try {
                    ((RateItemViewHolder) holder).bind((RateItem) item);
                } catch (ClassCastException e) {
                    Log.e(TAG, "bind: ", e);
                }
            }
        },
        HEADER {
            @Override
            int type() {
                return R.layout.item_header;
            }

            @Override
            RecyclerView.ViewHolder viewHolder(ViewGroup parent, OnClickListener onClickListener) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                ItemHeaderBinding binding = ItemHeaderBinding.inflate(inflater, parent, false);
                return new HeaderItemViewHolder(binding, onClickListener);
            }

            @Override
            boolean is(Object item) {
                return item instanceof HeaderItem;
            }

            @Override
            void bind(RecyclerView.ViewHolder holder, Object item) {
                try {
                    ((HeaderItemViewHolder) holder).bind((HeaderItem) item);
                } catch (ClassCastException e) {
                    Log.e(TAG, "bind: ", e);
                }
            }
        },
        ADVIEW {
            @Override
            int type() {
                return R.layout.item_adview;
            }

            @Override
            RecyclerView.ViewHolder viewHolder(ViewGroup parent, OnClickListener onClickListener) {
                View bannerLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.item_adview,
                        parent, false);
                return new AdViewHolder(bannerLayoutView, onClickListener);
            }

            @Override
            boolean is(Object item) {
                return item instanceof AdView;
            }

            @Override
            void bind(RecyclerView.ViewHolder holder, Object item) {
                ((AdViewHolder) holder).bind(holder, (AdView) item);
            }
        };

        static EItemType get(Object item) {
            for (EItemType itemType : EItemType.values()) {
                if (itemType.is(item)) {
                    return itemType;
                }
            }
            throw new RuntimeException("wrong Item class");
        }

        static EItemType get(int viewType) {
            for (EItemType itemType : EItemType.values()) {
                if (itemType.type() == viewType) {
                    return itemType;
                }
            }
            throw new RuntimeException("wrong ViewType");
        }

        abstract int type();

        abstract RecyclerView.ViewHolder viewHolder(ViewGroup parent, OnClickListener onClickListener);

        abstract boolean is(Object item);

        abstract void bind(RecyclerView.ViewHolder holder, Object item);
    }

    public interface OnClickListener {
        void onItemClick(View view, RateItem rateItem);

        void onFavoriteIconClick(View view, RateItem rateItem);
    }
}
