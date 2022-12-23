

package io.agilefastgateway.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 登录表单
 *
 * @author
 * @since 3.1.0 2018-01-25
 */
@ApiModel(value = "登录表单")
@Getter
@Setter
public class FreeLoginForm {
    @ApiModelProperty(value = "授权码")
    @NotBlank(message="授权码不能为空")
    private String code;
}
