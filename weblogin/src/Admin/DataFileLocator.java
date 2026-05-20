package Admin;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Resolves the data directory used by admin services in local and web-server runtimes.
 *
 * @author Yutong Yao
 * @version 1.0
 */
public final class DataFileLocator {
    private static final String DATA_DIR_PROPERTY = "tajobsystem.data.dir";
    private static final String DATA_DIR_ENV = "TAJOBSYSTEM_DATA_DIR";
    private static final String WEBAPP_NAME_PROPERTY = "tajobsystem.webapp.name";
    private static final String DEFAULT_WEBAPP_NAME = "weblogin";

    private DataFileLocator() {
    }

    /**
     * Resolves a named data file below the selected data directory.
     *
     * @param filename data file name
     * @param anchorClass class used to locate the runtime classpath when needed
     * @return file reference for the requested data file
     */
    public static File resolveDataFile(String filename, Class<?> anchorClass) {
        File dataDir = resolveDataDirectory(anchorClass);
        return new File(dataDir, filename);
    }

    private static File resolveDataDirectory(Class<?> anchorClass) {
        String configuredByProperty = System.getProperty(DATA_DIR_PROPERTY);
        if (configuredByProperty != null && !configuredByProperty.trim().isEmpty()) {
            return ensureDirectory(new File(configuredByProperty.trim()));
        }

        String configuredByEnv = System.getenv(DATA_DIR_ENV);
        if (configuredByEnv != null && !configuredByEnv.trim().isEmpty()) {
            return ensureDirectory(new File(configuredByEnv.trim()));
        }

        // In Tomcat runtime, force Admin to use the same webapp data directory as MO:
        // <catalina.base>/webapps/<webapp-name>/data
        File tomcatDataDir = resolveTomcatWebappDataDir();
        if (tomcatDataDir != null && tomcatDataDir.exists()) {
            return ensureDirectory(tomcatDataDir);
        }

        // In web runtime, prefer WEB-INF/classes-near webapp data directory (same location as MO data).
        File classpathDataDir = resolveDataDirNearClasspath(anchorClass);
        if (classpathDataDir != null && classpathDataDir.exists()) {
            return ensureDirectory(classpathDataDir);
        }

        File projectDataDir = new File(System.getProperty("user.dir"), "data");
        if (projectDataDir.exists()) {
            return ensureDirectory(projectDataDir);
        }

        return ensureDirectory(projectDataDir);
    }

    private static File resolveTomcatWebappDataDir() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase == null || catalinaBase.trim().isEmpty()) {
            return null;
        }
        String webappName = System.getProperty(WEBAPP_NAME_PROPERTY, DEFAULT_WEBAPP_NAME).trim();
        if (webappName.isEmpty()) {
            webappName = DEFAULT_WEBAPP_NAME;
        }
        return new File(new File(new File(catalinaBase), "webapps"), webappName + File.separator + "data");
    }

    private static File resolveDataDirNearClasspath(Class<?> anchorClass) {
        try {
            URL location = anchorClass.getProtectionDomain().getCodeSource().getLocation();
            if (location == null) {
                return null;
            }

            File codeSource = new File(location.toURI());
            if (codeSource.isFile()) {
                return null;
            }

            File webAppRootData = new File(new File(codeSource, ".." + File.separator + ".."), "data");
            return webAppRootData.getCanonicalFile();
        } catch (URISyntaxException | java.io.IOException ignored) {
            return null;
        }
    }

    private static File ensureDirectory(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }
}

