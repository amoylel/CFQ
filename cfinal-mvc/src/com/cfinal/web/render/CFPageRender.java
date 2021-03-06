/**
 * Created the com.cfinal.web.render.CFPageRender.java
 * @created 2016年9月28日 下午5:36:00
 * @version 1.0.0
 */
package com.cfinal.web.render;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.cfinal.web.CFRequest;
import com.cfinal.web.CFResponse;
import com.cfinal.web.control.CFActionPorxy;
import com.cfinal.web.model.CFModel;

/**
 * com.cfinal.web.render.CFPageRender.java
 * @author XChao
 */
public class CFPageRender implements CFRender {
//	获取域名与IP地址商品方法
//	String path = request.getContextPath();
//	String basePath = request.getScheme() + "://" + request.getServerName() + 
// ":" + request.getServerPort() + path + "/";

	@Override
	public void render(CFModel model, CFActionPorxy porxy, CFRequest request, CFResponse response)
		throws Exception {
		// 返回数据格式处理
		response.setContentType("text/html; charset=" + getContext().getEncoding());
		if(StringUtils.isNotBlank(model.getContentType())) {
			response.setContentType(model.getContentType());
		}

		// 页面模板处理
		String viewPath = porxy.getCurrentViewPath();
		if(StringUtils.isBlank(viewPath)) {
			throw new RuntimeException("********  Not found Page. ***********");
		}
		// 请示转发处理
		if(viewPath.trim().startsWith("f:")) {
			StringBuilder dispatcher = new StringBuilder();
			viewPath = viewPath.trim().substring(2).trim();
			if(!viewPath.startsWith("/")) {
				dispatcher.append("/");
			}
			dispatcher.append(viewPath);
			request.getRequestDispatcher(dispatcher.toString()).forward(request, response);
			return;
		}
		// 重定向处理
		if(viewPath.trim().startsWith("r:")) {
			viewPath = viewPath.trim().substring(2).trim();
			if(Pattern.compile("([a-zA-Z]{2,}://\\S+)+").matcher(viewPath).matches()) {
				response.sendRedirect(viewPath);
				return;
			}
			StringBuilder redirect = new StringBuilder();
			if(!viewPath.startsWith("/")) {
				redirect.append(request.getServletContext().getContextPath() + "/");
			}
			response.sendRedirect(redirect.append(viewPath).toString());
			return;
		}
		// 生成页面
		porxy.getViewPorxy().execute(model, viewPath, request, response);
	}

}
