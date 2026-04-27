# Lab: Pricing & Discount Engine (Refactoring + Gradle + Testing)

## Objective
This lab combines:
- Git/GitHub workflow
- Refactoring of poor-quality code
- Gradle build system
- Java unit testing (JUnit)
- Python-based integration testing

Students start from a badly designed Java class and progressively improve it.

## Project Overview

Build a **pricing engine** that calculates the final price of an order.

### Inputs
- List of item prices
- Quantities
- Customer type (`REGULAR`, `VIP`)
- Discount code (`SAVE10`, `SAVE20`, etc.)

### Outputs
- Subtotal
- Discount amount
- Tax
- Final price

## Lab Workflow

1. Create a Gradle-based Java project  
2. Initialize a Git repository and push to GitHub  
3. Add the provided “bad design” starter code  
4. Write initial unit tests (JUnit)  
5. Refactor the code (improve structure, separation of concerns, readability)  
6. Continue committing at each logical step with clear messages
