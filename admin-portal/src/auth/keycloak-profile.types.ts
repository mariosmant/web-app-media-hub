import type { IdTokenClaims } from "oidc-client-ts";

interface KeycloakRealmAccess {
  roles: string[];
}

export interface KeycloakProfile extends IdTokenClaims {
  realm_access?: KeycloakRealmAccess;
}
