package com.taotao.cloud.feign.loadbalancer.chooser;

import com.taotao.cloud.common.utils.collection.CollectionUtil;
import com.taotao.cloud.common.utils.log.LogUtil;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.cloud.client.ServiceInstance;

/**
 * 随机的选择器
 *
 * @author shuigedeng
 * @version 2022.06
 * @since 2022-06-08 10:42:37
 */
public class RandomRuleChooser implements IRuleChooser {

	@Override
	public ServiceInstance choose(List<ServiceInstance> instances) {
		if (CollectionUtil.isNotEmpty(instances)) {
			int randomValue = ThreadLocalRandom.current().nextInt(instances.size());
			ServiceInstance serviceInstance = instances.get(randomValue);
			LogUtil.info("RandomRuleChooser 选择了ip为 {}, 端口为：{} 的服务", serviceInstance.getHost(), serviceInstance.getPort());
			return serviceInstance;
		}
		return null;
	}
}
