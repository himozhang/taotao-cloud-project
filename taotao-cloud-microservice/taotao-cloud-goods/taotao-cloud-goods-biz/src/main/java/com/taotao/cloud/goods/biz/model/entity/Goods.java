package com.taotao.cloud.goods.biz.model.entity;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.utils.lang.StringUtil;
import com.taotao.cloud.goods.api.web.dto.GoodsOperationDTO;
import com.taotao.cloud.goods.api.enums.GoodsAuthEnum;
import com.taotao.cloud.goods.api.enums.GoodsStatusEnum;
import com.taotao.cloud.goods.api.enums.GoodsTypeEnum;
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
import java.util.Map;
import java.util.Objects;

/**
 * 商品表
 *
 * @author shuigedeng
 * @version 2021.10
 * @since 2022-03-11 17:21:04
 */
@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = Goods.TABLE_NAME)
@TableName(Goods.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Goods.TABLE_NAME, comment = "商品表")
public class Goods extends BaseSuperEntity<Goods, Long> {

	public static final String TABLE_NAME = "tt_goods";

	/**
	 * 商品名称
	 */
	@Column(name = "goods_name", columnDefinition = "varchar(255) not null comment '商品名称'")
	private String goodsName;

	/**
	 * 商品价格
	 */
	@Column(name = "price", columnDefinition = "decimal(10,2) not null comment '商品价格'")
	private BigDecimal price;

	/**
	 * 品牌id
	 */
	@Column(name = "brand_id", columnDefinition = "bigint not null comment '品牌id'")
	private Long brandId;

	/**
	 * 分类path
	 */
	@Column(name = "category_path", columnDefinition = "varchar(255) not null comment '分类path'")
	private String categoryPath;

	/**
	 * 计量单位
	 */
	@Column(name = "goods_unit", columnDefinition = "varchar(255) not null comment '计量单位'")
	private String goodsUnit;

	/**
	 * 商品卖点太长，不能超过60个字符
	 */
	@Column(name = "selling_point", columnDefinition = "varchar(255) not null comment '商品卖点'")
	private String sellingPoint;

	/**
	 * 上架状态
	 */
	@Column(name = "market_enable", columnDefinition = "varchar(255) not null comment '上架状态'")
	private String marketEnable;

	/**
	 * 详情
	 */
	@Column(name = "intro", columnDefinition = "mediumtext not null comment '详情'")
	private String intro;

	/**
	 * 购买数量
	 */
	@Column(name = "buy_count", columnDefinition = "int not null default 0 comment '购买数量'")
	private Integer buyCount;

	/**
	 * 库存
	 */
	@Column(name = "quantity", columnDefinition = "int not null default 0 comment '库存'")
	private Integer quantity;

	/**
	 * 商品好评率
	 */
	@Column(name = "grade", columnDefinition = "decimal(10,2) null comment '商品好评率'")
	private BigDecimal grade;

	/**
	 * 缩略图路径
	 */
	@Column(name = "thumbnail", columnDefinition = "varchar(255) not null comment '缩略图路径'")
	private String thumbnail;

	/**
	 * 小图路径
	 */
	@Column(name = "small", columnDefinition = "varchar(255) not null comment '小图路径'")
	private String small;

	/**
	 * 原图路径
	 */
	@Column(name = "original", columnDefinition = "varchar(255) not null comment '原图路径'")
	private String original;

	/**
	 * 店铺分类id
	 */
	@Column(name = "store_category_path", columnDefinition = "varchar(255) not null comment '店铺分类id'")
	private String storeCategoryPath;

	/**
	 * 评论数量
	 */
	@Column(name = "comment_num", columnDefinition = "int null default 0 comment '评论数量'")
	private Integer commentNum;

	/**
	 * 卖家id
	 */
	@Column(name = "store_id", columnDefinition = "bigint not null comment '会员卖家id'")
	private Long storeId;

	/**
	 * 卖家名字
	 */
	@Column(name = "store_name", columnDefinition = "varchar(255) not null comment '卖家名字'")
	private String storeName;

	/**
	 * 运费模板id
	 */
	@Column(name = "template_id", columnDefinition = "bigint not null comment '运费模板id'")
	private Long templateId;

	/**
	 * 审核状态
	 *
	 * @see GoodsAuthEnum
	 */
	@Column(name = "is_auth", columnDefinition = "varchar(255) not null comment '审核状态'")
	private String isAuth;

	/**
	 * 审核信息
	 */
	@Column(name = "auth_message", columnDefinition = "varchar(255) null comment '审核信息'")
	private String authMessage;

	/**
	 * 下架原因
	 */
	@Column(name = "under_message", columnDefinition = "varchar(255) null comment '下架原因'")
	private String underMessage;

	/**
	 * 是否自营
	 */
	@Column(name = "self_operated", columnDefinition = "boolean not null default false comment '是否自营'")
	private Boolean selfOperated;

