package rpc.core.transport.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractRpcServer {
    protected Logger logger= LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;


}
