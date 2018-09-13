package com.tfedu.record;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * desc :
 * author：xiedong
 * data：2018/9/11
 */
public class ThreadPool {
    private static ThreadPool instance;
    private final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    private ExecutorService processorsPools,cachedPools;

    private ThreadPool(){

    }

    public synchronized static ThreadPool getInstance(){
        if (instance==null){
            instance=new ThreadPool();
        }
        return instance;
    }

    public ExecutorService getProcessorsPools(){
        if (processorsPools==null){
            processorsPools = Executors.newFixedThreadPool(EXECUTOR_THREADS);
        }
        return processorsPools;
    }

    public ExecutorService getCachedPools(){
        if (cachedPools==null){
            cachedPools = Executors.newCachedThreadPool();
        }
        return cachedPools;
    }
}
