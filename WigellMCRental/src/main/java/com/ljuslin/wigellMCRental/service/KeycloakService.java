package com.ljuslin.wigellMCRental.service;

import com.ljuslin.wigellMCRental.dto.CustomerCreateDto;
import com.ljuslin.wigellMCRental.dto.CustomerUpdateDto;
import com.ljuslin.wigellMCRental.exception.DataConflictException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakService {
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public String createKeycloakUser(CustomerCreateDto dto) {
        // Här bygger vi anropet till Keycloak
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEnabled(true);
        user.setRealmRoles(List.of("USER"));

        // Sätt lösenordet
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(dto.password());
        user.setCredentials(List.of(cred));

        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() == 409) {
            throw new DataConflictException("Användarnamn eller e-post finns redan i Keycloak");
        }

        if (response.getStatus() != 201) {
            throw new RuntimeException("Kunde inte skapa användare: " + response.getStatusInfo());
        }

        return CreatedResponseUtil.getCreatedId(response);
    }


    public void deleteKeycloakUser(String keycloakId) {
        try {
            // Vi bygger anropet på samma sätt som vid create
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm) // Din wigell-realm
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();

            keycloak.realm(realm).users().get(keycloakId).remove();

        } catch (Exception e) {
            throw new RuntimeException("Kunde inte radera användare i Keycloak: " + e.getMessage());
        }
    }

    public void updateKeycloakUser(String keycloakId, CustomerUpdateDto dto) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();

            UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
            UserRepresentation user = userResource.toRepresentation();

            user.setFirstName(dto.firstName());
            user.setLastName(dto.lastName());
            user.setEmail(dto.email());

            userResource.update(user);

        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 409) {
                throw new DataConflictException("E-posten är redan upptagen i Keycloak.");
            }
            throw new RuntimeException("Keycloak-uppdatering misslyckades: " + e.getMessage());
        }
    }
}