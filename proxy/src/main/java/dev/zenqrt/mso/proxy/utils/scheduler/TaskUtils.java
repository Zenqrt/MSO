package dev.zenqrt.mso.proxy.utils.scheduler;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import dev.zenqrt.mso.proxy.MSOProxy;

import java.util.function.Consumer;

public final class TaskUtils {

    public static Scheduler.TaskBuilder createBuilder(MSOProxy plugin, Consumer<ScheduledTask> taskHandler) {
        return plugin.getServer().getScheduler().buildTask(plugin, taskHandler);
    }

    public static Scheduler.TaskBuilder createBuilder(MSOProxy plugin, Runnable runnable) {
        return plugin.getServer().getScheduler().buildTask(plugin, runnable);
    }

}
