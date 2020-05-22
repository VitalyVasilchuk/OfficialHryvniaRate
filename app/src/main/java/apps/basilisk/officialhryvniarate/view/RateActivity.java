package apps.basilisk.officialhryvniarate.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import apps.basilisk.officialhryvniarate.BuildConfig;
import apps.basilisk.officialhryvniarate.R;
import apps.basilisk.officialhryvniarate.adapter.RateAdapter;
import apps.basilisk.officialhryvniarate.adapter.RateItem;
import apps.basilisk.officialhryvniarate.databinding.ActivityRateBinding;
import apps.basilisk.officialhryvniarate.helper.BillingHelper;
import apps.basilisk.officialhryvniarate.viewmodel.RateViewModel;


public class RateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String ADS_TEST_DEVICE_ID = "48D3555CDF229F4456E51C5760A61826";

    private ActivityRateBinding binding;
    private RateAdapter adapter;
    private RateViewModel viewModel;
    private boolean disabledAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rate);

        initRecyclerView();
        initViewModel();
    }

    private void initRecyclerView() {
        adapter = new RateAdapter(new RateAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, RateItem rateItem) {
                //Toast.makeText(RateActivity.this, "onItemClick(" + rateItem.getCodeLiteral() + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFavoriteIconClick(View view, RateItem rateItem) {
                viewModel.refresh();
            }

        });
        binding.rvRates.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRates.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(RateViewModel.class);
        viewModel.getRates().observe(this, this::updateUI);
        viewModel.getDisabledAds().observe(this, disabledAds -> {
            if (disabledAds) {
                hideAdsMob();
            }
            else {
                initAdsMob();
            }
        });
    }

    private void initAdsMob() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder()
                .setTestDeviceIds(Collections.singletonList(ADS_TEST_DEVICE_ID)).build());

        AdView adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.adaptive_banner_bottom_unit_id));
        binding.adViewContainer.removeAllViews();
        binding.adViewContainer.addView(adView);
        binding.adViewContainer.setVisibility(View.VISIBLE);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(getAdSize());
        adView.loadAd(adRequest);
        disabledAds = true;
    }

    private void hideAdsMob() {
        binding.adViewContainer.removeAllViews();
        binding.adViewContainer.setVisibility(View.GONE);
        disabledAds = false;
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rate_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchText(newText);
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                setItemsMenuVisibility(menu, searchItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                viewModel.setSearchText("");
                viewModel.refresh();
                setItemsMenuVisibility(menu, searchItem, true);
                return true;
            }
        });

        String q = viewModel.getSearchText().getValue();
        if (q != null && !q.isEmpty()) {
            searchItem.expandActionView();
            searchView.setQuery(q, true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_no_ads).setVisible(disabledAds);
        return true;
    }

    private void setItemsMenuVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_date_picker:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "date_picker");
                break;

            case R.id.action_no_ads:
                viewModel.launchBilling(this, BillingHelper.SKU_NO_ADS_1_WEEK);
                break;

            case R.id.action_about:
                showAbout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        String[] aboutMessageArray = {
                getString(R.string.about_subtitle),
                getString(R.string.about_version, BuildConfig.VERSION_NAME),
                getString(R.string.about_agreement)};

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setItems(aboutMessageArray, null)
                .setPositiveButton("OK", (dialog, i) -> dialog.dismiss());
        builder.create();
        builder.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        DateFormat.getDateInstance().format(c.getTime());

        viewModel.getRates(c.getTime()).observe(this, this::updateUI);
    }

    private void updateUI(List<RateItem> rates) {
        if (rates != null) {
            adapter.setRates(rates);

            ActionBar toolbar = getSupportActionBar();
            if (toolbar != null && rates.size() > 0) {
                toolbar.setTitle(String.format(getString(R.string.title_rate_on), rates.get(0).getDate()));
            }
        } else {
            Toast.makeText(this, getString(R.string.msg_data_not_available), Toast.LENGTH_SHORT).show();
        }
    }
}
