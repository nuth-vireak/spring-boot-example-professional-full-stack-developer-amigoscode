package com.kakreak.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    void deleteCustomer(Integer id);
    boolean exitsPersonWithId(Integer id);
    void updateCustomer(Customer customer);
}
