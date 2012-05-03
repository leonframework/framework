/*
 * Created by IntelliJ IDEA.
 * User: roman
 * Date: 4/30/12
 * Time: 11:12 AM
 */
package io.leon.web.comet.annotations;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@BindingAnnotation
public @interface CometThreadPoolExecutor {
}
