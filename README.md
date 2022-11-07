
# Rest_Assured_API_Testing_TestNG

### An API is tested by using REST Assured framework integrated with TestNG as testing framework for validation purpose. Here, the status codes, validation messages and the flow of API is tested using a Dmoney API where there is login,searching,creating,updating and deleting features.

Here the following tasks are done:

* Login feature tested using proper valiadtion,negative test cases added for email and password.
* Can get user list by user authorization token, both positive and negative test cases are added for it.
* Can search any user by proper id.
* Can create a user by random name,email,password,nid and phone number using proper validation and secret key token.
* Can update any user by the corresponding id, validated using PUT and PATCH method.
* Can delete any user by the id, negative test cases are added for it.
* The variables are set and used from config.properties file.

### Technology:

* Tool: REST Assured
* IDE: Intellij
* Build tool: Gradle
* Language: Java
* Test_Runner: TestNG

### Project Run

* Clone the repo.
* Open cmd in the root folder.


### Run the Automation Script by the following command:

```bash
  gradle clean test 
```

* After automation to view allure report , give the following commands:

```bash
allure generate allure-results --clean -o allure-report
allure serve allure-results
```
#### Here is the Allure report overview:


