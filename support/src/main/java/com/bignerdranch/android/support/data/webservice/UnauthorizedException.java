package com.bignerdranch.android.support.data.webservice;

import retrofit.RetrofitError;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(RetrofitError cause) {
        super(cause);
    }
}
