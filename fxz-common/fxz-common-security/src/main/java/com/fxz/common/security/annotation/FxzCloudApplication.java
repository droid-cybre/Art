package com.fxz.common.security.annotation;

import com.fxz.common.security.selector.FxzCloudApplicationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Fxz
 * @version 1.0
 * @date 2021-11-28 13:36
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FxzCloudApplicationSelector.class)
public @interface FxzCloudApplication {

}