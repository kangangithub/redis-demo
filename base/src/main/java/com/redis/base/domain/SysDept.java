package com.redis.base.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * 部门表
 *
 * @author akang
 */
@Data
@NoArgsConstructor
@Component
public class SysDept implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上级部门ID，一级部门为0
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sn;

    /**
     * 是否删除  1：已删除  0：正常
     */
    private String delFlag;

    /**
     * 修改时间
     */
    private Date updateAt;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 创建人
     */
    private Long createBy;

}
