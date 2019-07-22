package com.chenghe.parttime.parttime.controller;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2019/7/22.
 */
public class PartTimeResPonse {
    @ApiModelProperty("status")
    private String status;

    @ApiModelProperty("返回消息")
    private String message;

    @ApiModelProperty("广告列表")
    private List<PartTimeVo> result;


    class PartTimeVo{
        private Integer id;
        private Integer companyId;
        private Integer recommend;
        private String categoryId;
        private String categoryName;
        private Integer topNum;
        private String title;
        private Integer salary;
        private Integer cycle;
        private String lable;
        private Integer contactType;
        private String contact;
        private String content;
        private Integer num;
        private String workTime;
        private String workAddress;
        private Integer status;
        private Date sTime;
        private Date eTime;
        private Integer browseNum;
        private Integer copyNum;
        private Integer joinNum;
        private Date cTime;
        private Date mTime;

    }
}
