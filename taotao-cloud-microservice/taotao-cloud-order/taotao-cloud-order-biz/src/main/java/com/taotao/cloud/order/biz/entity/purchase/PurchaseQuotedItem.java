package com.taotao.cloud.order.biz.entity.purchase;

import com.baomidou.mybatisplus.annotation.TableName;
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

/**
 * 报价单字内容
 *
 * 
 * @since 2020/11/26 20:43
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = PurchaseQuotedItem.TABLE_NAME)
@TableName(PurchaseQuotedItem.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = PurchaseQuotedItem.TABLE_NAME, comment = "供求单报价表")
public class PurchaseQuotedItem extends BaseSuperEntity<PurchaseQuotedItem, Long> {

	public static final String TABLE_NAME = "tt_purchase_quoted_item";

	/**
	 * 报价单ID
	 */
	@Column(name = "purchase_quoted_id", columnDefinition = "bigint not null comment '报价单ID'")
    private Long purchaseQuotedId;

	/**
	 * 商品名称
	 */
	@Column(name = "goods_name", columnDefinition = "varchar(255) not null comment '商品名称'")
    private String goodsName;
	/**
	 * 规格
	 */
	@Column(name = "specs", columnDefinition = "varchar(255) not null comment '规格'")
    private String specs;
	/**
	 * 数量
	 */
	@Column(name = "num", columnDefinition = "int not null comment '数量'")
    private Integer num;
	/**
	 * 数量单位
	 */
	@Column(name = "goods_unit", columnDefinition = "varchar(255) not null comment '数量单位'")
    private String goodsUnit;
	/**
	 * 价格
	 */
	@Column(name = "price", columnDefinition = "decimal(10,2) not null comment '价格'")
    private BigDecimal price;

	@Override
	public boolean equals(Object o) {
				if (this == o) {
			return true;
		}
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		PurchaseQuotedItem purchaseQuotedItem = (PurchaseQuotedItem) o;
		return getId() != null && Objects.equals(getId(), purchaseQuotedItem.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
