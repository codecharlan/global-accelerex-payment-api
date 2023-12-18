# GlobalAccelerex Payment API

![CI/CD Workflow](https://github.com/codecharlan/Test/actions/workflows/maven.yml/badge.svg)

## Overview

Welcome to the GlobalAccelerex Payment API! This RESTful service allows you to handle card payments and retrieve payment information for different devices, like mobile and browsers. Here's what our service offers:
- **Make Payment Endpoint:** Make secure card payments via a POST request.
- **Get Payments Endpoint:** Retrieve payment information via a GET request.
- **Support for Multiple Payment Types:** Designed to support various payment types in the future.
- **Device Support:** Integrates with different devices such as mobile and web browsers.

### Prerequisites

Ensure that input and output data are formatted in JSON.

### Getting Started

To begin using the Payment Application, follow these steps:

1. Install the required dependencies by running:
   ```shell
   mvn install
    ```

2. Start the application with:
    ```shell
    mvn spring-boot:run
    ```

### API Endpoints
The API exposes the following endpoints:

* **Make Payment:** Send a POST request to **`/api/v1/pay`** with JSON request body containing payment details.

* **Get Payments:** Issue a GET request to **`/api/v1/payments`** to retrieve payment information.


### Testing
Unit tests can be run using the following command:

```shell
    mvn test
   ```
### Technology Used:
* Java
* SpringBoot
* CI/CD
* Junit & Mockito
* Git and GitHub
* Docker
* Postman(Documentation)

### Assumptions
Integration with a Third Party Payment API:
The payment API is integrated with a secured third party api **`Flutterwave`** for secured and seamless transactions.

Support for Future Payment Types:
The system architecture is designed to seamlessly integrate with additional payment types in the future without significant alterations.

Device Compatibility:
The API is built to handle payments made from various devices and supports future integrations with ease.

### Conclusion
In conclusion, our Payment API provides a solid foundation for handling card payments and retrieving payment details. We encourage you to explore the endpoints and functionalities provided by our API to streamline your payment processes. Whether you're a developer, a business entity or a user, our service is designed to simplify payment handling and ensure future scalability.
PostMan Documentation Available Here: https://documenter.getpostman.com/view/29888943/2s9Ykoc1dH
Feel free to reach out with any questions, feedback, or suggestions. We're committed to enhancing our API to meet your payment needs.