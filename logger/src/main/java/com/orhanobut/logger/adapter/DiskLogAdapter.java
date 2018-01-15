package com.orhanobut.logger.adapter;

import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.strategy.CsvFormatStrategy;
import com.orhanobut.logger.strategy.FormatStrategy;

public class DiskLogAdapter implements LogAdapter {

    private final FormatStrategy formatStrategy;

    public DiskLogAdapter() {
        formatStrategy = CsvFormatStrategy.newBuilder().build();
    }

    public DiskLogAdapter(String logPath) {
        formatStrategy = CsvFormatStrategy.newBuilder()
                .path(logPath)
                .build();
    }

    public DiskLogAdapter(FormatStrategy formatStrategy) {
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