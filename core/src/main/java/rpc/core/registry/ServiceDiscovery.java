package rpc.core.registry;


import java.net.InetSocketAddress;

/**
 * 服务发现接口
 */
interface ServiceDiscovery {

    /**
     *根据服务名称查找服务实体
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);
}
