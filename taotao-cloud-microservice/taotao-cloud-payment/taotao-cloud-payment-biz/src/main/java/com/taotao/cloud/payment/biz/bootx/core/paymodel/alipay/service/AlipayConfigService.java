package com.taotao.cloud.payment.biz.bootx.core.paymodel.alipay.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.db.PageResult;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.taotao.cloud.common.model.PageParam;
import com.taotao.cloud.payment.biz.bootx.code.paymodel.AliPayCode;
import com.taotao.cloud.payment.biz.bootx.code.paymodel.AliPayWay;
import com.taotao.cloud.payment.biz.bootx.core.paymodel.alipay.dao.AlipayConfigManager;
import com.taotao.cloud.payment.biz.bootx.core.paymodel.alipay.entity.AlipayConfig;
import com.taotao.cloud.payment.biz.bootx.dto.paymodel.alipay.AlipayConfigDto;
import com.taotao.cloud.payment.biz.bootx.exception.payment.PayFailureException;
import com.taotao.cloud.payment.biz.bootx.param.paymodel.alipay.AlipayConfigParam;
import com.taotao.cloud.payment.biz.bootx.param.paymodel.alipay.AlipayConfigQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 支付宝支付
 * @author xxm
 * @date 2020/12/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayConfigService {
    private final AlipayConfigManager alipayConfigManager;

    /**
     * 添加支付宝配置
     */
    @Transactional(rollbackFor = Exception.class)
    public AlipayConfigDto add(AlipayConfigParam param){
        AlipayConfig alipayConfig = AlipayConfig.init(param);
        alipayConfig.setActivity(false)
                .setState(1);
        AlipayConfig save = alipayConfigManager.save(alipayConfig);
        return save.toDto();
    }

    /**
     * 设置启用的支付宝配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void setUpActivity(Long id){
        AlipayConfig alipayConfig = alipayConfigManager.findById(id).orElseThrow(DataNotExistException::new);
        if (Objects.equals(alipayConfig.getActivity(),Boolean.TRUE)){
            return;
        }
        alipayConfigManager.removeAllActivity();
        alipayConfig.setActivity(true);
        alipayConfigManager.updateById(alipayConfig);
    }

    /**
     * 清除启用状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearActivity(Long id){
        AlipayConfig alipayConfig = alipayConfigManager.findById(id).orElseThrow(() -> new PayFailureException("支付宝配置不存在"));
        if (Objects.equals(alipayConfig.getActivity(),Boolean.FALSE)){
            return;
        }
        alipayConfig.setActivity(false);
        alipayConfigManager.updateById(alipayConfig);
    }

    /**
     * 修改
     */
    @Transactional(rollbackFor = Exception.class)
    public AlipayConfigDto update(AlipayConfigParam param){
        AlipayConfig alipayConfig = alipayConfigManager.findById(param.getId())
                .orElseThrow(DataNotExistException::new);
        BeanUtil.copyProperties(param,alipayConfig, CopyOptions.create().ignoreNullValue());
        // 支付方式
        if (CollUtil.isNotEmpty(param.getPayWayList())){
            alipayConfig.setPayWays(String.join(",", param.getPayWayList()));
        } else {
            alipayConfig.setPayWays(null);
        }
        return alipayConfigManager.updateById(alipayConfig).toDto();
    }

    /**
     * 获取
     */
    public AlipayConfigDto findById(Long id){
        return alipayConfigManager.findById(id)
                .map(AlipayConfig::toDto)
                .orElseThrow(DataNotExistException::new);
    }

    /**
     * 分页
     */
    public PageResult<AlipayConfigDto> page(PageParam pageParam, AlipayConfigQuery param) {
        return MpUtil.convert2DtoPageResult(alipayConfigManager.page(pageParam,param));
    }

    /**
     * 支付宝支持支付方式
     */
    public List<KeyValue> findPayWayList() {
        return AliPayWay.getPayWays().stream()
                .map(e->new KeyValue(e.getCode(),e.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 移到工具类中
     */
    @SneakyThrows
    public static void initApiConfig(AlipayConfig alipayConfig){

        AliPayApiConfig aliPayApiConfig;
        // 公钥
        if (Objects.equals(alipayConfig.getAuthType(), AliPayCode.AUTH_TYPE_KEY)){
            aliPayApiConfig = AliPayApiConfig.builder()
                    .setAppId(alipayConfig.getAppId())
                    .setPrivateKey(alipayConfig.getPrivateKey())
                    .setAliPayPublicKey(alipayConfig.getAlipayPublicKey())
                    .setCharset(CharsetUtil.UTF_8)
                    .setServiceUrl(alipayConfig.getServerUrl())
                    .setSignType(alipayConfig.getSignType())
                    .build();
        }
        // 证书
        else if (Objects.equals(alipayConfig.getAuthType(), AliPayCode.AUTH_TYPE_CART)){
            aliPayApiConfig = AliPayApiConfig.builder()
                    .setAppId(alipayConfig.getAppId())
                    .setPrivateKey(alipayConfig.getPrivateKey())
                    .setAppCertContent(alipayConfig.getAppCert())
                    .setAliPayCertContent(alipayConfig.getAlipayCert())
                    .setAliPayRootCertContent(alipayConfig.getAlipayRootCert())
                    .setCharset(CharsetUtil.UTF_8)
                    .setServiceUrl(alipayConfig.getServerUrl())
                    .setSignType(alipayConfig.getSignType())
                    .buildByCertContent();
        } else {
            throw new BizException("支付宝认证方式不可为空");
        }

        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
    }
}
