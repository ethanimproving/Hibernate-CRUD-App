package org.hibernate.jht;

import org.hibernate.jht.database.JPAUtility;
import org.hibernate.jht.entity.Customer;
import org.hibernate.type.CustomCollectionType;

import javax.persistence.*;
import java.util.List;

public class TestSystem {

    public static void main(String[] args) {
        addCustomer("Ethan", "Miller");

    }

    // CREATE

    public static void addCustomer(String firstName, String lastName) {
        EntityManager em = JPAUtility.getEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            Customer customer = new Customer();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            em.persist(customer);
            et.commit();
        } catch (Exception ex) {
            if(et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        }
    }

    // READ

    public static void getCustomer(int id) {
        EntityManager em = JPAUtility.getEntityManager();
        // :custID is a paramaterized query
        String query = "select c from Customer c where c.id = :custID";

        TypedQuery<Customer> tq = em.createQuery(query, Customer.class);
        tq.setParameter("custID", id);
        Customer customer;
        try {
            customer = tq.getSingleResult();
            System.out.println(customer.getFirstName());
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }

    }

    public static void getCustomers() {
        EntityManager em = JPAUtility.getEntityManager();
        String query = "select c from Customer as c where c.id is not null";
        TypedQuery<Customer> tq = em.createQuery(query, Customer.class);
        List<Customer> customers;
        try {
            customers = tq.getResultList();
            customers.forEach(customer -> System.out.println(customer.getFirstName() + " " + customer.getLastName()));
        } catch (NoResultException ex) {
            ex.printStackTrace();
        }
    }

    // UPDATE

    public static void changeFirstName(int id, String firstName) {
        EntityManager em = JPAUtility.getEntityManager();
        EntityTransaction et = null;
        Customer customer = null;
        try {
            et = em.getTransaction();
            et.begin();
            customer = em.find(Customer.class, id);
            customer.setFirstName(firstName);

            em.persist(customer);
            et.commit();
        } catch (Exception ex) {
            if(et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        }
    }

    // DELETE

    public static void deleteCustomer(int id) {
        EntityManager em = JPAUtility.getEntityManager();
        EntityTransaction et = null;
        Customer customer;
        try {
            et = em.getTransaction();
            et.begin();
            customer = em.find(Customer.class, id);
            em.remove(customer);

            em.persist(customer);
            et.commit();
        } catch (Exception ex) {
            if(et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        }
    }


}
