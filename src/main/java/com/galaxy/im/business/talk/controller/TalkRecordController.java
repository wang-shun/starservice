package com.galaxy.im.business.talk.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxy.im.bean.schedule.ScheduleDetailBean;
import com.galaxy.im.bean.talk.TalkRecordBean;
import com.galaxy.im.bean.talk.TalkRecordBeanVo;
import com.galaxy.im.bean.talk.TalkRecordDetailBean;
import com.galaxy.im.bean.talk.SopFileBean;
import com.galaxy.im.business.callon.service.ICallonDetailService;
import com.galaxy.im.business.talk.service.ITalkRecordDetailService;
import com.galaxy.im.business.talk.service.ITalkRecordService;
import com.galaxy.im.common.DateUtil;
import com.galaxy.im.common.ResultBean;
import com.galaxy.im.common.db.Page;
import com.galaxy.im.common.db.PageRequest;

@Controller
@RequestMapping("/talk")
public class TalkRecordController {
	private Logger log = LoggerFactory.getLogger(TalkRecordController.class);

	@Autowired
	private ICallonDetailService dService;
	@Autowired
	private ITalkRecordService service;
	@Autowired
	private ITalkRecordDetailService talkDetailService;
	
	
	/**
	 * 访谈历史记录
	 * @param record
	 * @return
	 */
	@RequestMapping("getTalkHistoryList")
	@ResponseBody
	public Object getTalkHistoryList(@RequestBody TalkRecordBeanVo record){
		ResultBean<TalkRecordBean> resultBean = new ResultBean<TalkRecordBean>();
		resultBean.setStatus("error");
		try {
			ScheduleDetailBean bean = dService.queryById(record.getCallonId());
			if(bean!=null){
				//关联项目不为空，取项目的历史访谈记录
				if(!"".equals(bean.getProjectName()) && bean.getProjectName()!=null){
					record.setProId(bean.getProjectId());
				}
				//访谈对象不为空，取访谈对象的历史访谈记录
				else if(!"".equals(bean.getContactName()) && bean.getContactName()!=null){
					record.setConId(bean.getContactId());
				}
			}
			//分页查询项目的访谈记录
			Page<TalkRecordBean> page = service.queryPageList(record,
					new PageRequest(record.getPageNum(),
							record.getPageSize(), 
							Direction.fromString(record.getDirection()),
							record.getProperty()));
			//查询结果放在List
			List<TalkRecordBean> list = new ArrayList<TalkRecordBean>();
			for(TalkRecordBean p : page.getContent()){
				if(bean.getProjectName()!=null){
					p.setProjectName(bean.getProjectName());
				}else{
					p.setProjectName("");
				}
				list.add(p);
			}
			//页面
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("pageNum", page.getPageable().getPageNumber());
			map.put("pageSize", page.getPageable().getPageSize());
			map.put("total", page.getTotal());
			
			resultBean.setStatus("OK");
			resultBean.setEntityList(list);
			resultBean.setMap(map);
		} catch (Exception e) {
			log.error(TalkRecordController.class.getName() + "：getTalkHistoryList",e);
		}
		return resultBean;
	}
	
	/**
	 * 访谈记录详情
	 * @param detail
	 * @return
	 */
	@RequestMapping("getTalkDetails")
	@ResponseBody
	public Object getTalkDetails(@RequestBody TalkRecordBeanVo detail){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setStatus("error");
		try{
			//通过id查记录信息
			TalkRecordDetailBean bean = talkDetailService.queryById(detail.getTalkRecordId());
			if(bean!=null){
				//拼接拜访人格式
				bean.setCallonName(bean.getScheduleName()+"("+bean.getProjectType()+")");
				bean.setProjectName(detail.getProjectName());
			}
			resultBean.setStatus("ok");
			resultBean.setEntity(bean);
		}catch(Exception e){
			log.error(TalkRecordController.class.getName() + "：getTalkDetails",e);
		}
		return resultBean;
	}
	
	/**
	 * 保存/编辑拜访记录
	 * @param paramString
	 * @return
	 */
	@RequestMapping("addTalkRecord")
	@ResponseBody
	public Object save(@RequestBody TalkRecordBean talkBean){
		ResultBean<Object> resultBean = new ResultBean<Object>();
		resultBean.setFlag(0);
		try{
			int updateCount = 0;
			Long id = 0L;
			if(talkBean!=null){
				//时间转换
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date date = dateFormat.parse(talkBean.getViewDateStr());
				talkBean.setViewDate(date);
				//访谈记录存在，进行更新操作，否则保存
				if(talkBean.getId()!=null && talkBean.getId()!=0){
					//保存sop_file
					if(!"".equals(talkBean.getFileKey()) && talkBean.getFileKey()!=null){
						SopFileBean sopFileBean =new SopFileBean();
						sopFileBean.setFileKey(talkBean.getFileKey());
						sopFileBean.setFileLength(talkBean.getFileLength());
						sopFileBean.setBucketName(talkBean.getBucketName());
						sopFileBean.setFileName(talkBean.getFileName());
						
						long sopId =service.saveSopFile(sopFileBean);
						//获取sopfile 主键
						if(sopId!=0){
							talkBean.setFileId(sopId);
						}
					}
					
					//更新
					talkBean.setUpdatedTime(DateUtil.getMillis(new Date()));
					updateCount = service.updateById(talkBean);
				}else{
					//保存
					id = service.insert(talkBean);
				}
			}
			
			if(updateCount!=0 || id!=0L){
				resultBean.setStatus("OK");
			}
			
		}catch(Exception e){
			log.error(TalkRecordController.class.getName() + "：addTalkRecord",e);
		}
		return resultBean;
	}

}