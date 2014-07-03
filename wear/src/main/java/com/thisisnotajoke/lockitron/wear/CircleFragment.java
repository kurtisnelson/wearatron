package com.thisisnotajoke.lockitron.wear;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CircleFragment extends android.app.Fragment {

    private static final String LOCK_ARG = "LockArg";
    private boolean mLock;
    private Callback mCallback;

    public interface Callback {
        public void onClick(boolean lock);
    }

    public static CircleFragment newInstance(boolean lock) {
        CircleFragment f = new CircleFragment();
        Bundle args = new Bundle();
        args.putBoolean(LOCK_ARG, lock);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLock = getArguments().getBoolean(LOCK_ARG, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_lock, container, false);
        CircledImageView view = (CircledImageView) layout.findViewById(R.id.fragment_lock_image);
        TextView text = (TextView) layout.findViewById(R.id.fragment_lock_text);
        if(mLock){
            view.setImageResource(R.drawable.lock);
            text.setText(R.string.lock);
        }else{
            view.setImageResource(R.drawable.unlock);
            text.setText(R.string.unlock);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onClick(mLock);
            }
        });
        return layout;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
}
