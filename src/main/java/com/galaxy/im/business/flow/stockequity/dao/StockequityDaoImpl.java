package com.galaxy.im.business.flow.stockequity.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.galaxy.im.bean.Test;
import com.galaxy.im.common.db.BaseDaoImpl;
import com.galaxy.im.common.exception.DaoException;

@Repository
public class StockequityDaoImpl extends BaseDaoImpl<Test, Long> implements IStockequityDao{
	private Logger log = LoggerFactory.getLogger(StockequityDaoImpl.class);
	
	public StockequityDaoImpl(){
		super.setLogger(log);
	}
	
	/**
	 * 获取项目上传的所有文件的和个数
	 */
	@Override
	public List<Map<String, Object>> operateStatus(Map<String, Object> paramMap) {
		try{
			String sqlName = "com.galaxy.im.business.flow.stockequity.dao.IStockequityDao.operateStatus";
			List<Map<String, Object>> map =sqlSessionTemplate.selectList(sqlName,paramMap);
			return map;
		}catch(Exception e){
			log.error(StockequityDaoImpl.class.getName() + "：operateStatus",e);
			throw new DaoException(e);
		}
	}
	
}
