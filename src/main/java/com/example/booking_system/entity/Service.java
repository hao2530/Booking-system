package com.example.booking_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("biz_service")
public class Service {
    @TableId(type = IdType.AUTO)
    private Integer serviceId;
    private Integer providerId;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer durationMin;
    private String category;
}
