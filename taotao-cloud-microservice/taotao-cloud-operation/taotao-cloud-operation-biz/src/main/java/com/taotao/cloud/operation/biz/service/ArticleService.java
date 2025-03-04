package com.taotao.cloud.operation.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taotao.cloud.operation.api.web.query.ArticlePageQuery;
import com.taotao.cloud.operation.api.web.vo.ArticleVO;
import com.taotao.cloud.operation.biz.model.entity.Article;
import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;


/**
 * 文章业务层
 *
 * @author shuigedeng
 * @version 2022.06
 * @since 2022-06-02 15:05:50
 */
@CacheConfig(cacheNames = "{article}")
public interface ArticleService extends IService<Article> {

	/**
	 * 管理端获取文章
	 *
	 * @param articlePageQuery
	 * @return {@link IPage }<{@link ArticleVO }>
	 * @since 2022-06-02 15:05:50
	 */
	IPage<ArticleVO> managerArticlePage(ArticlePageQuery articlePageQuery);

	/**
	 * 获取文章分页
	 *
	 * @param articlePageQuery 文章搜索条件
	 * @return {@link IPage }<{@link ArticleVO }>
	 * @since 2022-06-02 15:05:50
	 */
	IPage<ArticleVO> articlePage(ArticlePageQuery articlePageQuery);

	/**
	 * 获取文章分页
	 *
	 * @param categoryId 文章分类ID
	 * @return {@link List }<{@link Article }>
	 * @since 2022-06-02 15:05:50
	 */
	List<Article> list(String categoryId);

	/**
	 * 修改文章内容
	 *
	 * @param article 文章
	 * @return {@link Article }
	 * @since 2022-06-02 15:05:50
	 */
	@CacheEvict(key = "#article.id")
	Article updateArticle(Article article);

	/**
	 * 删除文章
	 *
	 * @param id
	 */
	@CacheEvict(key = "#id")
	void customRemove(String id);

	/**
	 * 读取文章
	 *
	 * @param id
	 * @return 文章
	 */
	@Cacheable(key = "#id")
	Article customGet(String id);

	/**
	 * 读取文章
	 *
	 * @param type
	 * @return 文章
	 */
	@Cacheable(key = "#type")
	Article customGetByType(String type);

	/**
	 * 修改文章状态
	 *
	 * @param id     文章ID
	 * @param status 显示状态
	 * @return 操作状态
	 */
	@CacheEvict(key = "#id")
	Boolean updateArticleStatus(String id, boolean status);
}
