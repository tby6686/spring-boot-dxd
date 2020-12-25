package cn.com.topnetwork.dxd.util;

import com.google.common.base.CaseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

/**
 * 
 * <p>
 * 反射工具类
 * </p>
 *
 * @author tby
 * @since 2020年2月18日
 */
public class ReflectionUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 
     * @description spring管理的Env中获取属性 配置到静态变量中
     * @param <T>
     * @param env
     * @param prefix
     * @param cls
     */
    public static <T> void staticFieldSetWithEnv(Environment env, String prefix, Class<T> cls) {
        try {
            // 因为 是为静态变量赋值，所以这里使用getFields方法
            Field[] fields = cls.getFields();
            for (Field field : fields) {
                // 非final 并且为static的 属性
                if (!Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                    // 驼峰互转
                    String proName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.getName());
                    String proVal = env.getProperty(prefix + proName);

                    // 获取field类型
                    Class<?> type = field.getType();
                    if (type.equals(String.class)) {
                        field.set(null, proVal);
                    } else if (type.equals(int.class) || type.equals(Integer.class)) {
                        field.set(null, Integer.valueOf(proVal));
                    } else if (type.equals(long.class) || type.equals(Long.class)) {
                        field.set(null, Long.valueOf(proVal));
                    } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                        field.set(null, Boolean.parseBoolean(proVal));
                    } else if (type.equals(BigDecimal.class)) {
                        field.set(null, new BigDecimal(proVal));
                    } else {
                        field.set(null, proVal);
                    }

                }
            }

        } catch (Exception e) {
            logger.error(e.getStackTrace()[0].getMethodName() + " exception.", e);
        }

    }

}
