package com.siegfried.blog.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Created by zy_zhu on 2021/5/9.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleVO {

    /**
     * id
     */
    @ApiModelProperty(name = "id", value = "用户id", dataType = "Integer")
    private Integer id;

    /**
     * 标签名
     */
    @NotBlank(message = "角色名不能为空")
    @ApiModelProperty(name = "categoryName", value = "标签名", required = true, dataType = "String")
    private String roleName;

    /**
     * 标签名
     */
    @NotBlank(message = "权限标签不能为空")
    @ApiModelProperty(name = "categoryName", value = "标签名", required = true, dataType = "String")
    private String roleLabel;

    /**
     * 资源列表
     */
    @ApiModelProperty(name = "resourceIdList", value = "资源列表", required = true, dataType = "List<Integer>")
    private List<Integer> resourceIdList;

    /**
     * 菜单列表
     */
    @ApiModelProperty(name = "menuIdList", value = "菜单列表", required = true, dataType = "List<Integer>")
    private List<Integer> menuIdList;
}
