/**
 * Licensed to the Apache Software Foundation （ASF） under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * （the "License"）； you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * https://www.q3z3.com
 * QQ : 939313737
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.im.biz.platform.modules.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.platform.common.config.PlatformConfig;
import com.platform.common.constant.ApiConstant;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.shiro.vo.LoginUser;
import com.platform.common.utils.redis.RedisUtils;
import com.platform.modules.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * token 服务层
 */
@Service("tokenService")
public class TokenServiceImpl implements TokenService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String generateToken() {
        String token = RandomUtil.randomString(32);
        LoginUser loginUser = ShiroUtils.getLoginUser();
        // 设置token
        loginUser.setTokenId(token);
        String tokenPrefix = ApiConstant.TOKEN_APP;
        Integer timeout = PlatformConfig.TIMEOUT;
        // 存储redis
        redisUtils.set(tokenPrefix + token, JSONUtil.toJsonStr(loginUser), timeout, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public LoginUser queryByToken(String token) {
        String key = ApiConstant.TOKEN_APP + token;
        Integer timeout = PlatformConfig.TIMEOUT;
        if (!redisUtils.hasKey(key)) {
            return null;
        }
        // 续期
        redisUtils.expire(key, timeout, TimeUnit.MINUTES);
        // 转换
        return JSONUtil.toBean(redisUtils.get(key), LoginUser.class);
    }

    @Override
    public void deleteToken(String token) {
        String tokenPrefix = ApiConstant.TOKEN_APP;
        redisUtils.delete(tokenPrefix.concat(token));
    }

}
