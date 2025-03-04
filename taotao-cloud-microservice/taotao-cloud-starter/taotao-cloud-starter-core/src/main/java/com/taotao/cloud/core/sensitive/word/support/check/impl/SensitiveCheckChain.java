package com.taotao.cloud.core.sensitive.word.support.check.impl;


import com.google.common.collect.Lists;
import com.taotao.cloud.common.support.instance.impl.Instances;
import com.taotao.cloud.core.sensitive.word.api.IWordContext;
import com.taotao.cloud.core.sensitive.word.constant.enums.ValidModeEnum;
import com.taotao.cloud.core.sensitive.word.support.check.ISensitiveCheck;
import com.taotao.cloud.core.sensitive.word.support.check.SensitiveCheckResult;
import java.util.List;

/**
 * 敏感词检测责任链模式
 *
 * 这里可以提供一个公共的父类。
 *
 *
 * DFA 算法的优化可以参考论文：
 * 【DFA 算法】各种论文。
 *
 */
public class SensitiveCheckChain implements ISensitiveCheck {

    @Override
    public SensitiveCheckResult sensitiveCheck(String txt, int beginIndex, ValidModeEnum validModeEnum, IWordContext context) {
        // 初始化责任链
        List<ISensitiveCheck> sensitiveChecks = Lists.newArrayList();
        // 默认添加敏感词校验
        sensitiveChecks.add(Instances.singleton(SensitiveCheckWord.class));
        if(context.sensitiveCheckNum()) {
            sensitiveChecks.add(Instances.singleton(SensitiveCheckNum.class));
        }
        if(context.sensitiveCheckEmail()) {
            sensitiveChecks.add(Instances.singleton(SensitiveCheckEmail.class));
        }
        if(context.sensitiveCheckUrl()) {
            sensitiveChecks.add(Instances.singleton(SensitiveCheckUrl.class));
        }

        // 循环调用
        for(ISensitiveCheck sensitiveCheck : sensitiveChecks) {
            SensitiveCheckResult result = sensitiveCheck.sensitiveCheck(txt, beginIndex, validModeEnum, context);

            if(result.index() > 0) {
                return result;
            }
        }

        // 这里直接进行正则表达式相关的调用。
        // 默认返回 0
        return SensitiveCheckResult.of(0, SensitiveCheckChain.class);
    }

}
