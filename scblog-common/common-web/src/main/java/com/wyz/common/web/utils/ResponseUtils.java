package com.wyz.common.web.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Web 应用程序的 API 响应处理, 将 Java 对象转换为 JSON 格式的响应
 */
public class ResponseUtils {

	static ObjectWriter writer;

	static {
		ObjectMapper mapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		writer = mapper.writer(dateFormat);
	}

	public static void objectToJson(HttpServletResponse response, Object object) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		writer.writeValue(response.getWriter(), object);
	}

}
