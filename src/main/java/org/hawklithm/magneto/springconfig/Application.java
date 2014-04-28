package org.hawklithm.magneto.springconfig;

import org.hawklithm.magneto.zookeeper.ZookeeperConnector;
import org.hawklithm.magneto.zookeeper.ZookeeperConnectorImpl;
import org.hawklithm.magneto.zookeeper.ZookeeperListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class Application {
	public static ZookeeperConnector connector=new ZookeeperConnectorImpl();
	public static ZookeeperListener listener=new ZookeeperListener();
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	@Bean(name="connector")
	public ZookeeperConnector getConnector(){
		return connector;
	}
	@Bean(name="listener")
	public ZookeeperListener getListener(){
		return listener;
	}
	
	
}
