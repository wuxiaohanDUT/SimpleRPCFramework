package rpc.common.factory;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池的工具类
 */
public class ThreadPoolFactory {
    /**
     * 线程池参数
     */
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static Map<String, ExecutorService> threadPoolsMap=new ConcurrentHashMap<>();

    private ThreadPoolFactory(){
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix,Boolean daemon){
        ExecutorService pool=threadPoolsMap.computeIfAbsent(threadNamePrefix,k->createThreadPool(threadNamePrefix,daemon));
        if(pool.isShutdown()||pool.isTerminated()){
            threadPoolsMap.remove(threadNamePrefix);
            pool=createThreadPool(threadNamePrefix,daemon);
            threadPoolsMap.put(threadNamePrefix,pool);
        }
        return pool;
    }

    private static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon){
        BlockingQueue<Runnable> workQueue=new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory=createThreadFactory(threadNamePrefix,daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.MINUTES,workQueue,threadFactory);
    }

    public static void shutdownAll(){
        logger.info("关闭所有线程池");
        threadPoolsMap.entrySet().parallelStream().forEach(e->{
            ExecutorService executorService=e.getValue();
            executorService.shutdown();
            logger.info("关闭线程池 [{}] [{}]", e.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10,TimeUnit.SECONDS);
            }catch (InterruptedException ex){
                logger.error("线程池关闭失败");
                executorService.shutdownNow();
            }
        });
    }
    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     *
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix,Boolean daemon){
        if(threadNamePrefix!=null){
            if(daemon!=null){
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix+"-%d").setDaemon(daemon).build();
            }else{
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix+"-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
