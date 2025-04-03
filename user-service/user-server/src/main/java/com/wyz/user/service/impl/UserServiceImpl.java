package com.wyz.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.common.exception.BusinessException;
import com.wyz.common.exception.MapperException;
import com.wyz.common.result.RestResult;
import com.wyz.common.tool.utils.RandomUtils;
import com.wyz.resource.client.MessageClient;
import com.wyz.resource.client.ResourceClient;
import com.wyz.resource.client.pojo.MailDTO;
import com.wyz.user.mapper.*;
import com.wyz.user.pojo.*;
import com.wyz.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.wyz.user.utils.RedisConstants.*;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserViewMapper, UserView> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserGeneralMapper userGeneralMapper;

    @Resource
    private UserSafetyMapper userSafetyMapper;

    @Resource
    private UserViewMapper userViewMapper;

    @Resource
    private UserBasicMapper userBasicMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private MessageClient messageClient;

    @Resource
    private ResourceClient resourceClient;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    /**
     * 根据id获取用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    @Override
    public UserView getById(Integer id) {
        // 1. 构造redis key
        String key = USER_SERVICE_INFO_KEY + id;
        // 2. 尝试从redis获取
        UserView user = (UserView) objectRedisTemplate.opsForValue().get(key);
        if (user != null) {
            System.out.println("从redis获取用户信息");
            System.out.println(user);
            // 3. 存在，直接返回
            return user;
        }
        // 4. 不存在，查数据库
        user = userViewMapper.getById(id);
        System.out.println(user);
        if (user != null) {
            // 4.1 查询成功，存入redis
            objectRedisTemplate.opsForValue().set(key, user, USER_SERVICE_INFO_TTL, TimeUnit.SECONDS);
        }
        // 5. 返回信息
        return user;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public User getByUsername(String username) {
        return lambdaQuery().eq(UserView::getUsername, username).one();
    }

    /**
     * 批量获取用户信息，并封装成key为userId,value为user的map
     *
     * @param userIdList 用户id列表
     * @return 用户信息map，key为用户id，value为用户信息
     */
    @Override
    public Map<Integer, UserView> getUserListMap(Set<Integer> userIdList) {
        if (userIdList == null || userIdList.size() == 0) {
            return null;
        }
        List<UserView> userViewList = query().getBaseMapper().selectBatchIds(userIdList);
        HashMap<Integer, UserView> userMap = new HashMap<>(userIdList.size());
        for (UserView user : userViewList) {
            userMap.put(user.getId(), user);
        }
        return userMap;
    }

    /**
     * 获取用户创作信息
     *
     * @param userId 用户id
     * @return 批量用户统计数据
     */
    @Override
    public UserGeneral getUserGeneral(Integer userId) {
        if (userId == null) {
            return null;
        }
        LambdaQueryWrapper<UserGeneral> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGeneral::getUserId, userId);
        return userGeneralMapper.selectOne(wrapper);
    }

    /**
     * 批量查询用户各项数据统计
     *
     * @param userIdList 用户id列表
     * @return 批量用户统计数据
     */
    @Override
    public Map<Integer, UserGeneral> getUserGeneralListMap(List<Integer> userIdList) {
        if (userIdList == null || userIdList.size() == 0) {
            return null;
        }
        LambdaQueryWrapper<UserGeneral> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserGeneral::getUserId, userIdList);
        List<UserGeneral> userGenerals = userGeneralMapper.selectList(wrapper);
        HashMap<Integer, UserGeneral> userGeneralMap = new HashMap<>(userIdList.size());
        for (UserGeneral userGeneral : userGenerals) {
            userGeneralMap.put(userGeneral.getUserId(), userGeneral);
        }
        return userGeneralMap;
    }

    /**
     * 移除用户
     *
     * @param id 用户id
     * @return 是否删除成功
     */
    @Override
    public boolean removeById(Integer id) {
        // 由于user表当中username字段为唯一索引，且该项目使用了逻辑删除
        // 所以我重写了userMapper的deleteById方法，将deleted字段值修改为id
        // user_safety、user_basic也是一样
        if (userSafetyMapper.deleteById(id) == 1 && userMapper.deleteById(id) == 1 && userBasicMapper.deleteById(id) == 1) {
            return true;
        }
        throw new MapperException("删除失败", "remove by id error,id->" + id);
    }

    /**
     * 检查密码是否正确
     *
     * @param id       用户id
     * @param password 密码
     * @return 是否验证成功
     */
    @Override
    public boolean checkPassword(Integer id, String password) {
        UserSafety userSafety = userSafetyMapper.selectById(id);
        return passwordEncoder.matches(password, userSafety.getPassword());
    }

    /**
     * 修改密码
     *
     * @param id       用户id
     * @param password 新密码
     * @return 是否修改成功
     */
    @Override
    public boolean updatePasswordById(Integer id, String password) {
        String encode = passwordEncoder.encode(password);
        return userSafetyMapper.updatePasswordById(id, encode) > 0;
    }

    /**
     * 更新用户头像
     *
     * @param id         用户id
     * @param avatarFile 头像文件
     * @return 资源链接
     */
    @Override
    public String updateAvatar(Integer id, MultipartFile avatarFile) {
        log.debug("updateAvatar,id->{}, fileName->{}", id, avatarFile.getOriginalFilename());
        // 获取用户信息
        User user = userMapper.selectById(id);
        // 提取文件的拓展名
        String originalFilename = avatarFile.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 每次更新数据库, 生成新的文件名, 使用 userid+username+文件拓展名的格式来命名文件
        user.setAvatarUrl(user.getId() + "_" + user.getUsername() + fileExtension);
        objectRedisTemplate.delete(USER_SERVICE_INFO_KEY + id);
        userMapper.updateById(user);
        RestResult<String> result = resourceClient.uploadAvatarImage(avatarFile, user.getAvatarUrl());
        return result.getStatus() ? result.getData() : null;
    }

    /**
     * 更新用户昵称
     *
     * @param id       用户id
     * @param nickname 用户昵称
     * @return 是否更新成功
     */
    @Override
    public boolean updateNickname(Integer id, String nickname) {
        User user = new User();
        user.setId(id);
        user.setNickname(nickname);
        objectRedisTemplate.delete(USER_SERVICE_INFO_KEY + id);
        return userMapper.updateById(user) == 1;
    }

    /**
     * 更新用户简介
     *
     * @param id    用户id
     * @param intro 用户简介
     * @return 是否更新成功
     */
    @Override
    public boolean updateIntro(Integer id, String intro) {
        UserBasic userBasic = new UserBasic();
        userBasic.setUserId(id);
        userBasic.setIntro(intro);
        objectRedisTemplate.delete(USER_SERVICE_INFO_KEY + id);
        return userBasicMapper.updateById(userBasic) == 1;
    }

    /**
     * 更新用户的院校代码
     * 只更新 user 表的 school_code 字段即可, 对于 blog、blink中的 school_code 字段不作更新(显然这是符合逻辑的)
     *
     * @param id         用户id
     * @param schoolCode 院校代码
     * @return 是否更新成功
     */
    @Override
    public boolean updateSchoolCode(Integer id, Integer schoolCode) {
        User user = new User();
        user.setId(id);
        user.setSchoolCode(schoolCode);
        objectRedisTemplate.delete(USER_SERVICE_INFO_KEY + id);
        return userMapper.updateById(user) == 1;
    }

    /**
     * 更新用户邮箱
     *
     * @param id   用户id
     * @param mail 新邮箱
     * @return 是否更新成功
     */
    @Override
    public boolean updateMail(Integer id, String mail) {
        objectRedisTemplate.delete(USER_SERVICE_INFO_KEY + id);
        return userSafetyMapper.updateMailById(id, mail) > 0;
    }

    /**
     * 发送邮箱验证码
     *
     * @param id   用户id
     * @param mail 新邮箱
     * @return 是否发送成功
     */
    @Override
    public boolean sendMailVerify(Integer id, String mail) {
        // 0. 构造redis key
        String key = USER_SERVICE_MAIL_CODE_KEY + id;
        // 1. 查询当前用户的最近发送记录，通过ttl判断
        Long expire = redisTemplate.getExpire(key);
        // 允许的发送时间间隔
        int sendCodeInterval = 60;
        if (expire != null && USER_SERVICE_MAIL_CODE_TTL - expire < sendCodeInterval) {
            // 1.1 若时间不为空且未超过固定的时间间隔，则不允许发送
            throw new BusinessException("发送频繁");
        }
        // 2 若为空或已经超过固定的时间间隔，则允许发送。生成验证码，并构造发送邮件类型
        String code = RandomUtils.generator(6);
        MailDTO mailDTO = new MailDTO();
        mailDTO.setFrom("校园博客");
        mailDTO.setTo(mail);
        mailDTO.setSubject("校园博客验证码");
        mailDTO.setText("亲爱的用户：\n" + "你正在操作你的账户信息，你的邮箱验证码为：" + code + "，此验证码有效时长5分钟，请勿转发他人。");
        // 3. 将验证码保存到redis
        redisTemplate.opsForValue().set(key, code, USER_SERVICE_MAIL_CODE_TTL, TimeUnit.SECONDS);
        // 4. 发送邮件
        RestResult<Object> result = messageClient.sendMail(mailDTO);
        return result.getStatus();
    }

    /**
     * 检查验证码
     *
     * @param id     用户id
     * @param verify 验证码
     * @return 是否验证成功
     */
    @Override
    public boolean checkMailVerify(Integer id, @Valid @NotNull String verify) {
        String code = redisTemplate.opsForValue().get(USER_SERVICE_MAIL_CODE_KEY + id);
        return verify.equals(code);
    }

}
