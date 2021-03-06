package com.galaxy.im.bean.project;

import java.util.List;
import java.util.Set;

import com.galaxy.im.common.db.BaseEntity;

public class InformationListdata extends BaseEntity{
	private static final long serialVersionUID = 1L;
	 private Long id;

	    private Long projectId;

	    private Long titleId;

	    private Long parentId;

	    private String code;

	    private String field1;

	    private String field2;

	    private String field3;

	    private String field4;

	    private String field5;

	    private String field6;

	    private String field7;

	    private String field8;

	    private String field9;

	    private String field10;
	    
	    private String field11;

	    private String field12;

	    private String field13;

	    private String field14;

	    private String field15;

	    private String field16;

	    private String field17;

	    private String field18;

	    private String field19;

	    private String field20;
	    
	    private String relateFileId;

	    private Integer isValid;

	    private Long createTime;

	    private Long createId;

	    private Long updateTime;

	    private Long updateId;
	    
	    private Set<String> titleIds;

	    private List<InformationListdata> dataList;

	    private List<InformationListdata> workList;

	    private List<InformationListdata> studyList;

	    private List<InformationListdata> startupList;

	    private List<String> codes;

	    private Set<String> ids;

	    private String property;

	    private  String direction;
	    
	    private String createUserName;
	    private String createTimeStr;
	    private String updateUserName;
	    private String updateTimeStr;

	    


	    public Set<String> getTitleIds()
		{
			return titleIds;
		}

		public void setTitleIds(Set<String> titleIds)
		{
			this.titleIds = titleIds;
		}

		public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public Long getProjectId() {
	        return projectId;
	    }

	    public void setProjectId(Long projectId) {
	        this.projectId = projectId;
	    }

	    public Long getTitleId() {
	        return titleId;
	    }

	    public void setTitleId(Long titleId) {
	        this.titleId = titleId;
	    }

	    public Long getParentId() {
	        return parentId;
	    }

	    public void setParentId(Long parentId) {
	        this.parentId = parentId;
	    }

	    public String getCode() {
	        return code;
	    }

	    public void setCode(String code) {
	        this.code = code;
	    }

	    public String getField1() {
	        return field1;
	    }

	    public void setField1(String field1) {
	        this.field1 = field1;
	    }

	    public String getField2() {
	        return field2;
	    }

	    public void setField2(String field2) {
	        this.field2 = field2;
	    }

	    public String getField3() {
	        return field3;
	    }

	    public void setField3(String field3) {
	        this.field3 = field3;
	    }

	    public String getField4() {
	        return field4;
	    }

	    public void setField4(String field4) {
	        this.field4 = field4;
	    }

	    public String getField5() {
	        return field5;
	    }

	    public void setField5(String field5) {
	        this.field5 = field5;
	    }

	    public String getField6() {
	        return field6;
	    }

	    public void setField6(String field6) {
	        this.field6 = field6;
	    }

	    public String getField7() {
	        return field7;
	    }

	    public void setField7(String field7) {
	        this.field7 = field7;
	    }

	    public String getField8() {
	        return field8;
	    }

	    public void setField8(String field8) {
	        this.field8 = field8;
	    }

	    public String getField9() {
	        return field9;
	    }

	    public void setField9(String field9) {
	        this.field9 = field9;
	    }

	    public String getField10() {
	        return field10;
	    }

	    public void setField10(String field10) {
	        this.field10 = field10;
	    }

	    public Integer getIsValid() {
	        return isValid;
	    }

	    public void setIsValid(Integer isValid) {
	        this.isValid = isValid;
	    }

	    public Long getCreateTime() {
	        return createTime;
	    }

	    public void setCreateTime(Long createTime) {
	        this.createTime = createTime;
	    }

	    public Long getCreateId() {
	        return createId;
	    }

	    public void setCreateId(Long createId) {
	        this.createId = createId;
	    }

	    public Long getUpdateTime() {
	        return updateTime;
	    }

	    public void setUpdateTime(Long updateTime) {
	        this.updateTime = updateTime;
	    }

	    public Long getUpdateId() {
	        return updateId;
	    }

	    public void setUpdateId(Long updateId) {
	        this.updateId = updateId;
	    }

	    public List<InformationListdata> getWorkList() {
	        return workList;
	    }

	    public void setWorkList(List<InformationListdata> workList) {
	        this.workList = workList;
	    }

	    public List<InformationListdata> getStudyList() {
	        return studyList;
	    }

	    public void setStudyList(List<InformationListdata> studyList) {
	        this.studyList = studyList;
	    }

	    public List<InformationListdata> getStartupList() {
	        return startupList;
	    }

	    public void setStartupList(List<InformationListdata> startupList) {
	        this.startupList = startupList;
	    }

	    public void setCodes(List<String> codes) {
	        this.codes = codes;
	    }

	    public List<String> getCodes() {
	        return codes;
	    }

		public Set<String> getIds() {
			return ids;
		}

		public void setIds(Set<String> ids) {
			this.ids = ids;
		}

	    public void setDataList(List<InformationListdata> dataList) {
	        this.dataList = dataList;
	    }

	    public List<InformationListdata> getDataList() {
	        return dataList;
	    }

	    public String getProperty() {
	        return property;
	    }

	    public void setProperty(String property) {
	        this.property = property;
	    }

	    public String getDirection() {
	        return direction;
	    }

	    public void setDirection(String direction) {
	        this.direction = direction;
	    }

		public String getCreateUserName()
		{
			return createUserName;
		}

		public void setCreateUserName(String createUserName)
		{
			this.createUserName = createUserName;
		}

		public String getCreateTimeStr()
		{
			return createTimeStr;
		}

		public void setCreateTimeStr(String createTimeStr)
		{
			this.createTimeStr = createTimeStr;
		}

		public String getUpdateUserName()
		{
			return updateUserName;
		}

		public void setUpdateUserName(String updateUserName)
		{
			this.updateUserName = updateUserName;
		}

		public String getUpdateTimeStr()
		{
			return updateTimeStr;
		}

		public void setUpdateTimeStr(String updateTimeStr)
		{
			this.updateTimeStr = updateTimeStr;
		}

		public String getField11() {
			return field11;
		}

		public void setField11(String field11) {
			this.field11 = field11;
		}

		public String getField12() {
			return field12;
		}

		public void setField12(String field12) {
			this.field12 = field12;
		}

		public String getField13() {
			return field13;
		}

		public void setField13(String field13) {
			this.field13 = field13;
		}

		public String getField14() {
			return field14;
		}

		public void setField14(String field14) {
			this.field14 = field14;
		}

		public String getField15() {
			return field15;
		}

		public void setField15(String field15) {
			this.field15 = field15;
		}

		public String getField16() {
			return field16;
		}

		public void setField16(String field16) {
			this.field16 = field16;
		}

		public String getField17() {
			return field17;
		}

		public void setField17(String field17) {
			this.field17 = field17;
		}

		public String getField18() {
			return field18;
		}

		public void setField18(String field18) {
			this.field18 = field18;
		}

		public String getField19() {
			return field19;
		}

		public void setField19(String field19) {
			this.field19 = field19;
		}

		public String getField20() {
			return field20;
		}

		public void setField20(String field20) {
			this.field20 = field20;
		}

		public String getRelateFileId() {
			return relateFileId;
		}

		public void setRelateFileId(String relateFileId) {
			this.relateFileId = relateFileId;
		}
}
