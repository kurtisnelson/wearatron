package com.thisisnotajoke.lockitron.model.event;

import retrofit.RetrofitError;


public class UnauthorizedException extends Throwable {
    public UnauthorizedException(RetrofitError cause) {
    }
}