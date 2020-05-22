package apps.basilisk.officialhryvniarate.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apps.basilisk.officialhryvniarate.adapter.RateItem;
import apps.basilisk.officialhryvniarate.helper.FavoriteHelper;
import apps.basilisk.officialhryvniarate.helper.NbuHelperImpl;
import apps.basilisk.officialhryvniarate.model.entity.Nbu.Rate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoryImpl implements Repository {
    private static final String TAG = "RepositoryImpl";
    private final Context context;

    public RepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public MutableLiveData<List<RateItem>> getRates(Date date) {
        MutableLiveData<List<RateItem>> mutableLiveData = new MutableLiveData<>();
        new NbuHelperImpl().getExchangeAll(date).enqueue(new Callback<List<Rate>>() {
            @Override
            public void onResponse(Call<List<Rate>> call, Response<List<Rate>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0 && response.body().get(0).getDate() != null) {
                        List<Rate> rates = response.body();
                        ArrayList<RateItem> items = new ArrayList<>(transformRates(rates));
                        mutableLiveData.setValue(items);
                        Log.d(TAG, String.format("getRates()::onResponse: %1s items loaded", rates.size()));
                    } else {
                        mutableLiveData.setValue(null);
                    }
                } else {
                    mutableLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Rate>> call, Throwable t) {
                Log.e(TAG, "getRates()::onFailure: ", t);
            }
        });

/*
        NetObservable.getRateItems(context, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rateItems -> {
                    mutableLiveData.setValue(rateItems);
                }, throwable -> {
                    Log.e(TAG, "getRates: ", throwable);
                })
        ;
*/
        return mutableLiveData;
    }

    @Override
    public MutableLiveData<List<RateItem>> getRates() {
        return getRates(new Date());
    }

    public List<RateItem> transformRates(List<Rate> rates) {
        List<String> favorites = FavoriteHelper.getFavorites(context);
        List<RateItem> rateItems = new ArrayList<>();
        for (Rate rate : rates) {
            rateItems.add(
                    new RateItem(
                            rate.getName(),
                            rate.getCodeDigital(),
                            rate.getCodeLiteral(),
                            rate.getValue(),
                            0,
                            rate.getDate(),
                            favorites.contains(rate.getCodeLiteral())
                    )
            );
        }

        return rateItems;
    }

}
