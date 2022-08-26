package com.limachi.lim_lib.saveData;

/**
 * <pre>
 * to be put on SyncSaveData classes that you want to register automatically
 * name will default to Class name transformed to snake case with 'save_data' and 'data' removed
 * sync defaults to SERVER_TO_CLIENT, see com.limachi.lim_lib.saveData.saveSync for more info
 * </pre>
 */
public @interface RegisterSaveData {
    String name() default "";
    SaveSync sync() default SaveSync.SERVER_TO_CLIENT;
}
