package com.galaxy.im.business.common.dict.service;

import java.util.List;
import java.util.Map;

import com.galaxy.im.bean.common.Dict;
import com.galaxy.im.common.db.service.IBaseService;

public interface IDictService extends IBaseService<Dict>{
	Map<String,Object> selectCallonFilter();

	Map<String, Object> selectResultFilter(Map<String, Object> paramMap);

	Map<String, Object> selectReasonFilter(Map<String, Object> paramMap);

	List<Dict> selectByParentCode(String string);

	List<Map<String, Object>> getDictionaryList(Map<String, Object> paramMap);

	Map<String, Object> getDictionaryFinanceList(Map<String, Object> paramMap);

}
