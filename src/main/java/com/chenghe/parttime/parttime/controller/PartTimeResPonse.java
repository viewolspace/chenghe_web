package com.chenghe.parttime.parttime.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lenovo on 2019/7/22.
 */
@ApiModel
public class PartTimeResPonse {
    @ApiModelProperty("status")
    private String status;

    @ApiModelProperty("返回消息")
    private String message;

    @ApiModelProperty("0 未报名  1 已报名")
    private String isJoin;

    @ApiModelProperty("兼职内容")
    private PartTimeVo result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PartTimeVo getResult() {
        return result;
    }

    public void setResult(PartTimeVo result) {
        this.result = result;
    }
}
