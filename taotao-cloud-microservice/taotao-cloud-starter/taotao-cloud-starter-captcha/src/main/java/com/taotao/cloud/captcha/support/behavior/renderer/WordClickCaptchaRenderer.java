/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.taotaocloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taotao.cloud.captcha.support.behavior.renderer;

import cn.hutool.core.util.IdUtil;
import com.taotao.cloud.captcha.support.behavior.definition.AbstractBehaviorRenderer;
import com.taotao.cloud.captcha.support.behavior.dto.WordClickCaptcha;
import com.taotao.cloud.captcha.support.core.definition.domain.Coordinate;
import com.taotao.cloud.captcha.support.core.definition.domain.Metadata;
import com.taotao.cloud.captcha.support.core.definition.enums.CaptchaCategory;
import com.taotao.cloud.captcha.support.core.definition.enums.FontStyle;
import com.taotao.cloud.captcha.support.core.dto.Captcha;
import com.taotao.cloud.captcha.support.core.dto.Verification;
import com.taotao.cloud.captcha.support.core.exception.CaptchaHasExpiredException;
import com.taotao.cloud.captcha.support.core.exception.CaptchaMismatchException;
import com.taotao.cloud.captcha.support.core.exception.CaptchaParameterIllegalException;
import com.taotao.cloud.captcha.support.core.provider.RandomProvider;
import com.taotao.cloud.redis.repository.RedisRepository;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 文字点选验证码处理器 </p>
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-12 12:53:39
 */
@Component
public class WordClickCaptchaRenderer extends AbstractBehaviorRenderer {

	private WordClickCaptcha wordClickCaptcha;

	@Autowired
	private RedisRepository redisRepository;
	private static final Duration DEFAULT_EXPIRE = Duration.ofMinutes(1);

	private Font getFont() {
		int fontSize = this.getCaptchaProperties().getWordClick().getFontSize();
		String fontName = this.getCaptchaProperties().getWordClick().getFontName();
		FontStyle fontStyle = this.getCaptchaProperties().getWordClick().getFontStyle();
		return this.getResourceProvider().getFont(fontName, fontSize, fontStyle);
	}

	@Override
	public String getCategory() {
		return CaptchaCategory.WORD_CLICK.getConstant();
	}

	@Override
	public Captcha getCapcha(String key) {
		String identity = key;
		if (StringUtils.isBlank(identity)) {
			identity = IdUtil.fastUUID();
		}

		Metadata metadata = draw();
		WordClickObfuscator wordClickObfuscator = new WordClickObfuscator(metadata.getWords(),
			metadata.getCoordinates());
		WordClickCaptcha wordClickCaptcha = new WordClickCaptcha();
		wordClickCaptcha.setIdentity(identity);
		wordClickCaptcha.setWordClickImageBase64(metadata.getWordClickImageBase64());
		wordClickCaptcha.setWords(wordClickObfuscator.getWordString());
		wordClickCaptcha.setWordsCount(metadata.getWords().size());

		redisRepository.setExpire(identity, wordClickObfuscator.getCoordinates(),
			DEFAULT_EXPIRE.toMillis(),
			TimeUnit.MILLISECONDS);

		this.wordClickCaptcha = wordClickCaptcha;
		return this.wordClickCaptcha;
	}

	@Override
	public boolean verify(Verification verification) {

		if (ObjectUtils.isEmpty(verification) || CollectionUtils.isEmpty(
			verification.getCoordinates())) {
			throw new CaptchaParameterIllegalException("Parameter Stamp value is null");
		}

		List<Coordinate> store = (List<Coordinate>) redisRepository.get(verification.getIdentity());
		if (CollectionUtils.isEmpty(store)) {
			throw new CaptchaHasExpiredException("Stamp is invalid!");
		}

		redisRepository.del(verification.getIdentity());

		List<Coordinate> real = verification.getCoordinates();

		for (int i = 0; i < store.size(); i++) {
			if (isDeflected(real.get(i).getX(), store.get(i).getX(), this.getFontSize())
				|| isDeflected(real.get(i).getX(), store.get(i).getX(), this.getFontSize())) {
				throw new CaptchaMismatchException("");
			}
		}

		return true;
	}

