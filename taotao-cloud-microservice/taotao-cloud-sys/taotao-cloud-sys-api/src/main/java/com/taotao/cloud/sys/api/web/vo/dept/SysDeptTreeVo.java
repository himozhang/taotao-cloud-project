package com.taotao.cloud.sys.api.web.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

/**
 * @author shuigedeng
 * @since 2020/5/14 10:44
 */
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "部门树VO")
public class SysDeptTreeVo implements Serializable {

	@Serial
	private static final long serialVersionUID = -4132785717179910025L;

	@Schema(description = "对应SysDepart中的id字段,前端数据树中的key")
	private Integer key;

	@Schema(description = "对应SysDepart中的id字段;前端数据树中的value")
	private String value;

	@Schema(description = "对应depart_name字段;前端数据树中的title")
	private String title;

	@Schema(description = "部门主键ID")
	private Long deptId;

	@Schema(description = "部门名称")
	private String name;

	@Schema(description = "上级部门")
	private Long parentId;

	@Schema(description = "排序")
	private Integer sort;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "创建时间")
	private LocalDateTime createTime;

	@Schema(description = "修改时间")
	private LocalDateTime updateTime;

	@Schema(description = "是否删除  -1：已删除  0：正常")
	private Boolean delFlag;

	@Schema(description = "上级部门")
	private String parentName;

	@Schema(description = "等级")
	private Integer level;

	@Schema(description = "children")
	private List<SysDeptTreeVo> children;

}
