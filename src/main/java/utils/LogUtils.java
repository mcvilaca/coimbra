package utils;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class LogUtils {

	public static void changeLog(Class<?> klass, Level level) {
	    Logger restClientLogger = (Logger) LoggerFactory.getLogger(klass);
	    restClientLogger.setLevel(level);
	}
}
