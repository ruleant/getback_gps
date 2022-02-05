package com.github.ruleant.getback_gps;

import android.os.Bundle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StateBundleHelperTest {

    @Test
    public void testFilter() {
        Bundle state = createBundleMock();
        state.putString("prefix-null", null);
        state.putString("abc-key", "xyz");
        state.putInt("prefix-int", 42);
        state.putString("prefix-string", ":-)");

        Bundle filtered = StateBundleHelper.filterStringValuesForPrefix(
                "prefix", state, createBundleMock());
        Assertions.assertEquals(new HashSet<>(Arrays.asList("prefix-string")), filtered.keySet());
    }

    /**
     * Creates a mocked {@code Bundle} that is backed by a {@code HashMap} instance.
     */
    private static Bundle createBundleMock() {
        Map<String, Object> storage = new HashMap<>();

        Answer<Void> storeSideEffect = invocation -> {
            String key = invocation.getArgument(0, String.class);
            Object value = invocation.getArgument(1, Object.class);
            storage.put(key, value);
            return null;
        };

        Bundle bundle = Mockito.mock(Bundle.class);

        Mockito.doAnswer(storeSideEffect).when(bundle).putString(Mockito.anyString(), Mockito.anyString());
        Mockito.doAnswer(storeSideEffect).when(bundle).putInt(Mockito.anyString(), Mockito.anyInt());
        Mockito.doAnswer(invocation -> storage.keySet()).when(bundle).keySet();
        Mockito.doAnswer(invocation -> storage.get(invocation.getArgument(0, String.class)))
                .when(bundle).get(Mockito.anyString());

        return bundle;
    }
}
