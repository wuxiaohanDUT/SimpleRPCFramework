package rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.exception.RpcException;
import rpc.common.util.NacosUtil;

import java.net.InetSocketAddress;

import static rpc.common.enumeration.RpcError.REGISTER_SERVICE_FAILED;

/**
 * Nacos服务注册中心
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger= LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName,inetSocketAddress);
        }catch (NacosException e){
            logger.error("注册服务时有错误发生：",e);
            throw new RpcException(REGISTER_SERVICE_FAILED);
        }
    }
}
