package apps.basilisk.officialhryvniarate.helper;

import java.util.Date;
import java.util.List;

import apps.basilisk.officialhryvniarate.model.entity.Nbu.Rate;
import retrofit2.Call;

public interface NbuHelper {
    Call<List<Rate>> getExchangeAll();

    Call<List<Rate>> getExchangeAll(Date date);

    Call<Rate> getExchange(String currencyCode);

    Call<Rate> getExchange(String currencyCode, Date date);
}
