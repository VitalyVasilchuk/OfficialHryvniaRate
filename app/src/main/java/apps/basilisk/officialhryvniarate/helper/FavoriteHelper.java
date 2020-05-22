package apps.basilisk.officialhryvniarate.helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteHelper {
    private static final String FAVORITES = "FAVORITES";

    public static void saveFavorite(Context context, List<String> favorites) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);
        editor.apply();
    }

    public static void addFavorite(Context context, String favoriteItem) {
        List<String> favorites = getFavorites(context);
        favorites.add(favoriteItem);
        saveFavorite(context, favorites);
    }

    public static void removeFavorite(Context context, String favoriteItem) {
        ArrayList<String> favorites = getFavorites(context);
        favorites.remove(favoriteItem);
        saveFavorite(context, favorites);
    }

    public static ArrayList<String> getFavorites(Context context) {
        List<String> favorites;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            String[] favoriteItems = gson.fromJson(jsonFavorites, String[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else {
            favorites = new ArrayList<>();
        }
        return (ArrayList<String>) favorites;
    }
}
