package com.taotao.cloud.common.support.generator;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVFileGenerator {

    private static final String LINE_SEPERATOR = System
        .getProperty("line.separator");

    private final static Charset charset = Charset.forName("utf-8");

    public static void generate(List<HashMap<String, Object>> data,
            String[] columns,
            String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

        for (Map<String, Object> objects : data) {
            List<String> result = Lists.newArrayList();
            for (String column : columns) {
                if (objects.get(column) != null) {
                    result.add(objects.get(column).toString());
                } else {
                    result.add("");
                }
            }

            String lineData = Joiner.on(",").skipNulls().join(result);
            try {
	            Files.asCharSink(file, charset, FileWriteMode.APPEND).write(lineData + LINE_SEPERATOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
