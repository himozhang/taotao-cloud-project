package com.taotao.cloud.store.biz.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taotao.cloud.store.api.web.dto.StoreAfterSaleAddressDTO;
import com.taotao.cloud.store.api.web.dto.StoreSettlementDay;
import com.taotao.cloud.store.api.web.vo.StoreBasicInfoVO;
import com.taotao.cloud.store.api.web.vo.StoreDetailInfoVO;
import com.taotao.cloud.store.api.web.vo.StoreOtherVO;
import com.taotao.cloud.store.biz.model.entity.StoreDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 店铺详细数据处理层
 */
public interface StoreDetailMapper extends BaseMapper<StoreDetail> {

	/**
	 * 获取店铺详情VO
	 *
	 * @param storeId 店铺ID
	 * @return 店铺详情VO
	 */
	@Select("""
		select s.store_logo,s.member_name,s.store_name,s.store_disable,s.self_operated,s.store_address_detail,s.store_address_path,s.store_address_id_path,s.store_center,s.store_desc,s.yzf_sign,s.yzf_mp_sign,d.* 
		from tt_store s inner join tt_store_detail d on s.id=d.store_id where s.id=#{storeId}
		""")
	StoreDetailInfoVO getStoreDetail(Long storeId);

	/**
	 * 根据会员ID获取店铺详情
	 *
	 * @param memberId 会员ID
	 * @return 店铺详情
	 */
	@Select("""
		select s.member_name,s.store_name,s.store_disable,s.self_operated,s.store_center,s.store_logo,s.store_desc,s.store_address_detail,s.store_address_path,s.store_address_id_path,d.*\s
		from tt_store s inner join tt_store_detail d on s.id=d.store_id where s.member_id=#{memberId}
		""")
	StoreDetailInfoVO getStoreDetailByMemberId(Long memberId);

	/**
	 * 获取店铺基础信息DTO
	 *
	 * @param storeId 店铺ID
	 * @return 店铺基础信息DTO
	 */
	@Select("SELECT s.id as storeId,s.* FROM tt_store s WHERE s.id=#{storeId}")
	StoreBasicInfoVO getStoreBasicInfoDTO(String storeId);

	/**
	 * 获取店铺售后地址DTO
	 *
	 * @param storeId 店铺ID
	 * @return 店铺售后地址DTO
	 */
	@Select("""
		select s.sales_consignee_name,s.sales_consignee_mobile,s.sales_consignee_address_id,s.sales_consignee_address_path,s.sales_consignee_detail\s
		from tt_store_detail s  where s.store_id=#{storeId}
		""")
	StoreAfterSaleAddressDTO getStoreAfterSaleAddressDTO(Long storeId);

	/**
	 * 获取待结算店铺列表
	 *
	 * @param day 结算日
	 * @return 待结算店铺列表
	 */
	@Select("""
		SELECT store_id,settlement_day FROM tt_store_detail
				WHERE settlement_cycle LIKE concat(#{day},',%') 
				OR settlement_cycle LIKE concat('%,',#{day},',%')
				OR settlement_cycle LIKE concat('%,',#{day})
		""")
	List<StoreSettlementDay> getSettlementStore(int day);

	/**
	 * 修改店铺的结算日
	 *
	 * @param storeId  店铺ID
	 * @param dateTime 结算日
	 */
	@Update("UPDATE tt_store_detail SET settlement_day=#{dateTime} WHERE store_id=#{storeId}")
	void updateSettlementDay(String storeId, DateTime dateTime);

	/**
	 * 查看店铺营业执照信息
	 *
	 * @param storeId 店铺ID
	 * @return 店铺营业执照
	 */
	@Select("SELECT * FROM tt_store_detail WHERE store_id=#{storeId}")
	StoreOtherVO getLicencePhoto(@Param("storeId") String storeId);
}
