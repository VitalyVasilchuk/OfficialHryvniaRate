package apps.basilisk.officialhryvniarate.model;

import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

import apps.basilisk.officialhryvniarate.adapter.RateItem;

public interface Repository {
    MutableLiveData<List<RateItem>> getRates();

    MutableLiveData<List<RateItem>> getRates(Date date);
}
