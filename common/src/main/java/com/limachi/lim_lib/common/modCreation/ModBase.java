package com.limachi.lim_lib.common.modCreation;

/*
public abstract class ModBase {
    private static final HashMap<String, ModBase> mods = new HashMap<>();

    public static ModBase getMod(String modId) {
        return mods.get(modId);
    }

    public static ModBase getMod(StackTraceElement trace) {
        LimLib.INSTANCE.logger.error("!!! test !!! " + trace.getModuleName());
        return null;
    }

    public static ModBase getMod(int depth) {
        var stack = Thread.currentThread().getStackTrace();
        if (stack.length > depth + 2)
            return getMod(stack[depth + 2]);
        return null;
    }

    public static ModBase getMod() {
        return getMod(1);
    }

    public Logger logger;
    public ModBase instance;
    public Registries registries;
    public AnnotationExtractor extractor;
    public ConfigManager configs = null;
    public Object client = null;

    public ModBase() {}

    private void extractMod(AnnotationExtractor extractor) {
        extractor.runOnClasses(Mod.class, (c, a)->{
            if (!ModBase.class.isAssignableFrom(c)) {
                System.err.println("@Mod should be used on a class that extends ModBase: " + c);
                System.exit(-1);
            }
            if (registries == null) {
                logger = LogManager.getLogger(a.value());
                configs = new ConfigManager(a.value(), extractor);
                registries = new Registries(a.value(), (Class<ModBase>)c);
            } else {
                System.err.println("@Mod is used multiple times: " + registries.mod + " & " + c);
                System.exit(-1);
            }
        });
        if (registries == null) {
            System.err.println("missing @Mod annotation");
            System.exit(-1);
        }
    }

    public void init(AnnotationExtractor extractor) {
        this.extractor = extractor;
        extractMod(extractor);
        extractor.runOnMethods(PreRegistries.class, (m, a)->m.getStatic());
        StaticInitializer.initialize(Stage.FIRST, true);
        StaticInitializer.initialize(Stage.FIRST, false);
        registries.extractInStages();
        instance = registries.initMod();
        registries.register();
        extractor.runOnMethods(ReloadListener.class, (m, a)->ReloadListenerRegistry.register(a.type(), (preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2) -> (CompletableFuture<Void>)m.get(null, true, preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2), ResourceLocation.fromNamespaceAndPath(registries.mod_id, Registries.defaultToMethod(a.value(), m))));
        CommandManager.register(extractor);
        StaticInitializer.initialize(Stage.LAST, true);
        StaticInitializer.initialize(Stage.LAST, false);
    }

    @Environment(EnvType.CLIENT)
    public ClientModBase getClientMod() {
        if (client instanceof ClientModBase c)
            return c;
        return null;
    }

    @Environment(EnvType.CLIENT)
    public static abstract class ClientModBase {
        public final ModBase mod;
        public ClientRegistries registries = new ClientRegistries(this);

        protected ClientModBase(ModBase mod) {
            this.mod = mod;
        }

        public void init() {
            StaticInitializer.initialize(ClientStage.FIRST, true);
            StaticInitializer.initialize(ClientStage.FIRST, false);
            registries.extractInStages();
            registries.register();
            ScrollHandler.register();
            StaticInitializer.initialize(ClientStage.LAST, true);
//            Platform.getMod(ModBase.registries.mod_id).registerConfigurationScreen(parent->new ConfigScreen(parent, configs));
            StaticInitializer.initialize(ClientStage.LAST, false);
        }
    }
}
*/