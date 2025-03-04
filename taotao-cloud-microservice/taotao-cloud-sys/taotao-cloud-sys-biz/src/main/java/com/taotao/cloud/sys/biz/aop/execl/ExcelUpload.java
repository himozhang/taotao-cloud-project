package com.taotao.cloud.sys.biz.aop.execl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 在service
 * <pre class="code">
 * &#064;ExcelUpload(type  = UploadType.类型1)
 * public String upload(List<ClassOne> items)  {
 *    if (items == null || items.size() == 0) {
 *       return;
 *    }
 *    //校验
 *    String error = uploadCheck(items);
 *    if (StringUtils.isNotEmpty) {
 *        return error;
 *    }
 *    //删除旧的
 *    deleteAll();
 *    //插入新的
 *    batchInsert(items);
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExcelUpload {
   // 记录上传类型
   ExcelUploadType type() default ExcelUploadType.未知;
}
