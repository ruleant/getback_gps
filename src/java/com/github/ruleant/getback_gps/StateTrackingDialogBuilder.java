package com.github.ruleant.getback_gps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.StringRes;

/**
 * Custom dialog builder that uses {@code AlertDialog.Builder} under the hood. The text values
 * of all {@code TextView} views are automatically synced with the state.
 * Also standard use cases as the negative button or dialog dismiss are covered and clear the state.
 */
class StateTrackingDialogBuilder {

    private final AlertDialog.Builder delegate;
    private final String statePrefix;
    private final Bundle state;

    /**
     * @param context is used for{@code AlertDialog.Builder}
     * @param statePrefix as prefix for all keys of the state
     * @param state is used to store the {@code TextView} texts
     */
    StateTrackingDialogBuilder(Context context, String statePrefix, Bundle state) {
        delegate = new AlertDialog.Builder(context);
        this.statePrefix = statePrefix;
        this.state = state;
    }

    StateTrackingDialogBuilder setView(View view) {
        delegate.setView(view);

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);

                if (child instanceof TextView) {
                    trackState((TextView) child, statePrefix + child.getId());
                }
            }
        }

        return this;
    }

    private void trackState(TextView textView, String stateId) {
        String stateTextValue = state.getString(stateId);
        if (stateTextValue != null) {
            textView.setText(stateTextValue);
        } else {
            state.putString(stateId, "");
        }

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                state.putString(stateId, textView.getText().toString());
            }
        });
    }

    StateTrackingDialogBuilder setTitle(@StringRes int titleId) {
        delegate.setTitle(titleId);
        return this;
    }

    StateTrackingDialogBuilder setPositiveButton(@StringRes int textId,
                                                 final DialogInterface.OnClickListener listener) {
        delegate.setPositiveButton(textId, listener);
        return this;
    }

    StateTrackingDialogBuilder setNegativeButton(@StringRes int textId,
                                                 final DialogInterface.OnClickListener listener) {
        delegate.setNegativeButton(textId, (dialog, which) -> {
            state.clear();
            listener.onClick(dialog, which);
        });

        return this;
    }

    AlertDialog create() {
        AlertDialog dialog = delegate.create();
        dialog.setOnDismissListener(listenerDialog -> state.clear());

        return dialog;
    }
}
