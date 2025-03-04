package com.taotao.cloud.goods.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taotao.cloud.goods.api.web.dto.DraftGoodsSkuParamsDTO;
import com.taotao.cloud.goods.api.web.query.DraftGoodsPageQuery;
import com.taotao.cloud.goods.api.web.vo.DraftGoodsSkuParamsVO;
import com.taotao.cloud.goods.biz.model.entity.DraftGoods;

/**
 * 草稿商品业务层
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 16:59:56
 */
public interface IDraftGoodsService extends IService<DraftGoods> {

	/**
	 * 添加草稿商品
	 *
	 * @param draftGoods 草稿商品
	 * @return {@link Boolean }
	 * @since 2022-04-27 16:59:56
	 */
	Boolean addGoodsDraft(DraftGoodsSkuParamsDTO draftGoods);

	/**
	 * 更新草稿商品
	 *
	 * @param draftGoods 草稿商品
	 * @return {@link Boolean }
	 * @since 2022-04-27 16:59:56
	 */
	Boolean updateGoodsDraft(DraftGoodsSkuParamsDTO draftGoods);

	/**
	 * 保存草稿商品
	 *
	 * @param draftGoodsVO 草稿商品
	 * @return {@link Boolean }
	 * @since 2022-04-27 16:59:56
	 */
	Boolean saveGoodsDraft(DraftGoodsSkuParamsDTO draftGoodsVO);

	/**
	 * 根据ID删除草稿商品
	 *
	 * @param id 草稿商品ID
	 * @return {@link Boolean }
	 * @since 2022-04-27 16:59:56
	 */
	Boolean deleteGoodsDraft(Long id);

	/**
	 * 获取草稿商品详情
	 *
	 * @param id 草稿商品ID
	 * @return {@link DraftGoodsSkuParamsVO }
	 * @since 2022-04-27 16:59:57
	 */
	DraftGoodsSkuParamsVO getDraftGoods(Long id);

	/**
	 * 分页获取草稿商品
	 *
	 * @param searchParams 查询参数
	 * @return {@link IPage }<{@link DraftGoods }>
	 * @since 2022-04-27 16:59:57
	 */
	IPage<DraftGoods> getDraftGoods(DraftGoodsPageQuery searchParams);

}
