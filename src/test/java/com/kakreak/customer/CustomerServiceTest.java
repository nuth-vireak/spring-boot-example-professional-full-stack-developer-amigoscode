package com.kakreak.customer;

import com.kakreak.exception.DuplicateResourceException;
import com.kakreak.exception.RequestValidationException;
import com.kakreak.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        // Given
        Integer id = 10;
        Customer customer = new Customer(
                id,
                "John Doe",
                "doe@gmail.com",
                25
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomerById(id);

        // Then
        assertThat(actual).isEqualTo(customer);
        verify(customerDao).selectCustomerById(id);
    }

    @Test
    void willThrowWhenGetCustomerByIdReturnEmptyOptional() {
        // Given
        Integer id = 10;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer " + id + " does not exists");

        verify(customerDao).selectCustomerById(id);
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "Alex",
                email,
                20
        );

        // When
        underTest.addCustomer(customerRegistrationRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue.getId()).isNull();
        assertThat(customerArgumentCaptorValue.getName()).isEqualTo(customerRegistrationRequest.name());
        assertThat(customerArgumentCaptorValue.getEmail()).isEqualTo(customerRegistrationRequest.email());
        assertThat(customerArgumentCaptorValue.getAge()).isEqualTo(customerRegistrationRequest.age());
    }

    @Test
    void willThrowWhenEmailIsTakenWhileAddCustomer() {
        // Given
        String email = "alex@gmail.com";

        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "Alex",
                email,
                20
        );

        // When
        // Then
        assertThatThrownBy(() -> underTest.addCustomer(customerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        verify(customerDao).existsPersonWithEmail(email);
    }

    @Test
    void deleteCustomerById() {
        // Given
        Integer id = 10;

        when(customerDao.exitsPersonWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomer(id);
    }

    @Test
    void willThrowWhenDeleteCustomerByIdReturnEmptyOptional() {
        // Given
        Integer id = 10;

        when(customerDao.exitsPersonWithId(id)).thenReturn(false);

        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer " + id + " does not exist.");

        verify(customerDao).exitsPersonWithId(id);
    }

    @Test
    void updateCustomerById_ChangesName_Success() {
        // Given
        Integer id = 10;
        Customer customer = new Customer(
                id,
                "John Doe",
                "doe@gmail.com",
                25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Alex",
                null,
                null
        );

        // When
        underTest.updateCustomerById(id, customerUpdateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue.getId()).isEqualTo(id);
        assertThat(customerArgumentCaptorValue.getName()).isEqualTo(customerUpdateRequest.name());
    }

    @Test
    void updateCustomerById_ChangesEmail_Success() {
        // Given
        Integer id = 10;
        Customer customer = new Customer(
                id,
                "John Doe",
                "doe@gmail.com",
                25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                "test@gmail.com",
                null
        );

        // When
        underTest.updateCustomerById(id, customerUpdateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue.getId()).isEqualTo(id);
        assertThat(customerArgumentCaptorValue.getEmail()).isEqualTo(customerUpdateRequest.email());
    }

    @Test
    void updateCustomerById_ChangesAge_Success() {
        // Given
        Integer id = 10;
        Customer customer = new Customer(
                id,
                "John Doe",
                "doe@gmail.com",
                25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                null,
                30
        );

        // When
        underTest.updateCustomerById(id, customerUpdateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue.getId()).isEqualTo(id);
        assertThat(customerArgumentCaptorValue.getAge()).isEqualTo(customerUpdateRequest.age());
    }

    @Test
    void updateCustomerById_NoChanges_ThrowException() {
        // Given
        Integer id = 10;
        Customer customer = new Customer(
                id,
                "John Doe",
                "doe@mgmail.com",
                25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                null,
                null
        );

        // When
        // Then
        assertThatThrownBy(() -> underTest.updateCustomerById(id, customerUpdateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changed");

        verify(customerDao, never()).updateCustomer(any());
    }
}