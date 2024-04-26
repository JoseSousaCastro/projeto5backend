package project5.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import project5.bean.*;
import project5.dao.UserDao;
import project5.dto.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import project5.entity.UserEntity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.*;

@Path("/users")
public class UserService implements Serializable {

    @Inject
    UserBean userBean;
    @Inject
    TaskBean taskBean;
    @Inject
    CategoryBean categoryBean;
    @Inject
    EmailBean emailBean;
    @Inject
    StatsBean statsBean;
    @Inject
    UserDao userDao;
    @Inject
    ChatBean chatBean;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(UserService.class);


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Login login, @Context HttpServletRequest request) {

        LoggedUser loggedUser = userBean.login(login);
        Response response;

        if (loggedUser != null) {
            response = Response.status(200).entity(loggedUser).build();
            logger.info("User '{}' logged in. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    loggedUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
            logger.info("Failed login attempt. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        }
        return response;
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam("token") String token, @Context HttpServletRequest request) {

        logger.info("User '{}' logged out. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userBean.convertEntityByToken(token).getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.logout(token)) return Response.status(200).entity("Logout Successful!").build();
        logger.info("Failed logout attempt. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(401).entity("Invalid Token!").build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(User user, @Context HttpServletRequest request) {
        Response response;
        logger.info("User '{}' is trying to register. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isUsernameAvailable = userBean.isUsernameAvailable(user);
        logger.info("Username '{}' is available: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), isUsernameAvailable, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isEmailValid = userBean.isEmailValid(user);
        logger.info("Email '{}' is valid: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getEmail(), isEmailValid, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isFieldEmpty = userBean.isAnyFieldEmpty(user);
        logger.info("All registration form fields have been filled in: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                isFieldEmpty, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isPhoneNumberValid = userBean.isPhoneNumberValid(user);
        logger.info("Phone number '{}' is valid: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getPhone(), isPhoneNumberValid, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean isImageValid = userBean.isImageUrlValid(user.getPhotoURL());
        logger.info("Image URL '{}' is valid: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getPhotoURL(), isImageValid, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        long expirationTime = System.currentTimeMillis() + 48 * 60 * 60 * 1000; // 48 horas em milissegundos
        logger.info("Expiration time for defining the password started at: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                expirationTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        user.setExpirationTime(expirationTime);
        user.setConfirmed(false);
        logger.info("User '{}' is not confirmed yet. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        user.setCreationDate(LocalDate.of(2021, 1, 1));
        logger.info("User '{}' was created at: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), user.getCreationDate(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean newPassword = emailBean.sendConfirmationEmail(user);
        logger.info("Email was sent to '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());

        if (isFieldEmpty) {
            logger.info("User registration failed. There's an empty field. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(422).entity("There's an empty field. ALl fields must be filled in").build();
        } else if (!isEmailValid) {
            logger.info("User registration failed. Email '{}' is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(422).entity("Invalid email").build();
        } else if (!isUsernameAvailable) {
            logger.info("User registration failed. Username '{}' is already in use. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.CONFLICT).entity("Username already in use").build(); //status code 409
        } else if (!isImageValid) {
            logger.info("User registration failed. Image URL '{}' is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getPhotoURL(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(422).entity("Image URL invalid").build(); //400
        } else if (!isPhoneNumberValid) {
            logger.info("User registration failed. Phone number '{}' is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getPhone(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(422).entity("Invalid phone number").build();
        } else if (!newPassword) {
            logger.info("User registration failed. Email was not sent. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(404).entity("Email not sent").build();
        } else if (userBean.register(user)) {
            logger.info("User '{}' registered successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.CREATED).entity("User registered successfully").build(); //status code 201
        } else {
            logger.info("User registration failed. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong").build(); //status code 400
        }
        return response;
    }

    @GET
    @Path("/getFirstName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFirstName(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        logger.info("User is trying to get his first name. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User currentUser = userBean.convertEntityByToken(token);
        logger.info("User '{}' got his first name successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                currentUser.getFirstName(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his first name. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his first name successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    currentUser.getFirstName(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(currentUser.getFirstName()).build();
        }
        return response;
    }

    //Retorna o url da foto do token enviado
    @GET
    @Path("/getPhotoUrl")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImage(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        logger.info("User is trying to get his photo URL. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User currentUser = userBean.convertEntityByToken(token);
        logger.info("User '{}' got his photo URL successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                currentUser.getPhotoURL(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his photo URL. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his photo URL successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    currentUser.getPhotoURL(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(currentUser.getPhotoURL()).build();
        }
        return response;
    }

    //Retorna username do token enviado
    @GET
    @Path("/getUsername")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsername(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        logger.info("User is trying to get his username. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User currentUser = userBean.convertEntityByToken(token);
        logger.info("User '{}' got his username successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                currentUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his username. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his username successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    currentUser.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(currentUser.getUsername()).build();
        }
        return response;
    }


    //Retorna tipo de user do token enviado
    @GET
    @Path("/getTypeOfUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTypeOfUser(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        logger.info("User is trying to get his type of user. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User currentUser = userBean.convertEntityByToken(token);
        logger.info("User '{}' got his type of user successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                currentUser.getTypeOfUser(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his type of user. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his type of user successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    currentUser.getTypeOfUser(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(currentUser.getTypeOfUser()).build();
        }
        return response;
    }

    @GET
    @Path("/getUsernameFromEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsernameFromEmail(@HeaderParam("email") String email, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        logger.info("User is trying to get his username from email. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.convertEntityByEmail(email);
        logger.info("User '{}' got his username from email successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!userBean.isAuthenticated(token)) {
            logger.info("User is not authenticated to get his username from email. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        } else {
            logger.info("User '{}' got his username from email successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getUsername(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(200).entity(user.getUsername()).build();
        }
        return response;
    }

    //Atualizar um user
    @PUT
    @Path("/update/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("username") String username, @HeaderParam("token") String token, User user, @Context HttpServletRequest request) {
        Response response;
        logger.info("User '{}' is trying to update his information. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User userUpdate = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                userUpdate, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userUpdate == null) {
            logger.info("User '{}' is not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            return response;
        }
        if (userBean.isAuthenticated(token) && userBean.userIsProductOwner(token) || userBean.thisTokenIsFromThisUsername(token, username)) {
            logger.info("User '{}' is authenticated and is a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (!userBean.isEmailUpdatedValid(user) && user.getEmail() != null) {
                logger.info("User '{}' email is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(422).entity("Invalid email").build();
            } else if (!userBean.isImageUrlUpdatedValid(user.getPhotoURL()) && user.getPhotoURL() != null) {
                logger.info("User '{}' image URL is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(422).entity("Image URL invalid").build();
            } else if (!userBean.isPhoneNumberUpdatedValid(user) && user.getPhone() != null) {
                logger.info("User '{}' phone number is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(422).entity("Invalid phone number").build();
            } else {
                logger.info("User '{}' information was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                boolean updatedUser = userBean.updateUser(user, username);
                response = Response.status(Response.Status.OK).entity(updatedUser).build();
            }
        } else {
            logger.info("User '{}' is not authenticated or is not a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @PUT
    @Path("/update/{username}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(@PathParam("username") String username,
                                   @HeaderParam("token") String token,
                                   @HeaderParam("oldpassword") String oldPassword,
                                   @HeaderParam("newpassword") String newPassword, @Context HttpServletRequest request) {
        logger.info("User '{}' is trying to update his password. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token)) {
            logger.info("User '{}' is authenticated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            boolean isOldPasswordValid = userBean.verifyOldPassword(username, oldPassword);
            if (!isOldPasswordValid) {
                logger.info("User '{}' old password is incorrect. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                return Response.status(401).entity("Incorrect old password").build();
            }
            logger.info("User '{}' old password is correct. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            boolean updated = userBean.updatePassword(username, newPassword);
            if (!updated) {
                logger.info("User '{}' password was not updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                return Response.status(400).entity("User with this username is not found").build();
            } else
                logger.info("User '{}' password was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(200).entity("User password updated").build();
        } else
            logger.info("User '{}' is not authenticated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(401).entity("User is not logged in").build();
    }

    @PUT
    @Path("/{username}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(@PathParam("username") String username,
                                   @HeaderParam("newpassword") String newPassword, @Context HttpServletRequest request) {
        logger.info("User '{}' is trying to update his password. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User '{}' is not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(404).entity("User with this username is not found").build();
        }
        long expirationTime = user.getExpirationTime();
        logger.info("User '{}' expiration time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, expirationTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        long currentTime = System.currentTimeMillis();
        logger.info("User '{}' current time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, currentTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (expirationTime != 0 && currentTime > expirationTime) {
            logger.info("User '{}' link expired. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            user.setExpirationTime(0);
            logger.info("User '{}' expiration time is now: 0. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(401).entity("Link expired").build();
        }
        logger.info("User '{}' link is not expired. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean updated = userBean.updatePassword(user.getUsername(), newPassword);
        if (!updated) {
            logger.info("User '{}' password was not updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(400).entity("User with this username is not found").build();
        } else
            logger.info("User '{}' password was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(200).entity("Password updated").build();
    }

    @POST
    @Path("/recover-password")
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendRecoverPasswordEmail(@HeaderParam("email") String email, @Context HttpServletRequest request) {
        logger.info("User is trying to recover his password. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUserByEmail(email);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User with email '{}' not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    email, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(404).entity("User with this email not found").build();
        }
        long expirationTime = System.currentTimeMillis() + 2 * 60 * 1000; // 2 minutos em milissegundos
        logger.info("User '{}' expiration time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, expirationTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        user.setExpirationTime(expirationTime);
        boolean newEmailNemPassword = emailBean.sendPasswordResetEmail(user);
        logger.info("Email was sent to '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (!newEmailNemPassword) {
            logger.info("Email was not sent to '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(404).entity("Email not sent").build();
        }
        logger.info("Email was sent to '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user.getEmail(), request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(200).entity("Email sent").build();
    }

    @PUT
    @Path("/{username}/reset-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPassword(@PathParam("username") String username,
                                  @HeaderParam("newpassword") String newPassword, @Context HttpServletRequest request) {
        logger.info("User '{}' is trying to reset his password. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User with username '{}' not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(404).entity("User with this username is not found").build();
        } else if (!user.isConfirmed()) {
            logger.info("User '{}' is not confirmed. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(401).entity("User is not confirmed").build();
        }
        long expirationTime = user.getExpirationTime();
        logger.info("User '{}' expiration time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, expirationTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        long currentTime = System.currentTimeMillis();
        logger.info("User '{}' current time is: '{}'. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, currentTime, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (expirationTime != 0 && currentTime > expirationTime) {
            logger.info("User '{}' link expired. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            user.setExpirationTime(0);
            logger.info("User '{}' expiration time is now: 0. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(401).entity("Link expired").build();
        }
        logger.info("User '{}' link is not expired. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        boolean updated = userBean.updatePassword(user.getUsername(), newPassword);
        if (!updated) {
            logger.info("User '{}' password was not updated. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            return Response.status(400).entity("User with this username is not found").build();
        }
        logger.info("User '{}' password was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        return Response.status(200).entity("Password updated").build();
    }


    @PUT
    @Path("/update/{username}/visibility")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVisibility(@PathParam("username") String username, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        logger.info("User '{}' is trying to update his visibility. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User '{}' is not found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            return response;
        }
        logger.info("User '{}' is found. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token) && userBean.userIsProductOwner(token)) {
            logger.info("User '{}' is authenticated and is a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            userBean.updateUserEntityVisibility(username);
            logger.info("User '{}' visibility was updated successfully. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.OK).entity(username + " visibility: " + !user.isVisible()).build(); //status code 200
        } else {
            logger.info("User '{}' is not authenticated or is not a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @PUT
    @Path("/update/{username}/role")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRole(@PathParam("username") String username, @HeaderParam("token") String token, @HeaderParam("typeOfUser") int typeOfUser, @Context HttpServletRequest request) {
        Response response;
        logger.info("User '{}' is trying to update his role. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        User user = userBean.getUser(username);
        logger.info("User '{}' got his information successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                user, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (user == null) {
            logger.info("User '{}' is not found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            return response;
        }
        logger.info("User '{}' is found. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
        if (userBean.isAuthenticated(token) && userBean.userIsProductOwner(token)) {
            logger.info("User '{}' is authenticated and is a Product Owner. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            if (typeOfUser == 100 || typeOfUser == 200 || typeOfUser == 300) {
                logger.info("User '{}' type of user is valid. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                boolean updatedRole = userBean.updateUserEntityRole(username, typeOfUser);
                logger.info("User '{}' role was updated successfully. Author: '{}'. IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
                response = Response.status(Response.Status.OK).entity("Role updated with success").build(); //status code 200
            } else
                logger.info("User '{}' type of user is invalid. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                        username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid type of User").build();
        } else {
            logger.info("User '{}' is not authenticated or is not a Product Owner. Author: '{}' . IP: '{}'. Timestamp: '{}'.",
                    username, request.getRemoteUser(), request.getRemoteAddr(), LocalDateTime.now());
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @DELETE
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUser(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {

        Response response;
        if (userBean.isAuthenticated(token)) {

            boolean removed = userBean.delete(username);
            if (removed) {
                response = Response.status(200).entity("User removed successfully").build();
            } else {
                response = Response.status(404).entity("User is not found").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        if (userBean.isAuthenticated(token)) {
            List<User> allUsers = userBean.getUsers();
            response = Response.status(200).entity(allUsers).build();
        } else {
            response = Response.status(401).entity("You don't have permission").build();
        }
        return response;
    }

    @GET
    @Path("/all/visible/{visible}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersByVisibility(@HeaderParam("token") String token, @PathParam("visible") boolean visible, @Context HttpServletRequest request) {
        Response response;
        if (userBean.isAuthenticated(token) && !userBean.userIsDeveloper(token)) {
            List<User> users = userBean.getUsersByVisibility(visible);
            response = Response.status(200).entity(users).build();
        } else {
            response = Response.status(401).entity("You don't have permission").build();
        }
        return response;
    }

    @GET
    @Path("/all/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("token") String token, @PathParam("type") int typeOfUser, @Context HttpServletRequest request) {
        Response response;
        if (userBean.isAuthenticated(token) && !userBean.userIsDeveloper(token)) {
            List<User> users = userBean.getUsersByType(typeOfUser);
            response = Response.status(200).entity(users).build();
        } else {
            response = Response.status(401).entity("You don't have permission").build();
        }
        return response;
    }

    @GET
    @Path("/all/{type}/{visible}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("token") String token, @PathParam("type") int typeOfUser, @PathParam("visible") boolean visible, @Context HttpServletRequest request) {
        Response response;
        if (userBean.isAuthenticated(token) && !userBean.userIsDeveloper(token)) {
            List<User> users = userBean.getUsersByTypeAndVisibility(typeOfUser, visible);
            response = Response.status(200).entity(users).build();
        } else {
            response = Response.status(401).entity("You don't have permission").build();
        }
        return response;
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("username") String username, @HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;

        User userSearched = userBean.getUser(username);

        //Verifica se o username existe na base de dados
        if (userSearched == null) {
            response = Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            return response;
        }
        //Verifica se token existe de quem consulta
        if (userBean.isAuthenticated(token)) {
            response = Response.ok().entity(userSearched).build();
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks(@HeaderParam("token") String token, @Context HttpServletRequest request) {

        Response response;

        if (userBean.isAuthenticated(token)) {
            ArrayList<Task> allTasks = taskBean.getAllTasks(token);
            response = Response.status(Response.Status.OK).entity(allTasks).build();
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/{username}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasksFromUser(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {

        Response response;

        if (userBean.isAuthenticated(token)) {
            if (userBean.thisTokenIsFromThisUsername(token, username) || userBean.userIsProductOwner(token) || userBean.userIsScrumMaster(token)) {
                ArrayList<Task> userTasks = taskBean.getAllTasksFromUser(username, token);
                response = Response.status(Response.Status.OK).entity(userTasks).build();
            } else {
                response = Response.status(406).entity("You don't have permission for this request").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @POST
    @Path("/{username}/addTask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newTask(@HeaderParam("token") String token, @PathParam("username") String username, Task task, @Context HttpServletRequest request) {
        Response response;

        if (userBean.isAuthenticated(token)) {
            if (userBean.thisTokenIsFromThisUsername(token, username)) {
                try {
                    boolean added = taskBean.newTask(task, token);
                    if (added) {
                        response = Response.status(201).entity("Task created successfully").build();
                    } else {
                        response = Response.status(404).entity("Impossible to create task. Verify all fields").build();
                    }
                } catch (Exception e) {
                    response = Response.status(404).entity("Something went wrong. A new category was not created.").build();
                }
            } else {
                response = Response.status(Response.Status.BAD_REQUEST).entity("Invalid username on path").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }

        return response;
    }

    @PUT
    @Path("/updatetask/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTask(@HeaderParam("token") String token, @PathParam("id") String id, Task task, @Context HttpServletRequest request) {

        Response response;
        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsTaskOwner(token, id) || userBean.userIsScrumMaster(token) || userBean.userIsProductOwner(token)) {
                boolean updated = taskBean.updateTask(task, id);
                System.out.println(task.getCategory().getName() + " " + task.getOwner() + " " + task.getErased() + " " + task.getStateId() + " "
                        + task.getDescription() + " " + task.getTitle() + " " + task.getId());
                if (updated) {
                    response = Response.status(200).entity("Task updated successfully").build();
                } else {
                    response = Response.status(404).entity("Impossible to update task. Verify all fields").build();
                }
            } else {
                response = Response.status(403).entity("You don't have permission to update this task").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @PUT
    @Path("/tasks/{taskId}/{newStateId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTaskStatus(@HeaderParam("token") String token, @PathParam("taskId") String taskId, @PathParam("newStateId") int stateId, @Context HttpServletRequest request) {

        Response response;
        if (userBean.isAuthenticated(token)) {
            boolean updated = taskBean.updateTaskStatus(taskId, stateId);
            if (updated) {
                response = Response.status(200).entity("Task status updated successfully").build();
            } else {
                response = Response.status(404).entity("Impossible to update task status. Task not found or invalid status").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eraseTask(@HeaderParam("token") String token, @PathParam("taskId") String id, @Context HttpServletRequest request) {

        Response response;
        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsScrumMaster(token) || userBean.userIsProductOwner(token)) {
                try {
                    boolean switched = taskBean.switchErasedTaskStatus(id);
                    if (switched) {
                        response = Response.status(200).entity("Task erased status switched successfully").build();
                    } else {
                        response = Response.status(404).entity("Task with this id is not found").build();
                    }
                } catch (Exception e) {
                    response = Response.status(404).entity("Something went wrong. The task erased status was switched.").build();
                }
            } else {
                response = Response.status(403).entity("You don't have permission to switch the erased status of a task").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/eraseAllTasks/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eraseAllTasksFromUser(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {

        Response response;
        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsProductOwner(token)) {
                try {
                    boolean erased = taskBean.eraseAllTasksFromUser(username);
                    if (erased) {
                        response = Response.status(200).entity("All tasks were erased successfully").build();
                    } else {
                        response = Response.status(404).entity("Impossible to erase tasks").build();
                    }
                } catch (Exception e) {
                    response = Response.status(404).entity("Something went wrong. The tasks were not erased.").build();
                }
            } else {
                response = Response.status(403).entity("You don't have permission to erase these tasks").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @DELETE
    @Path("/delete/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteTask(@HeaderParam("token") String token, @PathParam("taskId") String id, @Context HttpServletRequest request) {

        Response response;
        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsProductOwner(token)) {
                try {
                    boolean deleted = taskBean.permanentlyDeleteTask(id);
                    if (deleted) {
                        response = Response.status(200).entity("Task removed successfully").build();
                    } else {
                        response = Response.status(404).entity("Task with this id is not found").build();
                    }
                } catch (Exception e) {
                    response = Response.status(404).entity("Something went wrong. The task was not removed.").build();
                }
            } else {
                response = Response.status(403).entity("You don't have permission to delete a task").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/tasks/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasksByCategory(@HeaderParam("token") String token, @PathParam("category") String category, @Context HttpServletRequest request) {

        Response response;
        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsScrumMaster(token) || userBean.userIsProductOwner(token)) {
                ArrayList<Task> tasksByCategory = taskBean.getTasksByCategory(category);
                response = Response.status(200).entity(tasksByCategory).build();
            } else {
                response = Response.status(403).entity("You don't have permission for this request").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/erasedTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getErasedTasks(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsScrumMaster(token) || userBean.userIsProductOwner(token)) {
                ArrayList<Task> erasedTasks = taskBean.getErasedTasks();
                response = Response.status(200).entity(erasedTasks).build();
            } else {
                response = Response.status(403).entity("You don't have permission for this request").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @POST
    @Path("/newCategory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCategory(@HeaderParam("token") String token, Category category, @Context HttpServletRequest request) {

        Response response;

        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsProductOwner(token)) {
                if (categoryBean.categoryExists(category.getName())) {
                    response = Response.status(409).entity("Category with this name already exists").build();
                } else {
                    try {
                        boolean added = categoryBean.newCategory(category.getName());
                        if (added) {
                            response = Response.status(201).entity("Category created successfully").build();
                        } else {
                            response = Response.status(404).entity("Impossible to create category. Verify all fields").build();
                        }
                    } catch (Exception e) {
                        response = Response.status(404).entity("Something went wrong. A new category was not created.").build();
                    }
                }
            } else {
                response = Response.status(403).entity("You don't have permission to create a category").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response; // FALTA FAZER VERIFICAÇÃO DAS PERMISSÕES DO UTILIZADOR PARA CRIAR CATEGORIA
    }

    @DELETE
    @Path("/deleteCategory/{categoryName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@HeaderParam("token") String token, @PathParam("categoryName") String categoryName, @Context HttpServletRequest request) {
        System.out.println("********************** CATEGORY NAME " + categoryName);
        Response response;

        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsProductOwner(token)) {
                try {
                    boolean deleted = categoryBean.deleteCategory(categoryName);
                    if (deleted) {
                        response = Response.status(200).entity("Category removed successfully").build();
                    } else {
                        response = Response.status(400).entity("Category with this name can't be deleted").build();
                    }
                } catch (Exception e) {
                    response = Response.status(404).entity("Something went wrong. The category was not removed.").build();
                }
            } else {
                response = Response.status(403).entity("You don't have permission to delete a category").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/editCategory/{categoryName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editCategory(@HeaderParam("token") String token, @PathParam("categoryName") String categoryName, @HeaderParam("newCategoryName") String newCategoryName, @Context HttpServletRequest request) {

        Response response;

        if (userBean.isAuthenticated(token)) {
            if (userBean.userIsProductOwner(token)) {
                try {
                    System.out.println("########################## TRY " + categoryName + " " + newCategoryName);
                    boolean edited = categoryBean.editCategory(categoryName, newCategoryName);
                    System.out.println("************************** EDITED ENDPOINT " + edited + " *********************************");
                    if (edited) {
                        response = Response.status(200).entity("Category edited successfully").build();
                    } else {
                        response = Response.status(404).entity("Category with this name is not found").build();
                    }
                } catch (Exception e) {
                    response = Response.status(404).entity("Something went wrong. The category was not edited.").build();
                }
            } else {
                response = Response.status(403).entity("You don't have permission to edit a category").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories(@HeaderParam("token") String token, @Context HttpServletRequest request) {

        Response response;

        if (userBean.isAuthenticated(token)) {
            try {
                List<Category> allCategories = categoryBean.findAllCategories();
                response = Response.status(200).entity(allCategories).build();
            } catch (Exception e) {
                response = Response.status(404).entity("Something went wrong. The categories were not found.").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }


    @GET
    @Path("/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStats(@HeaderParam("token") String token, @Context HttpServletRequest request) {

        Response response;

        if (userBean.isAuthenticated(token)) {
            try {
                int totalUsers = statsBean.getNumberOfUsers();
                int totalConfirmedUsers = statsBean.getNumberOfConfirmedUsers();
                int totalUnconfirmedUsers = statsBean.getNumberOfUnconfirmedUsers();
                ArrayList<RegistInfoUser> usersOverTime = statsBean.getSumOfUsersPerMonth();

                int totalTasks = statsBean.getNumberOfTasks();
                int totalToDoTasks = statsBean.getNumberOfTodoTasks();
                int totalDoingTasks = statsBean.getNumberOfDoingTasks();
                int totalDoneTasks = statsBean.getNumberOfDoneTasks();
                ArrayList<RegistInfoTask> tasksCompletedOverTime = statsBean.getSumOfCompletedTasksPerMonth();

                double tasksPerUser = statsBean.getAverageNumberOfTasksPerUser();
                double averageTaskTime = statsBean.getAverageOfTaskTimes();

                ArrayList<RegistInfoCategory> categoriesListDesc = statsBean.getNumberOfCategoriesFromMostFrequentToLeast();

                response = Response.status(200).entity(new Stats(totalUsers, totalConfirmedUsers, totalUnconfirmedUsers, totalTasks, totalToDoTasks,
                        totalDoingTasks, totalDoneTasks, tasksPerUser, averageTaskTime, categoriesListDesc, usersOverTime, tasksCompletedOverTime)).build();

            } catch (Exception e) {
                response = Response.status(404).entity("Something went wrong. The stats were not found.").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/{username}/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserStats(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {

        Response response;

        if (userBean.isAuthenticated(token)) {
            try {
                int totalUserTasks = statsBean.getNumberOfTasksByUser(username);
                int totalUserToDoTasks = statsBean.getNumberOfTodoTasksByUser(username);
                int totalUserDoingTasks = statsBean.getNumberOfDoingTasksByUser(username);
                int totalUserDoneTasks = statsBean.getNumberOfDoneTasksByUser(username);

                response = Response.status(200).entity(new UserStats(totalUserTasks, totalUserToDoTasks, totalUserDoingTasks, totalUserDoneTasks)).build();

            } catch (Exception e) {
                response = Response.status(404).entity("Something went wrong. The stats were not found.").build();
            }
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/getAllMessagesBetweenUsers/{usernameReceiver}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMessagesBetweenUsers(@HeaderParam("token") String token, @PathParam("usernameReceiver") String usernameReceiver, @Context HttpServletRequest request) {
        Response response;
        if (userBean.isAuthenticated(token)) {
            UserEntity userSender = userDao.findUserByToken(token);
            String usernameSender = userSender.getUsername();
            ArrayList<ChatMessage> messages = chatBean.getAllChatMessagesBetweenUsers(usernameSender, usernameReceiver);
            chatBean.setMessagesAsReadFromSenderToUser(usernameSender, usernameReceiver);
            response = Response.status(200).entity(messages).build();
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @GET
    @Path("/getAllNotifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUnreadNotifications(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        Response response;
        if (userBean.isAuthenticated(token)) {
            UserEntity userReceiver = userDao.findUserByToken(token);
            String usernameReceiver = userReceiver.getUsername();
            ArrayList<ChatNotification> notifications = chatBean.getAllNotificationsByReceiver(usernameReceiver);
            System.out.println("Notifications: " + notifications);
            response = Response.status(200).entity(notifications).build();
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }

    @PUT
    @Path("/markNotificationsAsRead/{senderUsername}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markNotificationsAsRead(@HeaderParam("token") String token, @PathParam("senderUsername") String senderUsername, @Context HttpServletRequest request) {
        Response response;
        if (userBean.isAuthenticated(token)) {
            UserEntity userReceiver = userDao.findUserByToken(token);
            System.out.println("User receiver: " + userReceiver.getUsername());
            System.out.println("User sender: " + senderUsername);
            String usernameReceiver = userReceiver.getUsername();
            chatBean.setNotificationsAsReadFromSenderToUser(usernameReceiver, senderUsername);
            System.out.println("Notifications marked as read");
            response = Response.status(200).entity("Notifications marked as read").build();
        } else {
            response = Response.status(401).entity("Invalid credentials").build();
        }
        return response;
    }
}