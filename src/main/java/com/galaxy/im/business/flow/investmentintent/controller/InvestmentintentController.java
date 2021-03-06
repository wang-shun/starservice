package com.galaxy.im.business.flow.investmentintent.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxy.im.bean.common.SessionBean;
import com.galaxy.im.bean.project.SopProjectBean;
import com.galaxy.im.bean.soptask.SopTask;
import com.galaxy.im.bean.talk.SopFileBean;
import com.galaxy.im.business.flow.common.service.IFlowCommonService;
import com.galaxy.im.business.flow.investmentintent.service.IInvestmentintentService;
import com.galaxy.im.business.message.service.IScheduleMessageService;
import com.galaxy.im.business.operationLog.controller.ControllerUtils;
import com.galaxy.im.common.BeanUtils;
import com.galaxy.im.common.CUtils;
import com.galaxy.im.common.ResultBean;
import com.galaxy.im.common.StaticConst;
import com.galaxy.im.common.cache.redis.RedisCacheImpl;
import com.galaxy.im.common.webconfig.interceptor.operationLog.UrlNumber;

/**
 * 投资意向书
 * 
 * @author 
 */
@Controller
@RequestMapping("/flow/investmentintent")
public class InvestmentintentController{
	private Logger log = LoggerFactory.getLogger(InvestmentintentController.class);
	@Autowired
	IInvestmentintentService service;
	@Autowired
	private IFlowCommonService fcService;
	@Autowired
	IScheduleMessageService messageService;
	
