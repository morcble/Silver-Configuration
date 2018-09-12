package com.cnautosoft.silver.resource.impl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.cnautosoft.h2o.annotation.Resource;
import com.cnautosoft.h2o.annotation.tag.Autowired;
import com.cnautosoft.h2o.common.Logger;
import com.cnautosoft.h2o.core.H2OResourceImpl;
import com.cnautosoft.h2o.core.CommonUtil;
import com.cnautosoft.h2o.web.core.ResourceRequest;
import com.cnautosoft.h2o.web.core.ResourceResponse;
import com.cnautosoft.h2o.web.core.RespCode;
import com.cnautosoft.h2o.web.wrapper.PaginationTO;
import com.cnautosoft.h2o.web.wrapper.ResourceResWrapper;
import com.cnautosoft.silver.dto.ConfigDto;
import com.cnautosoft.silver.entity.Config;
import com.cnautosoft.silver.resource.ConfigResource;
import com.cnautosoft.silver.service.ConfigService;
import com.cnautosoft.silver.service.impl.ConfigServiceImpl;
import com.cnautosoft.h2o.core.exception.ValidationException;
import com.cnautosoft.silver.entity.utils.ConfigUtil;


@Resource
public class ConfigResourceImpl extends H2OResourceImpl implements ConfigResource{
	private static final Logger logger = Logger.getLogger(ConfigResourceImpl.class);
	
	@Autowired(targetClass = ConfigServiceImpl.class)
	private ConfigService configService;
	
	/**
	 * backend common validation 
	 * @param dto
	 * @throws Exception
	 */
	private void valid(ConfigDto dto) throws Exception{
		if(dto.getId()==null) {
			if(CommonUtil.isEmpty(dto.getGroupId()))throw new ValidationException(getMessage("silver.config.groupId.isnull"));
		    if(CommonUtil.isEmpty(dto.getItemId()))throw new ValidationException(getMessage("silver.config.itemId.isnull"));
		    
		   //check duplicate groupid and itemid
		    String groupId = dto.getGroupId();
		    String itemId = dto.getItemId();
		    
		    List<Config> exsits = configService.getList(1, 10, null, PaginationTO.NOT_DELETED, groupId, itemId, null);
		    if(exsits!=null&&exsits.size()>0)throw new ValidationException("Group ID and Item Id already exsit");
		}
		
		
	}
	
	
	@Override
	public ResourceResponse<ConfigDto> create(ConfigDto dto,String operator){
		try{
			valid(dto);
			Config config = new Config();
			ConfigUtil.copyDtoToEntity(dto, config);
			config = configService.create(config, operator);
			ConfigDto result = new ConfigDto();
			ConfigUtil.copyEntityToDto(config, result);
			return ResourceResWrapper.successResult(getMessage("operation.successfully"), result);
		}
		catch(ValidationException e){
			logger.warn(e);
			return ResourceResWrapper.failResult(getMessage("operation.failed")+"  :  "+e.getMessage(), dto, RespCode._700);
		}
		catch(Exception e){
			logger.error(e);
			return ResourceResWrapper.failResult(getMessage("operation.failed")+"  :  "+e.getMessage(), dto, RespCode._500);
		}
	}
	
	@Override
	public ResourceResponse<ConfigDto> getByID(String primaryKey) {
		try{
			Config config = configService.getByID(primaryKey);
			if(config==null)return ResourceResWrapper.successResult("Record not found, primaryKey:"+primaryKey,null,RespCode._500);
			ConfigDto result = new ConfigDto();
			ConfigUtil.copyEntityToDto(config, result);
			return ResourceResWrapper.successResult(null, result);
		} 
		catch(Exception e){
			logger.error(e, "getByID");
			return ResourceResWrapper.failResult(e.getMessage(), null, RespCode._500);
		}
	}
	
	@Override
	public ResourceResponse<ConfigDto> update(ConfigDto dto,String operator){
		try{
			valid(dto);
			Config config = new Config();
			ConfigUtil.copyDtoToEntity(dto, config);
			config = configService.update(config, operator);
			if(config==null)return null;
			ConfigDto result = new ConfigDto();
			ConfigUtil.copyEntityToDto(config, result);
			
			return ResourceResWrapper.successResult(getMessage("operation.successfully"), result);
		}
		catch(ValidationException e){
			logger.warn(e);
			return ResourceResWrapper.failResult(getMessage("operation.failed")+"  :  "+e.getMessage(), dto, RespCode._700);
		}
		catch(Exception e){
			logger.error(e);
			return ResourceResWrapper.failResult(getMessage("operation.failed")+": "+e.getMessage(), dto, RespCode._500);
		}
	}
	
