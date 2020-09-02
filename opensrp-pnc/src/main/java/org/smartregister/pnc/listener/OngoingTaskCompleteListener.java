package org.smartregister.pnc.listener;


import android.support.annotation.NonNull;

import org.smartregister.pnc.pojo.OngoingTask;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 26-03-2020.
 */
public interface OngoingTaskCompleteListener {

    void onTaskComplete(@NonNull OngoingTask ongoingTask);
}
