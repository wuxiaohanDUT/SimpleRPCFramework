package rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 消费者向服务者发送的请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    /**
     * 请求号
     */
    private String requestId;
    /**
     * 请求调用接口的名称
     */
    private String interfaceName;
    /**
     * 调用方法的名称
     */
    private String methodName;
    /**
     * 调用方法的参数
     */
    private Object[] parameters;
    /**
     * 方法参数的类型
     */
    private Class<?>[] paramTypes;
    /**
     * 是否为心跳包
     */
    private boolean heartBeat;
}
