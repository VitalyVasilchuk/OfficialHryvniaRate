package apps.basilisk.officialhryvniarate.binding;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import apps.basilisk.officialhryvniarate.R;

public class BindingAdapters {
    private static final String TAG = "BindingAdapters";

    private static final String RESOURCE_PREFIX = "flag_";
    private static final String RESOURCE_TYPE = "drawable";
    private static final int DOUBLE_FRACTION_DIGITS = 340;
    private static final float PROPORTION_FONT_SIZE = 0.9f;

    @BindingAdapter({"imageSrc"})
    public static void loadImage(ImageView view, String code) {
        String resourceName = RESOURCE_PREFIX + code;
        Resources resources = view.getContext().getResources();
        int resourceId = resources.getIdentifier(resourceName.toLowerCase(), RESOURCE_TYPE, view.getContext().getPackageName());
        view.setImageResource(resourceId);
    }

    @BindingAdapter({"spanMessage"})
    public static void spannableText(TextView view, double value) {
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(DOUBLE_FRACTION_DIGITS);
        String message = df.format(value);

        int count = 1;
        for (EDecimal d : EDecimal.values()) {
            if (value < d.value) {
                count = d.count;
                break;
            }
        }

        int endIdx = message.length();
        int startIdx = message.indexOf(".") + count;

        SpannableString partMessage = new SpannableString(message);
        if (startIdx > 0 && startIdx <= message.length()) {
            partMessage.setSpan(new ForegroundColorSpan(ContextCompat.getColor(view.getContext(), R.color.primary_text_transparent)),
                    startIdx, endIdx, 0);
            partMessage.setSpan(new StyleSpan(Typeface.NORMAL), startIdx, endIdx, 0);
            partMessage.setSpan(new RelativeSizeSpan(PROPORTION_FONT_SIZE), startIdx, endIdx, 0);
        }
        view.setText(partMessage);
    }

    enum EDecimal {
        D1(0.001, 6),
        D2(0.01, 5),
        D3(0.1, 4),
        D4(100, 3),
        D5(1000, 2);

        double value;
        int count;

        EDecimal(double value, int count) {
            this.value = value;
            this.count = count;
        }
    }
}
