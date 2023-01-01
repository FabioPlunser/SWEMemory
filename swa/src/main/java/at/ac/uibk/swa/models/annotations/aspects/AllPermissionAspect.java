package at.ac.uibk.swa.models.annotations.aspects;

import at.ac.uibk.swa.config.person_authentication.AuthContext;
import at.ac.uibk.swa.models.Authenticable;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.annotations.AllPermission;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class AllPermissionAspect {
    @Around("@annotation(at.ac.uibk.swa.models.annotations.AllPermission)")
    public Object doSomething(ProceedingJoinPoint jp) throws Throwable {
        // Get the Permissions that are all needed from the Attribute
        Set<Permission> requiredPermission = Arrays.stream(
                ((MethodSignature) jp.getSignature())
                    .getMethod()
                    .getAnnotation(AllPermission.class)
                        .value()
                ).collect(Collectors.toSet());

        // Try to get the currently logged-in user
        Optional<Set<GrantedAuthority>> maybeUserPermissions = AuthContext.getCurrentUser().map(Authenticable::getPermissions);

        // If no user is logged in => No Permissions => Fail
        if (maybeUserPermissions.isEmpty())
            throw new AccessDeniedException("");

        // Get the logged-in user's Permissions and check if they meet the requirements
        Set<GrantedAuthority> userPermissions = maybeUserPermissions.get();
        for (Permission permission : requiredPermission) {
            // Fail if any Permission is missing
            if (!userPermissions.contains(permission)) {
                throw new AccessDeniedException("");
            }
        }

        // Proceed if all Permissions were met
        return jp.proceed();
    }
}
