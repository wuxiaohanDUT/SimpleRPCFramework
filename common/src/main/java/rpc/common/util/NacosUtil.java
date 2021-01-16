package rpc.common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *管理Nacos连接等工具类
 */
public class NacosUtil {
    private static final Logger logger= LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingservice;
    private static final Set<String> serviceNames=new HashSet<>();
    private static  InetSocketAddress address;

    private static final String SERVER_ADDR="127.0.0.1:8848";

    static {
        namingservice=getNacosNamingService();
    }

    /**
     * 获取Nacos名称服务对象
     * @return
     */
    public static NamingService getNacosNamingService(){
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        }catch (NacosException e){
            logger.info("连接到Nacos时有错误发生：",e);
        }
        return null;
    }

    /**
     * 注册服务
     * @param servicename
     * @param address
     * @throws NacosException
     */
    public static void registerService(String servicename,InetSocketAddress address) throws NacosException{
        namingservice.registerInstance(servicename,address.getHostName(),address.getPort());
        NacosUtil.address=address;
        serviceNames.add(servicename);
    }

    /**
     * 获取所有提供服务的实例对象
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException{
        return namingservice.getAllInstances(serviceName);
    }

    /**
     * 注销所有服务
     */
    public static void clearRegistry(){
        if(!serviceNames.isEmpty()&&address!=null){
            String host=address.getHostName();
            int port=address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()){
                String servicename= iterator.next();
                try {
                    namingservice.deregisterInstance(servicename,host,port);
                }catch (NacosException e){
                    logger.error("注销服务{}失败",e);
                }
            }
        }
    }
}
