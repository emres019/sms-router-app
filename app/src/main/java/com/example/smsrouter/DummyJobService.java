package com.example.smsrouter;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class DummyJobService extends JobService {

    static final int DUMMY_JOB_ID = 0;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}