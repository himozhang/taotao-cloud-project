package com.taotao.cloud.order.biz.service.aftersale;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taotao.cloud.order.api.web.query.aftersale.AfterSaleReasonPageQuery;
import com.taotao.cloud.order.biz.model.entity.aftersale.AfterSaleReason;

import java.util.List;

/**
 * 售后原因业务层
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:49:03
 */
public interface IAfterSaleReasonService extends IService<AfterSaleReason> {

	/**
	 * 获取售后原因列表
	 *
	 * @param serviceType 售后类型
	 * @return {@link List }<{@link AfterSaleReason }>
	 * @since 2022-04-28 08:49:03
	 */
	List<AfterSaleReason> afterSaleReasonList(String serviceType);

	/**
	 * 修改售后原因
	 *
	 * @param afterSaleReason 售后原因
	 * @return {@link Boolean }
	 * @since 2022-04-28 08:49:03
	 */
	Boolean editAfterSaleReason(AfterSaleReason afterSaleReason);

	/**
	 * 分页查询售后原因
	 *
	 * @param afterSaleReasonPageQuery 查询条件
	 * @return {@link IPage }<{@link AfterSaleReason }>
	 * @since 2022-04-28 08:49:03
	 */
	IPage<AfterSaleReason> getByPage(AfterSaleReasonPageQuery afterSaleReasonPageQuery);

}
