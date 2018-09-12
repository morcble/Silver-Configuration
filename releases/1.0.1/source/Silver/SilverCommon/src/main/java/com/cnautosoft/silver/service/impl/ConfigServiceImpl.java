package com.cnautosoft.silver.service.impl;

import java.util.Date;
import java.util.List;
import com.cnautosoft.h2o.annotation.Service;
import com.cnautosoft.h2o.annotation.tag.Autowired;
import com.cnautosoft.h2o.common.Logger;
import com.cnautosoft.h2o.core.ids.IDGenerator;
import com.cnautosoft.h2o.data.persistence.Transactional;
import com.cnautosoft.h2o.web.wrapper.PaginationTO;
import com.cnautosoft.silver.core.ConfigChangedEventListener;
import com.cnautosoft.silver.dao.ConfigDao;
import com.cnautosoft.silver.entity.Config;
import com.cnautosoft.silver.service.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService{
	private static final Logger logger = Logger.getLogger(ConfigServiceImpl.class);
	
	@Autowired
	private ConfigDao configDao;
	
	@Override
	public Config create(Config entity,String operator) throws Exception {
		entity.setId(IDGenerator.getStringID());
		
		Date now = new Date();
		entity.setUpdateDt(now);
		entity.setCreateDt(now);
		entity.setUpdateBy(operator);
		entity.setCreateBy(operator);
		return configDao.insert(entity);
	}
	
	@Override
	public Config getByID(String primaryKey) throws Exception {
		if(primaryKey==null)return null;
		Config entity = configDao.find(primaryKey);
		if(entity==null||entity.getSoftDelete()!=0)return null;
		else return entity;
	}
	
	@Override
	public Config update(Config entity,String operator) throws Exception{
		entity.setUpdateDt(new Date());
		entity.setUpdateBy(operator);
		Config result = configDao.update(entity,"properties","updateDt","updateBy");
		
		ConfigChangedEventListener.Event event = new ConfigChangedEventListener.Event();
		ConfigChangedEventListener.EventData evtData = new ConfigChangedEventListener.EventData(result.getGroupId(),result.getItemId(),result.getProperties());
		event.setData(evtData);
		event.publish();
		
		return result;
	}
	
	@Override
	public void deleteByID(String primaryKey,String operator)  throws Exception{
		if(primaryKey==null)throw new Exception("primaryKey is null");
		Config entity = configDao.find(primaryKey);
		entity.setSoftDelete(1);
		entity.setUpdateDt(new Date());
		entity.setUpdateBy(operator);
		configDao.update(entity, "softDelete","updateDt","updateBy");
	}
	
	@Override
	@Transactional(rollBackFor=Exception.class)
	public void deleteAll(String operator) throws Exception{
		List<Config> ls = findAll();
		for(Config tmp:ls){
			deleteByID(tmp.getId(),operator);
		}
	}
	
	@Override
	public List<Config> findAll() throws Exception{
		 return (List<Config>) configDao.findAll();
	}
	
	@Override
	@Transactional(rollBackFor=Exception.class)
	public void batchDelete(String[] deleteIds,String operator) throws Exception{
		for(String tempId:deleteIds){
			deleteByID(tempId,operator);
		}
	}
	
	@Override
	public List<Config> getList(Integer pageNo ,Integer pageSize ,String orderBy,Integer softDelete
				,String groupId
				,String itemId
				,String properties
		) throws Exception{
		return configDao.getList(pageNo,pageSize,orderBy,softDelete,groupId,itemId,properties);
	}
	
	@Override
	public Long getAmount(Integer softDelete
				,String groupId
				,String itemId
				,String properties
			) throws Exception{		
		return configDao.getAmount(softDelete,groupId,itemId,properties);
	}
}
