package com.taotao.cloud.stock.biz.domain.role.model.entity;

import com.taotao.cloud.stock.biz.domain.model.role.RoleCode;
import com.taotao.cloud.stock.biz.domain.model.role.RoleId;
import com.taotao.cloud.stock.biz.domain.model.role.RoleName;
import com.taotao.cloud.stock.biz.domain.role.model.vo.RoleCode;
import com.taotao.cloud.stock.biz.domain.role.model.vo.RoleId;
import com.taotao.cloud.stock.biz.domain.role.model.vo.RoleName;
import com.xtoon.cloud.common.core.domain.Entity;
import com.xtoon.cloud.common.core.domain.StatusEnum;
import com.xtoon.cloud.sys.domain.model.permission.PermissionId;

import java.util.List;

/**
 * 角色
 *
 * @author shuigedeng
 * @date 2021-02-08
 */
public class Role implements Entity<Role> {

    /**
     * roleId
     */
    private RoleId roleId;

    /**
     * 角色编码
     */
    private RoleCode roleCode;

    /**
     * 角色名称
     */
    private RoleName roleName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态
     */
    private StatusEnum status;

    /**
     * 权限列表
     */
    private List<PermissionId> permissionIds;

    public Role(RoleId roleId, RoleCode roleCode, RoleName roleName, String remarks, StatusEnum status, List<PermissionId> permissionIds) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.remarks = remarks;
        this.status = status;
        this.permissionIds = permissionIds;
    }

    public Role(RoleCode roleCode, RoleName roleName, List<PermissionId> permissionIds) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.permissionIds = permissionIds;
    }

    /**
     * 禁用
     */
    public void disable() {
        StatusEnum status = this.status == StatusEnum.DISABLE ? StatusEnum.ENABLE : StatusEnum.DISABLE;
        this.status = status;
    }

    @Override
    public boolean sameIdentityAs(Role other) {
        return other != null && roleId.sameValueAs(other.roleId);
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public RoleCode getRoleCode() {
        return roleCode;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public String getRemarks() {
        return remarks;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public List<PermissionId> getPermissionIds() {
        return permissionIds;
    }
}
