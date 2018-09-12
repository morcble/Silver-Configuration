package com.cnautosoft.silver.entity;

import java.io.Serializable;
import com.cnautosoft.h2o.data.persistence.Column;
import com.cnautosoft.h2o.data.persistence.Entity;
import com.cnautosoft.h2o.data.persistence.Id;
import com.cnautosoft.h2o.data.persistence.Inheritance;
import com.cnautosoft.h2o.data.persistence.InheritanceType;
import com.cnautosoft.h2o.data.persistence.Table;
import com.cnautosoft.h2o.data.persistence.BaseEntityWithStringID;
import com.cnautosoft.h2o.data.persistence.Text;
import java.util.Date;


@Entity
@Table(name = "m_config")
public class Config extends BaseEntityWithStringID implements Serializable{
	 @Column(name = "groupId" ,length = 50)
	 private String groupId;
	 
	 @Column(name = "itemId" ,length = 50)
	 private String itemId;
	 
	 @Text
	 @Column(name = "properties")
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
