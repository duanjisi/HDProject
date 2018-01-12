package com.orhanobut.logger.adapter;

import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.strategy.FormatStrategy;
import com.orhanobut.logger.strategy.PrettyFormatStrategy;

public class AndroidLogAdapter implements LogAdapter {

    private final FormatStrategy formatStrategy;

    public AndroidLogAdapter() {
        this.formatStrategy = PrettyFormatStrategy.newBuilder().build();
    }

    public AndroidLogAdapter(FormatStrategy formatStrategy) {
        this.formatStrategy = formatStrategy;
    }

    @Override
    public boolean isLoggable(int priority, String tag) {
        return BuildConfig.DEBUG;
    }

    @Override
    public void log(int priority, String tag, String message) {
        formatStrategy.log(priority, tag, message);
    }

}