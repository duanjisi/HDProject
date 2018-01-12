package com.orhanobut.logger.strategy;

public interface FormatStrategy {

    void log(int priority, String tag, String message);
}
