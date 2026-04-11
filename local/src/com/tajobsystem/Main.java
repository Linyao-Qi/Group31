package com.tajobsystem; // 因为它和 ui 文件夹平级，所以这里没有 .ui

import com.tajobsystem.model.TAProfile;
import com.tajobsystem.ui.TAJobsFrame; // 必须导入 ui 包下的界面
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // 伪造一个登录的 TA 账号，用于测试
            TAProfile dummyTA = new TAProfile();
            dummyTA.setTaId("TA001");
            dummyTA.setName("Test Student");
            dummyTA.setEmail("test@bupt.edu");
            dummyTA.setSkills("Java, Python");

            // 启动你的岗位查看界面，并把假账号传进去
            TAJobsFrame frame = new TAJobsFrame(dummyTA);
            frame.setVisible(true);

        });
    }
}