	/**
	 * 进入尽职调查的操作按钮状态
	 * 依据：需要上传完成“投资意向书”
	 * 前端处理：如果已经上传，则进入尽职调查的按钮变亮
	 * 
	 * @param
	 * 	projectId   项目ID-使用JSON的方式传递
	 * @return
	 *  entity -> pass 布尔型  已上传投资意向书：true
	 */
	@RequestMapping("poButonStatus")
	@ResponseBody
	public Object projectOperateStatus(@RequestBody String paramString){
		ResultBean<Object> result = new ResultBean<Object>();
		try{
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);
			paramMap.put("fileStatus2", StaticConst.FILE_STATUS_2);
			paramMap.put("fileStatus4", StaticConst.FILE_STATUS_4);
			Map<String,Object> m = service.investmentOperateStatus(paramMap);
			if(CUtils.get().mapIsNotEmpty(m)){
				result.setEntity(m);
			}
			
			//项目最后上传的文件信息
			paramMap.put("fileWorkType", StaticConst.FILE_WORKTYPE_5);
			Map<String,Object> map = fcService.getLatestSopFileInfo(paramMap);
			if(CUtils.get().mapIsNotEmpty(map)){
				result.setMap(map);
			}
			result.setStatus("OK");
		}catch(Exception e){
			log.error(InvestmentintentController.class.getName() + "projectOperateStatus",e);
		}
		return result;
	}
	
	/**
	 * 进入尽职调查
	 * @param paramString
	 * @return
	 */
	@RequestMapping("startDuediligence")
	@ResponseBody
	public Object startDuediligence(HttpServletRequest request,HttpServletResponse response,@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setFlag(0);
		try{
			//获取登录用户信息
			@SuppressWarnings("unchecked")
			RedisCacheImpl<String,Object> cache = (RedisCacheImpl<String,Object>)StaticConst.ctx.getBean("cache");
			SessionBean b = CUtils.get().getBeanBySession(request);
			if (b==null) {
				resultBean.setMessage("获取用户信息失败");
				return resultBean;
			}
			Map<String, Object> user = BeanUtils.toMap(cache.get(b.getSessionid()));
			
			@SuppressWarnings("unused")
			long deptId=0l;
			String progressHistory="";
			Map<String,Object> map =new HashMap<String,Object>();
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);
			if(CUtils.get().mapIsNotEmpty(paramMap)){
				SopProjectBean sopBean = fcService.getSopProjectInfo(paramMap);
				if(sopBean!=null){
					if(sopBean.getProjectProgress().equals(StaticConst.PROJECT_PROGRESS_5)){
						//项目当前所处在投资意向书阶段,在流程历史记录拼接要进入的下个阶段
						if(!"".equals(sopBean.getProgressHistory()) && sopBean.getProgressHistory()!=null){
							progressHistory =sopBean.getProgressHistory()+","+StaticConst.PROJECT_PROGRESS_6;
						}else{
							progressHistory =StaticConst.PROJECT_PROGRESS_6;
						}
						paramMap.put("projectProgress", StaticConst.PROJECT_PROGRESS_6);	//进入尽职调查
						paramMap.put("progressHistory", progressHistory);					//流程历史记录
						
						if(fcService.enterNextFlow(paramMap)){
							resultBean.setFlag(1);
							map.put("projectProgress", StaticConst.PROJECT_PROGRESS_6);
							/*SessionBean sessionBean = CUtils.get().getBeanBySession(request);
							
							//给投资经理自己生成业务尽职调查待办任务
							SopTask bean = new SopTask();
							List<Map<String, Object>> list = fcService.getDeptId(sessionBean.getGuserid(), request, response);
							if(list!=null){
								for(Map<String, Object> vMap:list){
									deptId= CUtils.get().object2Long( vMap.get("deptId"));
								}
							}
							bean.setProjectId(CUtils.get().object2Long(paramMap.get("projectId")));
							bean.setTaskName(StaticConst.TASK_NAME_YWJD);
							bean.setTaskType(StaticConst.TASK_TYPE_XTBG);
							bean.setTaskFlag(StaticConst.TASK_FLAG_YWJD);
							bean.setTaskStatus(StaticConst.TASK_STATUS_DWG);
							bean.setTaskOrder(StaticConst.TASK_ORDER_NORMAL);
							bean.setAssignUid(sessionBean.getGuserid());
							bean.setDepartmentId(deptId);
							bean.setCreatedTime(new Date().getTime());
							@SuppressWarnings("unused")
							Long id = fcService.insertsopTask(bean);*/
							
							//给人事生成人事尽职调查待办任务
							SopTask beanHr = new SopTask();
							int hrDeptId = fcService.getDeptIdByDeptName(StaticConst.DEPT_NAME_HR,request,response);
							beanHr.setProjectId(CUtils.get().object2Long(paramMap.get("projectId")));
							beanHr.setTaskName(StaticConst.TASK_NAME_RSJD);
							beanHr.setTaskType(StaticConst.TASK_TYPE_XTBG);
							beanHr.setTaskFlag(StaticConst.TASK_FLAG_RSJD);
							beanHr.setTaskStatus(StaticConst.TASK_STATUS_DRL);
							beanHr.setTaskOrder(StaticConst.TASK_ORDER_NORMAL);
							beanHr.setDepartmentId(CUtils.get().object2Long(hrDeptId));
							beanHr.setCreatedTime(new Date().getTime());
							@SuppressWarnings("unused")
							Long hr = fcService.insertsopTask(beanHr);
							
							//给财务生成财务尽职调查待办任务
							SopTask beanFd = new SopTask();
							int fdDeptId = fcService.getDeptIdByDeptName(StaticConst.DEPT_NAME_FD,request,response);
							beanFd.setProjectId(CUtils.get().object2Long(paramMap.get("projectId")));
							beanFd.setTaskName(StaticConst.TASK_NAME_CWJD);
							beanFd.setTaskType(StaticConst.TASK_TYPE_XTBG);
							beanFd.setTaskFlag(StaticConst.TASK_FLAG_CWJD);
							beanFd.setTaskStatus(StaticConst.TASK_STATUS_DRL);
							beanFd.setTaskOrder(StaticConst.TASK_ORDER_NORMAL);
							beanFd.setDepartmentId(CUtils.get().object2Long(fdDeptId));
							beanFd.setCreatedTime(new Date().getTime());
							Long fd = fcService.insertsopTask(beanFd);
							
							//给法务生成法务尽职调查待办任务
							SopTask beanLaw = new SopTask();
							int lawDeptId = fcService.getDeptIdByDeptName(StaticConst.DEPT_NAME_LAW,request,response);
							beanLaw.setProjectId(CUtils.get().object2Long(paramMap.get("projectId")));
							beanLaw.setTaskName(StaticConst.TASK_NAME_FWJD);
							beanLaw.setTaskType(StaticConst.TASK_TYPE_XTBG);
							beanLaw.setTaskFlag(StaticConst.TASK_FLAG_FWJD);
							beanLaw.setTaskStatus(StaticConst.TASK_STATUS_DRL);
							beanLaw.setTaskOrder(StaticConst.TASK_ORDER_NORMAL);
							beanLaw.setDepartmentId(CUtils.get().object2Long(lawDeptId));
							beanLaw.setCreatedTime(new Date().getTime());
							@SuppressWarnings("unused")
							Long law = fcService.insertsopTask(beanLaw);
							resultBean.setMap(map);
							resultBean.setStatus("OK");
							//记录操作日志
							ControllerUtils.setRequestParamsForMessageTip(request, sopBean.getProjectName(), sopBean.getId(),"");
							
							//消息
							List<Map<String, Object>> userList=new ArrayList<Map<String, Object>>();
							List<Map<String, Object>> lawDeptIdList = fcService.getUserListByDeptId(lawDeptId);
							List<Map<String, Object>> fdDeptIdList = fcService.getUserListByDeptId(fdDeptId);
							List<Map<String, Object>> hrDeptIdList = fcService.getUserListByDeptId(hrDeptId);
							userList.addAll(lawDeptIdList);
							userList.addAll(fdDeptIdList);
							userList.addAll(hrDeptIdList);
							SopTask sopTask =new SopTask();
							sopTask.setId(fd);
							sopTask.setProjectName(sopBean.getProjectName());
							sopTask.setUsers(userList);
							sopTask.setMessageType("1.2.5");
							sopTask.setAssignUname(CUtils.get().object2String(user.get("userName")));
							sopTask.setCreatedId(b.getGuserid());
							sopTask.setUserName(CUtils.get().object2String(user.get("realName")));
							messageService.operateMessageSopTaskInfo(sopTask,sopTask.getMessageType());
						}else{
							resultBean.setMessage("项目当前状态或进度已被修改，请刷新");
						}
					}else{
						resultBean.setMessage("项目当前状态或进度已被修改，请刷新");	
					}
				}
			}
		}catch(Exception e){
			log.error(InvestmentintentController.class.getName() + "startDuediligence",e);
		}
		return resultBean;
	}
	
	/**
	 * 投资意向书信息
	 * @param paramString
	 * @return
	 */
	@RequestMapping("investmentintentList")
	@ResponseBody
	public Object investmentintentList(@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setFlag(0);
		try{
			List<String> fileWorkTypeList = new ArrayList<String>();
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);
			
			fileWorkTypeList.add(StaticConst.FILE_WORKTYPE_5);
				
			paramMap.put("fileWorkTypeList", fileWorkTypeList);
			List<Map<String,Object>> list = fcService.getSopFileList(paramMap);
			resultBean.setMapList(list);
			resultBean.setStatus("ok");
		}catch(Exception e){
			log.error(InvestmentintentController.class.getName() + "investmentintentList",e);
		}
		return resultBean;
	}
	
	/**
	 * 上传/更新投资意向书
	 * @param paramString
	 * @return
	 */
	@RequestMapping("uploadInvestmentintent")
	@ResponseBody
	public Object uploadInvestmentintent(HttpServletRequest request,HttpServletResponse response,@RequestBody SopFileBean bean){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		resultBean.setFlag(0);
		long id=0L;
		int prograss = 0;
		try{
			Long deptId =0L;
			SessionBean sessionBean = CUtils.get().getBeanBySession(request);
			//通过用户id获取一些信息
			List<Map<String, Object>> list = fcService.getDeptId(sessionBean.getGuserid(),request,response);
			if(list!=null){
				for(Map<String, Object> vMap:list){
					deptId= CUtils.get().object2Long( vMap.get("deptId"));
				}
			}
			paramMap.put("projectId", bean.getProjectId());
			paramMap.put("fileWorkType", bean.getFileWorkType());
			
			if(bean!=null){
				SopProjectBean sopBean = fcService.getSopProjectInfo(paramMap);
				if(sopBean!=null && sopBean.getProjectStatus().equals(StaticConst.PROJECT_STATUS_0)
						&& sopBean.getProjectProgress().equals(bean.getProjectProgress())){
					//项目id，当前阶段，所属事业线
					bean.setProjectId(sopBean.getId());
					bean.setProjectProgress(sopBean.getProjectProgress());
					//事业线为用户部门id
					bean.setCareerLine(deptId);
					//文件类型
					String fileType =fcService.getFileType(bean.getFileSuffix());
					bean.setFileType(fileType);
					//文件名称拆分
					Map<String,String> nameMap = fcService.transFileNames(bean.getFileName());
					bean.setFileName(nameMap.get("fileName"));
					//文件状态：已上传
					bean.setFileStatus(StaticConst.FILE_STATUS_2);
					bean.setFileValid(1);
					bean.setCreatedTime(new Date().getTime());
					bean.setFileUid(sessionBean.getGuserid());
					//业务操作
					if(bean.getId()!=null && bean.getId()!=0){
						//更新：添加新的一条记录
						@SuppressWarnings("unused")
						int vid = fcService.updateValid(bean.getId());
						id =fcService.addSopFile(bean);
						prograss=1;
					}else{
						//上传之前:查数据库中是否存在信息，存在更新，否则新增
						Map<String,Object> info = fcService.getLatestSopFileInfo(paramMap);
						if(info!=null && info.get("id")!=null && CUtils.get().object2Long(info.get("id"))!=0){
							bean.setId(CUtils.get().object2Long(info.get("id")));
							bean.setUpdatedTime(new Date().getTime());
							id=fcService.updateSopFile(bean);
						}else{
							id =fcService.addSopFile(bean);
						}
					}
					if(id>0){
						//更新代办任务
						SopTask taskBean = new SopTask();
						taskBean.setProjectId(bean.getProjectId());
						taskBean.setTaskName(StaticConst.TASK_NAME_SCTZYXS);
						taskBean.setTaskFlag(StaticConst.TASK_FLAG_SCTZYXS);
						taskBean.setTaskStatus(StaticConst.TASK_STATUS_YWG);
						taskBean.setTaskType(StaticConst.TASK_TYPE_XTBG);
						taskBean.setUpdatedTime(new Date().getTime());
						taskBean.setTaskDeadline(new Date());
						@SuppressWarnings("unused")
						Long taskId = fcService.updateSopTask(taskBean);
						//返回信息
						paramMap.clear();
						paramMap.put("fileId", id);
						resultBean.setMap(paramMap);
						resultBean.setStatus("ok");
						resultBean.setFlag(1);
					}
				}else{
					resultBean.setMessage("项目当前状态或进度已被修改，请刷新");	
				}
				if(id!=0L){
					//记录操作日志
					UrlNumber uNum = fcService.setNumForFile(prograss,bean);
					ControllerUtils.setRequestParamsForMessageTip(request, sopBean.getProjectName(), sopBean.getId(), null, uNum);
				}
			}
		}catch(Exception e){
			log.error(InvestmentintentController.class.getName() + "uploadInvestmentintent",e);
		}
		return resultBean;
	}

}
