package com.chenghe.parttime.user.controller;

/**
 * Created by lenovo on 2019/7/23.
 */

import com.alibaba.fastjson.JSONObject;
import com.chenghe.parttime.pojo.User;
import com.chenghe.parttime.service.IUserService;
import com.chenghe.parttime.util.LruCache;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.*;
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


    public static void main(String[] args) {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }
}
