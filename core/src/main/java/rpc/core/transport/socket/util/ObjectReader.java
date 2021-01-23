package rpc.core.transport.socket.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.entity.RpcRequest;
import rpc.common.entity.RpcResponse;
import rpc.common.enumeration.PackageType;
import rpc.common.enumeration.RpcError;
import rpc.common.exception.RpcException;
import rpc.core.serializer.CommonSerializer;

import java.io.IOException;
import java.io.InputStream;

/**
 * 从输入流中读取数据并且反序列化
 */
public class ObjectReader {
    private static final Logger logger= LoggerFactory.getLogger(ObjectReader.class);
    private static final int MAGIC_NUMBER=0xCAFEBABE;
    public static Object readObject(InputStream inputStream) throws IOException{
        byte[] numberBytes=new byte[4];
        inputStream.read(numberBytes);
        int magic=bytesToInt(numberBytes);
        if(magic!=MAGIC_NUMBER){
            logger.error("不识别协议包：{}",magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        inputStream.read(numberBytes);
        int packageCode=bytesToInt(numberBytes);
        Class<?> packageClass;
        if(packageCode== PackageType.REQUEST_PACK.getCode()){
            packageClass= RpcRequest.class;
        }else if(packageCode==PackageType.RESPONSE_PACK.getCode()){
            packageClass= RpcResponse.class;
        }else{
            logger.error("不识别协议包：{}",packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        inputStream.read(numberBytes);
        int serializerCode=bytesToInt(numberBytes);
        CommonSerializer serializer=CommonSerializer.getByCode(serializerCode);
        if(serializer==null){
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        inputStream.read(numberBytes);
        int length=bytesToInt(numberBytes);
        byte[] bytes=new byte[length];
        inputStream.read(bytes);
        return serializer.deserialize(bytes,packageClass);
    }
    public static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF)<<24)
                |((src[1] & 0xFF)<<16)
                |((src[2] & 0xFF)<<8)
                |(src[3] & 0xFF);
        return value;
    }
}
