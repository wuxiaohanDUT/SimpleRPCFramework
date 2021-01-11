package rpc.core.transport;


import rpc.common.entity.RpcRequest;
import rpc.core.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER= CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest request);
}
