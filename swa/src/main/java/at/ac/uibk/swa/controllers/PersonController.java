package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.models.annotations.Admin;
import at.ac.uibk.swa.models.restResponses.CreatedUserResponse;
import at.ac.uibk.swa.models.restResponses.ListResponse;
import at.ac.uibk.swa.models.restResponses.MessageResponse;
import at.ac.uibk.swa.models.restResponses.RestResponse;
import at.ac.uibk.swa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static at.ac.uibk.swa.util.EndpointMatcherUtil.registerEndpoint;

/**
 * Controller handling {@link Person} related Information (e.g. creating, updating, deleting {@link Person})
 *
 * @author David Rieser
 */
@SuppressWarnings("unused")
@RestController
public class PersonController {

    //region Autowired Components
    @Autowired
    private PersonService personService;
    //endregion

    //region User Creation Endpoints
    /**
     * User Registration Endpoint for Users to create an Account by themselves.
     *
     * @param username The new Users username.
     * @param password The new Users password (needs to be already hashed).
     * @param email The new Users email.
     * @return A RestResponse indicating whether the user could be created or not.
     */
    @PostMapping(registerEndpoint)
    public RestResponse register(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password,
            @RequestParam("email") final String email
    ) {
        UUID token = UUID.randomUUID();
        Person person = new Person(username, email, password, token, Permission.defaultPermissions());

        return createUser(person);
    }

    /**
     * User Creation Endpoint for Admins to manually create Accounts.
     *
     * @param username The new Users username.
     * @param password The new Users password (needs to be already hashed).
     * @param email The new Users email.
     * @param permissions The Permissions the new User should have.
     * @return A RestResponse indicating whether the user could be created or not.
     */
    @Admin
    @PostMapping("/api/createUser")
    public RestResponse create(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password,
            @RequestParam("email") final String email,
            @RequestParam("permissions") final Set<Permission> permissions
    ) {
        Person person = new Person(username, email, password, permissions);

        return createUser(person);
    }

    /**
     * Helper Function for saving a Person and returning a corresponding RestResponse.
     *
     * @param person The Person to save.
     * @return A RestResponse indicating whether the operation was successful or not.
     */
    private RestResponse createUser(Person person) {
        if (!personService.create(person))
            return new MessageResponse(false, "Could not create User - Username already exists!");

        return new CreatedUserResponse(person);
    }
    //endregion

    //region Update User Endpoints
    /**
     * Endpoint for Admins to change/update a user
     *
     * @param personId The ID of the User to update
     * @param username The new username
     * @param email The new email
     * @param password The new Password
     * @param permissions The new Permissions
     * @return A RESTResponse indicating Success
     */
    @Admin
    @PostMapping("/api/updateUser")
    public RestResponse updateUser(
            @RequestParam("personId") final UUID personId,
            @RequestParam(name = "username", required = false) final String username,
            @RequestParam(name = "email", required = false) final String email,
            @RequestParam(name = "permissions", required = false) final Set<Permission> permissions,
            @RequestParam(name = "password", required = false) final String password
    ) {
        if (personService.update(personId, username, permissions, password))
            return new MessageResponse(true, "User updated successfully!");

        return new MessageResponse(false, "Could not update User - User does not exist!");
    }
    //endregion

    //region Delete User Endpoints
    /**
     * Endpoint for Admins to delete a user.
     *
     * @param personId The ID of the User to delete
     * @return A RestResponse indicating whether the operation was successful or not.
     */
    @Admin
    @PostMapping("/api/deleteUser")
    public RestResponse deleteUser(
            @RequestParam("personId") final UUID personId
    ) {
        if (!personService.delete(personId))
            return new MessageResponse(false, "Could not delete User - User does not exist!");

        return new MessageResponse(true, "User deleted successfully!");
    }
    //endregion

    //region GET Endpoints
    /**
     * Endpoint for Admins to get all users.
     *
     * @return A RestReponse containing a List of all users.
     */
    @Admin
    @GetMapping("/api/getAllUsers")
    public RestResponse getAllUsers() {
        return new ListResponse<>(personService.getPersons());
    }

    /**
     * Endpoint for Admins to get all possible Permission so that they don't need to be changed manually on frontend.
     *
     * @return A List of all possible Permissions.
     */
    @Admin
    @GetMapping("/api/getAllPermissions")
    public RestResponse getAllPermissions() {
        return new ListResponse<>(Stream.of(Permission.values()).map(Enum::name).toList());
    }
    //endregion
}
