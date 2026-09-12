package cn.edu.czjtxy.sjypeploemanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.czjtxy.sjypeploemanage.mapper")
public class SjyPeploeManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SjyPeploeManageApplication.class, args);
	}

}