package rpc.core.provider;

public interface ServiceProvider {
    <T> void addServiceProvider(T service,String serviceName);
    Object getServiceProvider(String serviceName);
}
