package com.taotao.cloud.order.biz.model.entity.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taotao.cloud.common.utils.bean.BeanUtil;
import com.taotao.cloud.order.api.web.dto.cart.TradeDTO;
import com.taotao.cloud.order.api.web.dto.order.PriceDetailDTO;
import com.taotao.cloud.order.api.enums.order.CommentStatusEnum;
import com.taotao.cloud.order.api.enums.order.OrderComplaintStatusEnum;
import com.taotao.cloud.order.api.enums.order.OrderItemAfterSaleStatusEnum;
import com.taotao.cloud.order.api.web.vo.cart.CartSkuVO;
import com.taotao.cloud.order.api.web.vo.cart.CartVO;
import com.taotao.cloud.promotion.api.web.vo.PromotionSkuVO;
import com.taotao.cloud.web.base.entity.BaseSuperEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 子订单表
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-28 09:01:35
 */
@Getter
@Setter
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = OrderItem.TABLE_NAME)
@TableName(OrderItem.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = OrderItem.TABLE_NAME, comment = "子订单表")
public class OrderItem extends BaseSuperEntity<OrderItem, Long> {

	public static final String TABLE_NAME = "tt_order_item";

	/**
	 * 订单编号
	 */
	@Column(name = "order_sn", columnDefinition = "varchar(64) not null comment '订单编号'")
	private String orderSn;

	/**
	 * 子订单编号
	 */
	@Column(name = "sn", columnDefinition = "varchar(64) not null comment '子订单编号'")
	private String sn;

	/**
	 * 单价
	 */
	@Column(name = "unit_price", columnDefinition = "varchar(64) not null comment '单价'")
	private BigDecimal unitPrice;

	/**
	 * 小记
	 */
	@Column(name = "sub_total", columnDefinition = "varchar(64) not null comment '小记'")
	private BigDecimal subTotal;

	/**
	 * 商品ID
	 */
	@Column(name = "goods_id", columnDefinition = "varchar(64) not null comment '商品ID'")
	private Long goodsId;

	/**
	 * 货品ID
	 */
	@Column(name = "sku_id", columnDefinition = "varchar(64) not null comment '货品ID'")
	private Long skuId;

	/**
	 * 销售量
	 */
	@Column(name = "num", columnDefinition = "varchar(64) not null comment '销售量'")
	private Integer num;

	/**
	 * 交易编号
	 */
	@Column(name = "trade_sn", columnDefinition = "varchar(64) not null comment '交易编号'")
	private String tradeSn;

	/**
	 * 图片
	 */
	@Column(name = "image", columnDefinition = "varchar(64) not null comment '图片'")
	private String image;

	/**
	 * 商品名称
	 */
	@Column(name = "goods_name", columnDefinition = "varchar(64) not null comment '商品名称'")
	private String goodsName;

	/**
	 * 分类ID
	 */
	@Column(name = "category_id", columnDefinition = "varchar(64) not null comment '分类ID'")
	private Long categoryId;

	/**
	 * 快照id
	 */
	@Column(name = "snapshot_id", columnDefinition = "varchar(64) not null comment '快照id'")
	private Long snapshotId;

	/**
	 * 规格json
	 */
	@Column(name = "specs", columnDefinition = "json not null comment '规格json'")
	private String specs;

	/**
	 * 促销类型
	 */
	@Column(name = "promotion_type", columnDefinition = "varchar(64) not null comment '促销类型'")
	private String promotionType;

	/**
	 * 促销id
	 */
	@Column(name = "promotion_id", columnDefinition = "varchar(64) not null comment '促销id'")
	private Long promotionId;

	/**
	 * 销售金额
	 */
	@Column(name = "goods_price", columnDefinition = "varchar(64) not null comment '销售金额'")
	private BigDecimal goodsPrice;

	/**
	 * 实际金额
	 */
	@Column(name = "flow_price", columnDefinition = "varchar(64) not null comment '实际金额'")
	private BigDecimal flowPrice;

