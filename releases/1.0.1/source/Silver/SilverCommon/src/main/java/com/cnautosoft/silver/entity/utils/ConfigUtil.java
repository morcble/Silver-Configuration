package com.cnautosoft.silver.entity.utils;

import java.util.ArrayList;
import java.util.List;
import com.cnautosoft.silver.dto.ConfigDto;
import com.cnautosoft.silver.entity.Config;


public class ConfigUtil{
	/**
	 * copy utils
	 */
	public static void copyDtoToEntity(ConfigDto dto,Config config){
		config.setGroupId(dto.getGroupId());		
		config.setItemId(dto.getItemId());		
		config.setProperties(dto.getProperties());		
		config.setId(dto.getId());	
		config.setCreateBy(dto.getCreateBy());
		config.setCreateDt(dto.getCreateDt());
		config.setUpdateBy(dto.getUpdateBy());
		config.setUpdateDt(dto.getUpdateDt());
		config.setVersion(dto.getVersion());
		config.setSoftDelete(dto.getSoftDelete());
	}
	
	
	public static  void copyEntityToDto(Config config,ConfigDto dto){
		dto.setGroupId(config.getGroupId());	
		dto.setItemId(config.getItemId());	
		dto.setProperties(config.getProperties());	
		dto.setId(config.getId());	
		dto.setCreateBy(config.getCreateBy());
		dto.setCreateDt(config.getCreateDt());
		dto.setUpdateBy(config.getUpdateBy());
		dto.setUpdateDt(config.getUpdateDt());
		dto.setVersion(config.getVersion());
		dto.setSoftDelete(config.getSoftDelete());
	}
	
	public static  void copyEntityToDto2(Config config,ConfigDto dto){
		dto.setGroupId(config.getGroupId());	
		dto.setItemId(config.getItemId());	
		dto.setId(config.getId());	
		dto.setCreateBy(config.getCreateBy());
		dto.setCreateDt(config.getCreateDt());
		dto.setUpdateBy(config.getUpdateBy());
		dto.setUpdateDt(config.getUpdateDt());
		dto.setVersion(config.getVersion());
		dto.setSoftDelete(config.getSoftDelete());
	}
	
	public static  List<ConfigDto> copyListPropertiesFromEntityToDto(List<Config> src){
		if(src==null)return null;
		List<ConfigDto> ls = new ArrayList<ConfigDto>();
		for(Config entity:src){
			ConfigDto dto = new ConfigDto();
			copyEntityToDto2(entity,dto);
			ls.add(dto);
		}
		return ls;
	}
}
