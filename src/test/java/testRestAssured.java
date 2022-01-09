import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.testng.Assert;

import java.util.List;

import static io.restassured.RestAssured.given;

public class testRestAssured {

    final static String url="https://restful-booker.herokuapp.com/booking";
    static String vid = "";
    static String vid1 = "";

    public static void main(String args[]) {
        try {
            vid = getResponseBody();
            //getResponseStatus();
            vid1 = postResponseBody("{\n" +
                    "    \"firstname\" : \"Jim\",\n" +
                    "    \"lastname\" : \"Brown\",\n" +
                    "    \"totalprice\" : 123,\n" +
                    "    \"depositpaid\" : false,\n" +
                    "    \"bookingdates\" : {\n" +
                    "        \"checkin\" : \"2018-01-01\",\n" +
                    "        \"checkout\" : \"2019-01-01\"\n" +
                    "    },\n" +
                    "    \"additionalneeds\" : \"Breakfast\"\n" +
                    "}");
            putResponseBody("{\n" +
                    "    \"firstname\" : \"James\",\n" +
                    "    \"lastname\" : \"Brown\",\n" +
                    "    \"totalprice\" : 111,\n" +
                    "    \"depositpaid\" : true,\n" +
                    "    \"bookingdates\" : {\n" +
                    "        \"checkin\" : \"2018-01-01\",\n" +
                    "        \"checkout\" : \"2019-01-01\"\n" +
                    "    },\n" +
                    "    \"additionalneeds\" : \"Breakfast\"\n" +
                    "}", vid1);
            deleteResponseBody(vid);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void putResponseBody(String s, String id) {
        String APIBody = s;
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(APIBody);
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpec = builder.build();
        Response response = given().auth().preemptive().basic("admin", "password123")
                .spec(requestSpec).when().put(url + "/" + id); //.then().assertThat().statusCode(200);
        int status = response.getStatusCode();
        Assert.assertEquals(status, 200);
        System.out.println("Change Successful for id:" + id);
        //System.out.println("PUT response" + response.getBody());
    }

    private static void deleteResponseBody(String id) {
        /*Response response = given().auth().preemptive().basic("admin", "password123")
                .when().delete(url+ id);*/
        //int statusCode= given().when().delete(url + "/" + id).getStatusCode();
        //System.out.println("The response code is: " + when().get(url).then().statusCode(200).extract().body().asString());
        given().auth().preemptive().basic("admin", "password123").when().delete(url + "/" + id).then().assertThat().statusCode(201);
        System.out.println("The following id was deleted: "+ id);
    }

    private static String getResponseBody(){
        //Response resp = given().when().get(url).then().log().all();
        Response resp = given().when().get(url);
        String respBod = resp.getBody().asString();
        System.out.println("Response Body is: " + respBod);
        JsonPath jsonPathEval = resp.jsonPath();
        List<Integer> keyVal = jsonPathEval.get("bookingid");
        getResponseStatus("bookingid", keyVal);
        return Integer.toString(keyVal.get(0));
    }


    private static void getResponseStatus(String id, List val){
        int statusCode= given().queryParam(id, val)
                .when().get(url).getStatusCode();
        //System.out.println("The response code is: " + when().get(url).then().statusCode(200).extract().body().asString());
        given().when().get(url).then().assertThat().statusCode(200);
    }

    private static String postResponseBody(String body) throws JSONException,InterruptedException {
        String APIBody = body;

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(APIBody);
        builder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpec = builder.build();
        Response response = given().auth().preemptive().basic("admin", "password123")
                .spec(requestSpec).when().post(url);
        int status = response.getStatusCode();
        String result = response.jsonPath().get("bookingid").toString();
        //given().when().post(url+"/booking").then().assertThat().statusCode(200);
        try {
            int isNum = Integer.parseInt(result);
            System.out.println("Booking id is a Number");
        } catch(NumberFormatException nfe){
            System.out.println("Booking id is not a Number");
        }
        System.out.println("Response Body is: " + response.getBody().asString());
        Assert.assertEquals(status, 200);
        return result;
    }

}