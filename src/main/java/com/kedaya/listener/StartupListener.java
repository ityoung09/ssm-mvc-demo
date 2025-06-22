package com.kedaya.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== ServletContext 初始化时的参数检查 ===");

        // 检查 JVM 系统属性
        System.out.println("JVM 参数 spring.profiles.active: " +
                System.getProperty("spring.profiles.active"));
        System.out.println("JVM 参数 logback.configurationFile: " +
                System.getProperty("logback.configurationFile"));

        // 检查 web.xml 中的 context-param
        String webXmlProfile = sce.getServletContext().getInitParameter("spring.profiles.active");
        String webXmlLogback = sce.getServletContext().getInitParameter("logbackConfigLocation");

        System.out.println("web.xml context-param spring.profiles.active: " + webXmlProfile);
        System.out.println("web.xml context-param logbackConfigLocation: " + webXmlLogback);

        // 尝试从 web.xml 设置到系统属性（对 Logback 来说已经太晚了）
        if (System.getProperty("spring.profiles.active") == null && webXmlProfile != null) {
            System.setProperty("spring.profiles.active", webXmlProfile);
            System.out.println("从 web.xml 设置 spring.profiles.active 到系统属性: " + webXmlProfile);
        }

        System.out.println("=== 参数检查完成 ===");
        System.out.println("注意：此时设置系统属性对 Logback 已经无效，因为 Logback 已经初始化完成");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 清理资源
    }
}