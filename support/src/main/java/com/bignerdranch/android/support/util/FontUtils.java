package com.bignerdranch.android.support.util;

public class FontUtils {
    // TODO does this need to move? explain assets/fonts
    private static final String FONT_ASSETS = "fonts/";
    public static final String BASE_TYPEFACE = FONT_ASSETS + "Font-Regular.otf";
    private static final String BASE_TYPEFACE_SEMI_BOLD = FONT_ASSETS + "Font-Semibold.otf";
    private static final String BASE_TYPEFACE_BOLD = FONT_ASSETS + "Font-Bold.otf";

//    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(4); // only store four objects
//
//    public static Typeface getSemiBoldTypeface(Context context) {
//        return getTypefaceFromKey(context, BASE_TYPEFACE_SEMI_BOLD);
//    }
//
//    public static Typeface getBoldTypeface(Context context) {
//        return getTypefaceFromKey(context, BASE_TYPEFACE_BOLD);
//    }
//
//    public static Typeface getRegularTypeface(Context context) {
//        return getTypefaceFromKey(context, BASE_TYPEFACE);
//    }
//
//    private static Typeface getTypefaceFromKey(Context context, String key) {
//        // try to get from cache
//        Typeface typeface = sTypefaceCache.get(key);
//
//        // if not in cache, create and add to cache
//        if (typeface == null) {
//            typeface = Typeface.createFromAsset(context.getAssets(), key);
//            sTypefaceCache.put(key, typeface);
//        }
//
//        return typeface;
//    }
}
