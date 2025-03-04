package com.taotao.cloud.office.execl.temp;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.PositionUtils;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson2.JSON;
import com.taotao.cloud.common.execl.demo.write.DemoData;
import com.taotao.cloud.common.execl.util.TestFileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellReference;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 临时测试
 *

 */
public class Lock2Test {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lock2Test.class);

    @Test
    public void test() throws Exception {
        // File file = TestFileUtil.readUserHomeFile("test/test4.xlsx");
        //        File file = TestFileUtil.readUserHomeFile("test/test6.xls");
        File file = new File("/Users/zhuangjiaju/IdeaProjects/easyexcel/src/test/resources/converter/converter07.xlsx");

        List<Object> list = EasyExcel.read("/Users/zhuangjiaju/test/test3.xlsx").sheet(0).headRowNumber(0).doReadSync();
        LOGGER.info("数据：{}", list.size());
        for (Object data : list) {
            LOGGER.info("返回数据：{}", CollectionUtils.size(data));
            LOGGER.info("返回数据：{}", JSON.toJSONString(data));
        }
    }

    @Test
    public void test33() throws Exception {
        File file = TestFileUtil.readUserHomeFile("test/test6.xlsx");

        EasyExcel.read(file, LockData.class, new LockDataListener()).sheet(0).headRowNumber(0)
            .doRead();

    }

    @Test
    public void write() throws Exception {
        String fileName = TestFileUtil.getPath() + "styleWrite" + System.currentTimeMillis() + ".xlsx";
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为红色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short)20);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 背景绿色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short)20);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
            new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, DemoData.class).registerWriteHandler(horizontalCellStyleStrategy).sheet("模板")
            .doWrite(data());
    }

    @Test
    public void simpleWrite() {
        String fileName = TestFileUtil.getPath() + System.currentTimeMillis() + ".xlsx";
        System.out.println(fileName);
        EasyExcel.write(fileName).head(head()).sheet("模板").doWrite(dataList());
    }

    private List<List<String>> head() {
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        head0.add("表头");

        list.add(head0);
        return list;
    }

    private List<List<Object>> dataList() {
        List<List<Object>> list = new ArrayList<List<Object>>();
        List<Object> data = new ArrayList<Object>();
        data.add("字符串");
        data.add(new Date());
        data.add(0.56);
        list.add(data);
        return list;
    }

    private List<DemoData> data() {
        List<DemoData> list = new ArrayList<DemoData>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }

    @Test
    public void testc() throws Exception {
        LOGGER.info("reslut:{}", JSON.toJSONString(new CellReference("B3")));
    }

    @Test
    public void simpleRead() {
        // 写法1：
        String fileName = "D:\\test\\珠海 (1).xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, LockData.class, new LockDataListener()).useDefaultListener(false).sheet().doRead();
    }

    @Test
    public void test2() throws Exception {
        File file = new File("D:\\test\\converter03.xls");

        List<Object> list = EasyExcel.read(file).sheet().headRowNumber(0).doReadSync();
        LOGGER.info("数据：{}", list.size());
        for (Object data : list) {
            LOGGER.info("返回数据：{}", JSON.toJSONString(data));
        }
        LOGGER.info("文件状态：{}", file.exists());
        file.delete();
        Thread.sleep(500 * 1000);
    }

    @Test
    public void test335() throws Exception {


        LOGGER.info("reslut:{}", PositionUtils.getCol("A10",null));
        LOGGER.info("reslut:{}", PositionUtils.getRow("A10"));
        LOGGER.info("reslut:{}", PositionUtils.getCol("AB10",null));
        LOGGER.info("reslut:{}", PositionUtils.getRow("AB10"));

        //LOGGER.info("reslut:{}", PositionUtils2.getCol("A10",null));
        //LOGGER.info("reslut:{}", PositionUtils2.getRow("A10"));
        //LOGGER.info("reslut:{}", PositionUtils2.getCol("AB10",null));
        //LOGGER.info("reslut:{}", PositionUtils2.getRow("AB10"));
    }


}
