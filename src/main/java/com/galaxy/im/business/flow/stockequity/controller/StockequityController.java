package com.galaxy.im.business.flow.stockequity.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxy.im.business.flow.common.service.IFlowCommonService;
import com.galaxy.im.business.flow.investmentintent.service.IInvestmentintentService;
import com.galaxy.im.business.flow.stockequity.service.IStockequityService;
import com.galaxy.im.common.CUtils;
import com.galaxy.im.common.ResultBean;
import com.galaxy.im.common.StaticConst;

/**
 * 股权交割
 * 
 * @author 
 */
@Controller
@RequestMapping("/flow/stockequity")
public class StockequityController {
	@Autowired
	private IFlowCommonService fcService;
	
	@Autowired
	IInvestmentintentService service;
	
	@Autowired
	IStockequityService isservice;
	/**
	 * 判断项目操作按钮状态
	 * 依据：需要上传完成工商转让凭证
	 * 前端处理：满足条件则"进入投后运营"按钮点亮;
	 * 
	 * @param
	 * 	projectId   项目ID-使用JSON的方式传递
	 * @return
	 * 	entity ->  pass:true,
	 *  
	 */
	
	@RequestMapping("poButonStatus")
	@ResponseBody
	public Object projectOperateStatus(@RequestBody String paramString){
		ResultBean<Object> result = new ResultBean<Object>();
		try{
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);
			Map<String,Object> m = isservice.operateStatus(paramMap);
			if(CUtils.get().mapIsNotEmpty(m)){
				result.setEntity(m);
			}
			//项目最后上传的文件信息
			paramMap.put("fileWorkType", StaticConst.FILE_WORKTYPE_8);
			Map<String,Object> map = fcService.getLatestSopFileInfo(paramMap);
			if(CUtils.get().mapIsNotEmpty(map)){
				result.setMap(map);
			}
			result.setStatus("OK");
		}catch(Exception e){
		}
		return result;
	}
	
	/**
	 * 进入投后运营
	 * @param paramString
	 * @return
	 */

	@RequestMapping("startIntervestoperate")
	@ResponseBody
	public Object startIntervestoperate(@RequestBody String paramString){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		Map<String, Object> map = new HashMap<>();
		resultBean.setFlag(0);
		try{
			Map<String,Object> paramMap = CUtils.get().jsonString2map(paramString);
			if(CUtils.get().mapIsNotEmpty(paramMap)){
				paramMap.put("projectProgress", StaticConst.PROJECT_PROGRESS_10);	//表示进入投后运营阶段
				paramMap.put("projectStatus", "projectStatus:1");//将项目状态调整为“投后运营”
				if(isservice.enterNextFlow(paramMap)){
					resultBean.setFlag(1);
					map.put("projectProgress", StaticConst.PROJECT_PROGRESS_10);
				}
			}
			resultBean.setStatus("OK");
			resultBean.setMap(map);
		}catch(Exception e){
		}
		return resultBean;
	}
}
