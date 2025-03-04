package com.taotao.cloud.goods.api.web.vo;

import com.taotao.cloud.common.enums.PromotionTypeEnum;
import com.taotao.cloud.goods.api.enums.GoodsTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品索引
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-25 16:18:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class EsGoodsIndexVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -6856471777036048874L;

	@Id
	private Long id;

	/**
	 * 商品id
	 */
	private Long goodsId;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 商品编号
	 */
	private String sn;

	/**
	 * 卖家id
	 */
	private Long storeId;

	/**
	 * 卖家名称
	 */
	private String storeName;

	/**
	 * 销量
	 */
	private Integer buyCount;

	/**
	 * 小图
	 */
	private String small;

	/**
	 * 缩略图
	 */
	private String thumbnail;

	/**
	 * 品牌id
	 */
	private Long brandId;

	/**
	 * 品牌名称
	 */
	private String brandName;

	/**
	 * 品牌图片地址
	 */
	private String brandUrl;

	/**
	 * 分类path
	 */
	private String categoryPath;

	/**
	 * 分类名称path
	 */
	private String categoryNamePath;

	/**
	 * 店铺分类id
	 */
	private String storeCategoryPath;

	/**
	 * 店铺分类名称
	 */
	private String storeCategoryNamePath;

	/**
	 * 商品价格
	 */
	private BigDecimal price;

	/**
	 * 促销价
	 */
	private BigDecimal promotionPrice;

	/**
	 * 如果是积分商品需要使用的积分
	 */
	private Integer point;

	/**
	 * 评价数量
	 */
	private Integer commentNum;

	/**
	 * 好评数量
	 */
	private Integer highPraiseNum;

	/**
	 * 好评率
	 */
	private BigDecimal grade;

	/**
	 * 详情
	 */
	private String intro;

	/**
	 * 商品移动端详情
	 */
	private String mobileIntro;

	/**
	 * 是否自营
	 */
	private Boolean selfOperated;

	/**
	 * 是否为推荐商品
	 */
	private Boolean recommend;

	/**
	 * 销售模式
	 */
	private String salesModel;

	/**
	 * 审核状态
	 */
	private String authFlag;

	/**
	 * 卖点
	 */
	private String sellingPoint;

	/**
	 * 上架状态
	 */
	private String marketEnable;

	/**
	 * 商品视频
	 */
	private String goodsVideo;

	private LocalDateTime releaseTime;

	/**
	 * 商品类型
	 *
	 * @see GoodsTypeEnum
	 */
	private String goodsType;

	/**
	 * 商品sku基础分数
	 */
	private Integer skuSource;

	/**
	 * 商品属性（参数和规格）
	 */
	private List<EsGoodsAttributeVO> attrList;

	/**
	 * 商品促销活动集合JSON，key 为 促销活动类型，value 为 促销活动实体信息
	 *
	 * @see PromotionTypeEnum value 为 促销活动实体信息
	 */
	private String promotionMapJson;

}
