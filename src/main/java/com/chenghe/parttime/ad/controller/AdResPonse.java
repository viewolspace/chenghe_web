package com.chenghe.parttime.ad.controller;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by lenovo on 2019/7/22.
 */
public class AdResPonse {
    @ApiModelProperty("status")
    private String status;

    @ApiModelProperty("返回消息")
    private String message;

    @ApiModelProperty("广告列表")
    private List<AdVo> result;


    class AdVo{
        @ApiModelProperty("广告id")
        private int id;


        @ApiModelProperty("图片地址")
        private String imageUrl;

        @ApiModelProperty("跳转地址")
        private String url;

        @ApiModelProperty("标题")
        private int title;

    }
}
