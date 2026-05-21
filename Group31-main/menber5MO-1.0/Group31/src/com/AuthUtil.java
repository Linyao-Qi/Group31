package com;

import java.io.File;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 身份认证工具类 - 适配CSV配置文件（5MO+2ADMIN）
 */
public class AuthUtil {
    // 认证配置文件路径（与job/application.csv同级）
	private static final String PROJECT_ABSOLUTE_PATH = "D:/soft/eclipse_2020.12/data/Group31";
	private static final String AUTH_FILE_PATH = PROJECT_ABSOLUTE_PATH + "/data/auth.csv";


    // 全局认证Map：key=用户ID，value=密码；分MO和ADMIN两个Map，提升认证效率
    private static final Map<String, String> MO_USER_MAP = new HashMap<>();
    private static final Map<String, String> ADMIN_USER_MAP = new HashMap<>();

    // 静态代码块：项目启动时加载CSV中的认证信息到Map
    static {
        loadAuthDataFromCsv();
    }

    /**
     * 从CSV读取认证数据并初始化MO/ADMIN Map
     */
    private static void loadAuthDataFromCsv() {
        List<Auth> authList = CsvFileUtil.readAuthListFromCsv(AUTH_FILE_PATH);
        for (Auth auth : authList) {
            if ("MO".equals(auth.getUserType())) {
                MO_USER_MAP.put(auth.getUserId(), auth.getPassword());
            } else if ("ADMIN".equals(auth.getUserType())) {
                ADMIN_USER_MAP.put(auth.getUserId(), auth.getPassword());
            }
        }
    }

    /**
     * MO身份认证
     * @param moId MO编号
     * @param password 密码
     * @return 认证是否通过
     */
    public static boolean authenticateMO(String moId, String password) {
        if (moId == null || moId.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        // 密码匹配则认证通过
        return password.equals(MO_USER_MAP.get(moId));
    }

    /**
     * 管理员身份认证
     * @param adminId 管理员编号
     * @param password 密码
     * @return 认证是否通过
     */
    public static boolean authenticateAdmin(String adminId, String password) {
        if (adminId == null || adminId.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        // 密码匹配则认证通过
        return password.equals(ADMIN_USER_MAP.get(adminId));
    }

    /**
     * 通用认证方法（支持MO/管理员）
     * @param userId 用户ID（moId/adminId）
     * @param password 密码
     * @param isAdmin 是否是管理员
     * @return 认证是否通过
     */
    public static boolean authenticate(String userId, String password, boolean isAdmin) {
        return isAdmin ? authenticateAdmin(userId, password) : authenticateMO(userId, password);
    }

    /**
     * 内部静态类：封装认证信息（与auth.csv字段对应）
     */
    public static class Auth implements Serializable {
        private static final long serialVersionUID = 1L;
        private String userType; // MO/ADMIN
        private String userId;   // moXXX/adminXXX
        private String password;// 密码

        // 无参/有参构造
        public Auth() {}
        public Auth(String userType, String userId, String password) {
            this.userType = userType;
            this.userId = userId;
            this.password = password;
        }

        // 全量Getter&Setter
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}