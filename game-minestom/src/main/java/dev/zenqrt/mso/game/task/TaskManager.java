package dev.zenqrt.mso.game.task;

import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.List;

public final class TaskManager {

    private final Scheduler scheduler;
    private final List<Task> tasks = new ArrayList<>();

    public TaskManager(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void startTask(Runnable runnable, TaskSchedule delay, TaskSchedule interval) {
        tasks.add(scheduler.scheduleTask(runnable, delay, interval));
    }

    public void shutdownAllTasks() {
        tasks.forEach(Task::cancel);
        tasks.clear();
    }

}
