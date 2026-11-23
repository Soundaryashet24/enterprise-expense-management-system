
package com.soundarya.enterpriseexpensemanagementsystem.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component("expenseAccess")
public class ExpenseAccess {

    public boolean isManager(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MANAGER".equals(a.getAuthority()));
    }

    public boolean isSelf(Authentication authentication, Long employeeId) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)
                || employeeId == null) {
            return false;
        }

        Number linkedId = jwtAuth.getToken().getClaim("employeeId");

        return linkedId != null
                && linkedId.longValue() == employeeId.longValue();
    }

    public boolean isSelfOrManager(
            Authentication authentication, Long employeeId) {
        return isManager(authentication)
                || isSelf(authentication, employeeId);
    }
}
