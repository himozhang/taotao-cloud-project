package com.taotao.cloud.office.execl.temp.read;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.cache.Ehcache;
import com.taotao.cloud.common.execl.util.TestFileUtil;
import java.io.File;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 临时测试
 *

 */
@Ignore
public class HeadReadTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeadReadTest.class);

    @Test
    public void test() throws Exception {
        File file = TestFileUtil.readUserHomeFile("test/t2.xlsx");
        EasyExcel.read(file, HeadReadData.class, new HeadListener()).ignoreEmptyRow(false).sheet(0).doRead();

    }

    @Test
    public void testCache() throws Exception {
        File file = new File("D:\\test\\headt1.xls");
        EasyExcel.read(file, HeadReadData.class, new HDListener()).readCache(new Ehcache(20)).sheet(0).doRead();

        LOGGER.info("------------------");
        EasyExcel.read(file, HeadReadData.class, new HDListener()).readCache(new Ehcache(20)).sheet(0).doRead();
        LOGGER.info("------------------");
        EasyExcel.read(file, HeadReadData.class, new HDListener()).readCache(new Ehcache(20)).sheet(0).doRead();
    }


}
