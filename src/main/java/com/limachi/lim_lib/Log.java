package com.limachi.lim_lib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Log {
    public static final Logger LOGGER = LogManager.getLogger(LimLib.class);

    @Configs.Config(cmt = "do debug calls actually log something. set to false to silence all debug calls")
    public static boolean DO_DEBUG = true;

    @Configs.Config(cmt = "send the debug calls through the Info log instead of Debug log")
    public static boolean DEBUG_AS_INFO = true;

    public static Consumer<String> debugConsumer() { return DEBUG_AS_INFO ? LOGGER::info : LOGGER::debug; }

    public static void debug(String s) { if (DO_DEBUG) debugConsumer().accept(Thread.currentThread().getStackTrace()[2].toString() + " " + s); }
    public static <T> T debug(T v, String s) { if (DO_DEBUG) debugConsumer().accept(Thread.currentThread().getStackTrace()[2].toString() + " V: " + v + " : "+ s); return v; }
    public static <T> T debug(T v) { if (DO_DEBUG) debugConsumer().accept(Thread.currentThread().getStackTrace()[2].toString() + " V: " + v); return v; }
    public static <T> T debug(T v, int depth) { if (DO_DEBUG) debugConsumer().accept(Thread.currentThread().getStackTrace()[2 + depth].toString() + " V: " + v); return v; }
    public static <T> T debug(T v, int depth, String s) { if (DO_DEBUG) debugConsumer().accept(Thread.currentThread().getStackTrace()[2 + depth].toString() + " V: " + v + " : "+ s); return v; }

    public static void info(String s) { LOGGER.info(s); }
    public static <T> T info(T v, String s) { LOGGER.info(" V: " + v + " : "+ s); return v; }
    public static <T> T info(T v) { LOGGER.info("V: " + v); return v; }

    public static void warn(String s) { LOGGER.warn(Thread.currentThread().getStackTrace()[2].toString() + " "+ s); }
    public static <T> T warn(T v, String s) { LOGGER.warn(Thread.currentThread().getStackTrace()[2].toString() + " V: " + v + " : "+ s); return v; }
    public static <T> T warn(T v) { LOGGER.warn(Thread.currentThread().getStackTrace()[2].toString() + " V: " + v); return v; }
    public static <T> T warn(T v, int depth) { LOGGER.warn(Thread.currentThread().getStackTrace()[2 + depth].toString() + " V: " + v); return v; }
    public static <T> T warn(T v, int depth, String s) { LOGGER.warn(Thread.currentThread().getStackTrace()[2 + depth].toString() + " V: " + v + " : "+ s); return v; }

    public static void error(String s) { LOGGER.error(Thread.currentThread().getStackTrace()[2].toString() + " "+ s); }
    public static <T> T error(T v, String s) { LOGGER.error(Thread.currentThread().getStackTrace()[2].toString() + " V: " + v + " : "+ s); return v; }
    public static <T> T error(T v) { LOGGER.error(Thread.currentThread().getStackTrace()[2].toString() + " V: " + v); return v; }
    public static <T> T error(T v, int depth) { LOGGER.error(Thread.currentThread().getStackTrace()[2 + depth].toString() + " V: " + v); return v; }
    public static <T> T error(T v, int depth, String s) { LOGGER.error(Thread.currentThread().getStackTrace()[2 + depth].toString() + " V: " + v + " : "+ s); return v; }
}
