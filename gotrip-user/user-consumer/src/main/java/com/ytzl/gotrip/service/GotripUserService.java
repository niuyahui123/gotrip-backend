package com.ytzl.gotrip.service;

import com.ytzl.gotrip.model.GotripUser;
import com.ytzl.gotrip.utils.exception.GotripException;
import com.ytzl.gotrip.vo.userinfo.ItripUserVO;

/**
 * @author sam
 */

public interface GotripUserService {

    /**
     * 根据登录账号查询用户信息
     *
     * @param userCode 登录账号
     * @return 用户信息（包含密码）
     */
    public GotripUser findByUserCode(String userCode) throws Exception;

    /**
     * 通过手机号注册
     * @param itripUserVO
     */
    public void registerByPhone(ItripUserVO itripUserVO) throws Exception;

    /**
     * 通过邮箱注册
     * @param itripUserVO
     * @throws Exception
     */
    public void registerByEmail(ItripUserVO itripUserVO) throws Exception;

    /**
     * 账号激活（手机）
     *
     * @param user  登入账号
     * @param code  验证码
     */
    void validatePhone(String user, String code) throws Exception;

    /**
     * 账号激活（邮箱）
     * @param user
     * @param code
     * @throws Exception
     */
    void validateEmail(String user, String code) throws Exception;
}

