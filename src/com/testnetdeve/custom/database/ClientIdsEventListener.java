package com.testnetdeve.custom.database;

import java.util.EventListener;
import java.util.HashSet;

public interface ClientIdsEventListener extends EventListener {
    void onClientIdsClone(HashSet<String> hashSet);
}
