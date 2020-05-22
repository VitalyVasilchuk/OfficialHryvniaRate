package apps.basilisk.officialhryvniarate.model;

public class NetObservable {
/*
    public static Observable<List<Rate>> getRates(Date date) {
        Observable<List<Rate>> rates = NbuService.Factory.getObservableExchangeAll(date);
        return rates;
    }

    public static Observable<List<RateItem>> getRateItems(Context context, Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date datePrevious = cal.getTime();

        Observable<List<RateItem>> rateItems = Observable.zip(
                getRates(date), getRates(datePrevious),
                ((rates, ratesPrevious) -> {
                    HashMap<String, Rate> ratesMapPrevious = listToMap(ratesPrevious);
                    return transformRates(context, rates, ratesMapPrevious);
                })
        );
        return rateItems;
    }

    private static HashMap<String, Rate> listToMap(List<Rate> ratesList) {
        HashMap<String, Rate> ratesMap = new HashMap<>();
        for (Rate rate : ratesList) {
            ratesMap.put(rate.getCodeLiteral(), rate);
        }
        return ratesMap;
    }

    private static List<RateItem> transformRates(Context context, List<Rate> rates, HashMap<String, Rate> ratesMapPrevious) {
        List<String> favorites = FavoriteHelper.getFavorites(context);
        List<RateItem> rateItems = new ArrayList<>();
        for (Rate rate : rates) {
            double diff = ratesMapPrevious.containsKey(rate.getCodeLiteral()) ?
                    getDiff(ratesMapPrevious.get(rate.getCodeLiteral()).getValue(), rate.getValue()) : 0;
            rateItems.add(
                    new RateItem(
                            rate.getName(),
                            rate.getCodeDigital(),
                            rate.getCodeLiteral(),
                            rate.getValue(),
                            diff,
                            rate.getDate(),
                            favorites.contains(rate.getCodeLiteral())
                    )
            );
        }

        return rateItems;
    }

    private static double getDiff(Double value1, Double value2) {
        String[] splitter = value1.toString().split("\\.");
        int numDecimals = splitter[1].length();

        BigDecimal bdVal1 = new BigDecimal(value1);
        BigDecimal bdVal2 = new BigDecimal(value2);
        BigDecimal diff = bdVal1.subtract(bdVal2).setScale(numDecimals,BigDecimal.ROUND_HALF_EVEN);
        return diff.doubleValue();
    }

*/
}
