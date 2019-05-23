package net.arver.miaosha.validator;

import net.arver.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机校验器.
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String > {

    private boolean required = false;

    @Override
    public void initialize(final IsMobile constraintAnnotation) {
       required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {
        if (!required && StringUtils.isBlank(s)) {
            return true;
        }
        return ValidatorUtil.isMobile(s);
    }
}
