package rpc.core.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.entity.RpcRequest;
import rpc.common.entity.RpcResponse;
import rpc.common.enumeration.SerializerCode;
import rpc.common.exception.SerializeException;

import java.io.ByteArrayInputStream;

public class KryoSerializer implements CommonSerializer{

    private static final Logger logger= LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal=ThreadLocal.withInitial(()->{
        Kryo kryo=new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }

    @Override
    public byte[] serialize(Object obj) {
        try(ByteOutputStream byteOutputStream=new ByteOutputStream();
            Output output=new Output(byteOutputStream)){
            Kryo kryo=kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        } catch (Exception e) {
            logger.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }
}
