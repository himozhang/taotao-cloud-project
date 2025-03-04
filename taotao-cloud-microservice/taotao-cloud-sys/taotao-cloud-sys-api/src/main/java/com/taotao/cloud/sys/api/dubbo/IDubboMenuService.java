package com.taotao.cloud.sys.api.dubbo;


import com.taotao.cloud.sys.api.dubbo.request.MenuQueryRequest;
import java.util.List;

/**
 * 后台菜单服务接口
 *
 * @author shuigedeng
 * @version 2022.03
 * @since 2022-03-25 14:13:19
 */
public interface IDubboMenuService {

	/**
	 * 根据id获取菜单信息
	 *
	 * @param id id
	 * @return 菜单信息
	 * @since 2022-03-25 14:13:34
	 */
	List<MenuQueryRequest> queryAllById(Long id);
}