	@Override
	public ResourceResponse<?> deleteByID(String primaryKey,String operator){
		try{
			configService.deleteByID(primaryKey, operator);
			return ResourceResWrapper.successResult(getMessage("operation.successfully"), null);
		} 
		catch(Exception e){
			logger.error(e);
			return ResourceResWrapper.failResult(getMessage("operation.failed")+"  :  "+e.getMessage(), null, RespCode._500);
		}
	}
	
	@Override
	public ResourceResponse<?> batchDelete(String[] deleteIds,String operator){
		try{
			configService.batchDelete(deleteIds, operator);
			return ResourceResWrapper.successResult(getMessage("operation.successfully"), null);
		}
		catch(Exception e){
			logger.error(e);
			return ResourceResWrapper.failResult(getMessage("operation.failed")+"  :  "+e.getMessage(), null, RespCode._500);
		}
	}
	
	@Override
	public ResourceResponse<List<ConfigDto>> findAll(){
		try{
			List<Config> configs = configService.findAll();
			List<ConfigDto> result = ConfigUtil.copyListPropertiesFromEntityToDto(configs);
			return ResourceResWrapper.successResult(null, result);
		} 
		catch(Exception e){
			logger.error(e);
			return ResourceResWrapper.failResult(e.getMessage(), null, RespCode._500);
		}
	}
	
	@Override
	public ResourceResponse<?> deleteAll(String operator){
		try{
			configService.deleteAll(operator);
			return ResourceResWrapper.successResult(null, null);
		} 
		catch(Exception e){
			logger.error(e);
			return ResourceResWrapper.failResult(e.getMessage(), null, RespCode._500);
		}
	}
	
	
	@Override
	public ResourceResponse<PaginationTO> getList(ResourceRequest<ConfigDto> listRequest){
		try{
			ConfigDto dto = listRequest.getData();
			Long amount = configService.getAmount(PaginationTO.NOT_DELETED 
						,dto.getGroupId()
						,dto.getItemId()
						,null
			);
			
			PaginationTO paginationTO = listRequest.getPaginationTO();
	    	String orderBy = paginationTO.getOrderBy();
	    	if(CommonUtil.isEmpty(orderBy)){
	    		orderBy = "createDt desc";
	    		paginationTO.setOrderBy(orderBy);
	    	}
			int totalAmount = 0;
	    	int totalPageAmount = 0;
	    	int currentPageNo = paginationTO.getCurrentPageNo();
			paginationTO.setTotalAmount(amount.intValue());
			totalAmount = paginationTO.getTotalAmount();
			if(totalAmount==0){
				paginationTO.setCurrentPageNo(0);
				paginationTO.setTotalPageAmount(0);
			}
			else{
				totalPageAmount = totalAmount / paginationTO.getPageSize();
				int temp = totalAmount % paginationTO.getPageSize();
				if (temp > 0)
					totalPageAmount++;

				if (currentPageNo < 1)
					currentPageNo = 1;
				if (currentPageNo > totalPageAmount)
					currentPageNo = totalPageAmount;
				paginationTO.setCurrentPageNo(currentPageNo);
				paginationTO.setTotalPageAmount(totalPageAmount);
				
				List<Config> configs = configService.getList(paginationTO.getCurrentPageNo(),paginationTO.getPageSize(),paginationTO.getOrderBy(), PaginationTO.NOT_DELETED
							,dto.getGroupId()
							,dto.getItemId()
							,null
						);
				List<ConfigDto> result = ConfigUtil.copyListPropertiesFromEntityToDto(configs);
				paginationTO.setList(result);
			}
			return ResourceResWrapper.successResult(null, paginationTO);
		} 
		catch(Exception e){
			logger.error(e);
			return ResourceResWrapper.failResult(e.getMessage(),null, RespCode._500);
		}
	}

}
