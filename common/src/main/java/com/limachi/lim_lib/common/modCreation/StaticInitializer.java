package com.limachi.lim_lib.common.modCreation;

import com.limachi.lim_lib.common.annotations.StaticInit;
import com.limachi.lim_lib.client.modCreation.ClientStage;
import com.limachi.lim_lib.client.annotations.StaticInitClient;
import com.limachi.lim_lib.common.reflect.AnnotationExtractor;

public class StaticInitializer {
    public static void initialize(AnnotationExtractor extractor, Stage stage, boolean before) {
        extractor.runOnMethods(StaticInit.class, (m, a)->{
            if (a.value() == stage && before == a.before())
                m.get(null, true);
        });
    }

    public static void initialize(AnnotationExtractor extractor, ClientStage stage, boolean before) {
        extractor.runOnMethods(StaticInitClient.class, (m, a)->{
            if (a.value() == stage && before == a.before())
                m.get(null, true);
        });
    }
}
