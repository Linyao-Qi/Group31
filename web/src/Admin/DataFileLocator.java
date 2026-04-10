package Admin;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;


public final class DataFileLocator {
    private static final String DATA_DIR_PROPERTY = "tajobsystem.data.dir";
    private static final String DATA_DIR_ENV = "TAJOBSYSTEM_DATA_DIR";
    private static final String WEBAPP_NAME_PROPERTY = "tajobsystem.webapp.name";

    private DataFileLocator() {
    }

    public static File resolveDataFile(String filename, Class<?> anchorClass) {
        File dataDir = resolveDataDirectory(anchorClass);
        return new File(dataDir, filename);
    }

    public static File resolveWebDataFile(String filename, Class<?> anchorClass) {
        File webDataDir = resolveWebDataDirectory(anchorClass);
        return new File(webDataDir, filename);
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

    private static File resolveWebDataDirectory(Class<?> anchorClass) {
        File webDataFromTomcat = resolveTomcatWebappWebDataDir();
        if (webDataFromTomcat != null) {
            return ensureDirectory(webDataFromTomcat);
        }

        try {
            URL location = anchorClass.getProtectionDomain().getCodeSource().getLocation();
            if (location != null) {
                File codeSource = new File(location.toURI());
                if (!codeSource.isFile()) {
                    File webAppRoot = new File(new File(codeSource, ".." + File.separator + ".."), "");
                    return ensureDirectory(new File(webAppRoot.getCanonicalFile(), "web" + File.separator + "data"));
                }
            }
        } catch (URISyntaxException | java.io.IOException ignored) {
        }

        return ensureDirectory(new File(System.getProperty("user.dir"), "web" + File.separator + "data"));
    }

    private static File resolveTomcatWebappDataDir() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase == null || catalinaBase.trim().isEmpty()) {
            return null;
        }
        String webappName = System.getProperty(WEBAPP_NAME_PROPERTY);
        if (webappName == null || webappName.trim().isEmpty()) {
            // No explicit webapp name configured. Let caller fall back to classpath-based data resolution.
            return null;
        }
        webappName = webappName.trim();
        return new File(new File(new File(catalinaBase), "webapps"), webappName + File.separator + "data");
    }

    private static File resolveTomcatWebappWebDataDir() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase == null || catalinaBase.trim().isEmpty()) {
            return null;
        }
        String webappName = System.getProperty(WEBAPP_NAME_PROPERTY);
        if (webappName == null || webappName.trim().isEmpty()) {
            return null;
        }
        webappName = webappName.trim();
        return new File(new File(new File(catalinaBase), "webapps"), webappName + File.separator + "web" + File.separator + "data");
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

