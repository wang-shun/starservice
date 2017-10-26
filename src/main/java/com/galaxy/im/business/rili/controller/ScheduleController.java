package com.galaxy.im.business.rili.controller;

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
import com.galaxy.im.bean.schedule.ScheduleDict;
import com.galaxy.im.bean.schedule.ScheduleInfo;
import com.galaxy.im.business.rili.service.IScheduleDictService;
import com.galaxy.im.business.rili.service.IScheduleService;
import com.galaxy.im.common.BeanUtils;
import com.galaxy.im.common.CUtils;
import com.galaxy.im.common.DateUtil;
import com.galaxy.im.common.ResultBean;
import com.galaxy.im.common.StaticConst;
import com.galaxy.im.common.cache.redis.RedisCacheImpl;


@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	private Logger log = LoggerFactory.getLogger(ScheduleController.class);

	@Autowired
	IScheduleService service;
	
	@Autowired
	private IScheduleDictService scheduleDictService;
	
	
	/**
	 * 时间是否冲突 或 时间冲突数
	 * @param record
	 * @return
	 */
	@RequestMapping("ctSchedule")
	@ResponseBody
	public Object ctSchedule(HttpServletRequest request,HttpServletResponse response,@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setStatus("error");
		try {
			//获取登录用户信息
			SessionBean bean = CUtils.get().getBeanBySession(request);
			Map<String,Object> m = new HashMap<String,Object>();
			Map<String,Object> map = CUtils.get().jsonString2map(paramString);
			if(map!=null){
				map.put("userId",bean.getGuserid());
				List<Map<String,Object>> list = service.ctSchedule(map);
				if(list!=null && list.size()>0){
					if(list.size()==1){
						Map<String,Object> mm=list.get(0);
						resultBean.setStatus("ok");
						resultBean.setMessage("日程\""+mm.get("name")+"\"");
					}else{
						resultBean.setStatus("ok");
						resultBean.setMessage(list.size()+"个日程");
					}
					m.put("ctCount", list.size());
					resultBean.setMap(m);
				}else{
					resultBean.setStatus("error");
				}
			}
		}catch (Exception e) {
			log.error(ScheduleController.class.getName() + "_ctSchedule",e);
		}
		return resultBean;
	}
	
	/**
	 * 判断日程是否超过20条
	 * @param record
	 * @return
	 */
	@RequestMapping("getCountSchedule")
	@ResponseBody
	public Object getCountSchedule(HttpServletRequest request,HttpServletResponse response,@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setStatus("error");
		try {
			//获取登录用户信息
			SessionBean bean = CUtils.get().getBeanBySession(request);
			Map<String,Object> map = CUtils.get().jsonString2map(paramString);
			map.put("createdId", bean.getGuserid());
			String ss = service.getCountSchedule(map);
			if(ss==null){
				resultBean.setStatus("OK");
			}else{
				resultBean.setStatus("error");
				resultBean.setMessage(ss);
			}
		}catch (Exception e) {
			log.error(ScheduleController.class.getName() + "getCountSchedule",e);
		}
		return resultBean;
		
	}
	
	/**
	 * 添加其他日程
	 */
	@ResponseBody
	@RequestMapping("saveOtherSchedule")
	public Object saveOtherSchedule(@RequestBody ScheduleInfo scheduleInfo,HttpServletRequest request){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setFlag(0);
		
		//@SuppressWarnings("unchecked")
		//RedisCacheImpl<String,Object> cache = (RedisCacheImpl<String,Object>)StaticConst.ctx.getBean("cache");
		//获取登录用户信息
		SessionBean bean = CUtils.get().getBeanBySession(request);
		//Map<String, Object> user = BeanUtils.toMap(cache.get(bean.getSessionid()));
		if(scheduleInfo.getName()==null){
			resultBean.setMessage("日程名称不能为空");
			return resultBean;
		}
		try {
			int updateCount = 0;
			Long id = 0L;
			if(scheduleInfo!=null && scheduleInfo.getId()!=null && scheduleInfo.getId()!=0){
				//更新其他日程
				scheduleInfo.setUpdatedId(bean.getGuserid());
				updateCount = service.updateById(scheduleInfo);
			}else{
				//保存其他日程
				scheduleInfo.setCreatedId(bean.getGuserid());
				id = service.insert(scheduleInfo);
				//scheduleInfo.setMessageType("1.3.1");
				//scheduleInfo.setId(id);
				//scheduleInfo.setUserName(user.get("realName").toString());
				//返给客户端id和状态
				//resultBean.setId(id);
				//创建其他日程后发消息
				//messageService.operateMessageBySaveInfo(scheduleInfo);
			}
			if(updateCount!=0 || id!=0L){
				resultBean.setFlag(1);
			}
			resultBean.setStatus("OK");
		} catch (Exception e) {
			log.error(ScheduleController.class.getName() + "_saveOtherSchedule",e);
		}
		return resultBean;
	}
	
	/**
	 * 删除其他日程
	 */
	@ResponseBody
	@RequestMapping("deleteOtherScheduleById")
	public Object deleteOtherSchedule(@RequestBody String paramString,HttpServletRequest request){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setFlag(0);
		try {
			Map<String,Object> map = CUtils.get().jsonString2map(paramString);
			Map<String, Object> ss = service.selectOtherScheduleById(map);
			if(ss!=null){
				//判断是否过期
				Long st= DateUtil.stringToLong(CUtils.get().object2String(ss.get("startTime")), "yyyy-MM-dd HH:mm");
				if(System.currentTimeMillis()>st){
					resultBean.setMessage("此日程过期了");
					return resultBean;
				}	
			}
			//逻辑删除
			if(map!=null){
				map.put("updatedTime", DateUtil.getMillis(new Date()));
				SessionBean sessionBean = CUtils.get().getBeanBySession(request);
				map.put("updatedId", sessionBean.getGuserid());
				boolean flag = service.deleteOtherScheduleById(map);
				
				Long id = CUtils.get().object2Long(map.get("id"), 0L);
				/*if(id!=0){
					pushDeleteCallon(request, id);
				}*/
				
				if(flag){
					resultBean.setFlag(1);
				}
			}
			resultBean.setStatus("OK");
		} catch (Exception e) {
			log.error(ScheduleController.class.getName() + "_deleteOtherSchedule",e);
		}
		return resultBean;
	}
	
	/**
	 * 其他日程详情
	 */
	@ResponseBody
	@RequestMapping("selectOtherScheduleById")
	public Object selectOtherScheduleById(@RequestBody String paramString,HttpServletRequest request){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		
		@SuppressWarnings("unchecked")
		RedisCacheImpl<String,Object> cache = (RedisCacheImpl<String,Object>)StaticConst.ctx.getBean("cache");
		//获取登录用户信息
		SessionBean bean = CUtils.get().getBeanBySession(request);
		Map<String, Object> user = BeanUtils.toMap(cache.get(bean.getSessionid()));
		try {
			Map<String,Object> map = CUtils.get().jsonString2map(paramString);
			Map<String, Object> mapList = service.selectOtherScheduleById(map);
			if(mapList==null){
				resultBean.setMessage("日称不存在");
				return resultBean;
			}
			//为了防止共享的人查看详情
			if(mapList.get("createdId")!=CUtils.get().object2Long(user.get("id"))){
				resultBean.setMessage("没有查看权限");
				return resultBean;				
			}
			if(mapList.get("startTime")!=null){
				
				String starttime = dateStrformat(CUtils.get().object2String(mapList.get("startTime")));
				mapList.put("startTime", starttime);
			}
			if(mapList.get("endTime")!=null){
				
				String endTime = dateStrformat(CUtils.get().object2String(mapList.get("endTime")));
				mapList.put("endTime", endTime);
			}
			if (mapList.get("wakeupId")!=null) {
				ScheduleDict scheduleDict = scheduleDictService.queryById(CUtils.get().object2Long(mapList.get("wakeupId")));
				mapList.put("remind", scheduleDict.getName());
			}
			resultBean.setMap(mapList);;
			resultBean.setStatus("OK");
		} catch (Exception e) {
			log.error(ScheduleController.class.getName() + "_selectOtherScheduleById",e);
		}
		return resultBean;
	}
	
	/**
	 * 查询字典表
	 * @param ScheduleDict
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("selectScheduleDict")
	public Object selectScheduleDict(@RequestBody ScheduleDict scheduleDict,HttpServletRequest request){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		if(scheduleDict.getDirection()==null){
			scheduleDict.setDirection("asc");
		}
		if(scheduleDict.getProperty()==null){
			scheduleDict.setProperty("index_num");
		}
		try {
			List<ScheduleDict> list = scheduleDictService.queryList(scheduleDict);
			resultBean.setEntity(list);
			resultBean.setStatus("OK");
		} catch (Exception e) {
			log.error(ScheduleController.class.getName() + "_selectScheduleDict",e);
		}
		return resultBean;
	}
	
	
	/**
	 * 转化日期
	 * @param dateStr
	 * @return
	 */
	public static String dateStrformat(String dateStr){  //2016-05-27 16:00:00   19
		
		if( dateStr.indexOf("-") != -1){
			dateStr = dateStr.replaceAll("-", "/");
		}
		String dateStrs = dateStr.substring(0,16);
		
		return dateStrs;
	}
}