	/**
	 * 商品移动端详情
	 */
	@Column(name = "mobile_intro", columnDefinition = "mediumtext not null comment '商品移动端详情'")
	private String mobileIntro;

	/**
	 * 商品视频
	 */
	@Column(name = "goods_video", columnDefinition = "varchar(255) not null comment '商品视频'")
	private String goodsVideo;

	/**
	 * 是否为推荐商品
	 */
	@Column(name = "recommend", columnDefinition = "boolean not null default false comment '是否为推荐商品'")
	private Boolean recommend;

	/**
	 * 销售模式
	 */
	@Column(name = "sales_model", columnDefinition = "varchar(255) not null comment '销售模式'")
	private String salesModel;

	/**
	 * 商品海报id
	 */
	@Column(name = "poster_pic_id", columnDefinition = "bigint null comment '商品海报id'")
	private Long posterPicId;

	/**
	 * 商品类型
	 *
	 * @see GoodsTypeEnum
	 */
	@Column(name = "goods_type", columnDefinition = "varchar(255) not null comment '商品类型'")
	private String goodsType;

	/**
	 * 商品参数json
	 */
	@Column(name = "params", columnDefinition = "json not null comment '商品参数json'")
	private String params;

	public Goods(GoodsOperationDTO goodsOperationDTO) {
		this.goodsName = goodsOperationDTO.getGoodsName();
		this.categoryPath = goodsOperationDTO.categoryPath();
		this.storeCategoryPath = goodsOperationDTO.storeCategoryPath();
		this.brandId = goodsOperationDTO.brandId();
		this.templateId = goodsOperationDTO.templateId();
		this.recommend = goodsOperationDTO.recommend();
		this.sellingPoint = goodsOperationDTO.sellingPoint();
		this.salesModel = goodsOperationDTO.salesModel();
		this.goodsUnit = goodsOperationDTO.goodsUnit();
		this.intro = goodsOperationDTO.intro();
		this.mobileIntro = goodsOperationDTO.mobileIntro();
		this.goodsVideo = goodsOperationDTO.goodsVideo();
		this.price = goodsOperationDTO.price();
		if (goodsOperationDTO.goodsParamsDTOList() != null
			&& goodsOperationDTO.goodsParamsDTOList().isEmpty()) {
			this.params = JSONUtil.toJsonStr(goodsOperationDTO.goodsParamsDTOList());
		}

		//如果立即上架则
		this.marketEnable =
			Boolean.TRUE.equals(goodsOperationDTO.release()) ? GoodsStatusEnum.UPPER.name()
				: GoodsStatusEnum.DOWN.name();
		this.goodsType = goodsOperationDTO.goodsType();
		this.grade = BigDecimal.valueOf(100);

		//循环sku，判定sku是否有效
		for (Map<String, Object> sku : goodsOperationDTO.skuList()) {
			//判定参数不能为空
			if (sku.get("sn") == null) {
				throw new BusinessException(ResultEnum.GOODS_SKU_SN_ERROR);
			}
			//商品SKU价格不能小于等于0
			if (StringUtil.isEmpty(sku.get("price").toString())
				|| Convert.toBigDecimal(sku.get("price")).compareTo(BigDecimal.ZERO) <= 0) {
				throw new BusinessException(ResultEnum.GOODS_SKU_PRICE_ERROR);
			}
			//商品SKU成本价不能小于等于0
			if (StringUtil.isEmpty(sku.get("cost").toString())
				|| Convert.toBigDecimal(sku.get("cost")).compareTo(BigDecimal.ZERO) <= 0) {
				throw new BusinessException(ResultEnum.GOODS_SKU_COST_ERROR);
			}
			//商品重量不能为负数 虚拟商品没有重量字段
			if (sku.containsKey("weight") && (StringUtil.isEmpty(sku.get("weight").toString())
				|| Convert.toBigDecimal(sku.get("weight").toString()).compareTo(BigDecimal.ZERO) < 0)) {
				throw new BusinessException(ResultEnum.GOODS_SKU_WEIGHT_ERROR);
			}
			//商品库存数量不能为负数
			if (StringUtil.isEmpty(sku.get("quantity").toString())
				|| Convert.toInt(sku.get("quantity").toString()) < 0) {
				throw new BusinessException(ResultEnum.GOODS_SKU_QUANTITY_ERROR);
			}
		}
	}

	public String getIntro() {
		if (CharSequenceUtil.isNotEmpty(intro)) {
			return HtmlUtil.unescape(intro);
		}
		return intro;
	}

	public String getMobileIntro() {
		if (CharSequenceUtil.isNotEmpty(mobileIntro)) {
			return HtmlUtil.unescape(mobileIntro);
		}
		return mobileIntro;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		Goods goods = (Goods) o;
		return getId() != null && Objects.equals(getId(), goods.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
