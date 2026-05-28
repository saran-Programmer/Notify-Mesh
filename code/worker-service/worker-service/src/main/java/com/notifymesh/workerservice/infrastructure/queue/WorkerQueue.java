package com.notifymesh.workerservice.infrastructure.queue;

import com.notifymesh.workerservice.domain.service.WorkerService;
import com.notifymesh.workerservice.dto.NotificationEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class WorkerQueue {

    private static volatile WorkerQueue instance;

    private WorkerQueue() {
    }

    @Autowired
    private WorkerService workerService;

    public static WorkerQueue getInstance() {
        return instance;
    }

    private final BlockingQueue<NotificationEvent> highQueue   = new LinkedBlockingQueue<>();
    private final BlockingQueue<NotificationEvent> mediumQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<NotificationEvent> lowQueue    = new LinkedBlockingQueue<>();

    private int threadTimeoutMs = 100;

    private volatile boolean running;

    @PostConstruct
    public void startWorkerThread() {
        instance = this;
        running = true;
        Thread worker = new Thread(this::pollAndProcess, "worker-queue-thread");
        worker.setDaemon(true);
        worker.start();
    }

    @PreDestroy
    public void stop() {
        running = false;
    }

    private void pollAndProcess() {
        while (running) {
            try {
                NotificationEvent event = highQueue.poll(threadTimeoutMs, TimeUnit.MILLISECONDS);

                if (event == null) {
                    event = mediumQueue.poll(threadTimeoutMs, TimeUnit.MILLISECONDS);
                }

                if (event == null) {
                    event = lowQueue.poll(threadTimeoutMs, TimeUnit.MILLISECONDS);
                }

                if (event != null) {
                    workerService.handleNotification(event);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void enqueue(NotificationEvent event) {
        switch (event.getPriority()) {
            case HIGH   -> highQueue.offer(event);
            case MEDIUM -> mediumQueue.offer(event);
            case LOW    -> lowQueue.offer(event);
        }
    }
}
