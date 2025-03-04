package com.taotao.cloud.sys.biz.task.quartz;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
//@Service
public class ScheduleServiceImpl implements IScheduleService {

	private String defaultGroup = "default_group";

	@Autowired
	private Scheduler scheduler;

	@Override
	public String scheduleJob(Class<? extends Job> jobBeanClass, String cron, String data) {
		String jobName = UUID.fastUUID().toString();
		JobDetail jobDetail = JobBuilder.newJob(jobBeanClass)
			.withIdentity(jobName, defaultGroup)
			.usingJobData("data", data)
			.build();
		//创建触发器，指定任务执行时间
		CronTrigger cronTrigger = TriggerBuilder.newTrigger()
			.withIdentity(jobName, defaultGroup)
			.withSchedule(CronScheduleBuilder.cronSchedule(cron))
			.build();
		// 调度器进行任务调度
		try {
			scheduler.scheduleJob(jobDetail, cronTrigger);
		} catch (SchedulerException e) {
			log.error("任务调度执行失败{}", e.getMessage());
		}
		return jobName;
	}

	@Override
	public String scheduleFixTimeJob(Class<? extends Job> jobBeanClass, Date startTime,
		String data) {
		//日期转CRON表达式
		String startCron = String.format("%d %d %d %d %d ? %d",
			DateUtil.second(startTime),
			DateUtil.minute(startTime),
			DateUtil.hour(startTime, true),
			DateUtil.dayOfMonth(startTime),
			DateUtil.month(startTime) + 1,
			DateUtil.year(startTime));
		return scheduleJob(jobBeanClass, startCron, data);
	}

	@Override
	public Boolean cancelScheduleJob(String jobName) {
		boolean success = false;
		try {
			// 暂停触发器
			scheduler.pauseTrigger(new TriggerKey(jobName, defaultGroup));
			// 移除触发器中的任务
			scheduler.unscheduleJob(new TriggerKey(jobName, defaultGroup));
			// 删除任务
			scheduler.deleteJob(new JobKey(jobName, defaultGroup));
			success = true;
		} catch (SchedulerException e) {
			log.error("任务取消失败{}", e.getMessage());
		}
		return success;
	}
}
