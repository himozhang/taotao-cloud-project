package com.taotao.cloud.promotion.biz.service.impl;


import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.PromotionTypeEnum;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.PageParam;
import com.taotao.cloud.goods.api.enums.GoodsStatusEnum;
import com.taotao.cloud.goods.api.feign.IFeignGoodsSkuService;
import com.taotao.cloud.goods.api.web.vo.GoodsSkuSpecGalleryVO;
import com.taotao.cloud.promotion.api.web.dto.KanjiaActivityGoodsDTO;
import com.taotao.cloud.promotion.api.web.dto.KanjiaActivityGoodsOperationDTO;
import com.taotao.cloud.promotion.api.enums.PromotionsStatusEnum;
import com.taotao.cloud.promotion.api.web.query.PromotionGoodsPageQuery;
import com.taotao.cloud.promotion.api.tools.PromotionTools;
import com.taotao.cloud.promotion.api.web.vo.kanjia.KanjiaActivityGoodsListVO;
import com.taotao.cloud.promotion.api.web.vo.kanjia.KanjiaActivityGoodsParams;
import com.taotao.cloud.promotion.api.web.vo.kanjia.KanjiaActivityGoodsVO;
import com.taotao.cloud.promotion.biz.model.entity.KanjiaActivityGoods;
import com.taotao.cloud.promotion.biz.model.entity.PromotionGoods;
import com.taotao.cloud.promotion.biz.mapper.KanJiaActivityGoodsMapper;
import com.taotao.cloud.promotion.biz.service.KanjiaActivityGoodsService;
import com.taotao.cloud.promotion.biz.service.PromotionGoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 砍价业务层实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 16:46:12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KanjiaActivityGoodsServiceImpl extends ServiceImpl<KanJiaActivityGoodsMapper, KanjiaActivityGoods> implements
	KanjiaActivityGoodsService {

    /**
     * 规格商品
     */
    @Autowired
    private IFeignGoodsSkuService goodsSkuService;

    @Autowired
    private PromotionGoodsService promotionGoodsService;

    @Override
    public Boolean add(KanjiaActivityGoodsOperationDTO kanJiaActivityGoodsOperationDTO) {
        List<KanjiaActivityGoods> kanjiaActivityGoodsList = new ArrayList<>();
        List<PromotionGoods> promotionGoodsList = new ArrayList<>();
        for (KanjiaActivityGoodsDTO kanJiaActivityGoodsDTO : kanJiaActivityGoodsOperationDTO.getPromotionGoodsList()) {
            //根据skuId查询商品信息
            GoodsSku goodsSku = this.checkSkuExist(kanJiaActivityGoodsDTO.getSkuId());
            //参数检测
            this.checkParam(kanJiaActivityGoodsDTO, goodsSku);
            //检测同一时间段是否存在相同的商品
            PromotionTools.checkPromotionTime(kanJiaActivityGoodsOperationDTO.getStartTime(), kanJiaActivityGoodsOperationDTO.getEndTime());
            kanJiaActivityGoodsDTO.setStartTime(kanJiaActivityGoodsOperationDTO.getStartTime());
            kanJiaActivityGoodsDTO.setEndTime(kanJiaActivityGoodsOperationDTO.getEndTime());
            //检测同一时间段不能允许添加相同的商品
            if (this.checkSkuDuplicate(goodsSku.getId(), kanJiaActivityGoodsDTO) != null) {
                throw new BusinessException("商品id为" + goodsSku.getId() + "的商品已参加砍价商品活动！");
            }
            kanJiaActivityGoodsDTO.setSkuId(kanJiaActivityGoodsDTO.getSkuId());
            kanJiaActivityGoodsDTO.setThumbnail(goodsSku.getThumbnail());
            kanJiaActivityGoodsDTO.setGoodsName(goodsSku.getGoodsName());
            kanJiaActivityGoodsDTO.setOriginalPrice(goodsSku.getPrice());
            kanjiaActivityGoodsList.add(kanJiaActivityGoodsDTO);
            PromotionGoods promotionGoods = new PromotionGoods(kanJiaActivityGoodsDTO);
            promotionGoods.setPromotionId(kanJiaActivityGoodsDTO.getId());
            promotionGoods.setPromotionType(PromotionTypeEnum.KANJIA.name());
            promotionGoods.setGoodsId(kanJiaActivityGoodsDTO.getGoodsId());
            promotionGoods.setTitle(kanJiaActivityGoodsDTO.getPromotionName());
            promotionGoodsList.add(promotionGoods);
        }
        this.promotionGoodsService.saveBatch(promotionGoodsList);
        return this.saveBatch(kanjiaActivityGoodsList);
    }


    @Override
    public IPage<KanjiaActivityGoods> getForPage(KanjiaActivityGoodsParams kanJiaActivityGoodsParams, PageParam pageParam) {
        return this.page(pageParam.buildMpPage(), kanJiaActivityGoodsParams.wrapper());

    }

    @Override
    public IPage<KanjiaActivityGoodsListVO> kanjiaGoodsVOPage(KanjiaActivityGoodsParams kanjiaActivityGoodsParams, PageParam pageParam) {
        return this.baseMapper.kanjiaActivityGoodsVOPage(pageParam.buildMpPage(), kanjiaActivityGoodsParams.wrapper());
    }


    /**
     * 检查商品Sku是否存
     *
     * @param skuId skuId
     * @return 商品sku
     */
    private GoodsSkuSpecGalleryVO checkSkuExist(Long skuId) {
        GoodsSkuSpecGalleryVO goodsSku = this.goodsSkuService.getGoodsSkuByIdFromCache(skuId).data();
        if (goodsSku == null) {
            log.error("商品ID为" + skuId + "的商品不存在！");
            throw new BusinessException("商品ID为" + skuId + "的商品不存在！");
        }
        return goodsSku;
    }

    /**
     * 检查参与砍价商品参数
     *
     * @param kanJiaActivityGoodsDTO 砍价商品信息
     * @param goodsSku               商品sku信息
     */
    private void checkParam(KanjiaActivityGoodsDTO kanJiaActivityGoodsDTO, GoodsSkuSpecGalleryVO goodsSku) {
        //校验商品是否存在
        if (goodsSku == null) {
            throw new BusinessException(ResultEnum.PROMOTION_GOODS_NOT_EXIT);
        }
        //校验商品状态
        if (goodsSku.goodsSkuBase().marketEnable().equals(GoodsStatusEnum.DOWN.name())) {
            throw new BusinessException(ResultEnum.GOODS_NOT_EXIST);
        }
        //校验活动库存是否超出此sku的库存
        if (goodsSku.goodsSkuBase().quantity() < kanJiaActivityGoodsDTO.getStock()) {
            throw new BusinessException(ResultEnum.KANJIA_GOODS_ACTIVE_STOCK_ERROR);
        }
        //校验最低购买金额不能高于商品金额
        if (goodsSku.goodsSkuBase().price() < kanJiaActivityGoodsDTO.getPurchasePrice()) {
            throw new BusinessException(ResultEnum.KANJIA_GOODS_ACTIVE_PRICE_ERROR);
        }
        //校验结算价格不能超过商品金额
        if (goodsSku.goodsSkuBase().price() < kanJiaActivityGoodsDTO.getSettlementPrice()) {
            throw new BusinessException(ResultEnum.KANJIA_GOODS_ACTIVE_SETTLEMENT_PRICE_ERROR);
        }
        //校验最高砍价金额
        if (kanJiaActivityGoodsDTO.getHighestPrice() > goodsSku.goodsSkuBase().price() || kanJiaActivityGoodsDTO.getHighestPrice() <= 0) {
            throw new BusinessException(ResultEnum.KANJIA_GOODS_ACTIVE_HIGHEST_PRICE_ERROR);
        }
        //校验最低砍价金额
        if (kanJiaActivityGoodsDTO.getLowestPrice() > goodsSku.goodsSkuBase().price() || kanJiaActivityGoodsDTO.getLowestPrice() <= 0) {
            throw new BusinessException(ResultEnum.KANJIA_GOODS_ACTIVE_LOWEST_PRICE_ERROR);
        }
        //校验最低砍价金额不能高与最低砍价金额
        if (kanJiaActivityGoodsDTO.getLowestPrice() > kanJiaActivityGoodsDTO.getHighestPrice()) {
            throw new BusinessException(ResultEnum.KANJIA_GOODS_ACTIVE_LOWEST_PRICE_ERROR);
        }
    }

    /**
     * 检查砍价商品是否重复存在
     *
     * @param skuId                  商品SkuId
     * @param kanJiaActivityGoodsDTO 砍价商品
     * @return 积分商品信息
     */
    private KanjiaActivityGoods checkSkuDuplicate(String skuId, KanjiaActivityGoodsDTO kanJiaActivityGoodsDTO) {
        QueryWrapper<KanjiaActivityGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId);
        if (kanJiaActivityGoodsDTO != null && CharSequenceUtil.isNotEmpty(kanJiaActivityGoodsDTO.getId())) {
            queryWrapper.ne("id", kanJiaActivityGoodsDTO.getId());
        }
        queryWrapper.and(i -> i
                .or(PromotionTools.queryPromotionStatus(PromotionsStatusEnum.START))
                .or(PromotionTools.queryPromotionStatus(PromotionsStatusEnum.NEW)));

        if (kanJiaActivityGoodsDTO != null && kanJiaActivityGoodsDTO.getStartTime() != null) {
            queryWrapper.ge("start_time", kanJiaActivityGoodsDTO.getStartTime());
        }

        if (kanJiaActivityGoodsDTO != null && kanJiaActivityGoodsDTO.getEndTime() != null) {
            queryWrapper.le("end_time", kanJiaActivityGoodsDTO.getEndTime());
        }

        return this.getOne(queryWrapper);

    }

    @Override
    public KanjiaActivityGoodsDTO getKanjiaGoodsDetail(String goodsId) {
        KanjiaActivityGoods kanjiaActivityGoods = this.getById(goodsId);
        if (kanjiaActivityGoods == null) {
            log.error("id为" + goodsId + "的砍价商品不存在！");
            throw new BusinessException();
        }
        KanjiaActivityGoodsDTO kanjiaActivityGoodsDTO = new KanjiaActivityGoodsDTO();
        BeanUtils.copyProperties(kanjiaActivityGoods, kanjiaActivityGoodsDTO);
        GoodsSkuSpecGalleryVO goodsSku = this.goodsSkuService.getGoodsSkuByIdFromCache(kanjiaActivityGoods.getSkuId());
        if (goodsSku != null) {
            kanjiaActivityGoodsDTO.setGoodsSku(goodsSku);
        }
        return kanjiaActivityGoodsDTO;
    }

    @Override
    public KanjiaActivityGoods getKanjiaGoodsBySkuId(String skuId) {
        KanjiaActivityGoods kanjiaActivityGoods = this.getOne(new QueryWrapper<KanjiaActivityGoods>().eq("sku_id", skuId), false);
        if (kanjiaActivityGoods != null && PromotionsStatusEnum.START.name().equals(kanjiaActivityGoods.getPromotionStatus())) {
            return kanjiaActivityGoods;
        }
        return null;
    }

    @Override
    public KanjiaActivityGoodsVO getKanJiaGoodsVO(String id) {
        KanjiaActivityGoodsVO kanJiaActivityGoodsVO = new KanjiaActivityGoodsVO();
        //获取砍价商品
        KanjiaActivityGoods kanJiaActivityGoods = this.getById(id);
        //获取商品SKU
        GoodsSkuSpecGalleryVO goodsSku = this.goodsSkuService.getGoodsSkuByIdFromCache(kanJiaActivityGoods.getSkuId());
        //填写活动商品价格、剩余数量
        kanJiaActivityGoodsVO.setGoodsSku(goodsSku);
        kanJiaActivityGoodsVO.setStock(kanJiaActivityGoods.getStock());
        kanJiaActivityGoodsVO.setPurchasePrice(kanJiaActivityGoods.getPurchasePrice());
        //返回商品数据
        return kanJiaActivityGoodsVO;

    }

    @Override
    public boolean updateKanjiaActivityGoods(KanjiaActivityGoodsDTO kanJiaActivityGoodsDTO) {
        //校验砍价商品是否存在
        KanjiaActivityGoods dbKanJiaActivityGoods = this.getKanjiaGoodsDetail(kanJiaActivityGoodsDTO.getId());
        //校验当前活动是否已经开始,只有新建的未开始的活动可以编辑
        if (!dbKanJiaActivityGoods.getPromotionStatus().equals(PromotionsStatusEnum.NEW.name())) {
            throw new BusinessException(ResultEnum.PROMOTION_UPDATE_ERROR);
        }
        //获取当前sku信息
        GoodsSkuSpecGalleryVO goodsSku = this.checkSkuExist(kanJiaActivityGoodsDTO.getSkuId());
        //校验商品状态
        if (goodsSku.getMarketEnable().equals(GoodsStatusEnum.DOWN.name())) {
            throw new BusinessException(ResultEnum.GOODS_NOT_EXIST);
        }
        //常规校验砍价商品参数
        this.checkParam(kanJiaActivityGoodsDTO, goodsSku);
        //检测开始结束时间是否正确
        PromotionTools.checkPromotionTime(kanJiaActivityGoodsDTO.getStartTime(), kanJiaActivityGoodsDTO.getEndTime());
        //检测同一时间段不能允许添加相同的商品
        if (this.checkSkuDuplicate(goodsSku.getId(), kanJiaActivityGoodsDTO) != null) {
            throw new BusinessException("商品id为" + goodsSku.getId() + "的商品已参加砍价商品活动！");
        }
        //修改数据库
        return this.updateById(kanJiaActivityGoodsDTO);
    }

    @Override
    public boolean deleteKanJiaGoods(List<String> ids) {
        PromotionGoodsPageQuery searchParams = new PromotionGoodsPageQuery();
        searchParams.setPromotionIds(ids);
        this.promotionGoodsService.deletePromotionGoods(searchParams);
        return this.removeByIds(ids);
    }
}
