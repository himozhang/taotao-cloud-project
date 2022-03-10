package com.taotao.cloud.core.sensitive.sensitive.core.bs;

import com.taotao.cloud.common.support.deepcopy.FastJsonDeepCopy;
import com.taotao.cloud.common.support.deepcopy.IDeepCopy;
import com.taotao.cloud.common.support.instance.impl.Instances;
import com.taotao.cloud.common.utils.common.ArgUtil;
import com.taotao.cloud.core.sensitive.sensitive.api.ISensitive;
import com.taotao.cloud.core.sensitive.sensitive.api.ISensitiveConfig;
import com.taotao.cloud.core.sensitive.sensitive.core.api.SensitiveService;
import com.taotao.cloud.core.sensitive.sensitive.core.support.config.DefaultSensitiveConfig;

/**
 * 脱敏引导类
 */
public final class SensitiveBs {

    private SensitiveBs(){}

    /**
     * 深度拷贝
     * @since 0.0.13
     */
    private IDeepCopy deepCopy = FastJsonDeepCopy.getInstance();

    /**
     * 脱敏实现
     * @since 0.0.9
     */
    private ISensitive sensitive = Instances.singleton(SensitiveService.class);

    /**
     * 新建实例
     * @since 0.0.9
     * @return 引导类实例
     */
    public static SensitiveBs newInstance() {
       return new SensitiveBs();
    }

    /**
     * 设置深度拷贝实现
     * @param deepCopy 深度拷贝实现类
     * @return this
     * @since 0.0.9
     */
    public SensitiveBs deepCopy(IDeepCopy deepCopy) {
        ArgUtil.notNull(deepCopy, "deepCopy");

        this.deepCopy = deepCopy;
        return this;
    }

    /**
     * 脱敏对象
     *
     * 每次都创建一个新的对象，避免线程问题
     * 可以使用 {@link ThreadLocal} 简单优化。
     * @param object 原始对象
     * @param <T> 泛型
     * @return 脱敏后的对象
     * @since 0.0.4 以前用的是单例。建议使用 spring 等容器管理 ISensitive 实现。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T desCopy(T object) {
        ISensitiveConfig config = buildConfig();
        return (T) sensitive.desCopy(object, config);
    }

    /**
     * 返回脱敏后的对象 json
     * null 对象，返回字符串 "null"
     * @param object 对象
     * @return 结果 json
     * @since 0.0.9
     */
    public String desJson(Object object) {
        ISensitiveConfig config = buildConfig();
        return sensitive.desJson(object, config);
    }

    /**
     * 构建上下文
     * @return 配置
     * @since 0.0.9
     */
    private ISensitiveConfig buildConfig() {
        return DefaultSensitiveConfig.newInstance()
                .deepCopy(deepCopy);
    }

}
