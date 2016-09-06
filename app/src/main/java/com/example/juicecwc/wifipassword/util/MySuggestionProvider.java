package com.example.juicecwc.wifipassword.util;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by juicecwc on 2016/9/6.
 */
//搜索记录类
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.juicecwc.wifipassword.util.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
