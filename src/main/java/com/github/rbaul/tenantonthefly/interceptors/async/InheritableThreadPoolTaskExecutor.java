package com.github.rbaul.tenantonthefly.interceptors.async;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class InheritableThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        ExecutorService executor = getThreadPoolExecutor();
        InheritableExecutorServiceDelegate inheritableExecutorServiceDelegate = new InheritableExecutorServiceDelegate(executor);
        return inheritableExecutorServiceDelegate.submit(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        ExecutorService executor = getThreadPoolExecutor();
        InheritableExecutorServiceDelegate inheritableExecutorServiceDelegate = new InheritableExecutorServiceDelegate(executor);
        return inheritableExecutorServiceDelegate.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        ExecutorService executor = getThreadPoolExecutor();
        InheritableExecutorServiceDelegate inheritableExecutorServiceDelegate = new InheritableExecutorServiceDelegate(executor);
        return inheritableExecutorServiceDelegate.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ExecutorService executor = getThreadPoolExecutor();
        InheritableExecutorServiceDelegate inheritableExecutorServiceDelegate = new InheritableExecutorServiceDelegate(executor);
        return inheritableExecutorServiceDelegate.submitListenable(task);
    }

    @Override
    public void execute(Runnable task) {
        ExecutorService executor = getThreadPoolExecutor();
        InheritableExecutorServiceDelegate inheritableExecutorServiceDelegate = new InheritableExecutorServiceDelegate(executor);
        inheritableExecutorServiceDelegate.execute(task);
    }
}