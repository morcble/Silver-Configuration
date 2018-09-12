package com.cnautosoft.silver.core;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import com.cnautosoft.h2o.annotation.QuickEventListener;
import com.cnautosoft.h2o.common.Logger;
import com.cnautosoft.h2o.core.CommonUtil;
import com.cnautosoft.h2o.event.BaseEvent;
import com.cnautosoft.h2o.event.BaseEventListener;

import io.netty.channel.Channel;

@QuickEventListener
public class ConfigChangedEventListener extends BaseEventListener<ConfigChangedEventListener.Event> {
	public static final Charset UTF_8 = Charset.forName("utf-8");
	
	public static Logger logger = Logger.getLogger(ConfigChangedEventListener.class);
    public ConfigChangedEventListener() {
    }

    @Override
    public void handleEvent(ConfigChangedEventListener.Event event) {
        EventData eventData = event.getData();
        try {
        	ConfigManager configManager = ConfigManager.getInstance();
        	String propertyKey = eventData.groupId +"," + eventData.getDataId();
        	
        	byte[] propertiesBytes = null;
        	if(CommonUtil.isEmpty(eventData.getProperties())) {
        		propertiesBytes = Util.intToByte4(0);
        	}
        	else {
        		propertiesBytes = eventData.getProperties().getBytes(UTF_8);
        	}
        	configManager.getConfigCache().put(propertyKey, propertiesBytes);
        	
        	Set<String> watchers = configManager.getConfigWatchers().get(propertyKey);
        	if(watchers==null) return;
        	
        	Map<String, Channel> channelMap = configManager.getChannelMap();
        	Channel tmpChannel = null;
        	for(String watchId:watchers) {
        		tmpChannel = channelMap.get(watchId);
        		if(tmpChannel!=null) {
        			tmpChannel.writeAndFlush(Util.intToByte4(propertiesBytes.length));
        			tmpChannel.writeAndFlush(propertiesBytes);
        		}
        	}
        	
		} catch (Exception e) {
			logger.error(e);
		}	
    }


    public static class Event extends BaseEvent<EventData> {
        private EventData data;

        @Override
        public EventData getData() {
            return data;
        }

        @Override
        public void setData(EventData data) {
            this.data = data;
        }

    }

    public static class EventData {
    	String groupId;
    	String dataId;
    	String properties;
    	
		public EventData(String groupId, String dataId, String properties) {
			super();
			this.groupId = groupId;
			this.dataId = dataId;
			this.properties = properties;
		}
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getDataId() {
			return dataId;
		}
		public void setDataId(String dataId) {
			this.dataId = dataId;
		}
		public String getProperties() {
			return properties;
		}
		public void setProperties(String properties) {
			this.properties = properties;
		}
    }
}
