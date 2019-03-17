package com.ytzl.gotrip.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ytzl.gotrip.ext.utils.RedisUtils;
import com.ytzl.gotrip.model.GotripUser;
import com.ytzl.gotrip.rpc.api.RpcGotripUserService;
import com.ytzl.gotrip.rpc.api.RpcSendMailService;
import com.ytzl.gotrip.rpc.api.RpcSendMessageService;
import com.ytzl.gotrip.service.GotripUserService;
import com.ytzl.gotrip.utils.common.Constants;
import com.ytzl.gotrip.utils.common.DigestUtil;
import com.ytzl.gotrip.utils.common.EmptyUtils;
import com.ytzl.gotrip.utils.common.ErrorCode;
import com.ytzl.gotrip.utils.exception.GotripException;
import com.ytzl.gotrip.vo.userinfo.ItripUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author jayden
 */
@Service("gotripUserService")
public class GotripUserServiceImpl implements GotripUserService {

    private Logger LOG= LoggerFactory.getLogger(GotripUserServiceImpl.class);

    @Reference
    private RpcGotripUserService rpcGotripUserService;

    @Reference
    private RpcSendMessageService rpcSendMessageService;

    @Reference
    private RpcSendMailService rpcSendMailService;
    //缓存
    @Resource
    private RedisUtils redisUtils;