	@Override
	public Metadata draw() {

		BufferedImage backgroundImage = this.getResourceProvider().getRandomWordClickImage();

		int wordCount = getCaptchaProperties().getWordClick().getWordCount();

		List<String> words = RandomProvider.randomWords(wordCount);

		Graphics backgroundGraphics = backgroundImage.getGraphics();
		int backgroundImageWidth = backgroundImage.getWidth();
		int backgroundImageHeight = backgroundImage.getHeight();

		List<Coordinate> coordinates = IntStream.range(0, words.size())
			.mapToObj(
				index -> drawWord(backgroundGraphics, backgroundImageWidth, backgroundImageHeight,
					index, wordCount, words.get(index))).collect(Collectors.toList());

		addWatermark(backgroundGraphics, backgroundImageWidth, backgroundImageHeight);

		//创建合并图片
		BufferedImage combinedImage = new BufferedImage(backgroundImageWidth, backgroundImageHeight,
			BufferedImage.TYPE_INT_RGB);
		Graphics combinedGraphics = combinedImage.getGraphics();
		combinedGraphics.drawImage(backgroundImage, 0, 0, null);

		//定义随机1到arr.length某一个字不参与校验
		int excludeWordIndex = RandomProvider.randomInt(1, wordCount) - 1;
		words.remove(excludeWordIndex);
		coordinates.remove(excludeWordIndex);

		Metadata metadata = new Metadata();
		metadata.setWordClickImageBase64(toBase64(backgroundImage));
		metadata.setCoordinates(coordinates);
		metadata.setWords(words);
		return metadata;
	}

	private Coordinate drawWord(Graphics graphics, int width, int height, int index, int wordCount,
		String word) {
		Coordinate coordinate = randomWordCoordinate(width, height, index, wordCount);

		//随机字体颜色
		if (getCaptchaProperties().getWordClick().isRandomColor()) {
			graphics.setColor(
				new Color(RandomProvider.randomInt(1, 255), RandomProvider.randomInt(1, 255),
					RandomProvider.randomInt(1, 255)));
		} else {
			graphics.setColor(Color.BLACK);
		}

		// 设置角度
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(RandomProvider.randomInt(-45, 45)), 0, 0);
		Font rotatedFont = this.getFont().deriveFont(affineTransform);
		graphics.setFont(rotatedFont);
		graphics.drawString(word, coordinate.getX(), coordinate.getY());
		return coordinate;
	}

	private int getFontSize() {
		return this.getCaptchaProperties().getWordClick().getFontSize();
	}

	private int getHalfFontSize() {
		return this.getFontSize() / 2;
	}

	/**
	 * 根据汉字排序的枚举值值，计算汉字的坐标点。
	 *
	 * @param backgroundImageWidth  图片宽度
	 * @param backgroundImageHeight 图片高度
	 * @param wordIndex             汉字排序的枚举值值
	 * @param wordCount             显示汉字的总数量
	 * @return 当前汉字的坐标 {@link  Coordinate}
	 */
	private Coordinate randomWordCoordinate(int backgroundImageWidth, int backgroundImageHeight,
		int wordIndex, int wordCount) {
		int wordSize = getFontSize();
		int halfWordSize = getHalfFontSize();

		int averageWidth = backgroundImageWidth / (wordCount + 1);
		int x, y;
		if (averageWidth < halfWordSize) {
			x = RandomProvider.randomInt(getStartInclusive(halfWordSize), backgroundImageWidth);
		} else {
			if (wordIndex == 0) {
				x = RandomProvider.randomInt(getStartInclusive(halfWordSize),
					getEndExclusive(wordIndex, averageWidth, halfWordSize));
			} else {
				x = RandomProvider.randomInt(averageWidth * wordIndex + halfWordSize,
					getEndExclusive(wordIndex, averageWidth, halfWordSize));
			}
		}
		y = RandomProvider.randomInt(wordSize, backgroundImageHeight - wordSize);
		return new Coordinate(x, y);
	}

	/**
	 * 获取默认随机数起始点
	 *
	 * @param halfWordSize 半个汉字的大小
	 * @return 最小的随机 x 坐标
	 */
	private int getStartInclusive(int halfWordSize) {
		return 1 + halfWordSize;
	}

	/**
	 * 获取默认随机数终点
	 *
	 * @param wordIndex    汉字的枚举值值(当前是第几个汉字)
	 * @param averageWidth 栅格宽度
	 * @param halfWordSize 半个汉字的大小
	 * @return 最大的随机 x 坐标
	 */
	private int getEndExclusive(int wordIndex, int averageWidth, int halfWordSize) {
		return averageWidth * (wordIndex + 1) - halfWordSize;
	}
}
