package com.wyz.user.controller;

import com.wyz.common.result.RestResult;
import com.wyz.common.web.anno.RequestLimit;
import com.wyz.common.web.auth.AuthHelper;
import com.wyz.resource.type.FileType;
import com.wyz.resource.utils.FileUtils;
import com.wyz.user.pojo.UserGeneral;
import com.wyz.user.pojo.UserView;
import com.wyz.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 用户信息相关接口
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取公开信息
     *
     * @param id 用户名id
     */
    @GetMapping
    @RequestLimit
    public UserView getByUserId(Integer id) {
        Integer getId = null;
        Integer userId = AuthHelper.getCurrentUserId();
        if (id == null && userId != null) {
            getId = userId;
        } else if (id != null) {
            getId = id;
        }
        return userService.getById(getId);
    }

    /**
     * 批量获取用户信息
     */
    @PostMapping("/list")
    public Map<Integer, UserView> getUserList(@RequestParam List<Integer> userIdList) {
        return userService.getUserListMap(new HashSet<>(userIdList));
    }

    /**
     * 查询用户创作数据
     *
     * @param userId 用户id
     * @return 批量用户统计数据
     */
    @GetMapping("/general")
    public UserGeneral getUserGeneral(@RequestParam Integer userId) {
        return userService.getUserGeneral(userId);
    }

    /**
     * 批量查询用户各项数据统计
     *
     * @param userIdList 用户id列表
     * @return 批量用户统计数据
     */
    @GetMapping("/general/map")
    public Map<Integer, UserGeneral> getUserGeneralList(@RequestParam List<Integer> userIdList) {
        return userService.getUserGeneralListMap(userIdList);
    }

    /**
     * 修改昵称
     *
     * @param nickname 昵称
     */
    @PutMapping("/nickname")
    public RestResult<Object> updateNickname(@NotNull String nickname) {
        Integer userId = AuthHelper.getCurrentUserIdOrExit();
        if (userService.updateNickname(userId, nickname)) {
            return RestResult.ok();
        }
        return RestResult.fail();
    }

    /**
     * 修改简介
     *
     * @param intro 简介
     */
    @PutMapping("/intro")
    public RestResult<Object> updateIntro(@NotNull String intro) {
        Integer userId = AuthHelper.getCurrentUserIdOrExit();
        if (userService.updateIntro(userId, intro)) {
            return RestResult.ok();
        }
        return RestResult.fail();
    }

    /**
     * 修改头像
     *
     * @param avatarFile 文件流
     */
    @PutMapping("/avatar")
    public RestResult<String> updateAvatar(@NotNull MultipartFile avatarFile) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        log.debug("updateAvatar,fileSize->{}", avatarFile.getSize());
        // 检查文件，小于1Mib ,仅支持JPEG和PNG
        FileUtils.checkFile(avatarFile, 1024 * 1024L, FileType.JPEG, FileType.PNG);
        String avatar = userService.updateAvatar(id, avatarFile);
        if (avatar != null) {
            return RestResult.ok(avatar);
        } else {
            return RestResult.fail("上传失败");
        }
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @PutMapping("/password")
    public RestResult<Object> updatePassword(@NotNull String oldPassword, @NotNull String newPassword) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        if (userService.checkPassword(id, oldPassword)) {
            boolean result = userService.updatePasswordById(id, newPassword);
            return RestResult.ok(result);
        }
        return RestResult.fail("修改失败");
    }

    /**
     * 更新用户院校代码 TODO: 院校代码更新有，选择就是，但是选完后后端更新而前端显示不更新，同时只更新了user表
     *
     * @param code 院校代码
     */
    @PutMapping("/school/code")
    public Boolean updateSchoolCode(@NotNull Integer code) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        return userService.updateSchoolCode(id, code);
    }

    /**
     * 修改邮箱
     *
     * @param mail       新邮箱
     * @param mailVerify 邮箱验证码
     */
    @PutMapping("/mail")
    public RestResult<Object> updateMail(@NotNull String mail, @NotNull String mailVerify) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        if (userService.checkMailVerify(id, mailVerify)) {
            boolean result = userService.updateMail(id, mail);
            return RestResult.ok(result);
        }
        return RestResult.fail("修改失败");
    }

    /**
     * 发送邮箱验证码
     *
     * @param mail 新邮箱
     */
    @PostMapping("/mail/send-mail-verify")
    public Boolean sendMailVerifyForUpdateMail(@NotNull String mail) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        return userService.sendMailVerify(id, mail);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/user")
    public RestResult<Object> delete(@NotNull String password) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        if (!userService.checkPassword(id, password)) {
            return new RestResult<>(false, "密码错误");
        } else {
            return new RestResult<>(userService.removeById(id));
        }
    }

}
