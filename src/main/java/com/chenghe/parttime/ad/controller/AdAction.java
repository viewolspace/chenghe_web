package com.chenghe.parttime.ad.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenghe.parttime.pojo.Ad;
import com.chenghe.parttime.service.IAdService;
import com.chenghe.parttime.service.IAdStatService;
import com.chenghe.parttime.service.IChannelViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.*;
import java.util.List;

/**
 * Created by lenovo on 2019/7/22.
 */
@SwaggerDefinition(
        tags = {
                @Tag(name = "v1.0", description = "用户API")
        }
)
@Api(value = "AdAction")
@Path(value = "/ad")
@Controller("adAction")
public class AdAction {
    @Resource
    private IAdService adService;

    @Resource
    private IAdStatService adStatService;

    @Resource
    private IChannelViewService channelViewService;

    @GET
    @Path(value = "/queryAdList")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "根据广告分类查询广告", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = AdResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = AdResPonse.class)

    })
    public String queryAdList(@ApiParam(value = "分类id", required = true) @QueryParam("categoryId") String categoryId,
                              @ApiParam(value = "appId") @HeaderParam("appId") @DefaultValue("1") int appId,
                              @ApiParam(value = "channelNo") @HeaderParam("channelNo") @DefaultValue("") String channelNo,
                              @ApiParam(value = "version") @HeaderParam("version") @DefaultValue("") String version) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        List<Ad> list = adService.listAd(categoryId);

        //----------------处理审核--------------------
        boolean isView = channelViewService.isView(String.valueOf(appId),version,channelNo);

        if(isView){
            for(Ad ad:list){
                ad.setUrl(ad.getViewUrl());
            }
        }
        //------------------------------------
        json.put("result", list);

        return json.toJSONString();
    }



    @POST
    @Path(value = "/adStat")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "广告统计", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = AdResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = AdResPonse.class)

    })
    public String adStat(@ApiParam(value = "广告id", required = true) @QueryParam("adId") int adId,
                              @ApiParam(value = "userId") @HeaderParam("userId") @DefaultValue("0") int userId) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        if(adId > 0){

            adStatService.userClick(userId,adId);

        }

        return json.toJSONString();
    }
}
