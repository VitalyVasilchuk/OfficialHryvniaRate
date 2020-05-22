package apps.basilisk.officialhryvniarate.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingHelper {
    private static final String TAG = "BillingHelper";
    public static final String SKU_NO_ADS_1_WEEK = "no_ads_1_week";
    public static final String SKU_NO_ADS_1_MONTH = "no_ads_1_month";

    private BillingClient billingClient;
    private Map<String, SkuDetails> skuDetailsMap = new HashMap<>();
    private OnPayComplete payCompleteListener;

    public BillingHelper(OnPayComplete payCompleteListener) {
        this.payCompleteListener = payCompleteListener;
    }

    public void initBilling(Context context) {
        billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingResponseCode.OK && purchases != null) {
                        payComplete(purchases);
                    } else if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED) {
                        // Handle an error caused by a user cancelling the purchase flow.
                    } else {
                        // Handle any other error codes.
                    }
                }).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                    querySkuDetails();
                    payComplete(queryPurchases());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

    }

    private void querySkuDetails() {
        List<String> skuList = new ArrayList<>();
        skuList.add(SKU_NO_ADS_1_WEEK);
        skuList.add(SKU_NO_ADS_1_MONTH);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingResponseCode.OK && skuDetailsList != null) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    skuDetailsMap.put(skuDetails.getSku(), skuDetails);
                }
            }
        });
    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(SkuType.SUBS);
        return purchasesResult.getPurchasesList();
    }

    public void launchBilling(Activity activity, String skuId) {
        SkuDetails skuDetails = skuDetailsMap.get(skuId);
        if (skuDetails != null) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            billingClient.launchBillingFlow(activity, billingFlowParams);
        }
    }

    private void payComplete(List<Purchase> purchases) {
        if (purchases == null || purchases.size() == 0) {
            if (payCompleteListener != null) {
                payCompleteListener.onPayComplete(false);
            }
        } else {
            for (Purchase purchase : purchases) {
                Log.d(TAG, "payComplete: " + purchase.toString());
                if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) continue;
                switch (purchase.getSku()) {
                    case SKU_NO_ADS_1_WEEK:
                    case SKU_NO_ADS_1_MONTH:
                        if (payCompleteListener != null) {
                            payCompleteListener.onPayComplete(true);
                        }
                        break;
                }
            }
        }
    }
    public void endConnection(){
        billingClient.endConnection();
    }

    public interface OnPayComplete {
        void onPayComplete(boolean isPayComplete);
    }
}
