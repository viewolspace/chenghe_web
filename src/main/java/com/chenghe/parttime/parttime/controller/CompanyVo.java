package com.chenghe.parttime.parttime.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lenovo on 2019/7/23.
 */
@ApiModel
public class CompanyVo {
    @ApiModelProperty("公司ID")
    private Integer id;

    @ApiModelProperty("公司名称")
    private String name;

    @ApiModelProperty("公司Logo")
    private String logo;

    @ApiModelProperty("QQ")
    private String qq;

    @ApiModelProperty("微信")
    private String wx;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("公司描述")
    private String des;

    @ApiModelProperty("综合评分")
    private Integer star;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }
}
