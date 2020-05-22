package apps.basilisk.officialhryvniarate.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import apps.basilisk.officialhryvniarate.model.entity.Nbu.Rate;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NbuService {
    String ENDPOINT = "https://bank.gov.ua/NBUStatService/v1/statdirectory/";

    @GET("exchange/")
    Call<List<Rate>> getExchangeAll(@Query("date") String date, @Query("json") String json);

    @GET("exchange/")
    Call<Rate> getExchange(@Query("valcode") String valcode, @Query("date") String date, @Query("json") String json);

    @GET("exchange/")
    Observable<List<Rate>> getObservableExchangeAll(@Query("date") String date, @Query("json") String json);

    class Factory {
        private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        private static NbuService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit.create(NbuService.class);
        }

        public static Call<List<Rate>> getExchangeAll() {
            return create().getExchangeAll(df.format(System.currentTimeMillis()), "");
        }

        public static Call<List<Rate>> getExchangeAll(Date date) {
            return create().getExchangeAll(df.format(date), "");
        }

        public static Call<Rate> getExchange(String currencyCode) {
            return create().getExchange(currencyCode, df.format(System.currentTimeMillis()), "");
        }

        public static Call<Rate> getExchange(String currencyCode, Date date) {
            return create().getExchange(currencyCode, df.format(date), "");
        }

        public static Observable<List<Rate>> getObservableExchangeAll(Date date) {
            return create().getObservableExchangeAll(df.format(date), "");
        }
    }
}
