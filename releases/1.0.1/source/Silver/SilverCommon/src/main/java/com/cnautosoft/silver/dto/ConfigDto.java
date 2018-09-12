package com.cnautosoft.silver.dto;
 
import com.cnautosoft.h2o.web.core.BaseDtoWithStringID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.cnautosoft.h2o.common.Constants;
import com.cnautosoft.h2o.common.Logger;
import com.cnautosoft.h2o.core.CommonUtil;

public class ConfigDto extends BaseDtoWithStringID{
	private static final Logger logger = Logger.getLogger(ConfigDto.class);
	 private String groupId;
	 
	 private String itemId;
	 
	 private String properties;
	 
	 public String getGroupId() {
		 return groupId;
	 }

	 public void setGroupId(String groupId) {
		 this.groupId = groupId;
	 }
	 
	 public String getItemId() {
		 return itemId;
	 }

	 public void setItemId(String itemId) {
		 this.itemId = itemId;
	 }
	 
	 public String getProperties() {
		 return properties;
	 }

	 public void setProperties(String properties) {
		 this.properties = properties;
	 }
	 
}
