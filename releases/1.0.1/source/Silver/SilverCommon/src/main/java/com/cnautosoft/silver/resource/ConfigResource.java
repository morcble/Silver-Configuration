package com.cnautosoft.silver.resource;

import java.util.List;
import com.cnautosoft.silver.dto.ConfigDto;
import com.cnautosoft.h2o.web.core.ResourceRequest;
import com.cnautosoft.h2o.web.core.ResourceResponse;
import com.cnautosoft.h2o.web.wrapper.PaginationTO;

public interface ConfigResource {
	ResourceResponse<ConfigDto> create(ConfigDto dto, String operator);

	ResourceResponse<ConfigDto> getByID(String primaryKey);
 
	ResourceResponse<ConfigDto> update(ConfigDto configTO, String operator);

	ResourceResponse<?> deleteByID(String primaryKey, String operator);

	ResourceResponse<?> batchDelete(String[] deleteIds, String operator);

	ResourceResponse<List<ConfigDto>> findAll();

	ResourceResponse<?> deleteAll(String operator);

	ResourceResponse<PaginationTO> getList(ResourceRequest<ConfigDto> listRequest);

}
