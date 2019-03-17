package com.ytzl.gotrip.controller;

import com.ytzl.gotrip.model.GotripUser;
import com.ytzl.gotrip.service.GotripUserService;
import com.ytzl.gotrip.utils.common.Dto;
import com.ytzl.gotrip.utils.common.DtoUtil;
import com.ytzl.gotrip.vo.userinfo.ItripUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.ytzl.gotrip.utils.common.DtoUtil.returnDataSuccess;

/**
 * @author jayden
 */
@RestController
@RequestMapping("/api")
@Api(description = "用户模块控制器")
public class GotripUserController {

    @Resource
    private GotripUserService gotripUserService;

    // value 简单描述  notes 详细描述
    @ApiOperation(value = "根据用户Code查询用户信息",
            notes = "根据用户Code查询用户信息  \n"+
                    " 错误码:  \n"+
                    "    30003:参数不能为空")
    @PostMapping("/findByUserCode")
    public Dto findByUserCode(
            @ApiParam(value = "登录账号")
            @RequestParam("userCode") String userCode) throws Exception {
        GotripUser gotripUser =
                gotripUserService.findByUserCode(userCode);
        return returnDataSuccess(gotripUser);
    }

    @ApiOperation(value = "手机号激活")
   @PutMapping("/velidatephone")
    public Dto velidatephone(@ApiParam(value = "登入手机号")
                             @RequestParam String user,
                             @ApiParam(value = "短信验证码")
                             @RequestParam String code) throws Exception {
         gotripUserService.validatePhone(user,code);
         return DtoUtil.returnDataSuccess("激活成功");
    }
    @ApiOperation(value = "邮箱激活")
    @PutMapping("/velidateEmail")
    public Dto velidateEmail(@ApiParam(value = "登入邮箱")
                             @RequestParam String user,
                             @ApiParam(value = "短信验证码")
                             @RequestParam String code) throws Exception {
        gotripUserService.validateEmail(user,code);
        return DtoUtil.returnDataSuccess("激活成功");
    }

   @ApiOperation(value = "手机号注册")
   @PostMapping("/resgisterbyphone")
    public  Dto registerByPhone(@RequestBody ItripUserVO itripUserVO) throws Exception {
      gotripUserService.registerByPhone(itripUserVO);
      return returnDataSuccess("注册成功");
    }

    @ApiOperation(value = "邮箱注册")
    @PostMapping("/registerByEmail")
    public  Dto registerByEmail(@RequestBody ItripUserVO itripUserVO) throws Exception {
        gotripUserService.registerByEmail(itripUserVO);
        return returnDataSuccess("注册成功");
    }
}
