package com.chenghe.parttime.parttime.controller;

import com.alibaba.fastjson.JSONObject;
import com.chenghe.parttime.pojo.PartTime;
import com.chenghe.parttime.service.IPartTimeService;
import io.swagger.annotations.*;
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
@Api(value = "PartTimeAction")
@Path(value = "/partTime")
@Controller("partTimeAction")
public class PartTimeAction {
        @Resource
        private IPartTimeService partTimeService;
        @GET
        @Path(value = "/queryRecommnet")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "查询热门或者今日精选", notes = "", author = "更新于 2019-07-22")
//        @ApiResponses(value = {
//                @ApiResponse(code = "0000", message = "请求成功", response = QueryUserResponse.class),
//                @ApiResponse(code = "0001", message = "用户不存在", response = QueryUserResponse.class)

//        })
        public String queryRecommnet(@ApiParam(value = " 1 热门 2 精选", required = true) @QueryParam("recomment") int recomment,
                                     @ApiParam(value = "第几页", required = true) @QueryParam("pageIndex") int pageIndex,
                                     @ApiParam(value = "页数", required = true) @QueryParam("pageSize") int pageSize){
                JSONObject json = new JSONObject();

                json.put("status","0000");

                json.put("message","ok");

                List<PartTime> list  = partTimeService.listRecomment(recomment, pageIndex, pageSize);

                json.put("result",list);

                return json.toJSONString();
        }



        @GET
        @Path(value = "/queryAll")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "查询全部 或者 搜索", notes = "", author = "更新于 2019-07-22")
//        @ApiResponses(value = {
//                @ApiResponse(code = "0000", message = "请求成功", response = QueryUserResponse.class),
//                @ApiResponse(code = "0001", message = "用户不存在", response = QueryUserResponse.class)

//        })
        public String queryAll(@ApiParam(value = "关键词，可以不传", required = false) @QueryParam("keyWord") String keyWord,
                                     @ApiParam(value = "第几页", required = true) @QueryParam("pageIndex") int pageIndex,
                                     @ApiParam(value = "页数", required = true) @QueryParam("pageSize") int pageSize){
                JSONObject json = new JSONObject();

                json.put("status","0000");

                json.put("message","ok");

                List<PartTime> list  = partTimeService.listAll(keyWord, pageIndex, pageSize);

                json.put("result",list);

                return json.toJSONString();
        }


        @GET
        @Path(value = "/getPartTime")
        @Produces("text/html;charset=UTF-8")
        @ApiOperation(value = "职位详情", notes = "", author = "更新于 2019-07-22")
//        @ApiResponses(value = {
//                @ApiResponse(code = "0000", message = "请求成功", response = QueryUserResponse.class),
//                @ApiResponse(code = "0001", message = "用户不存在", response = QueryUserResponse.class)

//        })
        public String getPartTime(
                               @ApiParam(value = "id", required = true) @QueryParam("id") int id){
                JSONObject json = new JSONObject();

                json.put("status","0000");

                json.put("message","ok");

                PartTime partTime  = partTimeService.getPartTime(id);

                json.put("result",partTime);

                return json.toJSONString();
        }
}
