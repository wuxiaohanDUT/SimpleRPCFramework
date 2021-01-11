package rpc.core.transport;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.entity.RpcRequest;
import rpc.common.entity.RpcResponse;
import rpc.common.util.RpcMessageChecker;
import rpc.core.transport.netty.client.NettyClient;
import rpc.core.transport.socket.client.SocketClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 * Rpc客户端代理
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient){
        this.rpcClient=rpcClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法",method.getDeclaringClass().getName(),method.getName());
        RpcRequest request=new RpcRequest(UUID.randomUUID().toString(),method.getDeclaringClass().getName(),method.getName(),
                args,method.getParameterTypes(),false);
        RpcResponse response=null;
        if(rpcClient instanceof NettyClient){
            try{
                CompletableFuture<RpcResponse> completableFuture=(CompletableFuture<RpcResponse>)rpcClient.sendRequest(request);
                response=completableFuture.get();
            }catch (Exception e){
                logger.info("方法调用请求发送失败",e);
                return null;
            }
        }
        if(rpcClient instanceof SocketClient){
            response=(RpcResponse) rpcClient.sendRequest(request);
        }
        RpcMessageChecker.check(request,response);
        return response.getData();
    }
}
