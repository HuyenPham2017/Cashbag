package resource.utility.webdrivers;

import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import resource.common.GlobalVariables;
import resource.utility.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class MobileDriver {
    public RemoteWebDriver webDriver;
    private static Log log = LogFactory.getLog("Mobile logs");

    public synchronized RemoteWebDriver initialDriver(String platformName, String deviceID, String appPackage, String appActivity, ExtentTest logTest) throws IOException {
        try {

            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", platformName);
            caps.setCapability("deviceName", deviceID);
            caps.setCapability("platformName", platformName);
            caps.setCapability("app", "F:\\AUTOMATION\\12.Appium\\vn.selly.staging_1.12.0.apk");

            if (GlobalVariables.USE_PERFECTO_CONNECT) caps.setCapability("tunnelId", GlobalVariables.TUNNEL_ID);

            ChromeOptions chromeOptions = new ChromeOptions();

            switch (platformName.toLowerCase()) {

                case "ios":
                case "android":
                    String deviceId = getAvailableDevice(platformName);
                    if (deviceId == null || deviceId == "") {
                        log.error("No available devices found: " + caps);
                        throw new Exception("No available devices found: " + caps);
                    } else {
                        log.info("Device ID: " + deviceId);
                        webDriver = new RemoteWebDriver(new URL(GlobalVariables.PERFECTO_HUB_ADDRESS + "/fast"), caps);
                    }

                    break;
                default:
                    throw new Exception("PLATFORM is invalid: ");
            }

            log.info(caps.toJson());
            Utility.sleep(5);
        } catch (Exception e) {
            log.error("initialDriver method - Error: " + e);
            logTest.fail("initialDriver method - Error" + e);
        }

        log.info("Started PerfectoMobile driver");
        return webDriver;
    }

    public String getAvailableDevice(String platformName) {
        log.info("Get available device...start: " + DateTime.now());
        try {
            long start = System.currentTimeMillis();
            while ((((System.currentTimeMillis() - start) / 1000F) < 300)) {
                Map<String, String> pars = new HashMap<>();
                pars.put("operation", "list");
                pars.put("securityToken", GlobalVariables.PERFECTO_TOKEN);
                pars.put("availableTo", GlobalVariables.PERFECTO_USERNAME);
                pars.put("allocatedTo", "");
                pars.put("reservedTo", "");
                pars.put("status", "Connected");
                pars.put("inUse", "false");
                pars.put("os", platformName);

                RestAssured.baseURI = "https://" + GlobalVariables.PERFECTO_HOST;
                RequestSpecification httpRequest = RestAssured.given();
                httpRequest.queryParams(pars);

                Response response = httpRequest.get("/services/handsets");
                XmlPath xmlPath = response.xmlPath();
                int size = xmlPath.get("handsets.handset.size()");
                if (size > 0) {
                    log.info("Get available device...end: " + DateTime.now());
                    return xmlPath.get("handsets.handset[0].deviceId");
                }
            }
        } catch (Exception e) {
            log.error("getAvailableDevice method - ERROR - " + e);
        }

        log.info("Get available device timeout: " + DateTime.now());
        return "";
    }

    public String getAccessToken(String userName, String password) {
        try {
            String GET_ACCESS_TOKEN = "/auth/security-token?userAPI=" + userName + "&password=" + password;
            RequestSpecification requestSpec = given().
                    baseUri("https://" + GlobalVariables.PERFECTO_HOST).
                    basePath("/services/v2.0").
                    relaxedHTTPSValidation().
                    header("Accept", "application/json").
                    contentType("application/json");
            Response response = requestSpec.get(GET_ACCESS_TOKEN);
            HashMap data = response.then().extract().response().path("data");
            return data.get("securityToken").toString();
        } catch (Exception ex) {
            log.error("Error while getting Perfecto access token: " + ex.getMessage());
            return "";
        }
    }

    public String startPerfectoConnection(String securityToken) {
        try {
            String startCmd = "";
            if (GlobalVariables.OS_NAME.contains("mac")) {
                startCmd = GlobalVariables.PERFECTO_CONNECT_MAC + " start -c " + GlobalVariables.PERFECTO_HOST + " -s " + securityToken;
            } else if (GlobalVariables.OS_NAME.contains("windows")) {
                startCmd = GlobalVariables.PERFECTO_CONNECT_WIN + " start -c " + GlobalVariables.PERFECTO_HOST + " -s " + securityToken;
            }

            // Execute command
            GlobalVariables.p = Runtime.getRuntime().exec(startCmd);
            GlobalVariables.p.waitFor();
            Utility.sleep(5);

            // Get Tunnel ID from console output
            String tunnelID = null;
            int repeat = 1;
            while (tunnelID == null && repeat < 10) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(GlobalVariables.p.getInputStream()));
                while ((tunnelID = reader.readLine()) != null)
                    return tunnelID; // Return Tunnel ID if the command is executed successfully
                repeat += 1;
                Utility.sleep(3);
            }

            return "";
        } catch (Exception ex) {
            log.error("Error while getting Tunnel ID: " + ex.getMessage());
            return "";
        }
    }

    public void stopPerfectoConnection() {
        try {
            String stopCmd = "";
            if (GlobalVariables.OS_NAME.contains("mac")) {
                stopCmd = GlobalVariables.PERFECTO_CONNECT_MAC + " stop";
            } else if (GlobalVariables.OS_NAME.contains("windows")) {
                stopCmd = GlobalVariables.PERFECTO_CONNECT_WIN + " stop";
            }

            GlobalVariables.p = Runtime.getRuntime().exec(stopCmd);
            GlobalVariables.p.waitFor();
            GlobalVariables.p.destroy();
        } catch (Exception ex) {
            log.error("Error while stopping Perfecto Connection: " + ex.getMessage());
        }
    }
}