package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.errors.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ExceptionControllerTest {
    private MockMvc mock;

    private ErrorHandler exceptionController = new ErrorHandler();

    @Test
    public void checkHandleNotFound() {
        NotFoundException nfe = new NotFoundException("not found");
        ErrorResponse response = exceptionController.handleNotFound(nfe);
        assertThat(response.getError(), equalTo(nfe.getMessage()));
    }

    @Test
    public void checkHandleNotOwnerTryEdit() {
        ForbidenForUserOperationException ffuoe = new ForbidenForUserOperationException("not owner try to edit");
        ErrorResponse response = exceptionController.handleNotOwnerTryEdit(ffuoe);
        assertThat(response.getError(), equalTo(ffuoe.getMessage()));
    }

    @Test
    public void checkHandleNotValid() {
        ValidationException ve = new ValidationException("not valid");
        ErrorResponse response = exceptionController.handleNotValid(ve);
        assertThat(response.getError(), equalTo(ve.getMessage()));
    }

    @Test
    public void checkHandleSameEmail() {
        SameEmailException see = new SameEmailException("same email");
        ErrorResponse response = exceptionController.handleSameEmail(see);
        assertThat(response.getError(), equalTo(see.getMessage()));
    }

}
