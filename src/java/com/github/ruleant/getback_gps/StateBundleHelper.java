package com.github.ruleant.getback_gps;

import android.os.Bundle;

import java.util.function.Supplier;

class StateBundleHelper {

    /**
     * @param prefix is used for identifying relevant keys
     * @param state is the input state
     * @return @return New bundle with all non-null String entries with a key that have the prefix.
     */
    static Bundle filterStringValuesForPrefix(String prefix, Bundle state) {
        return filterStringValuesForPrefix(prefix, state, new Bundle());
    }

    static Bundle filterStringValuesForPrefix(String prefix, Bundle state, Bundle result) {
        for (final String stateKey : state.keySet()) {
            if (stateKey.startsWith(prefix)) {
                Object stateValue = state.get(stateKey);

                if(stateValue instanceof String) {
                    result.putString(stateKey, stateValue.toString());
                }
            }
        }

        return result;
    }
}
