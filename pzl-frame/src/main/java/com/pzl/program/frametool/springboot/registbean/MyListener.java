package com.pzl.program.frametool.springboot.registbean;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 自定义 Listener 监听器
 * <p>
 * 1.	监听当前应用ServletContext对象
 * 	javax.servlet.ServletContextListener
 * 	监听ServletContext对象创建和销毁.
 * 	应用场景:
 * 	在服务器启动时建立数据库表结构,初始化数据库.
 * 	在服务器启动时,将数据库常量数据加载到内存,提供访问效率.
 * 	在服务器启动时,获取项目上下文路径,存放到application域,给页面使用.
 * 	存放计数器,计算在线用户数.
 * 	javax.servlet.ServletContextAttributeListener
 * 	监听ServletContext对象的属性的变化:添加,覆盖,删除
 * 2.	监听器HttpSession对象
 * 	javax.servlet.http.HttpSessionListener
 * 	监听HttpSession对象的创建和销毁
 * 	javax.servlet.http.HttpSessionAttributeListener
 * 	监听HttpSession对象的属性变化:添加,覆盖,删除
 * 3.	监听HttpServletRequest对象
 * 	javax.servlet.ServletRequestListener
 * 	监听HttpServletRequest对象的创建和销毁
 * 	javax.servlet.ServletRequestAttributeListener
 * 	监听HttpServletRequest对象的属性变化:添加,覆盖,删除
 *
 * @author pzl
 * @date 2020-04-16
 */
public class MyListener implements ServletContextListener {

    /**
     * 初始化:服务器启动时，加载项目时被创建。
     *
     * @param servletContextEvent servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //上下文对象，全局域对象
        ServletContext servletContext = servletContextEvent.getServletContext();
        //项目路径
        String contextPath = servletContext.getContextPath();
        //保存变量，全局项目中都可使用
        servletContext.setAttribute("path", contextPath);
        System.out.print("contextInitialized...项目启动,项目路径" + contextPath);
    }

    /**
     * 销毁，服务器停止时或卸载项目时，对象被销毁
     *
     * @param servletContextEvent servletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.print("contextInitialized...项目销毁");
    }

}