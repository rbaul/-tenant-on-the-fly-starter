package com.github.rbaul.tenantonthefly.interceptors.async;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

@RequiredArgsConstructor
public class InheritableExecutorDelegate implements Executor {

    private final Executor executor;

    public ListenableFuture<?> submitListenable(Runnable task) {
        InheritableWrapperRunnable wrapperTask = new InheritableWrapperRunnable(task);
        try {
            ListenableFutureTask<Object> future = new ListenableFutureTask<>(wrapperTask, null);
            executor.execute(future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + wrapperTask, ex);
        }
    }

    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        InheritableWrapperCallable<T> wrapperTask = new InheritableWrapperCallable<>(task);
        try {
            ListenableFutureTask<T> future = new ListenableFutureTask<>(wrapperTask);
            executor.execute(future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + wrapperTask, ex);
        }
    }

    public void execute(Runnable task) {
        InheritableWrapperRunnable wrapperTask = new InheritableWrapperRunnable(task);
        try {
            executor.execute(wrapperTask);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + wrapperTask, ex);
        }
    }
}