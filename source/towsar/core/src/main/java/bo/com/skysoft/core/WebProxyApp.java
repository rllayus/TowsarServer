package bo.com.skysoft.core;

import bo.com.skysoft.core.server.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by :MC4
 * Autor      :Ricardo Laredo
 * Email      :rlaredo@mc4.com.bo
 * Date       :11-04-19
 * Project    :WebProxy
 * Package    :bo.com.mc4.webproxyapp
 * Copyright  :MC4
 */
@Slf4j
public class WebProxyApp {
    public static void main(String []arg){
       //PropertyConfigurator.configure("log4j.properties");
        TimeZone.setDefault(TimeZone.getTimeZone("America/La_Paz"));
        log.info("****************************************************************************");
        log.info("**********************        Towsar 1.0              **********************");
        log.info("**********************            MC4                 **********************");
        log.info("**********************      http://towsar.com.bo      **********************");
        log.info("****************************************************************************");
        Map<String,String> env = System.getenv();
        Set<String> keys = env.keySet();
        for (String key: keys){
            //System.out.println(key + " = "+env.get(key));
        }
        new Server().start();
    }
}
