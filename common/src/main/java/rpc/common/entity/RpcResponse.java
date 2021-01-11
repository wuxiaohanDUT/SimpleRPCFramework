package rpc.common.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import rpc.common.enumeration.ResponseCode;

import java.io.Serializable;

/**
 * 服务者执行完成或者出错后返回给消费者的结果对象
 * @param <T>
 */
@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    /**
     * 响应所对应的请求号
     */
    private String requestId;

    /**
     * 状态码
     */
    private Integer statusCode;

    /**
     * 状态信息
     */
    private String message;

    /**
     * 其他数据
     */
    private T data;


    public static <T> RpcResponse<T> success(T data,String requestId){
        RpcResponse<T> response=new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        response.setRequestId(requestId);
        return response;
    }

    public static <T> RpcResponse<T> fail(T data,String requestId){
        RpcResponse<T> response=new RpcResponse<>();
        response.setStatusCode(ResponseCode.FAIL.getCode());
        response.setMessage(ResponseCode.FAIL.getMessage());
        response.setRequestId(requestId);
        response.setData(data);
        return response;
    }
}
