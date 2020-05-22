package apps.basilisk.officialhryvniarate.helper;


import java.util.Date;
import java.util.List;

import apps.basilisk.officialhryvniarate.rest.NbuService;
import apps.basilisk.officialhryvniarate.model.entity.Nbu.Rate;
import retrofit2.Call;

public class NbuHelperImpl implements NbuHelper {

    @Override
    public Call<List<Rate>> getExchangeAll() {
        return NbuService.Factory.getExchangeAll();
    }

    @Override
    public Call<List<Rate>> getExchangeAll(Date date) {
        return NbuService.Factory.getExchangeAll(date);
    }

    @Override
    public Call<Rate> getExchange(String currencyCode) {
        return NbuService.Factory.getExchange(currencyCode);
    }

    @Override
    public Call<Rate> getExchange(String currencyCode, Date date) {
        return NbuService.Factory.getExchange(currencyCode, date);
    }
}
