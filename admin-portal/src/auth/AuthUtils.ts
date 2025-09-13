import type { IdTokenClaims } from "oidc-client-ts";
import type { KeycloakProfile } from "./keycloak-profile.types";

export interface CustomAuthState {
  returnTo?: string;
}


/**
 * Extracts roles from a Keycloak-authenticated OIDC user profile.
 * Falls back to a generic `roles` claim if `realm_access` is not present.
 */
export function getUserRoles(profile: IdTokenClaims | undefined): string[] {
  if (!profile) {
    return [];
  }

  let keyCloakProfile = (profile as KeycloakProfile);

  // Try Keycloak's realm_access.roles first
  const realmRoles = keyCloakProfile?.realm_access?.roles;
  if (Array.isArray(realmRoles)) {
    return realmRoles;
  }

  return [];
}
