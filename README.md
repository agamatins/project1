<b>To build:</b> 
gradle build

<b>To run:</b>  
java -jar build/lib/project1-1.0.0.jar  

<b>To execute on Linux:</b>  
GET: curl -i -H "Content-Type: application/json" -X GET $URL  
POST: curl -i -H "Content-Type: application/json" -X POST -d '{$JSON}' $URL  

<b>To execute on Windows:</b>  
Get yourself SoapUI or stuff like that=)  

<b>API reference:</b>  
<pre><code>To submit loan application:   
URL: POST http://localhost:8080/loan  
params in JSON: 
customerId : String (Mandatory)  
firstName : String (Mandatory)  
lastName : String (Mandatory)  
amount : Numeric in format xxxx.xx (min=1, max=1000) (Mandatory)   
Except of above mentioned requirements following validation rules are applied: 
<ol>
<li>Should be not more than 2 requests per seconf from the same country</li>
<li>If user with requested customerId already exist in the database provided firstName and lastName must match ones in the database</li>
<li>If user with requested customerId already exist in the database it should not be blacklisted</li>
</ol>
Expected success response: 201 CREATED 
</pre></code>
To retrieve all approved loan applications:  
URL: GET http://localhost:8080/loans/list  
Expected success response: 200 OK  
Expected success response body: TBD  

To retrieve single loan by id:  
UR: GET http://localhost:8080/loan/{id}  
params:  
id : Long (Mandatory)
Expected success response: 200 OK  
Expected success response body: TBD 

To retrieve all loans approved for customer by customerId:  
URL: GET http://localhost:8080/loans/user/{customerId}  
params:  
customerId : String (Mandatory)
Expected success response: 200 OK  
Expected success response body: TBD 

To retrieve customer details by customerId:  
URL: GET http://localhost:8080/customer/{id}  
params:  
id : String (Mandatory)
Expected success response: 200 OK  
Expected success response body: TBD 

>To mark customer as blacklisted by customerId:  
URL: POST http://localhost:8080/customer/blacklist/{id}  
params:  
id : String (Mandatory)
Following validation rules are applied: 
<ol>
<li>User with given id should exist in the database</li>
<li>User with given id should not be already blacklisted</li>
</ol>
Expected success response: 200 OK




