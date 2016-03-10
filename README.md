<b>To build:</b>  
gradle build

<b>To start server:</b>  
java -jar build/lib/project1-1.0.0.jar  

<b>To execute on Linux:</b>  
GET: curl -i -H "Content-Type: application/json" -X GET $URL  
POST: curl -i -H "Content-Type: application/json" -X POST -d '{$JSON}' $URL  

<b>To execute on Windows:</b>  
Get yourself SoapUI or stuff like that=)  

<b>API reference:</b>  

**To submit loan application:**   
URL: POST http://localhost:8080/loan  
params in JSON: 
customerId : String (Mandatory)  
firstName : String (Mandatory), should contains latin letters only  
lastName : String (Mandatory), should contains latin letters only  
amount : Numeric in format xxxx.xx (min=1, max=1000) (Mandatory)   
Except of above mentioned requirements following validation rules are applied: 
  - Should be not more than 2 requests per seconf from the same country
  - If user with requested customerId already exist in the database provided firstName and lastName must match ones in the database
  - If user with requested customerId already exist in the database it should not be blacklisted  

Expected success response: 201 CREATED 



**To retrieve all approved loan applications:**  
URL: GET http://localhost:8080/loans/all  
Expected success response: 200 OK  
Example of success response body: [{"id":1,"amount":200.34,"customer":{"id":"2323","firstName":"Name","lastName":"Surname","blacklisted":false},"applicationCountry":"LV"},{"id":2,"amount":888.88,"customer":{"id":"2323","firstName":"Name","lastName":"Surname","blacklisted":false},"applicationCountry":"LV"}]  



**To retrieve single loan by id:**  
UR: GET http://localhost:8080/loan/{id}  
params:  
id : Long (Mandatory)  
Expected success response: 200 OK  
Example of success response body: {"id":1,"amount":200.34,"customer":{"id":"2323","firstName":"Name","lastName":"Surname","blacklisted":false},"applicationCountry":"LV"}



**To retrieve all loans approved for customer by customerId:**  
URL: GET http://localhost:8080/loans/user/{customerId}  
params:  
customerId : String (Mandatory)  
Expected success response: 200 OK  
Example of success response body: [{"id":1,"amount":200.34,"customer":{"id":"2323","firstName":"Name","lastName":"Surname","blacklisted":false},"applicationCountry":"LV"},{"id":2,"amount":888.88,"customer":{"id":"2323","firstName":"Name","lastName":"Surname","blacklisted":false},"applicationCountry":"LV"}] 



**To retrieve customer details by customerId:**  
URL: GET http://localhost:8080/customer/{id}  
params:  
id : String (Mandatory)  
Expected success response: 200 OK  
Example of success response body: {"id":"2323","firstName":"Name","lastName":"Surname","blacklisted":false} 



**To mark customer as blacklisted by customerId:** 
URL: POST http://localhost:8080/customer/blacklist/{id}  
params:  
id : String (Mandatory)  
Following validation rules are applied: 
  - User with given id should exist in the database
  - User with given id should not be already blacklisted  

Expected success response: 200 OK





