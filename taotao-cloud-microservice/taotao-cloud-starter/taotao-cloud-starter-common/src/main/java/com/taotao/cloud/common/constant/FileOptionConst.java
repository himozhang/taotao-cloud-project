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
package com.taotao.cloud.common.constant;

/**
 * 文件操作常量
 * 可以结合 {@link java.io.RandomAccessFile} 随机访问文件使用
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-22 10:44:26
 */
public final class FileOptionConst {

    private FileOptionConst(){}

    /**
     * 读取权限
     */
    public static final String READ = "r";

    /**
     * 读取写入权限
     * 备注：这时会默认使用 buffer，不会立刻刷新到文件。
     * 系统直接挂了，或者 debug 会比较麻烦。
     * 可以参考
     */
    public static final String READ_WRITE = "rw";

    /**
     * 打开以便读取和写入。相对于 "rw"，"rws" 还要求对“文件的内容”或“元数据”的每个更新都同步写入到基础存储设备。
     * 适用场景：安全性要求较高，文件内容不大，debug 模式。
     * 元数据：又称中介数据、中继数据，为描述数据的数据（data about data），主要是描述数据属性（property）的信息，用来支持如指示存储位置、历史数据、资源查找、文件记录等功能。
     */
    public static final String READ_WRITE_SYNCHRONOUSLY = "rws";

    /**
     * 打开以便读取和写入，相对于 "rw"，"rwd" 还要求对“文件的内容”的每个更新都同步写入到基础存储设备
     * 备注：介于 rw-rws 中间。个人暂时比较倾向于这种方式。
     */
    public static final String READ_WRITE_DATA = "rwd";

}
