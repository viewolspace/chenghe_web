package com.chenghe.parttime.parttime.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenghe.parttime.pojo.*;
import com.chenghe.parttime.pojo.Contact;
import com.chenghe.parttime.service.*;
import com.youguu.core.logging.Log;
import com.youguu.core.logging.LogFactory;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.*;
import java.util.*;

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
    private ISysUserService sysUserService;
    @Resource
    private IPartTimeService partTimeService;
    @Resource
    private IUserJoinService userJoinService;
    @Resource
    private ICategoryService categoryService;
    @Resource
    private IUserService userService;
    @Resource
    private ICompanyService companyService;
    @Resource
    private IChannelViewService channelViewService;



    private Log log = LogFactory.getLog(PartTimeAction.class);

    @GET
    @Path(value = "/queryRecommnet")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询热门或者今日精选", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PartTimeListResPonse.class),
            @ApiResponse(code = "0001", message = "请求失败", response = PartTimeListResPonse.class)

    })
    public String queryRecommnet(@ApiParam(value = " 1 兼职圈-推荐 2 兼职圈-精选 3 兼职圈-热门  4 土豆-推荐 5 土豆-精选 6 土豆-热门 7 新app-推荐 8 新app-精选 9 新app-热门", required = true) @QueryParam("recommend") int recommend,
                                 @ApiParam(value = " 0 正序 1 倒叙") @QueryParam("order") @DefaultValue("0") int order,
                                 @ApiParam(value = "第几页", required = true) @QueryParam("pageIndex") int pageIndex,
                                 @ApiParam(value = "页数", required = true) @QueryParam("pageSize") int pageSize,
                                 @ApiParam(value = "appId") @HeaderParam("appId") @DefaultValue("1") int appId,
                                 @ApiParam(value = "channelNo") @HeaderParam("channelNo") @DefaultValue("") String channelNo,
                                 @ApiParam(value = "version") @HeaderParam("version") @DefaultValue("") String version) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        //----------------处理审核--------------------
        boolean isView = channelViewService.isView(String.valueOf(appId),version,channelNo);
        if(isView){
            try{
                recommend = Integer.parseInt(getAllId(appId,isView));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //------------------------------------

        List<PartTime> list = partTimeService.listRecommend(recommend, pageIndex, pageSize);

        if(isView){
            list = this.randomList(list);
        }

        if(order==1){
            Collections.reverse(list);
        }

        json.put("result", list);

        return json.toJSONString();
    }


    @GET
    @Path(value = "/queryBycategoryId")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询分类下的兼职列表", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PartTimeListResPonse.class),
            @ApiResponse(code = "0001", message = "请求失败", response = PartTimeListResPonse.class)

    })
    public String queryBycategoryId(@ApiParam(value = "分类id", required = true) @QueryParam("categoryId") String categoryId,
                                    @ApiParam(value = "第几页", required = true) @QueryParam("pageIndex") int pageIndex,
                                    @ApiParam(value = "页数", required = true) @QueryParam("pageSize") int pageSize) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        Category category = categoryService.getCategory(categoryId);

        List<PartTime> list = partTimeService.listByCategory(categoryId, pageIndex, pageSize);

        json.put("result", list);

        json.put("category", category);

        return json.toJSONString();
    }


    @GET
    @Path(value = "/queryAll")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询全部 或者 搜索", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PartTimeListResPonse.class),
            @ApiResponse(code = "0001", message = "请求失败", response = PartTimeListResPonse.class)

    })
    public String queryAll(@ApiParam(value = "关键词，可以不传", required = false) @QueryParam("keyWord") String keyWord,
                           @ApiParam(value = " 1:兼职圈 2:土豆 3:新app") @QueryParam("app") @DefaultValue("0") int app,
                           @ApiParam(value = "第几页", required = true) @QueryParam("pageIndex") int pageIndex,
                           @ApiParam(value = "页数", required = true) @QueryParam("pageSize") int pageSize,
                           @ApiParam(value = "appId") @HeaderParam("appId") @DefaultValue("1") int appId,
                           @ApiParam(value = "channelNo") @HeaderParam("channelNo") @DefaultValue("") String channelNo,
                           @ApiParam(value = "version") @HeaderParam("version") @DefaultValue("") String version) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        boolean isView = channelViewService.isView(String.valueOf(appId),version,channelNo);

        String recommend = "";

        try{
            recommend = getAllId(appId,isView);
        }catch (Exception e){
            e.printStackTrace();
        }

        List<PartTime> list = partTimeService.listAll(keyWord,recommend,pageIndex,pageSize);

        json.put("result", list);

        return json.toJSONString();
    }

    private int fac = 0;

    @GET
    @Path(value = "/getPartTime")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "职位详情", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PartTimeResPonse.class),
            @ApiResponse(code = "0001", message = "请求失败", response = PartTimeResPonse.class)

    })
    public String getPartTime(
            @ApiParam(value = "id", required = true) @QueryParam("id") int id,
            @ApiParam(value = "userId") @HeaderParam("userId") @DefaultValue("0") int userId,
            @ApiParam(value = "appId") @HeaderParam("appId") @DefaultValue("1") int appId,
            @ApiParam(value = "channelNo") @HeaderParam("channelNo") @DefaultValue("") String channelNo,
            @ApiParam(value = "version") @HeaderParam("version") @DefaultValue("") String version) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        log.info("appId:{} channelNo:{}",appId,channelNo);

        PartTime partTime = partTimeService.getAndStatPartTime(id, userId, "");

        //准备随机联系方式
        if(partTime.getExt()!=null && !"".equals(partTime.getExt())){
            List<Contact> list = JSON.parseArray(partTime.getExt(),Contact.class);
            if(list!=null && list.size()>0){
                List<Contact> temp_list = new ArrayList<>();
                if(partTime.getContact()!=null && !partTime.getContact().equals("")){
                    Contact contact = new Contact();
                    contact.setContact(partTime.getContact());
                    contact.setContactType(partTime.getContactType());
                    temp_list.add(contact);
                }
                for(Contact contact:list){
                    if(contact.getContact()!=null && !"".equals(contact.getContact())){
                        temp_list.add(contact);
                    }
                }

                int number = new Random().nextInt(temp_list.size());
                log.info("parttime id:{} ,companyId:{} ,this number:{}",partTime.getId(),partTime.getCompanyId(),temp_list.get(number).getContact());
                partTime.setContactType(temp_list.get(number).getContactType());
                partTime.setContact(temp_list.get(number).getContact());

            }
        }
        //随机联系方式结束

        /*************看当前是否是审核中*************/

