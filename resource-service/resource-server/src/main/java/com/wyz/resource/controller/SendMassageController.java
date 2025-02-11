package com.wyz.resource.controller;

import com.wyz.common.result.RestResult;
import com.wyz.resource.pojo.Mail;
import com.wyz.resource.service.MailService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件信息发送控制器
 */
@Slf4j
@RestController
@RequestMapping("/send")
@Validated
public class SendMassageController {

	@Resource
	private MailService mailService;

	@PostMapping("/mail")
	public RestResult<Object> sendMail(@NotNull Mail mail) {
		try {
			mailService.sendMail(mail);
			return RestResult.ok(null, "发送成功");
		} catch (Exception e) {
			return RestResult.fail("发送失败");
		}
	}

}
