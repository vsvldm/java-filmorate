package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface ValidDate {
    String message() default "Дата не может быть раньше {value}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}
