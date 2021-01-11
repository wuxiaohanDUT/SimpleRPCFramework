package rpc.common.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.entity.RpcRequest;
import rpc.common.entity.RpcResponse;
import rpc.common.enumeration.ResponseCode;


/**
 * 检查响应与请求
 */
public class RpcMessageChecker {

    private static final String INTERFACE_NAME="interfacename";
    private static final Logger logger=LoggerFactory.getLogger(RpcMessageChecker.class);

    public static void check(RpcRequest request, RpcResponse response){
        if(response==null){
            logger.error("调用服务失败,serviceName:{}", request.getInterfaceName());
        }
        if(request.getRequestId()!=response.getRequestId()){

        }
        if(response.getStatusCode()==null||!response.getStatusCode().equals(ResponseCode.SUCCESS)){
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", request.getInterfaceName(), response);
        }
    }
}
