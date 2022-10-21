package com.fxz.system.controller;

import com.fxz.common.log.annotation.OperLogAnn;
import com.fxz.common.log.enums.BusinessType;
import com.fxz.common.mp.result.PageResult;
import com.fxz.common.mp.result.Result;
import com.fxz.system.feign.RemoteTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 令牌管理
 *
 * @author Fxz
 * @version 1.0
 * @date 2022-04-14 21:32
 */
@Tag(name = "令牌管理")
@RequiredArgsConstructor
@RestController
public class TokenController {

	private final RemoteTokenService remoteTokenService;

	/**
	 * 删除token
	 */
	@Operation(summary = "删除token")
	@OperLogAnn(title = "强退", businessType = BusinessType.FORCE)
	@SneakyThrows
	@DeleteMapping("/token/{token}")
	public Result<Void> removeToken(@PathVariable("token") String token) {
		return remoteTokenService.removeToken(token);
	}

	/**
	 * 分页查询token
	 * @param params 分页参数
	 * @return
	 */
	@Operation(summary = "分页查询token")
	@GetMapping("/token/page")
	public Result<PageResult> tokenList(@RequestParam Map<String, Object> params) {
		return remoteTokenService.tokenList(params);
	}

}
