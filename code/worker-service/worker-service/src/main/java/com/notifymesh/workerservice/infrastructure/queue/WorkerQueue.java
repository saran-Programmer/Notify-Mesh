package com.notifymesh.workerservice.infrastructure.queue;

import com.notifymesh.workerservice.dto.NotificationEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WorkerQueue {

    // ------------------------------------------------------------------ singleton

    private static volatile WorkerQueue instance;

    /** Private constructor — prevents direct instantiation outside this class.
     *  Spring creates the one managed instance via reflection. */
    private WorkerQueue() {
    }

    /** Returns the single application-wide instance.
     *  Available after the Spring context has started. */
    public static WorkerQueue getInstance() {
        return instance;
    }

    // ------------------------------------------------------------------ state

    private final BlockingQueue<NotificationEvent> highQueue   = new LinkedBlockingQueue<>();
    private final BlockingQueue<NotificationEvent> mediumQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<NotificationEvent> lowQueue    = new LinkedBlockingQueue<>();

    private int threadTimeoutMs = 100;

    private volatile boolean running;

    // ------------------------------------------------------------------ lifecycle

    @PostConstruct
    public void startWorkerThread() {
        instance = this;        // anchor the static reference to the Spring-managed bean
        running = true;
        Thread worker = new Thread(this::pollAndProcess, "worker-queue-thread");
        worker.setDaemon(true);
        worker.start();
        log.info("WorkerQueue started — polling thread is live");
    }

    @PreDestroy
    public void stop() {
        running = false;
        log.info("WorkerQueue stopped — polling thread will exit");
    }

    // ------------------------------------------------------------------ core loop

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
                    // TODO: delegate to WorkerService once wired
                    log.debug("Dequeued event — id={}, priority={}", event.getId(), event.getPriority());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Worker polling thread interrupted", e);
            }
        }
        log.info("Worker polling thread exited cleanly");
    }

    // ------------------------------------------------------------------ enqueue API

    /**
     * Routes an incoming {@link NotificationEvent} into the correct priority queue.
     * Called by the Kafka consumer (or any upstream producer).
     */
    public void enqueue(NotificationEvent event) {
        switch (event.getPriority()) {
            case HIGH   -> highQueue.offer(event);
            case MEDIUM -> mediumQueue.offer(event);
            case LOW    -> lowQueue.offer(event);
        }
        log.debug("Enqueued event — id={}, priority={}", event.getId(), event.getPriority());
    }
}
