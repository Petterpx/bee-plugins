package com.bee.simple.test.update.router;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RouterNavigation {

    public static final RouterNavigation INSTANCE = new RouterNavigation();
    private static final Map<String, String> routerMap = new LinkedHashMap();

    private RouterNavigation() {
    }

    static {
        INSTANCE.initRouter();
    }

    private final void initRouter() {
    }
}