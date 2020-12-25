package cn.com.topnetwork.dxd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableCaching
@MapperScan("cn.com.topnetwork.dxd.**.mapper")
public class SpringBootDxdApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDxdApplication.class, args);
    }

}