	/**
	 * 评论状态:未评论(UNFINISHED),待追评(WAIT_CHASE),评论完成(FINISHED)，
	 *
	 * @see CommentStatusEnum
	 */
	@Column(name = "comment_status", columnDefinition = "varchar(64) not null comment '评论状态:未评论(UNFINISHED),待追评(WAIT_CHASE),评论完成(FINISHED)，'")
	private String commentStatus;

	/**
	 * 售后状态
	 *
	 * @see OrderItemAfterSaleStatusEnum
	 */
	@Column(name = "after_sale_status", columnDefinition = "varchar(64) not null comment '售后状态'")
	private String afterSaleStatus;

	/**
	 * 价格详情
	 */
	@Column(name = "price_detail", columnDefinition = "varchar(64) not null comment '价格详情'")
	private String priceDetail;

	/**
	 * 投诉状态
	 *
	 * @see OrderComplaintStatusEnum
	 */
	@Column(name = "complain_status", columnDefinition = "varchar(64) not null comment '投诉状态'")
	private String complainStatus;

	/**
	 * 交易投诉id
	 */
	@Column(name = "complain_id", columnDefinition = "varchar(64) not null comment '交易投诉id'")
	private Long complainId;

	/**
	 * 退货商品数量
	 */
	@Column(name = "return_goods_number", columnDefinition = "varchar(64) not null comment '退货商品数量'")
	private Integer returnGoodsNumber;

	public OrderItem(CartSkuVO cartSkuVO, CartVO cartVO, TradeDTO tradeDTO) {
	    Long oldId = this.getId();
	    BeanUtil.copyProperties(cartSkuVO.getGoodsSku(), this);
	    BeanUtil.copyProperties(cartSkuVO.getPriceDetailDTO(), this);
	    BeanUtil.copyProperties(cartSkuVO, this);
	    this.setId(oldId);
	    if (cartSkuVO.getPriceDetailDTO().getJoinPromotion() != null && !cartSkuVO.getPriceDetailDTO().getJoinPromotion().isEmpty()) {
	        this.setPromotionType(
		        CollUtil.join(cartSkuVO.getPriceDetailDTO().getJoinPromotion().stream().map(
			        PromotionSkuVO::getPromotionType).collect(Collectors.toList()), ","));
	        this.setPromotionId(CollUtil.join(cartSkuVO.getPriceDetailDTO().getJoinPromotion().stream().map(PromotionSkuVO::getActivityId).collect(Collectors.toList()), ","));
	    }
	    this.setAfterSaleStatus(OrderItemAfterSaleStatusEnum.NEW.name());
	    this.setCommentStatus(CommentStatusEnum.NEW.name());
	    this.setComplainStatus(OrderComplaintStatusEnum.NEW.name());
	    this.setPriceDetailDTO(cartSkuVO.getPriceDetailDTO());
	    this.setOrderSn(cartVO.getSn());
	    this.setTradeSn(tradeDTO.getSn());
	    this.setImage(cartSkuVO.getGoodsSku().getThumbnail());
	    this.setGoodsName(cartSkuVO.getGoodsSku().getGoodsName());
	    this.setSkuId(cartSkuVO.getGoodsSku().getId());
	    this.setCategoryId(cartSkuVO.getGoodsSku().getCategoryPath().substring(
	            cartSkuVO.getGoodsSku().getCategoryPath().lastIndexOf(",") + 1
	    ));
	    this.setGoodsPrice(cartSkuVO.getGoodsSku().getPrice());
	    this.setUnitPrice(cartSkuVO.getPurchasePrice());
	    this.setSubTotal(cartSkuVO.getSubTotal());
	    this.setSn(SnowFlake.createStr("OI"))
	}

	public PriceDetailDTO getPriceDetailDTO() {
	    return JSONUtil.toBean(priceDetail, PriceDetailDTO.class);
	}

	public void setPriceDetailDTO(PriceDetailDTO priceDetail) {
	    this.priceDetail = JSONUtil.toJsonStr(priceDetail);
	}

	@Override
	public boolean equals(Object o) {
				if (this == o) {
			return true;
		}
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		OrderItem orderItem = (OrderItem) o;
		return getId() != null && Objects.equals(getId(), orderItem.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
