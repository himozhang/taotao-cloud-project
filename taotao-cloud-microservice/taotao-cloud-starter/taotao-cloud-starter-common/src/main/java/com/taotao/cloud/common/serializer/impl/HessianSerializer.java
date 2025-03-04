package com.taotao.cloud.common.serializer.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.taotao.cloud.common.serializer.Serializer;
import com.taotao.cloud.common.serializer.SerializerConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

public class HessianSerializer implements Serializer {
    @Override
    public String name() {
        return SerializerConstants.HESSIAN;
    }

    @Override
    public byte[] serialize(Object o) throws IOException {
        if (o == null) {
            return new byte[0];
        }

        ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
        // Hessian的序列化输出
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        try {
            hessianOutput.writeObject(o);
        } finally {
            IOUtils.closeQuietly(byteArrayOutputStream);
            try {
                hessianOutput.close();
            } catch (IOException ignored) {}
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes,ClassLoader classLoader) throws IOException {
        if (bytes == null) {
            return null;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // Hessian的反序列化读取对象
        HessianInput hessianInput = new HessianInput(byteArrayInputStream);
        try {
            return hessianInput.readObject();
        } finally {
            IOUtils.closeQuietly(byteArrayInputStream);
            try {
                hessianInput.close();
            } catch (Exception e) {}
        }
    }
}
