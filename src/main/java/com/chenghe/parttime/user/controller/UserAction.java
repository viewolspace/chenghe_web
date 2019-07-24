package com.chenghe.parttime.user.controller;

/**
 * Created by lenovo on 2019/7/23.
 */

import com.alibaba.fastjson.JSONObject;
import com.chenghe.parttime.pojo.User;
import com.chenghe.parttime.service.IUserService;
import com.chenghe.parttime.util.LruCache;
import com.youguu.core.util.PropertiesUtil;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.ws.rs.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private LruCache tokenCache = new LruCache(300);

    private LruCache phoneCache = new LruCache(300);

    @Resource
    private IUserService userService;


    @GET
    @Path(value = "/getToken")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "获取token", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = TokenResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = TokenResPonse.class)

    })
    public String queryAdList(@ApiParam(value = "手机号码", required = true) @QueryParam("phone") String phone){
        JSONObject json = new JSONObject();

        json.put("status","0000");

        json.put("message","ok");

        UUID uuid = UUID.randomUUID();

        json.put("token",uuid.toString());

        tokenCache.put(phone,uuid.toString());

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
                              @ApiParam(value = "token", required = true) @HeaderParam("token") String token){

        JSONObject json = new JSONObject();

        String token_cache = tokenCache.get(phone);

        if(token==null || !token.equals(token_cache)){

            json.put("status","0001");

            json.put("message","请重试");

            return json.toJSONString();
        }

        tokenCache.remove(phone);


        String random = "1234";

        json.put("status","0000");

        json.put("message","ok");

        json.put("rand",random);

        phoneCache.put(phone,random);

        return json.toJSONString();
    }



    @GET
    @Path(value = "/login")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "手机号验证码登录", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PhoneResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = PhoneResPonse.class)

    })
    public String login(@ApiParam(value = "手机号码", required = true) @QueryParam("phone") String phone,
                          @ApiParam(value = "验证码", required = true) @QueryParam("rand") String rand,
                          @ApiParam(value = "idfa(ios) 或者 imei(Android)", required = true) @QueryParam("idfa") String idfa){

        JSONObject json = new JSONObject();

        String rand_cache = phoneCache.get(phone);

        if(rand==null || !rand.equals(rand_cache)){

            json.put("status","0001");

            json.put("message","验证码错误");

            return json.toJSONString();
        }

        phoneCache.remove(phone);

        User user = userService.getUser(phone);

        if(user == null){
            user = new User();
            user.setPhone(phone);
            user.setIdfa(idfa);
            user.setNickName(phone.substring(0,3) + "****" + phone.subSequence(7,11));
            userService.addUser(user);
        }

        json.put("status","0000");

        json.put("message","ok");

        UUID uuid = UUID.randomUUID();

        json.put("sessionId",uuid.toString());

        json.put("result",user);


        return json.toJSONString();
    }




    @GET
    @Path(value = "/getUser")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "手机号验证码登录", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PhoneResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = PhoneResPonse.class)

    })
    public String getUser(@ApiParam(value = "userId", required = true) @HeaderParam("userId") int userId){

        JSONObject json = new JSONObject();

        User user = userService.getUser(userId);

        json.put("status","0000");

        json.put("message","ok");

        UUID uuid = UUID.randomUUID();

        json.put("sessionId",uuid.toString());

        json.put("result",user);


        return json.toJSONString();
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
                        @ApiParam(value = "性别", required = true) @FormParam("sex") int sex,
                        @ApiParam(value = "出生日期", required = true) @FormParam("birthday") String birthday,
                        @ApiParam(value = "工作经验", required = true) @FormParam("exp") String exp,
                        @ApiParam(value = "自我介绍", required = true) @FormParam("des") String des,
                        @ApiParam(value = "userId", required = true) @HeaderParam("userId") int userId){

        JSONObject json = new JSONObject();

        User user = userService.getUser(userId);

        if(user == null){
            json.put("status","0001");

            json.put("message","用户不存在");
        }

        String headPath = this.picHandler(userId,imgStr);

        if(headPath!=null && !headPath.equals("")){
            user.setHeadPic(headPath);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        user.setSex(sex);

        try {
            user.setBirthday(sdf.parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setExp(exp);

        user.setDes(des);

        userService.updateUser(user);

        json.put("status","0000");

        json.put("message","ok");


        return json.toJSONString();
    }


    private String picHandler(int userId,String imgStr){

        int a = userId%64;

        Properties properties = null;
        try {
            properties = PropertiesUtil.getProperties("properties/config.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String rootPath = properties.getProperty("img.path");

        String savePath = rootPath + "userHead/" + a + "/" + userId + ".ipg";
        String webPath = "/userHead/" + a + "/" + userId + ".ipg";

        if(imgStr==null || "".equals(imgStr)){
            return null;
        }

        if(imgStr.indexOf(",") >= 0){
            imgStr = imgStr.substring(imgStr.indexOf(",")+1);
        }


        byte[] fileByte = null;
        InputStream is = null;
        ByteArrayOutputStream os = null;
        ByteArrayInputStream bis = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                if(imgStr.indexOf(",") >= 0){
                    imgStr = imgStr.substring(imgStr.indexOf(",")+1);
                }

                // Base64解码
                fileByte = decoder.decodeBuffer(imgStr);
            } catch (Exception e) {
                e.printStackTrace();

            }
            bis= new ByteArrayInputStream(fileByte);
            bis.mark(0);
            BufferedImage bi = ImageIO.read(bis);
            ImageIO.write(bi,"JPG",new File(savePath));
            return webPath;
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            if(is!=null){ try {is.close();} catch (IOException e) {e.printStackTrace();}}
            if(os!=null){ try {os.close();} catch (IOException e) {e.printStackTrace();}}
            if(bis!=null){ try {bis.close();} catch (IOException e) {e.printStackTrace();}}
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
                             @ApiParam(value = "userId", required = true) @HeaderParam("userId") int userId){

        JSONObject json = new JSONObject();

        User user = userService.getUser(userId);

        if(user == null){
            json.put("status","0001");

            json.put("message","用户不存在");
        }

        user.setNickName(nickName);

        userService.updateUser(user);

        json.put("status","0000");

        json.put("message","ok");


        return json.toJSONString();
    }

    public static void main(String[] args) {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }
}
