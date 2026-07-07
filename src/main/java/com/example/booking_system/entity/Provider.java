package com.example.booking_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("sys_provider")
public class Provider {
    @TableId(type = IdType.AUTO)
    private Integer providerId;
    private Integer userId;
    private String companyName;
    private String category;
    private BigDecimal rating;
    private Integer status;
}
