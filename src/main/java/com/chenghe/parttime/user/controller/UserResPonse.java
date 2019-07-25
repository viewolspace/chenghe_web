package com.chenghe.parttime.user.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by lenovo on 2019/7/22.
 */
@ApiModel
public class UserResPonse {
    @ApiModelProperty("status")
    private String status;

    @ApiModelProperty("返回消息")
    private String message;

    @ApiModelProperty("result")
    private UserVo result;

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

    public UserVo getResult() {
        return result;
    }

    public void setResult(UserVo result) {
        this.result = result;
    }

    @ApiModel
    class UserVo{
        @ApiModelProperty("userId")
        private Integer userId;

        @ApiModelProperty("手机")
        private String phone;


        private String pwd;

        @ApiModelProperty("昵称")
        private String nickName;
        private String idfa;

        @ApiModelProperty("头像")
        private String headPic;
        private Date cTime;
        private Date mTime;

        @ApiModelProperty("真实姓名")
        private String realName;

        @ApiModelProperty("性别 1 男  2 女")
        private int sex;

        @ApiModelProperty("生日")
        private Date birthday;

        @ApiModelProperty("工作经验")
        private String exp;

        @ApiModelProperty("自我介绍")
        private String des;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getIdfa() {
            return idfa;
        }

        public void setIdfa(String idfa) {
            this.idfa = idfa;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }

        public Date getcTime() {
            return cTime;
        }

        public void setcTime(Date cTime) {
            this.cTime = cTime;
        }

        public Date getmTime() {
            return mTime;
        }

        public void setmTime(Date mTime) {
            this.mTime = mTime;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public String getExp() {
            return exp;
        }

        public void setExp(String exp) {
            this.exp = exp;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }
}
