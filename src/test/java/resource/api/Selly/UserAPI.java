package resource.api.Selly;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import resource.api.common.RestAssuredConfiguration;
import resource.common.GlobalVariables;
import resource.common.TestBase;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class UserAPI extends TestBase {
    private String issueIdPath = "/{issueId}";

    private JSONParser jsonParser = new JSONParser();
    private JSONObject jsonExpected = null;
    private JSONObject jsonUser = null;
    private JSONObject jsonUserMeInfo = null;
    private String userStatistic = null;
    private String userId = null;
    private String userToken = null;

    private RequestSpecification UserSpecification() {
        return given().
                baseUri("https://" + GlobalVariables.SellyEnvironment).
                relaxedHTTPSValidation();
    }

//    private RequestSpecification UserMeSpecification(String userToken) {
//        return given().
//                baseUri("https://" + GlobalVariables.API_ENVIRONMENT).
//                header("version","1.2").
//                header("Authorization", "Bearer " + userToken).
//                relaxedHTTPSValidation();
//    }

//    public void deleteUserAccount(ExtentTest logTest, String email) throws IOException {
//        Response response = null;
//        logInfo(logTest, "----->Delete userAPI account: " + email);
//
//        RequestSpecification deleteUserSpec = new userAPI().UserSpecification();
//        deleteUserSpec.queryParam("email", email);
//        response = deleteUserSpec.delete("/api/user");
//
////        logInfo(logTest, "----->Delete User Account - RESPONSE: " + "<br>" + response.getBody().asString());
//
//        handleResponseStatusCode(response, 200, logTest);
//
//    }


//    public JSONObject getUserInfo(ExtentTest logTest, String email) throws IOException {
//        try {
//            RequestSpecification getUserInfoSpec = new userAPI().UserSpecification();
//            getUserInfoSpec.queryParam("email", email);
//            Response response = getUserInfoSpec.get("/api/user");
//
//            jsonUserInfo = (JSONObject) jsonParser.parse(response.body().asString());
//
//            return jsonUserInfo;
//
//            } catch (Exception e) {
//            log4j.error("getUserInfo method - ERROR: " + e);
//            logException(logTest, "getUserInfo method - ERROR: ", e);
//            }
//
//        return jsonUserInfo;
//    }

    public String getSellerToken(ExtentTest logTest, String userId) throws IOException {
        try {

            userToken = ((JSONObject) getSellerInfo(logTest, userId).get("data")).get("token").toString();

            logInfo(logTest, "----->GET Seller Token: " + userToken);

            return userToken;

        } catch (Exception e) {
            log4j.error("getSellerToken method - ERROR: " + e);
            logException(logTest, "getSellerToken method - ERROR: ", e);
        }

        return userToken;
    }

    public JSONObject getSellerInfo(ExtentTest logTest, String userId) throws IOException {
        try {
            RequestSpecification getSellerInfoSpec = this.UserSpecification();
            getSellerInfoSpec.queryParam("userId", userId);
            Response response = getSellerInfoSpec.get("/users/token");

            jsonUser = (JSONObject) jsonParser.parse(response.body().asString());

            return jsonUser;

            } catch (Exception e) {
            log4j.error("getSellerInfo method - ERROR: " + e);
            logException(logTest, "getSellerInfo method - ERROR: ", e);
        }

        return jsonUser;
    }

//    public JSONObject getUserMe(ExtentTest logTest, String userToken) throws IOException {
//        try {
//            RequestSpecification getUserMeInfoSpec = new userAPI().UserMeSpecification(userToken);
//            Response response = getUserMeInfoSpec.get("/me");
//
//            jsonUserMeInfo = (JSONObject) jsonParser.parse(response.body().asString());
//
//            return jsonUserMeInfo;
//
//        } catch (Exception e) {
//            log4j.error("getUserMe method - ERROR: " + e);
//            logException(logTest, "getUserMe method - ERROR: ", e);
//        }
//
//        return jsonUserMeInfo;
//    }

//    public String getUserMeStatistic(ExtentTest logTest, String userToken, String statisticField) throws IOException {
//        try {
//            userStatistic = ((JSONObject) ((JSONObject) getUserMe(logTest, userToken).get("data")).get("user")).get("statistic").toString();
//            userStatistic = ((JSONObject) ((JSONObject) ((JSONObject) getUserMe(logTest, userToken).get("data")).get("user")).get("statistic")).get(statisticField).toString();
//            logInfo(logTest, "----->GET User Me Statistic: " + statisticField + " " + userStatistic);
//
//            return userStatistic;
//
//        } catch (Exception e) {
//            log4j.error("getUserMeStatistic method - ERROR: " + e);
//            logException(logTest, "getUserMeStatistic method - ERROR: ", e);
//        }
//
//        return userStatistic;
//    }



//    public void verifyGetIssueResponse(String issueId, Hashtable<String, String> data, Response response, ExtentTest logTest) throws IOException, ParseException {
//        logInfo(logTest, "verifyGetIssueResponse starts..........");
//        log4j.info("verifyGetIssueResponse starts..........");
//
//        if (response.getStatusCode() == 200) {
//            logPass(logTest, "API calling successful. Status code response is  200");
//
//            jsonActual = (JSONObject) jsonParser.parse(response.body().asString());
//
//            logInfo(logTest, "Verify the response is not empty");
//            verifyActualIsNotEmptyResults(logTest, jsonActual.get("key").toString());
//
//            String actualIssueId = jsonActual.get("key").toString();
//            String actualIssueType = ((JSONObject) ((JSONObject) jsonActual.get("fields")).get("issuetype")).get("name").toString();
//            String actualIssueSummary = ((JSONObject) jsonActual.get("fields")).get("summary").toString();
//
//            verifyExpectedAndActualResults(logTest, issueId, actualIssueId);
//            verifyExpectedAndActualResults(logTest, data.get("IssueType"), actualIssueType);
//            verifyExpectedAndActualResults(logTest, data.get("IssueSummary"), actualIssueSummary);
//        }
//        else
//            logFail(logTest, "API calling was unsuccessful: Status code response is " + response.getStatusCode() + " instead of 200");
//
//
//
//        logInfo(logTest, "verifyGetIssueResponse ends..........");
//        log4j.info("verifyGetIssueResponse ends..........");
//    }

}