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
package com.taotao.cloud.captcha.service.impl;


import cn.hutool.core.util.StrUtil;
import com.taotao.cloud.captcha.model.Captcha;
import com.taotao.cloud.captcha.model.CaptchaCodeEnum;
import com.taotao.cloud.captcha.model.CaptchaException;
import com.taotao.cloud.captcha.model.CaptchaTypeEnum;
import com.taotao.cloud.captcha.model.Point;
import com.taotao.cloud.captcha.util.ImageUtils;
import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.common.utils.common.RandomUtil;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.common.utils.secure.AESUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * 滑动验证码
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-03 20:58:27
 */
public class BlockPuzzleCaptchaServiceImpl extends AbstractCaptchaService {

	@Override
	public void init(Properties config) {
		super.init(config);
	}

	@Override
	public void destroy(Properties config) {
		LogUtil.info("start-clear-history-data-}", captchaType());
	}

	@Override
	public String captchaType() {
		return CaptchaTypeEnum.BLOCKPUZZLE.getCodeValue();
	}

	@Override
	public Captcha get(Captcha captchaVO) {
		super.get(captchaVO);

		//原生图片
		BufferedImage originalImage = ImageUtils.getOriginal();
		if (null == originalImage) {
			LogUtil.error("滑动底图未初始化成功，请检查路径");
			throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_BASEMAP_NULL);
		}

		//设置水印
		Graphics backgroundGraphics = originalImage.getGraphics();
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		backgroundGraphics.setFont(waterMarkFont);
		backgroundGraphics.setColor(Color.white);
		backgroundGraphics.drawString(waterMark, width - getEnOrChLength(waterMark),
			height - (HAN_ZI_SIZE / 2) + 7);