//        String phone = this.getPhone(appId, channelNo);
//        if(phone!=null){
//            partTime.setContactType(3);
//            partTime.setContact(phone);//手机号码
//        }
        /*************看当前是否是审核中*************/
        String isJoin = "0"; //未报名

        if (userId > 0) {
            UserJoin us = userJoinService.getUserJoin(userId, id, 1);
            if (us != null) {
                isJoin = "1";
            }
        }

        json.put("isJoin", isJoin);

        json.put("result", partTime);

        /**
         * 公司信息返回
         */
        Company company = companyService.getCompany(partTime.getCompanyId());
        CompanyVo companyVo = new CompanyVo();
        if (null != company) {
            companyVo.setId(company.getId());
            companyVo.setDes(company.getDes());
            companyVo.setLogo(company.getLogo());
            companyVo.setName(company.getName());
            companyVo.setPhone(company.getPhone());
            companyVo.setQq(company.getQq());
            companyVo.setWx(company.getWx());
            companyVo.setStar(company.getStar());
        }

        json.put("company", companyVo);

        long times = System.currentTimeMillis();
        if(fac>=10){
            fac = 0;
        }
        times=times*10 + fac;

        fac = fac + 1;

        json.put("customerId", times);

//        Map<Integer,Integer> map = sysUserService.getMap();
//        if(map.containsKey(partTime.getCompanyId())){
//            json.put("customerId", map.get(partTime.getCompanyId()));
//        }else{
//            json.put("customerId", 0);
//        }



        return json.toJSONString();
    }

//    private String getPhone(int appId,String channelNo){
//        if(channelNo==null || "".endsWith(channelNo)) return null;
//        ChannelView channelView = channelViewService.getChannelView();
//        if(channelView!=null){
//            String appIds = channelView.getAppIds();
//            if((appIds+",").indexOf(appId+",")>=0){
//                String channels = channelView.getChannelNos() + ",";
//                if(channels.indexOf(channelNo+",")>=0){
//                    String phones = channelView.getPhoneNums();
//                    String[] phoneArray = phones.split(",");
//                    if(phoneArray!=null && phoneArray.length>0){
//                        int number = new Random().nextInt(phoneArray.length);
//                        return phoneArray[number];
//                    }
//                }
//            }
//        }
//        return null;
//    }


    @GET
    @Path(value = "/copyPartTime")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "拷贝职位qq等", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功"),
            @ApiResponse(code = "0001", message = "请求失败")

    })
    public String copyPartTime(
            @ApiParam(value = "id", required = true) @QueryParam("id") int id,
            @ApiParam(value = "userId", defaultValue = "0", required = true) @HeaderParam("userId") @DefaultValue("0") int userId) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

