package com.kakreak.customer;

import com.kakreak.exception.DuplicateResourceException;
import com.kakreak.exception.RequestValidationException;
import com.kakreak.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " does not exists"));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // check if mail exist
        String email = customerRegistrationRequest.email();
        if (customerDao.existsPersonWithEmail(email)) {
            throw new DuplicateResourceException("Email already taken");
        }
        // add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer customerId) {

        // check if customerID exist
        if (!customerDao.exitsPersonWithId(customerId)) {
            throw new ResourceNotFoundException("Customer " + customerId + " does not exist.");
        }

        customerDao.deleteCustomer(customerId);
    }

    public void updateCustomerById(Integer customerId, CustomerUpdateRequest customerUpdateRequest) {

        Customer customer = getCustomerById(customerId);

        boolean ischanged = false;

        if (customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())) {
            customer.setName(customerUpdateRequest.name());
            ischanged = true;
        }

        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customer.getEmail())) {
            customer.setEmail(customerUpdateRequest.email());
            ischanged = true;
        }

        if (customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customer.getAge())) {
            customer.setAge(customerUpdateRequest.age());
            ischanged = true;
        }

        if (!ischanged)
            throw new RequestValidationException("No data changed");

        customerDao.updateCustomer(customer);
    }
}
