package com.galaxy.im.business.callon.dao;

import java.util.List;
import java.util.Map;

import com.galaxy.im.bean.schedule.ScheduleDetailBean;
import com.galaxy.im.common.db.IBaseDao;

public interface ICallonDetailDao extends IBaseDao<ScheduleDetailBean, Long>{

	//获取字典表信息
	List<Map<String, Object>> getSignificanceDictList();
	//获取提醒通知信息
	List<Map<String, Object>> getScheduleDictList();


}
