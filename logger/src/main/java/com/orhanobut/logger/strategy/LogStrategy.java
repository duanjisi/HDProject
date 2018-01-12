package com.orhanobut.logger.strategy;

public interface LogStrategy {

  void log(int priority, String tag, String message);
}
