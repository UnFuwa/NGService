package com.unfuwa.ngservice.extendedmodel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.unfuwa.ngservice.model.GraphWork;
import com.unfuwa.ngservice.model.TaskWork;

public class GraphTaskWork {

    @Embedded
    private GraphWork graphWork;

    @Relation(parentColumn = "IdTaskWork", entityColumn = "Id")
    private TaskWork taskWork;

    public GraphWork getGraphWork() {
        return graphWork;
    }

    public void setGraphWork(GraphWork graphWork) {
        this.graphWork = graphWork;
    }

    public TaskWork getTaskWork() {
        return taskWork;
    }

    public void setTaskWork(TaskWork taskWork) {
        this.taskWork = taskWork;
    }
}
