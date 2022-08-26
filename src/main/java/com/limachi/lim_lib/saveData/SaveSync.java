package com.limachi.lim_lib.saveData;

/**
 * <pre>
 * SERVER_ONLY:      Default behavior of forge ->        this data is only to be stored and accessed by the server
 * SERVER_TO_CLIENT: Default for AbstractSyncSaveData -> this data is sync from the server to the client, allowing both to read but only the server can write
 * BOTH_WAY:         YOLO ->                             the server writes to the client but the client can modify it too
 * </pre>
 */
public enum SaveSync {
    SERVER_ONLY,
    SERVER_TO_CLIENT,
    BOTH_WAY
}
