package rpc.core.transport.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.enumeration.RpcError;
import rpc.common.exception.RpcException;
import rpc.common.util.ReflectUtil;
import rpc.core.annotation.Service;
import rpc.core.annotation.ServiceScan;
import rpc.core.provider.ServiceProvider;
import rpc.core.registry.ServiceRegistry;
import rpc.core.transport.RpcServer;

import java.net.InetSocketAddress;
import java.util.Set;


public abstract class AbstractRpcServer implements RpcServer {
    protected Logger logger= LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    //服务注册
    protected ServiceRegistry serviceRegistry;
    //服务提供
    protected ServiceProvider serviceProvider;

    public void scanSercices(){
        String mainClassName= ReflectUtil.getStackTrace();
        Class<?> startClass;
        try{
            startClass=Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        }catch (ClassNotFoundException e){
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage=startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)){
            //默认扫描和启动类同一个包下的所有类
            basePackage=mainClassName.substring(0,mainClassName.lastIndexOf("."));
        }
        //获取对应包下的所有类的Class对象
        Set<Class<?>> classSet=ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz:classSet){
            //选出被注解修饰的对象，获取注解里的属性值
            if(clazz.isAnnotationPresent(Service.class)){
                String serviceName=clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj=clazz.newInstance();
                }catch (InstantiationException|IllegalAccessException e){
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)){//默认注册所有实现的接口
                    Class<?>[] interfaces=clazz.getInterfaces();
                    for(Class<?> oneInterface:interfaces){
                        publishService(obj,oneInterface.getCanonicalName());
                    }
                }else {
                    publishService(obj,serviceName);
                }
            }
        }
    }
    @Override
    public <T> void publishService(T service,String serviceName){
        serviceProvider.addServiceProvider(service,serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
