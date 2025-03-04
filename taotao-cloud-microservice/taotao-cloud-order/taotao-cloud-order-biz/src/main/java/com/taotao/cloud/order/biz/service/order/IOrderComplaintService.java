package com.taotao.cloud.order.biz.service.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taotao.cloud.order.api.web.dto.order.OrderComplaintDTO;
import com.taotao.cloud.order.api.web.dto.order.OrderComplaintOperationDTO;
import com.taotao.cloud.order.api.web.dto.order.StoreAppealDTO;
import com.taotao.cloud.order.api.web.query.order.OrderComplaintPageQuery;
import com.taotao.cloud.order.api.web.vo.order.OrderComplaintVO;
import com.taotao.cloud.order.biz.model.entity.order.OrderComplaint;

/**
 * 交易投诉业务层
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 08:54:36
 */
public interface IOrderComplaintService extends IService<OrderComplaint> {

	/**
	 * 分页获取交易投诉信息
	 *
	 * @param orderComplaintPageQuery 订单投诉页面查询
	 * @return {@link IPage }<{@link OrderComplaint }>
	 * @since 2022-04-28 08:54:36
	 */
	IPage<OrderComplaint> getOrderComplainByPage(OrderComplaintPageQuery orderComplaintPageQuery);

	/**
	 * 获取交易投诉详情
	 *
	 * @param id 交易投诉ID
	 * @return {@link OrderComplaintVO }
	 * @since 2022-04-28 08:54:36
	 */
	OrderComplaintVO getOrderComplainById(Long id);

	/**
	 * 获取交易投诉详情
	 *
	 * @param storeId 店铺id
	 * @return {@link OrderComplaint }
	 * @since 2022-04-28 08:54:36
	 */
	OrderComplaint getOrderComplainByStoreId(Long storeId);

	/**
	 * 添加交易投诉
	 *
	 * @param orderComplaintDTO 交易投诉信息
	 * @return {@link OrderComplaint }
	 * @since 2022-04-28 08:54:36
	 */
	OrderComplaint addOrderComplain(OrderComplaintDTO orderComplaintDTO);

	/**
	 * 更新交易投诉
	 *
	 * @param orderComplaint 交易投诉信息
	 * @return {@link Boolean }
	 * @since 2022-04-28 08:54:36
	 */
	Boolean updateOrderComplain(OrderComplaint orderComplaint);

	/**
	 * 修改交易投诉状态
	 *
	 * @param orderComplaintOperationDTO 订单投诉操作dto
	 * @return {@link Boolean }
	 * @since 2022-04-28 08:54:36
	 */
	Boolean updateOrderComplainByStatus(OrderComplaintOperationDTO orderComplaintOperationDTO);

	/**
	 * 待处理投诉数量
	 *
	 * @return long
	 * @since 2022-04-28 08:54:37
	 */
	long waitComplainNum();

	/**
	 * 取消交易投诉
	 *
	 * @param id 交易投诉ID
	 * @return {@link Boolean }
	 * @since 2022-04-28 08:54:37
	 */
	Boolean cancel(Long id);

	/**
	 * 店铺申诉
	 *
	 * @param storeAppealDTO 商店吸引力dto
	 * @return {@link Boolean }
	 * @since 2022-04-28 08:54:37
	 */
	Boolean appeal(StoreAppealDTO storeAppealDTO);
}
