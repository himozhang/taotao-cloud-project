package com.taotao.cloud.core.sensitive.sensitive.core.sensitive.system;

import com.taotao.cloud.core.sensitive.sensitive.core.DataPrepareTest;
import com.taotao.cloud.core.sensitive.sensitive.core.api.SensitiveUtil;
import com.taotao.cloud.core.sensitive.sensitive.model.sensitive.system.SystemBuiltInMixed;
import org.junit.Assert;
import org.junit.Test;

/**
 * 混合模式测试
 */
public class SystemBuiltInMixedTest {

    /**
     * 系统内置+Sensitive注解测试
     */
    @Test
    public void systemBuiltInAndSensitiveTest() {
        final String originalStr = "SystemBuiltInMixed{testField='混合'}";
        final String sensitiveStr = "SystemBuiltInMixed{testField='null'}";
        SystemBuiltInMixed entry = DataPrepareTest.buildSystemBuiltInMixed();
        Assert.assertEquals(originalStr, entry.toString());

        SystemBuiltInMixed sensitive = SensitiveUtil.desCopy(entry);
        Assert.assertEquals(sensitiveStr, sensitive.toString());
        Assert.assertEquals(originalStr, entry.toString());
    }

    /**
     * 系统内置+Sensitive注解测试JSON
     */
    @Test
    public void systemBuiltInAndSensitiveJsonTest() {
        final String originalStr = "SystemBuiltInMixed{testField='混合'}";
        final String sensitiveJson = "{}";
        SystemBuiltInMixed entry = DataPrepareTest.buildSystemBuiltInMixed();

        Assert.assertEquals(sensitiveJson, SensitiveUtil.desJson(entry));
        Assert.assertEquals(originalStr, entry.toString());
    }

}
