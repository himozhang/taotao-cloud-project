package com.taotao.cloud.goods.biz.entity;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.taotao.cloud.goods.api.enums.DraftGoodsSaveType;
import com.taotao.cloud.goods.api.enums.GoodsStatusEnum;
import com.taotao.cloud.goods.api.enums.GoodsTypeEnum;
import com.taotao.cloud.web.base.entity.BaseSuperEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 草稿商品表
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = DraftGoods.TABLE_NAME)
@TableName(DraftGoods.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = DraftGoods.TABLE_NAME, comment = "草稿商品表")
public class DraftGoods extends BaseSuperEntity<DraftGoods, Long> {

	public static final String TABLE_NAME = "li_draft_goods";

	/**
	 * 商品名称
	 */
	@Column(name = "goods_name", nullable = false, columnDefinition = "varchar(64) not null comment '商品名称'")
	private String goodsName;

	/**
	 * 商品价格
	 */
	@Column(name = "price", nullable = false, columnDefinition = "decimal(10,2) not null comment '商品价格'")
	private BigDecimal price;

	/**
	 * 品牌id
	 */
	@Column(name = "brand_id", nullable = false, columnDefinition = "varchar(64) not null comment '品牌id'")
	private Long brandId;

	/**
	 * 分类path
	 */
	@Column(name = "category_path", nullable = false, columnDefinition = "varchar(64) not null comment '分类path'")
	private String categoryPath;

	/**
	 * 计量单位
	 */
	@Column(name = "goods_unit", nullable = false, columnDefinition = "varchar(64) not null comment '计量单位'")
	private String goodsUnit;

	/**
	 * 卖点
	 */
	@Column(name = "selling_point", nullable = false, columnDefinition = "varchar(64) not null comment '卖点'")
	private String sellingPoint;

	/**
	 * 上架状态
	 *
	 * @see GoodsStatusEnum
	 */
	@Column(name = "market_enable", nullable = false, columnDefinition = "varchar(64) not null comment '上架状态'")
	private String marketEnable;

	/**
	 * 详情
	 */
	@Column(name = "intro", nullable = false, columnDefinition = "varchar(64) not null comment '详情'")
	private String intro;

	/**
	 * 商品移动端详情
	 */
	@Column(name = "mobile_intro", nullable = false, columnDefinition = "varchar(64) not null comment '商品移动端详情'")
	private String mobileIntro;

	/**
	 * 购买数量
	 */
	@Column(name = "buy_count", nullable = false, columnDefinition = "int not null default 0 comment '购买数量'")
	private Integer buyCount;

	/**
	 * 库存
	 */
	@Column(name = "quantity", nullable = false, columnDefinition = "int not null default 0 comment '库存'")
	private Integer quantity;

	/**
	 * 可用库存
	 */
	@Column(name = "enable_quantity", nullable = false, columnDefinition = "int not null default 0 comment '可用库存'")
	private Integer enableQuantity;

	/**
	 * 商品好评率
	 */
	@Column(name = "grade", nullable = false, columnDefinition = "decimal(10,2) not null comment '商品好评率'")
	private BigDecimal grade;

	/**
	 * 缩略图路径
	 */
	@Column(name = "thumbnail", nullable = false, columnDefinition = "varchar(64) not null comment '缩略图路径'")
	private String thumbnail;

	/**
	 * 大图路径
	 */
	@Column(name = "big", nullable = false, columnDefinition = "varchar(64) not null comment '大图路径'")
	private String big;

	/**
	 * 小图路径
	 */
	@Column(name = "small", nullable = false, columnDefinition = "varchar(64) not null comment '小图路径'")
	private String small;

	/**
	 * 原图路径
	 */
	@Column(name = "original", nullable = false, columnDefinition = "varchar(64) not null comment '原图路径'")
	private String original;

	/**
	 * 店铺分类id
	 */
	@Column(name = "store_category_path", nullable = false, columnDefinition = "varchar(64) not null comment '店铺分类id'")
	private String storeCategoryPath;

	/**
	 * 评论数量
	 */
	@Column(name = "comment_num", columnDefinition = "int comment '评论数量'")
	private Integer commentNum;

	/**
	 * 卖家id
	 */
	@Column(name = "store_id", nullable = false, columnDefinition = "varchar(64) not null comment '卖家id'")
	private Long storeId;

	/**
	 * 卖家名字
	 */
	@Column(name = "store_name", nullable = false, columnDefinition = "varchar(64) not null comment '卖家名字'")
	private String storeName;

	/**
	 * 运费模板id
	 */
	@Column(name = "template_id", nullable = false, columnDefinition = "varchar(64) not null comment '运费模板id'")
	private Long templateId;

	/**
	 * 是否自营
	 */
	@Column(name = "self_operated", nullable = false, columnDefinition = "boolean not null comment '是否自营'")
	private Boolean selfOperated;

	/**
	 * 商品视频
	 */
	@Column(name = "goods_video", nullable = false, columnDefinition = "varchar(64) not null comment '商品视频'")
	private String goodsVideo;

	/**
	 * 是否为推荐商品
	 */
	@Column(name = "recommend", nullable = false, columnDefinition = "boolean not null comment '是否为推荐商品'")
	private Boolean recommend;

	/**
	 * 销售模式
	 */
	@Column(name = "sales_model", nullable = false, columnDefinition = "varchar(64) not null comment '销售模式'")
	private String salesModel;

	/**
	 * 草稿商品保存类型
	 *
	 * @see DraftGoodsSaveType
	 */
	@Column(name = "save_type", nullable = false, columnDefinition = "varchar(64) not null comment '草稿商品保存类型'")
	private String saveType;

	/**
	 * 分类名称JSON
	 */
	@Column(name = "category_name_json", nullable = false, columnDefinition = "varchar(64) not null comment '分类名称JSON'")
	private String categoryNameJson;

	/**
	 * 商品参数JSON
	 */
	@Column(name = "goods_params_list_json", nullable = false, columnDefinition = "varchar(64) not null comment '商品参数JSON'")
	private String goodsParamsListJson;

	/**
	 * 商品图片JSON
	 */
	@Column(name = "goods_gallery_list_json", nullable = false, columnDefinition = "varchar(64) not null comment '商品图片JSON'")
	private String goodsGalleryListJson;

	/**
	 * sku列表JSON
	 */
	@Column(name = "sku_list_json", nullable = false, columnDefinition = "varchar(64) not null comment 'sku列表JSON'")
	private String skuListJson;

	/**
	 * 商品类型
	 *
	 * @see GoodsTypeEnum
	 */
	@Column(name = "goods_type", nullable = false, columnDefinition = "varchar(64) not null comment '商品类型'")
	private String goodsType;

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
}
