package rpc.core.provider;

/**
 * 服务提供接口
 */
public interface ServiceProvider {
    /**
     * 增加服务提供者
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void addServiceProvider(T service,String serviceName);

    /**
     * 获取服务提供者
     * @param serviceName
     * @return
     */
    Object getServiceProvider(String serviceName);
}
