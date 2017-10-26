package com.galaxy.im.business.rili.dao;

import java.util.List;
import java.util.Map;

import com.galaxy.im.bean.schedule.ScheduleInfo;
import com.galaxy.im.common.db.IBaseDao;

public interface IScheduleDao extends IBaseDao<ScheduleInfo, Long>{

	//获取日程冲突list
	List<Map<String, Object>> getCTSchedule(Map<String, Object> map);
	//判断是否超过20条
	List<Map<String, Object>> getCountSchedule(Map<String, Object> map);
	//删除日程
	int delCallonById(Map<String, Object> paramMap);
	//根据id查询详情
	Map<String, Object> selectOtherScheduleById(Map<String, Object> map);

}