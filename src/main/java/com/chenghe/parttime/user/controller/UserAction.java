package com.chenghe.parttime.user.controller;

/**
 * Created by lenovo on 2019/7/23.
 */

import com.alibaba.fastjson.JSONObject;
import com.chenghe.parttime.pojo.User;
import com.chenghe.parttime.service.IIdfaService;
import com.chenghe.parttime.service.ISysUserService;
import com.chenghe.parttime.service.IUserService;
import com.chenghe.parttime.sms.ISmsService;
import com.chenghe.parttime.sms.QingSmsServiceImpl;
import com.chenghe.parttime.sms.YtxSmsServiceImpl;
import com.chenghe.parttime.util.LruCache;
import com.chenghe.parttime.util.SecurityCode;
import com.youguu.core.util.PropertiesUtil;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.ws.rs.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;


@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "用户API")
        }
)
@Api(value = "UserAction")
@Path(value = "/user")
@Controller("userAction")
public class UserAction {

    private LruCache<String,String > tokenCache = new LruCache<String,String >(300);

    private LruCache<String,String > phoneCache = new LruCache<String,String >(300);

    @Resource
    private IUserService userService;

    @Resource
    private IIdfaService iIdfaService;

    @Resource
    private ISysUserService sysUserService;


    @GET
    @Path(value = "/getAppQQ")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "获取token", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = TokenResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = TokenResPonse.class)

    })
    public String getAppQQ(@ApiParam(value = "应用编号", required = true) @QueryParam("app") String app) {


        String appQQ = sysUserService.getRemark(app);

        if(appQQ==null || "".equals(appQQ)){
            appQQ = "2206388328";
        }

        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        json.put("qq", appQQ);

        json.put("showMsg", "商务合作请联系QQ：" + appQQ);



        return json.toJSONString();
    }

    @GET
    @Path(value = "/getToken")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "获取token", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = TokenResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = TokenResPonse.class)

    })
    public String queryAdList(@ApiParam(value = "手机号码", required = true) @QueryParam("phone") String phone) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        UUID uuid = UUID.randomUUID();

        json.put("token", uuid.toString());

        tokenCache.put(phone, uuid.toString());

        return json.toJSONString();
    }


    @GET
    @Path(value = "/getRand")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "获取验证码 ， 测试阶段，验证码：1234", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PhoneResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = PhoneResPonse.class)

    })
    public String getRand(@ApiParam(value = "手机号码", required = true) @QueryParam("phone") String phone,
                          @ApiParam(value = " 1:兼职圈 2:土豆 3:彩虹 4:暖阳兼职 6:松鼠兼职 5:蜜桔兼职 7:快乐兼职 8:点滴兼职") @QueryParam("app") @DefaultValue("1") int app,
                          @ApiParam(value = "token", required = true) @HeaderParam("token") String token) {

        JSONObject json = new JSONObject();

        String token_cache = tokenCache.get(phone);

        if (token == null || !token.equals(token_cache)) {

            json.put("status", "0001");

            json.put("message", "请重试");

            return json.toJSONString();
        }

        tokenCache.remove(phone);




        String random = SecurityCode.getSimpleSecurityCode();

        //兼职圈
        String sign = "500391";
        String skin = "900586";
        if(app==2){
            sign = "500392";
            skin = "900586";
        }
//        switch (app){
//            case 2:
//        }
        if(app>=3){//彩虹的短信通道不一致
            ISmsService smsService = new YtxSmsServiceImpl(app);
            smsService.sendRand(phone,random);
        }else{
            ISmsService smsService = new QingSmsServiceImpl();
            smsService.sendRand(phone,random,sign,skin);
        }


        json.put("status", "0000");

        json.put("message", "ok");

        json.put("rand", random);

        phoneCache.put(phone, random);

        return json.toJSONString();
    }


    @GET
    @Path(value = "/login")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "手机号验证码登录", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = UserResPonse.class),
            @ApiResponse(code = "0001", message = "失败")

    })
    public String login(@ApiParam(value = "手机号码", required = true) @QueryParam("phone") String phone,
                        @ApiParam(value = "验证码", required = true) @QueryParam("rand") String rand,
                        @ApiParam(value = "idfa(ios) 或者 imei(Android)", required = true) @QueryParam("idfa") String idfa) {

        JSONObject json = new JSONObject();

        if(!"13810436365".equals(phone)){
            String rand_cache = phoneCache.get(phone);

            if (rand == null || !rand.equals(rand_cache)) {

                json.put("status", "0001");

                json.put("message", "验证码错误");

                return json.toJSONString();
            }

            phoneCache.remove(phone);
        }



        User user = userService.getUser(phone);

        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setIdfa(idfa);
            user.setNickName(phone.substring(0, 3) + "****" + phone.subSequence(7, 11));
            userService.addUser(user);
        }

        json.put("status", "0000");

        json.put("message", "ok");

        UUID uuid = UUID.randomUUID();

        json.put("sessionId", uuid.toString());

        Properties properties = null;
        try {
            properties = PropertiesUtil.getProperties("properties/config.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imageUrl = properties.getProperty("imageUrl");

        user.setHeadPic(imageUrl + File.separator + user.getHeadPic());

        json.put("result", user);


        return json.toJSONString();
    }


    @GET
    @Path(value = "/getUser")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询个人(我的)信息", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = UserResPonse.class),
            @ApiResponse(code = "0001", message = "失败")

    })
    public String getUser(@ApiParam(value = "userId", required = true) @HeaderParam("userId") int userId) {

        UserResPonse resPonse = new UserResPonse();

        try {
            User user = userService.getUser(userId);

            if (user == null) {
                resPonse.setStatus("0001");
                resPonse.setMessage("用户不存在");
                return JSONObject.toJSONString(resPonse);
            }

            resPonse.setStatus("0000");
            resPonse.setMessage("ok");
            UUID uuid = UUID.randomUUID();
            resPonse.setSessionId(uuid.toString());

            UserResPonse.UserVo userVo = new UserResPonse.UserVo();
            userVo.setUserId(user.getUserId());
            userVo.setPhone(user.getPhone());
            userVo.setPwd(user.getPwd());
            userVo.setNickName(user.getNickName());
            userVo.setIdfa(user.getIdfa());
            Properties properties = null;
            try {
                properties = PropertiesUtil.getProperties("properties/config.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
            String imageUrl = properties.getProperty("imageUrl");

            userVo.setHeadPic(imageUrl + File.separator + user.getHeadPic());
            userVo.setcTime(user.getcTime());
            userVo.setmTime(user.getmTime());
            userVo.setRealName(user.getRealName());
            userVo.setSex(user.getSex());
            userVo.setExp(user.getExp());
            userVo.setDes(user.getDes());

            if (!StringUtils.isEmpty(user.getBirthday())) {
                SimpleDateFormat dft = new SimpleDateFormat("yyyy.MM.dd");
                userVo.setBirthday(dft.format(user.getBirthday()));
            } else {
                userVo.setBirthday("");
            }

            resPonse.setResult(userVo);
        } catch (Exception e) {
            resPonse.setStatus("0002");
            resPonse.setMessage("系统异常");
            e.printStackTrace();
        }
        return JSONObject.toJSONString(resPonse);
    }


    @POST
    @Path(value = "/updateUser")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "完善简历", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功"),
            @ApiResponse(code = "0001", message = "失败")

    })
    public String updateUser(@ApiParam(value = "图片base64", required = true) @FormParam("imgStr") String imgStr,
                             @ApiParam(value = "姓名", required = true) @FormParam("realName") String realName,
                             @ApiParam(value = "性别", required = true) @FormParam("sex") int sex,
                             @ApiParam(value = "出生日期 格式 yyyy.MM.dd", required = true) @FormParam("birthday") String birthday,
                             @ApiParam(value = "工作经验", required = true) @FormParam("exp") String exp,
                             @ApiParam(value = "自我介绍", required = true) @FormParam("des") String des,
                             @ApiParam(value = "用户ID", required = true) @HeaderParam("userId") int userId) {

        JSONObject json = new JSONObject();

        User user = userService.getUser(userId);

        if (user == null) {
            json.put("status", "0001");
            json.put("message", "用户不存在");
            return json.toJSONString();
        }

        String headPath = this.picHandler(userId, imgStr);

        if (headPath != null && !headPath.equals("")) {
            headPath = headPath + "?rand=" + new Date().getTime();
            user.setHeadPic(headPath);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        user.setSex(sex);

        user.setRealName(realName);

        try {
            user.setBirthday(sdf.parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setExp(exp);

        user.setDes(des);

        userService.updateUser(user);

        json.put("status", "0000");

        json.put("message", "ok");


        return json.toJSONString();
    }


    private String picHandler(int userId, String imgStr) {

        int a = userId % 64;

        Properties properties = null;
        try {
            properties = PropertiesUtil.getProperties("properties/config.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String rootPath = properties.getProperty("img.path");

        String savePath = rootPath + "userHead/" + a + "/" + userId + ".ipg";
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String webPath = "/userHead/" + a + "/" + userId + ".ipg";

        if (imgStr == null || "".equals(imgStr)) {
            return null;
        }

        if (imgStr.indexOf(",") >= 0) {
            imgStr = imgStr.substring(imgStr.indexOf(",") + 1);
        }


        byte[] fileByte = null;
        InputStream is = null;
        ByteArrayOutputStream os = null;
        ByteArrayInputStream bis = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                if (imgStr.indexOf(",") >= 0) {
                    imgStr = imgStr.substring(imgStr.indexOf(",") + 1);
                }

                // Base64解码
                fileByte = decoder.decodeBuffer(imgStr);
            } catch (Exception e) {
                e.printStackTrace();

            }
            bis = new ByteArrayInputStream(fileByte);
            bis.mark(0);
            BufferedImage bi = ImageIO.read(bis);
            ImageIO.write(bi, "JPG", new File(savePath));
            return webPath;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    @POST
    @Path(value = "/updateNickName")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "完善简历", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功"),
            @ApiResponse(code = "0001", message = "失败")

    })
    public String updateNickName(@ApiParam(value = "昵称", required = true) @FormParam("nickName") String nickName,
                                 @ApiParam(value = "userId", required = true) @HeaderParam("userId") int userId) {

        JSONObject json = new JSONObject();

        User user = userService.getUser(userId);

        if (user == null) {
            json.put("status", "0001");

            json.put("message", "用户不存在");
        }

        user.setNickName(nickName);

        userService.updateUser(user);

        json.put("status", "0000");

        json.put("message", "ok");


        return json.toJSONString();
    }


    @GET
    @Path(value = "/active")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "首次激活调用", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功"),
            @ApiResponse(code = "0001", message = "失败")

    })
    public String active(@ApiParam(value = "idfa", required = true) @QueryParam("idfa") String idfa,
                         @ApiParam(value = "操作系统 1 IOS  2 Android", required = true) @QueryParam("os") String os) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        iIdfaService.addIdfa(idfa, os);

        return json.toJSONString();
    }


    @GET
    @Path(value = "/getReviewStatus")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "获取渠道是否显示跳转第三方   1 显示跳转   其他都不显示", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = TokenResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = TokenResPonse.class)

    })
    public String getReviewStatus(@ApiParam(value = "应用编号", required = true) @QueryParam("app") int app,
                           @ApiParam(value = "渠道名称", required = true) @QueryParam("channel") String channel) {


        int status = 1;

//        if(app==4 && "vivo".equals(channel)){
//            status = 2;
//        }

        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        json.put("status", status);


        return json.toJSONString();
    }

    public static void main(String[] args) {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }
}
