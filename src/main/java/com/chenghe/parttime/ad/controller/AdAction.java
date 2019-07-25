package com.chenghe.parttime.ad.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenghe.parttime.pojo.Ad;
import com.chenghe.parttime.service.IAdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

    @GET
    @Path(value = "/queryAdList")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "根据广告分类查询广告", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = AdResPonse.class),
            @ApiResponse(code = "0001", message = "失败", response = AdResPonse.class)

    })
    public String queryAdList(@ApiParam(value = "分类id", required = true) @QueryParam("categoryId") String categoryId) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        List<Ad> list = adService.listAd(categoryId);

        json.put("result", list);

        return json.toJSONString();
    }
}