//        User user = userService.getUser(userId);

        partTimeService.copyPartTime(userId, id);

        return json.toJSONString();
    }


    @GET
    @Path(value = "/joinPartTime")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "报名", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功" , response=JoinPonse.class),
            @ApiResponse(code = "0001", message = "请求失败" , response=JoinPonse.class)

    })
    public String joinPartTime(
            @ApiParam(value = "id", required = true) @QueryParam("id") int id,
            @ApiParam(value = "userId", defaultValue = "0", required = true) @HeaderParam("userId") @DefaultValue("0") int userId) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        User user = userService.getUser(userId);

        int flag  = 0;

        partTimeService.joinPartTime(userId, id);

        if (user != null) {

            if(user.getBirthday()==null || "".equals(user.getBirthday()) || user.getDes()==null || "".equals(user.getDes())
                    || user.getExp()==null || "".equals(user.getExp()) ){
                flag = 1; //需要完善
            }
        }

        json.put("flag", flag);
        return json.toJSONString();
    }


    @GET
    @Path(value = "/queryMyPartTime")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "查询我报名的兼职", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PartTimeListResPonse.class),
            @ApiResponse(code = "0001", message = "请求失败", response = PartTimeListResPonse.class)

    })
    public String queryMyPartTime(@ApiParam(value = "用户id", required = true) @HeaderParam("userId") int userId,
                                  @ApiParam(value = "第几页", required = true) @QueryParam("pageIndex") int pageIndex,
                                  @ApiParam(value = "页数", required = true) @QueryParam("pageSize") int pageSize) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        if (userId <= 0) {
            return json.toJSONString();
        }

        List<PartTime> list = partTimeService.listMyjoin(userId, pageIndex, pageSize);

        json.put("result", list);

        return json.toJSONString();
    }




    @GET
    @Path(value = "/question")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "意见反馈", notes = "", author = "更新于 2019-07-22")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PartTimeListResPonse.class),
            @ApiResponse(code = "0001", message = "请求失败", response = PartTimeListResPonse.class)

    })
    public String question(@ApiParam(value = "用户id", required = true) @HeaderParam("userId") int userId,
                                  @ApiParam(value = "问题描述", required = true) @FormParam("question") String question,
                                  @ApiParam(value = "1 兼职圈  2 土豆 3 彩虹", required = true) @FormParam("app") int app) {
        JSONObject json = new JSONObject();

        log.info("问题:{} , app:{} , uid:{}",question,app,userId);

        json.put("status", "0000");

        json.put("message", "感谢您的反馈，我们会尽快处理！");


        return json.toJSONString();
    }


    @GET
    @Path(value = "/queryByIds")
    @Produces("text/html;charset=UTF-8")
    @ApiOperation(value = "通过职位id查询多个职位", notes = "", author = "更新于 2020")
    @ApiResponses(value = {
            @ApiResponse(code = "0000", message = "请求成功", response = PartTimeListResPonse.class),
            @ApiResponse(code = "0001", message = "请求失败", response = PartTimeListResPonse.class)

    })
    public String queryByIds(@ApiParam(value = " 多个使用 , 分隔", required = true) @QueryParam("ids") String ids,
                                 @ApiParam(value = "appId") @HeaderParam("appId") @DefaultValue("1") int appId,
                                 @ApiParam(value = "channelNo") @HeaderParam("channelNo") @DefaultValue("") String channelNo,
                                 @ApiParam(value = "version") @HeaderParam("version") @DefaultValue("") String version) {
        JSONObject json = new JSONObject();

        json.put("status", "0000");

        json.put("message", "ok");

        String[] idArrays = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        if(idArrays.length>0){
            for(String str:idArrays){
                try {
                    idList.add(Integer.parseInt(str));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        Integer[] a = new Integer[idList.size()];

        List<PartTime> list = partTimeService.queryByIds(idList.toArray(a));

        json.put("result", list);

        return json.toJSONString();
    }


    private String getAllId(int appId,boolean isView){
        String menu = "";
        List<SysDictionary> list = sysUserService.selectMenu(appId);

        for (SysDictionary sysDictionary:list){
            if(sysDictionary.getName().indexOf("审核")>=0 && isView){//审核状态
                menu = sysDictionary.getValue();
            }else{
                if(sysDictionary.getName().indexOf("审核")<0){
                    menu += sysDictionary.getValue() + ",";
                }
            }
        }

        if(menu.endsWith(",")){
            menu.substring(0,menu.length()-1);
        }

        return menu;
    }


    private List<PartTime> randomList(List<PartTime> list ){
        List<PartTime> result = new ArrayList<>();
        if(list!=null && list.size()>0){
            Collections.shuffle(list);
            Random r = new Random();
            int len = 5;
            if(r.nextInt(100) > 50){
                len =6;
            }
            result = list.subList(0,len>list.size()?list.size():len);
        }
        return result;

    }

}
