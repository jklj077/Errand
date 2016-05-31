package com.example.errand.errand.Objects;

import java.util.ArrayList;
import java.util.List;

public class TaskInfo{
    public Integer pk;
    public String headline;
    public String detail;
    public String reward;
    public String creator;
    public String createTime;

    public String status;
    public List<String> takers;
    public String executor;
    public String comment;
    public Integer score;

    public TaskInfo() {
        pk =null;
        headline =null;
        detail = null;
        reward = null;
        creator = null;
        createTime = null;
        status = null;
        takers = new ArrayList<>();
        executor = null;
        comment = null;
        score = null;
    }
}
