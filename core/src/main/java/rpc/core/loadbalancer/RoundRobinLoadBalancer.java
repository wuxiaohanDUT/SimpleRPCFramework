package rpc.core.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 轮转负载均衡
 */
public class RoundRobinLoadBalancer implements LoadBalancer{
    private int index=0;
    @Override
    public Instance select(List<Instance> list) {
        if(index>= list.size()){
            index%=list.size();
        }
        return list.get(index++);
    }
}
