package rpc.core.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.entity.RpcRequest;
import rpc.common.enumeration.SerializerCode;
import rpc.common.exception.SerializeException;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer{

    private static final Logger logger= LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper=new ObjectMapper();
    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }

    @Override
    public byte[] serialize(Object obj) {
        try{
            return objectMapper.writeValueAsBytes(obj);
        }catch (JsonProcessingException e){
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj=objectMapper.readValue(bytes,clazz);
            if(obj instanceof RpcRequest){
                obj = handleRequest(obj);
            }
            return obj;
        }catch (IOException e){
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    /*
        这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型
        需要重新判断处理
     */
    private Object handleRequest(Object obj) throws IOException{
        RpcRequest request=(RpcRequest) obj;
        for(int i=0;i<request.getParameters().length;++i){
            Class<?> clazz=request.getParamTypes()[i];
            if(!clazz.isAssignableFrom(request.getParamTypes()[i].getClass())){
                byte[] bytes=objectMapper.writeValueAsBytes(request.getParameters()[i]);
                request.getParameters()[i]=objectMapper.readValue(bytes,clazz);
            }
        }
        return request;
    }
}