    @Override
    public GotripUser findByUserCode(String userCode) throws Exception {
        //校验数据
        if (EmptyUtils.isEmpty(userCode)) {
            throw new GotripException("用户Code不能为空!",
                    ErrorCode.AUTH_PARAMETER_ERROR);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", userCode);
        List<GotripUser> gotripUserList =
                rpcGotripUserService.getGotripUserListByMap(params);
//        if (EmptyUtils.isEmpty(gotripUserList)) {
////            throw new GotripException("登录账号不存在!",
////                    ErrorCode.AUTH_PARAMETER_ERROR);
////        }
////        return gotripUserList.get(0);
        return EmptyUtils.isEmpty(gotripUserList)?null
                :gotripUserList.get(0);
    }

    /**
     * 手机号注册
     * @param itripUserVO
     * @throws Exception
     */
    @Override
    public void registerByPhone(ItripUserVO itripUserVO) throws Exception {
        //数据验证
        checkRegisterData(itripUserVO);
        //验证手机号
        if (!validPhone(itripUserVO.getUserCode())) {
            throw new GotripException("手机格式不正确",
                    ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //判断用户是否存在
        GotripUser user = this.findByUserCode(itripUserVO.getUserCode());
        if(!EmptyUtils.isEmpty(user)){
            throw new GotripException("用户已存在",ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //构建用户信息
        GotripUser gotripUser=new GotripUser();
        BeanUtils.copyProperties(itripUserVO,gotripUser);
        gotripUser.setActivated(0);

        //数据加密
        String mdgUserPassword=DigestUtil.hmacSign(gotripUser.getUserPassword());
        gotripUser.setUserPassword(mdgUserPassword);


        //数据入库
        Integer resultSize= rpcGotripUserService.insertGotripUser(gotripUser);
        if(resultSize<=0){
            throw new GotripException("用户注册失败!请稍后再试",
                    ErrorCode.AUTH_UNKNOWN);
        }
        //发生短信验证码
        //构建四为验证码
        int code= DigestUtil.randomCode();
        rpcSendMessageService.sendMessage(gotripUser.getUserCode(),"1",""+code);
        //将验证码保存到redis中
        String key= Constants.RedisKeyPrefix.ACTIVATION_MOBILE_PREFIX+gotripUser.getUserCode();
        redisUtils.set(key, 60*3,""+code);

    }

    /**
     * 邮箱注册
     * @param itripUserVO
     * @throws Exception
     */
    @Override
    public void registerByEmail(ItripUserVO itripUserVO) throws Exception {
        //数据验证
        checkRegisterData(itripUserVO);
        //验证邮箱
        if (!validEmail(itripUserVO.getUserCode())) {
            throw new GotripException("手机格式不正确",
                    ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //判断用户是否存在
        GotripUser user1 = this.findByUserCode(itripUserVO.getUserCode());
        if(!EmptyUtils.isEmpty(user1)){
            throw new GotripException("用户已存在",ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //构建用户信息
        GotripUser gotripUser1=new GotripUser();
        BeanUtils.copyProperties(itripUserVO,gotripUser1);
        gotripUser1.setActivated(0);
        //数据加密
        String mdgUserPassword=DigestUtil.hmacSign(gotripUser1.getUserPassword());
        gotripUser1.setUserPassword(mdgUserPassword);

        //数据入库
        Integer resultSize1= rpcGotripUserService.insertGotripUser(gotripUser1);
        if(resultSize1<=0){
            throw new GotripException("用户注册失败!请稍后再试",
                    ErrorCode.AUTH_UNKNOWN);
        }

        //发送邮箱验证码
        //构建四位验证码
        int code= DigestUtil.randomCode();
        rpcSendMailService.sendMail(gotripUser1.getUserCode(),"邮箱验证","本次验证的验证码为："+code);
        //将验证码保存到redis中
        String key= Constants.RedisKeyPrefix.ACTIVATION_MOBILE_PREFIX+gotripUser1.getUserCode();
        redisUtils.set(key, 60*3,""+code);
    }




    @Override
    public void validatePhone(String user, String code) throws Exception {
        //验证手机号格式是否正确
        if(!validPhone(user)){
            throw  new  GotripException("请输入正确的手机号",
            ErrorCode.AUTH_PARAMETER_ERROR);
        }

        //验证用户是否存在
        GotripUser gotripUser1 = this.findByUserCode(user);
        if(EmptyUtils.isEmpty(gotripUser1)){
            throw new GotripException("账号不存在",
                    ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //获取redis中存在的短信验证码
        String key = Constants.RedisKeyPrefix.ACTIVATION_MOBILE_PREFIX+user;
        String cacheCode = (String)redisUtils.get(key);
        if (EmptyUtils.isEmpty(cacheCode)||!cacheCode.equals(code)) {
            throw new GotripException("验证码已失效",
                    ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //激活用户
        gotripUser1.setActivated(1);
        gotripUser1.setUserType(0);
        gotripUser1.setFlatID(gotripUser1.getId());
        rpcGotripUserService.updateGotripUser(gotripUser1);
        LOG.info("---> 用户激活成功 <---",user);

    }

    @Override
    public void validateEmail(String user, String code) throws Exception {
        //验证邮箱号格式是否正确
        if(!validEmail(user)){
            throw  new  GotripException("请输入正确的邮箱",
                    ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //验证用户是否存在
        GotripUser gotripUser = this.findByUserCode(user);
        if(EmptyUtils.isEmpty(gotripUser)){
            throw new GotripException("账号不存在",
                    ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //获取redis中存在的短信验证码
        String key = Constants.RedisKeyPrefix.ACTIVATION_MOBILE_PREFIX+user;
        String cacheCode = (String)redisUtils.get(key);
        if (EmptyUtils.isEmpty(cacheCode)||!cacheCode.equals(code)) {
            throw new GotripException("验证码已失效",
                    ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //激活用户
        gotripUser.setActivated(1);
        gotripUser.setUserType(0);
        gotripUser.setFlatID(gotripUser.getId());
        rpcGotripUserService.updateGotripUser(gotripUser);
        LOG.info("---> 用户激活成功 <---",user);

    }


    /**
     *  校验注册数据
     * @param itripUserVO   注册用户信息
     * @throws GotripException
     */
    private void checkRegisterData(ItripUserVO itripUserVO) throws GotripException {
        if(EmptyUtils.isEmpty(itripUserVO)){
            throw new GotripException("请传递参数", ErrorCode.AUTH_PARAMETER_ERROR);
        }
        if (EmptyUtils.isEmpty(itripUserVO.getUserCode())) {
            throw new GotripException("用户Code不能为空",ErrorCode.AUTH_PARAMETER_ERROR);
        }
        if (EmptyUtils.isEmpty(itripUserVO.getUserName())) {
            throw new GotripException("用户账号不能为空",ErrorCode.AUTH_PARAMETER_ERROR);
        }
        if (EmptyUtils.isEmpty(itripUserVO.getUserPassword())) {
            throw new GotripException("用户密码不能为空",ErrorCode.AUTH_PARAMETER_ERROR);
        }
    }

    /**			 *
     * 合法E-mail地址：
     * 1. 必须包含一个并且只有一个符号“@”
     * 2. 第一个字符不得是“@”或者“.”
     * 3. 不允许出现“@.”或者.@
     * 4. 结尾不得是字符“@”或者“.”
     * 5. 允许“@”前的字符中出现“＋”
     * 6. 不允许“＋”在最前面，或者“＋@”
     */
    private boolean validEmail(String email){

        String regex="^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"  ;
        return Pattern.compile(regex).matcher(email).find();
    }
    /**
     * 验证是否合法的手机号
     * @param phone
     * @return
     */
    private boolean validPhone(String phone) {
        String regex="^1[356789]{1}\\d{9}$";
        return Pattern.compile(regex).matcher(phone).find();
    }

}
