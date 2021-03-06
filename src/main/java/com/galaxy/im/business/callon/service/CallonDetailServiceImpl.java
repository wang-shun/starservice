package com.galaxy.im.business.callon.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxy.im.bean.schedule.ScheduleDetailBean;
import com.galaxy.im.bean.schedule.ScheduleDetailBeanVo;
import com.galaxy.im.business.callon.dao.ICallonDetailDao;
import com.galaxy.im.common.db.IBaseDao;
import com.galaxy.im.common.db.service.BaseServiceImpl;
import com.galaxy.im.common.exception.ServiceException;

@Service
public class CallonDetailServiceImpl extends BaseServiceImpl<ScheduleDetailBean> implements ICallonDetailService{
	private Logger log = LoggerFactory.getLogger(CallonDetailServiceImpl.class);
	
	@Autowired
	private ICallonDetailDao dao;
	@Override
	protected IBaseDao<ScheduleDetailBean, Long> getBaseDao() {
		return dao;
	}
	
	/**
	 * 获取字典表信息
	 */
	@Override
	public Map<String, Object> getDictInfo() {
		try{
			Map<String,Object> resultMap = null;
			
			//获取重要性list
			List<Map<String,Object>> significanceList = dao.getSignificanceDictList();
			//获取提醒通知list
			List<Map<String,Object>> scheduleList = dao.getScheduleDictList();
			
			resultMap = new HashMap<String,Object>();
			resultMap.put("significance", significanceList);
			resultMap.put("schedule", scheduleList);
			
			return resultMap;
		}catch(Exception e){
			log.error(CallonDetailServiceImpl.class.getName() + ":getDictInfo",e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取历史访谈记录个数
	 */
	@Override
	public long getTalkHistoryCounts(ScheduleDetailBeanVo detail) {
		try{
			return dao.getTalkHistoryCounts(detail);
		}catch(Exception e){
			log.error(CallonDetailServiceImpl.class.getName() + "_getTalkHistoryCounts",e);
			throw new ServiceException(e);
		}
		
	}

	/**
	 * 拜访详情
	 */
	@Override
	public List<ScheduleDetailBean> getQueryById(long callonId) {
		try{
			return dao.getQueryById(callonId);
		}catch(Exception e){
			log.error(CallonDetailServiceImpl.class.getName() + "_getQueryById",e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 运营会议历史个数
	 */
	@Override
	public long getPosMeetingCount(Map<String, Object> paramMap) {
		try{
			return dao.getPosMeetingCount(paramMap);
		}catch(Exception e){
			log.error(CallonDetailServiceImpl.class.getName() + "getPosMeetingCount",e);
			throw new ServiceException(e);
		}
		
	}

}
