package com.example.springboot_batch_processing.config;

import org.springframework.batch.item.ItemProcessor;

import com.example.springboot_batch_processing.entity.Customer;

public class ItemProcessing implements ItemProcessor<Customer, Customer> {
    
    @Override
    public Customer process(Customer customer) throws Exception{
        return customer;
    }
    
}
