package com.wyz.resource.client;

import com.wyz.common.result.RestResult;
import com.wyz.resource.client.fuse.MessageClientFuse;
import com.wyz.resource.client.pojo.MailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 消息服务模块接口客户端
 */
@Import(MessageClientFuse.class)
@FeignClient(value = "resource-server", fallback = MessageClientFuse.class, contextId = "MessageClient")
public interface MessageClient {

	/**
	 * 发送邮件
	 *
	 * @param mail 邮件信息
	 * @return 发送情况
	 */
	@PostMapping(value = "/send/mail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	RestResult<Object> sendMail(MailDTO mail);

}