		//抠图图片
		String jigsawImageBase64 = ImageUtils.getslidingBlock();
		BufferedImage jigsawImage = ImageUtils.getBase64StrToImage(jigsawImageBase64);
		if (null == jigsawImage) {
			LogUtil.error("滑动底图未初始化成功，请检查路径");
			throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_BASEMAP_NULL);
		}

		Captcha captcha = pictureTemplatesCut(originalImage, jigsawImage, jigsawImageBase64);
		if (captcha == null
			|| StrUtil.isBlank(captcha.getJigsawImageBase64())
			|| StrUtil.isBlank(captcha.getOriginalImageBase64())) {
			throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_ERROR);
		}
		return captcha;
	}

	@Override
	public Captcha check(Captcha captcha) {
		check(captcha);

		//取坐标信息
		String codeKey = String.format(REDIS_CAPTCHA_KEY, captcha.getToken());
		if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
			throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
		}

		String s = CaptchaServiceFactory.getCache(cacheType).get(codeKey);
		//验证码只用一次，即刻失效
		CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
		Point point;
		Point point1;
		String pointJson;

		try {
			point = JsonUtil.toObject(s, Point.class);
			//aes解密
			assert point != null;
			pointJson = decrypt(captcha.getPointJson(), point.getSecretKey());
			point1 = JsonUtil.toObject(pointJson, Point.class);
		} catch (Exception e) {
			LogUtil.error("验证码坐标解析失败", e);
			afterValidateFail(captcha);
			throw new CaptchaException(e.getMessage());
		}

		assert point1 != null;
		if (point.x - Integer.parseInt(slipOffset) > point1.x
			|| point1.x > point.x + Integer.parseInt(slipOffset)
			|| point.y != point1.y) {
			afterValidateFail(captcha);
			throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_COORDINATE_ERROR);
		}

		//校验成功，将信息存入缓存
		String secretKey = point.getSecretKey();
		String value;
		try {
			value = Base64.getEncoder().encodeToString(AESUtil.encrypt(captcha.getToken().concat("---").concat(pointJson),
				secretKey));
		} catch (Exception e) {
			LogUtil.error("AES加密失败", e);
			afterValidateFail(captcha);
			throw new CaptchaException(e.getMessage());
		}

		String secondKey = String.format(REDIS_SECOND_CAPTCHA_KEY, value);
		CaptchaServiceFactory.getCache(cacheType)
			.set(secondKey, captcha.getToken(), EXPIRESIN_THREE);
		captcha.setResult(true);
		captcha.resetClientFlag();

		return captcha;
	}

	@Override
	public Captcha verification(Captcha captcha) {
		super.verification(captcha);

		try {
			String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY,
				captcha.getCaptchaVerification());
			if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
				throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
			}
			//二次校验取值后，即刻失效
			CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
		} catch (Exception e) {
			LogUtil.error("验证码坐标解析失败", e);
			throw new CaptchaException(e.getMessage());
		}
		return captcha;
	}

	/**
	 * 根据模板切图
	 *
	 * @param originalImage     originalImage
	 * @param jigsawImage       jigsawImage
	 * @param jigsawImageBase64 jigsawImageBase64
	 * @return {@link com.taotao.cloud.captcha.model.Captcha }
	 * @since 2021-09-03 20:58:38
	 */
	public Captcha pictureTemplatesCut(BufferedImage originalImage, BufferedImage jigsawImage,
		String jigsawImageBase64) {
		try {
			Captcha dataVO = new Captcha();

			int originalWidth = originalImage.getWidth();
			int originalHeight = originalImage.getHeight();
			int jigsawWidth = jigsawImage.getWidth();
			int jigsawHeight = jigsawImage.getHeight();

			//随机生成拼图坐标
			Point point = generateJigsawPoint(originalWidth, originalHeight, jigsawWidth,
				jigsawHeight);
			int x = point.getX();
			int y = point.getY();

			//生成新的拼图图像
			BufferedImage newJigsawImage = new BufferedImage(jigsawWidth, jigsawHeight, jigsawImage.getType());
			Graphics2D graphics = newJigsawImage.createGraphics();

			int bold = 5;
			//如果需要生成RGB格式，需要做如下配置,Transparency 设置透明
			newJigsawImage = graphics.getDeviceConfiguration().createCompatibleImage(jigsawWidth, jigsawHeight, Transparency.TRANSLUCENT);
			// 新建的图像根据模板颜色赋值,源图生成遮罩
			cutByTemplate(originalImage, jigsawImage, newJigsawImage, x, 0);
			if (captchaInterferenceOptions > 0) {
				int position = 0;
				if (originalWidth - x - 5 > jigsawWidth * 2) {
					//在原扣图右边插入干扰图
					position = RandomUtil.randomInt(x + jigsawWidth + 5, originalWidth - jigsawWidth);
				} else {
					//在原扣图左边插入干扰图
					position = RandomUtil.randomInt(100, x - jigsawWidth - 5);
				}
				while (true) {
					String s = ImageUtils.getslidingBlock();
					if (!jigsawImageBase64.equals(s)) {
						interferenceByTemplate(originalImage,
							Objects.requireNonNull(ImageUtils.getBase64StrToImage(s)), position,
							0);
						break;
					}
				}
			}
			if (captchaInterferenceOptions > 1) {
				while (true) {
					String s = ImageUtils.getslidingBlock();
					if (!jigsawImageBase64.equals(s)) {
						int randomInt = RandomUtil.randomInt(jigsawWidth, 100 - jigsawWidth);
						interferenceByTemplate(originalImage,
							Objects.requireNonNull(ImageUtils.getBase64StrToImage(s)),
							randomInt, 0);
						break;
					}
				}
			}

			// 设置“抗锯齿”的属性
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setStroke(new BasicStroke(bold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			graphics.drawImage(newJigsawImage, 0, 0, null);
			graphics.dispose();

			ByteArrayOutputStream os = new ByteArrayOutputStream();//新建流。
			ImageIO.write(newJigsawImage, IMAGE_TYPE_PNG, os);//利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
			byte[] jigsawImages = os.toByteArray();

			ByteArrayOutputStream oriImagesOs = new ByteArrayOutputStream();//新建流。
			ImageIO.write(originalImage, IMAGE_TYPE_PNG,
				oriImagesOs);//利用ImageIO类提供的write方法，将bi以jpg图片的数据模式写入流。
			byte[] oriCopyImages = oriImagesOs.toByteArray();
			Base64.Encoder encoder = Base64.getEncoder();
			dataVO.setOriginalImageBase64(encoder.encodeToString(oriCopyImages).replaceAll("\r|\n", ""));

			//point信息不传到前端，只做后端check校验
			//dataVO.setPoint(point);
			dataVO.setJigsawImageBase64(encoder.encodeToString(jigsawImages).replaceAll("\r|\n", ""));
			dataVO.setToken(RandomUtil.randomString(16));
			dataVO.setSecretKey(point.getSecretKey());
			//base64StrToImage(encoder.encodeToString(oriCopyImages), "D:\\原图.png");
			//base64StrToImage(encoder.encodeToString(jigsawImages), "D:\\滑动.png");

			//将坐标信息存入redis中
			String codeKey = String.format(REDIS_CAPTCHA_KEY, dataVO.getToken());
			CaptchaServiceFactory.getCache(cacheType).set(codeKey, JsonUtil.toJSONString(point), EXPIRESIN_SECONDS);
			LogUtil.info("token：{},point:{}", dataVO.getToken(), JsonUtil.toJSONString(point));
			return dataVO;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 随机生成拼图坐标
	 *
	 * @param originalWidth  originalWidth
	 * @param originalHeight originalHeight
	 * @param jigsawWidth    jigsawWidth
	 * @param jigsawHeight   jigsawHeight
	 * @return {@link com.taotao.cloud.captcha.model.Point }
	 * @since 2021-09-03 20:59:02
	 */
	private static Point generateJigsawPoint(int originalWidth, int originalHeight,
		int jigsawWidth, int jigsawHeight) {
		Random random = new Random();
		int widthDifference = originalWidth - jigsawWidth;
		int heightDifference = originalHeight - jigsawHeight;
		int x, y;
		if (widthDifference <= 0) {
			x = 5;
		} else {
			x = random.nextInt(originalWidth - jigsawWidth - 100) + 100;
		}
		if (heightDifference <= 0) {
			y = 5;
		} else {
			y = random.nextInt(originalHeight - jigsawHeight) + 5;
		}
		String key = null;
		if (captchaAesStatus) {
			key = RandomUtil.randomString(16);
		}
		return new Point(x, y, key);
	}

	/**
	 * @param oriImage      原图
	 * @param templateImage 模板图
	 * @param newImage      新抠出的小图
	 * @param x             随机扣取坐标X
	 * @param y             随机扣取坐标y
	 * @since 2021-09-03 20:59:14
	 */
	private static void cutByTemplate(BufferedImage oriImage, BufferedImage templateImage,
		BufferedImage newImage, int x, int y) {
		//临时数组遍历用于高斯模糊存周边像素值
		int[][] martrix = new int[3][3];
		int[] values = new int[9];

		int xLength = templateImage.getWidth();
		int yLength = templateImage.getHeight();
		// 模板图像宽度
		for (int i = 0; i < xLength; i++) {
			// 模板图片高度
			for (int j = 0; j < yLength; j++) {
				// 如果模板图像当前像素点不是透明色 copy源文件信息到目标图片中
				int rgb = templateImage.getRGB(i, j);
				if (rgb < 0) {
					newImage.setRGB(i, j, oriImage.getRGB(x + i, y + j));

					//抠图区域高斯模糊
					readPixel(oriImage, x + i, y + j, values);
					fillMatrix(martrix, values);
					oriImage.setRGB(x + i, y + j, avgMatrix(martrix));
				}

				//防止数组越界判断
				if (i == (xLength - 1) || j == (yLength - 1)) {
					continue;
				}
				int rightRgb = templateImage.getRGB(i + 1, j);
				int downRgb = templateImage.getRGB(i, j + 1);
				//描边处理，,取带像素和无像素的界点，判断该点是不是临界轮廓点,如果是设置该坐标像素是白色
				if ((rgb >= 0 && rightRgb < 0) || (rgb < 0 && rightRgb >= 0) || (rgb >= 0
					&& downRgb < 0) || (rgb < 0 && downRgb >= 0)) {
					newImage.setRGB(i, j, Color.white.getRGB());
					oriImage.setRGB(x + i, y + j, Color.white.getRGB());
				}
			}
		}

	}

	/**
	 * 干扰抠图处理
	 *
	 * @param oriImage      原图
	 * @param templateImage 模板图
	 * @param x             随机扣取坐标X
	 * @param y             随机扣取坐标y
	 * @since 2021-09-03 20:59:25
	 */
	private static void interferenceByTemplate(BufferedImage oriImage, BufferedImage templateImage,
		int x, int y) {
		//临时数组遍历用于高斯模糊存周边像素值
		int[][] martrix = new int[3][3];
		int[] values = new int[9];

		int xLength = templateImage.getWidth();
		int yLength = templateImage.getHeight();
		// 模板图像宽度
		for (int i = 0; i < xLength; i++) {
			// 模板图片高度
			for (int j = 0; j < yLength; j++) {
				// 如果模板图像当前像素点不是透明色 copy源文件信息到目标图片中
				int rgb = templateImage.getRGB(i, j);
				if (rgb < 0) {
					//抠图区域高斯模糊
					readPixel(oriImage, x + i, y + j, values);
					fillMatrix(martrix, values);
					oriImage.setRGB(x + i, y + j, avgMatrix(martrix));
				}
				//防止数组越界判断
				if (i == (xLength - 1) || j == (yLength - 1)) {
					continue;
				}
				int rightRgb = templateImage.getRGB(i + 1, j);
				int downRgb = templateImage.getRGB(i, j + 1);
				//描边处理，,取带像素和无像素的界点，判断该点是不是临界轮廓点,如果是设置该坐标像素是白色
				if ((rgb >= 0 && rightRgb < 0) || (rgb < 0 && rightRgb >= 0) || (rgb >= 0
					&& downRgb < 0) || (rgb < 0 && downRgb >= 0)) {
					oriImage.setRGB(x + i, y + j, Color.white.getRGB());
				}
			}
		}

	}

	/**
	 * readPixel
	 *
	 * @param img    img
	 * @param x      x
	 * @param y      y
	 * @param pixels pixels
	 * @author shuigedeng
	 * @since 2021-09-03 20:59:30
	 */
	private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
		int xStart = x - 1;
		int yStart = y - 1;
		int current = 0;
		for (int i = xStart; i < 3 + xStart; i++) {
			for (int j = yStart; j < 3 + yStart; j++) {
				int tx = i;
				if (tx < 0) {
					tx = -tx;

				} else if (tx >= img.getWidth()) {
					tx = x;
				}
				int ty = j;
				if (ty < 0) {
					ty = -ty;
				} else if (ty >= img.getHeight()) {
					ty = y;
				}
				pixels[current++] = img.getRGB(tx, ty);

			}
		}
	}

	/**
	 * fillMatrix
	 *
	 * @param matrix matrix
	 * @param values values
	 * @author shuigedeng
	 * @since 2021-09-03 20:59:34
	 */
	private static void fillMatrix(int[][] matrix, int[] values) {
		int filled = 0;
		for (int i = 0; i < matrix.length; i++) {
			int[] x = matrix[i];
			for (int j = 0; j < x.length; j++) {
				x[j] = values[filled++];
			}
		}
	}

	/**
	 * avgMatrix
	 *
	 * @param matrix matrix
	 * @return int
	 * @author shuigedeng
	 * @since 2021-09-03 20:59:37
	 */
	private static int avgMatrix(int[][] matrix) {
		int r = 0;
		int g = 0;
		int b = 0;
		for (int[] x : matrix) {
			for (int j = 0; j < x.length; j++) {
				if (j == 1) {
					continue;
				}
				Color c = new Color(x[j]);
				r += c.getRed();
				g += c.getGreen();
				b += c.getBlue();
			}
		}
		return new Color(r / 8, g / 8, b / 8).getRGB();
	}


}
