package com.happyroad.wy.service;

import com.happyroad.wy.entity.JobModel;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

public interface JobService {

	void saveJob(JobModel jobModel);

	void doJob(JobDataMap jobDataMap);

	void deleteJob(Long id) throws SchedulerException;
}
