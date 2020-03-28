package com.vip.saturn.job.console.service;

import com.vip.saturn.job.console.domain.*;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.mybatis.entity.JobConfig4DB;
import com.vip.saturn.job.console.repository.zookeeper.CuratorRepository.CuratorFrameworkOp;
import com.vip.saturn.job.console.vo.GetJobConfigVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author hebelala
 */
public interface JobService {

	List<String> getGroups(String namespace) throws SaturnJobConsoleException;

	void enableJob(String namespace, String jobName, String updatedBy) throws SaturnJobConsoleException;

	void disableJob(String namespace, String jobName, String updatedBy) throws SaturnJobConsoleException;

	void removeJob(String namespace, String jobName) throws SaturnJobConsoleException;

	/**
	 * 获取该作业可选择的优先Executor
	 */
	List<ExecutorProvided> getCandidateExecutors(String namespace, String jobName) throws SaturnJobConsoleException;

	void setPreferList(String namespace, String jobName, String preferList, String updatedBy)
			throws SaturnJobConsoleException;

	List<String> getCandidateUpStream(String namespace) throws SaturnJobConsoleException;

	List<String> getCandidateDownStream(String namespace) throws SaturnJobConsoleException;

	void addJob(String namespace, JobConfig jobConfig, String createdBy) throws SaturnJobConsoleException;

	void copyJob(String namespace, JobConfig jobConfig, String copyingJobName, String createdBy)
			throws SaturnJobConsoleException;

	int getMaxJobNum() throws SaturnJobConsoleException;

	boolean jobIncExceeds(String namespace, int maxJobNum, int inc) throws SaturnJobConsoleException;

	List<JobConfig> getUnSystemJobs(String namespace) throws SaturnJobConsoleException;

	List<JobConfig> getUnSystemJobsWithCondition(String namespace, Map<String, Object> condition, int page, int size)
			throws SaturnJobConsoleException;

	int countUnSystemJobsWithCondition(String namespace, Map<String, Object> condition)
			throws SaturnJobConsoleException;

	int countEnabledUnSystemJobs(String namespace) throws SaturnJobConsoleException;

	/**
	 * @deprecated since 3.1.0，不再支持systemJob
	 */
	@Deprecated
	List<String> getUnSystemJobNames(String namespace) throws SaturnJobConsoleException;


	List<String> getJobNames(String namespace) throws SaturnJobConsoleException;

	/**
	 * 持久化作业到指定namespace
	 */
	void persistJobFromDB(String namespace, JobConfig jobConfig) throws SaturnJobConsoleException;

	/**
	 * 持久化作业到特定zk上面
	 */
	void persistJobFromDB(JobConfig jobConfig, CuratorFrameworkOp curatorFrameworkOp) throws SaturnJobConsoleException;

	List<BatchJobResult> importJobs(String namespace, MultipartFile file, String createdBy)
			throws SaturnJobConsoleException;

	File exportJobs(String namespace) throws SaturnJobConsoleException;

	ArrangeLayout getArrangeLayout(String namespace) throws SaturnJobConsoleException;

	JobConfig getJobConfigFromZK(String namespace, String jobName) throws SaturnJobConsoleException;

	JobConfig getJobConfig(String namespace, String jobName) throws SaturnJobConsoleException;

	JobStatus getJobStatus(String namespace, String jobName) throws SaturnJobConsoleException;

	JobStatus getJobStatus(String namespace, JobConfig jobConfig) throws SaturnJobConsoleException;

	boolean isJobShardingAllocatedExecutor(String namespace, String jobName) throws SaturnJobConsoleException;

	List<String> getJobServerList(String namespace, String jobName) throws SaturnJobConsoleException;

	GetJobConfigVo getJobConfigVo(String namespace, String jobName) throws SaturnJobConsoleException;

	void updateJobConfig(String namespace, JobConfig jobConfig, String updatedBy) throws SaturnJobConsoleException;

	List<String> getAllJobNamesFromZK(String namespace) throws SaturnJobConsoleException;

	void updateJobCron(String namespace, String jobName, String cron, Map<String, String> customContext,
			String updatedBy) throws SaturnJobConsoleException;

	/**
	 * 获取作业所分配的executor及先关分配信息。
	 */
	List<JobServer> getJobServers(String namespace, String jobName) throws SaturnJobConsoleException;

	/**
	 * 获取JobServer状态信息
	 */
	List<JobServerStatus> getJobServersStatus(String namespace, String jobName) throws SaturnJobConsoleException;

	void runAtOnce(String namespace, String jobName) throws SaturnJobConsoleException;

	void stopAtOnce(String namespace, String jobName) throws SaturnJobConsoleException;

	/**
	 * 获取作业运行状态
	 */
	List<ExecutionInfo> getExecutionStatus(String namespace, String jobName) throws SaturnJobConsoleException;

	/**
	 * 获取运行日志
	 */
	String getExecutionLog(String namespace, String jobName, String jobItem) throws SaturnJobConsoleException;

	/**
	 * 通过Queue名获取作业信息
	 */
	List<JobConfig4DB> getJobsByQueue(String queue) throws SaturnJobConsoleException;

	/**
	 * 批量设置作业的分组
	 * @param namespace
	 * @param jobNames 待设置分组的作业名集合
	 * @param oldGroupNames 修改前的分组名集合
	 * @param newGroupsNames 修改后的分组名集合
	 * @param userName 操作者
	 */
	void batchSetGroups(String namespace, List<String> jobNames, List<String> oldGroupNames, List<String> newGroupsNames, String userName)  throws SaturnJobConsoleException;
}
