package com.bignerdranch.android.support.data.event;

public class FailureEvent extends BaseEvent {

//    protected ErrorResponseList mErrorResponseList;
    protected String mErrorDescription;
    protected int mStatusCode;
    protected boolean mIsConnectedToInternet;

    public FailureEvent() {
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int statusCode) {
        mStatusCode = statusCode;
    }

//    public ErrorResponseList getErrorResponseList() {
//        return mErrorResponseList;
//    }
//
//    public void setErrorResponseList(ErrorResponseList errorResponseList) {
//        mErrorResponseList = errorResponseList;
//    }

    public boolean isConnectedToInternet() {
        return mIsConnectedToInternet;
    }

    public void setIsConnectedToInternet(boolean isConnectedToInternet) {
        mIsConnectedToInternet = isConnectedToInternet;
    }

    public String getErrorDescription() {
        // if we have error description return it
        if (mErrorDescription != null) {
            return mErrorDescription;
        }

        // if we have an error response list, just return all as one string
//        String description = "";
//        if (mErrorResponseList != null && !mErrorResponseList.isEmpty()) {
//            List<ErrorResponse> errors = mErrorResponseList.getErrors();
//            for (ErrorResponse error : errors) {
//                description = description + error.getDescription() + "\n";
//            }
//            return description;
//        }


        // as a last resort return class name
        return getClass().getSimpleName();
    }

    public void setErrorDescription(String errorDescription) {
        mErrorDescription = errorDescription;
    }
}

