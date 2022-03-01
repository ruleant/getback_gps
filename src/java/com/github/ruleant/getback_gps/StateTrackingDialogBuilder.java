/**
 * StateTrackingDialogBuilder
 *
 * Copyright (C) 2022 Jan Scheible
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @package com.github.ruleant.getback_gps
 * @author Jan Scheible
 */
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
     * Create a new dialog builder with state tracking enabled.
     *
     * @param context is used for {@code AlertDialog.Builder}
     * @param statePrefix as prefix for all keys of the state
     * @param state is used to store the {@code TextView} texts
     */
    StateTrackingDialogBuilder(Context context, String statePrefix, Bundle state) {
        delegate = new AlertDialog.Builder(context);
        this.statePrefix = statePrefix;
        this.state = state;
    }

    /**
     * Calls {@link AlertDialog.Builder#setView(View)} under the hood and tracks state of all
     * {@code TextView} children.

     * @param view the view of the dialog
     * @return builder instance for chaining
     */
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

    /**
     * Tracks state of the {@code textView}. That includes initial state setting as well change
     * detection in case of UI changes.
     *
     * @param textView to track state
     * @param stateId key to use for the state bundle that was passed in the constructor
     */
    private void trackState(TextView textView, String stateId) {
        String stateTextValue = state.getString(stateId);
        if (stateTextValue != null) {
            textView.setText(stateTextValue);
        } else {
            state.putString(stateId, textView.getText().toString());
        }

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // only the final text is stored
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // only the final text is stored
            }

            @Override
            public void afterTextChanged(Editable s) {
                state.putString(stateId, textView.getText().toString());
            }
        });
    }

    /**
     * Calls {@link AlertDialog.Builder#setTitle(int)} under the hood.
     *
     * @param titleId the title of the dialog
     * @return builder instance for chaining
     */
    StateTrackingDialogBuilder setTitle(@StringRes int titleId) {
        delegate.setTitle(titleId);
        return this;
    }

    /**
     * Calls {@link AlertDialog.Builder#setPositiveButton(int, DialogInterface.OnClickListener)}
     * under the hood.
     *
     * @param textId the text of the button
     * @param listener the on click listener of the button
     * @return builder instance for chaining
     */
    StateTrackingDialogBuilder setPositiveButton(@StringRes int textId,
                                                 final DialogInterface.OnClickListener listener) {
        delegate.setPositiveButton(textId, listener);
        return this;
    }

    /**
     * Calls {@link AlertDialog.Builder#setNegativeButton(int, DialogInterface.OnClickListener)}
     * under the hood.
     *
     * @param textId the text of the button
     * @param listener the on click listener of the button
     * @return builder instance for chaining
     */
    StateTrackingDialogBuilder setNegativeButton(@StringRes int textId,
                                                 final DialogInterface.OnClickListener listener) {
        delegate.setNegativeButton(textId, (dialog, which) -> {
            state.clear();
            listener.onClick(dialog, which);
        });

        return this;
    }

    /**
     * Calls {@link AlertDialog.Builder#create()} under the hood.
     *
     * @return builder instance for chaining
     */
    AlertDialog create() {
        AlertDialog dialog = delegate.create();
        dialog.setOnDismissListener(listenerDialog -> state.clear());

        return dialog;
    }
}
