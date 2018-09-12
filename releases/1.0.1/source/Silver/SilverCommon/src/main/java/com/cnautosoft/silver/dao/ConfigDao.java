package com.cnautosoft.silver.dao;

import com.cnautosoft.h2o.annotation.Dao;
import com.cnautosoft.h2o.common.Logger;
import com.cnautosoft.h2o.core.CountResult;
import com.cnautosoft.h2o.core.H2ODao;
import com.cnautosoft.silver.entity.Config;
import com.cnautosoft.h2o.core.CommonUtil;
import java.util.List;
import java.util.Date;

@Dao
public class ConfigDao extends H2ODao<Config,String>{
	private static final Logger logger = Logger.getLogger(ConfigDao.class);
	
	public List<Config> getList(Integer pageNo ,Integer pageSize, String orderBy, Integer softDelete
			,String groupId,String itemId,String properties
					) throws Exception{
		String tableName = getBindTableName();
		
		StringBuilder sqlBuf = new StringBuilder("select * from ");
		sqlBuf.append(tableName);
		sqlBuf.append(" where 1=1 ");
		
		int paraCount = 0;
		if(softDelete!=null){
			sqlBuf.append(" and softDelete = ?");
			paraCount++;
		}
		
		if(!CommonUtil.isEmpty(groupId)){
			sqlBuf.append(" and groupId = ?");
		  paraCount++;
		}
		else{
			groupId = null;
		}
		if(!CommonUtil.isEmpty(itemId)){
			sqlBuf.append(" and itemId = ?");
		  paraCount++;
		}
		else{
			itemId = null;
		}
		if(!CommonUtil.isEmpty(properties)){
			sqlBuf.append(" and properties = ?");
		  paraCount++;
		}
		else{
			properties = null;
		}
		


		if(CommonUtil.isEmpty(orderBy)){
			sqlBuf.append(" order by id desc ");
		}
		else{
			orderBy = orderBy.replace("'", "").replace("%", "");
			sqlBuf.append(" order by "+orderBy);
		}
	
		Object[] sqlParas =  new Object[paraCount];
		paraCount = 0;
		if(softDelete!=null){
			sqlParas[paraCount] = softDelete;
			paraCount++;
		}
		
		if(!CommonUtil.isEmpty(groupId)){
		  	sqlParas[paraCount] = groupId;
		  paraCount++;
		}
		if(!CommonUtil.isEmpty(itemId)){
		  	sqlParas[paraCount] = itemId;	
		  paraCount++;
		}
		if(!CommonUtil.isEmpty(properties)){
		  	sqlParas[paraCount] = properties;	
		  paraCount++;
		}
		
		List<Config> result = getEntityList(sqlBuf.toString(),sqlParas,pageNo, pageSize);
		return result;
	}
	
	
	public Long getAmount(Integer softDelete ,String groupId,String itemId,String properties) throws Exception{		
		String tableName = getBindTableName();
		
		StringBuilder sqlBuf = new StringBuilder("select count(1) as count from ");
		
		sqlBuf.append(tableName);
		sqlBuf.append(" where 1=1 ");
		
		int paraCount = 0;
		if(softDelete!=null){
			sqlBuf.append(" and softDelete = ?");
			paraCount++;
		}
		
		if(!CommonUtil.isEmpty(groupId)){
			sqlBuf.append(" and groupId like ?");
		  paraCount++;
		}
		else{
			groupId = null;
		}
		if(!CommonUtil.isEmpty(itemId)){
			sqlBuf.append(" and itemId = ?");
		  paraCount++;
		}
		else{
			itemId = null;
		}
		if(!CommonUtil.isEmpty(properties)){
			sqlBuf.append(" and properties = ?");
		  paraCount++;
		}
		else{
			properties = null;
		}
	
		Object[] sqlParas =  new Object[paraCount];
		paraCount = 0;
		if(softDelete!=null){
			sqlParas[paraCount] = softDelete;
			paraCount++;
		}
		
		if(!CommonUtil.isEmpty(groupId)){
		  	sqlParas[paraCount] = "%"+groupId+"%";
		  paraCount++;
		}
		if(!CommonUtil.isEmpty(itemId)){
		  	sqlParas[paraCount] = itemId;
		  paraCount++;
		}
		if(!CommonUtil.isEmpty(properties)){
		  	sqlParas[paraCount] = properties;
		  paraCount++;
		}
		
		CountResult countResult = getEntityManager().getObject(sqlBuf.toString(), sqlParas, CountResult.class);
		return countResult.getCount();
	}
}
