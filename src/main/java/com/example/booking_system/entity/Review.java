package com.example.booking_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("biz_review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Integer reviewId;
    private Integer userId;
    private Integer orderId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
