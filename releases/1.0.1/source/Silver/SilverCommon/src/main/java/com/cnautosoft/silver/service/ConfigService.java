package com.cnautosoft.silver.service;

import java.util.List;
import com.cnautosoft.silver.entity.Config;
import java.util.Date;

public interface ConfigService {
	Config create(Config instance,String operator) throws Exception;
	Config getByID(String primaryKey) throws Exception;
	Config update(Config instance,String operator) throws Exception;
	void deleteByID(String primaryKey, String operator) throws Exception;
	void deleteAll(String operator) throws Exception;
	void batchDelete(String[] deleteIds, String operator) throws Exception;
	List<Config> getList(Integer pageNo, Integer pageSize, String orderBy, Integer softDelete,String groupId,String itemId,String properties) throws Exception;
	List<Config> findAll() throws Exception;
	Long getAmount(Integer softDelete,String groupId,String itemId,String properties) throws Exception;
}
