package at.ac.uibk.swa.models.annotations;

import at.ac.uibk.swa.models.Permission;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnyPermission {
    Permission[] value();
}
