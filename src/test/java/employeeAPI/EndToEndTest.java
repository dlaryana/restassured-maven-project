package employeeAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class EndToEndTest {
	
	Response response;
	RequestSpecification request;
	String baseURI = "http://localhost:3000";

	
	@Test
	public void test1() {
		
		//get all employees
		response = GetAllEmployee();
		Assert.assertEquals(200, response.statusCode());
		
		//create an employee
		response = CreateEmployee("John", "8000");
		JsonPath jpath = response.jsonPath();
		int empId = jpath.get("id");
		Assert.assertEquals(201, response.statusCode());
		
		//get a single employee           
		response = GetSingleEmployee(empId);
		JsonPath jPath = response.jsonPath();
		String name = jPath.get("name");		
		Assert.assertEquals(name, "John");
		Assert.assertEquals(200, response.statusCode());
		
		//update first employee
		response = UpdateEmployee( empId, "Smith", "8000");
		Assert.assertEquals(200, response.statusCode());
		//System.out.println(response.getBody().asString());
		
		//validate the name has been changed to Smith           
		response = GetSingleEmployee(empId);
		JsonPath updatedJpath = response.jsonPath();
		String updatedName = updatedJpath.get("name");		
		Assert.assertEquals(updatedName, "Smith");
		Assert.assertEquals(200, response.statusCode());
		
		//delete created employee
		response = DeleteEmployee( empId);
		Assert.assertEquals(200, response.statusCode());
		
		//validate 404 on retrieving the deleted employee           
		response = GetSingleEmployee(empId);
		Assert.assertEquals(404, response.statusCode());
		
		//validate deleted employee is not in response
		response = GetAllEmployee();
		JsonPath Jpath = response.jsonPath();
		List<String> names = Jpath.get("name");
		Assert.assertFalse(names.contains("Smith"));
		
	}
	
	public Response GetAllEmployee(){
		
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		response = request.get("employees");
		return response;
	}	
	
	public Response GetSingleEmployee(int empId){
			
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
		//response = request.param("id", empId).get("employees");
		response = request.get("employees/" + empId);
		
		return response;
	}
	
	public Response CreateEmployee(String name, String salary){
		
		Map<String,Object> MapObj = new HashMap<String,Object>();	
		MapObj.put("name", name);
		MapObj.put("salary", salary);
		
		RestAssured.baseURI = this.baseURI;
		request = RestAssured.given();
	
		Response response = request 
							.contentType(ContentType.JSON)
							.accept(ContentType.JSON)
							.body(MapObj)
							.post("employees/create");

		return response;
	}
	
	public Response UpdateEmployee(int empId, String name, String salary){
			
			Map<String,Object> MapObj = new HashMap<String,Object>();	
			MapObj.put("name", empId);
			MapObj.put("name", name);
			MapObj.put("salary", salary);
			
			RestAssured.baseURI = this.baseURI;
			request = RestAssured.given();
		
			Response response = request 
								.contentType(ContentType.JSON)
								.accept(ContentType.JSON)
								.body(MapObj)
								.put("employees/" + empId);
	
			return response;
		}
	
		public Response DeleteEmployee(int empId) {
			RestAssured.baseURI = this.baseURI;
			request = RestAssured.given();
			
			response = request.delete("employees/" + empId);
			
			return response;
			
		}
}
