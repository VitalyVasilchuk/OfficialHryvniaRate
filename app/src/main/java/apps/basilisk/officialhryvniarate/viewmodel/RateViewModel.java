package apps.basilisk.officialhryvniarate.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

import apps.basilisk.officialhryvniarate.adapter.RateItem;
import apps.basilisk.officialhryvniarate.helper.BillingHelper;
import apps.basilisk.officialhryvniarate.model.Repository;
import apps.basilisk.officialhryvniarate.model.RepositoryImpl;

public class RateViewModel extends AndroidViewModel {
    private static final String TAG = "RateViewModel";
    private MutableLiveData<List<RateItem>> rates;
    private Repository repository;
    private MutableLiveData<String> searchText;
    private MutableLiveData<Boolean> disabledAds;
    private BillingHelper billingHelper;

    public RateViewModel(@NonNull Application application) {
        super(application);
        repository = new RepositoryImpl(getApplication().getApplicationContext());
        searchText = new MutableLiveData<>("");
        disabledAds = new MutableLiveData<>(true);

        initBilling();
    }

    private void initBilling() {
        billingHelper = new BillingHelper(purchase -> {
            disabledAds.setValue(purchase);
            Log.d(TAG, "initBilling: isPayComplete = " + disabledAds.getValue());
        });
        billingHelper.initBilling(getApplication());
    }

    public LiveData<List<RateItem>> getRates() {
        if (rates == null) {
            loadRates();
        }
        return rates;
    }

    public LiveData<List<RateItem>> getRates(Date date) {
        loadRates(date);
        return rates;
    }

    public MutableLiveData<String> getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText.setValue(searchText);
    }

    public LiveData<Boolean> getDisabledAds() {
        return disabledAds;
    }

    private void loadRates() {
        rates = repository.getRates();
    }

    private void loadRates(Date date) {
        rates = repository.getRates(date);
    }

    public void refresh() {
        if ("".equals(searchText.getValue())) {
            rates.setValue(rates.getValue());
        }
    }

    public void launchBilling(Activity activity, String skuId) {
        billingHelper.launchBilling(activity, skuId);
    }
}
