package rpc.core.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.entity.RpcRequest;
import rpc.common.entity.RpcResponse;
import rpc.common.enumeration.ResponseCode;
import rpc.core.provider.ServiceProvider;
import rpc.core.provider.ServiceProviderImp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 远程过程调用处理器
 */
public class RequestHandler {
    private static final Logger logger= LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider sericeProvider;
    static {
        sericeProvider=new ServiceProviderImp();
    }
    public Object handle(RpcRequest request){
        Object service=sericeProvider.getServiceProvider(request.getInterfaceName());
        return invokeTargetMethod(request,service);
    }
    public Object invokeTargetMethod(RpcRequest request,Object service){
        Object result;
        try {
            Method method=service.getClass().getMethod(request.getMethodName(),request.getParamTypes());
            result=method.invoke(service,request.getParameters());
            logger.info("服务:{} 成功调用方法:{}", request.getInterfaceName(), request.getMethodName());
        }catch (NoSuchMethodException|IllegalAccessException| InvocationTargetException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND,request.getRequestId());
        }
        return result;
    }
}
