# Hibernate

1. Create new Maven App
1. Add hibernate-core, jaxb-api, and mysql-connector-java to dependencies


#### Create new schema

```sql
create schema hibernatesandbox;

use hibernatesandbox;

create table customer(
	firstName varchar(30) not null,
	lastName varchar(30) not null,
	custID int unsigned not null primary key
);

-- view table makeup
describe customer;

```

#### Create new user

```sql
-- view all users
select *
from mysql.user
order by user;


-- create new user
create user 'improving'@'localhost' identified by 'excellence';


-- grant all priveleges to new use
grant all privileges on hibernatesandbox to
	'improving'@'localhost' identified by 'excellence';
	

-- drop user for reference
drop user 'improving'@'localhost';


-- Check MySQL Version for reference
SHOW VARIABLES LIKE "%version%";
```

## META-INF

Create a file called `persistence.xml` with the following boilerplate configuration:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0">
    <persistence-unit name="ORG.HIBERNATE.JHT">
        <description>JPA Demo</description>
        <provider>org.hibernate.ejb.HibernatePersistence</provider>
        <class>org.hibernate.jht.entity.Customer</class>

        <properties>
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/> <!-- create or validate the database schema -->
            <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/HIBERNATESANDBOX?serverTimezone=UTC"/>
            <property name="javax.persistence.jdbc.user" value="IMPROVING"/>
            <property name="javax.persistence.jdbc.password" value="EXCELLENCE"/>
        </properties>
    </persistence-unit>
</persistence>
```

## Javax API Bind

```xml
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.2.11</version>
</dependency>
```

### What is a Persistence Unit?

A persistence unit defines a set of all entity classes that are managed by `EntityManager` in an application.

The declaration in your `pom.xml` file should match you entity manager factory:

```xml
<!--persistence.xml-->
<persistence-unit name="org.hibernate.jht">

// JPAUtility.java
emFactory = Persistence.createEntityManagerFactory("org.hibernate.jht");
```

### What is a Transaction?

A transaction is a logical unit of work that contains one or more SQL statements. The following is an example of a Read transaction:

```java
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
```

### Errors

#### SELECT command denied

``` warn
SELECT command denied to user 'improving'@'localhost' for table 'hibernate_sequence'
```

SOLUTION: Check if user has Select_priv (Y/N)

```xml
<!-- In persistence.xml -->
<property name="javax.persistence.jdbc.user" value="improving"/>
<property name="javax.persistence.jdbc.password" value="excellence"/>

-- In MySQL Workbench
select *
from mysql.user
order by user;

grant all privileges on *.* to 'improving'@'localhost' identified by 'excellence';
```

#### hibernate_sequence doesn't exist

``` warn
java.sql.SQLSyntaxErrorException: Table 'hibernatesandbox.hibernate_sequence' doesn't exist
```

With the generation GenerationType.AUTO hibernate will look for the default hibernate_sequence table , so change generation to IDENTITY as below :

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "custID", unique = true)
private int id;
```

Make sure that your ID field is set to AUTO-INCREMENT:

```SQL
drop table customer;

create table customer(
	custID int unsigned auto_increment not null primary key,
	firstName varchar(30) not null,
	lastName varchar(30) not null
);
```

Note: UNSIGNED means the int will never be a negative value.