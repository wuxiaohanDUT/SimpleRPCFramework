package rpc.core.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.enumeration.RpcError;
import rpc.common.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 *保存和提供服务实例对象
 */
public class ServiceProviderImp implements ServiceProvider{
    private static final Logger logger= LoggerFactory.getLogger(ServiceProviderImp.class);

    private static final Map<String,Object> serviceMap=new ConcurrentHashMap<>();
    private static final Set<String> registeredService=ConcurrentHashMap.newKeySet();

    /**
     * 在本地缓存中添加服务对象
     * @param service
     * @param serviceName
     * @param <T>
     */
    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if(registeredService.contains(serviceName)){
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName,service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    /**
     * 获取对应的服务
     * @param serviceName
     * @return
     */
    @Override
    public Object getServiceProvider(String serviceName) {
        Object service=serviceMap.get(serviceName);
        if(service==null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
