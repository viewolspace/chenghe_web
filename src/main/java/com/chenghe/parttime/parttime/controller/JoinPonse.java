package com.chenghe.parttime.parttime.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lenovo on 2019/7/22.
 */
@ApiModel
public class JoinPonse {
    @ApiModelProperty("status")
    private String status;

    @ApiModelProperty("返回消息")
    private String message;

    @ApiModelProperty("是否需要完善信息 0 不需要  1 需要")
    private int flag;

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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
