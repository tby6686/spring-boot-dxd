 package cn.com.topnetwork.dxd.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (ApplicationContextUtil.applicationContext == null) {
            ApplicationContextUtil.applicationContext = applicationContext;
        }
    }

    // 获取applicationContext
    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBeanByClassPath(String classPath, Class<T> clazz) {
        try{
            String className = classPath.substring(classPath.lastIndexOf(".")+1);

            String springBeanName = className.substring(0,1).toLowerCase().concat(className.substring(1));

            return getApplicationContext().getBean(springBeanName, clazz);

        }catch (Exception e){
            return null;
        }

    }

}
