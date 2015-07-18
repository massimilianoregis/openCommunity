package org.opencommunity;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;


@Configuration
@ComponentScan(basePackages="org.opencommunity")
@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)
//@EnableJpaRepositories
//@EnableAspectJAutoProxy
//@EnableSpringConfigured
public class CommunityApplication extends SpringBootServletInitializer 
{
	@Override
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder application) 
		{
		return application.sources(CommunityApplication.class);
		}
	
    public static void main(String[] args) {
    //	System.setProperty("spring.profiles.default", System.getProperty("spring.profiles.default", "dev"));
        ConfigurableApplicationContext ctx =SpringApplication.run(CommunityApplication.class, args);
        
//        Admin admin = (Admin)ctx.getBean("admin");
//        System.out.println("-------->"+admin.getCommunity(null));
//if(true) return;
//        System.out.println("-----------------------------------------");
//        MultipleDBase multi = new MultipleDBase();
//        	multi.add("ekaros", "org.h2.Driver", "jdbc:h2:file:/Users/max/Documents/data/ekaros;mode=db2", "test", "test");
//        	multi.add("moriana", "org.h2.Driver", "jdbc:h2:file:/Users/max/Documents/data/moraina;mode=db2", "test", "test");
    }
}