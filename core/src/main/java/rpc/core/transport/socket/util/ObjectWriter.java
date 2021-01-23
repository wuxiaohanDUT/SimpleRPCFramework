package rpc.core.transport.socket.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.entity.RpcRequest;
import rpc.common.enumeration.PackageType;
import rpc.core.serializer.CommonSerializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 向输出流中按照对应规则写入数据
 */
public class ObjectWriter {

    private static final Logger logger= LoggerFactory.getLogger(ObjectWriter.class);

    private static final int MAGIC_NUMBER=0xCAFEBABE;

    private static void writeObject(OutputStream outputStream, Object object, CommonSerializer serializer) throws IOException{

        outputStream.write(intToBytes(MAGIC_NUMBER));
        if(object instanceof RpcRequest){
            outputStream.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        }else{
            outputStream.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        outputStream.write(serializer.getCode());
        byte[] bytes=serializer.serialize(object);
        outputStream.write(bytes.length);
        outputStream.write(bytes);
        outputStream.flush();
    }
    private static byte[] intToBytes(int value){
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
