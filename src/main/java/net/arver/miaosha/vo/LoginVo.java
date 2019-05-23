package net.arver.miaosha.vo;

import net.arver.miaosha.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 登录vo.
 */
public class LoginVo {
    /**
     * 手机号.
     */
    @NotBlank
    @IsMobile
    private String mobile;

    /**
     * 密码.
     */
    @NotBlank
    @Length(min = 32)
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{"
                + "mobile='" + mobile + '\''
                + ", password='" + password + '\''
                + '}';
    }
}
