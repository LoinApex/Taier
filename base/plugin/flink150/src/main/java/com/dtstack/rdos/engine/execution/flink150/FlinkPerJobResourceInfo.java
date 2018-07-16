package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;

import java.util.Properties;

/**
 * flink yarn 资源相关
 * Date: 2018/07/10
 * Company: www.dtstack.com
 *
 * @author toutian
 */

public class FlinkPerJobResourceInfo extends EngineResourceInfo {


    public final static String CORE_TOTAL_KEY = "cores.total";
    public final static String CORE_USED_KEY = "cores.used";
    public final static String CORE_FREE_KEY = "cores.free";

    public final static String MEMORY_TOTAL_KEY = "memory.total";
    public final static String MEMORY_USED_KEY = "memory.used";
    public final static String MEMORY_FREE_KEY = "memory.free";

    public final static String JOBMANAGER_MEMORY_MB = "jobmanager.memory.mb";
    public final static String TASKMANAGER_MEMORY_MB = "taskmanager.memory.mb";
    public final static String CONTAINER = "container";
    public final static String SLOTS = "slots";

    public final static int MIN_JM_MEMORY = 1024; // the minimum memory should be higher than the min heap cutoff
    public final static int MIN_TM_MEMORY = 1024;

    private int jobmanagerMemoryMb = MIN_JM_MEMORY;
    private int taskmanagerMemoryMb = MIN_JM_MEMORY;
    private int numberTaskManagers = 1;
    private int slotsPerTaskManager = 1;
    private int containerLimit;

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        int totalFreeCore = 0;
        int totalFreeMem = 0;

        int[] nmFree = new int[nodeResourceMap.size()];
        int index = 0;
        for (NodeResourceInfo tmpMap : nodeResourceMap.values()) {
            int nodeFreeMem = MathUtil.getIntegerVal(tmpMap.getProp(MEMORY_FREE_KEY));
            int nodeFreeCores = MathUtil.getIntegerVal(tmpMap.getProp(CORE_FREE_KEY));

            totalFreeMem += nodeFreeMem;
            totalFreeCore += nodeFreeCores;

            nmFree[index++] = nodeFreeMem;
        }

        if (totalFreeCore == 0 || totalFreeMem == 0) {
            return false;
        }

        Properties properties = jobClient.getConfProperties();

        if (properties != null && properties.containsKey(SLOTS)) {
            slotsPerTaskManager = MathUtil.getIntegerVal(properties.get(SLOTS));
        }
        if (totalFreeCore < slotsPerTaskManager) {
            return false;
        }

        if (properties != null && properties.containsKey(JOBMANAGER_MEMORY_MB)) {
            jobmanagerMemoryMb = MathUtil.getIntegerVal(properties.get(JOBMANAGER_MEMORY_MB));
        }
        if (jobmanagerMemoryMb < MIN_JM_MEMORY) {
            jobmanagerMemoryMb = MIN_JM_MEMORY;
        }

        if (properties != null && properties.containsKey(TASKMANAGER_MEMORY_MB)) {
            taskmanagerMemoryMb = MathUtil.getIntegerVal(properties.get(TASKMANAGER_MEMORY_MB));
        }
        if (taskmanagerMemoryMb < MIN_TM_MEMORY) {
            taskmanagerMemoryMb = MIN_TM_MEMORY;
        }

        if (properties != null && properties.containsKey(CONTAINER)) {
            numberTaskManagers = MathUtil.getIntegerVal(properties.get(CONTAINER));
        }

        int totalMemoryRequired = jobmanagerMemoryMb + taskmanagerMemoryMb * numberTaskManagers;
        if (totalFreeMem < totalMemoryRequired) {
            return false;
        }
        if (taskmanagerMemoryMb > containerLimit || jobmanagerMemoryMb > containerLimit) {
            return false;
        }

        if (!allocateResource(nmFree, jobmanagerMemoryMb)) {
            return false;
        }

        for (int i = 0; i < numberTaskManagers; i++) {
            if (!allocateResource(nmFree, taskmanagerMemoryMb)) {
                return false;
            }
        }

        return true;
    }

    private boolean allocateResource(int[] nodeManagers, int toAllocate) {
        for (int i = 0; i < nodeManagers.length; i++) {
            if (nodeManagers[i] >= toAllocate) {
                nodeManagers[i] -= toAllocate;
                return true;
            }
        }
        return false;
    }

    public void setContainerLimit(int containerLimit) {
        this.containerLimit = containerLimit;
    }
}