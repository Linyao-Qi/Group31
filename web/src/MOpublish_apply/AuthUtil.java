package com;

import jakarta.servlet.ServletContext;
import java.io.Serializable;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份认证工具类 - 适配CSV配置文件（5MO+2ADMIN）
 */
public class AuthUtil {

    // 路径：Web 应用根目录下的 data/auth.csv
    private static String AUTH_FILE_PATH = "data/auth.csv";

    // 全局认证Map
    private static final Map<String, String> MO_USER_MAP = new HashMap<>();
    private static final Map<String, String> ADMIN_USER_MAP = new HashMap<>();
    private static final Map<String, String> TA_USER_MAP = new HashMap<>();

    // 必须由 Web 项目初始化一次路径
    public static void init(ServletContext context) {
        AUTH_FILE_PATH = context.getRealPath("data/auth.csv");
        loadAuthDataFromCsv();
    }

    private static void loadAuthDataFromCsv() {
        List<Auth> authList = CsvFileUtil.readAuthListFromCsv(AUTH_FILE_PATH);
        for (Auth auth : authList) {
            if ("MO".equals(auth.getUserType())) {
                MO_USER_MAP.put(auth.getUserId(), auth.getPassword());
            } else if ("ADMIN".equals(auth.getUserType())) {
                ADMIN_USER_MAP.put(auth.getUserId(), auth.getPassword());
            } else if ("TA".equals(auth.getUserType())) {
                TA_USER_MAP.put(auth.getUserId(), auth.getPassword());
            }
        }
    }

    public static boolean authenticateTA(String taId, String password) {
        if (taId == null || taId.isBlank() || password == null || password.isBlank())
            return false;
        return password.equals(TA_USER_MAP.get(taId));
    }

    public static boolean authenticateMO(String moId, String password) {
        if (moId == null || moId.isBlank() || password == null || password.isBlank())
            return false;
        return password.equals(MO_USER_MAP.get(moId));
    }

    public static boolean authenticateAdmin(String adminId, String password) {
        if (adminId == null || adminId.isBlank() || password == null || password.isBlank())
            return false;
        return password.equals(ADMIN_USER_MAP.get(adminId));
    }

    public static boolean authenticate(String userId, String password, boolean isAdmin) {
        return isAdmin ? authenticateAdmin(userId, password) : authenticateMO(userId, password);
    }

    public static class Auth implements Serializable {
        private static final long serialVersionUID = 1L;
        private String userType;
        private String userId;
        private String password;

        public Auth() {}

        public Auth(String userType, String userId, String password) {
            this.userType = userType;
            this.userId = userId;
            this.password = password;
        }

        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}