/*
 * COPYRIGHT (C) 2022 Art AUTHORS(fxzcloud@gmail.com). ALL RIGHTS RESERVED.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.art.system.controller;

import cn.hutool.extra.servlet.ServletUtil;
import com.art.common.Idempotent.annotation.Idempotent;
import com.art.common.Idempotent.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.art.common.core.exception.ErrorCodes;
import com.art.common.core.util.MsgUtils;
import com.art.common.mp.result.Result;
import com.art.common.mq.redis.core.RedisMQTemplate;
import com.art.common.redis.cache.support.CacheMessage;
import com.art.common.security.annotation.Ojbk;
import com.art.common.security.entity.FxzAuthUser;
import com.art.common.security.util.SecurityUtil;
import com.art.common.sequence.service.Sequence;
import com.art.common.websocket.service.UserWsNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Fxz
 * @version 0.0.1
 * @date 2022-02-27 18:33
 */
@Tag(name = "测试")
@Slf4j
@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {

	private final Sequence fxzSequence;

	private final Sequence cloudSequence;

	private final UserWsNoticeService userWsNoticeService;

	private final RedisMQTemplate redisMQTemplate;

	@Operation(summary = "清除缓存")
	@Ojbk
	@CacheEvict(value = "demo", key = "#id")
	@GetMapping("/cache/evict")
	public Result<String> CacheEvict(Long id) {
		redisMQTemplate.send(new CacheMessage());
		return Result.success(id.toString());
	}

	@Ojbk
	@Cacheable(value = "demo", key = "#id")
	@GetMapping("/cache/demo")
	public Result<String> get(Long id) {
		return Result.success(id.toString());
	}

	@Ojbk
	@GetMapping("/websocket")
	public Result<Void> websocket() {
		userWsNoticeService.sendMessageByAll("全体起立！");
		return Result.success();
	}

	@SneakyThrows
	@Ojbk
	@GetMapping("/seqTestZdy")
	public Result<String> seqTestZdy() {
		return Result.success(fxzSequence.nextValue("fxz") + ":" + cloudSequence.nextValue("cloud"));
	}

	@GetMapping("/security/inheritable")
	public Result<FxzAuthUser> securityInheritable() {
		AtomicReference<FxzAuthUser> user = new AtomicReference<>();

		user.set(SecurityUtil.getUser());
		log.info("user:{},Thread:{}", user, Thread.currentThread().getId());

		CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
			user.set(SecurityUtil.getUser());
			log.info("user:{},Thread:{}", user, Thread.currentThread().getId());
		});

		voidCompletableFuture.join();

		return Result.success(user.get());
	}

	@Ojbk
	@GetMapping("/messageTest")
	public Result<String> messageTest() {
		return Result.failed(MsgUtils.getMessage(ErrorCodes.SYS_TEST_MESSAGE_STR, "参数1", "参数2"));
	}

	@Ojbk
	@GetMapping("/ipTest")
	public Result<Object> getDeptTree(HttpServletRequest request) {
		String ip = ServletUtil.getClientIP(request);
		log.info("ip:{}", ip);
		return Result.success(ip);
	}

	@Idempotent(timeout = 10, message = "别发请求，等我执行完", keyResolver = ExpressionIdempotentKeyResolver.class, key = "#str")
	@Ojbk
	@GetMapping("/idempotent")
	public Result<Void> testIdempotent(String str) {
		log.info("方法执行");
		return Result.success();
	}

	@Idempotent(timeout = 10, message = "别发请求，等我执行完", keyResolver = ExpressionIdempotentKeyResolver.class, key = "#str",
			delKey = true)
	@Ojbk
	@GetMapping("/idempotentDel")
	public Result<Void> idempotentDel(String str) {
		log.info("方法执行且执行完自动删除key");
		return Result.success();
	}

	@GetMapping("/authTest")
	public Result<Void> authTest() {
		log.info("authTest.....");
		return Result.success();
	}

}